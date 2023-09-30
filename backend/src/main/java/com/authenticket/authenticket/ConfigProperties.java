package com.authenticket.authenticket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authenticket")
public record ConfigProperties(
        String apiVersion,
        String secretKey,
        String databaseId,
        String frontendDevUrl,
        String frontendProductionUrl,
        String backendDevUrl,
        String backendProductionUrl,
        String loadbalancerUrl,
        String S3BucketName,
        String smtpUsername,
        String smtpPassword,
        String stripeSecret,
        String stripePublishable,
        String payPalSecret,
        String payPalClient,
        String payPalMode
) {
}
