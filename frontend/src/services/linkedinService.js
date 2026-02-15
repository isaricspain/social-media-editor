import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const linkedinService = {
  // Initiates LinkedIn OAuth by requesting the authorization URL from backend
  getAuthorizationUrl: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/oauth/linkedin/authorize`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Exchanges authorization code (received on frontend) with backend while sending JWT
  exchangeCode: (code, state) => {
    const token = localStorage.getItem('token');
    return axios.post(
      `${API_BASE_URL}/oauth/linkedin/callback/frontend`,
      { code, state },
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }
};

export default linkedinService;
