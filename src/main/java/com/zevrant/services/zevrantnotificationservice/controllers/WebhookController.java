package com.zevrant.services.zevrantnotificationservice.controllers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.zevrant.services.zevrantnotificationservice.rest.request.WebhookRequest;
import net.zevrant.services.security.common.secrets.management.services.AwsSessionCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final AwsSessionCredentialsProvider credentialsProvider;
    private final AmazonSNS snsClient;

    public WebhookController() {
        this.credentialsProvider = new AwsSessionCredentialsProvider();
        this.snsClient = AmazonSNSClientBuilder
                .defaultClient();
    }

    @PostMapping("/serviceDown")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void serviceDown() {

        try {
            credentialsProvider.assumeRole(Regions.US_EAST_1.getName(), System.getenv("ROLE_ARN"));
            BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
                    System.getProperty("accessKeyId"),
                    System.getProperty("secretAccessKey"),
                    System.getProperty("sessionToken"));
            PublishRequest publishRequest = new PublishRequest()
                    .withTopicArn("arn:aws:sns:us-east-1:725235728275:kubernetes-alerts")
                    .withMessage("Service Offline")
                    .withRequestCredentialsProvider(new AWSStaticCredentialsProvider(basicSessionCredentials));

            PublishResult result = snsClient.publish(publishRequest);
            assert (result.getMessageId() != null);
        } catch (Exception ex) {
            logger.error("Failed to publish sns message");
            ex.printStackTrace();
            throw ex;
        }
    }

}
