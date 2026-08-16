/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import {
  Box, Grid, Card, CardContent, Typography, Button, Alert, Table, TableBody, TableCell, TableHead, TableRow, Chip,
} from '@mui/material';
import { membershipApi, donationApi, volunteerApi } from '../../api/services';
import { uploadUrl } from '../../api/axios';
import PageHeader from '../../components/common/PageHeader';

export default function MemberDashboard() {
  const [membership, setMembership] = useState(null);
  const [donations, setDonations] = useState([]);
  const [msg, setMsg] = useState('');

  useEffect(() => {
    membershipApi.my().then(setMembership).catch(() => {});
    donationApi.my({ size: 10 }).then((d) => setDonations(d.content || [])).catch(() => {});
  }, []);

  const renew = async () => {
    await membershipApi.renew();
    setMsg('Renewal request submitted');
  };

  const applyVolunteer = async () => {
    await volunteerApi.apply();
    setMsg('Volunteer application submitted');
  };

  if (!membership) return <Typography>Loading member dashboard...</Typography>;

  return (
    <Box>
      <PageHeader title="Member Dashboard" subtitle={`Membership ID: ${membership.membershipId || 'Pending'}`} />
      {msg && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setMsg('')}>{msg}</Alert>}

      <Grid container spacing={3}>
        <Grid item xs={12} md={5}>
          <Card sx={{ bgcolor: 'primary.main', color: 'white' }}>
            <CardContent>
              <Typography variant="overline">Digital Membership Card</Typography>
              <Typography variant="h5" fontWeight={700}>{membership.fullName}</Typography>
              <Typography variant="body1" sx={{ my: 1 }}>ID: {membership.membershipId}</Typography>
              <Chip label={membership.status} sx={{ bgcolor: 'rgba(255,255,255,0.2)', color: 'white', mb: 2 }} />
              <Typography variant="body2">Valid: {membership.validFrom} — {membership.validUntil}</Typography>
              {membership.qrCodePath && (
                <Box mt={2} textAlign="center" bgcolor="white" p={1} borderRadius={2} display="inline-block">
                  <img src={uploadUrl(membership.qrCodePath)} alt="Membership QR" width={120} />
                </Box>
              )}
              <Box mt={2} display="flex" gap={1} flexWrap="wrap">
                <Button variant="contained" color="secondary" size="small" onClick={renew}>Renew</Button>
                <Button
                  variant="contained"
                  size="small"
                  onClick={() => membershipApi.downloadCard().catch(() => setMsg('Card download failed — ensure membership is approved'))}
                >
                  Download Card PDF
                </Button>
                <Button variant="outlined" size="small" sx={{ color: 'white', borderColor: 'white' }} onClick={applyVolunteer}>
                  Become Volunteer
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Donation History</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Donation ID</TableCell>
                    <TableCell>Amount</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Receipt</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {donations.map((d) => (
                    <TableRow key={d.donationId}>
                      <TableCell>{d.donationId}</TableCell>
                      <TableCell>₹{d.amount?.toLocaleString()}</TableCell>
                      <TableCell>{d.status}</TableCell>
                      <TableCell>{d.createdAt?.substring(0, 10)}</TableCell>
                      <TableCell>
                        <Button size="small" onClick={() => donationApi.downloadReceipt(d.donationId)}>PDF</Button>
                      </TableCell>
                    </TableRow>
                  ))}
                  {donations.length === 0 && (
                    <TableRow><TableCell colSpan={5} align="center">No donations yet</TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
