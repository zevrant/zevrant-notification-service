package com.zevrant.services.zevrantnotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(scanBasePackages = {"com.zevrant.services"})
public class ZevrantNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZevrantNotificationServiceApplication.class, args);
    }

}
