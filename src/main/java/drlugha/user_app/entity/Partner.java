package drlugha.user_app.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Partner extends BaseEntity {

    @Column
    private String partnerName;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT") // Use TEXT data type for 'profile' field
    private String profile;

    @Column
    private String linkedinUrl;

    @Column
    private String twitterUrl;

    @Column
    private String facebookUrl;

    @Column(columnDefinition = "TEXT")
    private String imageUrl; // Field to store image URL

    @Column
    private String imageKey; // Stable S3 object key

    @Temporal(TemporalType.TIMESTAMP)
    private Date imageUrlExpiration; // Field to store URL expiration time

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
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
