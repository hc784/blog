package blog.post.service;


import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.s3.service.FileStorageService;
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
    private final FileStorageService fileStorageService;
    
    public BlogService(BlogRepository blogRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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
        blog.setTitle(title);
        blog.setDescription(description);
        blog.getOwner().setNickname(nickname);
        
        // ❗️ 프로필 이미지 처리 로직 변경
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // blogs/{blogId}/profile/{UUID}_{fileName} 형식의 키 생성
                String fileKey = "blogs/" + blogId + "/profile/" + UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
                
                // ❗️ FileStorageService를 사용하여 파일 업로드
                String imageUrl = fileStorageService.uploadFile(fileKey, profileImage.getBytes());
                
                blog.setProfileImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
            }
        }
        
        blogRepository.save(blog);
    }
}