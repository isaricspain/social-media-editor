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
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField
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
  Publish,
  AutoAwesome
} from '@mui/icons-material';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid } from 'recharts';
import dashboardService from '../services/dashboardService';
import postService from '../services/postService';
import PostStatistics from './PostStatistics';
import SocialAccountCard from './SocialAccountCard';
import AIContentGenerator from './AIContentGenerator';
import AIToolbar from './AIToolbar';
import linkedinService from '../services/linkedinService';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

const Dashboard = ({ user, onLogout }) => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAIGenerator, setShowAIGenerator] = useState(false);
  const [quickPostDialogOpen, setQuickPostDialogOpen] = useState(false);
  const [quickPostTitle, setQuickPostTitle] = useState('');
  const [quickPostContent, setQuickPostContent] = useState('');
  const [quickPostReferences, setQuickPostReferences] = useState('');
  const [savingPost, setSavingPost] = useState(false);
  const [inlineInfo, setInlineInfo] = useState(null);
  const [inlineError, setInlineError] = useState(null);

  const [posts, setPosts] = useState([]);
  const [postsLoading, setPostsLoading] = useState(false);
  const [postsError, setPostsError] = useState('');
  const [selectedPostId, setSelectedPostId] = useState(null);

  useEffect(() => {
    loadDashboardData();
    loadPosts();

    // Handle LinkedIn OAuth callback query params
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state');

    // If LinkedIn redirected back to the frontend with an authorization code, exchange it with backend while sending JWT
    if (code) {
      (async () => {
        try {
          await linkedinService.exchangeCode(code, state);
          // After successful exchange, navigate to dashboard with success flag
          window.location.replace('/dashboard?linkedin_success=true');
        } catch (err) {
          const backendErr = err?.response?.data;
          const reason = typeof backendErr === 'string' ? backendErr : 'callback_error';
          window.location.replace(`/dashboard?linkedin_error=${encodeURIComponent(reason)}`);
        }
      })();
      return; // prevent processing the rest of the params on this route
    }
    const liSuccess = params.get('linkedin_success');
    const liError = params.get('linkedin_error');
    if (liSuccess === 'true') {
      setInlineInfo('LinkedIn account connected successfully.');
      // reload data to include the new account
      loadDashboardData();
    } else if (liError) {
      const friendly = {
        already_connected: 'This LinkedIn account is already connected.',
        no_code: 'No authorization code returned from LinkedIn.',
        token_exchange_failed: 'Failed to exchange authorization code for token.',
        profile_fetch_failed: 'Failed to fetch LinkedIn profile.',
        callback_error: 'LinkedIn OAuth callback reported an error.',
        unexpected_error: 'An unexpected error occurred during LinkedIn connection.'
      }[liError] || 'LinkedIn connection failed.';
      setInlineError(friendly);
    }
    if (liSuccess || liError) {
      // Clean URL params
      const url = new URL(window.location.href);
      url.searchParams.delete('linkedin_success');
      url.searchParams.delete('linkedin_error');
      window.history.replaceState({}, document.title, url.toString());
    }
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

  const loadPosts = async () => {
    try {
      setPostsLoading(true);
      setPostsError('');
      const response = await postService.getPosts();
      // postService returns axios response directly; normalize
      const data = response.data ?? response?.data;
      setPosts(data || []);
    } catch (err) {
      setPostsError('Failed to load posts');
    } finally {
      setPostsLoading(false);
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

  const handleContentGenerated = (content) => {
    setQuickPostContent(content);
  };

  const handleContentUpdated = (content) => {
    setQuickPostContent(content);
  };

  const handleCreatePost = () => {
    setQuickPostDialogOpen(true);
    setSelectedPostId(null);
    setQuickPostTitle('');
    setQuickPostContent('');
    setQuickPostReferences('');
  };

  // Ensure the redirect_uri parameter (if present) is properly URL-encoded
  const sanitizeLinkedInAuthUrl = (urlStr) => {
    try {
      const u = new URL(urlStr);
      const ru = u.searchParams.get('redirect_uri');
      if (ru) {
        let decoded = ru;
        try {
          // If already encoded this will decode once; if not, it will be unchanged on exception
          decoded = decodeURIComponent(ru);
        } catch (_) {
          // ignore decoding errors; treat as already decoded
        }
        const reencoded = encodeURIComponent(decoded);
        // Only set if different to avoid unnecessary mutations
        if (reencoded !== ru) {
          u.searchParams.set('redirect_uri', reencoded);
        }
        return u.toString();
      }
      return urlStr;
    } catch (_) {
      return urlStr;
    }
  };

  const handleConnectLinkedIn = async () => {
    try {
      setInlineInfo(null);
      setInlineError(null);
      const resp = await linkedinService.getAuthorizationUrl();
      const authUrl = resp?.data?.authorizationUrl || resp?.data?.authorization_url;
      if (authUrl) {
        //const safeUrl = sanitizeLinkedInAuthUrl(authUrl);
        window.location.href = authUrl;
      } else {
        setInlineError('Failed to initiate LinkedIn connection.');
      }
    } catch (e) {
      setInlineError('Failed to initiate LinkedIn connection.');
    }
  };
  
  const handleSaveQuickPost = async () => {
    if (!quickPostContent.trim()) return;
    try {
      setSavingPost(true);
      const payload = {
        title: quickPostTitle?.trim() || null,
        content: quickPostContent.trim(),
        references: quickPostReferences?.trim() || null
      };
      if (selectedPostId) {
        const response = await postService.updatePost(selectedPostId, payload);
        const updated = response.data ?? response?.data;
        if (updated) {
          // Refresh list to reflect changes
          await loadPosts();
        }
      } else {
        const response = await postService.createPost(payload);
        // postService returns axios promise; response.data holds created post
        const created = response.data ?? response?.data;
        if (created) {
          setPosts((prev) => [created, ...prev]);
          setSelectedPostId(created.id);
        }
      }
      setQuickPostDialogOpen(false);
      setSelectedPostId(null);
      setQuickPostTitle('');
      setQuickPostContent('');
      setQuickPostReferences('');
    } catch (err) {
      setError('Failed to save post');
    } finally {
      setSavingPost(false);
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
          <Tooltip title="AI Content Generator">
            <IconButton
              onClick={() => setShowAIGenerator(!showAIGenerator)}
              sx={{ mr: 1, color: showAIGenerator ? 'primary.main' : 'default' }}
            >
              <AutoAwesome />
            </IconButton>
          </Tooltip>
          <Tooltip title="Quick Create Post">
            <IconButton onClick={handleCreatePost} sx={{ mr: 1 }}>
              <Add />
            </IconButton>
          </Tooltip>
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

      {!!inlineInfo && (
        <Box mb={2}>
          <Alert severity="success">{inlineInfo}</Alert>
        </Box>
      )}
      {!!inlineError && (
        <Box mb={2}>
          <Alert severity="error">{inlineError}</Alert>
        </Box>
      )}

      <Typography variant="h6" color="text.secondary" mb={4}>
        Welcome back, {user?.username}!
      </Typography>

      {/* AI Content Generator Panel */}
      {showAIGenerator && (
        <Card sx={{ mb: 3, border: '2px solid #e3f2fd' }}>
          <CardContent>
            <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <AutoAwesome color="primary" />
              AI Content Generator
            </Typography>
            <AIContentGenerator onContentGenerated={handleContentGenerated} />
          </CardContent>
        </Card>
      )}

      {/* Quick Post Creation Dialog */}
      <Dialog
        open={quickPostDialogOpen}
        onClose={() => setQuickPostDialogOpen(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>{selectedPostId ? 'Edit Post' : 'Create New Post'}</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Title (optional)"
            variant="outlined"
            value={quickPostTitle}
            onChange={(e) => setQuickPostTitle(e.target.value)}
            sx={{ mt: 1 }}
          />
          <TextField
            autoFocus
            multiline
            rows={4}
            fullWidth
            label="Post Content"
            variant="outlined"
            value={quickPostContent}
            onChange={(e) => setQuickPostContent(e.target.value)}
            sx={{ mt: 2, mb: 2 }}
          />
          <TextField
            fullWidth
            label="References (optional)"
            helperText="You can paste links or notes; saved as a text field"
            variant="outlined"
            value={quickPostReferences}
            onChange={(e) => setQuickPostReferences(e.target.value)}
            sx={{ mb: 1 }}
          />
          {quickPostContent && (
            <AIToolbar
              content={quickPostContent}
              onContentUpdated={handleContentUpdated}
              tone="neutral"
              platform="general"
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { setQuickPostDialogOpen(false); setSelectedPostId(null); }}>Cancel</Button>
          <Button
            onClick={handleSaveQuickPost}
            variant="contained"
            disabled={!quickPostContent.trim() || savingPost}
          >
            {savingPost ? (selectedPostId ? 'Updating...' : 'Saving...') : (selectedPostId ? 'Update Post' : 'Save Post')}
          </Button>
        </DialogActions>
      </Dialog>

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
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="h6">
                  Connected Social Media Accounts
                </Typography>
                <Button size="small" variant="outlined" startIcon={<LinkedIn />} onClick={handleConnectLinkedIn}>
                  Connect LinkedIn
                </Button>
              </Box>
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
                  <Button variant="contained" startIcon={<LinkedIn />} onClick={handleConnectLinkedIn}>
                    Connect LinkedIn
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

        {/* Posts List */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6">Your Posts</Typography>
                <Button size="small" onClick={loadPosts} startIcon={<Refresh />}>Refresh</Button>
              </Box>
              {postsLoading ? (
                <Box display="flex" justifyContent="center" py={3}><CircularProgress size={24} /></Box>
              ) : postsError ? (
                <Alert severity="error">{postsError}</Alert>
              ) : posts.length === 0 ? (
                <Typography variant="body2" color="text.secondary">No posts yet. Use the + button to create one.</Typography>
              ) : (
                <Grid container spacing={2}>
                  {posts.map((p) => (
                    <Grid item xs={12} md={6} key={p.id}>
                      <Card variant="outlined" sx={{ cursor: 'pointer' }} onClick={async () => {
                        try {
                          setSelectedPostId(p.id);
                          const resp = await postService.getPost(p.id);
                          const data = resp.data ?? resp?.data;
                          if (data) {
                            setQuickPostTitle(data.title || '');
                            setQuickPostContent(data.content || '');
                            setQuickPostReferences(data.references || '');
                            setQuickPostDialogOpen(true);
                          }
                        } catch (e) {
                          setError('Failed to load post');
                        }
                      }}>
                        <CardContent>
                          <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                            {p.title || 'Untitled'}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" noWrap>
                            {p.content}
                          </Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12}>
          <PostStatistics postStatistics={dashboardData?.postStatistics} />
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;