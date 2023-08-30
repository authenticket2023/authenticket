package com.authenticket.authenticket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authenticket")
public record ConfigProperties(
        String apiVersion,
        String secretKey,
        String databaseId,
        String apiPort,
        String S3BucketName,
        String smtpUsername,
        String smtpPassword
) {
}
