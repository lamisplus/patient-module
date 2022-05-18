package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.controller.exception.NoRecordFoundException;
import org.lamisplus.modules.patient.domain.dto.EncounterDto;
import org.lamisplus.modules.patient.domain.entity.Encounter;
import org.lamisplus.modules.patient.repository.EncounterRepository;
import org.lamisplus.modules.patient.repository.VisitRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final VisitRepository visitRepository;
    private final EncounterRepository encounterRepository;


    public EncounterDto registerEncounter(EncounterDto encounterDto) {
        Long visitId = encounterDto.getVisitId ();
        visitRepository.findById (visitId).orElseThrow (() -> new NoRecordFoundException ("No visit found with Id " + visitId));
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
        return encounterDto;
    }

    private Encounter convertDtoToEntity(EncounterDto encounterDto) {
        Encounter encounter = new Encounter ();
        BeanUtils.copyProperties (encounterDto, encounter);
        return encounter;
    }
}
