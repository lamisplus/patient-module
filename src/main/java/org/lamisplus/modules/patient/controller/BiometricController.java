package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.BiometricDto;
import org.lamisplus.modules.patient.domain.dto.BiometricEnrollmentDto;
import org.lamisplus.modules.patient.domain.entity.Biometric;
import org.lamisplus.modules.patient.service.BiometricService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/patient/biometric")
public class BiometricController {
    private final BiometricService biometricService;

    @PostMapping("/templates")
    public ResponseEntity<BiometricDto> saveBiometric(@RequestBody List<BiometricEnrollmentDto> biometrics) {
        return ResponseEntity.ok (biometricService.biometricEnrollment (biometrics));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Biometric>> findByPatient(@PathVariable Long id) {
        return ResponseEntity.ok (biometricService.getByPersonId (id));
    }
}
