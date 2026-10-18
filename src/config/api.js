import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

export const apiUrl = (path = '') => {
  const base = (import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1').replace(/\/api\/v1\/?$/, '');
  const cleanPath = path.startsWith('/') ? path : `/${path}`;
  return `${base}${cleanPath}`;
};


export const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 12000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.warn('[RacePulse API Warning]', error.message);
    return Promise.reject(error);
  }
);

export const endpoints = {
  dashboard: '/dashboard',
  races: '/races',
  raceById: (id) => `/races/${id}`,
  drivers: '/drivers',
  driverById: (id) => `/drivers/${id}`,
  driverStats: (id) => `/drivers/${id}/statistics`,
  constructors: '/constructors',
  constructorById: (id) => `/constructors/${id}`,
  constructorStats: (id) => `/constructors/${id}/statistics`,
  analytics: '/analytics',
  predictions: '/predictions',
  standings: '/standings',
  liveSession: '/live/session',
  health: '/health',
};
