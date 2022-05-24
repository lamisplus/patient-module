package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.CheckInDto;
import org.lamisplus.modules.patient.domain.dto.VisitDetailDto;
import org.lamisplus.modules.patient.domain.dto.VisitDto;
import org.lamisplus.modules.patient.service.VisitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/patient/visit")
public class VisitController {
    private final VisitService visitService;


    @PostMapping
    public ResponseEntity<VisitDto> createVisit(@RequestBody VisitDto visitDto) {
        return ResponseEntity.ok (visitService.createVisit (visitDto));
    }


    @GetMapping
    public ResponseEntity<List<VisitDto>> getAllVisit() {
        return ResponseEntity.ok (visitService.getAllVisit ());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitDto> getVisit(@PathVariable("id") Long id) {
        return ResponseEntity.ok (visitService.getVisitById (id));
    }

    @GetMapping("/visit-detail/{personId}")
    public ResponseEntity<List<VisitDetailDto>> getPersonVisitDetail(@PathVariable("personId") Long personId) {
        return ResponseEntity.ok (visitService.getVisitWithEncounterDetails (personId));
    }

    @PutMapping("/checkout/{visitId}")
    public ResponseEntity<String> checkoutVisitByVisitId(@PathVariable("visitId") Long visitId) {
        visitService.checkOutVisitById (visitId);
        return ResponseEntity.accepted ().build ();
    }

    @PostMapping("/checkin")
    public ResponseEntity<VisitDto> checkInVisitByPersonId(@RequestBody CheckInDto checkInDto) {
        return ResponseEntity.ok (visitService.checkInPerson (checkInDto));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<VisitDto> updateVisit(@PathVariable("id") Long id, @RequestBody VisitDto visitDto) {
        return ResponseEntity.ok (visitService.updateVisit (id, visitDto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteVisit(@PathVariable("id") Long id) {
        visitService.archivedVisit (id);
        return ResponseEntity.accepted ().build ();
    }


}
