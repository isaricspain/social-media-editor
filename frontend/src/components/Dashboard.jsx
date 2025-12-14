import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Avatar,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  Facebook,
  Twitter,
  Instagram,
  LinkedIn,
  Refresh,
  Add,
  TrendingUp,
  Article,
  Schedule,
  Publish
} from '@mui/icons-material';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import dashboardService from '../services/dashboardService';
import PostStatistics from './PostStatistics';
import SocialAccountCard from './SocialAccountCard';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

const Dashboard = ({ user, onLogout }) => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const response = await dashboardService.getDashboardStats();
      setDashboardData(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    try {
      await dashboardService.refreshAccountStats();
      await loadDashboardData();
    } catch (err) {
      setError('Failed to refresh account stats');
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box p={3}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  const postChartData = dashboardData?.postStatistics ? [
    { name: 'Draft', value: dashboardData.postStatistics.draftPosts, color: '#FFBB28' },
    { name: 'Published', value: dashboardData.postStatistics.publishedPosts, color: '#00C49F' },
    { name: 'Scheduled', value: dashboardData.postStatistics.scheduledPosts, color: '#0088FE' }
  ] : [];

  const engagementData = dashboardData?.connectedAccounts?.map(account => ({
    name: account.platform,
    followers: account.followersCount || 0,
    following: account.followingCount || 0,
    posts: account.postsCount || 0
  })) || [];

  return (
    <Box sx={{ flexGrow: 1, p: 3, backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          Dashboard
        </Typography>
        <Box>
          <Tooltip title="Refresh Stats">
            <IconButton onClick={handleRefresh} sx={{ mr: 1 }}>
              <Refresh />
            </IconButton>
          </Tooltip>
          <Button variant="outlined" onClick={onLogout}>
            Logout
          </Button>
        </Box>
      </Box>

      <Typography variant="h6" color="text.secondary" mb={4}>
        Welcome back, {user?.username}!
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#e3f2fd' }}>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: '#1976d2', mr: 2 }}>
                  <TrendingUp />
                </Avatar>
                <Box>
                  <Typography variant="h4" fontWeight="bold">
                    {dashboardData?.totalFollowers || 0}
                  </Typography>
                  <Typography color="text.secondary">
                    Total Followers
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#e8f5e8' }}>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: '#4caf50', mr: 2 }}>
                  <Article />
                </Avatar>
                <Box>
                  <Typography variant="h4" fontWeight="bold">
                    {dashboardData?.postStatistics?.totalPosts || 0}
                  </Typography>
                  <Typography color="text.secondary">
                    Total Posts
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#fff3e0' }}>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: '#ff9800', mr: 2 }}>
                  <Schedule />
                </Avatar>
                <Box>
                  <Typography variant="h4" fontWeight="bold">
                    {dashboardData?.postStatistics?.draftPosts || 0}
                  </Typography>
                  <Typography color="text.secondary">
                    Draft Posts
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ backgroundColor: '#f3e5f5' }}>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Avatar sx={{ bgcolor: '#9c27b0', mr: 2 }}>
                  <Publish />
                </Avatar>
                <Box>
                  <Typography variant="h4" fontWeight="bold">
                    {dashboardData?.postStatistics?.publishedPosts || 0}
                  </Typography>
                  <Typography color="text.secondary">
                    Published Posts
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Connected Social Media Accounts
              </Typography>
              {dashboardData?.connectedAccounts?.length > 0 ? (
                <Grid container spacing={2}>
                  {dashboardData.connectedAccounts.map((account) => (
                    <Grid item xs={12} sm={6} key={account.id}>
                      <SocialAccountCard account={account} />
                    </Grid>
                  ))}
                </Grid>
              ) : (
                <Box textAlign="center" py={4}>
                  <Typography variant="body1" color="text.secondary" gutterBottom>
                    No social media accounts connected yet
                  </Typography>
                  <Button variant="contained" startIcon={<Add />}>
                    Connect Account
                  </Button>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Post Distribution
              </Typography>
              {postChartData.some(item => item.value > 0) ? (
                <Box height={200}>
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie
                        data={postChartData}
                        cx="50%"
                        cy="50%"
                        outerRadius={60}
                        fill="#8884d8"
                        dataKey="value"
                        label={({ name, value }) => `${name}: ${value}`}
                      >
                        {postChartData.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                    </PieChart>
                  </ResponsiveContainer>
                </Box>
              ) : (
                <Box textAlign="center" py={4}>
                  <Typography variant="body2" color="text.secondary">
                    No posts created yet
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {dashboardData?.connectedAccounts?.length > 0 && (
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Account Statistics
                </Typography>
                <Box height={300}>
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={engagementData}>
                      <CartesianGrid strokeDasharray="3 3" />
                      <XAxis dataKey="name" />
                      <YAxis />
                      <Bar dataKey="followers" fill="#8884d8" name="Followers" />
                      <Bar dataKey="posts" fill="#82ca9d" name="Posts" />
                    </BarChart>
                  </ResponsiveContainer>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        )}

        <Grid item xs={12}>
          <PostStatistics postStatistics={dashboardData?.postStatistics} />
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;