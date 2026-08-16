/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Box, Card, CardContent, Typography, Button, Alert, CircularProgress } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { volunteerApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function VolunteerPage() {
  const { user, hasRole } = useAuth();
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [checking, setChecking] = useState(!!user);

  useEffect(() => {
    if (!user) {
      setProfile(null);
      setChecking(false);
      return;
    }
    let cancelled = false;
    setChecking(true);
    volunteerApi.my()
      .then((data) => {
        if (!cancelled) setProfile(data);
      })
      .catch(() => {
        if (!cancelled) setProfile(null);
      })
      .finally(() => {
        if (!cancelled) setChecking(false);
      });
    return () => { cancelled = true; };
  }, [user]);

  const apply = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await volunteerApi.apply();
      setProfile(res);
    } catch (e) {
      setError(e.response?.data?.message || 'Application failed. Please login first.');
    } finally {
      setLoading(false);
    }
  };

  const status = (profile?.status || '').toUpperCase();
  const isApproved = status === 'APPROVED' || hasRole('VOLUNTEER');
  const isPending = status === 'PENDING';
  const isRejected = status === 'REJECTED';
  const alreadyVolunteer = !!profile || hasRole('VOLUNTEER');

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

          {checking ? (
            <Box display="flex" alignItems="center" gap={1}>
              <CircularProgress size={22} />
              <Typography variant="body2">Checking your volunteer status...</Typography>
            </Box>
          ) : isApproved ? (
            <Alert severity="success" sx={{ mb: 2 }}>
              <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                Thank you for being a volunteer!
              </Typography>
              <Typography variant="body2">
                We appreciate your service with Odisha Flood Relief.
                {profile?.volunteerId ? ` Your Volunteer ID: ${profile.volunteerId}.` : ''}
                {profile?.assignedDistrict
                  ? ` Assigned area: ${profile.assignedArea || ''} ${profile.assignedDistrict}.`
                  : ''}
              </Typography>
              <Button
                component={RouterLink}
                to="/dashboard/volunteer"
                variant="contained"
                size="small"
                sx={{ mt: 1.5 }}
              >
                Go to Volunteer Dashboard
              </Button>
            </Alert>
          ) : isPending ? (
            <Alert severity="info" sx={{ mb: 2 }}>
              <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                Thank you for applying as a volunteer!
              </Typography>
              <Typography variant="body2">
                Your application is pending admin approval.
                We will notify you once it is reviewed.
              </Typography>
            </Alert>
          ) : isRejected ? (
            <Alert severity="warning" sx={{ mb: 2 }}>
              Your previous volunteer application was rejected.
              Please contact admin/CEO if you wish to re-apply.
            </Alert>
          ) : (
            <Button
              variant="contained"
              size="large"
              onClick={apply}
              disabled={loading || !user || alreadyVolunteer}
            >
              {!user ? 'Login to Apply' : (loading ? 'Submitting...' : 'Apply as Volunteer')}
            </Button>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
