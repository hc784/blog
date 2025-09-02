package blog.post.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.post.dto.PostDto;
import blog.post.model.Post;
import blog.post.model.PostImage;
import blog.post.repository.CategoryRepository;
import blog.post.repository.PostImageRepository;
import blog.post.repository.PostRepository;
import blog.s3.service.FileStorageService;
import blog.s3.service.S3Service;
import jakarta.persistence.EntityNotFoundException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    
    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
    		FileStorageService fileStorageService, PostImageRepository postImageRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.postImageRepository = postImageRepository;
        this.s3Service = s3Service;
    }

    // blogId를 포함하여 게시글 생성
 // 게시글 생성 (DTO를 매개변수로 받음)
    @Transactional
    public Post createPost(Long blogId, PostDto postDTO) {
        Post post = new Post();
        post.setBlogId(blogId);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategory(categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + postDTO.getCategoryId())));
        postRepository.save(post);
        
        updatePostImages(post, postDTO.getContent());

        return post;
    }

    @Transactional
    public Post createDraft(Long blogId) {
        Post post = new Post();
        post.setBlogId(blogId);
        post.setDraft(true);  // 임시 저장 상태
        return postRepository.save(post);
    }
    
    
 // 게시글 수정 처리 (필요 시 blogId 체크)
    @Transactional
    public Post updatePost(Long blogId, Long id, PostDto postDTO) {
        Post post = getPostById(blogId, id);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategory(categoryRepository.findById(postDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + postDTO.getCategoryId())));
        post.setDraft(false);
        postRepository.save(post);
        updatePostImages(post, postDTO.getContent());
        return post;

    }
    
    // 전체 게시글 조회 (blogId 조건 추가)
    @Transactional(readOnly = true)
    public Page<Post> getPaginatedPosts(Long blogId, int page, int size) {
        int maxSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, maxSize, Sort.by("createdAt").descending());
        return postRepository.findByBlogIdAndIsDraftFalse(blogId, pageable);  // blogId를 이용해 조회
    }
    
    // 특정 카테고리(및 자식 카테고리 포함) 게시글 조회 (blogId 조건 추가)
    @Transactional(readOnly = true)
    public Page<Post> getPaginatedPostsByCategory(Long blogId, Long categoryId, int page, int size) {
        int maxSize = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, maxSize, Sort.by("createdAt").descending());
        // Repository에 blogId와 category 관련 조건을 함께 처리하는 메서드 필요
        return postRepository.findPostsByBlogIdAndCategoryOrChildren(blogId, categoryId, pageable);
    }
    
    // 게시글 수정 처리 (필요 시 blogId 체크)
    @Transactional
    public void updatePost(Long blogId, Long id, String title, String content) {
        Post post = getPostById(blogId, id);
        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);
    }
    
    @Transactional
    public Post getPostById(Long blogId, Long id) {
        postRepository.incrementViewCount(id);  // 조회수 증가
        // blogId 조건을 추가하여 게시글 조회
        return postRepository.findByIdAndBlogId(id, blogId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    // 게시글 삭제 (blogId 조건 추가)
    @Transactional
    public void deletePost(Long blogId, Long id) {
        Post post = getPostById(blogId, id);
        deletePostImages(blogId, id);
        postRepository.delete(post);
    }
    
    // 🔹 제목 + 내용 하나의 검색어로 검색 (구분 없이 검색)
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(Long blogId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.searchPostsByBlogIdAndKeyword(
                blogId, keyword, pageable);
    }
    @Transactional(readOnly = true)
    public List<Post> getRecentPosts(Long blogId) {
        return postRepository.findTop5ByBlogIdAndIsDraftFalseOrderByCreatedAtDesc(blogId);
    }
    
    private void deletePostImages(Long blogId, Long postId) {
		/*
		 * String folderPath = "C:/uploads/posts/" + blogId + "/"; // 저장된 폴더 경로 File
		 * folder = new File(folderPath);
		 * 
		 * if (folder.exists() && folder.isDirectory()) { File[] files =
		 * folder.listFiles(); if (files != null) { for (File file : files) { if
		 * (file.getName().startsWith(postId + "_")) { // 📌 해당 게시글의 이미지만 삭제 if
		 * (file.delete()) { System.out.println("🗑️ 삭제됨: " + file.getName()); } else {
		 * System.err.println("🚨 삭제 실패: " + file.getName()); } } } } }
		 */
    	fileStorageService.deletePostImages(blogId, postId);
    }
    @Transactional
    private void updatePostImages(Post post, String content) {
        // 1. 게시물 본문(HTML)에서 이미지 URL들을 모두 파싱하여 추출
        Set<String> newImageUrls = parseImageUrlsFromContent(content);

        // 2. DB에서 해당 게시물에 이미 연결된 이미지 목록을 조회
        // 이건 update시에만 조회하기 (임시글도)
        List<PostImage> existingImages = postImageRepository.findByPost(post);
        Set<String> existingImageUrls = existingImages.stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toSet());

        // 3. (삭제 로직) 기존 이미지 목록(DB)에는 있지만, 새 본문에는 없는 이미지들을 찾아서 삭제
        List<PostImage> imagesToDelete = existingImages.stream()
                .filter(img -> !newImageUrls.contains(img.getImageUrl()))
                .collect(Collectors.toList());

        for (PostImage image : imagesToDelete) {
            // S3에서 파일 삭제
            s3Service.deleteFile(extractS3KeyFromUrl(image.getImageUrl()));
        }
        // DB에서 PostImage 레코드 삭제
        postImageRepository.deleteAll(imagesToDelete);

        // 4. (추가 로직) 새 본문에는 있지만, 기존 이미지 목록(DB)에는 없는 이미지들을 찾아서 DB에 추가
        for (String newUrl : newImageUrls) {
            if (!existingImageUrls.contains(newUrl)) {
                PostImage newPostImage = new PostImage(newUrl, post);
                postImageRepository.save(newPostImage);
            }
        }
    }
    
    private Set<String> parseImageUrlsFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return Set.of();
        }
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        return imgTags.stream()
                .map(element -> element.attr("src"))
                .collect(Collectors.toSet());
    }
    
    private String extractS3KeyFromUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            return path.substring(1);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI syntax: " + imageUrl);
            return null;
        }
    }


}
