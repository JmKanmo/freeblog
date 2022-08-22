package com.service.config.aws.s3;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AwsS3Config {
    @Value("${util-config.aws_s3-bucket.access_key_id}")
    private String accessKeyId;

    @Value("${util-config.aws_s3-bucket.secret_access_key_id}")
    private String secretAccessKeyId;

    @Value("${util-config.aws_s3-bucket.bucket_name}")
    private String bucketName;
}
