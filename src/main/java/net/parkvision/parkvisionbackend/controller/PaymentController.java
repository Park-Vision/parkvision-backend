package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.PaymentDTO;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentController(PaymentService paymentService, ModelMapper modelMapper) {
        this.paymentService = paymentService;
        this.modelMapper = modelMapper;
    }

    private PaymentDTO convertToDto(Payment payment) {
        return modelMapper.map(payment, PaymentDTO.class);
    }

    private Payment convertToEntity(PaymentDTO paymentDTO) {
        return modelMapper.map(paymentDTO, Payment.class);
    }

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(value -> ResponseEntity.ok(convertToDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        Payment createdPayment = paymentService.createPayment(convertToEntity(paymentDTO));
        return ResponseEntity.ok(convertToDto(createdPayment));
    }

    @PutMapping
    public ResponseEntity<PaymentDTO> updatePayment(@RequestBody PaymentDTO paymentDTO) {
        Payment updatedPayment = paymentService.updatePayment(convertToEntity(paymentDTO));
        return ResponseEntity.ok(convertToDto(updatedPayment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
