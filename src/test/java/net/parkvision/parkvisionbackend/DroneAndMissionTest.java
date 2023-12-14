package net.parkvision.parkvisionbackend;


import com.jayway.jsonpath.JsonPath;
import net.parkvision.parkvisionbackend.config.MessageEncryptor;
import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.kafka.KafkaTopicConfig;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.ParkingManager;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static net.parkvision.parkvisionbackend.controller.ParkingSpotControllerTest.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest
@AutoConfigureMockMvc
public class DroneAndMissionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private DroneMissionRepository droneMissionRepository;

    @Autowired
    KafkaTopicConfig kafkaTopicConfig;

    @Test
    public void createDroneAndStartMission() throws Exception {

        Optional<User> parkingManager = userRepository.findByEmail("string@wp.pl");

        if (parkingManager.isPresent()) {
            DroneDTO droneDTO = new DroneDTO();

            ParkingDTO parkingDTO = new ParkingDTO();
            parkingDTO.setId(4L);
            droneDTO.setParkingDTO(parkingDTO);
            droneDTO.setName("DroneDJ3");
            droneDTO.setModel("DJ3");
            droneDTO.setSerialNumber("87324637286432874");

            ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
            try (MockedStatic<MessageEncryptor> utilities = Mockito.mockStatic(MessageEncryptor.class)) {
                utilities.when(MessageEncryptor::generateKey).thenReturn("0Yk6XInH5GvWDJooOcHAuQ==");
                MvcResult result = mockMvc.perform(post("/api/drones")
                                .with(user(parkingManagerReal))
                                .content(asJsonString(droneDTO))
                                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.parkingDTO.id").value(parkingManagerReal.getParking().getId()))
                        .andExpect(jsonPath("$.name").value(droneDTO.getName()))
                        .andExpect(jsonPath("$.model").value(droneDTO.getModel()))
                        .andExpect(jsonPath("$.serialNumber").value(droneDTO.getSerialNumber()))
                        .andReturn();

                String jsonResponse = result.getResponse().getContentAsString();
                Integer idValue = JsonPath.read(jsonResponse, "$.id");

                assertTrue(kafkaTopicConfig.checkIfTopicExists("drone-" + idValue));
                Optional<Drone> byName = droneRepository.findByName(droneDTO.getName());

                assertTrue(byName.isPresent());
                assertEquals(byName.get().getParking().getName(), parkingManagerReal.getParking().getName());

            }
            mockMvc.perform(post("/api/drones/3/start")
                            .with(user(parkingManagerReal))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isOk());

            Thread.sleep(60000);
            assertEquals(droneMissionRepository.count(), 1);
        }
    }

    @Test
    public void createDroneBadParking() throws Exception {

        Optional<User> parkingManager = userRepository.findByEmail("string@wp.pl");

        if (parkingManager.isPresent()) {
            DroneDTO droneDTO = new DroneDTO();

            ParkingDTO parkingDTO = new ParkingDTO();
            parkingDTO.setId(2L);
            droneDTO.setParkingDTO(parkingDTO);
            droneDTO.setName("DroneDJ4");
            droneDTO.setModel("DJ3");
            droneDTO.setSerialNumber("87324637286432874");

            ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
            mockMvc.perform(post("/api/drones")
                            .with(user(parkingManagerReal))
                            .content(asJsonString(droneDTO))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isBadRequest());

            Optional<Drone> byName = droneRepository.findByName(droneDTO.getName());

            assertFalse(byName.isPresent());
        }
    }

}
