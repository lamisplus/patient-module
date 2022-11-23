package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByArchivedAndFacilityId(Integer archived, Long facilityId);

    List<Visit> getAllByVisitStartDateNotNullAndVisitEndDateIsNull();

    Optional<Visit> findVisitByPersonAndVisitStartDateNotNullAndVisitEndDateIsNull(Person person);

   /// Visit findByPersonAndStatus(Person person);

    List<Visit> findAllByIdAndVisitStartDateNotNullAndVisitEndDateIsNull(Long id);

    Page<Visit> findAllByArchivedAndVisitStartDateNotNullAndVisitEndDateIsNull(Integer archived, Pageable pageable);

   // List<Visit> findAllByArchivedOrderByVisitStartDateDesc(Integer archived);
}
