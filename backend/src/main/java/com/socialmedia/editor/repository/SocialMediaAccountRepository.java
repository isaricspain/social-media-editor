package com.socialmedia.editor.repository;

import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialMediaAccountRepository extends JpaRepository<SocialMediaAccount, Long> {

    List<SocialMediaAccount> findByUserAndIsActiveTrue(User user);

    List<SocialMediaAccount> findByUser(User user);

    @Query("SELECT SUM(s.followersCount) FROM SocialMediaAccount s WHERE s.user = :user AND s.isActive = true")
    Long getTotalFollowersByUser(User user);

    @Query("SELECT SUM(s.followingCount) FROM SocialMediaAccount s WHERE s.user = :user AND s.isActive = true")
    Long getTotalFollowingByUser(User user);

    @Query("SELECT COUNT(s) FROM SocialMediaAccount s WHERE s.user = :user AND s.isActive = true")
    Long getActiveAccountCountByUser(User user);

    boolean existsByUserAndPlatformAndAccountUsername(User user, SocialMediaAccount.Platform platform, String accountUsername);
}