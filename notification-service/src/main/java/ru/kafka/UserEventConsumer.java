package ru.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.john.dto.UserEvent;
import ru.john.service.EmailService;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(UserEvent event) {
        if ("CREATE".equals(event.getOperation())) {
            emailService.send(event.getEmail(), "Аккаунт создан",
                    "Здравствуйте! Ваш аккаунт был успешно создан.");
        } else if ("DELETE".equals(event.getOperation())) {
            emailService.send(event.getEmail(), "Аккаунт удалён",
                    "Здравствуйте! Ваш аккаунт был удалён.");
        }
    }
}