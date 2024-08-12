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

    @Query(value = "SELECT CONCAT(first_name, ' ', surname) AS fullname, hospital_number AS hospitalNumber, active, sex, EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age, (CASE WHEN b.person_uuid IS NOT NULL AND b.person_uuid != '' THEN 'Yes' ELSE 'No' END) AS biometric, pp.id,\n" +
            "pe.id AS encounterId, CAST (pe.encounter_date AS DATE) AS encounterDate, pp.uuid AS personUuid, pe.uuid, pe.service_code AS serviceCode, pe.status, pe.archived, date_of_birth AS dateOfBirth, pe.visitIdVisit AS visitId, r.address, pp.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone\n" +
            " FROM patient_person pp\n" +
            "LEFT JOIN (\n" +
            "SELECT * FROM (SELECT p.uuid, p.id, CONCAT(CAST(address_object->>'city' AS VARCHAR), ' ', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\', ''), ']', ''), '[', ''), 'null',''), '\"', '')) AS address\n" +
            "FROM patient_person p,\n" +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            ") r ON pp.uuid=r.uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT * FROM (\n" +
            "SELECT person_uuid, ROW_NUMBER() OVER(PARTITION BY person_uuid ORDER BY enrollment_date DESC) rnk\n" +
            "from biometric ) sub where rnk = 1"+
            ") b ON b.person_uuid = pp.uuid\n" +
            "INNER JOIN (\n" +
            "SELECT * from (\n" +
            "select *,pv.visit AS visitIdVisit,\n" +
            "ROW_NUMBER() OVER(PARTITION BY p.person_uuid ORDER BY p.encounter_date DESC) rnk\n" +
            "from patient_encounter p\n" +
            "INNER JOIN (\n" +
            "select vp.id AS visit, vp.uuid AS visitUuid, vp.archived AS visitArchived from patient_visit vp\n" +
            ") pv ON pv.visitUuid = p.visit_id\n" +
            " where p.archived = 0 AND pv.visitArchived = 0\n" +
            ") sub where rnk = 1\n" +
            ") pe ON pe.person_uuid = pp.uuid\n" +
            "where pp.archived = 0 AND pe.service_code = ?1 AND pe.status = 'PENDING'", nativeQuery = true)
    List<EncounterDto> findAllByServiceCodeAndStatus (String serviceCode);

    Optional<Encounter> getEncounterByVisitAndStatusAndServiceCode(Visit visit, String status, String serviceCode);

    List<Encounter> getEncounterByPersonAndArchived(Person person, Integer archived);

    List<Encounter> getEncounterByVisit(Visit visit);

    List<Encounter> findByPerson(Person person);

    List<Encounter> findByServiceCode (String serviceCode);


    List<Encounter> findByPersonAndStatus (Person person, String status);

    @Query(value = "SELECT * FROM patient_encounter pe \n" +
            "WHERE pe.visit_id = ?1 AND pe.status = 'PENDING'", nativeQuery = true)
    List<Encounter> findEncountersByUuid(String uuid);
}
