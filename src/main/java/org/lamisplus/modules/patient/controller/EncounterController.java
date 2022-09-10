package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.EncounterRequestDto;
import org.lamisplus.modules.patient.domain.dto.EncounterResponseDto;
import org.lamisplus.modules.patient.service.EncounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/patient")
public class EncounterController {

    private final EncounterService encounterService;

    @PostMapping("/post")
    public ResponseEntity<List<EncounterResponseDto>> createEncounter(@RequestBody EncounterRequestDto encounterRequestDto) {
        return ResponseEntity.ok (encounterService.registerEncounter (encounterRequestDto));
    }

    @GetMapping("/encounter")
    public ResponseEntity<List<EncounterResponseDto>> getAllEncounter() {
        return ResponseEntity.ok (encounterService.getAllEncounters ());
    }

    @GetMapping("/encounter/{id}")
    public ResponseEntity<EncounterResponseDto> getEncounter(@PathVariable("id") Long id) {
        return ResponseEntity.ok (encounterService.getEncounterById (id));
    }

    @GetMapping("/encounter/visit/{visit_id}")
    public ResponseEntity<List<EncounterResponseDto>> getEncounterByVisitId(@PathVariable("visit_id") Long id) {
        return ResponseEntity.ok (encounterService.getEncounterByVisitId(id));
    }

    @GetMapping("/encounter/person/{personId}")
    public ResponseEntity<List<EncounterResponseDto>> getPersonEncounter(@PathVariable("personId") Long personId) {
        return ResponseEntity.ok (encounterService.getAllEncounterByPerson (personId));
    }

    @PutMapping(value = "/encounter/{id}")
    public ResponseEntity<List<EncounterResponseDto>> updateEncounter(
            @PathVariable("id") Long id,
            @RequestBody EncounterRequestDto encounterRequestDto) {
        return ResponseEntity.ok (encounterService.updateEncounter (id, encounterRequestDto));
    }

    @DeleteMapping(value = "/encounter/{id}")
    public ResponseEntity<String> deleteEncounter(@PathVariable("id") Long id) {
        encounterService.archivedEncounter (id);
        return ResponseEntity.accepted ().build ();
    }
}
