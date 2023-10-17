package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByClientId(Long id);
}
