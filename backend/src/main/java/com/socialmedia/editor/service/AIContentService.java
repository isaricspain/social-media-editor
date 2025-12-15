package com.socialmedia.editor.service;

import com.socialmedia.editor.dto.AIContentRequest;
import com.socialmedia.editor.dto.AIContentResponse;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIContentService {

    private static final Logger logger = LoggerFactory.getLogger(AIContentService.class);

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    public AIContentResponse generateContent(AIContentRequest request) {
        try {
            String prompt = buildPrompt(request);
            String generatedText = chatLanguageModel.generate(prompt);

            AIContentResponse response = new AIContentResponse(generatedText);
            response.setTone(request.getTone());
            response.setPlatform(request.getPlatform());

            return response;
        } catch (Exception e) {
            logger.error("Error generating content", e);
            return new AIContentResponse("Failed to generate content: " + e.getMessage(), false);
        }
    }

    public AIContentResponse improveContent(AIContentRequest request) {
        try {
            String prompt = buildImprovePrompt(request);
            String improvedText = chatLanguageModel.generate(prompt);

            AIContentResponse response = new AIContentResponse(improvedText);
            response.setTone(request.getTone());
            response.setPlatform(request.getPlatform());

            return response;
        } catch (Exception e) {
            logger.error("Error improving content", e);
            return new AIContentResponse("Failed to improve content: " + e.getMessage(), false);
        }
    }

    public AIContentResponse generateHashtags(AIContentRequest request) {
        try {
            String prompt = buildHashtagPrompt(request);
            String hashtagText = chatLanguageModel.generate(prompt);

            List<String> hashtags = extractHashtags(hashtagText);

            AIContentResponse response = new AIContentResponse();
            response.setHashtags(hashtags);
            response.setPlatform(request.getPlatform());

            return response;
        } catch (Exception e) {
            logger.error("Error generating hashtags", e);
            return new AIContentResponse("Failed to generate hashtags: " + e.getMessage(), false);
        }
    }

    public AIContentResponse generateVariations(AIContentRequest request) {
        try {
            String prompt = buildVariationsPrompt(request);
            String variationsText = chatLanguageModel.generate(prompt);

            List<String> variations = Arrays.asList(variationsText.split("\n\n"));

            AIContentResponse response = new AIContentResponse();
            response.setVariations(variations);
            response.setTone(request.getTone());
            response.setPlatform(request.getPlatform());

            return response;
        } catch (Exception e) {
            logger.error("Error generating variations", e);
            return new AIContentResponse("Failed to generate variations: " + e.getMessage(), false);
        }
    }

    // LangChain4j handles API invocation; no manual HTTP/JSON required

    private String buildPrompt(AIContentRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create a social media post ");

        if (!"general".equals(request.getPlatform())) {
            prompt.append("for ").append(request.getPlatform()).append(" ");
        }

        prompt.append("with a ").append(request.getTone()).append(" tone about: ");
        prompt.append(request.getPrompt());

        if ("post".equals(request.getContentType())) {
            prompt.append("\n\nMake it engaging and optimized for social media engagement.");
        }

        return prompt.toString();
    }

    private String buildImprovePrompt(AIContentRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Improve the following social media content to make it more engaging ");

        if (!"general".equals(request.getPlatform())) {
            prompt.append("for ").append(request.getPlatform()).append(" ");
        }

        prompt.append("with a ").append(request.getTone()).append(" tone:\n\n");
        prompt.append(request.getExistingContent());

        if (request.getPrompt() != null && !request.getPrompt().trim().isEmpty()) {
            prompt.append("\n\nAdditional context: ").append(request.getPrompt());
        }

        return prompt.toString();
    }

    private String buildHashtagPrompt(AIContentRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate relevant hashtags ");

        if (!"general".equals(request.getPlatform())) {
            prompt.append("for ").append(request.getPlatform()).append(" ");
        }

        prompt.append("based on this content: ");

        if (request.getExistingContent() != null && !request.getExistingContent().trim().isEmpty()) {
            prompt.append(request.getExistingContent());
        } else {
            prompt.append(request.getPrompt());
        }

        prompt.append("\n\nProvide 5-10 relevant hashtags, each on a new line starting with #");

        return prompt.toString();
    }

    private String buildVariationsPrompt(AIContentRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Create 3 different variations of this social media content ");

        if (!"general".equals(request.getPlatform())) {
            prompt.append("for ").append(request.getPlatform()).append(" ");
        }

        prompt.append("with a ").append(request.getTone()).append(" tone:\n\n");

        if (request.getExistingContent() != null && !request.getExistingContent().trim().isEmpty()) {
            prompt.append(request.getExistingContent());
        } else {
            prompt.append(request.getPrompt());
        }

        prompt.append("\n\nSeparate each variation with a blank line.");

        return prompt.toString();
    }

    private List<String> extractHashtags(String text) {
        Pattern hashtagPattern = Pattern.compile("#\\w+");
        Matcher matcher = hashtagPattern.matcher(text);

        return matcher.results()
                .map(matchResult -> matchResult.group())
                .toList();
    }
}