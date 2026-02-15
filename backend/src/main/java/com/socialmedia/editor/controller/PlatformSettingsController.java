package com.socialmedia.editor.controller;

import com.socialmedia.editor.model.PlatformSettings;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.service.AuthService;
import com.socialmedia.editor.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/platform-settings")
@CrossOrigin(origins = "http://localhost:3000")
public class PlatformSettingsController {

    private final PlatformSettingsService platformSettingsService;

    private final AuthService authService;

    public PlatformSettingsController(PlatformSettingsService platformSettingsService, AuthService authService) {
        this.platformSettingsService = platformSettingsService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<PlatformSettings>> getPlatformSettings(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            List<PlatformSettings> settings = platformSettingsService.getUserPlatformSettings(user);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{platform}")
    public ResponseEntity<PlatformSettings> getPlatformSetting(@PathVariable SocialMediaAccount.Platform platform,
                                                              Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Optional<PlatformSettings> setting = platformSettingsService.getPlatformSetting(user, platform);
            if (setting.isPresent()) {
                return ResponseEntity.ok(setting.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{platform}")
    public ResponseEntity<?> createOrUpdatePlatformSetting(@PathVariable SocialMediaAccount.Platform platform,
                                                          @RequestBody PlatformSettingRequest request,
                                                          Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            PlatformSettings setting = platformSettingsService.createOrUpdatePlatformSetting(
                    user, platform, request.getDefaultPrompt());
            return ResponseEntity.ok(setting);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save platform setting");
        }
    }

    @PutMapping("/{platform}")
    public ResponseEntity<?> updateDefaultPrompt(@PathVariable SocialMediaAccount.Platform platform,
                                               @RequestBody PlatformSettingRequest request,
                                               Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            PlatformSettings setting = platformSettingsService.updateDefaultPrompt(
                    user, platform, request.getDefaultPrompt());
            return ResponseEntity.ok(setting);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update platform setting");
        }
    }

    @DeleteMapping("/{platform}")
    public ResponseEntity<String> deletePlatformSetting(@PathVariable SocialMediaAccount.Platform platform,
                                                       Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            platformSettingsService.deletePlatformSetting(user, platform);
            return ResponseEntity.ok("Platform setting deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete platform setting");
        }
    }

    public static class PlatformSettingRequest {
        private String defaultPrompt;

        public String getDefaultPrompt() {
            return defaultPrompt;
        }

        public void setDefaultPrompt(String defaultPrompt) {
            this.defaultPrompt = defaultPrompt;
        }
    }
}