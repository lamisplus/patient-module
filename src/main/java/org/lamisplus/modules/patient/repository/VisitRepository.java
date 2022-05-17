package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByArchived(Integer archived);
}
