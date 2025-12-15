package com.socialmedia.editor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIContentRequest {

    @NotBlank(message = "Prompt is required")
    @Size(max = 1000, message = "Prompt must be less than 1000 characters")
    private String prompt;

    private String tone = "neutral";
    private String platform = "general";
    private String contentType = "post";
    private String existingContent;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExistingContent() {
        return existingContent;
    }

    public void setExistingContent(String existingContent) {
        this.existingContent = existingContent;
    }
}