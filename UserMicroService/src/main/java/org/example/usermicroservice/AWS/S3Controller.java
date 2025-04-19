package org.example.usermicroservice.AWS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@Tag(name = "AWS S3", description = "Operations related to AWS S3 storage")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Operation(summary = "Upload file to S3")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileKey = s3Service.uploadFile(file);
        return ResponseEntity.ok("File uploaded: " + fileKey);
    }
}
