package org.lamisplus.modules.patient.domain.repository;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.domain.dto.ApplicationCodeDto;
import org.lamisplus.modules.patient.domain.dto.OrgUnitDto;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class PersonDao {

    private final String INSERT_QUERY = String.format
            ("INSERT INTO person(active, address, archived, contact_point, contact, created_by, " +
                     "date_created, date_modified, date_of_birth,date_of_registration, deceased, deceased_date_time, first_name, " +
                     "identifier, other_name, modified_by, surname, uuid, gender, marital_status,organization,education, employment_status, is_date_birth_estimated)" +
                     " VALUES (?, (to_json(?::jsonb)), ?,(to_json(?::jsonb)), (to_json(?::jsonb)), ?, ?, ?, ?,?, ?, ?, ?," +
                     "(to_json(?::jsonb)), ?, ?, ?, ?, (to_json(?::jsonb)), (to_json(?::jsonb))," +
                     "(to_json(?::jsonb)),(to_json(?::jsonb)),(to_json(?::jsonb)),?)");


    private final String UPDATE_QUERY = String.format
            ("UPDATE person SET  active=?, address=(to_json(?::jsonb)), archived=?, " +
                     "contact_point=(to_json(?::jsonb)), contact=(to_json(?::jsonb)), date_modified=?, date_of_birth=?," +
                     " date_of_registration=?, deceased=?, deceased_date_time=?, first_name=?, identifier=(to_json(?::jsonb)), other_name=?, modified_by=?, surname=?," +
                     " gender=(to_json(?::jsonb)), marital_status=(to_json(?::jsonb)), organization=(to_json(?::jsonb))," +
                     "education=(to_json(?::jsonb)), employment_status=(to_json(?::jsonb)), is_date_birth_estimated=? WHERE id=?");


    private final String GETALL_QUERY = String.format
            ("SELECT * FROM person WHERE archived=0");

    private final String findAppCodeSetQuery = "select id, display from application_codeset where id = ?";
    private final String findOrgUnitQuery = "select id, name from organisation_unit where id = ?";

    private final String GET_ID_BY_UUID_QUERY
            = String.format ("SELECT * FROM person WHERE uuid = ?");


    private final String GET_BY_ID_QUERY =
            String.format ("SELECT * FROM person WHERE id = ?");

//    private final String DELETE_BY_ID_QUERY =
//            String.format ("DELETE FROM person WHERE id = ?");

    private final String DELETE_BY_ID_QUERY =
            String.format ("UPDATE person SET archived = 1 WHERE id = ?");

    private final JdbcTemplate template;

    @Transactional
    //organization_id,occupation_id, employment_status_id
    public Person savePerson(Person person) {
        final String uuid = UUID.randomUUID ().toString ();

        template.update (INSERT_QUERY,
                         person.getActive (),
                         (person.getAddress () != null) ? person.getAddress ().toString () : null,
                         person.getArchived (),
                         (person.getContactPoint () != null) ? person.getContactPoint ().toString () : null,
                         (person.getContact () != null) ? person.getContact ().toString () : null,
                         person.getCreatedBy (),
                         person.getDateCreated (),
                         person.getDateModified (),
                         person.getDateOfBirth (),
                         person.getDateOfRegistration (),
                         person.getDeceased (),
                         person.getDeceasedDateTime (),
                         person.getFirstName (),
                         (person.getIdentifier () != null) ? person.getIdentifier ().toString () : null,
                         person.getOtherName (),
                         person.getModifiedBy (),
                         person.getSurname (),
                         uuid,
                         (person.getGender () != null) ? person.getGender ().toString () : null,
                         (person.getMaritalStatus () != null) ? person.getMaritalStatus ().toString () : null,
                         (person.getOrganization () != null) ? person.getOrganization ().toString () : null,
                         (person.getEducation () != null) ? person.getEducation ().toString () : null,
                         (person.getEmploymentStatus () != null) ? person.getEmploymentStatus ().toString () : null,
                         person.getIsDateOfBirthEstimated ()
        );
        person.setUuid (uuid);
        return person;
    }

    @Transactional
    public Person updatePerson(Long id, Person person) {
        template.update (UPDATE_QUERY,
                         person.getActive (),
                         (person.getAddress () != null) ? person.getAddress ().toString () : null,
                         person.getArchived (),
                         (person.getContactPoint () != null) ? person.getContactPoint ().toString () : null,
                         (person.getContact () != null) ? person.getContact ().toString () : null,
                         person.getDateModified (),
                         person.getDateOfBirth (),
                         person.getDateOfRegistration (),
                         person.getDeceased (),
                         person.getDeceasedDateTime (),
                         person.getFirstName(),
                         (person.getIdentifier () != null) ? person.getIdentifier ().toString () : null,
                         person.getOtherName (),
                         person.getModifiedBy (),
                         person.getSurname (),
                         (person.getGender () != null) ? person.getGender ().toString () : null,
                         (person.getMaritalStatus () != null) ? person.getMaritalStatus ().toString () : null,
                         (person.getOrganization () != null) ? person.getOrganization ().toString () : null,
                         (person.getEducation () != null) ? person.getEducation ().toString () : null,
                         (person.getEmploymentStatus () != null) ? person.getEmploymentStatus ().toString () : null,
                         person.getIsDateOfBirthEstimated (),
                         id
        );
        person.setId (id);
        return person;
    }

    public PersonResponseDto getPersonIdByUuid(String uuid) {
        return template.queryForObject (GET_ID_BY_UUID_QUERY, new Object[]{uuid}, new PersonRowMapper ());
    }

    public Optional<PersonResponseDto> getPersonById(Long id) {
        return Optional.ofNullable (template.queryForObject (GET_BY_ID_QUERY, new Object[]{id}, new PersonRowMapper ()));
    }

    public void deletePatientById(Long id) {
        template.update (DELETE_BY_ID_QUERY, id);
    }

    public List<PersonResponseDto> getAllPersons() {

        return template.query (GETALL_QUERY, new PersonRowMapper ());
    }

    public Optional<ApplicationCodeDto> getApplicationCodeSetById(Long id) {
        return Optional.ofNullable (template.queryForObject (findAppCodeSetQuery, new Object[]{id}, new AppCodeSetRowMapper ()));
    }

    public Optional<OrgUnitDto> getOrgUnitById(Long id) {
        return Optional.ofNullable (template.queryForObject (findOrgUnitQuery, new Object[]{id}, new OrgUnitRowMapper ()));
    }

}
