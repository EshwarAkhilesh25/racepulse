// YouTube searches should go through the Spring Boot backend (/api/v1/news/videos)
// to keep API keys secure and not exposed to browser bundles.
import { apiClient } from '../config/api.js';

export const searchF1Videos = (query, maxResults = 10) => {
  return apiClient.get('/news/videos', {
    params: { q: query, max: maxResults },
  });
};

export default { searchF1Videos };

