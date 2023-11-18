package net.parkvision.parkvisionbackend;


import com.jayway.jsonpath.JsonPath;
import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.kafka.KafkaTopicConfig;
import net.parkvision.parkvisionbackend.model.ParkingModerator;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static net.parkvision.parkvisionbackend.controller.ParkingSpotControllerTest.asJsonString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    KafkaTopicConfig kafkaTopicConfig;

    @Test
    public void createDrone() throws Exception {

        Optional<User> parkingModerator = userRepository.findByEmail("string");

        if (parkingModerator.isPresent()) {
            DroneDTO droneDTO = new DroneDTO();

            ParkingDTO parkingDTO = new ParkingDTO();
            parkingDTO.setId(3L);
            droneDTO.setParkingDTO(parkingDTO);
            droneDTO.setName("DroneDJ3");
            droneDTO.setModel("DJ3");
            droneDTO.setSerialNumber("87324637286432874");

            ParkingModerator parkingModeratorReal = (ParkingModerator) parkingModerator.get();
            MvcResult result = mockMvc.perform(post("/api/drones")
                            .with(user(parkingModeratorReal))
                            .content(asJsonString(droneDTO))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.parkingDTO.id").value(parkingModeratorReal.getParking().getId()))
                    .andExpect(jsonPath("$.name").value(droneDTO.getName()))
                    .andExpect(jsonPath("$.model").value(droneDTO.getModel()))
                    .andExpect(jsonPath("$.serialNumber").value(droneDTO.getSerialNumber()))
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();
            Integer idValue = JsonPath.read(jsonResponse, "$.id");

            assertTrue(kafkaTopicConfig.checkIfTopicExists("drone-" + idValue));
        }

    }

    @Test
    public void createDroneBadParking() throws Exception {

        Optional<User> parkingModerator = userRepository.findByEmail("string");

        if (parkingModerator.isPresent()) {
            DroneDTO droneDTO = new DroneDTO();

            ParkingDTO parkingDTO = new ParkingDTO();
            parkingDTO.setId(2L);
            droneDTO.setParkingDTO(parkingDTO);
            droneDTO.setName("DroneDJ3");
            droneDTO.setModel("DJ3");
            droneDTO.setSerialNumber("87324637286432874");

            ParkingModerator parkingModeratorReal = (ParkingModerator) parkingModerator.get();
            mockMvc.perform(post("/api/drones")
                            .with(user(parkingModeratorReal))
                            .content(asJsonString(droneDTO))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isBadRequest());

        }
    }

}
