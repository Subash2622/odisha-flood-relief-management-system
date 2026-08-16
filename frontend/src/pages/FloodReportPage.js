/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import {
  Box, Card, CardContent, TextField, Button, Alert, MenuItem, Grid,
  FormControl, InputLabel, Select,
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { floodApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

const URGENCY_LEVELS = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
const DISTRICTS = [
  'Angul', 'Balangir', 'Balasore', 'Bargarh', 'Bhadrak', 'Boudh', 'Cuttack',
  'Deogarh', 'Dhenkanal', 'Gajapati', 'Ganjam', 'Jagatsinghpur', 'Jajpur',
  'Jharsuguda', 'Kalahandi', 'Kandhamal', 'Kendrapara', 'Kendujhar', 'Khordha',
  'Koraput', 'Malkangiri', 'Mayurbhanj', 'Nabarangpur', 'Nayagarh', 'Nuapada',
  'Puri', 'Rayagada', 'Sambalpur', 'Subarnapur', 'Sundargarh',
];

export default function FloodReportPage() {
  const { user } = useAuth();
  const { register, handleSubmit, reset, control, setValue } = useForm({
    defaultValues: {
      urgency: 'HIGH',
      district: '',
      village: '',
      description: '',
      gpsLatitude: '',
      gpsLongitude: '',
      reporterName: '',
      reporterPhone: '',
    },
  });
  const [photo, setPhoto] = useState(null);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState('');
  const [gpsLoading, setGpsLoading] = useState(false);
  const onSubmit = async (data) => {
    setError('');
    try {
      const formData = new FormData();
      formData.append('report', new Blob([JSON.stringify({
        village: data.village,
        district: data.district,
        description: data.description,
        urgency: data.urgency,
        gpsLatitude: data.gpsLatitude !== '' && data.gpsLatitude != null
          ? parseFloat(data.gpsLatitude) : null,
        gpsLongitude: data.gpsLongitude !== '' && data.gpsLongitude != null
          ? parseFloat(data.gpsLongitude) : null,
        reporterName: data.reporterName,
        reporterPhone: data.reporterPhone,
      })], { type: 'application/json' }));
      if (photo) formData.append('photo', photo);

      const res = user ? await floodApi.report(formData) : await floodApi.reportPublic(formData);
      setSuccess(res);
      reset();
      setPhoto(null);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to submit report');
    }
  };

  const getLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by this browser');
      return;
    }
    setError('');
    setGpsLoading(true);
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setValue('gpsLatitude', String(pos.coords.latitude), { shouldDirty: true, shouldValidate: true });
        setValue('gpsLongitude', String(pos.coords.longitude), { shouldDirty: true, shouldValidate: true });
        setGpsLoading(false);
      },
      () => {
        setGpsLoading(false);
        setError('Unable to fetch GPS location. Allow location permission and try again.');
      },
      { enableHighAccuracy: true, timeout: 15000 }
    );
  };

  return (
    <Box>
      <PageHeader title="Report Flood Emergency" subtitle="Help us respond quickly to flood situations" />
      {success && <Alert severity="success" sx={{ mb: 2 }}>Report submitted! ID: {success.id}. Status: {success.status}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Card>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Village"
                  required
                  InputLabelProps={{ shrink: true }}
                  {...register('village', { required: true })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth required>
                  <InputLabel id="district-label" shrink>District</InputLabel>
                  <Controller
                    name="district"
                    control={control}
                    rules={{ required: true }}
                    render={({ field }) => (
                      <Select
                        {...field}
                        labelId="district-label"
                        label="District"
                        displayEmpty
                        notched
                        value={field.value ?? ''}
                      >
                        <MenuItem value="" disabled>
                          <em>Select district</em>
                        </MenuItem>
                        {DISTRICTS.map((d) => (
                          <MenuItem key={d} value={d}>{d}</MenuItem>
                        ))}
                      </Select>
                    )}
                  />
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  multiline
                  rows={4}
                  required
                  InputLabelProps={{ shrink: true }}
                  {...register('description', { required: true })}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <InputLabel id="urgency-label" shrink>Urgency</InputLabel>
                  <Controller
                    name="urgency"
                    control={control}
                    render={({ field }) => (
                      <Select
                        {...field}
                        labelId="urgency-label"
                        label="Urgency"
                        notched
                        value={field.value ?? 'HIGH'}
                      >
                        {URGENCY_LEVELS.map((u) => (
                          <MenuItem key={u} value={u}>{u}</MenuItem>
                        ))}
                      </Select>
                    )}
                  />
                </FormControl>
              </Grid>
              {!user && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField fullWidth label="Your Name" InputLabelProps={{ shrink: true }} {...register('reporterName')} />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField fullWidth label="Phone" InputLabelProps={{ shrink: true }} {...register('reporterPhone')} />
                  </Grid>
                </>
              )}
              <Grid item xs={12} sm={5}>
                <Controller
                  name="gpsLatitude"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="GPS Latitude"
                      type="number"
                      value={field.value ?? ''}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 'any' }}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={5}>
                <Controller
                  name="gpsLongitude"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="GPS Longitude"
                      type="number"
                      value={field.value ?? ''}
                      InputLabelProps={{ shrink: true }}
                      inputProps={{ step: 'any' }}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={2} display="flex" alignItems="center">
                <Button onClick={getLocation} variant="outlined" fullWidth disabled={gpsLoading}>
                  {gpsLoading ? 'Locating...' : 'Use GPS'}
                </Button>
              </Grid>
              <Grid item xs={12}>
                <Button component="label" variant="outlined">
                  Upload Photo
                  <input hidden type="file" accept="image/*" onChange={(e) => setPhoto(e.target.files[0])} />
                </Button>
                {photo && <span style={{ marginLeft: 8 }}>{photo.name}</span>}
              </Grid>
              <Grid item xs={12}>
                <Button type="submit" variant="contained" size="large">Submit Report</Button>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
