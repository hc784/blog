package blog.post.service;


import blog.post.model.Blog;
import blog.post.repository.BlogRepository;
import blog.security.model.User;
import blog.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found with id " + id));
    }
}
