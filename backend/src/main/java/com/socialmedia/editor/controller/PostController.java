package com.socialmedia.editor.controller;

import com.socialmedia.editor.model.Post;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.service.AuthService;
import com.socialmedia.editor.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostService postService;

    private final AuthService authService;

    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getPosts(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            List<Post> posts = postService.getPostsByUser(user);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<Post>> getDraftPosts(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            List<Post> posts = postService.getDraftPostsByUser(user);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/published")
    public ResponseEntity<List<Post>> getPublishedPosts(Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            List<Post> posts = postService.getPublishedPostsByUser(user);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId, Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Post post = postService.getPostById(postId, user);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request,
                                      Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Post post = postService.createPost(user, request.getContent(), request.getTitle(),
                    request.getReferences());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create post");
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                      @RequestBody UpdatePostRequest request,
                                      Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Post post = postService.updatePost(postId, request.getContent(), request.getTitle(),
                    request.getReferences(), user);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update post");
        }
    }

    @PostMapping("/{postId}/schedule")
    public ResponseEntity<?> schedulePost(@PathVariable Long postId,
                                        @RequestBody SchedulePostRequest request,
                                        Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Post post = postService.schedulePost(postId, request.getScheduledTime(), user);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to schedule post");
        }
    }

    @PostMapping("/{postId}/publish")
    public ResponseEntity<?> publishPost(@PathVariable Long postId,
                                       Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            Post post = postService.publishPost(postId, user);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to publish post");
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                           Authentication authentication) {
        try {
            User user = authService.getCurrentUser(authentication);

            postService.deletePost(postId, user);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete post");
        }
    }

    public static class CreatePostRequest {
        private String content;
        private String title;
        private String references;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReferences() {
            return references;
        }

        public void setReferences(String references) {
            this.references = references;
        }
    }

    public static class UpdatePostRequest {
        private String content;
        private String title;
        private String references;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getReferences() {
            return references;
        }

        public void setReferences(String references) {
            this.references = references;
        }
    }

    public static class SchedulePostRequest {
        private LocalDateTime scheduledTime;

        public LocalDateTime getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
        }
    }
}