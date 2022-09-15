package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findAllByArchived(Integer archived);

    List<Encounter> findAllByServiceCodeAndStatus(String serviceCode, String status);

    Optional<Encounter> getEncounterByVisitAndStatusAndServiceCode(Visit visit, String status, String serviceCode);

    List<Encounter> getEncounterByPersonAndArchived(Person person, Integer archived);

    List<Encounter> getEncounterByVisit(Visit visit);

    List<Encounter> findByPerson(Person person);


}
