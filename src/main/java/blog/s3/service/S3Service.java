package blog.s3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import blog.s3.AwsS3Properties;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("prod")
public class S3Service implements FileStorageService {

	 // 1. Logger 객체 생성
    private static final Logger log = LoggerFactory.getLogger(S3Service.class);
    
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties awsS3Properties;
    // 생성자 주입
    public S3Service(S3Client s3Client,S3Presigner s3Presigner, AwsS3Properties awsS3Properties) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.awsS3Properties = awsS3Properties;
    }

    /**
     * 바이트 배열을 이용한 업로드 (이미지 등)
     */
    @Override
    public String uploadFile(String key, byte[] data) {
    	   // S3에 업로드하면서 Public 읽기 권한 부여
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3Properties.getBucket())
                        .key(key)
                        .build(),
                RequestBody.fromBytes(data)
        );

        return awsS3Properties.getCloudfrontDomain() + "/" + key;
    }
    
    
    public String getPresignedUrl(String key) {
        // --- 시간 측정 코드 시작 ---
        long startTime = System.currentTimeMillis();
        // -------------------------
        
        // 1️⃣ S3에서 Presigned URL을 생성할 요청을 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .key(key)
                .build();

        // 2️⃣ Presigned 요청 생성 (유효 기간 20분)
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(20)) // Presigned URL 유효 시간
                .getObjectRequest(getObjectRequest)
                .build();

        // 3️⃣ Presigned URL 생성
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        // --- 시간 측정 코드 종료 및 로그 출력 ---
        long endTime = System.currentTimeMillis();
        log.info("Presigned URL 생성 소요 시간 ({}): {}ms",key, (endTime - startTime));
        // ------------------------------------

        
        return presignedRequest.url().toString();
    }
    
    /**
     * 특정 게시글(Post)의 이미지 삭제 (S3)
     */
    @Override
    public void deletePostImages(Long blogId, Long postId) {
        // 📌 postId 폴더 경로 설정
        String folderPrefix = "blogs/" + blogId + "/posts/" + postId + "/";
        // 📌 S3에서 postId 폴더 내 모든 객체 조회
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(awsS3Properties.getBucket())
                .prefix(folderPrefix)  // 📌 해당 폴더 내 파일만 조회
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        // 📌 폴더 안의 모든 파일 가져오기
        List<S3Object> postFiles = listResponse.contents();

        if (postFiles.isEmpty()) {
            System.out.println("ℹ️ 삭제할 이미지가 없습니다. (postId: " + postId + ")");
            return;
        }

        // 📌 삭제 요청 생성
        List<ObjectIdentifier> objectsToDelete = postFiles.stream()
                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(awsS3Properties.getBucket())
                .delete(Delete.builder().objects(objectsToDelete).build())
                .build();

        // 📌 삭제 요청 실행
        s3Client.deleteObjects(deleteRequest);

        System.out.println("🗑️ 삭제 완료: " + postFiles.size() + "개의 파일");
        postFiles.forEach(file -> System.out.println("  - 삭제됨: " + file.key()));
    }
}
