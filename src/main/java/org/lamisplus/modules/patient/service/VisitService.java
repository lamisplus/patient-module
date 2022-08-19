package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.controller.apierror.RecordExistException;
import org.lamisplus.modules.patient.domain.dto.CheckInDto;
import org.lamisplus.modules.patient.domain.dto.VisitDetailDto;
import org.lamisplus.modules.patient.domain.dto.VisitDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.PatientCheckPostServiceRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitService {
    private final PersonRepository personRepository;
    private final VisitRepository visitRepository;

    private final EncounterRepository encounterRepository;

    private final PatientCheckPostServiceRepository patientCheckPostServiceRepository;


    public VisitDto createVisit(VisitDto visitDto) {
        String checkInDate = visitDto.getCheckInDate();
        Person person = personRepository
                .findById (visitDto.getPersonId ())
                .orElseThrow (() -> new EntityNotFoundException (VisitService.class, "errorMessage", "No person found with id " + visitDto.getPersonId ()));
        Optional<Visit> currentVisit = visitRepository.findVisitByPersonAndVisitStartDateNotNullAndVisitEndDateIsNull (person);
        if (currentVisit.isPresent ())
            throw new RecordExistException (VisitService.class, "errorMessage", "Visit Already exist for this patient " + person.getId ());
        Visit visit = convertDtoToEntity (visitDto);
        visit.setUuid (UUID.randomUUID ().toString ());
        visit.setArchived (0);
        if(checkInDate != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime visitStartDateTime = LocalDateTime.parse(checkInDate, formatter);
            visit.setVisitStartDate(visitStartDateTime);
        }else{
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String formatDateTime = now.format(formatter);
            LocalDateTime visitStartDateTime = LocalDateTime.parse(formatDateTime, formatter);
            visit.setVisitStartDate(visitStartDateTime);
        }
        return convertEntityToDto (visitRepository.save (visit));
    }

    public VisitDto updateVisit(Long id, VisitDto visitDto) {
        Visit existVisit = getExistVisit (id);
        Visit visit = convertDtoToEntity (visitDto);
        visit.setId (existVisit.getId ());
        visit.setArchived (0);
        return convertEntityToDto (visitRepository.save (visit));

    }

    public void checkOutVisitById(Long visitId) {
        Visit visit = getExistVisit (visitId);
        List<Encounter> encounters = encounterRepository.getEncounterByVisit (visit);
        encounters.forEach (this::checkoutFromAllService);
        visit.setVisitEndDate (LocalDateTime.now ());
        visitRepository.save (visit);
    }

    private void checkoutFromAllService(Encounter encounter) {
        if (encounter.getStatus ().equals ("PENDING")) {
            encounter.setStatus ("COMPLETED");
        }
        encounterRepository.save (encounter);
    }

    public VisitDto getVisitById(Long id) {
        return convertEntityToDto (getExistVisit (id));
    }

    public List<VisitDto> getAllVisit() {
        return visitRepository
                .findAllByArchived (0)
                .stream ()
                .map (this::convertEntityToDto)
                .collect (Collectors.toList ());
    }

    public void archivedVisit(Long id) {
        Visit existVisit = getExistVisit (id);
        existVisit.setArchived (1);
        visitRepository.save (existVisit);
    }

    public VisitDto checkInPerson(CheckInDto checkInDto) {
        String checkInDate = checkInDto.getVisitDto().getCheckInDate();
        Long personId = checkInDto.getVisitDto ().getPersonId ();
        Person person = personRepository
                .findById (personId)
                .orElseThrow (() -> new EntityNotFoundException (VisitService.class, "errorMessage", "No person found with id " + personId));
        VisitDto visitDto = createVisit (checkInDto.getVisitDto ());
        Visit visit = getExistVisit (visitDto.getId ());
        checkInDto.getServiceIds ()
                .stream ()
                .map (id -> patientCheckPostServiceRepository
                        .findById (id)
                        .orElseThrow (() -> new EntityNotFoundException (VisitService.class, "errorMessage", "No service found with Id " + id)))
                .forEach (patientCheckPostService -> createEncounter (person, visit, patientCheckPostService.getModuleServiceCode ()));
        return visitDto;
    }

    private void createEncounter(Person person, Visit visit, String serviceCode) {
        Encounter encounter = getEncounter (person, visit);
        encounter.setServiceCode (serviceCode);
        encounter.setFacilityId (visit.getFacilityId ());
        encounter.setEncounterDate (LocalDateTime.now ());
        encounterRepository.save (encounter);
    }

    private Encounter getEncounter(Person person, Visit visit) {
        return Encounter.builder ()
                .person (person)
                .archived (0)
                .visit (visit)
                .encounterDate (visit.getVisitStartDate ())
                .uuid (UUID.randomUUID ().toString ())
                .status ("PENDING")
                .build ();
    }

    public List<VisitDetailDto> getVisitWithEncounterDetails(Long personId) {
        Optional<Person> person = personRepository.findById (personId);
        if (person.isPresent ()) {
            return encounterRepository.getEncounterByPersonAndArchived (person.get (), 0)
                    .stream ()
                    .map (encounter -> getVisitDetailDto (personId, encounter)).collect (Collectors.toList ());

        }

        return new ArrayList<> ();
    }

    private VisitDetailDto getVisitDetailDto(Long personId, Encounter encounter) {
        return VisitDetailDto.builder ()
                .status (encounter.getStatus ())
                .id (encounter.getVisit ().getId ())
                .personId (personId)
                .checkInDate (encounter.getVisit ().getVisitStartDate ())
                .checkOutDate (encounter.getVisit ().getVisitEndDate ())
                .encounterId (encounter.getId ())
                .service (encounter.getServiceCode ())
                .build ();
    }

    private Visit getExistVisit(Long id) {
        return visitRepository
                .findById (id)
                .orElseThrow (() -> new EntityNotFoundException (VisitService.class, "errorMessage", "No visit was found with given Id " + id));
    }


    private Visit convertDtoToEntity(VisitDto visitDto) {
        Person person = personRepository
                .findById (visitDto.getPersonId ())
                .orElseThrow (() -> new EntityNotFoundException (VisitService.class, "errorMessage", "No patient found with id " + visitDto.getPersonId ()));
        Visit visit = new Visit ();
        BeanUtils.copyProperties (visitDto, visit);
        visit.setVisitStartDate (LocalDateTime.now ());
        visit.setPerson (person);
        return visit;
    }

    private VisitDto convertEntityToDto(Visit visit) {
        VisitDto visitDto = new VisitDto ();
        BeanUtils.copyProperties (visit, visitDto);
        visitDto.setPersonId (visit.getPerson ().getId ());
        return visitDto;
    }

}
