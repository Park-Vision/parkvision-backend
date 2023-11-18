package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.StripeChargeDTO;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/charge")
public class StripeChargeController {
    private final StripeChargeService _stripeChargeService;
    private final ParkingSpotService _parkingSpotService;

    private final ReservationService _reservationService;
    private final ModelMapper modelMapper;
    private final EmailSenderService emailSenderService;

    public StripeChargeController(StripeChargeService stripeChargeService, ParkingSpotService parkingSpotService, ReservationService reservationService, ModelMapper modelMapper, EmailSenderService emailSenderService) {
        _stripeChargeService = stripeChargeService;
        _parkingSpotService = parkingSpotService;
        _reservationService = reservationService;
        this.emailSenderService = emailSenderService;
        this.modelMapper = modelMapper;
    }

    private StripeChargeDTO convertToDto(StripeCharge stripeCharge) {
        return modelMapper.map(stripeCharge, StripeChargeDTO.class);
    }

    private StripeCharge convertToEntity(StripeChargeDTO stripeChargeDTO) {
        return modelMapper.map(stripeChargeDTO, StripeCharge.class);
    }

    @GetMapping
    public List<StripeChargeDTO> getAllStripeCharges() {
        return _stripeChargeService.getAllStripeCharges().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StripeChargeDTO> getStripeChargeById(@PathVariable Long id) {
        Optional<StripeCharge> stripeCharge = _stripeChargeService.getStripeChargeById(id);
        return stripeCharge.map(charge -> ResponseEntity.ok(convertToDto(charge))).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<StripeChargeDTO> createStripeCharge(@RequestBody StripeChargeDTO stripeChargeDTO){
        User user = RequestContext.getUserFromRequest();
        StripeCharge createdStripeCharge = _stripeChargeService.createStripeCharge(convertToEntity(stripeChargeDTO));
        Optional<Reservation> reservation = _reservationService.getReservationById(createdStripeCharge.getReservation().getId());
        if (reservation.isPresent()){
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            if(user.getRole().equals(Role.USER)) {
                try {
                    if (createdStripeCharge.getSuccess()){
                        emailSenderService.sendHtmlEmailReservation(
                                user.getFirstname(),
                                user.getLastname(),
                                user.getEmail(),
                                "Reservation confirmation",
                                "Here is the confirmation of the reservation you made in our system. ",
                                reservation.get().getParkingSpot().getParking(),
                                reservation.get(), "ParkVision reservation confirmation");

                        emailSenderService.sendHtmlEmailPayment(
                                user.getFirstname(),
                                user.getLastname(),
                                user.getEmail(),
                                "Payment confirmation",
                                "Here is the confirmation of the payment for your reservation.",
                                createdStripeCharge,
                                reservation.get(),
                                "ParkVision payment confirmation");
                    } else {
                        createdStripeCharge.setReservation(null);
                        emailSenderService.sendHtmlEmailPayment(
                                user.getFirstname(),
                                user.getLastname(),
                                user.getEmail(),
                                "Payment declined",
                                "Payment for your reservation was declined. Please try again.",
                                createdStripeCharge,
                                reservation.get(),
                                "ParkVision payment declined");
                        _reservationService.deleteReservation(reservation.get().getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("return ok");
        return ResponseEntity.ok(convertToDto(createdStripeCharge));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StripeChargeDTO> refundCharge(@PathVariable Long id) {
        StripeCharge updatedStripeCharge = _stripeChargeService.refundCharge(id);
        return ResponseEntity.ok(convertToDto(updatedStripeCharge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStripeCharge(@PathVariable Long id) {
        _stripeChargeService.deleteStripeCharge(id);
        return ResponseEntity.noContent().build();
    }
}
