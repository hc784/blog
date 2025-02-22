package blog.s3.service;

import org.springframework.stereotype.Service;

import blog.s3.AwsS3Properties;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.nio.file.Paths;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final AwsS3Properties awsS3Properties;
    // 생성자 주입
    public S3Service(S3Client s3Client, AwsS3Properties awsS3Properties) {
        this.s3Client = s3Client;
        this.awsS3Properties = awsS3Properties;
    }

    public void uploadFile(String key, String filePath) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3Properties.getBucket())
                        .key(key)
                        .build(),
                RequestBody.fromFile(Paths.get(filePath))
        );
        System.out.println("파일 업로드 완료: " + key);
    }
}
