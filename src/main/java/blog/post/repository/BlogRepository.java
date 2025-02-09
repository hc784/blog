package blog.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.post.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long>{

}
