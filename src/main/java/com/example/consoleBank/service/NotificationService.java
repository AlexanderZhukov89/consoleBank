package com.example.consoleBank.service;

import com.example.consoleBank.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.notification-topic:bank-notifications}")
    private String notificationTopic;

    public void sendNotification(NotificationMessage message) {
        try {
            kafkaTemplate.send(notificationTopic, message.getMessageId(), message);
            log.info("Notification sent to Kafka: {}", message.getType());
        } catch (Exception e) {
            log.error("Failed to send notification to Kafka", e);
        }
    }

}
