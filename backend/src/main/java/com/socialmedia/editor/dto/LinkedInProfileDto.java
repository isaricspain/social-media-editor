package com.socialmedia.editor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedInProfileDto {

    private String id;

    @JsonProperty("firstName")
    private FirstName firstName;

    @JsonProperty("lastName")
    private LastName lastName;

    @JsonProperty("profilePicture")
    private ProfilePicture profilePicture;

    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FirstName getFirstName() {
        return firstName;
    }

    public void setFirstName(FirstName firstName) {
        this.firstName = firstName;
    }

    public LastName getLastName() {
        return lastName;
    }

    public void setLastName(LastName lastName) {
        this.lastName = lastName;
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
        if (profilePicture != null &&
            profilePicture.displayImage != null &&
            profilePicture.displayImage.elements != null &&
            !profilePicture.displayImage.elements.isEmpty()) {

            DisplayImageElement element = profilePicture.displayImage.elements.get(0);
            if (element.identifiers != null && !element.identifiers.isEmpty()) {
                return element.identifiers.get(0).identifier;
            }
        }
        return null;
    }

    public static class FirstName {
        @JsonProperty("localized")
        public java.util.Map<String, String> localized;
    }

    public static class LastName {
        @JsonProperty("localized")
        public java.util.Map<String, String> localized;
    }

    public static class ProfilePicture {
        @JsonProperty("displayImage")
        public DisplayImage displayImage;
    }

    public static class DisplayImage {
        @JsonProperty("elements")
        public java.util.List<DisplayImageElement> elements;
    }

    public static class DisplayImageElement {
        @JsonProperty("identifiers")
        public java.util.List<Identifier> identifiers;
    }

    public static class Identifier {
        @JsonProperty("identifier")
        public String identifier;
    }
}