package ru.aston.notification_service_spring_boot.controller;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.notification_service_spring_boot.service.NotificationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void sendNotification_validRequest() throws Exception {
        String event = "CREATE:itclasszoom33@mail.ru";

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.TEXT_PLAIN)
                .content(event))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent"));

        verify(notificationService).sendNotification("CREATE:itclasszoom33@mail.ru");
    }

    @Test
    void sendNotification_inValidRequest() throws Exception {
        String event = "CREATEitclasszoom33@mail.ru";

        doThrow(new RuntimeException("Service error")).when(notificationService).sendNotification(event);

        mockMvc.perform(post("/notifications/send")
                .contentType(MediaType.TEXT_PLAIN)
                .content(event))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }

}