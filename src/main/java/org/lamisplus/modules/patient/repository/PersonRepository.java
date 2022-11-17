package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person, Long> {

    @Override
    Optional<Person> findById(Long aLong);

    Optional<Person> getPersonByHospitalNumberAndFacilityId(String hospitalNumber, Long facility);

    List<Person> getPersonByHospitalNumber(String hospitalNumber);

    Optional<Person> getPersonByUuidAndFacilityIdAndArchived(String uuid, Long facility, Integer archive);



    Optional<Person> getPersonByNinNumberAndFacilityIdAndArchived(String ninNumber, Long facility, Integer archive);

    List<Person>  getPersonByNinNumber(String ninNumber);
    Optional<Person> findPersonByNinNumber (String ninNumber);

    List<Person> getAllByArchivedOrderByDateOfRegistrationDesc(int i);


    Page<Person> getAllByArchivedOrderByIdDesc (Integer archived, Pageable pageable);

    Page<Person> getAllByArchivedAndFacilityIdOrderByIdDesc (Integer archived, Long facilityId, Pageable pageable);


    @Query(value = "SELECT * FROM patient_person WHERE (first_name ilike ?1 OR surname ilike ?1 OR other_name ilike ?1 OR full_name ilike ?1 OR hospital_number ilike ?1)  AND archived=?2 AND facility_id=?3", nativeQuery = true)
    Page<Person> findAllPersonBySearchParameters(String queryParam, Integer archived, Long facilityId, Pageable pageable);

    @Query(value ="SELECT p.* from patient_person p JOIN (select hospital_number FROM patient_person b Group by hospital_number HAVING count(hospital_number) > 1) b on p.hospital_number = b.hospital_number WHERE (p.first_name ilike ?1 OR p.surname ilike ?1 OR p.other_name ilike ?1 OR full_name ilike ?1 OR p.hospital_number ilike ?1) AND p.facility_id=?2 ORDER BY p.hospital_number", nativeQuery = true)
    Page<Person> findDuplicatePersonBySearchParameters(String queryParam, Long facilityId, Pageable pageable);

    @Query(value ="SELECT p.* from patient_person p JOIN (select hospital_number FROM patient_person b Group by hospital_number HAVING count(hospital_number) > 1) b on p.hospital_number = b.hospital_number WHERE p.facility_id=?1 ORDER BY p.hospital_number", nativeQuery = true)
    Page<Person> findDuplicatePerson(Long facilityId, Pageable pageable);

    @Query(value = "SELECT count(*) FROM patient_person p WHERE p.archived = 0", nativeQuery = true)
    Integer getTotalRecords();

    @Query(value = "SELECT * FROM patient_person pp INNER JOIN patient_visit pv ON pp.uuid=pv.person_uuid WHERE (first_name ilike ?1 OR surname ilike ?1 OR other_name ilike ?1 OR full_name ilike ?1 OR hospital_number ilike ?1) AND pv.archived=?2 AND pp.archived=?2 AND pp.facility_id=?3 AND pv.visit_end_date is null", nativeQuery = true)
    Page<Person> findCheckedInPersonBySearchParameters(String queryParam, Integer archived, Long facilityId, Pageable pageable);

    @Query(value = "SELECT * FROM patient_person pp INNER JOIN patient_visit pv ON pp.uuid=pv.person_uuid WHERE pv.archived=?1 AND pp.archived=?1 AND pp.facility_id=?2 AND pv.visit_end_date is null", nativeQuery = true)
    Page<Person> findAllCheckedInPerson(Integer archived, Long facilityId, Pageable pageable);

    @Query(
            value = "SELECT count(*) FROM biometric b WHERE b.person_uuid = ?1",
            nativeQuery = true)
    Integer getBiometricCountByPersonUuid(String uuid);

    @Query(value = "SELECT * FROM patient_person pp WHERE (pp.first_name ilike ?1 OR pp.surname ilike ?1 OR pp.other_name ilike ?1 OR pp.full_name ilike ?1 OR pp.hospital_number ilike ?1) AND pp.archived=?2 AND pp.facility_id=?3 AND pp.sex ilike '%FEMALE%' AND (EXTRACT (YEAR FROM now()) - EXTRACT(YEAR FROM pp.date_of_birth) >= 10 ) ORDER BY pp.id desc", nativeQuery = true)
    Page<Person> findFemalePersonBySearchParameters(String queryParam, Integer archived, Long facilityId, Pageable pageable);

    @Query(value = "SELECT * FROM patient_person pp WHERE pp.archived=?1 AND pp.facility_id=?2 AND pp.sex ilike '%FEMALE%' AND (EXTRACT (YEAR FROM now()) - EXTRACT(YEAR FROM pp.date_of_birth) >= 10 ) ORDER BY pp.id desc", nativeQuery = true)
    Page<Person> findFemalePerson(Integer archived, Long facilityId, Pageable pageable);

    @Query(value = "SELECT * FROM patient_person pp WHERE pp.archived=?1 AND pp.facility_id=?2 AND pp.sex ilike 'FEMALE' AND (EXTRACT (YEAR FROM now()) - EXTRACT(YEAR FROM pp.date_of_birth) >= 10 ) ORDER BY pp.id desc", nativeQuery = true)
    Page<Person> findFemalePerson2(Integer archived, Long facilityId, Pageable pageable);

    @Query(value = "SELECT * FROM patient_person pp INNER JOIN pmtct_anc pa ON (pp.uuid=pa.person_uuid and pa.archived=0) WHERE pp.archived=?1 AND pp.facility_id=?2 AND pp.sex ilike 'FEMALE' AND (EXTRACT (YEAR FROM now()) - EXTRACT(YEAR FROM pp.date_of_birth) >= 10 ) ORDER BY pa.id desc", nativeQuery = true)
    Page<Person> getActiveOnANC(Integer archived, Long facilityId, Pageable pageable);

    @Query(value = "SELECT * FROM patient_person pp INNER JOIN pmtct_anc pa ON (pp.uuid=pa.person_uuid and pa.archived=0) WHERE (first_name ilike ?1 OR surname ilike ?1 OR other_name ilike ?1 OR full_name ilike ?1 OR hospital_number ilike ?1) AND pp.archived=?2 AND pp.facility_id=?3 AND pp.sex ilike 'FEMALE' AND (EXTRACT (YEAR FROM now()) - EXTRACT(YEAR FROM pp.date_of_birth) >= 10 ) ORDER BY pa.id desc", nativeQuery = true)
    Page<Person> getActiveOnANCBySearchParameters(String queryParam, Integer archived, Long facilityId, Pageable pageable);

}


