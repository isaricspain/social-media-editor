package com.socialmedia.editor.repository;

import com.socialmedia.editor.model.Post;
import com.socialmedia.editor.model.PostPlatform;
import com.socialmedia.editor.model.SocialMediaAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostPlatformRepository extends JpaRepository<PostPlatform, Long> {

    List<PostPlatform> findByPost(Post post);

    List<PostPlatform> findByPostAndIsEnabledTrue(Post post);

    Optional<PostPlatform> findByPostAndPlatform(Post post, SocialMediaAccount.Platform platform);

    List<PostPlatform> findByPublishStatus(PostPlatform.PublishStatus status);

    void deleteByPostAndPlatform(Post post, SocialMediaAccount.Platform platform);
}