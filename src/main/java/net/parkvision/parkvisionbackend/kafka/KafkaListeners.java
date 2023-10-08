package net.parkvision.parkvisionbackend.kafka;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
public class KafkaListeners {
    @KafkaListener(topics = "drone-start", groupId = "123")
    public void listenGroupFoo(String message, @Header(KafkaHeaders.RECEIVED_KEY) Integer key) {
        System.out.println(key);
        System.out.println("Received Message in group foo: " + message);
    }
}
