package com.socialmedia.editor.repository;

import com.socialmedia.editor.model.PlatformSettings;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformSettingsRepository extends JpaRepository<PlatformSettings, Long> {

    List<PlatformSettings> findByUserAndIsActiveTrue(User user);

    Optional<PlatformSettings> findByUserAndPlatform(User user, SocialMediaAccount.Platform platform);

    List<PlatformSettings> findByUser(User user);

    void deleteByUserAndPlatform(User user, SocialMediaAccount.Platform platform);
}