import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const socialMediaService = {
  getAccounts: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/social-media/accounts`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  connectAccount: (accountData) => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/social-media/connect`, accountData, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  disconnectAccount: (accountId) => {
    const token = localStorage.getItem('token');
    return axios.delete(`${API_BASE_URL}/social-media/accounts/${accountId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  refreshAccountStats: (accountId) => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/social-media/accounts/${accountId}/refresh`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default socialMediaService;