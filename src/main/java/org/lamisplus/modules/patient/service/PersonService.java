package org.lamisplus.modules.patient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.ApplicationCodeSet;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.domain.repositories.ApplicationCodesetRepository;
import org.lamisplus.modules.base.domain.repositories.OrganisationUnitRepository;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.dto.*;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    static final String PERSON_NOT_FOUND_MESSAGE = "No person is  found with id  ";
    private final PersonRepository personRepository;
    private final ApplicationCodesetRepository applicationCodesetRepository;

    private final OrganisationUnitRepository organisationUnitRepository;

    public PersonResponseDto createPerson(PersonDto personDto) {
        Person person = getPersonFromDto (personDto);
        person.setUuid (UUID.randomUUID ().toString ());
        return getDtoFromPerson (personRepository.save (person));
    }

    public PersonResponseDto updatePerson(Long id, PersonDto personDto) {
        Person existPerson = personRepository
                .findById (id).orElseThrow (() -> new NoRecordFoundException (PERSON_NOT_FOUND_MESSAGE + id));
        Person person = getPersonFromDto (personDto);
        person.setId (existPerson.getId ());
        person.setUuid (existPerson.getUuid ());
        person.setCreatedBy (existPerson.getCreatedBy ());
        person.setCreatedDate (existPerson.getCreatedDate ());
        return getDtoFromPerson (personRepository.save (person));
    }


    public List<PersonResponseDto> getAllPerson() {
        return personRepository.getAllByArchived (0)
                .stream ()
                .map (this::getDtoFromPerson)
                .collect (Collectors.toList ());
    }


    public PersonResponseDto getPersonById(Long id) {
        Person person = personRepository
                .findById (id)
                .orElseThrow (() -> new NoRecordFoundException (PERSON_NOT_FOUND_MESSAGE + id));
        return getDtoFromPerson (person);
    }


    public void deletePersonById(Long id) {
        Person person = personRepository
                .findById (id)
                .orElseThrow (() -> new NoRecordFoundException (PERSON_NOT_FOUND_MESSAGE + id));
        person.setArchived (1);
        personRepository.save (person);
    }


    @NotNull
    private Person getPersonFromDto(PersonDto personDto) {
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
        person.setFirstName (personDto.getFirstName ());
        person.setSurname (personDto.getSurname ());
        person.setOtherName (personDto.getOtherName ());
        person.setDateOfBirth (personDto.getDateOfBirth ());
        person.setDateOfRegistration (personDto.getDateOfRegistration ());
        person.setActive (personDto.getActive ());
        person.setFacilityId (personDto.getFacilityId ());
        person.setArchived (0);
        person.setDeceasedDateTime (personDto.getDeceasedDateTime ());
        person.setDeceased (personDto.getDeceased ());
        boolean isDateOfBirthEstimated = personDto.getIsDateOfBirthEstimated () != null;
        person.setIsDateOfBirthEstimated (isDateOfBirthEstimated);
        if (genderId != null) {
            ApplicationCodeDto genderDto = getAppCodeSet (genderId);
            JsonNode genderJsonNode = mapper.valueToTree (genderDto);
            person.setGender (genderJsonNode);
        }
        if (maritalStatusId != null) {
            ApplicationCodeDto maritalStatusDto = getAppCodeSet (maritalStatusId);
            JsonNode maritalJsonNode = mapper.valueToTree (maritalStatusDto);
            person.setMaritalStatus (maritalJsonNode);
        }
        if (educationalId != null) {
            ApplicationCodeDto educationStatusDto = getAppCodeSet (educationalId);
            JsonNode educationJsonNode = mapper.valueToTree (educationStatusDto);
            person.setEducation (educationJsonNode);
        }
        if (employmentStatusId != null) {
            ApplicationCodeDto employmentStatusDto = getAppCodeSet (employmentStatusId);
            JsonNode educationJsonNode = mapper.valueToTree (employmentStatusDto);
            person.setEmploymentStatus (educationJsonNode);
        }
        if (organizationId != null) {
            OrgUnitDto organisationUnitDto = getOrgUnit (organizationId);
            JsonNode organisationUnitJsonNode = mapper.valueToTree (organisationUnitDto);
            person.setOrganization (organisationUnitJsonNode);
        }
        if (contactPointDtos != null && ! contactPointDtos.isEmpty ()) {
            ArrayNode contactPointDtoArrayNode = mapper.valueToTree (contactPointDtos);
            JsonNode contactPointDtoJsonNode = mapper.createObjectNode ().set ("contactPoint", contactPointDtoArrayNode);
            person.setContactPoint (contactPointDtoJsonNode);
        }
        if (contact != null && ! contact.isEmpty ()) {
            ArrayNode contactsDtoArrayNode = mapper.valueToTree (contact);
            JsonNode contactDtoJsonNode = mapper.createObjectNode ().set ("contact", contactsDtoArrayNode);
            person.setContact (contactDtoJsonNode);
        }
        if (identifier != null && ! identifier.isEmpty ()) {
            ArrayNode identifierDtoArrayNode = mapper.valueToTree (identifier);
            JsonNode identifierDtoJsonNode = mapper.createObjectNode ().set ("identifier", identifierDtoArrayNode);
            person.setIdentifier (identifierDtoJsonNode);
        }

        if (address != null && ! address.isEmpty ()) {
            ArrayNode addressesDtoArrayNode = mapper.valueToTree (address);
            JsonNode addressesDtoJsonNode = mapper.createObjectNode ().set ("address", addressesDtoArrayNode);
            person.setAddress (addressesDtoJsonNode);
        }
        return person;
    }


    private ApplicationCodeDto getAppCodeSet(Long id) {
        ApplicationCodeSet applicationCodeSet = applicationCodesetRepository.getOne (id);
        return new ApplicationCodeDto (applicationCodeSet.getId (), applicationCodeSet.getDisplay ());
    }

    private OrgUnitDto getOrgUnit(Long id) {
        OrganisationUnit organizationUnit = organisationUnitRepository.getOne (id);
        return new OrgUnitDto (organizationUnit.getId (), organizationUnit.getName ());
    }


    public PersonResponseDto getDtoFromPerson(Person person) {
        PersonResponseDto personResponseDto = new PersonResponseDto ();
        personResponseDto.setId (person.getId ());
        personResponseDto.setIsDateOfBirthEstimated (person.getIsDateOfBirthEstimated ());
        personResponseDto.setDateOfBirth (person.getDateOfBirth ());
        personResponseDto.setFirstName (person.getFirstName ());
        personResponseDto.setSurname (person.getSurname ());
        personResponseDto.setOtherName (person.getOtherName ());
        personResponseDto.setContactPoint (person.getContactPoint ());
        personResponseDto.setAddress (person.getAddress ());
        personResponseDto.setContact (person.getContact ());
        personResponseDto.setIdentifier (person.getIdentifier ());
        personResponseDto.setEducation (person.getEducation ());
        personResponseDto.setEmploymentStatus (person.getEmploymentStatus ());
        personResponseDto.setMaritalStatus (person.getMaritalStatus ());
        personResponseDto.setGender (person.getGender ());
        personResponseDto.setDeceased (person.getDeceased ());
        personResponseDto.setDateOfRegistration (person.getDateOfRegistration ());
        personResponseDto.setActive (person.getActive ());
        personResponseDto.setDeceasedDateTime (person.getDeceasedDateTime ());
        personResponseDto.setOrganization (person.getOrganization ());
        return personResponseDto;
    }


}


