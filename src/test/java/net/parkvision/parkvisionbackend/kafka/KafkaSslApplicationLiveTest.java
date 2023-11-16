package net.parkvision.parkvisionbackend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.parkvision.parkvisionbackend.ParkVisionBackendApplication;
import net.parkvision.parkvisionbackend.dto.ParkingSpotCoordinatesDTO;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("dev")
@DirtiesContext
@SpringBootTest
class KafkaSslApplicationLiveTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DroneMissionRepository droneMissionRepository;

    @Test
    void givenSslIsConfigured_whenProducerSendsMessageOverSsl_thenConsumerReceivesOverSsl() throws JsonProcessingException, InterruptedException {
        String message = generateSampleMessage();
        //Thread.sleep(4000);
        kafkaTemplate.send("drones-info", String.valueOf(1), message);
        Thread.sleep(4000);
        assertEquals(droneMissionRepository.count(), 1);

    }

    private static String generateSampleMessage() throws JsonProcessingException {

        Map<String, Object> map = new HashMap<>();
        map.put("command", "start");
        map.put("cords", new ArrayList<>());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }
}