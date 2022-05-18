package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.EncounterDto;
import org.lamisplus.modules.patient.service.EncounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/patient/encounter")
public class EncounterController {

    private final EncounterService encounterService;

    @PostMapping
    public ResponseEntity<EncounterDto> createEncounter(@RequestBody EncounterDto encounterDto) {
        return ResponseEntity.ok (encounterService.registerEncounter (encounterDto));
    }

    @GetMapping
    public ResponseEntity<List<EncounterDto>> getAllEncounter() {
        return ResponseEntity.ok (encounterService.getAllEncounters ());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncounterDto> getEncounter(@PathVariable("id") Long id) {
        return ResponseEntity.ok (encounterService.getEncounterById (id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<EncounterDto> updateEncounter(
            @PathVariable("id") Long id,
            @RequestBody EncounterDto encounterDto) {
        return ResponseEntity.ok (encounterService.updateEncounter (id, encounterDto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteEncounter(@PathVariable("id") Long id) {
        encounterService.archivedEncounter (id);
        return ResponseEntity.accepted ().build ();
    }
}
