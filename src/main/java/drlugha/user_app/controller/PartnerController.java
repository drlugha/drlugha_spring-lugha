package drlugha.user_app.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import drlugha.user_app.dto.PartnerDTO;
import drlugha.user_app.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/partners")
public class PartnerController {

    private final PartnerService partnerService;
    private final AmazonS3 amazonS3;

    @Autowired
    public PartnerController(PartnerService partnerService, @Qualifier("partnerAmazonS3Client") AmazonS3 amazonS3) {
        this.partnerService = partnerService;
        this.amazonS3 = amazonS3;
    }

    @Value("${amazonProperties.partner.bucketName}")
    private String bucketName;

    @PostMapping
    public ResponseEntity<PartnerDTO> createPartner(@RequestParam("partnerName") String partnerName,
                                                    @RequestParam("title") String title,
                                                    @RequestParam("profile") String profile,
                                                    @RequestParam("linkedinUrl") String linkedinUrl,
                                                    @RequestParam("twitterUrl") String twitterUrl,
                                                    @RequestParam("facebookUrl") String facebookUrl,
                                                    @RequestParam("partnerImage") MultipartFile partnerImage) throws IOException {
        String objectKey = UUID.randomUUID() + "-" + partnerImage.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(partnerImage.getSize());
        amazonS3.putObject(new PutObjectRequest(bucketName, objectKey, partnerImage.getInputStream(), metadata));

        PartnerDTO partnerDTO = new PartnerDTO();
        partnerDTO.setPartnerName(partnerName);
        partnerDTO.setTitle(title);
        partnerDTO.setProfile(profile);
        partnerDTO.setLinkedinUrl(linkedinUrl);
        partnerDTO.setTwitterUrl(twitterUrl);
        partnerDTO.setFacebookUrl(facebookUrl);
        partnerDTO.setImageKey(objectKey);

        PartnerDTO createdPartner = partnerService.createPartner(partnerDTO);

        populatePresignedImage(createdPartner);

        return ResponseEntity.ok().body(createdPartner);
    }

    @GetMapping
    public ResponseEntity<List<PartnerDTO>> getPartners() {
        List<PartnerDTO> partners = partnerService.getAllPartners();

        partners.forEach(partner -> populatePresignedImage(partner));

        return ResponseEntity.ok().body(partners);
    }


    @GetMapping("/getPartnerId")
    public ResponseEntity<Long> getPartnerId(@RequestParam("id") Long partnerId) {
        // Check if the provided partner ID is not null
        if (partnerId != null) {
            Long retrievedPartnerId = partnerService.getPartnerIdFromDatabase(partnerId);
            if (retrievedPartnerId != null) {
                return ResponseEntity.ok().body(retrievedPartnerId);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            // Handle the case where the partner ID is null
            return ResponseEntity.badRequest().body(null); // You can choose to return a different HTTP status code or message here
        }
    }


    @GetMapping("/getPartnerById/{id}")
    public ResponseEntity<PartnerDTO> getPartnerById(@PathVariable("id") Long id) {
        // Retrieve the partner details from the database using the provided ID
        PartnerDTO partner = partnerService.getPartnerByIdFromDatabase(id);
        if (partner != null) {
            populatePresignedImage(partner);
            return ResponseEntity.ok().body(partner);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/renew-url")
    public ResponseEntity<PartnerDTO> renewImageUrl(@PathVariable("id") Long id) {
        System.out.println("Renewing image URL for partner with ID: " + id);
        PartnerDTO partner = partnerService.getPartnerById(id);
        if (partner == null) {
            System.out.println("Partner not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }

        populatePresignedImage(partner);

        return ResponseEntity.ok().body(partner);
    }

    private void populatePresignedImage(PartnerDTO partner) {
        if (partner == null) {
            return;
        }
        String objectKey = partner.getImageKey();
        if ((objectKey == null || objectKey.isBlank()) && partner.getImageUrl() != null) {
            objectKey = extractObjectKeyFromUrl(partner.getImageUrl());
            partner.setImageKey(objectKey);
            partnerService.updatePartner(partner);
        }

        if (objectKey == null || objectKey.isBlank()) {
            partner.setImageUrl(null);
            partner.setImageUrlExpiration(null);
            return;
        }

        Date expirationTime = new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey);
        generatePresignedUrlRequest.setExpiration(expirationTime);
        URL imageUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        partner.setImageUrl(imageUrl.toString());
        partner.setImageUrlExpiration(expirationTime);
    }

    private String extractObjectKeyFromUrl(String url) {
        try {
            URL u = new URL(url);
            String path = u.getPath();
            return (path != null && path.length() > 1) ? path.substring(1) : null;
        } catch (Exception e) {
            return null;
        }
    }
}