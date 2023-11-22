package net.parkvision.parkvisionbackend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.time.Instant;
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
        if (drone.isPresent()) {
            System.out.println("Received Message from Drone " + droneId + ": " + message);
            try {
                //message = MessageEncryptor.decryptMessage(message, drone.get().getDroneKey());
                //System.out.println(message);
                Map result = new ObjectMapper().readValue(message, HashMap.class);

                if (result.containsKey("status")) {
                    System.out.println("creating mission");
                    DroneMission droneMission = new DroneMission();
                    droneMission.setDrone(drone.get());
                    droneMission.setParking(drone.get().getParking());
                    droneMission.setMissionStartDate(Instant.ofEpochSecond(Long.parseLong(String.valueOf(result.get(
                            "start_timestamp")))).atOffset(drone.get().getParking().getTimeZone()));
                    droneMission.setMissionEndDate(Instant.ofEpochSecond(Long.parseLong(String.valueOf(result.get(
                            "end_timestamp")))).atOffset(drone.get().getParking().getTimeZone()));
                    droneMission.setStatus((String) result.get("status"));

                    List<MissionSpotResult> missionSpotResultList = new ArrayList<>();
                    List<Map<String, Object>> free_spots = (List<Map<String, Object>>) result.get("free_spots");
                    for (Map minimap : free_spots) {
                        MissionSpotResult missionSpotResult = new MissionSpotResult();
                        missionSpotResult.setParkingSpot(parkingSpotService.getParkingSpotById(Long.parseLong(String.valueOf(minimap.get(
                                "parking_spot_id")))).get());
                        missionSpotResult.setOccupied((Boolean) minimap.get("occupied"));
                        missionSpotResultList.add(missionSpotResult);
                    }

                    droneMission.setMissionSpotResultList(missionSpotResultList);
                    droneMissionService.createDroneMission(droneMission);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String finalMessage = message;
            drone.ifPresent(value -> template.convertAndSend("/topic/drones/" + value.getId(),
                    finalMessage));
        }
    }
}
