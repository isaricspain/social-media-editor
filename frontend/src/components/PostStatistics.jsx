import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Grid,
  LinearProgress
} from '@mui/material';

const PostStatistics = ({ postStatistics }) => {
  if (!postStatistics) {
    return null;
  }

  const {
    totalPosts,
    draftPosts,
    publishedPosts,
    scheduledPosts,
    totalEngagement,
    totalLikes,
    totalShares,
    totalComments
  } = postStatistics;

  const calculatePercentage = (value, total) => {
    return total > 0 ? (value / total) * 100 : 0;
  };

  const formatNumber = (num) => {
    if (!num) return '0';
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
    if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
    return num.toString();
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Post Statistics
        </Typography>

        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" gutterBottom>
              Post Status Breakdown
            </Typography>

            <Box mb={2}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="body2">Draft Posts</Typography>
                <Typography variant="body2" fontWeight="bold">
                  {draftPosts || 0} ({calculatePercentage(draftPosts, totalPosts).toFixed(1)}%)
                </Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={calculatePercentage(draftPosts, totalPosts)}
                sx={{ height: 8, borderRadius: 4, bgcolor: '#f5f5f5' }}
                color="warning"
              />
            </Box>

            <Box mb={2}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="body2">Published Posts</Typography>
                <Typography variant="body2" fontWeight="bold">
                  {publishedPosts || 0} ({calculatePercentage(publishedPosts, totalPosts).toFixed(1)}%)
                </Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={calculatePercentage(publishedPosts, totalPosts)}
                sx={{ height: 8, borderRadius: 4, bgcolor: '#f5f5f5' }}
                color="success"
              />
            </Box>

            <Box mb={2}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                <Typography variant="body2">Scheduled Posts</Typography>
                <Typography variant="body2" fontWeight="bold">
                  {scheduledPosts || 0} ({calculatePercentage(scheduledPosts, totalPosts).toFixed(1)}%)
                </Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={calculatePercentage(scheduledPosts, totalPosts)}
                sx={{ height: 8, borderRadius: 4, bgcolor: '#f5f5f5' }}
                color="info"
              />
            </Box>
          </Grid>

          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" gutterBottom>
              Engagement Metrics
            </Typography>

            <Box mb={3}>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Card variant="outlined" sx={{ textAlign: 'center', p: 2 }}>
                    <Typography variant="h5" color="primary" fontWeight="bold">
                      {formatNumber(totalLikes)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Likes
                    </Typography>
                  </Card>
                </Grid>
                <Grid item xs={6}>
                  <Card variant="outlined" sx={{ textAlign: 'center', p: 2 }}>
                    <Typography variant="h5" color="secondary" fontWeight="bold">
                      {formatNumber(totalShares)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Shares
                    </Typography>
                  </Card>
                </Grid>
                <Grid item xs={6}>
                  <Card variant="outlined" sx={{ textAlign: 'center', p: 2 }}>
                    <Typography variant="h5" color="info.main" fontWeight="bold">
                      {formatNumber(totalComments)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Comments
                    </Typography>
                  </Card>
                </Grid>
                <Grid item xs={6}>
                  <Card variant="outlined" sx={{ textAlign: 'center', p: 2 }}>
                    <Typography variant="h5" color="success.main" fontWeight="bold">
                      {formatNumber(totalEngagement)}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Total Engagement
                    </Typography>
                  </Card>
                </Grid>
              </Grid>
            </Box>
          </Grid>
        </Grid>

        {totalPosts === 0 && (
          <Box textAlign="center" py={4}>
            <Typography variant="body1" color="text.secondary">
              No posts created yet. Start creating your first post!
            </Typography>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default PostStatistics;