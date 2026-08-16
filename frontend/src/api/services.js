/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import api, { unwrap } from './axios';

const downloadBlob = (path, filename) =>
  api.get(path, { responseType: 'blob' }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  });

export const authApi = {
  register: (data) => api.post('/auth/register', data).then(unwrap),
  login: (data) => api.post('/auth/login', data).then(unwrap),
  me: () => api.get('/auth/me').then(unwrap),
  logout: () => api.post('/auth/logout'),
  forgotPassword: (data) => api.post('/auth/forgot-password', data).then(unwrap),
  resetPassword: (data) => api.post('/auth/reset-password', data).then(unwrap),
};

export const publicApi = {
  organization: () => api.get('/public/organization').then(unwrap),
  announcements: () => api.get('/announcements').then(unwrap),
  activePopups: () => api.get('/public/popups').then(unwrap),
  contact: (data) => api.post('/contact', data).then(unwrap),
};

export const campaignApi = {
  getAll: (params) => api.get('/campaigns', { params }).then(unwrap),
  getActive: (params) => api.get('/campaigns/active', { params }).then(unwrap),
  getById: (id) => api.get(`/campaigns/${id}`).then(unwrap),
  create: (formData) =>
    api.post('/campaigns', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  update: (id, formData) =>
    api.put(`/campaigns/${id}`, formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  close: (id) => api.patch(`/campaigns/${id}/close`).then(unwrap),
};

export const donationApi = {
  amounts: () => api.get('/donations/amounts').then(unwrap),
  donate: (data) => api.post('/donations', data).then(unwrap),
  guestDonate: (data) => api.post('/donations/guest', data).then(unwrap),
  my: (params) => api.get('/donations/my', { params }).then(unwrap),
  getById: (id) => api.get(`/donations/${id}`).then(unwrap),
  getAll: (params) => api.get('/donations/all', { params }).then(unwrap),
  downloadReceipt: (donationId) =>
    downloadBlob(`/donations/${donationId}/receipt`, `receipt_${donationId}.pdf`),
  receiptUrl: (donationId) => `${process.env.REACT_APP_API_URL || '/api'}/donations/${donationId}/receipt`,
  qrUrl: (donationId) => `${process.env.REACT_APP_API_URL || '/api'}/donations/${donationId}/qr`,
};

export const membershipApi = {
  apply: () => api.post('/membership/apply').then(unwrap),
  my: () => api.get('/membership/my').then(unwrap),
  renew: () => api.post('/membership/renew').then(unwrap),
  getAll: (params) => api.get('/membership', { params }).then(unwrap),
  pending: (params) => api.get('/membership/pending', { params }).then(unwrap),
  approve: (id) => api.patch(`/membership/${id}/approve`).then(unwrap),
  downloadCard: () => downloadBlob('/membership/my/card', 'membership-card.pdf'),
};

export const volunteerApi = {
  apply: () => api.post('/volunteers/apply').then(unwrap),
  my: () => api.get('/volunteers/my').then(unwrap),
  updateStatus: (data) => api.patch('/volunteers/work-status', data).then(unwrap),
  uploadPhotos: (formData) =>
    api.post('/volunteers/photos', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  getAll: (params) => api.get('/volunteers', { params }).then(unwrap),
  pending: (params) => api.get('/volunteers/pending', { params }).then(unwrap),
  approve: (id) => api.patch(`/volunteers/${id}/approve`).then(unwrap),
  assign: (id, data) => api.patch(`/volunteers/${id}/assign`, data).then(unwrap),
};

export const floodApi = {
  reportPublic: (formData) =>
    api.post('/flood-reports/public', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  report: (formData) =>
    api.post('/flood-reports', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  getAll: (params) => api.get('/flood-reports', { params }).then(unwrap),
  getById: (id) => api.get(`/flood-reports/${id}`).then(unwrap),
  updateStatus: (id, data) => api.patch(`/flood-reports/${id}/status`, data).then(unwrap),
};

export const dashboardApi = {
  ceo: () => api.get('/dashboard/ceo').then(unwrap),
  admin: () => api.get('/dashboard/admin').then(unwrap),
};

export const ceoApi = {
  users: (params) => api.get('/ceo/users', { params }).then(unwrap),
  createAdmin: (data) => api.post('/ceo/admins', data).then(unwrap),
  disableUser: (id) => api.patch(`/ceo/users/${id}/disable`).then(unwrap),
  deleteUser: (id) => api.delete(`/ceo/users/${id}`).then(unwrap),
  organization: () => api.get('/ceo/organization').then(unwrap),
  updateOrganization: (formData) =>
    api.put('/ceo/organization', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  createAnnouncement: (data) => api.post('/ceo/announcements', data).then(unwrap),
  payments: (params) => api.get('/ceo/payments', { params }).then(unwrap),
  auditLogs: (params) => api.get('/ceo/audit-logs', { params }).then(unwrap),
  exportDonationsExcel: () => downloadBlob('/ceo/reports/donations/excel', 'donations.xlsx'),
  exportDonationsPdf: () => downloadBlob('/ceo/reports/donations/pdf', 'donations.pdf'),
  exportMembersExcel: () => downloadBlob('/ceo/reports/members/excel', 'members.xlsx'),
  exportVolunteersExcel: () => downloadBlob('/ceo/reports/volunteers/excel', 'volunteers.xlsx'),
  exportCampaignsExcel: () => downloadBlob('/ceo/reports/campaigns/excel', 'campaigns.xlsx'),
  exportFloodReportsExcel: () => downloadBlob('/ceo/reports/flood-reports/excel', 'flood-reports.xlsx'),
  popups: () => api.get('/ceo/popups').then(unwrap),
  createPopup: (data) => api.post('/ceo/popups', data).then(unwrap),
  togglePopup: (id) => api.patch(`/ceo/popups/${id}/toggle`).then(unwrap),
  deletePopup: (id) => api.delete(`/ceo/popups/${id}`).then(unwrap),
};

export const reliefApi = {
  distribute: (formData) =>
    api.post('/admin/relief/distribute', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
  history: (params) => api.get('/admin/relief/history', { params }).then(unwrap),
  my: (params) => api.get('/volunteer/relief/my', { params }).then(unwrap),
};

export const userApi = {
  updateProfile: (formData) =>
    api.put('/users/profile', formData, { headers: { 'Content-Type': 'multipart/form-data' } }).then(unwrap),
};

export const notificationApi = {
  getAll: (params) => api.get('/notifications', { params }).then(unwrap),
};
