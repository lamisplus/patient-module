package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.patient.domain.dto.BiometricDto;
import org.lamisplus.modules.patient.domain.dto.BiometricEnrollmentDto;
import org.lamisplus.modules.patient.domain.entity.Biometric;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.BiometricRepository;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiometricService {
    private final BiometricRepository biometricRepository;
    private final PersonRepository personRepository;


    public BiometricDto biometricEnrollment(List<BiometricEnrollmentDto> biometricList) {
        Long personId = biometricList.get (0).getPersonId ();
        Person person = personRepository.findById (personId).orElseThrow (getEntityNotFoundExceptionSupplier (personId));
        List<Biometric> biometrics = biometricRepository.saveAll (getBiometrics (biometricList, person));
        return getBiometricDto (biometrics);
    }

    public List<Biometric> getByPersonId(Long personId) {
        return personRepository.findById (personId).map (biometricRepository::findAllByPerson).orElse (new ArrayList<> ());
    }


    @NotNull
    private List<Biometric> getBiometrics(List<BiometricEnrollmentDto> biometricList, Person person) {
        return biometricList
                .stream ()
                .map (biometricEnrollmentDto -> convertDtoToEntity (biometricEnrollmentDto, person))
                .collect (Collectors.toList ());
    }


    @NotNull
    private Supplier<EntityNotFoundException> getEntityNotFoundExceptionSupplier(Long personId) {
        return () -> new EntityNotFoundException (BiometricService.class, "Person not found with given Id " + personId);
    }


    private BiometricDto getBiometricDto(List<Biometric> biometricList) {
        return BiometricDto.builder ()
                .numberOfFingers (biometricList.size ())
                .personId (getId (biometricList))
                .date (getDate (biometricList))
                .iso (true).build ();
    }


    @Nullable
    private Long getId(List<Biometric> biometricList) {
        if (! biometricList.isEmpty ()) {
            return biometricList.get (0).getPerson ().getId ();
        }
        return null;
    }

    @Nullable
    private LocalDate getDate(List<Biometric> biometricList) {
        if (! biometricList.isEmpty ()) {
            return biometricList.get (0).getDate ();
        }
        return null;
    }


    private Biometric convertDtoToEntity(BiometricEnrollmentDto biometricEnrollmentDto, Person person) {
        Biometric biometric = new Biometric ();
        BeanUtils.copyProperties (biometricEnrollmentDto, biometric);
        biometric.setIso (true);
        biometric.setPerson (person);
        return biometric;
    }
}
