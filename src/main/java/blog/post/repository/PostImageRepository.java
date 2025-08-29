package blog.post.repository;

import blog.post.model.Post;
import blog.post.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // 특정 게시물에 속한 모든 이미지 찾기
    List<PostImage> findByPost(Post post);

    // 특정 게시물에 속한 모든 이미지 삭제
    void deleteByPost(Post post);
}