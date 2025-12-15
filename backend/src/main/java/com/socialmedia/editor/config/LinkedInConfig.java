package com.socialmedia.editor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "linkedin.oauth")
public class LinkedInConfig {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope = "r_liteprofile,r_emailaddress,w_member_social";
    private String authorizationUri = "https://www.linkedin.com/oauth/v2/authorization";
    private String tokenUri = "https://www.linkedin.com/oauth/v2/accessToken";
    private String apiBaseUrl = "https://api.linkedin.com/v2";

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri) {
        this.authorizationUri = authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getAuthorizationUrl() {
        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s",
            authorizationUri, clientId, redirectUri, scope);
    }
}