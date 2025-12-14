package com.socialmedia.editor.service;

import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.SocialMediaAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SocialMediaService {

    @Autowired
    private SocialMediaAccountRepository socialMediaAccountRepository;

    public List<SocialMediaAccount> getActiveAccountsByUser(User user) {
        return socialMediaAccountRepository.findByUserAndIsActiveTrue(user);
    }

    public List<SocialMediaAccount> getAllAccountsByUser(User user) {
        return socialMediaAccountRepository.findByUser(user);
    }

    public SocialMediaAccount addAccount(User user, SocialMediaAccount.Platform platform,
                                       String accountName, String accountUsername,
                                       String accessToken, String refreshToken) {
        if (socialMediaAccountRepository.existsByUserAndPlatformAndAccountUsername(user, platform, accountUsername)) {
            throw new RuntimeException("Account already connected for this platform");
        }

        SocialMediaAccount account = new SocialMediaAccount(user, platform, accountName);
        account.setAccountUsername(accountUsername);
        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);
        account.setFollowersCount(0L);
        account.setFollowingCount(0L);
        account.setPostsCount(0L);

        return socialMediaAccountRepository.save(account);
    }

    public SocialMediaAccount updateAccountStats(Long accountId, Long followersCount,
                                               Long followingCount, Long postsCount) {
        Optional<SocialMediaAccount> accountOpt = socialMediaAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            SocialMediaAccount account = accountOpt.get();
            account.setFollowersCount(followersCount);
            account.setFollowingCount(followingCount);
            account.setPostsCount(postsCount);
            return socialMediaAccountRepository.save(account);
        }
        throw new RuntimeException("Account not found");
    }

    public void disconnectAccount(Long accountId, User user) {
        Optional<SocialMediaAccount> accountOpt = socialMediaAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            SocialMediaAccount account = accountOpt.get();
            if (!account.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to disconnect this account");
            }
            account.setIsActive(false);
            socialMediaAccountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public Long getTotalFollowersByUser(User user) {
        Long total = socialMediaAccountRepository.getTotalFollowersByUser(user);
        return total != null ? total : 0L;
    }

    public Long getTotalFollowingByUser(User user) {
        Long total = socialMediaAccountRepository.getTotalFollowingByUser(user);
        return total != null ? total : 0L;
    }

    public Long getActiveAccountCountByUser(User user) {
        return socialMediaAccountRepository.getActiveAccountCountByUser(user);
    }

    public void refreshAccountStats(Long accountId) {
        Optional<SocialMediaAccount> accountOpt = socialMediaAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            SocialMediaAccount account = accountOpt.get();

            switch (account.getPlatform()) {
                case TWITTER:
                    refreshTwitterStats(account);
                    break;
                case FACEBOOK:
                    refreshFacebookStats(account);
                    break;
                case INSTAGRAM:
                    refreshInstagramStats(account);
                    break;
                case LINKEDIN:
                    refreshLinkedInStats(account);
                    break;
                default:
                    break;
            }
        }
    }

    private void refreshTwitterStats(SocialMediaAccount account) {
        account.setFollowersCount(account.getFollowersCount() != null ? account.getFollowersCount() : 0L);
        account.setFollowingCount(account.getFollowingCount() != null ? account.getFollowingCount() : 0L);
        account.setPostsCount(account.getPostsCount() != null ? account.getPostsCount() : 0L);
        socialMediaAccountRepository.save(account);
    }

    private void refreshFacebookStats(SocialMediaAccount account) {
        account.setFollowersCount(account.getFollowersCount() != null ? account.getFollowersCount() : 0L);
        account.setFollowingCount(account.getFollowingCount() != null ? account.getFollowingCount() : 0L);
        account.setPostsCount(account.getPostsCount() != null ? account.getPostsCount() : 0L);
        socialMediaAccountRepository.save(account);
    }

    private void refreshInstagramStats(SocialMediaAccount account) {
        account.setFollowersCount(account.getFollowersCount() != null ? account.getFollowersCount() : 0L);
        account.setFollowingCount(account.getFollowingCount() != null ? account.getFollowingCount() : 0L);
        account.setPostsCount(account.getPostsCount() != null ? account.getPostsCount() : 0L);
        socialMediaAccountRepository.save(account);
    }

    private void refreshLinkedInStats(SocialMediaAccount account) {
        account.setFollowersCount(account.getFollowersCount() != null ? account.getFollowersCount() : 0L);
        account.setFollowingCount(account.getFollowingCount() != null ? account.getFollowingCount() : 0L);
        account.setPostsCount(account.getPostsCount() != null ? account.getPostsCount() : 0L);
        socialMediaAccountRepository.save(account);
    }
}