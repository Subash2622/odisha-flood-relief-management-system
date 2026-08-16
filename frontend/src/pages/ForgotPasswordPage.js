/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  Box, Card, CardContent, TextField, Button, Typography, Alert, Link,
} from '@mui/material';
import { authApi } from '../api/services';
import PageHeader from '../components/common/PageHeader';

export default function ForgotPasswordPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [identifier, setIdentifier] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [usernameHint, setUsernameHint] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [maskedDestination, setMaskedDestination] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const requestOtp = async () => {
    setError('');
    setSuccess('');
    if (!identifier.trim()) {
      setError('Enter registered email or username');
      return;
    }
    setLoading(true);
    try {
      const res = await authApi.forgotPassword({ identifier: identifier.trim() });
      if (!res?.resetToken) {
        setError('Reset session was not created. Restart backend and try again.');
        return;
      }
      setResetToken(res.resetToken);
      setUsernameHint(res.usernameHint || '');
      setMaskedDestination(res.maskedDestination || '');
      setSuccess(res.message || 'OTP sent to your email');
      setStep(2);
    } catch (e) {
      setError(e.response?.data?.message || 'User not found or failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  const resetPassword = async () => {
    setError('');
    setSuccess('');
    if (!resetToken) {
      setError('Missing reset session. Go back and request OTP again.');
      return;
    }
    if (!otp.trim() || !newPassword.trim()) {
      setError('OTP and new password are required');
      return;
    }
    if (newPassword.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }
    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    setLoading(true);
    try {
      const res = await authApi.resetPassword({
        resetToken,
        otp: otp.trim(),
        newPassword,
      });
      const loginAs = res?.username || usernameHint;
      setSuccess(
        (res?.message || 'Password updated.') +
          (loginAs ? ` Login with username: ${loginAs}` : '')
      );
      setTimeout(() => navigate('/login', { state: { username: loginAs } }), 1800);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to reset password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box maxWidth={460} mx="auto">
      <PageHeader title="Forgot Password" subtitle="OTP will be sent to your registered email" />
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

      <Card>
        <CardContent>
          {step === 1 && (
            <>
              <TextField
                fullWidth
                margin="normal"
                label="Email or Username"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                helperText="Must match a user already registered in the database"
              />
              <Button
                fullWidth
                variant="contained"
                size="large"
                sx={{ mt: 2 }}
                disabled={loading}
                onClick={requestOtp}
              >
                {loading ? 'Sending...' : 'Send OTP to Email'}
              </Button>
            </>
          )}

          {step === 2 && (
            <>
              <Alert severity="info" sx={{ mb: 2 }}>
                Enter the OTP sent to <strong>{maskedDestination || 'your email'}</strong>
                {usernameHint ? <> (user: <strong>{usernameHint}</strong>)</> : null}.
                Check Spam if you do not see it.
              </Alert>
              <TextField
                fullWidth
                margin="normal"
                label="Enter OTP from email"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
              />
              <TextField
                fullWidth
                margin="normal"
                type="password"
                label="New Password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
              <TextField
                fullWidth
                margin="normal"
                type="password"
                label="Confirm Password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
              <Button
                fullWidth
                variant="contained"
                size="large"
                sx={{ mt: 2 }}
                disabled={loading}
                onClick={resetPassword}
              >
                {loading ? 'Updating...' : 'Reset Password'}
              </Button>
              <Button
                fullWidth
                sx={{ mt: 1 }}
                onClick={() => {
                  setStep(1);
                  setResetToken('');
                  setOtp('');
                }}
              >
                Back / Resend OTP
              </Button>
            </>
          )}

          <Typography variant="body2" align="center" sx={{ mt: 2 }}>
            Remembered password? <Link component={RouterLink} to="/login">Login</Link>
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}
