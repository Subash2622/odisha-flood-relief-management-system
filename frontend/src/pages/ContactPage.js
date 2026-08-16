/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import { Box, Card, CardContent, TextField, Button, Alert } from '@mui/material';
import { useForm } from 'react-hook-form';
import { publicApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function ContactPage() {
  const { register, handleSubmit, reset } = useForm();
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    setError('');
    try {
      await publicApi.contact(data);
      setSuccess(true);
      reset();
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to send message');
    }
  };

  return (
    <Box>
      <PageHeader title="Contact Us" subtitle="Reach out for partnerships, queries, or support" />
      {success && <Alert severity="success" sx={{ mb: 2 }}>Message sent successfully!</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Card sx={{ maxWidth: 600 }}>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField fullWidth label="Name" margin="normal" {...register('name', { required: true })} />
            <TextField fullWidth label="Email" type="email" margin="normal" {...register('email', { required: true })} />
            <TextField fullWidth label="Subject" margin="normal" {...register('subject')} />
            <TextField fullWidth label="Message" multiline rows={4} margin="normal" {...register('message', { required: true })} />
            <Button type="submit" variant="contained" sx={{ mt: 2 }}>Send Message</Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
