package com.socialmedia.editor.controller;

import com.socialmedia.editor.dto.AIContentRequest;
import com.socialmedia.editor.dto.AIContentResponse;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.service.AIContentService;
import com.socialmedia.editor.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AIContentController {

    private final AIContentService aiContentService;

    private final AuthService authService;

    public AIContentController(AIContentService aiContentService, AuthService authService) {
        this.aiContentService = aiContentService;
        this.authService = authService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AIContentResponse> generateContent(
            @Valid @RequestBody AIContentRequest request,
            Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);
            AIContentResponse response = aiContentService.generateContent(request);
            return ok(response);
        } catch (Exception e) {
            AIContentResponse errorResponse = new AIContentResponse("Failed to generate content", false);
            return badRequest().body(errorResponse);
        }
    }

    @PostMapping("/improve")
    public ResponseEntity<AIContentResponse> improveContent(
            @Valid @RequestBody AIContentRequest request,
            Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            if (request.getExistingContent() == null || request.getExistingContent().trim().isEmpty()) {
                AIContentResponse errorResponse = new AIContentResponse("Existing content is required for improvement", false);
                return badRequest().body(errorResponse);
            }

            AIContentResponse response = aiContentService.improveContent(request);
            return ok(response);
        } catch (Exception e) {
            AIContentResponse errorResponse = new AIContentResponse("Failed to improve content", false);
            return badRequest().body(errorResponse);
        }
    }

    @PostMapping("/hashtags")
    public ResponseEntity<AIContentResponse> generateHashtags(
            @Valid @RequestBody AIContentRequest request,
            Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            AIContentResponse response = aiContentService.generateHashtags(request);
            return ok(response);
        } catch (Exception e) {
            AIContentResponse errorResponse = new AIContentResponse("Failed to generate hashtags", false);
            return badRequest().body(errorResponse);
        }
    }

    @PostMapping("/variations")
    public ResponseEntity<AIContentResponse> generateVariations(
            @Valid @RequestBody AIContentRequest request,
            Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            if (request.getExistingContent() == null || request.getExistingContent().trim().isEmpty()) {
                if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                    AIContentResponse errorResponse = new AIContentResponse("Content or prompt is required for variations", false);
                    return badRequest().body(errorResponse);
                }
            }

            AIContentResponse response = aiContentService.generateVariations(request);
            return ok(response);
        } catch (Exception e) {
            AIContentResponse errorResponse = new AIContentResponse("Failed to generate variations", false);
            return badRequest().body(errorResponse);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            return ok("AI Content service is available");
        } catch (Exception e) {
            return badRequest().body("AI Content service unavailable");
        }
    }
}