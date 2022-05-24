package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.controller.exception.AlreadyExistException;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.dto.EncounterDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;

    private final PersonRepository personRepository;


    public EncounterDto registerEncounter(EncounterDto encounterDto) {
        Long visitId = encounterDto.getVisitId ();
        Visit visit = visitRepository.findById (visitId).orElseThrow (() -> new NoRecordFoundException ("No visit found with Id " + visitId));
        // set Pending by default
        Optional<Encounter> existingEncounter =
                encounterRepository.getEncounterByVisitAndStatusAndServiceCode (visit, encounterDto.getStatus (), encounterDto.getServiceCode ());
        if (existingEncounter.isPresent ())
            throw new AlreadyExistException ("Pending Encounter found for this Visit " + visit.getId ());
        Encounter encounter = convertDtoToEntity (encounterDto);
        encounter.setUuid (UUID.randomUUID ().toString ());
        encounter.setArchived (0);
        return convertEntityToDto (encounterRepository.save (encounter));
    }

    public EncounterDto updateEncounter(Long id, EncounterDto encounterDto) {
        Encounter existEncounter = getExistEncounter (id);
        Encounter encounter = convertDtoToEntity (encounterDto);
        encounter.setId (existEncounter.getId ());
        encounter.setArchived (0);
        return convertEntityToDto (encounterRepository.save (encounter));
    }

    public List<EncounterDto> getAllEncounters() {
        return encounterRepository.findAllByArchived (0)
                .stream ()
                .map (this::convertEntityToDto)
                .collect (Collectors.toList ());

    }

    public List<EncounterDto> getAllEncounterByPerson(Long personId) {
        Person person = personRepository
                .findById (personId)
                .orElseThrow (() -> new NoRecordFoundException ("No Person with given Id " + personId));
        List<Encounter> personEncounters = encounterRepository.getEncounterByPersonAndArchived (person, 0);
        return personEncounters
                .stream ()
                .map (this::convertEntityToDto)
                .collect (Collectors.toList ());
    }

    public EncounterDto getEncounterById(Long id) {
        return convertEntityToDto (getExistEncounter (id));
    }


    public void archivedEncounter(Long id) {
        Encounter existEncounter = getExistEncounter (id);
        existEncounter.setArchived (1);
        encounterRepository.save (existEncounter);
    }


    private Encounter getExistEncounter(Long id) {
        return encounterRepository.findById (id).orElseThrow (() -> new NoRecordFoundException ("No encounter found with Id " + id));
    }


    private EncounterDto convertEntityToDto(Encounter encounter) {
        EncounterDto encounterDto = new EncounterDto ();
        BeanUtils.copyProperties (encounter, encounterDto);
        encounterDto.setPersonId (encounter.getPerson ().getId ());
        encounterDto.setVisitId (encounter.getVisit ().getId ());
        return encounterDto;
    }

    private Encounter convertDtoToEntity(EncounterDto encounterDto) {
        Person person = personRepository
                .findById (encounterDto.getPersonId ())
                .orElseThrow (() -> new NoRecordFoundException ("No patient found with id " + encounterDto.getPersonId ()));
        Visit visit = visitRepository
                .findById (encounterDto.getVisitId ())
                .orElseThrow (() -> new NoRecordFoundException ("No visit found with id " + encounterDto.getVisitId ()));
        Encounter encounter = new Encounter ();
        BeanUtils.copyProperties (encounterDto, encounter);
        encounter.setPerson (person);
        encounter.setVisit (visit);
        return encounter;
    }
}
