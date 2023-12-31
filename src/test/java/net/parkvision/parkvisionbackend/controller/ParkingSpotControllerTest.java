package net.parkvision.parkvisionbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingManager;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import net.parkvision.parkvisionbackend.service.ParkingService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ActiveProfiles("dev")
@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest
public class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ParkingSpotController parkingSpotController;
    @MockBean
    private ParkingSpotService parkingSpotService;
    @MockBean
    private ParkingService parkingService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testUser")
    public void getAllParkingSpots_ReturnsAllParkingSpots() throws Exception {
        // Mock data
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setActive(false);
        parkingSpot.setParking(new Parking());
        parkingSpot.setPoints(new ArrayList<>());
        List<ParkingSpot> parkingSpotList = List.of(parkingSpot);

        when(parkingSpotService.getAllParkingSpotsWithPoints()).thenReturn(parkingSpotList);

        mockMvc.perform(get("/api/parkingspots")
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(false));
    }

    @Test
    public void createParkingSpot_CreatesNewParkingSpot() throws Exception {
        Optional<User> parkingManager = userRepository.findByEmail("string@wp.pl");

        ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setActive(false);
        ParkingDTO parkingDTO = new ParkingDTO();
        parkingDTO.setId(4L);
        parkingSpotDTO.setParkingDTO(parkingDTO);
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        when(parkingSpotService.createParkingSpot(any())).thenReturn(parkingSpotController.convertToEntity(parkingSpotDTO));

        mockMvc.perform(post("/api/parkingspots")
                        .with(user(parkingManagerReal))
                        .content(asJsonString(parkingSpotDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void createParkingSpot_CreatesNewParkingSpotFailed() throws Exception {
        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setActive(false);
        parkingSpotDTO.setParkingDTO(new ParkingDTO());
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        when(parkingSpotService.createParkingSpot(any())).thenReturn(parkingSpotController.convertToEntity(parkingSpotDTO));

        mockMvc.perform(post("/api/parkingspots")
                        .content(asJsonString(parkingSpotDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void updateParkingSpot_UpdateParkingSpotFailed() throws Exception {

        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setActive(false);
        parkingSpotDTO.setParkingDTO(new ParkingDTO());
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        when(parkingSpotService.updateParkingSpot(any())).thenReturn(parkingSpotController.convertToEntity(parkingSpotDTO));

        mockMvc.perform(put("/api/parkingspots")
                        .content(asJsonString(parkingSpotDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateParkingSpot_UpdateParkingSpot() throws Exception {

        Optional<User> parkingManager = userRepository.findByEmail("string@wp.pl");

        ParkingManager parkingManagerReal = (ParkingManager) parkingManager.get();
        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setActive(true);
        ParkingDTO parkingDTO = new ParkingDTO();
        parkingDTO.setId(4L);
        parkingSpotDTO.setParkingDTO(parkingDTO);
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        when(parkingSpotService.updateParkingSpot(any())).thenReturn(parkingSpotController.convertToEntity(parkingSpotDTO));

        mockMvc.perform(put("/api/parkingspots")
                        .with(user(parkingManagerReal))
                        .content(asJsonString(parkingSpotDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

    }

    @Test
    @WithMockUser(username = "testUser")
    public void getSpotsByParkingId_ReturnsParkingSpots() throws Exception {
        Long parkingId = 1L;
        Parking parking = new Parking();

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setParking(parking);
        parkingSpot.setPoints(new ArrayList<>());
        parkingSpot.setActive(false);


        when(parkingService.getParkingById(parkingId)).thenReturn(Optional.of(parking));
        when(parkingSpotService.getParkingSpotsWithPoints(parking)).thenReturn(Collections.singletonList(parkingSpot));

        mockMvc.perform(get("/api/parkingspots/parking/{id}", parkingId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(false));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"PARKING_MANAGER"})
    public void createParkingModel_CreatesParkingSpots() throws Exception {
        Long parkingId = 1L;
        Parking parking = new Parking();
        ParkingDTO parkingDTO = new ParkingDTO();
        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setParkingDTO(parkingDTO);
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        parkingSpotDTO.setActive(false);
        parkingSpotDTO.setActive(false);
        List<ParkingSpotDTO> parkingSpotDTOList = Collections.singletonList(parkingSpotDTO);
        List<ParkingSpot> parkingSpotList =
                Collections.singletonList(parkingSpotController.convertToEntity(parkingSpotDTO));

        when(parkingService.getParkingById(parkingId)).thenReturn(Optional.of(parking));
        when(parkingSpotService.createParkingSpots(anyList()))
                .thenReturn(parkingSpotList);

        mockMvc.perform(post("/api/parkingspots/parking/{id}/model/create", parkingId)
                        .content(asJsonString(parkingSpotDTOList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(false));
    }


    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}