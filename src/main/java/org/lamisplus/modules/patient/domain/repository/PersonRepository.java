package org.lamisplus.modules.patient.domain.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> getAllByArchived(int i);
}
