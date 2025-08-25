// LocalStorageService.java (새 파일)

package blog.s3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Profile("local") // ❗️ local 프로필에서만 이 빈을 사용
public class LocalStorageService implements FileStorageService {

    @Value("${file.upload-dir}") // ❗️ application.properties에서 설정한 경로 주입
    private String uploadDir;

    @Override
    public String uploadFile(String key, byte[] data) {
        try {
        	Path filePath = Paths.get(uploadDir + File.separator + key);
        	 Files.createDirectories(filePath.getParent());

             // 파일 저장
             Files.write(filePath, data);

             // 웹에서 접근 가능한 경로 반환 (URL은 / 사용)
             return "/uploads/" + key.replace(File.separator, "/");

        } catch (IOException e) {
            throw new RuntimeException("로컬 파일 업로드 실패: " + key, e);
        }
    }

    @Override
    public void deletePostImages(Long blogId, Long postId) {
        String folderPath = uploadDir + "/blogs/" + blogId + "/posts/" + postId;
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folder.delete(); // 폴더 삭제
        }
    }
}