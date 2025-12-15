package com.socialmedia.editor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedInStatsDto {

    @JsonProperty("firstDegreeSize")
    private Long connectionsCount;

    @JsonProperty("numConnections")
    private Long totalConnections;

    @JsonProperty("numFollowers")
    private Long followersCount;

    @JsonProperty("numConnectionsRange")
    private String connectionsRange;

    public Long getConnectionsCount() {
        return connectionsCount != null ? connectionsCount : 0L;
    }

    public void setConnectionsCount(Long connectionsCount) {
        this.connectionsCount = connectionsCount;
    }

    public Long getTotalConnections() {
        return totalConnections != null ? totalConnections : 0L;
    }

    public void setTotalConnections(Long totalConnections) {
        this.totalConnections = totalConnections;
    }

    public Long getFollowersCount() {
        return followersCount != null ? followersCount : 0L;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public String getConnectionsRange() {
        return connectionsRange;
    }

    public void setConnectionsRange(String connectionsRange) {
        this.connectionsRange = connectionsRange;
    }

    public Long getEffectiveConnectionsCount() {
        if (connectionsCount != null && connectionsCount > 0) {
            return connectionsCount;
        }
        if (totalConnections != null && totalConnections > 0) {
            return totalConnections;
        }
        if (connectionsRange != null) {
            try {
                if (connectionsRange.contains("+")) {
                    String baseNumber = connectionsRange.replace("+", "").trim();
                    return Long.parseLong(baseNumber);
                } else if (connectionsRange.contains("-")) {
                    String[] parts = connectionsRange.split("-");
                    if (parts.length == 2) {
                        return Long.parseLong(parts[1].trim());
                    }
                }
            } catch (NumberFormatException e) {

            }
        }
        return 0L;
    }
}