import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/layout/MainLayout';
import ProtectedRoute from './components/common/ProtectedRoute';

import LandingPage from './pages/LandingPage';
import AboutPage from './pages/AboutPage';
import CampaignsPage from './pages/CampaignsPage';
import DonatePage from './pages/DonatePage';
import VolunteerPage from './pages/VolunteerPage';
import MembershipPage from './pages/MembershipPage';
import FloodReportPage from './pages/FloodReportPage';
import ContactPage from './pages/ContactPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ProfilePage from './pages/ProfilePage';
import CeoDashboard from './pages/dashboards/CeoDashboard';
import AdminDashboard from './pages/dashboards/AdminDashboard';
import VolunteerDashboard from './pages/dashboards/VolunteerDashboard';
import MemberDashboard from './pages/dashboards/MemberDashboard';
import UserDashboard from './pages/dashboards/UserDashboard';

function PublicRoute({ children, hideFooter }) {
  return <MainLayout hideFooter={hideFooter}>{children}</MainLayout>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<PublicRoute><LandingPage /></PublicRoute>} />
      <Route path="/about" element={<PublicRoute><AboutPage /></PublicRoute>} />
      <Route path="/campaigns" element={<PublicRoute><CampaignsPage /></PublicRoute>} />
      <Route path="/donate" element={<PublicRoute><DonatePage /></PublicRoute>} />
      <Route path="/volunteer" element={<PublicRoute><VolunteerPage /></PublicRoute>} />
      <Route path="/membership" element={<PublicRoute><MembershipPage /></PublicRoute>} />
      <Route path="/flood-report" element={<PublicRoute><FloodReportPage /></PublicRoute>} />
      <Route path="/contact" element={<PublicRoute><ContactPage /></PublicRoute>} />
      <Route path="/login" element={<PublicRoute hideFooter><LoginPage /></PublicRoute>} />
      <Route path="/register" element={<PublicRoute hideFooter><RegisterPage /></PublicRoute>} />
      <Route path="/forgot-password" element={<PublicRoute hideFooter><ForgotPasswordPage /></PublicRoute>} />

      <Route path="/profile" element={
        <ProtectedRoute>
          <MainLayout><ProfilePage /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/dashboard/ceo" element={
        <ProtectedRoute roles={['CEO']}>
          <MainLayout><CeoDashboard /></MainLayout>
        </ProtectedRoute>
      } />
      <Route path="/dashboard/admin" element={
        <ProtectedRoute roles={['CEO', 'ADMIN']}>
          <MainLayout><AdminDashboard /></MainLayout>
        </ProtectedRoute>
      } />
      <Route path="/dashboard/volunteer" element={
        <ProtectedRoute roles={['CEO', 'ADMIN', 'VOLUNTEER']}>
          <MainLayout><VolunteerDashboard /></MainLayout>
        </ProtectedRoute>
      } />
      <Route path="/dashboard/member" element={
        <ProtectedRoute roles={['CEO', 'ADMIN', 'MEMBER']}>
          <MainLayout><MemberDashboard /></MainLayout>
        </ProtectedRoute>
      } />
      <Route path="/dashboard/user" element={
        <ProtectedRoute>
          <MainLayout><UserDashboard /></MainLayout>
        </ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
