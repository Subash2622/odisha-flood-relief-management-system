/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Box, Grid, Card, CardContent, Typography, Button, TextField, Alert, Chip, FormControlLabel, Checkbox,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { donationApi, campaignApi, publicApi } from '../api/services';
import { uploadUrl } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

export default function DonatePage() {
  const { user } = useAuth();
  const location = useLocation();
  const [amounts, setAmounts] = useState([100, 500, 1000, 5000]);
  const [selectedAmount, setSelectedAmount] = useState(null);
  const [customAmount, setCustomAmount] = useState('');
  const [campaigns, setCampaigns] = useState([]);
  const [org, setOrg] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const { register, handleSubmit } = useForm({
    defaultValues: { campaignId: location.state?.campaignId || '', isAnonymous: false },
  });

  useEffect(() => {
    donationApi.amounts().then(setAmounts).catch(() => {});
    campaignApi.getActive({ size: 20 }).then((d) => setCampaigns(d.content || [])).catch(() => {});
    publicApi.organization().then(setOrg).catch(() => {});
  }, []);

  const onSubmit = async (data) => {
    setError('');
    const amount = selectedAmount || parseFloat(customAmount);
    if (!amount || amount < 1) {
      setError('Please select or enter a valid amount');
      return;
    }
    try {
      const payload = {
        amount,
        campaignId: data.campaignId ? parseInt(data.campaignId, 10) : null,
        donorName: data.donorName || user?.fullName,
        donorEmail: data.donorEmail || user?.email,
        donorPhone: data.donorPhone,
        isAnonymous: data.isAnonymous,
        message: data.message,
        paymentMethod: 'UPI',
      };
      const res = user ? await donationApi.donate(payload) : await donationApi.guestDonate(payload);
      setResult(res);
    } catch (e) {
      setError(e.response?.data?.message || 'Donation failed');
    }
  };

  if (result) {
    return (
      <Box>
        <PageHeader title="Thank You!" subtitle="Your donation has been recorded" />
        <Card>
          <CardContent>
            <Alert severity="success" sx={{ mb: 2 }}>Donation ID: {result.donationId}</Alert>
            <Typography><strong>Amount:</strong> ₹{result.amount?.toLocaleString()}</Typography>
            <Typography><strong>Status:</strong> {result.status}</Typography>
            <Box mt={2} display="flex" gap={2} flexWrap="wrap">
              <Button
                variant="contained"
                onClick={() => donationApi.downloadReceipt(result.donationId).catch(() => setError('Failed to download receipt'))}
              >
                Download Receipt (PDF)
              </Button>
              <Button
                variant="outlined"
                href={donationApi.receiptUrl(result.donationId)}
                target="_blank"
                rel="noreferrer"
              >
                Open Receipt
              </Button>
            </Box>
            <Box mt={2}>
              <Typography gutterBottom>Donation QR Code:</Typography>
              <img
                src={donationApi.qrUrl(result.donationId)}
                alt="Donation QR"
                width={150}
                onError={(e) => {
                  if (result.qrCodePath) e.currentTarget.src = uploadUrl(result.qrCodePath);
                }}
              />
            </Box>
          </CardContent>
        </Card>
      </Box>
    );
  }

  return (
    <Box>
      <PageHeader title="Donate" subtitle="Every contribution helps flood-affected families in Odisha" />
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Select Amount</Typography>
              <Box display="flex" gap={1} flexWrap="wrap" mb={2}>
                {amounts.map((a) => (
                  <Chip
                    key={a}
                    label={`₹${a}`}
                    clickable
                    color={selectedAmount === a ? 'primary' : 'default'}
                    onClick={() => { setSelectedAmount(a); setCustomAmount(''); }}
                  />
                ))}
              </Box>
              <TextField
                fullWidth label="Custom Amount (₹)" type="number"
                value={customAmount}
                onChange={(e) => { setCustomAmount(e.target.value); setSelectedAmount(null); }}
                sx={{ mb: 2 }}
              />
              <form onSubmit={handleSubmit(onSubmit)}>
                {!user && (
                  <>
                    <TextField fullWidth label="Your Name" margin="dense" {...register('donorName', { required: true })} />
                    <TextField fullWidth label="Email" type="email" margin="dense" {...register('donorEmail', { required: true })} />
                    <TextField fullWidth label="Phone" margin="dense" {...register('donorPhone')} />
                  </>
                )}
                <TextField fullWidth select label="Campaign (Optional)" margin="dense" SelectProps={{ native: true }} {...register('campaignId')}>
                  <option value="">General Fund</option>
                  {campaigns.map((c) => <option key={c.id} value={c.id}>{c.title}</option>)}
                </TextField>
                <TextField fullWidth label="Message" margin="dense" multiline rows={2} {...register('message')} />
                <FormControlLabel control={<Checkbox {...register('isAnonymous')} />} label="Donate anonymously" />
                <Button type="submit" variant="contained" size="large" fullWidth sx={{ mt: 2 }}>
                  Donate ₹{(selectedAmount || customAmount || 0).toLocaleString()}
                </Button>
              </form>
            </CardContent>
          </Card>
        </Grid>
        {org?.upiId && (
          <Grid item xs={12} md={5}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>UPI Payment</Typography>
                <Typography variant="body2" gutterBottom>UPI ID: <strong>{org.upiId}</strong></Typography>
                {org.qrPaymentPath && (
                  <Box textAlign="center" mt={2}>
                    <img src={uploadUrl(org.qrPaymentPath)} alt="UPI QR" width={200} />
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
