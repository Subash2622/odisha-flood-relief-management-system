import { Box, Typography, Breadcrumbs, Link } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

export default function PageHeader({ title, subtitle, breadcrumbs = [] }) {
  return (
    <Box mb={3}>
      {breadcrumbs.length > 0 && (
        <Breadcrumbs sx={{ mb: 1 }}>
          {breadcrumbs.map((b, i) =>
            b.to ? (
              <Link key={i} component={RouterLink} to={b.to} underline="hover" color="inherit">
                {b.label}
              </Link>
            ) : (
              <Typography key={i} color="text.primary">{b.label}</Typography>
            )
          )}
        </Breadcrumbs>
      )}
      <Typography variant="h4" gutterBottom>{title}</Typography>
      {subtitle && <Typography color="text.secondary">{subtitle}</Typography>}
    </Box>
  );
}
