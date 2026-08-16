/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import { Box, Card, CardContent, TextField, Button, Alert, Avatar } from '@mui/material';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function ProfilePage() {
  const { user, refreshUser } = useAuth();
  const { register, handleSubmit, defaultValues } = useForm({
    defaultValues: { fullName: user?.fullName, phone: user?.phone, address: user?.address },
  });
  const [photo, setPhoto] = useState(null);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    setError('');
    try {
      const formData = new FormData();
      if (data.fullName) formData.append('fullName', data.fullName);
      if (data.phone) formData.append('phone', data.phone);
      if (data.address) formData.append('address', data.address);
      if (photo) formData.append('profileImage', photo);
      await userApi.updateProfile(formData);
      await refreshUser();
      setSuccess(true);
    } catch (e) {
      setError(e.response?.data?.message || 'Update failed');
    }
  };

  return (
    <Box maxWidth={600}>
      <PageHeader title="My Profile" subtitle={user?.email} />
      {success && <Alert severity="success" sx={{ mb: 2 }}>Profile updated!</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Card>
        <CardContent>
          <Box display="flex" alignItems="center" gap={2} mb={3}>
            <Avatar sx={{ width: 64, height: 64, bgcolor: 'primary.main', fontSize: 28 }}>
              {user?.fullName?.charAt(0)}
            </Avatar>
            <Box>
              <strong>{user?.fullName}</strong>
              <div>Roles: {user?.roles?.join(', ')}</div>
            </Box>
          </Box>
          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField fullWidth label="Full Name" margin="normal" defaultValue={defaultValues.fullName} {...register('fullName')} />
            <TextField fullWidth label="Phone" margin="normal" defaultValue={defaultValues.phone} {...register('phone')} />
            <TextField fullWidth label="Address" margin="normal" multiline defaultValue={defaultValues.address} {...register('address')} />
            <Button component="label" variant="outlined" sx={{ mt: 1 }}>
              Change Photo
              <input hidden type="file" accept="image/*" onChange={(e) => setPhoto(e.target.files[0])} />
            </Button>
            <Button type="submit" variant="contained" fullWidth sx={{ mt: 2 }}>Save Changes</Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
