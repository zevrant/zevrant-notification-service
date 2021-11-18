package com.zevrant.services.zevrantnotificationservice.rest.response;

public class ZevrantErrorResponse {

    private int responseStatus;
    private String message;

    public ZevrantErrorResponse(int responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getMessage() {
        return message;
    }
}
