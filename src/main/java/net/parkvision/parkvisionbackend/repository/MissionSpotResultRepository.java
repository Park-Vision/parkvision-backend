package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.MissionSpotResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionSpotResultRepository extends JpaRepository<MissionSpotResult, Long> {
}
