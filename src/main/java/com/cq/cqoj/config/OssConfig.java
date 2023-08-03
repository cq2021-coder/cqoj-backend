package com.cq.cqoj.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云文件系统配置
 *
 * @author 程崎
 * @since 2023/08/03
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class OssConfig {
    private String endpoint = "oss-cn-beijing.aliyuncs.com";
    private String accessKey = "LTAI5tSqHcpy8kVm7W3KSYJ5";
    private String keySecret = "dTJcMVGfYxdDy2x6knAXUwMvYgNc04";
    private String bucketName = "cq-cqyx";

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKey, keySecret);
    }
}
