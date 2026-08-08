import { useState } from 'react';
import { Box, Card, CardContent, Typography, Button, Alert } from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { membershipApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function MembershipPage() {
  const { user } = useAuth();
  const [status, setStatus] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const apply = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await membershipApi.apply();
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
          {status ? (
            <Alert severity="success">
              Application submitted! Status: {status.status}. Membership ID: {status.membershipId || 'Pending approval'}
            </Alert>
          ) : (
            <Button variant="contained" size="large" onClick={apply} disabled={loading || !user}>
              {user ? (loading ? 'Submitting...' : 'Apply for Membership') : 'Login to Apply'}
            </Button>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
