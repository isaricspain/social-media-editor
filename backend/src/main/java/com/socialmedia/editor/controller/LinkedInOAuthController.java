package com.socialmedia.editor.controller;

import com.socialmedia.editor.dto.LinkedInProfileDto;
import com.socialmedia.editor.dto.LinkedInStatsDto;
import com.socialmedia.editor.dto.LinkedInTokenResponseDto;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.UserRepository;
import com.socialmedia.editor.service.LinkedInConnectorService;
import com.socialmedia.editor.service.SocialMediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth/linkedin")
@CrossOrigin(origins = "http://localhost:3000")
public class LinkedInOAuthController {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInOAuthController.class);

    @Autowired
    private LinkedInConnectorService linkedInConnectorService;

    @Autowired
    private SocialMediaService socialMediaService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/authorize")
    public ResponseEntity<?> initiateOAuth() {
        try {
            String authorizationUrl = linkedInConnectorService.getAuthorizationUrl();
            Map<String, String> response = new HashMap<>();
            response.put("authorizationUrl", authorizationUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error initiating LinkedIn OAuth", e);
            return ResponseEntity.badRequest().body("Failed to initiate OAuth: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestParam("code") String authorizationCode,
                                          @RequestParam(value = "state", required = false) String state,
                                          @RequestParam(value = "error", required = false) String error,
                                          Authentication authentication) {
        try {
            if (error != null) {
                logger.error("LinkedIn OAuth error: {}", error);
                return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:3000/dashboard?linkedin_error=" + error))
                    .build();
            }

            if (authorizationCode == null) {
                return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:3000/dashboard?linkedin_error=no_code"))
                    .build();
            }

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LinkedInTokenResponseDto tokenResponse = linkedInConnectorService
                    .exchangeAuthorizationCode(authorizationCode)
                    .block();

            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:3000/dashboard?linkedin_error=token_exchange_failed"))
                    .build();
            }

            LinkedInProfileDto profile = linkedInConnectorService
                    .getUserProfile(tokenResponse.getAccessToken())
                    .block();

            String email = linkedInConnectorService
                    .getUserEmail(tokenResponse.getAccessToken())
                    .block();

            if (profile == null) {
                return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:3000/dashboard?linkedin_error=profile_fetch_failed"))
                    .build();
            }

            String accountName = profile.getFullName();
            String accountUsername = email != null ? email : profile.getId();

            SocialMediaAccount account = socialMediaService.addAccount(
                    user,
                    SocialMediaAccount.Platform.LINKEDIN,
                    accountName,
                    accountUsername,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken()
            );

            if (profile.getProfileImageUrl() != null) {
                account.setProfileImageUrl(profile.getProfileImageUrl());
            }

            LinkedInStatsDto stats = linkedInConnectorService
                    .getUserStats(tokenResponse.getAccessToken())
                    .block();

            if (stats != null) {
                account.setFollowersCount(stats.getFollowersCount());
                account.setFollowingCount(stats.getEffectiveConnectionsCount());
                account.setPostsCount(0L);
            }

            return ResponseEntity.status(302)
                .location(URI.create("http://localhost:3000/dashboard?linkedin_success=true"))
                .build();

        } catch (RuntimeException e) {
            logger.error("Error during LinkedIn OAuth callback", e);
            String errorMsg = e.getMessage().contains("already connected") ? "already_connected" : "callback_error";
            return ResponseEntity.status(302)
                .location(URI.create("http://localhost:3000/dashboard?linkedin_error=" + errorMsg))
                .build();
        } catch (Exception e) {
            logger.error("Unexpected error during LinkedIn OAuth callback", e);
            return ResponseEntity.status(302)
                .location(URI.create("http://localhost:3000/dashboard?linkedin_error=unexpected_error"))
                .build();
        }
    }

    @PostMapping("/refresh/{accountId}")
    public ResponseEntity<?> refreshToken(@PathVariable Long accountId,
                                        Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            socialMediaService.refreshAccountStats(accountId);

            return ResponseEntity.ok().body("LinkedIn account refreshed successfully");
        } catch (Exception e) {
            logger.error("Error refreshing LinkedIn token for account: {}", accountId, e);
            return ResponseEntity.badRequest().body("Failed to refresh LinkedIn account: " + e.getMessage());
        }
    }
}