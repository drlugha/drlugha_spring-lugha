package drlugha.user_app.entity;

import javax.persistence.*;

@Entity
public class Article extends BaseEntity {

    @Column
    private String articleName;

    @Column
    private String articleCategory;

    @Column
    private String articleSubCategory;

    @Column
    private String articleTitle;

    @Column(columnDefinition = "TEXT") // Use TEXT data type for 'description' field
    private String description;

    @Column(columnDefinition = "TEXT") // Use TEXT data type for 'description' field
    private String imageUrl; // Updated to store the image URL

    @Column
    private String imageKey; // Stable S3 object key

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleCategory() {
        return articleCategory;
    }

    public void setArticleCategory(String articleCategory) {
        this.articleCategory = articleCategory;
    }

    public String getArticleSubCategory() {
        return articleSubCategory;
    }

    public void setArticleSubCategory(String articleSubCategory) {
        this.articleSubCategory = articleSubCategory;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    // Getters and setters
    // Constructors, etc.
}
