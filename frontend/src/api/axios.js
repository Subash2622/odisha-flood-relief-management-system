/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const { data } = await axios.post(`${API_BASE}/auth/refresh`, { refreshToken });
          if (data.success) {
            localStorage.setItem('accessToken', data.data.accessToken);
            localStorage.setItem('refreshToken', data.data.refreshToken);
            original.headers.Authorization = `Bearer ${data.data.accessToken}`;
            return api(original);
          }
        } catch {
          localStorage.clear();
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;

export const uploadUrl = (path) => {
  if (!path) return null;
  if (path.startsWith('http')) return path;
  // Prefer dedicated files endpoint when path is /uploads/folder/file
  if (path.startsWith('/uploads/')) {
    const parts = path.replace('/uploads/', '').split('/');
    if (parts.length >= 2) {
      return `${API_BASE}/files/${parts[0]}/${parts.slice(1).join('/')}`;
    }
    return `${API_BASE}${path}`;
  }
  return `${API_BASE}${path.startsWith('/') ? path : `/${path}`}`;
};

export const unwrap = (response) => response.data?.data;
