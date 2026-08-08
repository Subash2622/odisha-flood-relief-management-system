import { createTheme } from '@mui/material/styles';

export const getTheme = (mode) =>
  createTheme({
    palette: {
      mode,
      primary: { main: mode === 'light' ? '#1565c0' : '#90caf9' },
      secondary: { main: mode === 'light' ? '#ff6f00' : '#ffb74d' },
      background: {
        default: mode === 'light' ? '#f5f7fa' : '#0a1929',
        paper: mode === 'light' ? '#ffffff' : '#132f4c',
      },
    },
    typography: {
      fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
      h4: { fontWeight: 700 },
      h5: { fontWeight: 600 },
    },
    shape: { borderRadius: 10 },
    components: {
      MuiButton: { styleOverrides: { root: { textTransform: 'none', fontWeight: 600 } } },
      MuiCard: { styleOverrides: { root: { boxShadow: mode === 'light' ? '0 2px 12px rgba(0,0,0,0.08)' : 'none' } } },
    },
  });
