import { useEffect, useState } from 'react';
import {
  Box, Grid, Typography, Card, CardContent, Table, TableBody, TableCell,
  TableHead, TableRow, Button, Tabs, Tab, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, MenuItem, Alert,
} from '@mui/material';
import { dashboardApi, membershipApi, volunteerApi, floodApi, reliefApi } from '../../api/services';
import StatCard from '../../components/common/StatCard';
import PageHeader from '../../components/common/PageHeader';

const RELIEF_ITEMS = ['FOOD_KITS', 'MEDICINE', 'BLANKETS', 'WATER', 'BABY_FOOD', 'CLOTHES', 'TENTS'];

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [tab, setTab] = useState(0);
  const [pendingMembers, setPendingMembers] = useState([]);
  const [pendingVolunteers, setPendingVolunteers] = useState([]);
  const [floodReports, setFloodReports] = useState([]);
  const [reliefHistory, setReliefHistory] = useState([]);
  const [reliefOpen, setReliefOpen] = useState(false);
  const [reliefForm, setReliefForm] = useState({ itemType: 'FOOD_KITS', quantity: 1, village: '', district: '' });
  const [msg, setMsg] = useState('');

  useEffect(() => {
    dashboardApi.admin().then(setStats).catch(() => {});
  }, []);

  useEffect(() => {
    if (tab === 0) membershipApi.pending({ size: 20 }).then((d) => setPendingMembers(d.content || [])).catch(() => {});
    if (tab === 1) volunteerApi.pending({ size: 20 }).then((d) => setPendingVolunteers(d.content || [])).catch(() => {});
    if (tab === 2) floodApi.getAll({ size: 20 }).then((d) => setFloodReports(d.content || [])).catch(() => {});
    if (tab === 3) reliefApi.history({ size: 20 }).then((d) => setReliefHistory(d.content || [])).catch(() => {});
  }, [tab]);

  const distribute = async () => {
    const formData = new FormData();
    formData.append('distribution', new Blob([JSON.stringify(reliefForm)], { type: 'application/json' }));
    await reliefApi.distribute(formData);
    setReliefOpen(false);
    setMsg('Relief distribution recorded');
  };

  if (!stats) return <Typography>Loading admin dashboard...</Typography>;

  return (
    <Box>
      <PageHeader title="Admin Dashboard" subtitle="Manage approvals, flood reports, and relief distribution" />
      {msg && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setMsg('')}>{msg}</Alert>}

      <Grid container spacing={2} mb={3}>
        <Grid item xs={6} md={4}><StatCard title="Pending Members" value={stats.pendingMembers} color="warning.main" /></Grid>
        <Grid item xs={6} md={4}><StatCard title="Pending Volunteers" value={stats.pendingVolunteers} color="warning.main" /></Grid>
        <Grid item xs={6} md={4}><StatCard title="Pending Flood Reports" value={stats.pendingFloodReports} color="error.main" /></Grid>
        <Grid item xs={6} md={4}><StatCard title="Total Members" value={stats.totalMembers} /></Grid>
        <Grid item xs={6} md={4}><StatCard title="Total Volunteers" value={stats.totalVolunteers} /></Grid>
        <Grid item xs={6} md={4}><StatCard title="Active Campaigns" value={stats.activeCampaigns} /></Grid>
      </Grid>

      <Button variant="contained" sx={{ mb: 2 }} onClick={() => setReliefOpen(true)}>Record Relief Distribution</Button>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label="Pending Memberships" />
        <Tab label="Pending Volunteers" />
        <Tab label="Flood Reports" />
        <Tab label="Relief History" />
      </Tabs>

      {tab === 0 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Name</TableCell><TableCell>Email</TableCell><TableCell>Status</TableCell><TableCell>Action</TableCell></TableRow></TableHead>
            <TableBody>
              {pendingMembers.map((m) => (
                <TableRow key={m.id}>
                  <TableCell>{m.fullName}</TableCell>
                  <TableCell>{m.email}</TableCell>
                  <TableCell>{m.status}</TableCell>
                  <TableCell>
                    <Button
                      size="small"
                      variant="contained"
                      onClick={() =>
                        membershipApi.approve(m.id).then(() => {
                          setMsg('Member approved');
                          membershipApi.pending({ size: 20 }).then((d) => setPendingMembers(d.content || []));
                          dashboardApi.admin().then(setStats).catch(() => {});
                        })
                      }
                    >
                      Approve
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 1 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Name</TableCell><TableCell>Email</TableCell><TableCell>Status</TableCell><TableCell>Actions</TableCell></TableRow></TableHead>
            <TableBody>
              {pendingVolunteers.map((v) => (
                <TableRow key={v.id}>
                  <TableCell>{v.fullName}</TableCell>
                  <TableCell>{v.email}</TableCell>
                  <TableCell>{v.status}</TableCell>
                  <TableCell>
                    <Button size="small" onClick={() => volunteerApi.approve(v.id).then(() => setMsg('Volunteer approved'))}>Approve</Button>
                    <Button size="small" onClick={() => volunteerApi.assign(v.id, { area: 'Relief Camp', district: 'Cuttack' })}>Assign</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 2 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Village</TableCell><TableCell>District</TableCell><TableCell>Urgency</TableCell><TableCell>Status</TableCell><TableCell>Actions</TableCell></TableRow></TableHead>
            <TableBody>
              {floodReports.map((r) => (
                <TableRow key={r.id}>
                  <TableCell>{r.village}</TableCell>
                  <TableCell>{r.district}</TableCell>
                  <TableCell>{r.urgency}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell>
                    <Button size="small" onClick={() => floodApi.updateStatus(r.id, { status: 'ACCEPTED' })}>Accept</Button>
                    <Button size="small" onClick={() => floodApi.updateStatus(r.id, { status: 'RESOLVED' })}>Resolve</Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 3 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Item</TableCell><TableCell>Qty</TableCell><TableCell>Village</TableCell><TableCell>District</TableCell><TableCell>Date</TableCell></TableRow></TableHead>
            <TableBody>
              {reliefHistory.map((r) => (
                <TableRow key={r.id}>
                  <TableCell>{r.itemType}</TableCell>
                  <TableCell>{r.quantity}</TableCell>
                  <TableCell>{r.village}</TableCell>
                  <TableCell>{r.district}</TableCell>
                  <TableCell>{r.createdAt?.substring(0, 10)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      <Dialog open={reliefOpen} onClose={() => setReliefOpen(false)}>
        <DialogTitle>Record Relief Distribution</DialogTitle>
        <DialogContent>
          <TextField fullWidth select label="Item Type" margin="dense" value={reliefForm.itemType} onChange={(e) => setReliefForm({ ...reliefForm, itemType: e.target.value })}>
            {RELIEF_ITEMS.map((i) => <MenuItem key={i} value={i}>{i.replace('_', ' ')}</MenuItem>)}
          </TextField>
          <TextField fullWidth label="Quantity" type="number" margin="dense" value={reliefForm.quantity} onChange={(e) => setReliefForm({ ...reliefForm, quantity: parseInt(e.target.value, 10) })} />
          <TextField fullWidth label="Village" margin="dense" value={reliefForm.village} onChange={(e) => setReliefForm({ ...reliefForm, village: e.target.value })} />
          <TextField fullWidth label="District" margin="dense" value={reliefForm.district} onChange={(e) => setReliefForm({ ...reliefForm, district: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setReliefOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={distribute}>Record</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
