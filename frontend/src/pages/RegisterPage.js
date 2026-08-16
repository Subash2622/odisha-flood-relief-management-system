/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { Box, Card, CardContent, TextField, Button, Typography, Alert, Link, Grid } from '@mui/material';
import { useForm } from 'react-hook-form';
import { authApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function RegisterPage() {
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    mode: 'onSubmit',
    defaultValues: {
      username: '',
      fullName: '',
      email: '',
      password: '',
      phone: '',
      address: '',
    },
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const onSubmit = async (data) => {
    setError('');
    setLoading(true);
    try {
      await authApi.register({
        username: data.username.trim(),
        fullName: data.fullName.trim(),
        email: data.email.trim(),
        password: data.password,
        phone: data.phone?.trim() || null,
        address: data.address?.trim() || null,
      });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (e) {
      const apiMsg = e.response?.data?.message;
      const fieldErrors = e.response?.data?.data;
      if (fieldErrors && typeof fieldErrors === 'object') {
        setError(Object.values(fieldErrors).join(' | ') || apiMsg || 'Registration failed');
      } else {
        setError(apiMsg || e.message || 'Registration failed');
      }
    } finally {
      setLoading(false);
    }
  };

  const onInvalid = () => {
    setError('Please fill all required fields correctly (password min 6 characters).');
  };

  return (
    <Box maxWidth={500} mx="auto">
      <PageHeader title="Register" subtitle="Create your account" />
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Registration successful! Redirecting to login...
        </Alert>
      )}
      <Card>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit, onInvalid)} noValidate>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Username"
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.username}
                  helperText={errors.username ? 'Username is required (min 3 chars)' : ''}
                  {...register('username', { required: true, minLength: 3 })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Full Name"
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.fullName}
                  helperText={errors.fullName ? 'Full name is required' : ''}
                  {...register('fullName', { required: true })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.email}
                  helperText={errors.email ? 'Valid email is required' : ''}
                  {...register('email', {
                    required: true,
                    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  })}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Password"
                  type="password"
                  InputLabelProps={{ shrink: true }}
                  error={!!errors.password}
                  helperText={errors.password ? 'Password must be at least 6 characters' : ''}
                  {...register('password', { required: true, minLength: 6 })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Phone"
                  InputLabelProps={{ shrink: true }}
                  {...register('phone')}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Address"
                  InputLabelProps={{ shrink: true }}
                  {...register('address')}
                />
              </Grid>
              <Grid item xs={12}>
                <Button type="submit" variant="contained" fullWidth size="large" disabled={loading || success}>
                  {loading ? 'Creating account...' : 'Register'}
                </Button>
              </Grid>
            </Grid>
          </form>
          <Typography variant="body2" align="center" sx={{ mt: 2 }}>
            Already have an account? <Link component={RouterLink} to="/login">Login</Link>
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}
