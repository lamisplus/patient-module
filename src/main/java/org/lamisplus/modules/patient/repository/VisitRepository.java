package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByArchived(Integer archived);

    List<Visit> getAllByVisitStartDateNotNullAndVisitEndDateIsNull();

    Optional<Visit> findVisitByPersonAndVisitStartDateNotNullAndVisitEndDateIsNull(Person person);

    Optional<Visit> findAllByIdAndVisitStartDateNotNullAndVisitEndDateIsNull(Long id);
}
