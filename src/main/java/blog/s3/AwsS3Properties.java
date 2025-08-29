package blog.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AwsS3Properties {

    @Value("${custom.aws.s3.bucket}")
    private String bucket;
    

    @Value("${custom.aws.cloudfront.domain}")
    private String cloudfrontDomain;


    public String getBucket() {
        return bucket;
    }
    
    public String getCloudfrontDomain() {
        return cloudfrontDomain;
    }
    
}
