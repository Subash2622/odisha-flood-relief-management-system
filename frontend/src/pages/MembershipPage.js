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
import { membershipApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function MembershipPage() {
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
    membershipApi.my()
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
      const res = await membershipApi.apply();
      setProfile(res);
    } catch (e) {
      setError(e.response?.data?.message || 'Application failed. Please login first.');
    } finally {
      setLoading(false);
    }
  };

  const status = (profile?.status || '').toUpperCase();
  const isApproved = status === 'APPROVED' || hasRole('MEMBER');
  const isPending = status === 'PENDING';
  const isRejected = status === 'REJECTED';
  const alreadyMember = !!profile || hasRole('MEMBER');

  return (
    <Box>
      <PageHeader
        title="NGO Membership"
        subtitle="Become a member and receive a digital membership card with QR code"
      />
      <Card>
        <CardContent>
          <Typography paragraph>
            Members get access to exclusive updates, donation history, digital membership cards,
            and the ability to renew membership annually.
          </Typography>
          <Typography variant="body2" color="text.secondary" paragraph>
            Benefits: Digital membership card, QR verification, donation tracking, priority volunteer applications.
          </Typography>

          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

          {checking ? (
            <Box display="flex" alignItems="center" gap={1}>
              <CircularProgress size={22} />
              <Typography variant="body2">Checking your membership status...</Typography>
            </Box>
          ) : isApproved ? (
            <Alert severity="success" sx={{ mb: 2 }}>
              <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                Thank you for being a valued member!
              </Typography>
              <Typography variant="body2">
                We are grateful for your support of Odisha Flood Relief.
                {profile?.membershipId ? ` Your Membership ID: ${profile.membershipId}.` : ''}
                {profile?.validUntil ? ` Valid until: ${profile.validUntil}.` : ''}
              </Typography>
              <Button
                component={RouterLink}
                to="/dashboard/member"
                variant="contained"
                size="small"
                sx={{ mt: 1.5 }}
              >
                Go to Member Dashboard
              </Button>
            </Alert>
          ) : isPending ? (
            <Alert severity="info" sx={{ mb: 2 }}>
              <Typography variant="subtitle1" fontWeight={700} gutterBottom>
                Thank you for applying for membership!
              </Typography>
              <Typography variant="body2">
                Your application is pending approval.
                You will get your digital membership card after admin/CEO approval.
              </Typography>
            </Alert>
          ) : isRejected ? (
            <Alert severity="warning" sx={{ mb: 2 }}>
              Your previous membership application was rejected.
              Please contact admin/CEO if you wish to re-apply.
            </Alert>
          ) : (
            <Button
              variant="contained"
              size="large"
              onClick={apply}
              disabled={loading || !user || alreadyMember}
            >
              {!user ? 'Login to Apply' : (loading ? 'Submitting...' : 'Apply for Membership')}
            </Button>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
