package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
}
