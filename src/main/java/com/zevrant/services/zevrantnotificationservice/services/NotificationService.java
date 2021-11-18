package com.zevrant.services.zevrantnotificationservice.services;

import com.amazonaws.auth.AWSSessionCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.zevrant.services.zevrantnotificationservice.exceptions.InvalidAddresseeException;
import com.zevrant.services.zevrantnotificationservice.exceptions.NotificationTypeNotImplementedException;
import com.zevrant.services.zevrantnotificationservice.pojo.NotificationType;
import com.zevrant.services.zevrantnotificationservice.rest.request.Notification;
import com.zevrant.services.zevrantsecuritycommon.services.AwsSessionCredentialsProvider;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final AwsSessionCredentialsProvider credentialsProvider;
    private final AmazonSimpleEmailServiceAsync sesClient;
    private final AmazonSNS snsClient;
    private final String returnAddress;
    private final String[] addressees;
    private final String environment;

    @Autowired
    public NotificationService(@Value("${zevrant.services.email.returnAddress}") String returnAddress,
                               @Value("${zevrant.services.email.addressees}") String[] addressees) {
        this.returnAddress = returnAddress;
        this.addressees = addressees;
        Pattern emailPattern = Pattern.compile("[\\w\\d\\-_]+@[\\w\\d\\-_]+\\.[\\w\\d\\-_]+");
        for(String address : addressees) {
            if(!emailPattern.matcher(address).matches()) {
                throw new InvalidAddresseeException("An invalid email recipient was provided at system start");
            }
        }
        this.environment = System.getenv().getOrDefault("ENVIRONMENT", "local");
        this.credentialsProvider = new AwsSessionCredentialsProvider();
        this.snsClient = AmazonSNSClientBuilder
                .defaultClient();
        this.sesClient = AmazonSimpleEmailServiceAsyncClientBuilder
                .defaultClient();
    }

    public void sendNotification(Notification notification, NotificationType type) {
        switch (type) {
            case SNS:
                sendSnsNotification(notification);
                break;
            case EMAIL:
                sendEmailNotification(notification);
                break;
            default:
                throw new NotificationTypeNotImplementedException("Notifications of type " + type.name() + " have not been implemented");
        }
    }

    private BasicSessionCredentials assumeRole() {
        credentialsProvider.assumeRole(Regions.US_EAST_1.getName(), System.getenv("ROLE_ARN"));
        return new BasicSessionCredentials(
                System.getProperty("accessKeyId"),
                System.getProperty("secretAccessKey"),
                System.getProperty("sessionToken"));
    }

    private void sendEmailNotification(Notification notification) {
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination(Arrays.asList(addressees)))
                    .withMessage(new Message()
                            .withSubject(new Content().withData(notification.getSubject()))
                            .withBody(new Body().withText(new Content().withData(notification.getBody()))))
                    .withSource(returnAddress)
                    .withRequestCredentialsProvider(new AWSStaticCredentialsProvider(assumeRole()));

            sesClient.sendEmailAsync(request,
                    new AsyncHandler<>() {
                        @Override
                        public void onError(Exception exception) {
                            RuntimeException runtimeException = new RuntimeException(exception.getMessage());
                            runtimeException.setStackTrace(exception.getStackTrace());
                            logger.error("Failed to Send email, {}", exception.getMessage());
                            throw runtimeException;
                        }

                        @Override
                        public void onSuccess(SendEmailRequest request, SendEmailResult sendEmailResult) {
                            logger.info("sent SES email with ID {} and response metadata {}",
                                    sendEmailResult.getMessageId(),
                                    sendEmailResult.getSdkResponseMetadata().toString());

                        }
                    });
    }

    private void sendSnsNotification(Notification notification) {
        StringBuilder messageBuilder = new StringBuilder("");
        messageBuilder.append("Title: ")
                .append(notification.getTitle());

        if (StringUtils.isNotBlank(notification.getSubject())) {
            messageBuilder.append("\n")
                    .append("Subject: ")
                    .append(notification.getSubject());
        }

        if(StringUtils.isNotBlank(notification.getBody())) {
            messageBuilder.append("\n")
                    .append("Body: ")
                    .append(notification.getBody())
                    .append("\n");
        }

        try {

            PublishRequest publishRequest = new PublishRequest()
                    .withTopicArn("arn:aws:sns:us-east-1:725235728275:kubernetes-alerts")
                    .withMessage(messageBuilder.toString())
                    .withRequestCredentialsProvider(new AWSStaticCredentialsProvider(assumeRole()));

            PublishResult result = snsClient.publish(publishRequest);
            assert (result.getMessageId() != null);
        } catch (Exception ex) {
            logger.error("Failed to publish sns message");
            ex.printStackTrace();
            throw ex;
        }
    }
}
