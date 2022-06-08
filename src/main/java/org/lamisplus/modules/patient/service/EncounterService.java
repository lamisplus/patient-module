package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.patient.domain.dto.EncounterRequestDto;
import org.lamisplus.modules.patient.domain.dto.EncounterResponseDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;

    private final PersonRepository personRepository;


    public List<EncounterResponseDto> registerEncounter(EncounterRequestDto encounterRequestDto) {
        Long visitId = encounterRequestDto.getVisitId ();
        Visit visit = visitRepository.findById (visitId).orElseThrow (() -> new EntityNotFoundException (EncounterService.class, "errorMessage", "No visit found with Id " + visitId));
        List<EncounterResponseDto> encounterRequestDtos = new ArrayList<> ();
        Set<String> serviceCodes = encounterRequestDto.getServiceCode ();
        serviceCodes
                .stream ()
                .forEach (serviceCode -> {
                    Optional<Encounter> existingEncounter =
                            encounterRepository.getEncounterByVisitAndStatusAndServiceCode (visit, encounterRequestDto.getStatus (), serviceCode);
                    if (!existingEncounter.isPresent ()) {
                        Encounter encounter = processedAndSaveEncounter (encounterRequestDto, serviceCode);
                        encounterRequestDtos.add (convertEntityToResponseDto (encounterRepository.save (encounter)));
                    }
                });
        return encounterRequestDtos;
    }


    private Encounter processedAndSaveEncounter(EncounterRequestDto encounterRequestDto, String s) {
        Encounter encounter = convertDtoToEntity (encounterRequestDto);
        encounter.setUuid (UUID.randomUUID ().toString ());
        encounter.setServiceCode (s);
        encounter.setArchived (0);
        return encounter;
    }

    public List<EncounterResponseDto> updateEncounter(Long id, EncounterRequestDto encounterRequestDto) {
        Encounter existEncounter = getExistEncounter (id);
        List<EncounterResponseDto> encounterRequestDtos = new ArrayList<> ();
        Set<String> serviceCodes = encounterRequestDto.getServiceCode ();
        serviceCodes.stream ()
                .forEach (serviceCode -> {
                    Encounter encounter = processedAndSaveEncounter (encounterRequestDto, serviceCode);
                    encounter.setId (existEncounter.getId ());
                    encounter.setArchived (0);
                    encounterRequestDtos.add (convertEntityToResponseDto (encounterRepository.save (encounter)));
                });
        return encounterRequestDtos;
    }

    public List<EncounterResponseDto> getAllEncounters() {
        return encounterRepository.findAllByArchived (0)
                .stream ()
                .map (this::convertEntityToResponseDto)
                .collect (Collectors.toList ());

    }

    public List<EncounterResponseDto> getAllEncounterByPerson(Long personId) {
        Person person = personRepository
                .findById (personId)
                .orElseThrow (() -> new EntityNotFoundException (EncounterService.class, "errorMessage", "No Person with given Id " + personId));
        List<Encounter> personEncounters = encounterRepository.getEncounterByPersonAndArchived (person, 0);
        return personEncounters
                .stream ()
                .map (this::convertEntityToResponseDto)
                .collect (Collectors.toList ());
    }

    public EncounterResponseDto getEncounterById(Long id) {
        return convertEntityToResponseDto (getExistEncounter (id));
    }


    public void archivedEncounter(Long id) {
        Encounter existEncounter = getExistEncounter (id);
        existEncounter.setArchived (1);
        encounterRepository.save (existEncounter);
    }


    private Encounter getExistEncounter(Long id) {
        return encounterRepository.findById (id).orElseThrow (() -> new EntityNotFoundException (EncounterService.class, "errorMessage", "No encounter found with Id " + id));
    }


    private EncounterResponseDto convertEntityToResponseDto(Encounter encounter) {
        EncounterResponseDto encounterRequestDto = new EncounterResponseDto ();
        BeanUtils.copyProperties (encounter, encounterRequestDto);
        encounterRequestDto.setPersonId (encounter.getPerson ().getId ());
        encounterRequestDto.setVisitId (encounter.getVisit ().getId ());
        encounter.setServiceCode (encounter.getServiceCode ());
        return encounterRequestDto;
    }

    private Encounter convertDtoToEntity(EncounterRequestDto encounterRequestDto) {
        Person person = personRepository
                .findById (encounterRequestDto.getPersonId ())
                .orElseThrow (() -> new EntityNotFoundException (EncounterService.class, "errorMessage", "No patient found with id " + encounterRequestDto.getPersonId ()));
        Visit visit = visitRepository
                .findById (encounterRequestDto.getVisitId ())
                .orElseThrow (() -> new EntityNotFoundException (EncounterService.class, "errorMessage", "No visit found with id " + encounterRequestDto.getVisitId ()));
        Encounter encounter = new Encounter ();
        BeanUtils.copyProperties (encounterRequestDto, encounter);
        encounter.setPerson (person);
        encounter.setStatus ("PENDING");
        encounter.setVisit (visit);
        return encounter;
    }
}
