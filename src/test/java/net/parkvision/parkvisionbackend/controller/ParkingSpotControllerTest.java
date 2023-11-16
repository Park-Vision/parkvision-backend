package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
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
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void getAllParkingSpots_ReturnsAllParkingSpots() throws Exception {
        // Mock data
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setActive(false);
        parkingSpot.setParking(new Parking());
        parkingSpot.setPoints(new ArrayList<>());
        List<ParkingSpot> parkingSpotList = List.of(parkingSpot);

        when(parkingSpotService.getAllParkingSpots()).thenReturn(parkingSpotList);

        mockMvc.perform(get("/api/parkingspots")
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(false));
    }
    @Test
    @WithMockUser(username = "testUser", roles = {"PARKING_MANAGER"})
    public void createParkingSpot_CreatesNewParkingSpot() throws Exception {
        ParkingSpotDTO parkingSpotDTO = new ParkingSpotDTO();
        parkingSpotDTO.setActive(false);
        parkingSpotDTO.setParkingDTO(new ParkingDTO());
        parkingSpotDTO.setPointsDTO(new ArrayList<>());
        parkingSpotDTO.setActive(false);
        when(parkingSpotService.createParkingSpot(any())).thenReturn(parkingSpotController.convertToEntity(parkingSpotDTO));

        mockMvc.perform(post("/api/parkingspots")
                        .content(asJsonString(parkingSpotDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}