/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useEffect, useState } from 'react';
import {
  Box, Card, CardContent, TextField, Button, Alert, Avatar, Typography,
  CircularProgress, Chip, Stack, Divider,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/services';
import { uploadUrl } from '../api/axios';
import PageHeader from '../components/common/PageHeader';

/** Convert ROLE_MEMBER → Member, ROLE_USER → User, etc. */
function formatRoles(roles = []) {
  const labels = {
    ROLE_CEO: 'CEO',
    ROLE_ADMIN: 'Admin',
    ROLE_MEMBER: 'Member',
    ROLE_VOLUNTEER: 'Volunteer',
    ROLE_USER: 'User',
    CEO: 'CEO',
    ADMIN: 'Admin',
    MEMBER: 'Member',
    VOLUNTEER: 'Volunteer',
    USER: 'User',
  };
  return roles.map((role) => {
    if (labels[role]) return labels[role];
    return role.replace(/^ROLE_/, '').replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, (c) => c.toUpperCase());
  });
}

function roleChipColor(label) {
  switch (label) {
    case 'CEO': return 'secondary';
    case 'Admin': return 'error';
    case 'Member': return 'primary';
    case 'Volunteer': return 'success';
    default: return 'default';
  }
}

export default function ProfilePage() {
  const { user, refreshUser, loading: authLoading } = useAuth();
  const { register, handleSubmit, reset } = useForm({
    defaultValues: { fullName: '', phone: '', address: '' },
  });
  const [editing, setEditing] = useState(false);
  const [photo, setPhoto] = useState(null);
  const [preview, setPreview] = useState('');
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!user) return;
    reset({
      fullName: user.fullName || '',
      phone: user.phone || '',
      address: user.address || '',
    });
    if (!photo) {
      setPreview(user.profileImage ? uploadUrl(user.profileImage) : '');
    }
  }, [user, reset, photo]);

  const startEdit = () => {
    setError('');
    setSuccess('');
    setPhoto(null);
    setPreview(user?.profileImage ? uploadUrl(user.profileImage) : '');
    reset({
      fullName: user?.fullName || '',
      phone: user?.phone || '',
      address: user?.address || '',
    });
    setEditing(true);
  };

  const cancelEdit = () => {
    setEditing(false);
    setPhoto(null);
    setPreview(user?.profileImage ? uploadUrl(user.profileImage) : '');
    setError('');
  };

  const onPhotoChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setPhoto(file);
    setPreview(URL.createObjectURL(file));
  };

  const onSubmit = async (data) => {
    setError('');
    setSuccess('');
    setSaving(true);
    try {
      const formData = new FormData();
      if (data.fullName) formData.append('fullName', data.fullName);
      if (data.phone != null) formData.append('phone', data.phone);
      if (data.address != null) formData.append('address', data.address);
      if (photo) formData.append('profileImage', photo);
      await userApi.updateProfile(formData);
      await refreshUser();
      setPhoto(null);
      setEditing(false);
      setSuccess('Profile updated successfully.');
    } catch (e) {
      setError(e.response?.data?.message || 'Update failed');
    } finally {
      setSaving(false);
    }
  };

  if (authLoading) {
    return (
      <Box display="flex" justifyContent="center" py={6}>
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return <Alert severity="warning">Please login to view and edit your profile.</Alert>;
  }

  const displayRoles = formatRoles(user.roles || []);

  return (
    <Box maxWidth={640}>
      <PageHeader title="My Profile" subtitle="View your account details" />
      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Card>
        <CardContent>
          {/* View mode */}
          {!editing && (
            <>
              <Box display="flex" alignItems="center" gap={2} mb={2}>
                <Avatar
                  src={user.profileImage ? uploadUrl(user.profileImage) : undefined}
                  sx={{ width: 88, height: 88, bgcolor: 'primary.main', fontSize: 32 }}
                >
                  {(user.fullName || user.username || '?').charAt(0).toUpperCase()}
                </Avatar>
                <Box>
                  <Typography variant="h5" fontWeight={700}>
                    {user.fullName || user.username}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {user.email}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    @{user.username}
                  </Typography>
                </Box>
              </Box>

              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Account roles
              </Typography>
              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap sx={{ mb: 2 }}>
                {displayRoles.length === 0 ? (
                  <Chip label="User" size="small" />
                ) : (
                  displayRoles.map((label) => (
                    <Chip
                      key={label}
                      label={label}
                      size="small"
                      color={roleChipColor(label)}
                      variant={label === 'User' ? 'outlined' : 'filled'}
                    />
                  ))
                )}
              </Stack>
              <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 2 }}>
                These show what you can access — e.g. Member unlocks membership features,
                Volunteer unlocks field-work tools. Everyone starts as User.
              </Typography>

              <Divider sx={{ my: 2 }} />

              <Typography variant="body2" sx={{ mb: 1 }}>
                <strong>Phone:</strong> {user.phone || 'Not set'}
              </Typography>
              <Typography variant="body2" sx={{ mb: 2 }}>
                <strong>Address:</strong> {user.address || 'Not set'}
              </Typography>

              <Button variant="contained" onClick={startEdit}>
                Edit Profile
              </Button>
            </>
          )}

          {/* Edit mode */}
          {editing && (
            <form onSubmit={handleSubmit(onSubmit)}>
              <Box display="flex" alignItems="center" gap={2} mb={3}>
                <Avatar
                  src={preview || undefined}
                  sx={{ width: 88, height: 88, bgcolor: 'primary.main', fontSize: 32 }}
                >
                  {(user.fullName || user.username || '?').charAt(0).toUpperCase()}
                </Avatar>
                <Box>
                  <Typography variant="subtitle1" fontWeight={600}>Update photo & details</Typography>
                  <Button component="label" variant="outlined" size="small" sx={{ mt: 1 }}>
                    {photo ? 'Change selected photo' : 'Choose Profile Photo'}
                    <input hidden type="file" accept="image/*" onChange={onPhotoChange} />
                  </Button>
                  {photo && (
                    <Typography variant="caption" display="block" color="text.secondary" sx={{ mt: 0.5 }}>
                      {photo.name}
                    </Typography>
                  )}
                </Box>
              </Box>

              <TextField
                fullWidth
                label="Full Name"
                margin="normal"
                InputLabelProps={{ shrink: true }}
                {...register('fullName', { required: true })}
              />
              <TextField
                fullWidth
                label="Phone"
                margin="normal"
                InputLabelProps={{ shrink: true }}
                {...register('phone')}
              />
              <TextField
                fullWidth
                label="Address"
                margin="normal"
                multiline
                rows={2}
                InputLabelProps={{ shrink: true }}
                {...register('address')}
              />

              <Box display="flex" gap={1} mt={2}>
                <Button type="submit" variant="contained" disabled={saving}>
                  {saving ? 'Saving...' : 'Save Changes'}
                </Button>
                <Button variant="outlined" onClick={cancelEdit} disabled={saving}>
                  Cancel
                </Button>
              </Box>
            </form>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
