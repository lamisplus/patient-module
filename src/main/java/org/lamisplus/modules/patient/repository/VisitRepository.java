package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByArchivedAndFacilityId(Integer archived, Long facilityId);

    List<Visit> getAllByVisitStartDateNotNullAndVisitEndDateIsNull();

    Optional<Visit> findVisitByPersonAndVisitStartDateNotNullAndVisitEndDateIsNull(Person person);

   /// Visit findByPersonAndStatus(Person person);

    List<Visit> findAllByIdAndVisitStartDateNotNullAndVisitEndDateIsNull(Long id);

    Page<Visit> findAllByArchivedAndVisitStartDateNotNullAndVisitEndDateIsNull(Integer archived, Pageable pageable);

   // List<Visit> findAllByArchivedOrderByVisitStartDateDesc(Integer archived);

    @Query(value ="SELECT * FROM patient_person", nativeQuery = true)
    List<Visit> findAllByFacilityIdAndArchived(Long facilityId, Integer archived);

    //@Query(value ="SELECT * FROM patient_person WHERE last_modified_date > ?1 AND facility_id=?2 And archived=?3", nativeQuery = true)

    @Query(value ="SELECT * FROM patient_person WHERE last_modified_date > ?1", nativeQuery = true)
    List<Visit> getVisitsDueForServerUpload(LocalDateTime dateLastSync, Long facilityId, int archived);

    @Query(value ="SELECT * FROM patient_visit where facility_id=?1", nativeQuery = true)
    List<Visit> findAllVisitsByFacilityId(Long facilityId);

    //@Query(value ="SELECT * FROM patient_person WHERE last_modified_date > ?1 AND facility_id=?2 And archived=?3", nativeQuery = true)

    @Query(value ="SELECT * FROM patient_visit WHERE last_modified_date > ?1 AND facility_id=?2", nativeQuery = true)
    List<Visit> findAllVisitsDueForServerUpload(LocalDateTime dateLastSync, Long facilityId);
}
