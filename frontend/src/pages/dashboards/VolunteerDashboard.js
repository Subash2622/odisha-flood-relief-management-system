import { useEffect, useState } from 'react';
import {
  Box, Grid, Card, CardContent, Typography, Button, MenuItem, TextField, Alert, Table,
  TableBody, TableCell, TableHead, TableRow, Chip,
} from '@mui/material';
import { volunteerApi, reliefApi, notificationApi } from '../../api/services';
import { uploadUrl } from '../../api/axios';
import PageHeader from '../../components/common/PageHeader';

const WORK_STATUSES = ['ASSIGNED', 'IN_PROGRESS', 'COMPLETED'];

export default function VolunteerDashboard() {
  const [profile, setProfile] = useState(null);
  const [distributions, setDistributions] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [status, setStatus] = useState('IN_PROGRESS');
  const [notes, setNotes] = useState('');
  const [msg, setMsg] = useState('');

  useEffect(() => {
    volunteerApi.my().then(setProfile).catch(() => {});
    reliefApi.my({ size: 10 }).then((d) => setDistributions(d.content || [])).catch(() => {});
    notificationApi.getAll({ size: 10 }).then((d) => setNotifications(d.content || [])).catch(() => {});
  }, []);

  const updateStatus = async () => {
    await volunteerApi.updateWorkStatus({ status, notes });
    setMsg('Work status updated');
    volunteerApi.my().then(setProfile);
  };

  const uploadPhotos = async (e) => {
    const formData = new FormData();
    if (e.target.files[0]) formData.append('before', e.target.files[0]);
    await volunteerApi.uploadPhotos(formData);
    setMsg('Photo uploaded');
  };

  if (!profile) return <Typography>Loading volunteer dashboard...</Typography>;

  return (
    <Box>
      <PageHeader title="Volunteer Dashboard" subtitle={`Welcome, ${profile.fullName}`} />

      {msg && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setMsg('')}>{msg}</Alert>}

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>My Assignment</Typography>
              <Typography><strong>Volunteer ID:</strong> {profile.volunteerId}</Typography>
              <Typography><strong>Status:</strong> <Chip label={profile.status} size="small" /></Typography>
              <Typography><strong>Area:</strong> {profile.assignedArea || 'Not assigned yet'}</Typography>
              <Typography><strong>District:</strong> {profile.assignedDistrict || '-'}</Typography>
              <Typography><strong>Work Status:</strong> <Chip label={profile.workStatus} color="primary" size="small" /></Typography>
              {profile.notes && <Typography variant="body2" sx={{ mt: 1 }}>{profile.notes}</Typography>}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Update Work Status</Typography>
              <TextField fullWidth select label="Status" value={status} onChange={(e) => setStatus(e.target.value)} sx={{ mb: 2 }}>
                {WORK_STATUSES.map((s) => <MenuItem key={s} value={s}>{s.replace('_', ' ')}</MenuItem>)}
              </TextField>
              <TextField fullWidth label="Notes" multiline rows={2} value={notes} onChange={(e) => setNotes(e.target.value)} sx={{ mb: 2 }} />
              <Button variant="contained" onClick={updateStatus} sx={{ mr: 1 }}>Update Status</Button>
              <Button component="label" variant="outlined">
                Upload Photo
                <input hidden type="file" accept="image/*" onChange={uploadPhotos} />
              </Button>
              {profile.beforePhoto && <Box mt={2}><img src={uploadUrl(profile.beforePhoto)} alt="Before" width={120} /></Box>}
              {profile.afterPhoto && <Box mt={1}><img src={uploadUrl(profile.afterPhoto)} alt="After" width={120} /></Box>}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>My Relief Distributions</Typography>
              <Table size="small">
                <TableHead><TableRow><TableCell>Item</TableCell><TableCell>Qty</TableCell><TableCell>Location</TableCell></TableRow></TableHead>
                <TableBody>
                  {distributions.map((d) => (
                    <TableRow key={d.id}>
                      <TableCell>{d.itemType}</TableCell>
                      <TableCell>{d.quantity}</TableCell>
                      <TableCell>{d.village}, {d.district}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>Notifications</Typography>
              {notifications.map((n) => (
                <Box key={n.id} sx={{ mb: 1, p: 1, bgcolor: n.isRead ? 'transparent' : 'action.hover', borderRadius: 1 }}>
                  <Typography variant="subtitle2">{n.title}</Typography>
                  <Typography variant="body2" color="text.secondary">{n.message}</Typography>
                </Box>
              ))}
              {notifications.length === 0 && <Typography color="text.secondary">No notifications</Typography>}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
