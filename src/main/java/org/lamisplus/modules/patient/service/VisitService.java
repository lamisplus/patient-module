package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.dto.VisitDetailDto;
import org.lamisplus.modules.patient.domain.dto.VisitDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.domain.entity.Visit;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final PersonRepository personRepository;
    private final VisitRepository visitRepository;

    private final EncounterRepository encounterRepository;


    public VisitDto createVisit(VisitDto visitDto) {
        personRepository.findById (visitDto.getPersonId ()).orElseThrow (() -> new NoRecordFoundException ("No person found with id " + visitDto.getPersonId ()));
        Visit visit = convertDtoToEntity (visitDto);
        visit.setUuid (UUID.randomUUID ().toString ());
        visit.setArchived (0);
        return convertEntityToDto (visitRepository.save (visit));
    }

    public VisitDto updateVisit(Long id, VisitDto visitDto) {
        Visit existVisit = getExistVisit (id);
        Visit visit = convertDtoToEntity (visitDto);
        visit.setId (existVisit.getId ());
        visit.setArchived (0);
        return convertEntityToDto (visitRepository.save (visit));

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
        VisitDetailDto visitDetailDto = VisitDetailDto.builder ()
                .status (encounter.getStatus ())
                .id (encounter.getVisit ().getId ())
                .personId (personId)
                .checkInDate (encounter.getVisit ().getVisitStartDate ())
                .checkOutDate (encounter.getVisit ().getVisitEndDate ())
                .service (encounter.getServiceCode ())
                .build ();
        return visitDetailDto;
    }

    private Visit getExistVisit(Long id) {
        return visitRepository.findById (id).orElseThrow (() -> new NoRecordFoundException ("No visit was found with given Id " + id));
    }


    private Visit convertDtoToEntity(VisitDto visitDto) {
        Person person = personRepository.findById (visitDto.getPersonId ()).orElseThrow (() -> new NoRecordFoundException ("No patient found with id " + visitDto.getPersonId ()));
        Visit visit = new Visit ();
        BeanUtils.copyProperties (visitDto, visit);
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
