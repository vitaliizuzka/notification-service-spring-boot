package ru.aston.notification_service_spring_boot.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.aston.notification_service_spring_boot.exception.WrongEmailException;
import ru.aston.notification_service_spring_boot.exception.WrongEventException;
import ru.aston.notification_service_spring_boot.exception.WrongKafkaMessageException;
import ru.aston.notification_service_spring_boot.service.impl.NotificationServiceImpl;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationServiceImpl notificationService;

    private GreenMail greenMail;

    @BeforeEach
    void setup() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @AfterEach
    void teardown() {
        greenMail.stop();
    }

    @Test
    void sendNotification_createEvent_sendsEmail() throws Exception {
        String event = "CREATE:itclasszoom32@mail.ru";

        notificationService.sendNotification(event);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertEquals("CREATE", message.getSubject());
        assertEquals("itclasszoom32@mail.ru", message.getAllRecipients()[0].toString());
        assertEquals("itclasszoom25@mail.ru", message.getFrom()[0].toString());
    }

    @Test
    void sendNotification_deleteEvent_sendsEmail() throws Exception {
        String event = "DELETE:itclasszoom32@mail.ru";

        notificationService.sendNotification(event);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertEquals("DELETE", message.getSubject());
        assertEquals("itclasszoom32@mail.ru", message.getAllRecipients()[0].toString());
        assertEquals("itclasszoom25@mail.ru", message.getFrom()[0].toString());
    }

    @Test
    void sendNotification_invalidFormat_throwsException() {
        String invalidEvent = "INVALID_FORMAT";

        assertThrows(WrongKafkaMessageException.class, () -> notificationService.sendNotification(invalidEvent));
    }

    @Test
    void sendNotification_invalidEmail_throwsException() {
        String invalidEmailEvent = "CREATE:invalid-email";

        assertThrows(WrongEmailException.class, () -> notificationService.sendNotification(invalidEmailEvent));
    }

    @Test
    void sendNotification_unknownEvent_throwsException() {
        String unknownEvent = "UPDATE:itclasszoom32@mail.ru";

        assertThrows(WrongEventException.class, () -> notificationService.sendNotification(unknownEvent));
    }

}