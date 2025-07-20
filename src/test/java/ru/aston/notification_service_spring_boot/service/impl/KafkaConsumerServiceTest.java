package ru.aston.notification_service_spring_boot.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.notification_service_spring_boot.service.NotificationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    void listenUserCreateDeleteEvents_callsSendNotification() throws Exception {
        String event = "CREATE:ignat@gmail.com";

        doNothing().when(notificationService).sendNotification(event);

        kafkaConsumerService.listenUserCreateDeleteEvents(event);

        verify(notificationService, times(1)).sendNotification(event);
    }
}