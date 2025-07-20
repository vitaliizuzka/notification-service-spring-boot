package ru.aston.notification_service_spring_boot.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.aston.notification_service_spring_boot.exception.WrongEmailException;
import ru.aston.notification_service_spring_boot.exception.WrongEventException;
import ru.aston.notification_service_spring_boot.exception.WrongKafkaMessageException;
import ru.aston.notification_service_spring_boot.model.NotificationDetails;


@Service
public class NotificationServiceImpl implements ru.aston.notification_service_spring_boot.service.NotificationService {

    private final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public final String CREATE_MESSAGE = "Здравствуйте! Ваш аккаунт успешно создан";
    public final   String DELETE_MESSAGE = "Здравствуйте! Ваш аккаунт был удалён.";
    @Value("${mail.from.address}")
    public String MAIL_SENDER;
    private final JavaMailSender mailSender;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //   Формат: "EVENT:email"
    private NotificationDetails parseMessage(String parseEvent) {
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

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000),
            retryFor = {MessagingException.class}
    )
    public void sendNotification(String event) throws MessagingException {
        LOGGER.info("trying to send notification service level {}, thread: {}", event, Thread.currentThread().getName());
        try {
            NotificationDetails details = parseMessage(event);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(MAIL_SENDER);
            helper.setTo(details.getEmail());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMessage(), false);

            mailSender.send(message);
            LOGGER.info("notification for {} with subject {} was sent successfully service level", details.getEmail(), details.getSubject());
        }catch (Exception e){
            LOGGER.error("failed send notification service level: {}", e.getMessage());
            throw e;
        }
    }

}
