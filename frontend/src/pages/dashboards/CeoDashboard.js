import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box, Grid, Typography, Card, CardContent, Table, TableBody, TableCell,
  TableHead, TableRow, Button, Tabs, Tab, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, Alert, MenuItem, Chip,
} from '@mui/material';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import PeopleIcon from '@mui/icons-material/People';
import CampaignIcon from '@mui/icons-material/Campaign';
import ReportIcon from '@mui/icons-material/Report';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import { Bar, Line, Doughnut } from 'react-chartjs-2';
import { dashboardApi, ceoApi } from '../../api/services';
import StatCard from '../../components/common/StatCard';
import PageHeader from '../../components/common/PageHeader';

ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend, ArcElement);

export default function CeoDashboard() {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [tab, setTab] = useState(0);
  const [users, setUsers] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [payments, setPayments] = useState([]);
  const [org, setOrg] = useState(null);
  const [adminDialog, setAdminDialog] = useState(false);
  const [adminForm, setAdminForm] = useState({ username: '', email: '', password: '', fullName: '' });
  const [msg, setMsg] = useState('');
  const [popups, setPopups] = useState([]);
  const [popupDialog, setPopupDialog] = useState(false);
  const [popupForm, setPopupForm] = useState({ title: '', message: '', type: 'SUGGESTION', priority: 0 });

  useEffect(() => {
    dashboardApi.ceo().then(setData).catch(() => {});
    ceoApi.organization().then(setOrg).catch(() => {});
  }, []);

  useEffect(() => {
    if (tab === 1) ceoApi.users({ size: 20 }).then((d) => setUsers(d.content || [])).catch(() => {});
    if (tab === 2) ceoApi.auditLogs({ size: 20 }).then((d) => setAuditLogs(d.content || [])).catch(() => {});
    if (tab === 3) ceoApi.payments({ size: 20 }).then((d) => setPayments(d.content || [])).catch(() => {});
    if (tab === 5) loadPopups();
  }, [tab]);

  const loadPopups = () => ceoApi.popups().then(setPopups).catch(() => {});

  const exportExcel = async () => {
    await ceoApi.exportDonationsExcel();
  };

  const exportPdf = async () => {
    await ceoApi.exportDonationsPdf();
  };

  const createAdmin = async () => {
    await ceoApi.createAdmin(adminForm);
    setAdminDialog(false);
    setMsg('Admin created successfully');
    setAdminForm({ username: '', email: '', password: '', fullName: '' });
  };

  const createPopup = async () => {
    if (!popupForm.title.trim() || !popupForm.message.trim()) {
      setMsg('Popup title and message are required');
      return;
    }
    await ceoApi.createPopup({
      ...popupForm,
      priority: Number(popupForm.priority) || 0,
    });
    setPopupDialog(false);
    setPopupForm({ title: '', message: '', type: 'SUGGESTION', priority: 0 });
    setMsg('Home popup published');
    loadPopups();
  };

  if (!data) return <Typography>Loading CEO dashboard...</Typography>;

  const monthlyLabels = Object.keys(data.monthlyTrend || {});
  const monthlyValues = Object.values(data.monthlyTrend || {}).map((v) => parseFloat(v));

  const districtLabels = Object.keys(data.districtReports || {});
  const districtValues = Object.values(data.districtReports || {});

  return (
    <Box>
      <PageHeader title="CEO Dashboard" subtitle="Complete financial overview and system management" />
      {msg && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setMsg('')}>{msg}</Alert>}

      <Alert severity="info" sx={{ mb: 2 }}
        action={
          <Button color="inherit" size="small" variant="outlined" onClick={() => navigate('/dashboard/admin')}>
            Open Approvals
          </Button>
        }
      >
        Approve pending members &amp; volunteers from the Approvals page.
      </Alert>

      <Grid container spacing={2} mb={3}>
        <Grid item xs={6} md={3}><StatCard title="Total Donations" value={`₹${data.totalDonations?.toLocaleString()}`} icon={<AttachMoneyIcon />} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Today's Donations" value={`₹${data.todayDonations?.toLocaleString()}`} color="success.main" /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Monthly" value={`₹${data.monthlyDonations?.toLocaleString()}`} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Yearly" value={`₹${data.yearlyDonations?.toLocaleString()}`} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Members" value={data.totalMembers} icon={<PeopleIcon />} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Volunteers" value={data.totalVolunteers} icon={<PeopleIcon />} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Active Campaigns" value={data.activeCampaigns} icon={<CampaignIcon />} /></Grid>
        <Grid item xs={6} md={3}><StatCard title="Flood Reports" value={data.floodReports} icon={<ReportIcon />} color="error.main" /></Grid>
      </Grid>

      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} md={8}>
          <Card><CardContent>
            <Typography variant="h6" gutterBottom>Monthly Donation Trend</Typography>
            <Line data={{
              labels: monthlyLabels,
              datasets: [{ label: 'Donations (₹)', data: monthlyValues, borderColor: '#1565c0', tension: 0.3 }],
            }} />
          </CardContent></Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card><CardContent>
            <Typography variant="h6" gutterBottom>Flood Reports by District</Typography>
            <Doughnut data={{
              labels: districtLabels,
              datasets: [{ data: districtValues, backgroundColor: ['#1565c0', '#ff6f00', '#2e7d32', '#d32f2f', '#7b1fa2'] }],
            }} />
          </CardContent></Card>
        </Grid>
      </Grid>

      <Box display="flex" gap={1} mb={2} flexWrap="wrap">
        <Button variant="contained" color="warning" onClick={() => navigate('/dashboard/admin')}>
          Approve Members / Volunteers
        </Button>
        <Button variant="contained" color="info" onClick={() => setTab(5)}>
          Home Popups (Suggestions / Bugs)
        </Button>
        <Button variant="contained" onClick={exportExcel}>Donations Excel</Button>
        <Button variant="contained" color="secondary" onClick={exportPdf}>Donations PDF</Button>
        <Button variant="outlined" onClick={() => ceoApi.exportMembersExcel()}>Members Excel</Button>
        <Button variant="outlined" onClick={() => ceoApi.exportVolunteersExcel()}>Volunteers Excel</Button>
        <Button variant="outlined" onClick={() => ceoApi.exportCampaignsExcel()}>Campaigns Excel</Button>
        <Button variant="outlined" onClick={() => ceoApi.exportFloodReportsExcel()}>Flood Reports Excel</Button>
        <Button variant="outlined" onClick={() => setAdminDialog(true)}>Create Admin</Button>
      </Box>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }} variant="scrollable" scrollButtons="auto">
        <Tab label="Recent Transactions" />
        <Tab label="All Users" />
        <Tab label="Audit Logs" />
        <Tab label="Payments" />
        <Tab label="Bank Details" />
        <Tab label="Home Popups" />
      </Tabs>

      {tab === 0 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>ID</TableCell><TableCell>Donor</TableCell><TableCell>Amount</TableCell><TableCell>Status</TableCell><TableCell>Date</TableCell></TableRow></TableHead>
            <TableBody>
              {(data.recentTransactions || []).map((t) => (
                <TableRow key={t.donationId}>
                  <TableCell>{t.donationId}</TableCell>
                  <TableCell>{t.donorName}</TableCell>
                  <TableCell>₹{t.amount?.toLocaleString()}</TableCell>
                  <TableCell>{t.status}</TableCell>
                  <TableCell>{t.createdAt?.substring(0, 10)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 1 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Username</TableCell><TableCell>Name</TableCell><TableCell>Roles</TableCell><TableCell>Actions</TableCell></TableRow></TableHead>
            <TableBody>
              {users.map((u) => (
                <TableRow key={u.id}>
                  <TableCell>{u.username}</TableCell>
                  <TableCell>{u.fullName}</TableCell>
                  <TableCell>{u.roles?.join(', ')}</TableCell>
                  <TableCell>
                    <Button size="small" color="warning" onClick={() => ceoApi.disableUser(u.id)}>Disable</Button>
                    {!u.roles?.some((r) => r === 'ROLE_CEO' || r === 'CEO') && (
                      <Button size="small" color="error" onClick={() => ceoApi.deleteUser(u.id)}>Delete</Button>
                    )}
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
            <TableHead><TableRow><TableCell>Action</TableCell><TableCell>User</TableCell><TableCell>Details</TableCell><TableCell>Date</TableCell></TableRow></TableHead>
            <TableBody>
              {auditLogs.map((l) => (
                <TableRow key={l.id}>
                  <TableCell>{l.action}</TableCell>
                  <TableCell>{l.username || l.user?.username || '-'}</TableCell>
                  <TableCell>{l.details}</TableCell>
                  <TableCell>{l.createdAt?.substring(0, 19)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 3 && (
        <Card><CardContent>
          <Table size="small">
            <TableHead><TableRow><TableCell>Transaction ID</TableCell><TableCell>Amount</TableCell><TableCell>Method</TableCell><TableCell>Status</TableCell></TableRow></TableHead>
            <TableBody>
              {payments.map((p) => (
                <TableRow key={p.id}>
                  <TableCell>{p.paymentId}</TableCell>
                  <TableCell>₹{p.amount?.toLocaleString()}</TableCell>
                  <TableCell>{p.paymentMethod}</TableCell>
                  <TableCell>{p.status}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {tab === 4 && org && (
        <Card><CardContent>
          <Typography><strong>Bank:</strong> {org.bankName}</Typography>
          <Typography><strong>Account:</strong> {org.bankAccountNumber}</Typography>
          <Typography><strong>IFSC:</strong> {org.bankIfsc}</Typography>
          <Typography><strong>UPI:</strong> {org.upiId}</Typography>
        </CardContent></Card>
      )}

      {tab === 5 && (
        <Card><CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h6">Home screen popups (Suggestions / Bugs)</Typography>
            <Button variant="contained" onClick={() => setPopupDialog(true)}>Create Popup</Button>
          </Box>
          <Typography variant="body2" color="text.secondary" mb={2}>
            Active popups appear on the home page. Visitors can close them (like ecommerce offers). Closed popups stay dismissed in their browser.
          </Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Type</TableCell>
                <TableCell>Title</TableCell>
                <TableCell>Message</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {popups.map((p) => (
                <TableRow key={p.id}>
                  <TableCell><Chip size="small" label={p.type} color={p.type === 'BUG' ? 'error' : p.type === 'WARNING' ? 'warning' : 'info'} /></TableCell>
                  <TableCell>{p.title}</TableCell>
                  <TableCell>{p.message?.substring(0, 60)}{p.message?.length > 60 ? '...' : ''}</TableCell>
                  <TableCell>
                    <Chip size="small" label={p.isActive ? 'Active' : 'Off'} color={p.isActive ? 'success' : 'default'} />
                  </TableCell>
                  <TableCell>
                    <Button size="small" onClick={() => ceoApi.togglePopup(p.id).then(loadPopups)}>
                      {p.isActive ? 'Deactivate' : 'Activate'}
                    </Button>
                    <Button size="small" color="error" onClick={() => ceoApi.deletePopup(p.id).then(loadPopups)}>
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
              {popups.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} align="center">No popups yet. Create one for suggestions or bug notices.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent></Card>
      )}

      {data.topDonors?.length > 0 && (
        <Card sx={{ mt: 3 }}><CardContent>
          <Typography variant="h6" gutterBottom>Top Donors</Typography>
          <Bar data={{
            labels: data.topDonors.map((d) => d.name),
            datasets: [{ label: 'Amount (₹)', data: data.topDonors.map((d) => d.amount), backgroundColor: '#1565c0' }],
          }} options={{ indexAxis: 'y' }} />
        </CardContent></Card>
      )}

      <Dialog open={adminDialog} onClose={() => setAdminDialog(false)}>
        <DialogTitle>Create Admin</DialogTitle>
        <DialogContent>
          <TextField fullWidth label="Username" margin="dense" value={adminForm.username} onChange={(e) => setAdminForm({ ...adminForm, username: e.target.value })} />
          <TextField fullWidth label="Email" margin="dense" value={adminForm.email} onChange={(e) => setAdminForm({ ...adminForm, email: e.target.value })} />
          <TextField fullWidth label="Full Name" margin="dense" value={adminForm.fullName} onChange={(e) => setAdminForm({ ...adminForm, fullName: e.target.value })} />
          <TextField fullWidth label="Password" type="password" margin="dense" value={adminForm.password} onChange={(e) => setAdminForm({ ...adminForm, password: e.target.value })} />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAdminDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={createAdmin}>Create</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={popupDialog} onClose={() => setPopupDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Home Popup</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth select label="Type" margin="dense" value={popupForm.type}
            onChange={(e) => setPopupForm({ ...popupForm, type: e.target.value })}
          >
            <MenuItem value="SUGGESTION">Suggestion</MenuItem>
            <MenuItem value="BUG">Bug Notice</MenuItem>
            <MenuItem value="WARNING">Warning</MenuItem>
            <MenuItem value="OFFER">Offer</MenuItem>
          </TextField>
          <TextField
            fullWidth label="Title" margin="dense" value={popupForm.title}
            onChange={(e) => setPopupForm({ ...popupForm, title: e.target.value })}
          />
          <TextField
            fullWidth label="Message" margin="dense" multiline rows={4} value={popupForm.message}
            onChange={(e) => setPopupForm({ ...popupForm, message: e.target.value })}
          />
          <TextField
            fullWidth label="Priority (higher shows first)" type="number" margin="dense" value={popupForm.priority}
            onChange={(e) => setPopupForm({ ...popupForm, priority: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPopupDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={createPopup}>Publish</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
