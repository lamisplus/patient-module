package org.lamisplus.modules.patient.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.lamisplus.modules.patient.utility.SecurityUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "patient_enhance")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
})
public class PatientEnhance {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;
    @Basic
    @Column(name = "hospital_number", nullable = false, length = 25)
    private String hospitalNumber;
    @Basic
    @Column(name = "unique_id", nullable = true, length = 36)
    private String uniqueId;
    @Basic
    @Column(name = "entry_point", nullable = true, length = 15)
    private String entryPoint;
    @Basic
    @Column(name = "target_group", nullable = true, length = 15)
    private String targetGroup;
    @Basic
    @Column(name = "date_confirmed_hiv", nullable = true)
    private LocalDate dateConfirmedHiv;
    @Basic
    @Column(name = "date_enrolled_pmtct", nullable = true)
    private LocalDate dateEnrolledPmtct;
    @Basic
    @Column(name = "source_referral", nullable = true, length = 100)
    private String sourceReferral;
    @Basic
    @Column(name = "time_hiv_diagnosis", nullable = true)
    private LocalDateTime timeHivDiagnosis;
    @Basic
    @Column(name = "tb_status", nullable = true, length = 75)
    private String tbStatus;
    @Basic
    @Column(name = "date_registration", nullable = true)
    private LocalDate dateRegistration;
    @Basic
    @Column(name = "status_at_registration", nullable = true, length = 75)
    private String statusAtRegistration;
    @Basic
    @Column(name = "enrollment_setting", nullable = true, length = 15)
    private String enrollmentSetting;
    @Basic
    @Column(name = "case_manager_id", nullable = true)
    private Long caseManagerId;
    @Basic
    @Column(name = "art_start_date", nullable = true)
    private LocalDate artStartDate;
    @Basic
    @Column(name = "date_last_cd4", nullable = true)
    private LocalDate dateLastCd4;
    @Basic
    @Column(name = "uuid", nullable = true, length = 36)
    private String uuid;
    @Basic
    @Column(name = "hts_id", nullable = true)
    private Long htsId;
    @Basic
    @Column(name = "pregnant", nullable = true)
    private Boolean pregnant;
    @Basic
    @Column(name = "breastfeeding", nullable = true)
    private Boolean breastfeeding;
    @Basic
    @Column(name = "send_message", nullable = true)
    private Boolean sendMessage;
    @Basic
    @Column(name = "hts_new_id", nullable = true)
    private Long htsNewId;

    @Type(type = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "details",
            nullable = false,
            columnDefinition = "jsonb")
    private Object details;
    @Basic
    @Column(name = "organisation_unit_id", nullable = true)
    private Long organisationUnitId;
    @Basic
    @Column(name = "date_created", nullable = true)
    private LocalDateTime dateCreated = LocalDateTime.now();
    @Column(name = "created_by", nullable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private String createdBy = SecurityUtils.getCurrentUserLogin().orElse(null);
    @Basic
    @Column(name = "date_modified")
    private LocalDateTime dateModified = LocalDateTime.now();
    @Basic
    @Column(name = "modified_by", nullable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private String modifiedBy = SecurityUtils.getCurrentUserLogin().orElse(null);
    @Basic
    @Column(name = "archived")
    private Integer archived;
}
