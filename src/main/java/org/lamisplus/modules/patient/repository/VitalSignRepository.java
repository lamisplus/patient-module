package org.lamisplus.modules.patient.repository;

import com.foreach.across.modules.hibernate.jpa.repositories.CommonJpaRepository;
import org.lamisplus.modules.patient.domain.entity.VitalSign;

import java.util.List;

public interface VitalSignRepository extends CommonJpaRepository<VitalSign, Long> {
    List<VitalSign> getVitalSignByArchived(Integer archived);
}
