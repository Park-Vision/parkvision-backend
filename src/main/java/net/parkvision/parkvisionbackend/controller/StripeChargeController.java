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
    private final StripeChargeService stripeChargeService;

    private final ReservationService reservationService;
    private final ModelMapper modelMapper;
    private final EmailSenderService emailSenderService;

    public StripeChargeController(StripeChargeService stripeChargeService, ReservationService reservationService,
                                  ModelMapper modelMapper, EmailSenderService emailSenderService) {
        this.stripeChargeService = stripeChargeService;
        this.reservationService = reservationService;
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
        return stripeChargeService.getAllStripeCharges().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StripeChargeDTO> getStripeChargeById(@PathVariable Long id) {
        Optional<StripeCharge> stripeCharge = stripeChargeService.getStripeChargeById(id);
        return stripeCharge.map(charge -> ResponseEntity.ok(convertToDto(charge))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<StripeChargeDTO> createStripeCharge(@RequestBody StripeChargeDTO stripeChargeDTO) {
        User user = RequestContext.getUserFromRequest();
        StripeCharge createdStripeCharge = stripeChargeService.createStripeCharge(convertToEntity(stripeChargeDTO), stripeChargeDTO.getReservation().getId());
        Optional<Reservation> reservation =
                reservationService.getReservationById(stripeChargeDTO.getReservation().getId());
        if (reservation.isPresent()) {
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            if (user.getRole().equals(Role.USER)) {
                try {
                    if (createdStripeCharge.getSuccess()) {
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
                        createdStripeCharge.getPayment().setReservation(null);
                        emailSenderService.sendHtmlEmailPayment(
                                user.getFirstname(),
                                user.getLastname(),
                                user.getEmail(),
                                "Payment declined",
                                "Payment for your reservation was declined. Please try again.",
                                createdStripeCharge,
                                reservation.get(),
                                "ParkVision payment declined");
                        reservationService.deleteReservationWithCharge(reservation.get().getId());
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
        StripeCharge updatedStripeCharge = stripeChargeService.refundCharge(id);
        return ResponseEntity.ok(convertToDto(updatedStripeCharge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStripeCharge(@PathVariable Long id) {
        stripeChargeService.deleteStripeCharge(id);
        return ResponseEntity.noContent().build();
    }
}
