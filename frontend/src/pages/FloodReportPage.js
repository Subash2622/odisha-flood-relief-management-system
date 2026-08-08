import { useState } from 'react';
import {
  Box, Card, CardContent, TextField, Button, Alert, MenuItem, Grid,
} from '@mui/material';
import { useForm } from 'react-hook-form';
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
  const { register, handleSubmit, reset } = useForm({ defaultValues: { urgency: 'HIGH' } });
  const [photo, setPhoto] = useState(null);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState('');

  const onSubmit = async (data) => {
    setError('');
    try {
      const formData = new FormData();
      formData.append('report', new Blob([JSON.stringify({
        village: data.village,
        district: data.district,
        description: data.description,
        urgency: data.urgency,
        gpsLatitude: data.gpsLatitude ? parseFloat(data.gpsLatitude) : null,
        gpsLongitude: data.gpsLongitude ? parseFloat(data.gpsLongitude) : null,
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
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((pos) => {
        document.getElementById('gpsLatitude').value = pos.coords.latitude;
        document.getElementById('gpsLongitude').value = pos.coords.longitude;
      });
    }
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
                <TextField fullWidth label="Village" required {...register('village', { required: true })} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth select label="District" required {...register('district', { required: true })}>
                  {DISTRICTS.map((d) => <MenuItem key={d} value={d}>{d}</MenuItem>)}
                </TextField>
              </Grid>
              <Grid item xs={12}>
                <TextField fullWidth label="Description" multiline rows={4} required {...register('description', { required: true })} />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField fullWidth select label="Urgency" {...register('urgency')}>
                  {URGENCY_LEVELS.map((u) => <MenuItem key={u} value={u}>{u}</MenuItem>)}
                </TextField>
              </Grid>
              {!user && (
                <>
                  <Grid item xs={12} sm={6}>
                    <TextField fullWidth label="Your Name" {...register('reporterName')} />
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <TextField fullWidth label="Phone" {...register('reporterPhone')} />
                  </Grid>
                </>
              )}
              <Grid item xs={12} sm={5}>
                <TextField fullWidth label="GPS Latitude" id="gpsLatitude" type="number" inputProps={{ step: 'any' }} {...register('gpsLatitude')} />
              </Grid>
              <Grid item xs={12} sm={5}>
                <TextField fullWidth label="GPS Longitude" id="gpsLongitude" type="number" inputProps={{ step: 'any' }} {...register('gpsLongitude')} />
              </Grid>
              <Grid item xs={12} sm={2} display="flex" alignItems="center">
                <Button onClick={getLocation} variant="outlined" fullWidth>Use GPS</Button>
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
