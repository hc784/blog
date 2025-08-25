package blog.s3.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import blog.s3.service.S3Service;

import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping("/s3")
@Profile("prod")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * S3 이미지 Presigned URL 제공
     */
    @GetMapping("/image")
    public ResponseEntity<Void> getImageUrl(@RequestParam String key) {
        // 최신 Presigned URL 생성
        String presignedUrl = s3Service.getPresignedUrl(key);

        // 302 Redirect (Presigned URL로 리디렉션)
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .build();
    }
}
