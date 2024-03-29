package com.zevrant.services.zevrantnotificationservice.controllers;

import com.zevrant.services.zevrantnotificationservice.pojo.NotificationType;
import com.zevrant.services.zevrantnotificationservice.services.NotificationService;
import com.zevrant.services.zevrantuniversalcommon.rest.notification.request.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     *
     * @param notification Notification to be sent out
     * @param notificationType Type of notification to be sent out, if not provided, defaults to SNS
     */
    @PostMapping
    @PreAuthorize("hasAuthority('notifications')")
    public Mono<?> sendNotification(@RequestBody Notification notification,
                                       @RequestHeader(value = "NOTIFICATION_TYPE", required = false) String notificationType) {
        return notificationService.sendNotification(notification, (NotificationType.isValidType(notificationType))?
                NotificationType.valueOf(notificationType.toUpperCase(Locale.ROOT))
                : NotificationType.DISCORD);
    }
}
