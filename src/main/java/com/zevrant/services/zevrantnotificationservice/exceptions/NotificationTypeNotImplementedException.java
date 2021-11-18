package com.zevrant.services.zevrantnotificationservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST,
    reason = "Notification Type Not Implemented")
public class NotificationTypeNotImplementedException extends RuntimeException{

    public NotificationTypeNotImplementedException(String message) {
        super(message);
    }
}
