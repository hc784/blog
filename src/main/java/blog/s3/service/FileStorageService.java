package blog.s3.service;

public interface FileStorageService {
    String uploadFile(String key, byte[] data);
    void deletePostImages(Long blogId, Long postId);
}