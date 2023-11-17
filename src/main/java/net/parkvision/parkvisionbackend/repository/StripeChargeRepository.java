package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.StripeCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StripeChargeRepository extends JpaRepository<StripeCharge, Long> {
    Optional<StripeCharge> findByReservationId(Long id);

}
