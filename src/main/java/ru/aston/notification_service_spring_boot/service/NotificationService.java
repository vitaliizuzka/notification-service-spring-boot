package ru.aston.notification_service_spring_boot.service;

import jakarta.mail.MessagingException;

public interface NotificationService {
    void sendNotification(String event) throws MessagingException;
}
