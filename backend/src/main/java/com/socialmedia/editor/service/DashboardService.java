package com.socialmedia.editor.service;

import com.socialmedia.editor.dto.DashboardStatsDto;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private SocialMediaService socialMediaService;

    @Autowired
    private PostService postService;

    public DashboardStatsDto getDashboardStats(User user) {
        List<SocialMediaAccount> accounts = socialMediaService.getActiveAccountsByUser(user);
        List<DashboardStatsDto.SocialMediaAccountDto> accountDtos = accounts.stream()
                .map(DashboardStatsDto.SocialMediaAccountDto::new)
                .collect(Collectors.toList());

        DashboardStatsDto.PostStatisticsDto postStats = new DashboardStatsDto.PostStatisticsDto(
                postService.getTotalPostCountByUser(user),
                postService.getDraftPostCountByUser(user),
                postService.getPublishedPostCountByUser(user),
                postService.getScheduledPostCountByUser(user),
                postService.getTotalEngagementByUser(user),
                postService.getTotalLikesByUser(user),
                postService.getTotalSharesByUser(user),
                postService.getTotalCommentsByUser(user)
        );

        Long totalFollowers = socialMediaService.getTotalFollowersByUser(user);
        Long totalFollowing = socialMediaService.getTotalFollowingByUser(user);

        return new DashboardStatsDto(accountDtos, postStats, totalFollowers, totalFollowing);
    }

    public void refreshAllAccountStats(User user) {
        List<SocialMediaAccount> accounts = socialMediaService.getActiveAccountsByUser(user);
        for (SocialMediaAccount account : accounts) {
            try {
                socialMediaService.refreshAccountStats(account.getId());
            } catch (Exception e) {
            }
        }
    }
}