package ru.aston.notification_service_spring_boot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.aston.notification_service_spring_boot.service.NotificationService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody String event){
        LOGGER.info("trying to send notification controller level {}", event);
        try {
            notificationService.sendNotification(event);
            LOGGER.info("notification was sent successfully controller level");
            return ResponseEntity.ok("Email sent");
        }catch (Exception e){
            LOGGER.error("failed send notification controller level: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        }
    }

}
