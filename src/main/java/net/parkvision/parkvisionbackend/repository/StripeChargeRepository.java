package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.StripeCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeChargeRepository extends JpaRepository<StripeCharge, Long> {
}
