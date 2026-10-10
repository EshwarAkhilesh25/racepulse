import { apiClient } from '../config/api.js';

export const fetchLiveTiming = async () => {
  return apiClient.get('/timing/live');
};

export const fetchHealth = async () => {
  return apiClient.get('/health');
};

