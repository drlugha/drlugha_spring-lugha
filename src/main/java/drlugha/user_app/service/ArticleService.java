package drlugha.user_app.service;

import drlugha.user_app.dto.ArticleDTO;
import drlugha.user_app.entity.Article;
import drlugha.user_app.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        // Perform any business logic/validation here before saving to repository
        // Example: Mapping DTO to entity
        Article article = new Article();
        article.setArticleName(articleDTO.getArticleName());
        article.setArticleCategory(articleDTO.getArticleCategory());
        article.setArticleSubCategory(articleDTO.getArticleSubCategory());
        article.setArticleTitle(articleDTO.getArticleTitle());
        article.setDescription(articleDTO.getDescription());

        // Persist the stable S3 object key instead of a presigned URL
        article.setImageKey(articleDTO.getImageKey());
        article.setImageUrl(null);

        // Save the article to the repository
        article = articleRepository.save(article);

        // Map the saved entity back to DTO and return
        return mapArticleToDTO(article);
    }

    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
                .map(this::mapArticleToDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO updateArticle(ArticleDTO articleDTO) {
        // Find the article by ID
        Optional<Article> optionalArticle = articleRepository.findById(articleDTO.getUserId());
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            // Update article properties
            article.setArticleName(articleDTO.getArticleName());
            article.setArticleCategory(articleDTO.getArticleCategory());
            article.setArticleSubCategory(articleDTO.getArticleSubCategory());
            article.setArticleTitle(articleDTO.getArticleTitle());
            article.setDescription(articleDTO.getDescription());
            if (articleDTO.getImageKey() != null) {
                article.setImageKey(articleDTO.getImageKey());
            }

            // Save the updated article
            article = articleRepository.save(article);

            // Map the updated article back to DTO and return
            return mapArticleToDTO(article);
        } else {
            // Article not found, return null or throw an exception
            return null;
        }
    }

    public ArticleDTO getArticleById(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.map(this::mapArticleToDTO).orElse(null);
    }

    // Helper method to map Article entity to ArticleDTO
    private ArticleDTO mapArticleToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId()); // Populate the id field
        articleDTO.setArticleName(article.getArticleName());
        articleDTO.setUserId(article.getId());
        articleDTO.setArticleCategory(article.getArticleCategory());
        articleDTO.setArticleSubCategory(article.getArticleSubCategory());
        articleDTO.setArticleTitle(article.getArticleTitle());
        articleDTO.setDescription(article.getDescription());
        articleDTO.setImageKey(article.getImageKey());
        articleDTO.setImageUrl(article.getImageUrl());
        return articleDTO;
    }
}
