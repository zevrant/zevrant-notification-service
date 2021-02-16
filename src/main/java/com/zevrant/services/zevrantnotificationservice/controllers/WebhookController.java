package com.zevrant.services.zevrantnotificationservice.controllers;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.zevrant.services.zevrantnotificationservice.rest.request.WebhookRequest;
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

    private final AmazonSNS snsClient;

    public WebhookController() {
        this.snsClient = AmazonSNSClientBuilder
                .defaultClient();
    }

    @PostMapping("/serviceDown")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void serviceDown(@RequestBody WebhookRequest webhookRequest) {

        try {
            PublishRequest publishRequest = new PublishRequest()
                    .withTopicArn("arn:aws:sns:us-east-1:725235728275:kubernetes-alerts")
                    .withMessage(webhookRequest.getMessage());

            PublishResult result = snsClient.publish(publishRequest);
            assert (result.getMessageId() != null);
        } catch (Exception ex) {
            logger.error("Failed to publish sns message");
        }
    }

}
