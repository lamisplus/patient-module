package org.lamisplus.modules.patient.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.lamisplus.modules.patient.domain.entity.Person;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
public interface PersonDtoProjection extends Serializable {

    Person getPerson();
//    Long getId();
//    Boolean getActive();
//    JsonNode getContactPoint();
//    JsonNode getAddress();
//    JsonNode getGender();
//    JsonNode getIdentifier();
//    Boolean getDeceased();
//    LocalDateTime getDeceasedDateTime();
//    JsonNode getMaritalStatus();
//    JsonNode getEmploymentStatus();
//    JsonNode getEducation();
//    String getSex();
//    JsonNode getOrganization();
//    JsonNode getContact();
//    LocalDate getDateOfBirth();
//    LocalDate getDateOfRegistration();
//    Integer getArchived();
//    String getNinNumber();
//    String getEmrId();
//    String getUuid();
//    String getFirstName();
//    String getSurname();
//    String getOtherName();
//    String getHospitalNumber();
//    Boolean getIsDateOfBirthEstimated();
//    String getFullName();
//    String getReason();
//    String getLatitude();
//    String getLongitude();
//    String getSource();
//    String getMobileExtra();

}
