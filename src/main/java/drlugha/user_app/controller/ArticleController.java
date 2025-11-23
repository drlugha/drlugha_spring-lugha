package drlugha.user_app.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import drlugha.user_app.dto.ArticleDTO;
import drlugha.user_app.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final AmazonS3 amazonS3;

    @Autowired
    public ArticleController(ArticleService articleService, @Qualifier("articleAmazonS3Client") AmazonS3 amazonS3) {
        this.articleService = articleService;
        this.amazonS3 = amazonS3;
    }

    @Value("${amazonProperties.article.bucketName}")
    private String bucketName;

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestParam("id") Long userId,
                                                    @RequestParam("articleName") String articleName,
                                                    @RequestParam("articleCategory") String articleCategory,
                                                    @RequestParam("articleSubCategory") String articleSubCategory,
                                                    @RequestParam("articleTitle") String articleTitle,
                                                    @RequestParam("description") String description,
                                                    @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (userId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        String imageKey = uploadImageToS3(imageFile);

        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setUserId(userId);
        articleDTO.setArticleCategory(articleCategory);
        articleDTO.setArticleSubCategory(articleSubCategory);
        articleDTO.setArticleName(articleName);
        articleDTO.setArticleTitle(articleTitle);
        articleDTO.setDescription(description);
        articleDTO.setImageKey(imageKey);

        ArticleDTO createdArticle = articleService.createArticle(articleDTO);

        populatePresignedImage(createdArticle);

        return ResponseEntity.ok().body(createdArticle);
    }

    private String uploadImageToS3(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID() + "-" + StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageFile.getSize());
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, imageFile.getInputStream(), metadata));

        return fileName;
    }

    private URL generatePresignedUrl(String fileName, int daysUntilExpiration) {
        Date expirationTime = new Date(System.currentTimeMillis() + daysUntilExpiration * 24 * 60 * 60 * 1000); // daysUntilExpiration days expiration
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
        generatePresignedUrlRequest.setExpiration(expirationTime);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    private String extractS3KeyFromUrl(String url) {
        // Logic to extract the S3 key from the URL, assuming the key starts after the bucket name
        // Example: https://bucket-name.s3.amazonaws.com/key-path
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            if (path != null && path.length() > 1) {
                return path.substring(1); // Remove the leading '/'
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{id}/renew-url")
    public ResponseEntity<ArticleDTO> renewImageUrl(@PathVariable("id") Long id) {
        ArticleDTO article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        populatePresignedImage(article);

        return ResponseEntity.ok().body(article);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        articles.forEach(article -> populatePresignedImage(article));
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable("id") Long id) {
        ArticleDTO article = articleService.getArticleById(id);
        if (article != null) {
            populatePresignedImage(article);
            return ResponseEntity.ok().body(article);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void populatePresignedImage(ArticleDTO article) {
        if (article == null) {
            return;
        }
        String objectKey = article.getImageKey();
        if ((objectKey == null || objectKey.isBlank()) && article.getImageUrl() != null) {
            objectKey = extractS3KeyFromUrl(article.getImageUrl());
            article.setImageKey(objectKey);
            articleService.updateArticle(article);
        }

        if (objectKey == null || objectKey.isBlank()) {
            article.setImageUrl(null);
            return;
        }

        URL presigned = generatePresignedUrl(objectKey, 7);
        article.setImageUrl(presigned.toString());
    }
}
