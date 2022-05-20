package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findAllByArchived(Integer archived);

    List<Encounter> findAllByServiceCodeAndStatus(String serviceCode, String status);

}
