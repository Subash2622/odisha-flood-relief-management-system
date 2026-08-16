/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Box, Grid, Card, CardContent, Typography, Button, Table, TableBody, TableCell, TableHead, TableRow,
} from '@mui/material';
import { donationApi, notificationApi } from '../../api/services';
import { useAuth } from '../../context/AuthContext';
import PageHeader from '../../components/common/PageHeader';

export default function UserDashboard() {
  const { user } = useAuth();
  const [donations, setDonations] = useState([]);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    donationApi.my({ size: 10 }).then((d) => setDonations(d.content || [])).catch(() => {});
    notificationApi.getAll({ size: 5 }).then((d) => setNotifications(d.content || [])).catch(() => {});
  }, []);

  return (
    <Box>
      <PageHeader title="My Dashboard" subtitle={`Welcome, ${user?.fullName}`} />

      <Grid container spacing={3} mb={3}>
        {[
          { title: 'Donate', desc: 'Support flood relief', path: '/donate' },
          { title: 'Apply Membership', desc: 'Join our NGO', path: '/membership' },
          { title: 'Volunteer', desc: 'Help in relief work', path: '/volunteer' },
          { title: 'Report Flood', desc: 'Report emergencies', path: '/flood-report' },
        ].map((action) => (
          <Grid item xs={6} md={3} key={action.title}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>{action.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>{action.desc}</Typography>
                <Button component={RouterLink} to={action.path} variant="contained" size="small">Go</Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>My Donations</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Amount</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Date</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {donations.map((d) => (
                    <TableRow key={d.donationId}>
                      <TableCell>{d.donationId}</TableCell>
                      <TableCell>₹{d.amount?.toLocaleString()}</TableCell>
                      <TableCell>{d.status}</TableCell>
                      <TableCell>{d.createdAt?.substring(0, 10)}</TableCell>
                    </TableRow>
                  ))}
                  {donations.length === 0 && (
                    <TableRow><TableCell colSpan={4} align="center">No donations yet. <Button component={RouterLink} to="/donate" size="small">Donate Now</Button></TableCell></TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={5}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Notifications</Typography>
              {notifications.map((n) => (
                <Box key={n.id} sx={{ mb: 1, p: 1, borderRadius: 1, bgcolor: 'action.hover' }}>
                  <Typography variant="subtitle2">{n.title}</Typography>
                  <Typography variant="body2" color="text.secondary">{n.message}</Typography>
                </Box>
              ))}
              {notifications.length === 0 && <Typography color="text.secondary">No notifications</Typography>}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
