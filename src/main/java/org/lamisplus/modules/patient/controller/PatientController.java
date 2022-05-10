package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.PersonDto;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.service.PersonService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/patient")
public class PatientController {

    private final PersonService personService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDto> createPatient(@RequestBody PersonDto patient) {
        return ResponseEntity.ok (personService.createPerson (patient));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonResponseDto>> getAllPatients() {
        return ResponseEntity.ok (personService.getAllPerson ());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonResponseDto> getPatient(@PathVariable("id") Long id) {
        return ResponseEntity.ok (personService.getPersonById (id));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDto> updatePatient(@PathVariable("id") Long id, @RequestBody PersonDto patient){
        return ResponseEntity.ok (personService.updatePerson (id, patient));
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deletePerson(@PathVariable("id") Long id){
        personService.deletePersonById (id);
        return ResponseEntity.accepted ().build ();
    }

}
