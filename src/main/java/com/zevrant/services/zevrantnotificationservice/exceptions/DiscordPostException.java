package com.zevrant.services.zevrantnotificationservice.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.Charset;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DiscordPostException extends WebClientResponseException {


    public DiscordPostException(int statusCode, String statusText, HttpHeaders headers, byte[] body, Charset charset) {
        super(statusCode, statusText, headers, body, charset);
    }

    public DiscordPostException(int status, String reasonPhrase, HttpHeaders headers, byte[] body, Charset charset, HttpRequest request) {
        super(status, reasonPhrase, headers, body, charset, request);
    }

    public DiscordPostException(String message, int statusCode, String statusText, HttpHeaders headers, byte[] responseBody, Charset charset) {
        super(message, statusCode, statusText, headers, responseBody, charset);
    }

    public DiscordPostException(String message, int statusCode, String statusText, HttpHeaders headers, byte[] responseBody, Charset charset, HttpRequest request) {
        super(message, statusCode, statusText, headers, responseBody, charset, request);
    }
}
