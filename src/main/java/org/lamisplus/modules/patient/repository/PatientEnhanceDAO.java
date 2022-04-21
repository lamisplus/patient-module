package org.lamisplus.modules.patient.repository;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.domain.entity.PatientEnhance;
import org.lamisplus.modules.patient.utility.SecurityUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientEnhanceDAO {
    private final JdbcTemplate template;

    private final String INSERT_QUERY =
            String.format("INSERT INTO patient_enhance(hospital_number, unique_id, entry_point, " +
                    "target_group, date_confirmed_hiv, date_enrolled_pmtct, source_referral, time_hiv_diagnosis, tb_status, " +
                    "date_registration, status_at_registration, enrollment_setting, case_manager_id, art_start_date," +
                    " date_last_cd4, uuid, hts_id, pregnant, breastfeeding, send_message, hts_new_id, details, " +
                    "organisation_unit_id, date_created, created_by, date_modified, modified_by, archived)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?, ?)");

    private final String UPATE_QUERY =
            String.format("UPDATE patient_enhance SET  hospital_number=?, " +
                    "unique_id=?, entry_point=?, target_group=?, date_confirmed_hiv=?, date_enrolled_pmtct=?, " +
                    "source_referral=?, time_hiv_diagnosis=?, tb_status=?, date_registration=?, status_at_registration=?," +
                    " enrollment_setting=?, case_manager_id=?, art_start_date=?, date_last_cd4=?, uuid=?, hts_id=?, pregnant=?, " +
                    "breastfeeding=?, send_message=?, hts_new_id=?, details=?::jsonb, organisation_unit_id=?," +
                    " date_modified=?, modified_by=? WHERE  id = ?");

    private final String GET_ALL_QUERY =
            String.format("SELECT id, hospital_number, unique_id, entry_point, target_group, " +
                    "date_confirmed_hiv,date_enrolled_pmtct, source_referral, " +
                    "time_hiv_diagnosis, tb_status, date_registration,status_at_registration, " +
                    "enrollment_setting, case_manager_id, art_start_date, date_last_cd4,uuid," +
                    " hts_id, pregnant, breastfeeding, send_message, hts_new_id,  details, " +
                    "organisation_unit_id FROM patient_enhance");

    private final String GET_ID_BY_UUID_QUERY
            = String.format("SELECT id FROM patient_enhance where uuid = ?");


    private final String GET_BY_ID_QUERY =
            String.format("SELECT * FROM patient_enhance where id = ?");

    private final String DELETE_BY_ID_QUERY =
            String.format("DELETE FROM patient_enhance where id = ?");


    @Transactional
    public PatientEnhance upate(PatientEnhance patientEnhance) {
        Object[] patientUpdateValues = {
                patientEnhance.getHospitalNumber(),
                patientEnhance.getUniqueId(),
                patientEnhance.getEntryPoint(),
                patientEnhance.getTargetGroup(),
                patientEnhance.getDateConfirmedHiv(),
                patientEnhance.getDateEnrolledPmtct(),
                patientEnhance.getSourceReferral(),
                patientEnhance.getTimeHivDiagnosis(),
                patientEnhance.getTbStatus(),
                patientEnhance.getDateRegistration(),
                patientEnhance.getStatusAtRegistration(),
                patientEnhance.getEnrollmentSetting(),
                patientEnhance.getCaseManagerId(),
                patientEnhance.getArtStartDate(),
                patientEnhance.getDateLastCd4(),
                patientEnhance.getUuid(),
                patientEnhance.getHtsId(),
                patientEnhance.getPregnant(),
                patientEnhance.getBreastfeeding(),
                patientEnhance.getSendMessage(),
                patientEnhance.getHtsNewId(),
                patientEnhance.getDetails(),
                patientEnhance.getOrganisationUnitId(),
                LocalDate.now(),
                SecurityUtils.getCurrentUserLogin().orElse(""),
                patientEnhance.getId()
        };
        template.update(UPATE_QUERY, patientUpdateValues);
        return patientEnhance;

    }

    @Transactional
    public PatientEnhance save(PatientEnhance patientEnhance) {
        int archived = 0;
        Object[] patientSaveValues = {
                patientEnhance.getHospitalNumber(),
                patientEnhance.getUniqueId(),
                patientEnhance.getEntryPoint(),
                patientEnhance.getTargetGroup(),
                patientEnhance.getDateConfirmedHiv(),
                patientEnhance.getDateEnrolledPmtct(),
                patientEnhance.getSourceReferral(),
                patientEnhance.getTimeHivDiagnosis(),
                patientEnhance.getTbStatus(),
                patientEnhance.getDateRegistration(),
                patientEnhance.getStatusAtRegistration(),
                patientEnhance.getEnrollmentSetting(),
                patientEnhance.getCaseManagerId(),
                patientEnhance.getArtStartDate(),
                patientEnhance.getDateLastCd4(),
                patientEnhance.getUuid(),
                patientEnhance.getHtsId(),
                patientEnhance.getPregnant(),
                patientEnhance.getBreastfeeding(),
                patientEnhance.getSendMessage(),
                patientEnhance.getHtsNewId(),
                patientEnhance.getDetails(),
                patientEnhance.getOrganisationUnitId(),
                LocalDate.now(),
                SecurityUtils.getCurrentUserLogin().orElse(""),
                LocalDate.now(),
                SecurityUtils.getCurrentUserLogin().orElse(""),
                archived
        };
        template.update(INSERT_QUERY,
                patientSaveValues);
        return patientEnhance;
    }


    public Long getPatientIdBYUUID(String uuid) {
        return template.queryForObject(GET_ID_BY_UUID_QUERY, new BeanPropertyRowMapper<>(Long.class), uuid);
    }

    public Optional<PatientEnhance> getPatientById(Long id) {
        return Optional.of(template.queryForObject(GET_BY_ID_QUERY, new BeanPropertyRowMapper<>(PatientEnhance.class), id));
    }

    public int deletePatientById(Long id) {
        return template.update(DELETE_BY_ID_QUERY, id);
    }

    public List<PatientEnhance> getAllPatients() {
        return template.query(GET_ALL_QUERY, new BeanPropertyRowMapper<>(PatientEnhance.class));
    }
}
