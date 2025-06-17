package ru.aston.notification_service_spring_boot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.notification_service_spring_boot.exception.WrongEmailException;
import ru.aston.notification_service_spring_boot.exception.WrongEventException;
import ru.aston.notification_service_spring_boot.exception.WrongKafkaMessageException;
import ru.aston.notification_service_spring_boot.model.NotificationDetails;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationService {

    private final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String CREATE_MESSAGE = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан";
    public static final String DELETE_MESSAGE = "Здравствуйте! Ваш аккаунт был удалён";

    @KafkaListener(topics = "user-create-delete-events-topic")
    public void listenUserCreateDeleteEvents(String event) {
        LOGGER.info("receive message from kafka listener level: {}", event);
        sendNotification(event);
    }

    //   Формат сообщения: "EVENT:email"
    public NotificationDetails parseMessage(String parseEvent) {
        NotificationDetails details = new NotificationDetails();
        String[] parts = parseEvent.split(":");
        if (parts.length != 2)
            throw new WrongKafkaMessageException(parseEvent);
        if (!parts[1].matches(EMAIL_REGEX))
            throw new WrongEmailException(parts[1]);
        details.setEmail(parts[1]);
        details.setSubject(parts[0]);
        details.setMessage(switch (parts[0]) {
            case "CREATE" -> CREATE_MESSAGE;
            case "DELETE" -> DELETE_MESSAGE;
            default -> throw new WrongEventException(parts[0]);
        });
        return details;
    }

    public void sendNotification(String event) {
        LOGGER.info("trying to send notification service level {}", event);
        try {
            NotificationDetails details = parseMessage(event);
            LOGGER.info("notification for {} with subject {} was sent successfully service level", details.getEmail(), details.getSubject());
        }catch (Exception e){
            LOGGER.error("failed send notification service level: {}", e.getMessage());
            throw e;
        }
    }

}
