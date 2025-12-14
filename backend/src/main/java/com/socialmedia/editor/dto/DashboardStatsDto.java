package com.socialmedia.editor.dto;

import com.socialmedia.editor.model.SocialMediaAccount;

import java.util.List;

public class DashboardStatsDto {
    private List<SocialMediaAccountDto> connectedAccounts;
    private PostStatisticsDto postStatistics;
    private Long totalFollowers;
    private Long totalFollowing;

    public DashboardStatsDto() {}

    public DashboardStatsDto(List<SocialMediaAccountDto> connectedAccounts,
                           PostStatisticsDto postStatistics,
                           Long totalFollowers,
                           Long totalFollowing) {
        this.connectedAccounts = connectedAccounts;
        this.postStatistics = postStatistics;
        this.totalFollowers = totalFollowers;
        this.totalFollowing = totalFollowing;
    }

    public List<SocialMediaAccountDto> getConnectedAccounts() {
        return connectedAccounts;
    }

    public void setConnectedAccounts(List<SocialMediaAccountDto> connectedAccounts) {
        this.connectedAccounts = connectedAccounts;
    }

    public PostStatisticsDto getPostStatistics() {
        return postStatistics;
    }

    public void setPostStatistics(PostStatisticsDto postStatistics) {
        this.postStatistics = postStatistics;
    }

    public Long getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(Long totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public Long getTotalFollowing() {
        return totalFollowing;
    }

    public void setTotalFollowing(Long totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public static class SocialMediaAccountDto {
        private Long id;
        private SocialMediaAccount.Platform platform;
        private String accountName;
        private String accountUsername;
        private String profileImageUrl;
        private Long followersCount;
        private Long followingCount;
        private Long postsCount;
        private Boolean isActive;

        public SocialMediaAccountDto() {}

        public SocialMediaAccountDto(SocialMediaAccount account) {
            this.id = account.getId();
            this.platform = account.getPlatform();
            this.accountName = account.getAccountName();
            this.accountUsername = account.getAccountUsername();
            this.profileImageUrl = account.getProfileImageUrl();
            this.followersCount = account.getFollowersCount();
            this.followingCount = account.getFollowingCount();
            this.postsCount = account.getPostsCount();
            this.isActive = account.getIsActive();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public SocialMediaAccount.Platform getPlatform() {
            return platform;
        }

        public void setPlatform(SocialMediaAccount.Platform platform) {
            this.platform = platform;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountUsername() {
            return accountUsername;
        }

        public void setAccountUsername(String accountUsername) {
            this.accountUsername = accountUsername;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public Long getFollowersCount() {
            return followersCount;
        }

        public void setFollowersCount(Long followersCount) {
            this.followersCount = followersCount;
        }

        public Long getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(Long followingCount) {
            this.followingCount = followingCount;
        }

        public Long getPostsCount() {
            return postsCount;
        }

        public void setPostsCount(Long postsCount) {
            this.postsCount = postsCount;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }

    public static class PostStatisticsDto {
        private Long totalPosts;
        private Long draftPosts;
        private Long publishedPosts;
        private Long scheduledPosts;
        private Long totalEngagement;
        private Long totalLikes;
        private Long totalShares;
        private Long totalComments;

        public PostStatisticsDto() {}

        public PostStatisticsDto(Long totalPosts, Long draftPosts, Long publishedPosts,
                               Long scheduledPosts, Long totalEngagement, Long totalLikes,
                               Long totalShares, Long totalComments) {
            this.totalPosts = totalPosts;
            this.draftPosts = draftPosts;
            this.publishedPosts = publishedPosts;
            this.scheduledPosts = scheduledPosts;
            this.totalEngagement = totalEngagement;
            this.totalLikes = totalLikes;
            this.totalShares = totalShares;
            this.totalComments = totalComments;
        }

        public Long getTotalPosts() {
            return totalPosts;
        }

        public void setTotalPosts(Long totalPosts) {
            this.totalPosts = totalPosts;
        }

        public Long getDraftPosts() {
            return draftPosts;
        }

        public void setDraftPosts(Long draftPosts) {
            this.draftPosts = draftPosts;
        }

        public Long getPublishedPosts() {
            return publishedPosts;
        }

        public void setPublishedPosts(Long publishedPosts) {
            this.publishedPosts = publishedPosts;
        }

        public Long getScheduledPosts() {
            return scheduledPosts;
        }

        public void setScheduledPosts(Long scheduledPosts) {
            this.scheduledPosts = scheduledPosts;
        }

        public Long getTotalEngagement() {
            return totalEngagement;
        }

        public void setTotalEngagement(Long totalEngagement) {
            this.totalEngagement = totalEngagement;
        }

        public Long getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(Long totalLikes) {
            this.totalLikes = totalLikes;
        }

        public Long getTotalShares() {
            return totalShares;
        }

        public void setTotalShares(Long totalShares) {
            this.totalShares = totalShares;
        }

        public Long getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(Long totalComments) {
            this.totalComments = totalComments;
        }
    }
}