package com.socialmedia.editor.service;

import com.socialmedia.editor.config.LinkedInConfig;
import com.socialmedia.editor.dto.LinkedInProfileDto;
import com.socialmedia.editor.dto.LinkedInStatsDto;
import com.socialmedia.editor.dto.LinkedInTokenResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class LinkedInConnectorService {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInConnectorService.class);

    @Autowired
    private LinkedInConfig linkedInConfig;

    private final WebClient webClient;

    public LinkedInConnectorService() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.USER_AGENT, "SocialMediaEditor/1.0")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    public String getAuthorizationUrl() {
        return linkedInConfig.getAuthorizationUrl();
    }

    public Mono<LinkedInTokenResponseDto> exchangeAuthorizationCode(String authorizationCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", authorizationCode);
        formData.add("client_id", linkedInConfig.getClientId());
        formData.add("client_secret", linkedInConfig.getClientSecret());
        formData.add("redirect_uri", linkedInConfig.getRedirectUri());

        return webClient.post()
                .uri(linkedInConfig.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(LinkedInTokenResponseDto.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error exchanging authorization code: ", error))
                .onErrorMap(WebClientResponseException.class, ex -> {
                    logger.error("LinkedIn API error response: {}", ex.getResponseBodyAsString());
                    return new RuntimeException("Failed to exchange authorization code: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
                });
    }

    public Mono<LinkedInTokenResponseDto> refreshAccessToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", linkedInConfig.getClientId());
        formData.add("client_secret", linkedInConfig.getClientSecret());

        return webClient.post()
                .uri(linkedInConfig.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(LinkedInTokenResponseDto.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error refreshing access token: ", error))
                .onErrorMap(WebClientResponseException.class, ex -> {
                    logger.error("LinkedIn refresh token error: {}", ex.getResponseBodyAsString());
                    return new RuntimeException("Failed to refresh access token: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
                });
    }

    public Mono<LinkedInProfileDto> getUserProfile(String accessToken) {
        String profileUrl = linkedInConfig.getApiBaseUrl() +
            "/me";

        return webClient.get()
                .uri(profileUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(LinkedInProfileDto.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error fetching user profile: ", error))
                .onErrorMap(WebClientResponseException.class, ex -> {
                    logger.error("LinkedIn profile API error: {}", ex.getResponseBodyAsString());
                    return new RuntimeException("Failed to fetch user profile: " + ex.getMessage());
                });
    }

    public Mono<LinkedInStatsDto> getUserStats(String accessToken) {
        String networkInfoUrl = linkedInConfig.getApiBaseUrl() +
            "/userinfo";

        return webClient.get()
                .uri(networkInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(LinkedInStatsDto.class)
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> logger.error("Error fetching user stats: ", error))
                .onErrorMap(WebClientResponseException.class, ex -> {
                    logger.error("LinkedIn stats API error: {}", ex.getResponseBodyAsString());
                    return new RuntimeException("Failed to fetch user stats: " + ex.getMessage());
                })
                .onErrorReturn(new LinkedInStatsDto());
    }

    private String extractEmailFromResponse(String response) {
        try {
            if (response.contains("emailAddress")) {
                int start = response.indexOf("\"emailAddress\":\"") + 16;
                int end = response.indexOf("\"", start);
                if (start > 15 && end > start) {
                    return response.substring(start, end);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract email from response", e);
        }
        return null;
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            String validationUrl = linkedInConfig.getApiBaseUrl() + "/people/~:(id)";

            String response = webClient.get()
                    .uri(validationUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return response != null && response.contains("id");
        } catch (Exception e) {
            logger.debug("Access token validation failed: {}", e.getMessage());
            return false;
        }
    }
}