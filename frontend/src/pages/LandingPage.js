import { useEffect, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Box, Typography, Button, Grid, Card, CardContent, CardMedia, Chip, Alert,
} from '@mui/material';
import VolunteerActivismIcon from '@mui/icons-material/VolunteerActivism';
import CampaignIcon from '@mui/icons-material/Campaign';
import ReportIcon from '@mui/icons-material/Report';
import { publicApi, campaignApi } from '../api/services';
import { uploadUrl } from '../api/axios';
import PageHeader from '../components/common/PageHeader';

export default function LandingPage() {
  const [org, setOrg] = useState(null);
  const [announcements, setAnnouncements] = useState([]);
  const [campaigns, setCampaigns] = useState([]);

  useEffect(() => {
    publicApi.organization().then(setOrg).catch(() => {});
    publicApi.announcements().then(setAnnouncements).catch(() => {});
    campaignApi.getActive({ size: 3 }).then((data) => setCampaigns(data.content || [])).catch(() => {});
  }, []);

  return (
    <Box>
      <Box
        sx={{
          background: (t) => `linear-gradient(135deg, ${t.palette.primary.main} 0%, ${t.palette.primary.dark || '#0d47a1'} 100%)`,
          color: 'white',
          borderRadius: 3,
          p: { xs: 4, md: 6 },
          mb: 4,
        }}
      >
        <Typography variant="h3" fontWeight={800} gutterBottom>
          {org?.orgName || 'Odisha Flood Relief Foundation'}
        </Typography>
        <Typography variant="h6" sx={{ opacity: 0.9, mb: 3, maxWidth: 600 }}>
          Join us in providing transparent, accountable flood relief across Odisha. Donate, volunteer, or report emergencies.
        </Typography>
        <Box display="flex" gap={2} flexWrap="wrap">
          <Button variant="contained" color="secondary" size="large" component={RouterLink} to="/donate">
            Donate Now
          </Button>
          <Button variant="outlined" size="large" component={RouterLink} to="/volunteer" sx={{ color: 'white', borderColor: 'white' }}>
            Become a Volunteer
          </Button>
          <Button variant="outlined" size="large" component={RouterLink} to="/flood-report" sx={{ color: 'white', borderColor: 'white' }}>
            Report Flood
          </Button>
        </Box>
      </Box>

      {announcements.length > 0 && (
        <Box mb={4}>
          {announcements.slice(0, 2).map((a) => (
            <Alert key={a.id} severity="info" sx={{ mb: 1 }}>
              <strong>{a.title}</strong> — {a.content}
            </Alert>
          ))}
        </Box>
      )}

      <Grid container spacing={3} mb={4}>
        {[
          { icon: <VolunteerActivismIcon fontSize="large" />, title: 'Transparent Donations', desc: 'Track every rupee with receipts, QR codes, and audit logs.' },
          { icon: <CampaignIcon fontSize="large" />, title: 'Relief Campaigns', desc: 'Support targeted flood relief campaigns across districts.' },
          { icon: <ReportIcon fontSize="large" />, title: 'Citizen Reports', desc: 'Report flood emergencies with GPS location and photos.' },
        ].map((item) => (
          <Grid item xs={12} md={4} key={item.title}>
            <Card sx={{ height: '100%', textAlign: 'center', p: 2 }}>
              <Box color="primary.main" mb={1}>{item.icon}</Box>
              <Typography variant="h6" gutterBottom>{item.title}</Typography>
              <Typography color="text.secondary">{item.desc}</Typography>
            </Card>
          </Grid>
        ))}
      </Grid>

      <PageHeader title="Active Campaigns" subtitle="Support ongoing relief efforts" />
      <Grid container spacing={3}>
        {campaigns.map((c) => (
          <Grid item xs={12} md={4} key={c.id}>
            <Card>
              {c.bannerImage && (
                <CardMedia component="img" height="160" image={uploadUrl(c.bannerImage)} alt={c.title} />
              )}
              <CardContent>
                <Typography variant="h6" gutterBottom>{c.title}</Typography>
                <Typography variant="body2" color="text.secondary" noWrap>{c.description}</Typography>
                <Box mt={2} display="flex" justifyContent="space-between" alignItems="center">
                  <Chip label={`${c.progressPercent?.toFixed(0) || 0}% funded`} color="primary" size="small" />
                  <Button size="small" component={RouterLink} to="/donate" state={{ campaignId: c.id }}>
                    Donate
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
        {campaigns.length === 0 && (
          <Grid item xs={12}>
            <Typography color="text.secondary">No active campaigns at the moment.</Typography>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
