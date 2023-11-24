package net.parkvision.parkvisionbackend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.parkvision.parkvisionbackend.config.MessageEncryptor;
import net.parkvision.parkvisionbackend.config.MessageEncryptor;
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
        System.out.println("Received Message from Drone " + droneId + ": " + message);

        try {
            message = MessageEncryptor.decryptMessage(message, drone.get().getDroneKey());
            System.out.println(message);
            Map result = new ObjectMapper().readValue(message, HashMap.class);
            if (result.get("command").equals("start")) {
                DroneMission droneMission = new DroneMission();
                droneMission.setDrone(drone.get());
                droneMission.setParking(drone.get().getParking());
                droneMission.setMissionStartDate(OffsetDateTime.now().minusHours(1));
                droneMission.setMissionEndDate(OffsetDateTime.now());
                droneMission.setStatus("DONE");
                List<MissionSpotResult> missionSpotResultList = new ArrayList<>();
                MissionSpotResult missionSpotResult = new MissionSpotResult();
                missionSpotResult.setParkingSpot(parkingSpotService.getParkingSpotById(1L).get());
                missionSpotResult.setOccupied(true);
                missionSpotResultList.add(missionSpotResult);
                MissionSpotResult missionSpotResult2 = new MissionSpotResult();
                missionSpotResult2.setParkingSpot(parkingSpotService.getParkingSpotById(2L).get());
                missionSpotResult2.setOccupied(false);
                missionSpotResultList.add(missionSpotResult2);

                droneMission.setMissionSpotResultList(missionSpotResultList);
                droneMissionService.createDroneMission(droneMission);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String finalMessage = message;
        drone.ifPresent(value -> template.convertAndSend("/topic/parkings/" + value.getParking().getId(), finalMessage));
    }
}
