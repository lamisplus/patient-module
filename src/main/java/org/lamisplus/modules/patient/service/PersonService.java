package org.lamisplus.modules.patient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.dto.ApplicationCodesetDTO;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.base.service.OrganisationUnitService;
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

    private final PersonRepository personRepository;

    static final  String PERSON_NOT_FOUND_MESSAGE = "No person is  found with id  ";
    private final ApplicationCodesetService applicationCodesetService;

    private final OrganisationUnitService organisationUnitService;

    public PersonResponseDto createPerson(PersonDto personDto) throws Exception {
        Person person = getPersonFromDto (personDto);
        person.setUuid (UUID.randomUUID ().toString ());
        return getDtoFromPerson (personRepository.save (person));
    }

    public PersonResponseDto updatePerson(Long id, PersonDto personDto) throws Exception {
        personRepository.findById (id).orElseThrow (() -> new Exception (PERSON_NOT_FOUND_MESSAGE + id));
        Person person = getPersonFromDto (personDto);
        person.setId (id);
        return getDtoFromPerson (personRepository.save (person));
    }


    public List<PersonResponseDto> getAllPerson() {
        return personRepository.getAllByArchived (0)
                .stream ()
                .map (person -> getDtoFromPerson (person))
                .collect (Collectors.toList ());
    }


    public PersonResponseDto getPersonById(Long id) throws Exception {
        Person person = personRepository
                .findById (id)
                .orElseThrow (() -> new Exception (PERSON_NOT_FOUND_MESSAGE + id));
        return getDtoFromPerson (person);
    }


    public void deletePersonById(Long id) throws Exception {
        Person person = personRepository
                .findById (id)
                .orElseThrow (() -> new Exception (PERSON_NOT_FOUND_MESSAGE + id));
        person.setArchived (1);
        personRepository.save (person);
    }


    @NotNull
    private Person getPersonFromDto(PersonDto personDto){
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
        person.setFirstName (personDto.getFirstname ());
        person.setSurname (personDto.getSurname ());
        person.setOtherName (personDto.getOtherName ());
        person.setDateOfBirth (personDto.getDateOfBirth ());
        person.setDateOfRegistration (personDto.getDateOfRegistration ());
        person.setActive (personDto.getActive ());
        person.setArchived (0);
        person.setDeceasedDateTime (personDto.getDeceasedDateTime ());
        person.setDeceased (personDto.getDeceased ());
        boolean isDateOfBirthEstimated = personDto.getIsDateOfBirthEstimated () != null;
        person.setIsDateOfBirthEstimated (isDateOfBirthEstimated);

        if (genderId != null) {
            ApplicationCodeDto genderDto = getAppCodeSet (genderId, "No Gender exist with id " + genderId);
            JsonNode genderJsonNode = mapper.valueToTree (genderDto);
            person.setGender (genderJsonNode);
        } else {
            person.setGender (null);
        }

        if (maritalStatusId != null) {
            ApplicationCodeDto maritalStatusDto = getAppCodeSet (maritalStatusId, "No marital status exist with id " + maritalStatusId);
            JsonNode maritalJsonNode = mapper.valueToTree (maritalStatusDto);
            person.setMaritalStatus (maritalJsonNode);
        }
        else { person.setMaritalStatus (null);}

        if (educationalId != null) {
            ApplicationCodeDto educationStatusDto = getAppCodeSet (educationalId, "No occupation exist with Id " + educationalId);
            JsonNode educationJsonNode = mapper.valueToTree (educationStatusDto);
            person.setEducation (educationJsonNode);
        } else person.setEducation (null);

        if (employmentStatusId != null) {
            ApplicationCodeDto employmentStatusDto = getAppCodeSet (employmentStatusId, "No employmentStatus exist with id " + employmentStatusId);
            JsonNode educationJsonNode = mapper.valueToTree (employmentStatusDto);
            person.setEmploymentStatus (educationJsonNode);
        } else person.setEmploymentStatus (null);


        if (organizationId != null) {
            OrgUnitDto organisationUnitDto = getOrgUnit (organizationId, "No organisationUnit exist with id " + organizationId);
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
        } else person.setIdentifier (null);


        if (address != null && ! address.isEmpty ()) {
            ArrayNode addressesDtoArrayNode = mapper.valueToTree (address);
            JsonNode addressesDtoJsonNode = mapper.createObjectNode ().set ("address", addressesDtoArrayNode);
            person.setAddress (addressesDtoJsonNode);
        } else person.setAddress (null);


        return person;
    }


    private ApplicationCodeDto getAppCodeSet(Long id, String errorMessage) {
        ApplicationCodesetDTO applicationCodeset = applicationCodesetService.getApplicationCodeset (id);
        if (applicationCodeset == null) {
            throw new NotFoundException (errorMessage);
        }
        return new ApplicationCodeDto (applicationCodeset.getId (), applicationCodeset.getDisplay ());
    }

    private OrgUnitDto getOrgUnit(Long id, String errorMessage) {
        OrganisationUnit organizationUnit = organisationUnitService.getOrganizationUnit (id);
        if (organizationUnit == null) throw new NotFoundException (errorMessage);
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
        personResponseDto.setGender (person);
        personResponseDto.setDeceased (person.getDeceased ());
        personResponseDto.setDateOfRegistration (person.getDateOfRegistration ());
        personResponseDto.setActive (person.getActive ());
        personResponseDto.setDeceasedDateTime (person.getDeceasedDateTime ());
        personResponseDto.setOrganization (person.getOrganization ());
        return personResponseDto;
    }


}


