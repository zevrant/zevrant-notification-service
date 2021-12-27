package com.zevrant.services.zevrantnotificationservice.controllers;

import com.zevrant.services.zevrantnotificationservice.pojo.NotificationType;
import com.zevrant.services.zevrantnotificationservice.services.NotificationService;
import com.zevrant.services.zevrantuniversalcommon.rest.notification.request.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final NotificationService notificationService;

    public WebhookController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/serviceDown")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void serviceDown() {

        Notification notification = new Notification();
        notification.setTitle("Service Down");
        notificationService.sendNotification(notification, NotificationType.SNS);
    }

}
