import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const dashboardService = {
  getDashboardStats: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/dashboard/stats`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  refreshAccountStats: () => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/dashboard/refresh`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default dashboardService;