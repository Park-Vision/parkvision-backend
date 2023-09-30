package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.DroneMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneMissionRepository extends JpaRepository<DroneMission, Long> {
}
