import { Box, Container, Typography, Grid, Link } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export default function Footer() {
  return (
    <Box component="footer" sx={{ bgcolor: 'background.paper', borderTop: 1, borderColor: 'divider', mt: 6, py: 4 }}>
      <Container maxWidth="lg">
        <Grid container spacing={3}>
          <Grid item xs={12} md={4}>
            <Typography variant="h6" gutterBottom fontWeight={700}>Odisha Flood Relief Foundation</Typography>
            <Typography variant="body2" color="text.secondary">
              Transparent flood relief management, donations, and volunteer coordination across Odisha.
            </Typography>
          </Grid>
          <Grid item xs={6} md={4}>
            <Typography variant="subtitle2" gutterBottom>Quick Links</Typography>
            {['Campaigns', 'Donate', 'Volunteer', 'Report Flood'].map((label) => (
              <Box key={label}>
                <Link component={RouterLink} to={`/${label.toLowerCase().replace(' ', '-')}`} color="text.secondary" underline="hover">
                  {label}
                </Link>
              </Box>
            ))}
          </Grid>
          <Grid item xs={6} md={4}>
            <Typography variant="subtitle2" gutterBottom>Contact</Typography>
            <Typography variant="body2" color="text.secondary">contact@odishafloodrelief.org</Typography>
            <Typography variant="body2" color="text.secondary">+91-9876543210</Typography>
          </Grid>
        </Grid>
        <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 3 }}>
          © {new Date().getFullYear()} Odisha Flood Relief & NGO Management System
        </Typography>
      </Container>
    </Box>
  );
}
