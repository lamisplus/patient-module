package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.entity.VitalSign;
import org.lamisplus.modules.patient.domain.entity.VitalSignDto;
import org.lamisplus.modules.patient.service.VitalSignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/patient/vital-sign")
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @PostMapping
    public ResponseEntity<VitalSignDto> createVitalSign(@RequestBody VitalSignDto vitalSignDto) {
        return ResponseEntity.ok (vitalSignService.registerVitalSign (vitalSignDto));
    }

    @GetMapping
    public ResponseEntity<List<VitalSignDto>> getAllVitalSign() {
        return ResponseEntity.ok (vitalSignService.getVitalSign ());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VitalSignDto> getVitalSign(@PathVariable("id") Long id) {
        return ResponseEntity.ok (vitalSignService.getVitalSignById (id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<VitalSign> updateVitalSign(
            @PathVariable("id") Long id,
            @RequestBody VitalSignDto vitalSignDto) {
        return ResponseEntity.ok (vitalSignService.updateVitalSign (id, vitalSignDto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteVitalSign(@PathVariable("id") Long id) {
        vitalSignService.archivedVitalSign (id);
        return ResponseEntity.accepted ().build ();
    }

}
