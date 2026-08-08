import { useEffect, useState } from 'react';
import { Box, Typography, Grid, Card, CardContent } from '@mui/material';
import { publicApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function AboutPage() {
  const [org, setOrg] = useState(null);

  useEffect(() => {
    publicApi.organization().then(setOrg).catch(() => {});
  }, []);

  return (
    <Box>
      <PageHeader title="About Our NGO" subtitle="Serving Odisha during flood emergencies" />
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h5" gutterBottom>{org?.orgName || 'Odisha Flood Relief Foundation'}</Typography>
              <Typography paragraph color="text.secondary">
                {org?.description || 'We are a dedicated NGO working across Odisha to provide timely flood relief, coordinate volunteers, and ensure transparent use of donations during natural disasters.'}
              </Typography>
              <Typography paragraph>
                Our platform enables citizens to donate, apply for membership, volunteer for relief operations,
                and report flood emergencies. Every transaction is logged for complete transparency.
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Contact Information</Typography>
              <Typography variant="body2" gutterBottom><strong>Email:</strong> {org?.email || 'contact@odishafloodrelief.org'}</Typography>
              <Typography variant="body2" gutterBottom><strong>Phone:</strong> {org?.phone || '+91-9876543210'}</Typography>
              <Typography variant="body2"><strong>Address:</strong> {org?.address || 'Bhubaneswar, Odisha'}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
