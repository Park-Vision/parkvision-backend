package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.PaymentDTO;
import net.parkvision.parkvisionbackend.dto.ReservationDTO;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.service.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService _paymentService;
    private final ModelMapper _modelMapper;

    public PaymentController(PaymentService paymentService, ModelMapper modelMapper) {
        _paymentService = paymentService;
        _modelMapper = modelMapper;
    }

    private PaymentDTO convertToDto(Payment payment) {
        PaymentDTO paymentDTO = _modelMapper.map(payment, PaymentDTO.class);
        paymentDTO.setReservation(_modelMapper.map(payment.getReservation(), ReservationDTO.class));
        return paymentDTO;
    }

    private Payment convertToEntity(PaymentDTO paymentDTO) {
        return _modelMapper.map(paymentDTO, Payment.class);
    }

    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return _paymentService.getAllPayments().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = _paymentService.getPaymentById(id);
        if (payment.isPresent()) {
            return ResponseEntity.ok(convertToDto(payment.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentDTO paymentDTO) {
        Payment createdPayment = _paymentService.createPayment(convertToEntity(paymentDTO));
        return ResponseEntity.ok(convertToDto(createdPayment));
    }

    @PutMapping
    public ResponseEntity<PaymentDTO> updatePayment(@RequestBody PaymentDTO paymentDTO) {
        Payment updatedPayment = _paymentService.updatePayment(convertToEntity(paymentDTO));
        return ResponseEntity.ok(convertToDto(updatedPayment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        _paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }


}
