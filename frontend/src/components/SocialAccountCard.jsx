import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Avatar,
  Chip,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  Facebook,
  Twitter,
  Instagram,
  LinkedIn,
  YouTube,
  Refresh,
  Delete
} from '@mui/icons-material';

const platformIcons = {
  TWITTER: <Twitter />,
  FACEBOOK: <Facebook />,
  INSTAGRAM: <Instagram />,
  LINKEDIN: <LinkedIn />,
  YOUTUBE: <YouTube />,
  TIKTOK: <Typography variant="body2">TT</Typography>
};

const platformColors = {
  TWITTER: '#1DA1F2',
  FACEBOOK: '#4267B2',
  INSTAGRAM: '#E4405F',
  LINKEDIN: '#0077B5',
  YOUTUBE: '#FF0000',
  TIKTOK: '#000000'
};

const SocialAccountCard = ({ account, onRefresh, onDisconnect }) => {
  const formatNumber = (num) => {
    if (!num) return '0';
    if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
    if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
    return num.toString();
  };

  const handleRefresh = () => {
    if (onRefresh) {
      onRefresh(account.id);
    }
  };

  const handleDisconnect = () => {
    if (onDisconnect) {
      onDisconnect(account.id);
    }
  };

  return (
    <Card sx={{ height: '100%', position: 'relative' }}>
      <CardContent>
        <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
          <Box display="flex" alignItems="center">
            <Avatar
              sx={{
                bgcolor: platformColors[account.platform] || '#gray',
                mr: 2,
                width: 40,
                height: 40
              }}
            >
              {platformIcons[account.platform]}
            </Avatar>
            <Box>
              <Typography variant="h6" component="div" noWrap>
                {account.accountName}
              </Typography>
              <Typography variant="body2" color="text.secondary" noWrap>
                @{account.accountUsername || account.accountName}
              </Typography>
            </Box>
          </Box>
          <Box>
            {onRefresh && (
              <Tooltip title="Refresh Stats">
                <IconButton size="small" onClick={handleRefresh}>
                  <Refresh />
                </IconButton>
              </Tooltip>
            )}
            {onDisconnect && (
              <Tooltip title="Disconnect Account">
                <IconButton size="small" onClick={handleDisconnect} color="error">
                  <Delete />
                </IconButton>
              </Tooltip>
            )}
          </Box>
        </Box>

        <Box display="flex" justifyContent="space-between" mb={2}>
          <Box textAlign="center">
            <Typography variant="h6" color="primary">
              {formatNumber(account.followersCount)}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Followers
            </Typography>
          </Box>
          <Box textAlign="center">
            <Typography variant="h6" color="primary">
              {formatNumber(account.followingCount)}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Following
            </Typography>
          </Box>
          <Box textAlign="center">
            <Typography variant="h6" color="primary">
              {formatNumber(account.postsCount)}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Posts
            </Typography>
          </Box>
        </Box>

        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Chip
            label={account.platform}
            size="small"
            sx={{
              bgcolor: platformColors[account.platform] || '#gray',
              color: 'white',
              fontWeight: 'bold'
            }}
          />
          <Chip
            label={account.isActive ? 'Active' : 'Inactive'}
            size="small"
            color={account.isActive ? 'success' : 'default'}
            variant={account.isActive ? 'filled' : 'outlined'}
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default SocialAccountCard;