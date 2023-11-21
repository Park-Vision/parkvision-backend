package net.parkvision.parkvisionbackend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.model.MissionSpotResult;
import net.parkvision.parkvisionbackend.service.DroneMissionService;
import net.parkvision.parkvisionbackend.service.DroneService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;

@Component
public class KafkaListeners {
    private final SimpMessagingTemplate template;
    private final DroneService droneService;
    private final DroneMissionService droneMissionService;
    private final ParkingSpotService parkingSpotService;

    public KafkaListeners(SimpMessagingTemplate template, DroneService droneService,
                          DroneMissionService droneMissionService, ParkingSpotService parkingSpotService) {
        this.template = template;
        this.droneService = droneService;
        this.droneMissionService = droneMissionService;
        this.parkingSpotService = parkingSpotService;
    }

    @MessageMapping("/message")
    @KafkaListener(topics = "drones-info", groupId = "parkVision")
    public void infoFromDrones(String message, @Header(KafkaHeaders.RECEIVED_KEY) Integer droneId) {
        Optional<Drone> drone = droneService.getDroneById(Long.valueOf(droneId));
        // System.out.println("Received Message from Drone " + droneId + ": " + message);

        drone.ifPresentOrElse(
            value -> template.convertAndSend("/topic/drones/" + droneId, message),
            () -> System.out.println("Drone not existent, skipping websocket send")
        );
    }
}
