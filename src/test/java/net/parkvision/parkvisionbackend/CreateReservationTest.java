package net.parkvision.parkvisionbackend;

import com.jayway.jsonpath.JsonPath;
import net.parkvision.parkvisionbackend.controller.ParkingSpotController;
import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.dto.ReservationDTO;
import net.parkvision.parkvisionbackend.dto.UserDTO;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static net.parkvision.parkvisionbackend.controller.ParkingSpotControllerTest.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DirtiesContext
@SpringBootTest
@AutoConfigureMockMvc
public class CreateReservationTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ParkingSpotController parkingSpotController;
    @Autowired
    private ParkingSpotService parkingSpotService;
    @Test
    public void createReservationTest() throws Exception {
        Optional<User> client = userRepository.findByEmail("anna@onet.pl");
        ReservationDTO reservationDTO = getReservationDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(client.get().getId());
        reservationDTO.setUserDTO(userDTO);
        ParkingSpotDTO parkingSpotDTO =
                parkingSpotController.convertToDto(parkingSpotService.getParkingSpotById(1L).get());

        reservationDTO.setParkingSpotDTO(parkingSpotDTO);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .with(user(client.get()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reservationDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationNumber").value(reservationDTO.getRegistrationNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userDTO").value(userDTO))
                .andExpect(MockMvcResultMatchers.jsonPath("$.parkingSpotDTO.id").value(parkingSpotDTO.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(reservationDTO.getAmount()))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Integer idResponse = JsonPath.read(jsonResponse, "$.id");
        String startDateResponse = JsonPath.read(jsonResponse, "$.startDate");
        String endDateResponse = JsonPath.read(jsonResponse, "$.endDate");
        assertEquals(reservationDTO.getStartDate().toString().replace("0", ""), startDateResponse.replace("0",""));
        assertEquals(reservationDTO.getEndDate().toString().replace("0", ""), endDateResponse.replace("0",""));
        Optional<Reservation> byId = reservationRepository.findById(Long.valueOf(idResponse));
        assertTrue(byId.isPresent());
        assertEquals(byId.get().getParkingSpot().getId(), 1L);
        assertEquals(byId.get().getUser().getId(), client.get().getId());
    }

    private ReservationDTO getReservationDTO() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setStartDate(OffsetDateTime.now(ZoneOffset.of("+1")).plusDays(1).withHour(11));
        reservationDTO.setEndDate(OffsetDateTime.now(ZoneOffset.of("+1")).plusDays(1).withHour(12));
        reservationDTO.setRegistrationNumber("ABC123");
        reservationDTO.setAmount(20.0);
        return reservationDTO;
    }
}
