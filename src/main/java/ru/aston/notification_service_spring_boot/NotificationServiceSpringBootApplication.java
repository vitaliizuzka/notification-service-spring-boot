package ru.aston.notification_service_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableRetry
public class NotificationServiceSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceSpringBootApplication.class, args);
    }

}
