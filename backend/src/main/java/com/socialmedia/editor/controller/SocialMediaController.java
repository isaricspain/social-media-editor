package com.socialmedia.editor.controller;

import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.UserRepository;
import com.socialmedia.editor.service.SocialMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-media")
@CrossOrigin(origins = "http://localhost:3000")
public class SocialMediaController {

    @Autowired
    private SocialMediaService socialMediaService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/accounts")
    public ResponseEntity<List<SocialMediaAccount>> getAccounts(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SocialMediaAccount> accounts = socialMediaService.getActiveAccountsByUser(user);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connectAccount(@RequestBody ConnectAccountRequest request,
                                          Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SocialMediaAccount account = socialMediaService.addAccount(
                    user,
                    request.getPlatform(),
                    request.getAccountName(),
                    request.getAccountUsername(),
                    request.getAccessToken(),
                    request.getRefreshToken()
            );

            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to connect account");
        }
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<String> disconnectAccount(@PathVariable Long accountId,
                                                  Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            socialMediaService.disconnectAccount(accountId, user);
            return ResponseEntity.ok("Account disconnected successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to disconnect account");
        }
    }

    @PostMapping("/accounts/{accountId}/refresh")
    public ResponseEntity<String> refreshAccountStats(@PathVariable Long accountId,
                                                     Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            socialMediaService.refreshAccountStats(accountId);
            return ResponseEntity.ok("Account stats refreshed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to refresh account stats");
        }
    }

    public static class ConnectAccountRequest {
        private SocialMediaAccount.Platform platform;
        private String accountName;
        private String accountUsername;
        private String accessToken;
        private String refreshToken;

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

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}