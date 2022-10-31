package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query(
            value = "SELECT count(*) FROM biometric b WHERE b.person_uuid = ?1",
            nativeQuery = true)
    Integer getBiometricCountByPersonUuid(String uuid);

    @Override
    Optional<Person> findById(Long aLong);

    Optional<Person> getPersonByHospitalNumberAndFacilityId(String hospitalNumber, Long facility);

    Optional<Person> getPersonByHospitalNumber(String hospitalNumber);

    Optional<Person> getPersonByHospitalNumberAndFacilityIdAndArchived(String hospitalNumber, Long facility, Integer archive);



    Optional<Person> getPersonByNinNumberAndFacilityIdAndArchived(String ninNumber, Long facility, Integer archive);

    List<Person>  getPersonByNinNumber(String ninNumber);
    Optional<Person> findPersonByNinNumber (String ninNumber);

    List<Person> getAllByArchivedOrderByDateOfRegistrationDesc(int i);


    Page<Person> getAllByArchivedOrderByIdDesc (Integer archived, Pageable pageable);

}
