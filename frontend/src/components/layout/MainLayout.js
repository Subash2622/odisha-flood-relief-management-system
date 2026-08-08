import { Box, Container } from '@mui/material';
import Navbar from './Navbar';
import Footer from './Footer';

export default function MainLayout({ children, hideFooter }) {
  return (
    <Box display="flex" flexDirection="column" minHeight="100vh">
      <Navbar />
      <Box component="main" flexGrow={1} py={3}>
        <Container maxWidth="lg">{children}</Container>
      </Box>
      {!hideFooter && <Footer />}
    </Box>
  );
}
