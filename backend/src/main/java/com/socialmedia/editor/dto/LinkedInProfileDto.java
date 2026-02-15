package com.socialmedia.editor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class LinkedInProfileDto {

    private String id;

    @JsonProperty("firstName")
    private MultiLocaleString firstName;

    @JsonProperty("lastName")
    private MultiLocaleString lastName;

    @JsonProperty("headline")
    private MultiLocaleString headline;

    @JsonProperty("localizedFirstName")
    private String localizedFirstName;

    @JsonProperty("localizedLastName")
    private String localizedLastName;

    @JsonProperty("localizedHeadline")
    private String localizedHeadline;

    @JsonProperty("vanityName")
    private String vanityName;

    @JsonProperty("profilePicture")
    private ProfilePicture profilePicture;

    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MultiLocaleString getFirstName() {
        return firstName;
    }

    public void setFirstName(MultiLocaleString firstName) {
        this.firstName = firstName;
    }

    public MultiLocaleString getLastName() {
        return lastName;
    }

    public void setLastName(MultiLocaleString lastName) {
        this.lastName = lastName;
    }

    public MultiLocaleString getHeadline() {
        return headline;
    }

    public void setHeadline(MultiLocaleString headline) {
        this.headline = headline;
    }

    public String getLocalizedFirstName() {
        return localizedFirstName;
    }

    public void setLocalizedFirstName(String localizedFirstName) {
        this.localizedFirstName = localizedFirstName;
    }

    public String getLocalizedLastName() {
        return localizedLastName;
    }

    public void setLocalizedLastName(String localizedLastName) {
        this.localizedLastName = localizedLastName;
    }

    public String getLocalizedHeadline() {
        return localizedHeadline;
    }

    public void setLocalizedHeadline(String localizedHeadline) {
        this.localizedHeadline = localizedHeadline;
    }

    public String getVanityName() {
        return vanityName;
    }

    public void setVanityName(String vanityName) {
        this.vanityName = vanityName;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        if (localizedFirstName != null && localizedLastName != null) {
            return localizedFirstName + " " + localizedLastName;
        }
        if (localizedFirstName != null) {
            return localizedFirstName;
        }
        if (localizedLastName != null) {
            return localizedLastName;
        }

        String fullName = "";
        if (firstName != null && firstName.localized != null && !firstName.localized.isEmpty()) {
            fullName += firstName.localized.values().iterator().next();
        }
        if (lastName != null && lastName.localized != null && !lastName.localized.isEmpty()) {
            if (!fullName.isEmpty()) fullName += " ";
            fullName += lastName.localized.values().iterator().next();
        }
        return fullName;
    }

    public String getProfileImageUrl() {
        if (profilePicture != null) {
            return profilePicture.displayImage;
        }
        return null;
    }

    public static class MultiLocaleString {
        @JsonProperty("localized")
        public Map<String, String> localized;

        @JsonProperty("preferredLocale")
        public PreferredLocale preferredLocale;
    }

    public static class PreferredLocale {
        @JsonProperty("country")
        public String country;

        @JsonProperty("language")
        public String language;
    }

    public static class ProfilePicture {
        @JsonProperty("displayImage")
        public String displayImage;
    }
}
