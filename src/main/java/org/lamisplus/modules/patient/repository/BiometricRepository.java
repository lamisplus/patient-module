package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Biometric;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiometricRepository extends JpaRepository<Biometric, String> {
    List<Biometric> findAllByPerson(Person person);
}
