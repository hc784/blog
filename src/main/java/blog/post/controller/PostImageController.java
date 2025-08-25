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

import blog.s3.service.FileStorageService;
import blog.s3.service.S3Service;

@RestController
@RequestMapping("/blogs/{blogId}/posts/{postId}")
public class PostImageController {

	private final String UPLOAD_DIR = "C:/uploads/posts/";
    private final FileStorageService fileStorageService; // ❗️ 인터페이스 타입으로 변경

    public PostImageController(FileStorageService fileStorageService) { // ❗️ 인터페이스 타입으로 변경
        this.fileStorageService = fileStorageService;
    }

	/*
	 * @PostMapping("/upload-image") public ResponseEntity<Map<String, String>>
	 * uploadImage(@PathVariable Long blogId,
	 * 
	 * @PathVariable Long postId,
	 * 
	 * @RequestParam(value="upload",required = false) MultipartFile file) {
	 * System.out.println("📌 uploadImage() 호출됨"); System.out.println("📌 blogId: "
	 * + blogId); System.out.println("📌 postId: " + postId); if (file == null ||
	 * file.isEmpty()) { System.err.println("🚨 Error: 파일이 업로드되지 않았습니다."); return
	 * ResponseEntity.status(HttpStatus.BAD_REQUEST) .body(Map.of("error",
	 * "No file uploaded")); } try { // 파일 저장 디렉토리 설정 (외부 폴더 사용) String folderPath =
	 * UPLOAD_DIR + blogId + "/"; File folder = new File(folderPath); if
	 * (!folder.exists()) { folder.mkdirs(); // 폴더 없으면 생성 }
	 * 
	 * // 파일명 = 게시글 ID + UUID + 확장자 String fileName = postId + "_" +
	 * UUID.randomUUID() + "_" + file.getOriginalFilename(); Path path =
	 * Paths.get(folderPath + fileName); Files.write(path, file.getBytes());
	 * 
	 * // 저장된 파일 URL 반환 String fileUrl = "/uploads/posts/" + blogId + "/" +
	 * fileName; Map<String, String> response = new HashMap<>(); response.put("url",
	 * fileUrl); return ResponseEntity.ok(response); } catch (IOException e) {
	 * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); }
	 * }
	 */
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable Long blogId,
            @PathVariable Long postId,
            @RequestParam("upload") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 업로드되지 않았습니다."));
        }

        try {
            // S3에 저장할 파일 키 생성
            String fileKey = "blogs/" + blogId + "/posts/" + postId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            String fileUrl = fileStorageService.uploadFile(fileKey, file.getBytes());

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드 실패: " + e.getMessage()));
        }
    }

}
