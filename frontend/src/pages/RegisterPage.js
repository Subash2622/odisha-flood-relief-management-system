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
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

export default function RegisterPage() {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const { register, handleSubmit } = useForm();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const onSubmit = async (data) => {
    setError('');
    try {
      await registerUser(data);
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (e) {
      setError(e.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <Box maxWidth={500} mx="auto">
      <PageHeader title="Register" subtitle="Create your account" />
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>Registration successful! Redirecting to login...</Alert>}
      <Card>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Username" {...register('username', { required: true })} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Full Name" {...register('fullName', { required: true })} />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Email" type="email" {...register('email', { required: true })} />
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Password" type="password" {...register('password', { required: true, minLength: 6 })} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Phone" {...register('phone')} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth label="Address" {...register('address')} />
              </Grid>
              <Grid item xs={12}>
                <Button type="submit" variant="contained" fullWidth size="large">Register</Button>
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
