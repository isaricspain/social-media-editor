package com.socialmedia.editor.repository;

import com.socialmedia.editor.model.Post;
import com.socialmedia.editor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserOrderByCreatedAtDesc(User user);

    List<Post> findByUserAndStatus(User user, Post.PostStatus status);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user")
    Long getTotalPostCountByUser(User user);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.status = :status")
    Long getPostCountByUserAndStatus(User user, Post.PostStatus status);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.status = 'DRAFT'")
    Long getDraftPostCountByUser(User user);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.status = 'PUBLISHED'")
    Long getPublishedPostCountByUser(User user);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user = :user AND p.status = 'SCHEDULED'")
    Long getScheduledPostCountByUser(User user);

    @Query("SELECT SUM(p.engagementCount) FROM Post p WHERE p.user = :user AND p.status = 'PUBLISHED'")
    Long getTotalEngagementByUser(User user);

    @Query("SELECT SUM(p.likesCount) FROM Post p WHERE p.user = :user AND p.status = 'PUBLISHED'")
    Long getTotalLikesByUser(User user);

    @Query("SELECT SUM(p.sharesCount) FROM Post p WHERE p.user = :user AND p.status = 'PUBLISHED'")
    Long getTotalSharesByUser(User user);

    @Query("SELECT SUM(p.commentsCount) FROM Post p WHERE p.user = :user AND p.status = 'PUBLISHED'")
    Long getTotalCommentsByUser(User user);

    List<Post> findByStatusAndScheduledTimeBefore(Post.PostStatus status, LocalDateTime dateTime);
}