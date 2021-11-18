//package com.zevrant.services.zevrantnotificationservice.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.zevrant.services.zevrantnotificationservice.rest.response.ZevrantErrorResponse;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedOutputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@ControllerAdvice
//public class ErrorHandler {
//
//    private final ObjectWriter objectWriter;
//
//    public ErrorHandler() {
//        this.objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
//    }
//
//    @ExceptionHandler(value = RuntimeException.class)
//    public void handleRuntimeException(HttpServletResponse response, RuntimeException runtimeException) throws IOException {
//        ResponseStatus responseStatus = AnnotationUtils.findAnnotation
//                (runtimeException.getClass(), ResponseStatus.class);
//        if (responseStatus == null) {
//            return;
//        }
//        response.setStatus(responseStatus.value().value());
//        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
//        ZevrantErrorResponse errorResponse = new ZevrantErrorResponse(response.getStatus(), runtimeException.getMessage());
//        outputStream.write(objectWriter.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8));
//        outputStream.flush();
//        outputStream.close();
//    }
//
//}
