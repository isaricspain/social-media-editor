package com.socialmedia.editor.controller;

import com.socialmedia.editor.dto.DashboardStatsDto;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.service.AuthService;
import com.socialmedia.editor.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    private final AuthService authService;

    public DashboardController(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);
            DashboardStatsDto stats = dashboardService.getDashboardStats(user);
            return ok(stats);
        } catch (Exception e) {
            LOG.error("Failed to get dashboard stats", e);
            return badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccountStats(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);
            dashboardService.refreshAllAccountStats(user);
            return ok("Account stats refreshed successfully");
        } catch (Exception e) {
            return badRequest().body("Failed to refresh account stats");
        }
    }
}