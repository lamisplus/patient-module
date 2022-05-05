package org.lamisplus.modules.patient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.dto.PersonDto;
import org.lamisplus.modules.patient.domain.dto.PersonResponseDto;
import org.lamisplus.modules.patient.domain.entity.Person;
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

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDto> createPatient(@RequestBody PersonDto patient) throws Exception {
        return ResponseEntity.ok (personService.createPerson (patient));
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PersonResponseDto>> getAllPatients() {
        return ResponseEntity.ok (personService.getAllPerson ());
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDto> getPatient(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok (personService.getPersonById (id));
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonResponseDto> updatePatient(@PathVariable("id") Long id, @RequestBody PersonDto patient) throws Exception {
        return ResponseEntity.ok (personService.updatePerson (id, patient));
    }

    @RequestMapping(value = "/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deletePerson(@PathVariable("id") Long id) throws Exception {
        personService.deletePersonById (id);
        return ResponseEntity.accepted ().build ();
    }

}
