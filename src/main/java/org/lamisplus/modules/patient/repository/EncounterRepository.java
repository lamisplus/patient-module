package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
}
