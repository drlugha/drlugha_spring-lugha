package drlugha.user_app.dto;

import java.util.Date;

public class PartnerDTO {
    private String partnerName;
    private Long id;
    private String title;
    private String profile;
    private String linkedinUrl;
    private String twitterUrl;
    private String facebookUrl;
    private String imageUrl;
    private String imageKey;
    private Date imageUrlExpiration; // New field to store URL expiration time

    // Getters and setters
    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public Date getImageUrlExpiration() {
        return imageUrlExpiration;
    }

    public void setImageUrlExpiration(Date imageUrlExpiration) {
        this.imageUrlExpiration = imageUrlExpiration;
    }
}
