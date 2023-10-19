package net.parkvision.parkvisionbackend.kafka;

import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.service.DroneService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Component
public class KafkaListeners {
    private final SimpMessagingTemplate template;
    private final DroneService droneService;

    public KafkaListeners(SimpMessagingTemplate template, DroneService droneService) {
        this.template = template;
        this.droneService = droneService;
    }

    @KafkaListener(topics = "drones-info", groupId = "parkVision")
    public void infoFromDrones(String message, @Header(KafkaHeaders.RECEIVED_KEY) Integer droneId) {
        Optional<Drone> drone = droneService.getDroneById(Long.valueOf(droneId));
        System.out.println("Received Message from Drone " + droneId + ": " + message);
        drone.ifPresent(value -> template.convertAndSend("/topic/parkings/" + value.getParking().getId(), message));
    }
}
