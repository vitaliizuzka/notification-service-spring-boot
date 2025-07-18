package ru.aston.notification_service_spring_boot.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.aston.notification_service_spring_boot.exception.WrongEmailException;
import ru.aston.notification_service_spring_boot.exception.WrongEventException;
import ru.aston.notification_service_spring_boot.exception.WrongKafkaMessageException;
import ru.aston.notification_service_spring_boot.model.NotificationDetails;
import ru.aston.notification_service_spring_boot.service.impl.NotificationServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    public void setup(){
        notificationService=new NotificationServiceImpl(mailSender);
    }

    @Test
    void sendNotification_createEvent_sendEmail() throws MessagingException {
        String event = "CREATE:itclasszoom33@mail.ru";

        notificationService.sendNotification(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage simpleMailMessage = captor.getValue();

        assertEquals("CREATE", simpleMailMessage.getSubject());
        assertEquals("itclasszoom33@mail.ru", simpleMailMessage.getTo()[0]);
        assertEquals(notificationService.CREATE_MESSAGE, simpleMailMessage.getText());
    }

    @Test
    void sendNotification_parseMessage_validCreateEvent(){
        String event = "CREATE:itclasszoom33@mail.ru";

        NotificationDetails details = notificationService.parseMessage(event);

        assertEquals("CREATE", details.getSubject());
        assertEquals("itclasszoom33@mail.ru", details.getEmail());
        assertEquals(notificationService.CREATE_MESSAGE, details.getMessage());
    }

    @Test
    void sendNotification_parseMessage_validDeleteEvent(){
        String event = "DELETE:itclasszoom33@mail.ru";

        NotificationDetails details = notificationService.parseMessage(event);

        assertEquals("DELETE", details.getSubject());
        assertEquals("itclasszoom33@mail.ru", details.getEmail());
        assertEquals(notificationService.DELETE_MESSAGE, details.getMessage());
    }

    @Test
    void sendNotification_parseMessage_invalidFormat(){
        String event = "DELETEitclasszoom33@mail.ru";

       assertThrows(WrongKafkaMessageException.class, () -> notificationService.sendNotification(event));
    }

    @Test
    void sendNotification_parseMessage_invalidEmail(){
        String event = "DELETE:itclasszoom33-mail.ru";

        assertThrows(WrongEmailException.class, () -> notificationService.sendNotification(event));
    }

    @Test
    void sendNotification_parseMessage_invalidEvent(){
        String event = "UPDATE:itclasszoom33@mail.ru";

        assertThrows(WrongEventException.class, () -> notificationService.sendNotification(event));
    }


}