package org.lamisplus.modules.patient.domain.dto;

import java.time.LocalDate;

public interface EncounterDto {

    String getFullname();
    String getHospitalNumber();

    Boolean getActive();

    String getSex();

    Integer getAge();

//    String getServiceCode();

    String getBiometric();

    Integer getId();

    Integer getEncounterId();

    LocalDate getEncounterDate();

    String getPersonUuid();

    String getUuid();

    String getServiceCode();

    String getStatus();

    Integer getArchived();

    LocalDate getDateOfBirth();

    String getVisitId();

    String getAddress();

    String getPhone();



}
