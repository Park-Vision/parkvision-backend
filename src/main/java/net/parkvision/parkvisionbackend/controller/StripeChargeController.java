package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.StripeChargeDTO;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.model.StripeCharge;
import net.parkvision.parkvisionbackend.service.StripeChargeService;
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
    private final ModelMapper modelMapper;

    public StripeChargeController(StripeChargeService stripeChargeService, ModelMapper modelMapper) {
        _stripeChargeService = stripeChargeService;
        this.modelMapper = modelMapper;
    }

    private StripeChargeDTO convertToDto(StripeCharge stripeCharge){
        return modelMapper.map(stripeCharge, StripeChargeDTO.class);
    }
    private StripeCharge convertToEntity(StripeChargeDTO stripeChargeDTO){
        return modelMapper.map(stripeChargeDTO, StripeCharge.class);
    }
    @GetMapping
    public List<StripeChargeDTO> getAllStripeCharges(){
        return _stripeChargeService.getAllStripeCharges().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<StripeChargeDTO> getStripeChargeById(@PathVariable Long id){
        Optional<StripeCharge> stripeCharge = _stripeChargeService.getStripeChargeById(id);
        if (stripeCharge.isPresent()) {
            return ResponseEntity.ok(convertToDto(stripeCharge.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public ResponseEntity<StripeChargeDTO> createStripeCharge(@RequestBody StripeChargeDTO stripeChargeDTO){
        StripeCharge createdStripeCharge = _stripeChargeService.createStripeCharge(convertToEntity(stripeChargeDTO));
        return ResponseEntity.ok(convertToDto(createdStripeCharge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStripeCharge(@PathVariable Long id) {
        _stripeChargeService.deleteStripeCharge(id);
        return ResponseEntity.noContent().build();
    }
}
