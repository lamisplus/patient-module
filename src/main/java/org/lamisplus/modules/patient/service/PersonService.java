package org.lamisplus.modules.patient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entity.ApplicationCodeSet;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.base.repository.ApplicationCodesetRepository;
import org.lamisplus.modules.base.repository.OrganisationUnitRepository;
import org.lamisplus.modules.patient.domain.dto.*;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonDao personDao;
    //private final ApplicationCodesetRepository appCodeSetRepo;
    //private final OrganisationUnitRepository orgUnitRepo;

    public PersonResponseDto createPerson(PersonDto personDto) throws Exception {
        Person person = getPersonFromDto (personDto);
        Person personSave = personDao.savePerson (person);
        return personDao.getPersonIdByUuid (personSave.getUuid ());
    }

    public PersonResponseDto updatePerson(Long id, PersonDto personDto) throws Exception {
        personDao.getPersonById (id).orElseThrow (() -> new Exception ("person with Id " + id + " is not found"));
        Person personFromDto = getPersonFromDto (personDto);
        Person updatePerson = personDao.updatePerson (id, personFromDto);
        return personDao.getPersonById (updatePerson.getId ()).orElseThrow (() -> new Exception ("person with Id " + id + " is not found"));
    }

    public List<PersonResponseDto> getAllPerson() {
        return personDao.getAllPersons ();
    }

    public PersonResponseDto getPersonById(Long id) throws Exception {
        return personDao.getPersonById (id).orElseThrow (() -> new Exception ("person with Id " + id + " is not found"));
    }

    public void deletePersonById(Long id) throws Exception {
        PersonResponseDto personResponseDto = personDao.getPersonById (id).orElseThrow (() -> new Exception ("person with Id " + id + " is not found"));
        personDao.deletePatientById (personResponseDto.getId ());
    }


    @NotNull
    private Person getPersonFromDto(PersonDto personDto) throws JsonProcessingException {
        Long genderId = personDto.getGenderId ();
        Long maritalStatusId = personDto.getMaritalStatusId ();
        Long educationalId = personDto.getEducationId ();
        Long employmentStatusId = personDto.getEmploymentStatusId ();
        Long organizationId = personDto.getOrganizationId ();
        List<ContactPointDto> contactPointDtos = personDto.getContactPoint ();
        List<ContactDto> contact = personDto.getContact ();
        List<IdentifierDto> identifier = personDto.getIdentifier ();
        List<AddressDto> address = personDto.getAddress ();
        ObjectMapper mapper = new ObjectMapper ();


        Person person = new Person ();
        person.setFirstname (personDto.getFirstname ());
        person.setSurname (personDto.getSurname ());
        person.setOtherName (personDto.getOtherName ());
        person.setDateOfBirth (personDto.getDateOfBirth ());
        person.setDateOfRegistration (personDto.getDateOfRegistration ());
        person.setActive (personDto.getActive ());
        person.setArchived (0);
        person.setDeceasedDateTime (personDto.getDeceasedDateTime ());
        person.setDeceased (personDto.getDeceased ());


        if (genderId != null) {
            ApplicationCodeDto genderDto = getAppCodeSet (genderId, "No Gender exist with id " + genderId);
          //  ApplicationCodeDto genderDto = new ApplicationCodeDto (gender.getId (), gender.getDisplay ());
            JsonNode genderJsonNode = mapper.valueToTree (genderDto);
            person.setGender (genderJsonNode);
        } else person.setGender (null);

        if (maritalStatusId != null) {
            ApplicationCodeDto maritalStatusDto = getAppCodeSet (maritalStatusId, "No marital status exist with id " + maritalStatusId);
            //ApplicationCodeDto maritalStatusDto = new ApplicationCodeDto (maritalStatus.getId (), maritalStatus.getDisplay ());
            JsonNode maritalJsonNode = mapper.valueToTree (maritalStatusDto);
            person.setMaritalStatus (maritalJsonNode);
        } else person.setMaritalStatus (null);

        if (educationalId != null) {
            ApplicationCodeDto educationStatusDto = getAppCodeSet (educationalId, "No occupation exist with Id " + educationalId);
           // ApplicationCodeDto educationStatusDto = new ApplicationCodeDto (education.getId (), education.getDisplay ());
            JsonNode educationJsonNode = mapper.valueToTree (educationStatusDto);
            person.setEducation (educationJsonNode);
        } else person.setEducation (null);

        if (employmentStatusId != null) {
            ApplicationCodeDto employmentStatusDto = getAppCodeSet (employmentStatusId, "No employmentStatus exist with id " + employmentStatusId);
            //ApplicationCodeDto employmentStatusDto = new ApplicationCodeDto (employmentStatus.getId (), employmentStatus.getDisplay ());
            JsonNode educationJsonNode = mapper.valueToTree (employmentStatusDto);
            person.setEmploymentStatus (educationJsonNode);
        } else person.setEmploymentStatus (null);


        if (organizationId != null) {
            OrgUnitDto organisationUnitDto = getOrgUnit (organizationId, "No organisationUnit exist with id " + organizationId);
            // ApplicationCodeDto organisationUnitDto = new ApplicationCodeDto (organisationUnit.getId (), organisationUnit.getName ());
            JsonNode organisationUnitJsonNode = mapper.valueToTree (organisationUnitDto);
            person.setOrganization (organisationUnitJsonNode);
        } else person.setOrganization (null);


        if (contactPointDtos != null && ! contactPointDtos.isEmpty ()) {
            ArrayNode contactPointDtoArrayNode = mapper.valueToTree (contactPointDtos);
            JsonNode contactPointDtoJsonNode = mapper.createObjectNode ().set ("contactPoint", contactPointDtoArrayNode);
            person.setContactPoint (contactPointDtoJsonNode);
        } else person.setContactPoint (null);


        if (contact != null && ! contact.isEmpty ()) {
            ArrayNode contactsDtoArrayNode = mapper.valueToTree (contact);
            JsonNode contactDtoJsonNode = mapper.createObjectNode ().set ("contact", contactsDtoArrayNode);
            person.setContact (contactDtoJsonNode);
        } else person.setContact (null);


        if (identifier != null && ! identifier.isEmpty ()) {
            ArrayNode identifierDtoArrayNode = mapper.valueToTree (identifier);
            JsonNode identifierDtoJsonNode = mapper.createObjectNode ().set ("identifier", identifierDtoArrayNode);
            person.setIdentifier (identifierDtoJsonNode);
        } else person.setIdentifier(null);


        if (address != null && ! address.isEmpty ()) {
            ArrayNode addressesDtoArrayNode = mapper.valueToTree (address);
            JsonNode addressesDtoJsonNode = mapper.createObjectNode ().set ("address", addressesDtoArrayNode);
            person.setAddress (addressesDtoJsonNode);
        } else person.setAddress (null);


        return person;
    }


    private ApplicationCodeDto getAppCodeSet(Long id, String errorMessage) {
        return  personDao.getApplicationCodeSetById (id).orElseThrow (() -> new RuntimeException (errorMessage));
    }

    private OrgUnitDto getOrgUnit(Long id, String errorMessage) {
        return personDao.getOrgUnitById (id).orElseThrow (() -> new RuntimeException (errorMessage));
    }


}


