package blog.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsS3Properties {

    @Value("${custom.aws.s3.bucket}")
    private String bucket;

    public String getBucket() {
        return bucket;
    }
    
}
