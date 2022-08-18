package com.service.config.sftp;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SFtpConfig {
    @Value("${util-config.aws_jm_ec2_sftp.ip}")
    private String ip;

    @Value("${util-config.aws_jm_ec2_sftp.port}")
    private Integer port;

    @Value("${util-config.aws_jm_ec2_sftp.id}")
    private String id;

    @Value("${util-config.aws_jm_ec2_sftp.password}")
    private String password;

    @Value("${util-config.aws_jm_ec2_sftp.directory}")
    private String directory;

    @Value("${util-config.aws_jm_ec2_sftp.timeout}")
    private Integer timeout;
}
