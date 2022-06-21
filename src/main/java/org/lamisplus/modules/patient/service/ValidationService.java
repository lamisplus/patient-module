package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService {

    PersonRepository personRepository;

}
