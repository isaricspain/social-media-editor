package com.socialmedia.editor.service;

import com.socialmedia.editor.model.Post;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Post> getDraftPostsByUser(User user) {
        return postRepository.findByUserAndStatus(user, Post.PostStatus.DRAFT);
    }

    public List<Post> getPublishedPostsByUser(User user) {
        return postRepository.findByUserAndStatus(user, Post.PostStatus.PUBLISHED);
    }

    public Post createPost(User user, String content, String title, String references) {
        Post post = new Post(user, content, Post.PostStatus.DRAFT);
        post.setTitle(title);
        post.setReferences(references);
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, String content, String title, String references, User user) {
        Optional<Post> postOpt = postRepository.findByIdAndUser(postId, user);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setContent(content);
            post.setTitle(title);
            post.setReferences(references);
            return postRepository.save(post);
        }
        throw new RuntimeException("Post not found");
    }

    public Post getPostById(Long postId, User user) {
        Optional<Post> postOpt = postRepository.findByIdAndUser(postId, user);
        if (postOpt.isPresent()) {
            return postOpt.get();
        }
        throw new RuntimeException("Post not found");
    }

    public Post schedulePost(Long postId, LocalDateTime scheduledTime, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to schedule this post");
            }
            post.setScheduledTime(scheduledTime);
            post.setStatus(Post.PostStatus.SCHEDULED);
            return postRepository.save(post);
        }
        throw new RuntimeException("Post not found");
    }

    public Post publishPost(Long postId, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to publish this post");
            }
            post.setStatus(Post.PostStatus.PUBLISHED);
            post.setPublishedAt(LocalDateTime.now());
            return postRepository.save(post);
        }
        throw new RuntimeException("Post not found");
    }

    public void deletePost(Long postId, User user) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to delete this post");
            }
            postRepository.delete(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    public Long getTotalPostCountByUser(User user) {
        return postRepository.getTotalPostCountByUser(user);
    }

    public Long getDraftPostCountByUser(User user) {
        Long count = postRepository.getDraftPostCountByUser(user);
        return count != null ? count : 0L;
    }

    public Long getPublishedPostCountByUser(User user) {
        Long count = postRepository.getPublishedPostCountByUser(user);
        return count != null ? count : 0L;
    }

    public Long getScheduledPostCountByUser(User user) {
        Long count = postRepository.getScheduledPostCountByUser(user);
        return count != null ? count : 0L;
    }

    public Long getTotalEngagementByUser(User user) {
        Long total = postRepository.getTotalEngagementByUser(user);
        return total != null ? total : 0L;
    }

    public Long getTotalLikesByUser(User user) {
        Long total = postRepository.getTotalLikesByUser(user);
        return total != null ? total : 0L;
    }

    public Long getTotalSharesByUser(User user) {
        Long total = postRepository.getTotalSharesByUser(user);
        return total != null ? total : 0L;
    }

    public Long getTotalCommentsByUser(User user) {
        Long total = postRepository.getTotalCommentsByUser(user);
        return total != null ? total : 0L;
    }

    public List<Post> getScheduledPostsToPublish() {
        return postRepository.findByStatusAndScheduledTimeBefore(
                Post.PostStatus.SCHEDULED, LocalDateTime.now());
    }

    public Post updatePostEngagement(Long postId, Long likes, Long shares, Long comments) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setLikesCount(likes);
            post.setSharesCount(shares);
            post.setCommentsCount(comments);
            post.setEngagementCount(likes + shares + comments);
            return postRepository.save(post);
        }
        throw new RuntimeException("Post not found");
    }
}