package com.socialmedia.editor.service;

import com.socialmedia.editor.model.Post;
import com.socialmedia.editor.model.PostPlatform;
import com.socialmedia.editor.model.SocialMediaAccount;
import com.socialmedia.editor.repository.PostPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostPlatformService {

    @Autowired
    private PostPlatformRepository postPlatformRepository;

    public List<PostPlatform> getPostPlatforms(Post post) {
        return postPlatformRepository.findByPost(post);
    }

    public List<PostPlatform> getEnabledPostPlatforms(Post post) {
        return postPlatformRepository.findByPostAndIsEnabledTrue(post);
    }

    public PostPlatform createOrUpdatePostPlatform(Post post, SocialMediaAccount.Platform platform, String customPrompt) {
        Optional<PostPlatform> existingOpt = postPlatformRepository.findByPostAndPlatform(post, platform);

        if (existingOpt.isPresent()) {
            PostPlatform existing = existingOpt.get();
            existing.setCustomPrompt(customPrompt);
            existing.setIsEnabled(true);
            return postPlatformRepository.save(existing);
        } else {
            PostPlatform newPostPlatform = new PostPlatform(post, platform);
            newPostPlatform.setCustomPrompt(customPrompt);
            return postPlatformRepository.save(newPostPlatform);
        }
    }

    public void enablePostPlatform(Post post, SocialMediaAccount.Platform platform) {
        Optional<PostPlatform> postPlatformOpt = postPlatformRepository.findByPostAndPlatform(post, platform);
        if (postPlatformOpt.isPresent()) {
            PostPlatform postPlatform = postPlatformOpt.get();
            postPlatform.setIsEnabled(true);
            postPlatformRepository.save(postPlatform);
        } else {
            PostPlatform newPostPlatform = new PostPlatform(post, platform);
            postPlatformRepository.save(newPostPlatform);
        }
    }

    public void disablePostPlatform(Post post, SocialMediaAccount.Platform platform) {
        Optional<PostPlatform> postPlatformOpt = postPlatformRepository.findByPostAndPlatform(post, platform);
        if (postPlatformOpt.isPresent()) {
            PostPlatform postPlatform = postPlatformOpt.get();
            postPlatform.setIsEnabled(false);
            postPlatformRepository.save(postPlatform);
        }
    }

    public void deletePostPlatform(Post post, SocialMediaAccount.Platform platform) {
        postPlatformRepository.deleteByPostAndPlatform(post, platform);
    }

    public PostPlatform schedulePostPlatform(Post post, SocialMediaAccount.Platform platform, LocalDateTime scheduledTime) {
        Optional<PostPlatform> postPlatformOpt = postPlatformRepository.findByPostAndPlatform(post, platform);
        if (postPlatformOpt.isPresent()) {
            PostPlatform postPlatform = postPlatformOpt.get();
            postPlatform.setScheduledTime(scheduledTime);
            return postPlatformRepository.save(postPlatform);
        }
        throw new RuntimeException("Post platform configuration not found");
    }

    public PostPlatform markAsPublished(Post post, SocialMediaAccount.Platform platform) {
        Optional<PostPlatform> postPlatformOpt = postPlatformRepository.findByPostAndPlatform(post, platform);
        if (postPlatformOpt.isPresent()) {
            PostPlatform postPlatform = postPlatformOpt.get();
            postPlatform.setPublishStatus(PostPlatform.PublishStatus.PUBLISHED);
            postPlatform.setPublishedAt(LocalDateTime.now());
            return postPlatformRepository.save(postPlatform);
        }
        throw new RuntimeException("Post platform configuration not found");
    }
}