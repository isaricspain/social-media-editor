package com.socialmedia.editor.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Minimal Gemini-backed implementation of ChatLanguageModel using WebClient.
 * Avoids direct dependency on Google AI SDK and mirrors LangChain4j API.
 */
public class GoogleGeminiChatModel implements ChatLanguageModel {
    private static final Logger log = LoggerFactory.getLogger(GoogleGeminiChatModel.class);

    private final String apiKey;
    private final String modelName;
    private final double temperature;
    private final int maxTokens;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private GoogleGeminiChatModel(Builder builder) {
        this.apiKey = builder.apiKey;
        this.modelName = builder.modelName;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.webClient = builder.webClient != null ? builder.webClient :
                WebClient.builder()
                        .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                        .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String generate(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();
            part.put("text", prompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            requestBody.set("contents", contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", temperature);
            generationConfig.put("maxOutputTokens", maxTokens);
            requestBody.set("generationConfig", generationConfig);

            String path = "/models/" + modelName + ":generateContent?key=" + apiKey;

            Mono<String> responseMono = webClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30));

            String responseBody = responseMono.block();
            if (responseBody == null) {
                throw new IllegalStateException("Empty response from Gemini API");
            }

            JsonNode responseJson = objectMapper.readTree(responseBody);
            JsonNode candidates = responseJson.get("candidates");

            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode respContent = firstCandidate.get("content");
                if (respContent != null) {
                    JsonNode respParts = respContent.get("parts");
                    if (respParts != null && respParts.isArray() && respParts.size() > 0) {
                        JsonNode firstPart = respParts.get(0);
                        JsonNode text = firstPart.get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }

            throw new IllegalStateException("No content generated from Gemini API");
        } catch (Exception e) {
            log.error("Gemini generation error: {}", e.getMessage());
            throw new RuntimeException("Gemini generation failed: " + e.getMessage(), e);
        }
    }

    public static class Builder {
        private String apiKey;
        private String modelName = "gemini-1.5-flash";
        private double temperature = 0.2;
        private int maxTokens = 1024;
        private WebClient webClient;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey; return this;
        }
        public Builder modelName(String modelName) {
            this.modelName = modelName; return this;
        }
        public Builder temperature(double temperature) {
            this.temperature = temperature; return this;
        }
        public Builder maxOutputTokens(int maxTokens) {
            this.maxTokens = maxTokens; return this;
        }
        public Builder webClient(WebClient webClient) {
            this.webClient = webClient; return this;
        }
        public GoogleGeminiChatModel build() {
            return new GoogleGeminiChatModel(this);
        }
    }
}
