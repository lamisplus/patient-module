package org.lamisplus.modules.patient.repository;

import liquibase.pro.packaged.E;
import org.lamisplus.modules.patient.domain.dto.EncounterDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    List<Encounter> findAllByArchivedAndFacilityId(Integer archived, Long facilityId);

//    List<Encounter> findAllByServiceCodeAndStatus(String serviceCode, String status);

    @Query(value = "SELECT CONCAT(first_name, ' ', surname) AS fullName, hospital_number AS hospitalNumber, sex, EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age, pe.service_code AS serviceCode, pe.status AS status, CASE WHEN b.person_uuid IS NOT NULL AND b.person_uuid != '' THEN 'Yes' ELSE 'No' END AS biometric   FROM patient_person pp\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid from biometric LIMIT 1\n" +
            ") b ON b.person_uuid = pp.uuid\n" +
            "INNER JOIN (\n" +
            "SELECT * from (\n" +
            "select person_uuid, encounter_date, service_code, status,\n" +
            "ROW_NUMBER() OVER(PARTITION BY person_uuid ORDER BY encounter_date DESC) rnk\n" +
            "from patient_encounter where archived = 0\n" +
            ") sub where rnk = 1\n" +
            ") pe ON pe.person_uuid = pp.uuid\n" +
            "where pp.archived = 0 AND pe.service_code = '?1' AND pe.status = '?2'", nativeQuery = true)
    List<EncounterDto> findAllByServiceCodeAndStatus (String serviceCode, String status);

    Optional<Encounter> getEncounterByVisitAndStatusAndServiceCode(Visit visit, String status, String serviceCode);

    List<Encounter> getEncounterByPersonAndArchived(Person person, Integer archived);

    List<Encounter> getEncounterByVisit(Visit visit);

    List<Encounter> findByPerson(Person person);

    List<Encounter> findByServiceCode (String serviceCode);


    List<Encounter> findByPersonAndStatus (Person person, String status);


}
