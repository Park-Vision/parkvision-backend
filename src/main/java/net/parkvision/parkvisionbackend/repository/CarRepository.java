package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
