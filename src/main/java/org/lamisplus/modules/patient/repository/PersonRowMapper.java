package org.lamisplus.modules.patient.repository;

import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<PersonResponseDto> {
    @Override
    public PersonResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        PersonResponseDto person = new PersonResponseDto ();
        Object addresses = (rs.getObject ("address") != null) ? rs.getObject ("address").toString () : "";
        Object identifiers = (rs.getObject ("identifier") != null) ? rs.getObject ("identifier").toString () : "";
        Object contacts = (rs.getObject ("contact") != null) ? rs.getObject ("contact").toString () : "";
        Object contactPoints = (rs.getObject ("contact_point") != null) ? rs.getObject ("contact_point").toString () : "";
        Object gender = (rs.getObject ("gender") != null) ? rs.getObject ("gender").toString () : "";
        Object education = (rs.getObject ("education") != null) ? rs.getObject ("education").toString () : "";
        Object employment_status = (rs.getObject ("employment_status") != null) ? rs.getObject ("employment_status").toString () : "";
        Object organization = (rs.getObject ("organization") != null) ? rs.getObject ("organization").toString () : "";
        Object marital_status = (rs.getObject ("marital_status") != null) ? rs.getObject ("marital_status").toString () : "";
        person.setId (rs.getLong ("id"));
        person.setAddress (addresses);
        person.setContact (contacts);
        person.setContactPoint (contactPoints);
        person.setIdentifier (identifiers);
        person.setSurname (rs.getString ("surname"));
        person.setFirstname (rs.getString ("first_name"));
        person.setOtherName (rs.getString ("other_name"));
        person.setActive (rs.getBoolean ("active"));
        person.setGender (gender);
        person.setEducation (education);
        person.setEmploymentStatus (employment_status);
        person.setOrganization (organization);
        person.setMaritalStatus (marital_status);
        person.setDeceased (rs.getBoolean ("deceased"));
        person.setDateOfBirth (rs.getDate ("date_of_birth").toLocalDate ());
        person.setDateOfRegistration (rs.getDate ("date_of_registration").toLocalDate ());
        person.setIsDateOfBirthEstimated(rs.getBoolean ("is_date_Of_birth_estimated"));
        return person;
    }
}
