/*
 * Subash Chandra Sahoo
 * Software Engineer
 * Odisha Flood Relief & NGO Management System
 * Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.
 */
import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authApi } from '../api/services';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadUser = useCallback(async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      setLoading(false);
      return;
    }
    try {
      const data = await authApi.me();
      setUser(data);
    } catch {
      localStorage.clear();
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadUser();
  }, [loadUser]);

  const login = async (credentials) => {
    const data = await authApi.login(credentials);
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('roles', JSON.stringify(data.roles));
    setUser({
      id: data.id,
      username: data.username,
      email: data.email,
      fullName: data.fullName,
      roles: data.roles,
    });
    return data;
  };

  const register = async (data) => {
    await authApi.register(data);
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      localStorage.clear();
      setUser(null);
    }
  };

  const hasRole = (role) => {
    if (!user?.roles) return false;
    const withPrefix = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    const withoutPrefix = role.replace('ROLE_', '');
    return user.roles.some((r) => r === withPrefix || r === withoutPrefix || r === role);
  };

  const getDashboardPath = () => {
    if (hasRole('CEO')) return '/dashboard/ceo';
    if (hasRole('ADMIN')) return '/dashboard/admin';
    if (hasRole('VOLUNTEER')) return '/dashboard/volunteer';
    if (hasRole('MEMBER')) return '/dashboard/member';
    return '/dashboard/user';
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, hasRole, getDashboardPath, refreshUser: loadUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
