package com.socialmedia.editor.service;

import com.socialmedia.editor.model.PlatformSettings;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.PlatformSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformSettingsService {

    @Autowired
    private PlatformSettingsRepository platformSettingsRepository;

    public List<PlatformSettings> getUserPlatformSettings(User user) {
        return platformSettingsRepository.findByUserAndIsActiveTrue(user);
    }

    public Optional<PlatformSettings> getPlatformSetting(User user, SocialMediaAccount.Platform platform) {
        return platformSettingsRepository.findByUserAndPlatform(user, platform);
    }

    public PlatformSettings createOrUpdatePlatformSetting(User user, SocialMediaAccount.Platform platform, String defaultPrompt) {
        Optional<PlatformSettings> existingOpt = platformSettingsRepository.findByUserAndPlatform(user, platform);

        if (existingOpt.isPresent()) {
            PlatformSettings existing = existingOpt.get();
            existing.setDefaultPrompt(defaultPrompt);
            existing.setIsActive(true);
            return platformSettingsRepository.save(existing);
        } else {
            PlatformSettings newSetting = new PlatformSettings(user, platform, defaultPrompt);
            return platformSettingsRepository.save(newSetting);
        }
    }

    public void deactivatePlatformSetting(User user, SocialMediaAccount.Platform platform) {
        Optional<PlatformSettings> settingOpt = platformSettingsRepository.findByUserAndPlatform(user, platform);
        if (settingOpt.isPresent()) {
            PlatformSettings setting = settingOpt.get();
            setting.setIsActive(false);
            platformSettingsRepository.save(setting);
        }
    }

    public void deletePlatformSetting(User user, SocialMediaAccount.Platform platform) {
        platformSettingsRepository.deleteByUserAndPlatform(user, platform);
    }

    public PlatformSettings updateDefaultPrompt(User user, SocialMediaAccount.Platform platform, String newPrompt) {
        Optional<PlatformSettings> settingOpt = platformSettingsRepository.findByUserAndPlatform(user, platform);
        if (settingOpt.isPresent()) {
            PlatformSettings setting = settingOpt.get();
            setting.setDefaultPrompt(newPrompt);
            return platformSettingsRepository.save(setting);
        }
        throw new RuntimeException("Platform setting not found");
    }
}