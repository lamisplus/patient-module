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
            value ="SELECT p.* from patient_person p JOIN (select hospital_number FROM patient_person Group by hospital_number HAVING count(hospital_number) > 1) b on p.hospital_number = b.hospital_number ORDER BY hospital_number", nativeQuery = true)
    List<Person> findDuplicate();
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

    Page<Person> getAllByArchivedAndFacilityIdOrderByIdDesc (Integer archived, Long facilityId, Pageable pageable);


    @Query(value = "SELECT * FROM patient_person WHERE (first_name ilike ?1 OR surname ilike ?1 " +
            "OR other_name ilike ?1 OR hospital_number ilike ?1) AND archived=?2 AND facility_id=?3", nativeQuery = true)
    Page<Person> findAllPersonBySearchParameters(String queryParam, Integer archived, Long facilityId, Pageable pageable);
}


