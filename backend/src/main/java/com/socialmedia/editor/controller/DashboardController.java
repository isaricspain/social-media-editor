package com.socialmedia.editor.controller;

import com.socialmedia.editor.dto.DashboardStatsDto;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.UserRepository;
import com.socialmedia.editor.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(((User)authentication.getPrincipal()).getUsername())
                    .orElseThrow(() -> new IllegalAccessException("User %s not found".formatted(authentication.getName())));

            DashboardStatsDto stats = dashboardService.getDashboardStats(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            LOG.error("Failed to get dashboard stats", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccountStats(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            dashboardService.refreshAllAccountStats(user);
            return ResponseEntity.ok("Account stats refreshed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to refresh account stats");
        }
    }
}