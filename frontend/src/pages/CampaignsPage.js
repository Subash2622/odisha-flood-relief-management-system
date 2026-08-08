import { useEffect, useState } from 'react';
import {
  Grid, Card, CardContent, CardMedia, Typography, LinearProgress, Box, Chip, Button, Dialog,
  DialogTitle, DialogContent, DialogActions, TextField,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { campaignApi } from '../api/services';
import { uploadUrl } from '../api/axios';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/common/PageHeader';

export default function CampaignsPage() {
  const [campaigns, setCampaigns] = useState([]);
  const [open, setOpen] = useState(false);
  const { hasRole } = useAuth();
  const { register, handleSubmit, reset } = useForm();
  const [banner, setBanner] = useState(null);

  const load = () => campaignApi.getAll({ size: 20 }).then((d) => setCampaigns(d.content || [])).catch(() => {});

  useEffect(() => { load(); }, []);

  const onCreate = async (data) => {
    const formData = new FormData();
    formData.append('campaign', new Blob([JSON.stringify({
      title: data.title,
      description: data.description,
      targetAmount: parseFloat(data.targetAmount),
      startDate: data.startDate,
      endDate: data.endDate,
    })], { type: 'application/json' }));
    if (banner) formData.append('banner', banner);
    await campaignApi.create(formData);
    setOpen(false);
    reset();
    load();
  };

  const canManage = hasRole('CEO') || hasRole('ADMIN');

  return (
    <Box>
      <PageHeader title="Relief Campaigns" subtitle="Support flood relief efforts across Odisha" />
      {canManage && (
        <Button variant="contained" sx={{ mb: 3 }} onClick={() => setOpen(true)}>Create Campaign</Button>
      )}
      <Grid container spacing={3}>
        {campaigns.map((c) => (
          <Grid item xs={12} sm={6} md={4} key={c.id}>
            <Card sx={{ height: '100%' }}>
              {c.bannerImage && <CardMedia component="img" height="180" image={uploadUrl(c.bannerImage)} alt={c.title} />}
              <CardContent>
                <Box display="flex" justifyContent="space-between" mb={1}>
                  <Typography variant="h6">{c.title}</Typography>
                  <Chip label={c.status} size="small" color={c.status === 'ACTIVE' ? 'success' : 'default'} />
                </Box>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
                  {c.description?.substring(0, 100)}...
                </Typography>
                <Typography variant="body2">₹{c.collectedAmount?.toLocaleString()} / ₹{c.targetAmount?.toLocaleString()}</Typography>
                <LinearProgress variant="determinate" value={c.progressPercent || 0} sx={{ my: 1, height: 8, borderRadius: 4 }} />
                {canManage && c.status === 'ACTIVE' && (
                  <Button size="small" color="warning" onClick={() => campaignApi.close(c.id).then(load)}>
                    Close Campaign
                  </Button>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Campaign</DialogTitle>
        <form onSubmit={handleSubmit(onCreate)}>
          <DialogContent>
            <TextField fullWidth label="Title" margin="dense" {...register('title', { required: true })} />
            <TextField fullWidth label="Description" margin="dense" multiline rows={3} {...register('description')} />
            <TextField fullWidth label="Target Amount (₹)" type="number" margin="dense" {...register('targetAmount', { required: true })} />
            <TextField fullWidth label="Start Date" type="date" margin="dense" InputLabelProps={{ shrink: true }} {...register('startDate')} />
            <TextField fullWidth label="End Date" type="date" margin="dense" InputLabelProps={{ shrink: true }} {...register('endDate')} />
            <Button component="label" sx={{ mt: 1 }}>Upload Banner<input hidden type="file" accept="image/*" onChange={(e) => setBanner(e.target.files[0])} /></Button>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit" variant="contained">Create</Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
}
