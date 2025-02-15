package blog.post.service;


import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.security.model.User;
import blog.security.repository.UserRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogService(BlogRepository blogRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public boolean isOwner(String userEmail, Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) return false;

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        return blog.getOwner().getId().equals(user.getId());
    }
    

    
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }
    
    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("블로그를 찾을 수 없습니다. blogId: " + blogId));
    }
    
    @Transactional
    public void updateBlogSettings(Long blogId, String title, String description, String nickname, MultipartFile profileImage) {
        Blog blog = getBlogById(blogId);
        // 블로그 제목, 설명 업데이트
        blog.setTitle(title);
        blog.setDescription(description);
        // 블로그 소유자의 닉네임 업데이트
        blog.getOwner().setNickname(nickname);
        
        // 프로필 이미지 파일이 업로드된 경우 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 저장할 디렉토리 (프로젝트 내 resources/static/uploads/profile/)
                String uploadDir = "uploads/profile/";
                Path uploadPath = Paths.get("src/main/resources/static/" + uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 파일명 생성 (UUID 기반)
                String originalFilename = profileImage.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFilename = UUID.randomUUID().toString() + extension;

                // 파일 저장
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 저장된 파일의 URL (예: /uploads/profile/{newFilename})
                String imageUrl = "/" + uploadDir + newFilename;
                blog.setProfileImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
            }
        }
        // 블로그 업데이트 (유저 정보도 cascade 처리되었다면 함께 저장됨)
        blogRepository.save(blog);
    }
}


