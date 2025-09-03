package ru.john.kafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.john.dto.UserEvent;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;


    public void send (UserEvent userEvent){
        kafkaTemplate.send("user-events", userEvent);
    }
}
