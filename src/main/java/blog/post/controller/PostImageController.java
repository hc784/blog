package blog.post.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blog.post.model.Post;
import blog.post.model.PostImage;
import blog.post.repository.PostImageRepository;
import blog.post.repository.PostRepository;
import blog.s3.service.FileStorageService;
import blog.s3.service.S3Service;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/blogs/{blogId}/posts/{postId}")
public class PostImageController {


    private final FileStorageService fileStorageService; // ❗️ 인터페이스 타입으로 변경
    private final PostRepository postRepository; // Post 엔티티를 찾기 위해 추가
    private final PostImageRepository postImageRepository; // PostImage를 저장하기 위해 추가

    // 생성자를 통해 의존성 주입
    public PostImageController(FileStorageService fileStorageService,
                               PostRepository postRepository,
                               PostImageRepository postImageRepository) {
        this.fileStorageService = fileStorageService;
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
    }

    @PostMapping("/upload-image")
    @Transactional
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable Long blogId,
            @PathVariable Long postId,
            @RequestParam("upload") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 업로드되지 않았습니다."));
        }

        try {
        	Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        	
            // S3에 저장할 파일 키 생성
            String fileKey = "blogs/" + blogId + "/posts/" + postId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            String fileUrl = fileStorageService.uploadFile(fileKey, file.getBytes());

            PostImage postImage = new PostImage(fileUrl, post);
            postImageRepository.save(postImage);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 실패: " + e.getMessage()));
        }
    }

}
