package org.lamisplus.modules.patient.repository;

import com.foreach.across.modules.hibernate.jpa.repositories.IdBasedEntityJpaRepository;
import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VitalSignRepository extends IdBasedEntityJpaRepository<VitalSign> {
     List<VitalSign> getVitalSignByArchived(Integer archived);
}
