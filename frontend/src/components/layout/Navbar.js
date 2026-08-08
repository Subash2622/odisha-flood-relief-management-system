import { useContext, useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  AppBar, Toolbar, Typography, Button, Box, IconButton, Menu, MenuItem,
  Avatar, Drawer, List, ListItem, ListItemText, useMediaQuery, useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import WaterDropIcon from '@mui/icons-material/WaterDrop';
import { useAuth } from '../../context/AuthContext';
import { ThemeModeContext } from '../../context/ThemeContext';

const navLinks = [
  { label: 'Home', path: '/' },
  { label: 'About', path: '/about' },
  { label: 'Campaigns', path: '/campaigns' },
  { label: 'Donate', path: '/donate' },
  { label: 'Volunteer', path: '/volunteer' },
  { label: 'Membership', path: '/membership' },
  { label: 'Report Flood', path: '/flood-report' },
  { label: 'Contact', path: '/contact' },
];

export default function Navbar() {
  const { user, logout, getDashboardPath, hasRole } = useAuth();
  const { mode, toggleTheme } = useContext(ThemeModeContext);
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const closeMenu = () => setAnchorEl(null);

  return (
    <>
      <AppBar position="sticky" elevation={1}>
        <Toolbar>
          {isMobile && (
            <IconButton color="inherit" edge="start" onClick={() => setDrawerOpen(true)} sx={{ mr: 1 }}>
              <MenuIcon />
            </IconButton>
          )}
          <WaterDropIcon sx={{ mr: 1 }} />
          <Typography
            variant="h6"
            component={RouterLink}
            to="/"
            sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit', fontWeight: 700 }}
          >
            Odisha Flood Relief
          </Typography>

          {!isMobile && (
            <Box sx={{ display: 'flex', gap: 0.5, mr: 2 }}>
              {navLinks.map((link) => (
                <Button key={link.path} color="inherit" component={RouterLink} to={link.path} size="small">
                  {link.label}
                </Button>
              ))}
              {hasRole('CEO') && (
                <Button color="inherit" component={RouterLink} to="/dashboard/admin" size="small" sx={{ fontWeight: 700 }}>
                  Approvals
                </Button>
              )}
            </Box>
          )}

          <IconButton color="inherit" onClick={toggleTheme}>
            {mode === 'dark' ? <Brightness7Icon /> : <Brightness4Icon />}
          </IconButton>

          {user ? (
            <>
              <IconButton color="inherit" onClick={(e) => setAnchorEl(e.currentTarget)}>
                <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main', fontSize: 14 }}>
                  {user.fullName?.charAt(0) || user.username?.charAt(0)}
                </Avatar>
              </IconButton>
              <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
                <MenuItem disabled>{user.fullName || user.username}</MenuItem>
                <MenuItem onClick={() => { closeMenu(); navigate(getDashboardPath()); }}>
                  Dashboard
                </MenuItem>
                {hasRole('CEO') && (
                  <MenuItem onClick={() => { closeMenu(); navigate('/dashboard/admin'); }}>
                    Approvals (Members / Volunteers)
                  </MenuItem>
                )}
                <MenuItem onClick={() => { closeMenu(); navigate('/profile'); }}>Profile</MenuItem>
                <MenuItem onClick={handleLogout}>Logout</MenuItem>
              </Menu>
            </>
          ) : (
            <Box>
              <Button color="inherit" component={RouterLink} to="/login">Login</Button>
              <Button variant="outlined" color="inherit" component={RouterLink} to="/register" sx={{ ml: 1 }}>
                Register
              </Button>
            </Box>
          )}
        </Toolbar>
      </AppBar>

      <Drawer open={drawerOpen} onClose={() => setDrawerOpen(false)}>
        <List sx={{ width: 250, pt: 2 }}>
          {navLinks.map((link) => (
            <ListItem button key={link.path} component={RouterLink} to={link.path} onClick={() => setDrawerOpen(false)}>
              <ListItemText primary={link.label} />
            </ListItem>
          ))}
          {hasRole('CEO') && (
            <ListItem button component={RouterLink} to="/dashboard/admin" onClick={() => setDrawerOpen(false)}>
              <ListItemText primary="Approvals (Members / Volunteers)" />
            </ListItem>
          )}
          {user && (
            <ListItem button component={RouterLink} to={getDashboardPath()} onClick={() => setDrawerOpen(false)}>
              <ListItemText primary="Dashboard" />
            </ListItem>
          )}
        </List>
      </Drawer>
    </>
  );
}
