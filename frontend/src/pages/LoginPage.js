import { useState } from 'react';
import { useNavigate, useLocation, Link as RouterLink } from 'react-router-dom';
import { Box, Card, CardContent, TextField, Button, Typography, Alert, Link } from '@mui/material';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { register, handleSubmit } = useForm();
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    setError('');
    try {
      const result = await login(data);
      const roles = result.roles || [];
      const has = (r) => roles.includes(`ROLE_${r}`) || roles.includes(r);
      const rolePath = has('CEO') ? '/dashboard/ceo'
        : has('ADMIN') ? '/dashboard/admin'
        : has('VOLUNTEER') ? '/dashboard/volunteer'
        : has('MEMBER') ? '/dashboard/member'
        : '/dashboard/user';
      const from = location.state?.from?.pathname || rolePath;
      navigate(from, { replace: true });
    } catch (e) {
      setError(e.response?.data?.message || 'Invalid credentials');
    }
  };

  return (
    <Box maxWidth={420} mx="auto">
      <PageHeader title="Login" subtitle="Access your dashboard" />
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Card>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField fullWidth label="Username or Email" margin="normal" {...register('usernameOrEmail', { required: true })} />
            <TextField fullWidth label="Password" type="password" margin="normal" {...register('password', { required: true })} />
            <Button type="submit" variant="contained" fullWidth size="large" sx={{ mt: 2 }}>Login</Button>
          </form>
          <Typography variant="body2" align="center" sx={{ mt: 2 }}>
            Don't have an account? <Link component={RouterLink} to="/register">Register</Link>
          </Typography>
          <Typography variant="caption" color="text.secondary" display="block" align="center" sx={{ mt: 1 }}>
            CEO demo: ceo / ceo123
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}
