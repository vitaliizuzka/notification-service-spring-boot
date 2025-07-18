package ru.aston.notification_service_spring_boot.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.aston.notification_service_spring_boot.service.NotificationService;

@Service
public class KafkaConsumerService {
    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final NotificationService notificationService;

    public KafkaConsumerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${kafka.topics.user-events}")
    @Async
    public void listenUserCreateDeleteEvents(String event) throws MessagingException {
        LOGGER.info("receive message from kafka thread : {}", Thread.currentThread().getName());
        notificationService.sendNotification(event);
    }
}
