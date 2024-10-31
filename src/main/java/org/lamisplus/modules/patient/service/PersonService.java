package org.lamisplus.modules.patient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.domain.entities.ApplicationCodeSet;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.domain.entities.User;
import org.lamisplus.modules.base.domain.repositories.ApplicationCodesetRepository;
import org.lamisplus.modules.base.domain.repositories.OrganisationUnitRepository;
import org.lamisplus.modules.base.service.MenuService;
import org.lamisplus.modules.base.service.UserService;
//import org.lamisplus.modules.base.domain.dto.;
import org.lamisplus.modules.patient.domain.dto.*;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    static final String PERSON_NOT_FOUND_MESSAGE = "No person is  found with id  ";
    private final PersonRepository personRepository;
    private final ApplicationCodesetRepository applicationCodesetRepository;

    private final OrganisationUnitRepository organisationUnitRepository;

    private final VisitRepository visitRepository;

    private final EncounterRepository encounterRepository;
    private final UserService userService;

    private final MenuService menuService;

    public PersonResponseDto createPerson(PersonDto personDto) {
        Person person = getPersonFromDto(personDto);
        Optional<User> currentUser = userService.getUserWithRoles();
        if (currentUser.isPresent()) {
            log.info("currentUser: " + currentUser.get());
            User user = currentUser.get();
            Long currentOrganisationUnitId = user.getCurrentOrganisationUnitId();
            person.setFacilityId(currentOrganisationUnitId);
        }
        String hospitalNumber = getHospitalNumber(personDto);
        person.setHospitalNumber(hospitalNumber);
        person.setUuid(UUID.randomUUID().toString());
        person.setFullName(this.getFullName(personDto.getFirstName(), personDto.getOtherName(), personDto.getSurname()));
        person.setSource((personDto.getSource() != null && !personDto.getSource().trim().isEmpty()) ? personDto.getSource()  : "Web" );
        person.setLatitude(personDto.getLatitude());
        person.setLongitude(personDto.getLongitude());
        return getDtoFromPerson(personRepository.save(person));
    }


    public PersonResponseDto updatePerson(Long id, PersonDto personDto) {
        Person existPerson = personRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException(PersonService.class, PERSON_NOT_FOUND_MESSAGE + id));
        Person person = getPersonFromDto(personDto);
        person.setId(existPerson.getId());
        person.setUuid(existPerson.getUuid());
        person.setCreatedBy(existPerson.getCreatedBy());
        person.setCreatedDate(existPerson.getCreatedDate());
        person.setArchived(existPerson.getArchived());
        person.setFacilityId(existPerson.getFacilityId());
        return getDtoFromPerson(personRepository.save(person));
    }


    public List<PersonResponseDto> getAllPerson() {
        return personRepository.getAllByArchivedOrderByDateOfRegistrationDesc(0)
                .stream()
                .map(this::getDtoFromPerson)
                .collect(Collectors.toList());
    }

    //ResponseEntity<PersonMetaDataDto>
    public PersonMetaDataDto getAllPersonPageable(int pageNo, int pageSize) {
        //Person person = getPerson(personId);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> person = personRepository.getAllByArchivedAndFacilityIdOrderByIdDesc(0, currentOrganisationUnitId, paging);
        if (person.hasContent()) {

            PageDTO pageDTO = this.generatePagination(person);
            long recordSize = pageDTO.getTotalRecords();
            double totalPage = pageDTO.getTotalPages();
            PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
            personMetaDataDto.setTotalRecords(recordSize);
            personMetaDataDto.setPageSize(pageDTO.getPageSize());
            personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
            personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
            personMetaDataDto.setRecords(person.getContent().stream().map(this::getDtoFromPerson).collect(Collectors.toList()));
            return personMetaDataDto;
        }
        return null;
    }

    public Boolean isPersonExist(Long personId) {
        Optional<Person> person = personRepository.findById(personId);
        return person.isPresent();
    }

    public List<PersonResponseDto> getCheckedInPersonsByServiceCodeAndVisitId(String serviceCode) {
        List<Encounter> patientEncounters = encounterRepository.findAllByServiceCodeAndStatus(serviceCode, "PENDING");
        return patientEncounters.stream()
                .map(Encounter::getPerson)
                .map(this::getDtoFromPerson)
                .collect(Collectors.toList());
    }

    public PersonResponseDto getPersonById(Long id) {
        Person person = personRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PersonService.class, "errorMessage", PERSON_NOT_FOUND_MESSAGE + id));
        return getDtoFromPerson(person);
    }


    public void deletePersonById(Long id, String message) {
        Person person = personRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PersonService.class, "errorMessage", PERSON_NOT_FOUND_MESSAGE + id));
        person.setArchived(1);
        person.setReason(message);
        personRepository.save(person);
    }

    public void deletePersonById2(Long id) {
        Person person = personRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PersonService.class, "errorMessage", PERSON_NOT_FOUND_MESSAGE + id));
        person.setArchived(2);
        personRepository.save(person);
    }

    private String getHospitalNumber(PersonDto personDto) {
        List<IdentifierDto> identifier = personDto.getIdentifier();
        if (!identifier.isEmpty()) {
            IdentifierDto identifierDto = identifier.get(0);
            String type = identifierDto.getType();
            if (type.equals("HospitalNumber")) {
                String hospitalNumber = identifierDto.getValue();
                log.info("hospitalNumber {} ", hospitalNumber);
                return hospitalNumber;
            }
        }
        return null;
    }

    @NotNull
    private Person getPersonFromDto(PersonDto personDto) {
        Long sexId = personDto.getSexId();
        Long genderId = personDto.getGenderId();
        Long maritalStatusId = personDto.getMaritalStatusId();
        Long educationalId = personDto.getEducationId();
        Long employmentStatusId = personDto.getEmploymentStatusId();
        Long organizationId = personDto.getOrganizationId();
        List<ContactPointDto> contactPointDtos = personDto.getContactPoint();
        List<ContactDto> contact = personDto.getContact();
        List<IdentifierDto> identifier = personDto.getIdentifier();
        List<AddressDto> address = personDto.getAddress();
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person();
        String hospitalNumber = getHospitalNumber(personDto);
        person.setHospitalNumber(hospitalNumber);
        person.setFirstName(this.treatNull(personDto.getFirstName()));
        person.setSurname(this.treatNull(personDto.getSurname()));
        person.setOtherName(this.treatNull(personDto.getOtherName()));
        person.setDateOfBirth(personDto.getDateOfBirth());
        person.setDateOfRegistration(personDto.getDateOfRegistration());
        person.setActive(personDto.getActive());
        person.setFacilityId(personDto.getFacilityId());
        person.setArchived(0);
        person.setDeceasedDateTime(personDto.getDeceasedDateTime());
        person.setDeceased(personDto.getDeceased());
        person.setNinNumber(personDto.getNinNumber());
        person.setEmrId(personDto.getEmrId());
        boolean isDateOfBirthEstimated = personDto.getIsDateOfBirthEstimated() != null;
        person.setIsDateOfBirthEstimated(isDateOfBirthEstimated);

        if (genderId != null) {
            ApplicationCodeDto genderDto = getAppCodeSet(genderId);
            JsonNode genderJsonNode = mapper.valueToTree(genderDto);
            //person.setSex (genderDto.getDisplay ());
            person.setGender(genderJsonNode);
        }
        if (sexId != null) {
            ApplicationCodeDto sexDto = getAppCodeSet(sexId);
            log.info("sex {}", sexDto.getDisplay());
            person.setSex(sexDto.getDisplay());
        }
        if (maritalStatusId != null) {
            ApplicationCodeDto maritalStatusDto = getAppCodeSet(maritalStatusId);
            JsonNode maritalJsonNode = mapper.valueToTree(maritalStatusDto);
            person.setMaritalStatus(maritalJsonNode);
        }
        if (educationalId != null) {
            ApplicationCodeDto educationStatusDto = getAppCodeSet(educationalId);
            JsonNode educationJsonNode = mapper.valueToTree(educationStatusDto);
            person.setEducation(educationJsonNode);
        }
        if (employmentStatusId != null) {
            ApplicationCodeDto employmentStatusDto = getAppCodeSet(employmentStatusId);
            JsonNode educationJsonNode = mapper.valueToTree(employmentStatusDto);
            person.setEmploymentStatus(educationJsonNode);
        }
        if (organizationId != null) {
            OrgUnitDto organisationUnitDto = getOrgUnit(organizationId);
            JsonNode organisationUnitJsonNode = mapper.valueToTree(organisationUnitDto);
            person.setOrganization(organisationUnitJsonNode);
        }
        if (contactPointDtos != null && !contactPointDtos.isEmpty()) {
            ArrayNode contactPointDtoArrayNode = mapper.valueToTree(contactPointDtos);
            JsonNode contactPointDtoJsonNode = mapper.createObjectNode().set("contactPoint", contactPointDtoArrayNode);
            person.setContactPoint(contactPointDtoJsonNode);
        }
        if (contact != null && !contact.isEmpty()) {
            ArrayNode contactsDtoArrayNode = mapper.valueToTree(contact);
            JsonNode contactDtoJsonNode = mapper.createObjectNode().set("contact", contactsDtoArrayNode);
            person.setContact(contactDtoJsonNode);
        }
        if (identifier != null && !identifier.isEmpty()) {
            ArrayNode identifierDtoArrayNode = mapper.valueToTree(identifier);
            JsonNode identifierDtoJsonNode = mapper.createObjectNode().set("identifier", identifierDtoArrayNode);
            person.setIdentifier(identifierDtoJsonNode);
        }

        if (address != null && !address.isEmpty()) {
            ArrayNode addressesDtoArrayNode = mapper.valueToTree(address);
            JsonNode addressesDtoJsonNode = mapper.createObjectNode().set("address", addressesDtoArrayNode);
            person.setAddress(addressesDtoJsonNode);
        }
        return person;
    }

    private ApplicationCodeDto getAppCodeSet(Long id) {
        ApplicationCodeSet applicationCodeSet = applicationCodesetRepository.getOne(id);
        return new ApplicationCodeDto(applicationCodeSet.getId(), applicationCodeSet.getDisplay());
    }

    private OrgUnitDto getOrgUnit(Long id) {
        OrganisationUnit organizationUnit = organisationUnitRepository.getOne(id);
        return new OrgUnitDto(organizationUnit.getId(), organizationUnit.getName());
    }


    public PersonResponseDto getDtoFromPerson(Person person) {
//        Optional<Visit> visit = visitRepository.findVisitByPersonAndVisitStartDateNotNullAndVisitEndDateIsNull(person);
//        log.info("visit id {}", visit.get().getId());
        PersonResponseDto personResponseDto = new PersonResponseDto();
        Optional<Visit> visit = visitRepository.getRecentPatientVisit(person.getUuid());
           if (visit.isPresent()) {
               //log.info("visit id {}", visit.get().getId());
               personResponseDto.setVisitId(visit.get().getId());
           }
        personResponseDto.setId(person.getId());
        personResponseDto.setIsDateOfBirthEstimated(person.getIsDateOfBirthEstimated());
        personResponseDto.setDateOfBirth(person.getDateOfBirth());
        personResponseDto.setFirstName(person.getFirstName());
        personResponseDto.setSurname(person.getSurname());
        personResponseDto.setOtherName(person.getOtherName());
        personResponseDto.setContactPoint(person.getContactPoint());
        personResponseDto.setAddress(person.getAddress());
        personResponseDto.setContact(person.getContact());
        personResponseDto.setIdentifier(person.getIdentifier());
        personResponseDto.setEducation(person.getEducation());
        personResponseDto.setEmploymentStatus(person.getEmploymentStatus());
        personResponseDto.setMaritalStatus(person.getMaritalStatus());
        personResponseDto.setSex(person.getSex());
        personResponseDto.setGender(person.getGender());
        personResponseDto.setDeceased(person.getDeceased());
        personResponseDto.setDateOfRegistration(person.getDateOfRegistration());
        personResponseDto.setActive(person.getActive());
        personResponseDto.setNinNumber(person.getNinNumber());
        personResponseDto.setDeceasedDateTime(person.getDeceasedDateTime());
        personResponseDto.setOrganization(person.getOrganization());
        personResponseDto.setArchived(person.getArchived());
        personResponseDto.setUuid(person.getUuid());
        personResponseDto.setBiometricStatus(getPatientBiometricStatus(person.getUuid()));
        personResponseDto.setFacilityId(person.getFacilityId());
        personResponseDto.setLatitude(person.getLatitude());
        personResponseDto.setLongitude(person.getLongitude());
        personResponseDto.setSource(person.getSource());
//        personResponseDto.setSource((person.getSource() != null && !person.getSource().trim().isEmpty()) ? person.getSource()  : "Web" );
        return personResponseDto;
    }

    Boolean getPatientBiometricStatus(String uuid) {
        String moduleName = "BiometricModule";
        if (!menuService.exist(moduleName)) {
            return false;
        }
        Integer fingerCount = personRepository.getBiometricCountByPersonUuid(uuid);
        Boolean status = Boolean.FALSE;
        if (fingerCount >= 6) status = Boolean.TRUE;
        return status;
    }
    public PersonResponseDto getDtoFromPersonWithoutBiometric(Person person, Boolean status) {

        PersonResponseDto personResponseDto = new PersonResponseDto();
        personResponseDto.setId(person.getId());
        personResponseDto.setNinNumber(person.getNinNumber());
        personResponseDto.setEmrId(person.getEmrId());
        personResponseDto.setFacilityId(person.getFacilityId());
        personResponseDto.setIsDateOfBirthEstimated(person.getIsDateOfBirthEstimated());
        personResponseDto.setDateOfBirth(person.getDateOfBirth());
        personResponseDto.setFirstName(this.treatNull(person.getFirstName()));
        personResponseDto.setSurname(this.treatNull(person.getSurname()));
        personResponseDto.setOtherName(this.treatNull(person.getOtherName()));
        personResponseDto.setContactPoint(person.getContactPoint());
        personResponseDto.setAddress(person.getAddress());
        personResponseDto.setContact(person.getContact());
        personResponseDto.setIdentifier(person.getIdentifier());
        personResponseDto.setEducation(person.getEducation());
        personResponseDto.setEmploymentStatus(person.getEmploymentStatus());
        personResponseDto.setMaritalStatus(person.getMaritalStatus());
        personResponseDto.setSex(person.getSex());
        personResponseDto.setGender(person.getGender());
        personResponseDto.setDeceased(person.getDeceased());
        personResponseDto.setDateOfRegistration(person.getDateOfRegistration());
        personResponseDto.setActive(person.getActive());
        personResponseDto.setDeceasedDateTime(person.getDeceasedDateTime());
        personResponseDto.setOrganization(person.getOrganization());
        personResponseDto.setBiometricStatus(status);
        personResponseDto.setArchived(person.getArchived());


        return personResponseDto;
    }


    public String treatNull(String name) {
        String newName = "";
        if (name == null) newName = "";
        else newName = name;
        return newName;
    }


    public PersonResponseDto getPersonByNin(String nin) {
        Person person = personRepository
                .findPersonByNinNumber(nin)
                .orElseThrow(() -> new EntityNotFoundException(PersonService.class, "errorMessage", PERSON_NOT_FOUND_MESSAGE + nin));
        return getDtoFromPerson(person);
    }


    public boolean isNINExisting(String nin) {
        List<Person> person = personRepository.getPersonByNinNumber(nin);
        System.out.println("Size = " + person.size());
        boolean reply = false;
        if (person.isEmpty()) reply = false;
        else reply = true;
        return reply;
    }
    public Integer getTotalRecords() {
        return personRepository.getTotalRecords();
    }

    public PageDTO generatePagination(Page page) {
        long totalRecords = page.getTotalElements();
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        int totalPages = page.getTotalPages();
        return PageDTO.builder().totalRecords(totalRecords)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(totalPages).build();
    }

    public PersonMetaDataDto findPersonBySearchParam(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        String queryParam = "";
        Page<Person> person = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");

            queryParam = "%" + searchValue + "%";
            person = personRepository.findAllPersonBySearchParameters(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            person = personRepository.getAllByArchivedAndFacilityIdOrderByIdDesc(0, currentOrganisationUnitId, paging);
        }

        if (person.hasContent()) {

//            PageDTO pageDTO = this.generatePagination(person);
//            long recordSize = pageDTO.getTotalRecords();
//            double totalPage = pageDTO.getTotalPages();
            PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
            personMetaDataDto.setTotalRecords(person.getTotalElements());
            personMetaDataDto.setPageSize(person.getSize());
            personMetaDataDto.setTotalPages(person.getTotalPages());
            personMetaDataDto.setCurrentPage(person.getNumber());
            //personMetaDataDto.setRecords(person.getContent());
            personMetaDataDto.setRecords(person.getContent().stream().map(this::getDtoFromPerson).collect(Collectors.toList()));
            return personMetaDataDto;
        }
        return null;

    }

    public PersonMetaDataDto getAllActiveVisit(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        ArrayList<PersonResponseDto> checkedInPeople = new ArrayList<>();
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> person = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            person = personRepository.findCheckedInPersonBySearchParameters(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            person = personRepository.findAllCheckedInPerson(0, currentOrganisationUnitId, paging);

        }

        person.forEach(visit -> {
            checkedInPeople.add(this.getDtoFromPersonWithoutBiometric(visit, Boolean.TRUE));
        });
        PageDTO pageDTO = this.generatePagination(person);
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(checkedInPeople);
        return personMetaDataDto;
        //return checkedInPeople;
    }

//    public PersonMetaDataDto getAllPatientWithoutBiomentic(String searchValue, int pageNo, int pageSize) {
//        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
//        Optional<User> currentUser = this.userService.getUserWithRoles();
//        Long currentOrganisationUnitId = 0L;
//        if (currentUser.isPresent()) {
//            User user = (User) currentUser.get();
//            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();
//
//        }
//        Page<Person> persons = null;
//        if (!((searchValue == null) || (searchValue.equals("*")))) {
//            searchValue = searchValue.replaceAll("\\s", "");
//            searchValue = searchValue.replaceAll(",", "");
//            String queryParam = "%" + searchValue + "%";
//            persons = personRepository.findAllPersonBySearchParameters(queryParam, 0, currentOrganisationUnitId, paging);
//        } else {
//            persons = personRepository.getAllByArchivedAndFacilityIdOrderByIdDesc(0, currentOrganisationUnitId, paging);
//
//        }
//        List<PersonResponseDto> personResponseDtoList = new ArrayList<>();
//        persons.getContent().forEach(person -> {
//            Integer checkIfUserHasBiometric = this.personRepository.getBiometricCountByPersonUuid(person.getUuid());
//            if (checkIfUserHasBiometric == 0) {
//                personResponseDtoList.add(getDtoFromPersonWithoutBiometric(person, Boolean.FALSE));
//            }
//        });
//
//        Page toPage2 = this.toPage(personResponseDtoList, paging);
//        PageDTO pageDTO = this.generatePagination(toPage2);
//        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
//        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
//        personMetaDataDto.setPageSize(pageDTO.getPageSize());
//        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
//        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
//        personMetaDataDto.setRecords(personResponseDtoList);
//        return personMetaDataDto;
//        //return checkedInPeople;
//    }

    public Page toPage(List list, Pageable pageable) {
        System.out.println("List Size Before= " + list.size());
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ?
                list.size() :
                pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        System.out.println("SubList Size After= " + subList.size());
        return new PageImpl(subList, pageable, list.size());
    }

    public PersonMetaDataDto getDuplicateHospitalNumbers(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> persons = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            persons = personRepository.findDuplicatePersonBySearchParameters(queryParam, currentOrganisationUnitId, paging);
        } else {
            persons = personRepository.findDuplicatePerson(currentOrganisationUnitId, paging);

        }
        if (persons.hasContent()) {

            PageDTO pageDTO = this.generatePagination(persons);
            long recordSize = pageDTO.getTotalRecords();
            double totalPage = pageDTO.getTotalPages();
            PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
            personMetaDataDto.setTotalRecords(recordSize);
            personMetaDataDto.setPageSize(pageDTO.getPageSize());
            personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
            personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
            personMetaDataDto.setRecords(persons.getContent().stream().map(this::getDtoFromPerson).collect(Collectors.toList()));
            return personMetaDataDto;
        }
        return null;
    }

    public String getFullName(String fn, String mn, String sn) {
        String fullName = "";
        if (fn == null) fn = "";
        if (sn == null) sn = "";
        if (mn == null) mn = "";
        fullName = fn.trim() +mn.trim()+ sn.trim();
        fullName = fullName.replaceAll("\\s", "");
        fullName = fullName.replaceAll(",", "");

        return fullName;

    }


    public PersonMetaDataDto getAllPatientWithBiomentic(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> persons = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            persons = personRepository.findPersonWithBiometricsUsingSearchParam(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            persons = personRepository.findPersonWithBiometrics(0, currentOrganisationUnitId, paging);
            System.out.println("Testng the number of records "+persons.getNumberOfElements());

        }
//        List<PersonResponseDto> personResponseDtoList = new ArrayList<>();
//        persons.getContent().forEach(person -> {
//            Integer checkIfUserHasBiometric = this.personRepository.getBiometricCountByPersonUuid(person.getUuid());
//            if (checkIfUserHasBiometric > 0) {
//                personResponseDtoList.add(getDtoFromPersonWithoutBiometric(person, Boolean.TRUE));
//            }
//        });

        //Page toPage2 = this.toPage(persons, paging);
        PageDTO pageDTO = this.generatePagination(persons);
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(persons.getContent());
        return personMetaDataDto;
        //return checkedInPeople;
    }
    public PersonMetaDataDto getAllPatientWithIncompleteBiomentic(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> persons = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            persons = personRepository.findPersonWithOutBiometricsUsingSearchParam(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            persons = personRepository.findPersonWithOutBiometrics(0, currentOrganisationUnitId, paging);
           // System.out.println("Testng the number of records "+persons.getNumberOfElements());

        }
//        List<PersonResponseDto> personResponseDtoList = new ArrayList<>();
//        persons.getContent().forEach(person -> {
//            Integer checkIfUserHasBiometric = this.personRepository.getBiometricCountByPersonUuid(person.getUuid());
//            if (checkIfUserHasBiometric > 0) {
//                personResponseDtoList.add(getDtoFromPersonWithoutBiometric(person, Boolean.TRUE));
//            }
//        });

        //Page toPage2 = this.toPage(persons, paging);
        PageDTO pageDTO = this.generatePagination(persons);
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(persons.getContent());
        return personMetaDataDto;
        //return checkedInPeople;
    }


    public PersonMetaDataDto getAllPatientWithoutBiomentic(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> persons = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            persons = personRepository.findPersonWithOutBiometrics4(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            persons = personRepository.findPersonWithOutBiometrics3(0, currentOrganisationUnitId, paging);
            // System.out.println("Testng the number of records "+persons.getNumberOfElements());

        }
//        List<PersonResponseDto> personResponseDtoList = new ArrayList<>();
//        persons.getContent().forEach(person -> {
//            Integer checkIfUserHasBiometric = this.personRepository.getBiometricCountByPersonUuid(person.getUuid());
//            if (checkIfUserHasBiometric > 0) {
//                personResponseDtoList.add(getDtoFromPersonWithoutBiometric(person, Boolean.TRUE));
//            }
//        });

        //Page toPage2 = this.toPage(persons, paging);
        PageDTO pageDTO = this.generatePagination(persons);
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(persons.getContent());
        return personMetaDataDto;
        //return checkedInPeople;
    }

    public PersonMetaDataDto getAllPatientWithoutRecapture(String searchValue, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }
        Page<Person> persons = null;
        if (!((searchValue == null) || (searchValue.equals("*")))) {
            searchValue = searchValue.replaceAll("\\s", "");
            searchValue = searchValue.replaceAll(",", "");
            String queryParam = "%" + searchValue + "%";
            persons = personRepository.findPersonWithOutRecapture4(queryParam, 0, currentOrganisationUnitId, paging);
        } else {
            persons = personRepository.findPersonWithOutRecapture3(0, currentOrganisationUnitId, paging);

        }

        PageDTO pageDTO = this.generatePagination(persons);
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(persons.getContent());
        return personMetaDataDto;

    }

    public Optional<Person> findPersonByUuid(String uuid){
        return personRepository.findByUuid(uuid);
    }

    public PersonMetaDataDto getAllPatientByLga(List<String> lgaIds, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Long currentOrganisationUnitId = getCurrentOrganisationUnitId();
        Page<PersonDtoProjection> persons;
        if (lgaIds.size() < 1) {
            log.info("LGA IDs not supplied");
            return null;
        } else {
            persons = personRepository.findPersonByLga(0, currentOrganisationUnitId, lgaIds, paging);
        }

        PageDTO pageDTO = this.generatePagination(persons);
        return getPersonMetdaDataDtoObject(pageDTO, persons);
    }

    public List<PatientDtoRecapture> getPatientsByLgaBiometricRecapture(List<String> lgaIds) {
        List<PatientDtoRecapture> recaptures  = new ArrayList<PatientDtoRecapture>();
        Long currentOrganisationUnitId = getCurrentOrganisationUnitId();
        try {
            recaptures = personRepository.getEnrolledPatientsByLgaWithRecaptureMobile(currentOrganisationUnitId, lgaIds)
                    .stream()
                    .map(this::getPatientDTOBuild)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("An error occurred when fetching patients from the database: {}", e.getMessage());
        }
        return recaptures;
    }



//    public  List<PatientDtoRecapture> getPatientsByLgaBiometricRecapture (List<String> lgaIds) {
//        List<PatientDtoRecapture> recaptures  = new ArrayList<PatientDtoRecapture>();
//        Long currentOrganisationUnitId = getCurrentOrganisationUnitId();
//        try {
//            recaptures = personRepository.getEnrolledPatientsByLgaWithRecaptureMobile(currentOrganisationUnitId, lgaIds)
//                    .stream()
//                    .map(this::getPatientDTOBuild)
//                    .collect(Collectors.toList());
//        }catch(Exception e){
//            log.error("An error occurred when fetching patient from database {}", e.getMessage());
//        }
//        return recaptures;
//    }

    public List<PersonExtraDto> getAllPatientByUser(String userId) {
        List<Object[]> results = personRepository.findPersonByUser(userId);
        List<PersonExtraDto> dtos = results.stream().map(this::mapToDTO).collect(Collectors.toList());
        return dtos;
    }

    private  PatientDtoRecapture getPatientDTOBuild(PersonDtoProjection p) {
        PatientDtoRecapture patientDTO = PatientDtoRecapture.builder()
                .age(p.getAge())
                .dateOfBirth(p.getDateOfBirth())
                .sex(p.getGender())
                .hospitalNumber(p.getHospitalNumber())
                .firstName(p.getFirstName())
                .facilityId(p.getFacility())
                .personUuid(p.getPersonUuid())
                .otherName(p.getOtherName())
                .surname(p.getSurname())
                .id(p.getId())
                .isDobEstimated(p.getIsDobEstimated())
                .isEnrolled(p.getIsEnrolled())
                .uniqueId(p.getUniqueId())
                .dateOfRegistration(p.getDateOfRegistration())
                .mobileExtra(p.getMobileExtra())
//                .biometricStatus(p.getBiometricStatus())
                .build();
        patientDTO.setBiometricStatus(p.getBiometricStatus() != null);

        return patientDTO;
    }

    private PersonExtraDto mapToDTO(Object[] tuple) {
        PersonExtraDto dto = new PersonExtraDto();
        dto.setId((Number) tuple[0]);
        dto.setPersonUuid((String) tuple[1]);
        dto.setDeceased((Boolean) tuple[2]);
        dto.setDeceasedDateTime(convertToLocalDateTime(tuple[3]));
        dto.setSex((String) tuple[4]);
        dto.setDateOfBirth(convertToLocalDate(tuple[5]));
        dto.setDateOfRegistration(convertToLocalDate(tuple[6]));
        dto.setArchived((Integer) tuple[7]);
        dto.setNinNumber((String) tuple[8]);
        dto.setEmrId((String) tuple[9]);
        dto.setFirstName((String) tuple[10]);
        dto.setSurname((String) tuple[11]);
        dto.setOtherName((String) tuple[12]);
        dto.setHospitalNumber((String) tuple[13]);
        dto.setLabTestName((String) tuple[14]);
        dto.setGroupName((String) tuple[15]);
        dto.setResultReported((String) tuple[16]);
        dto.setLastVlDate(convertToLocalDateTime(tuple[17]));
        dto.setMaxDsdDate(convertToLocalDate(tuple[18]));
        dto.setLastDrugPickupDate(convertToLocalDate(tuple[19]));
        dto.setNextAppointment(convertToLocalDate(tuple[20]));

        return dto;
    }

    private LocalDate convertToLocalDate(Object date) {
        if (date instanceof Date) {
            return ((Date) date).toLocalDate();
        } else if (date instanceof java.sql.Timestamp) {
            return ((Timestamp) date).toLocalDateTime().toLocalDate();
        } else if (date instanceof java.util.Date) {
            return ((java.util.Date) date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private LocalDateTime convertToLocalDateTime(Object date) {
        if (date instanceof Timestamp) {
            return ((Timestamp) date).toLocalDateTime();
        } else if (date instanceof Date) {
            return ((Date) date).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    PersonMetaDataDto getPersonMetdaDataDtoObject(PageDTO pageDTO, Page persons){
        PersonMetaDataDto personMetaDataDto = new PersonMetaDataDto();
        personMetaDataDto.setTotalRecords(pageDTO.getTotalRecords());
        personMetaDataDto.setPageSize(pageDTO.getPageSize());
        personMetaDataDto.setTotalPages(pageDTO.getTotalPages());
        personMetaDataDto.setCurrentPage(pageDTO.getPageNumber());
        personMetaDataDto.setRecords(persons.getContent());
        return personMetaDataDto;
    }

    Long getCurrentOrganisationUnitId(){
        Optional<User> currentUser = this.userService.getUserWithRoles();
        Long currentOrganisationUnitId = 0L;
        if (currentUser.isPresent()) {
            User user = (User) currentUser.get();
            currentOrganisationUnitId = user.getCurrentOrganisationUnitId();

        }

        return currentOrganisationUnitId;
    }

    public List<Map<String, Object>> countRegistrationsBySex() {
        List<Object[]> result = personRepository.countRegistrationsBySex();
        List<Map<String, Object>> formattedResult = new ArrayList<>();

        for (Object[] row : result) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", row[0]);
            entry.put("y", row[1]);
            formattedResult.add(entry);
        }

        return formattedResult;
    }

    public List<Map<String, Object>> countRegistrationsByYearAndSex() {
        List<Object[]> result = personRepository.countRegistrationsByYearAndSex();
        List<Map<String, Object>> formattedResult = new ArrayList<>();

        for (Object[] row : result) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("year", row[0]);
            entry.put("male", row[1]);
            entry.put("female", row[2]);
            formattedResult.add(entry);
        }

        return formattedResult;
    }

}