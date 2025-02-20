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

@RestController
@RequestMapping("/blogs/{blogId}/posts/{postId}")
public class PostImageController {

    private final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@PathVariable Long blogId, 
                                                           @PathVariable Long postId, 
                                                           @RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장 디렉토리 (하나의 폴더에 저장)
            String folderPath = "C:/uploads/blog_" + blogId + "/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 파일명 = 게시글ID + UUID + 확장자
            String fileName = postId + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folderPath + fileName);
            Files.write(path, file.getBytes());

            // URL 반환
            String fileUrl = "/images/blog_" + blogId + "/" + fileName;
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
