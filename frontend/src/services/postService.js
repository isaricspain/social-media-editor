import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const postService = {
  getPosts: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/posts`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  getDraftPosts: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/posts/drafts`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  getPublishedPosts: () => {
    const token = localStorage.getItem('token');
    return axios.get(`${API_BASE_URL}/posts/published`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  createPost: (postData) => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/posts`, postData, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  updatePost: (postId, postData) => {
    const token = localStorage.getItem('token');
    return axios.put(`${API_BASE_URL}/posts/${postId}`, postData, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  schedulePost: (postId, scheduledTime) => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/posts/${postId}/schedule`, { scheduledTime }, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  publishPost: (postId) => {
    const token = localStorage.getItem('token');
    return axios.post(`${API_BASE_URL}/posts/${postId}/publish`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  deletePost: (postId) => {
    const token = localStorage.getItem('token');
    return axios.delete(`${API_BASE_URL}/posts/${postId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default postService;