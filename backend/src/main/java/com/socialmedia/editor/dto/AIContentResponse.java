package com.socialmedia.editor.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AIContentResponse {

    private String generatedContent;
    private List<String> hashtags;
    private List<String> variations;
    private String tone;
    private String platform;
    private LocalDateTime generatedAt;
    private boolean success;
    private String errorMessage;

    public AIContentResponse() {
        this.generatedAt = LocalDateTime.now();
        this.success = true;
    }

    public AIContentResponse(String generatedContent) {
        this();
        this.generatedContent = generatedContent;
    }

    public AIContentResponse(String errorMessage, boolean success) {
        this();
        this.errorMessage = errorMessage;
        this.success = success;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(String generatedContent) {
        this.generatedContent = generatedContent;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getVariations() {
        return variations;
    }

    public void setVariations(List<String> variations) {
        this.variations = variations;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}