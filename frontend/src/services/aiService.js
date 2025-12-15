import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/ai';

const getAuthToken = () => {
  return localStorage.getItem('token');
};

const createAuthHeaders = () => {
  const token = getAuthToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
};

const aiService = {
  generateContent: async (request) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/generate`,
        request,
        { headers: createAuthHeaders() }
      );
      return response.data;
    } catch (error) {
      console.error('Error generating content:', error);
      throw error.response?.data || error.message;
    }
  },

  improveContent: async (request) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/improve`,
        request,
        { headers: createAuthHeaders() }
      );
      return response.data;
    } catch (error) {
      console.error('Error improving content:', error);
      throw error.response?.data || error.message;
    }
  },

  generateHashtags: async (request) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/hashtags`,
        request,
        { headers: createAuthHeaders() }
      );
      return response.data;
    } catch (error) {
      console.error('Error generating hashtags:', error);
      throw error.response?.data || error.message;
    }
  },

  generateVariations: async (request) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/variations`,
        request,
        { headers: createAuthHeaders() }
      );
      return response.data;
    } catch (error) {
      console.error('Error generating variations:', error);
      throw error.response?.data || error.message;
    }
  },

  checkStatus: async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/status`,
        { headers: createAuthHeaders() }
      );
      return response.data;
    } catch (error) {
      console.error('Error checking AI service status:', error);
      throw error.response?.data || error.message;
    }
  }
};

export default aiService;