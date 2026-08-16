/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Box, Grid, Card, CardContent, Typography, Button, TextField, Alert,
  Chip, FormControlLabel, Checkbox, MenuItem, FormControl, InputLabel, Select,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { donationApi, campaignApi, publicApi } from '../api/services';
import { uploadUrl } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

export default function DonatePage() {
  const { user } = useAuth();
  const location = useLocation();
  const [amounts, setAmounts] = useState([100, 500, 1000, 5000]);
  const [selectedAmount, setSelectedAmount] = useState(500);
  const [customAmount, setCustomAmount] = useState('500');
  const [campaigns, setCampaigns] = useState([]);
  const [org, setOrg] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const { register, handleSubmit, control } = useForm({
    defaultValues: {
      campaignId: location.state?.campaignId ? String(location.state.campaignId) : '',
      isAnonymous: false,
      donorName: '',
      donorEmail: '',
      donorPhone: '',
      message: '',
    },
  });

  useEffect(() => {
    donationApi.amounts()
      .then((data) => {
        const list = (Array.isArray(data) ? data : [])
          .map((a) => Number(a))
          .filter((a) => !Number.isNaN(a) && a > 0);
        if (list.length) {
          setAmounts(list);
          setSelectedAmount((prev) => {
            const next = list.includes(prev) ? prev : list[0];
            setCustomAmount(String(next));
            return next;
          });
        }
      })
      .catch(() => {});
    campaignApi.getActive({ size: 20 }).then((d) => setCampaigns(d.content || [])).catch(() => {});
    publicApi.organization().then(setOrg).catch(() => {});
  }, []);

  const pickAmount = (amount) => {
    const value = Number(amount);
    setSelectedAmount(value);
    setCustomAmount(String(value));
  };

  const onCustomAmountChange = (e) => {
    const value = e.target.value;
    setCustomAmount(value);
    const parsed = parseFloat(value);
    if (!Number.isNaN(parsed) && amounts.map(Number).includes(parsed)) {
      setSelectedAmount(parsed);
    } else {
      setSelectedAmount(null);
    }
  };

  const resolvedAmount = () => {
    if (selectedAmount != null && selectedAmount !== '') {
      return Number(selectedAmount);
    }
    if (customAmount !== '' && customAmount != null) {
      return parseFloat(customAmount);
    }
    return 0;
  };

  const onSubmit = async (data) => {
    setError('');
    const amount = resolvedAmount();
    if (!amount || Number.isNaN(amount) || amount < 1) {
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
        isAnonymous: !!data.isAnonymous,
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

  const displayAmount = resolvedAmount();

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
                {amounts.map((a) => {
                  const value = Number(a);
                  const active = selectedAmount != null && Number(selectedAmount) === value;
                  return (
                    <Chip
                      key={value}
                      label={`₹${value}`}
                      clickable
                      color={active ? 'primary' : 'default'}
                      variant={active ? 'filled' : 'outlined'}
                      onClick={() => pickAmount(value)}
                      sx={{ fontWeight: active ? 700 : 500, px: 1 }}
                    />
                  );
                })}
              </Box>
              <TextField
                fullWidth
                label="Custom Amount (₹)"
                type="number"
                value={customAmount}
                onChange={onCustomAmountChange}
                InputLabelProps={{ shrink: true }}
                inputProps={{ min: 1, step: '1' }}
                helperText={selectedAmount ? `Preset selected — amount filled below` : 'Type a custom amount if needed'}
                sx={{ mb: 2 }}
              />
              <form onSubmit={handleSubmit(onSubmit)}>
                {!user && (
                  <>
                    <TextField fullWidth label="Your Name" margin="dense" InputLabelProps={{ shrink: true }} {...register('donorName', { required: true })} />
                    <TextField fullWidth label="Email" type="email" margin="dense" InputLabelProps={{ shrink: true }} {...register('donorEmail', { required: true })} />
                    <TextField fullWidth label="Phone" margin="dense" InputLabelProps={{ shrink: true }} {...register('donorPhone')} />
                  </>
                )}

                <FormControl fullWidth margin="dense">
                  <InputLabel id="campaign-label" shrink>Campaign (Optional)</InputLabel>
                  <Controller
                    name="campaignId"
                    control={control}
                    render={({ field }) => (
                      <Select
                        {...field}
                        labelId="campaign-label"
                        label="Campaign (Optional)"
                        displayEmpty
                        notched
                        value={field.value ?? ''}
                      >
                        <MenuItem value="">
                          <em>General Fund</em>
                        </MenuItem>
                        {campaigns.map((c) => (
                          <MenuItem key={c.id} value={String(c.id)}>{c.title}</MenuItem>
                        ))}
                      </Select>
                    )}
                  />
                </FormControl>

                <TextField fullWidth label="Message" margin="dense" multiline rows={2} InputLabelProps={{ shrink: true }} {...register('message')} />
                <FormControlLabel control={<Checkbox {...register('isAnonymous')} />} label="Donate anonymously" />
                <Button type="submit" variant="contained" size="large" fullWidth sx={{ mt: 2 }}>
                  Donate ₹{(displayAmount || 0).toLocaleString()}
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
