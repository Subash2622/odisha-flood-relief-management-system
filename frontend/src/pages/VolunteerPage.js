/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import { Box, Card, CardContent, Typography, Button, Alert } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { volunteerApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function VolunteerPage() {
  const { user } = useAuth();
  const [status, setStatus] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const apply = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await volunteerApi.apply();
      setStatus(res);
    } catch (e) {
      setError(e.response?.data?.message || 'Application failed. Please login first.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <PageHeader
        title="Volunteer With Us"
        subtitle="Join relief operations across flood-affected districts in Odisha"
      />
      <Card>
        <CardContent>
          <Typography paragraph>
            As a volunteer, you will be assigned to relief areas, help with distribution of food kits,
            medicine, blankets, and other essentials, and update work status with before/after photos.
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            Requirements: Commitment to field work, ability to travel to assigned districts, and admin approval.
          </Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          {status ? (
            <Alert severity="success">
              Application submitted! Status: {status.status}. Volunteer ID: {status.volunteerId || 'Pending approval'}
            </Alert>
          ) : (
            <Button variant="contained" size="large" onClick={apply} disabled={loading || !user}>
              {user ? (loading ? 'Submitting...' : 'Apply as Volunteer') : 'Login to Apply'}
            </Button>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
