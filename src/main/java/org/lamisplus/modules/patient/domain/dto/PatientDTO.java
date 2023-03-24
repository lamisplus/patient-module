package org.lamisplus.modules.patient.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lamisplus.modules.patient.utility.LocalDateConverter;

import javax.persistence.Convert;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDTO {
    private String hospitalNumber;
    private String uniqueId;
    private String entryPoint;
    private String targetGroup;
    private Long   patientId;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateConfirmedHiv;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEnrolledPmtct;
    private String sourceReferral;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime timeHivDiagnosis;
    private String tbStatus;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRegistration;
    private String statusAtRegistration;
    private String enrollmentSetting;
    private Long caseManagerId;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate artStartDate;
    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateLastCd4;
    private String uuid;
    private String ninNumber;
    private String emrNumber;
    private Long htsId;
    private Boolean pregnant;
    private Boolean breastfeeding;
    private Boolean sendMessage;
    private Long htsNewId;
    private Object details;
    private Long organisationUnitId;

}

