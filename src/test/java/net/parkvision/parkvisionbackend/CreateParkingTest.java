package net.parkvision.parkvisionbackend;


import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingManager;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetTime;
import java.time.ZoneOffset;
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
public class CreateParkingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Test
    public void createParkingWithManager() throws Exception {
        Optional<User> parkingManager = userRepository.findByEmail("string2");

        if(parkingManager.isPresent()){
            ParkingDTO parkingDTO = getParkingDTO();

            ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
            mockMvc.perform(post("/api/parkings")
                            .with(user(parkingManagerReal))
                            .content(asJsonString(parkingDTO))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(parkingDTO.getName()))
                    .andExpect(jsonPath("$.street").value(parkingDTO.getStreet()))
                    .andExpect(jsonPath("$.zipCode").value(parkingDTO.getZipCode()))
                    .andExpect(jsonPath("$.endTime").value(parkingDTO.getEndTime().toString()))
                    .andExpect(jsonPath("$.startTime").value(parkingDTO.getStartTime().toString()))
                    .andExpect(jsonPath("$.timeZone").value(parkingDTO.getTimeZone().toString()))
                    .andExpect(jsonPath("$.city").value(parkingDTO.getCity()))
                    .andExpect(jsonPath("$.latitude").value(parkingDTO.getLatitude()))
                    .andExpect(jsonPath("$.longitude").value(parkingDTO.getLongitude()))
                    .andExpect(jsonPath("$.currency").value(parkingDTO.getCurrency()))
                    .andExpect(jsonPath("$.costRate").value(parkingDTO.getCostRate()));
            Optional<Parking> byName = parkingRepository.findByName(getParkingDTO().getName());

            assertTrue(byName.isPresent());
            Optional<User> parkingManagerAfterParkingCreate = userRepository.findByEmail("string2");
            ParkingManager parkingManagerAfterParkingCreateReal = (ParkingManager) parkingManagerAfterParkingCreate.get();
            assertEquals(parkingManagerAfterParkingCreateReal.getParking().getName(), byName.get().getName());
        }
    }

    private static ParkingDTO getParkingDTO() {
        ParkingDTO parkingDTO = new ParkingDTO();
        parkingDTO.setCity("Wroclaw");
        parkingDTO.setDescription("new parking");
        parkingDTO.setLatitude(25.00);
        parkingDTO.setLongitude(25.01);
        parkingDTO.setCurrency("PLN");
        parkingDTO.setCostRate(3);
        parkingDTO.setTimeZone(ZoneOffset.MIN);
        parkingDTO.setStartTime(OffsetTime.now());
        parkingDTO.setEndTime(OffsetTime.now());
        parkingDTO.setName("ParkingPWR");
        parkingDTO.setStreet("Nowa");
        parkingDTO.setZipCode("100-100");
        return parkingDTO;
    }

    @Test
    public void createParkingWithModeratorWithParking() throws Exception {
        Optional<User> parkingManager = userRepository.findByEmail("string@wp.pl");

        if(parkingManager.isPresent()){
            ParkingDTO parkingDTO = new ParkingDTO();
            parkingDTO.setName("test");

            ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
            mockMvc.perform(post("/api/parkings")
                            .with(user(parkingManagerReal))
                            .content(asJsonString(parkingDTO))
                            .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isMethodNotAllowed());

            Optional<Parking> byName = parkingRepository.findByName(getParkingDTO().getName());

            assertFalse(byName.isPresent());
            assertNotEquals(parkingManagerReal.getParking().getName(), parkingDTO.getName());
        }
    }
}
