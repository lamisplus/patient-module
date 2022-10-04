package org.lamisplus.modules.patient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.User;
import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {
    private final PersonRepository personRepository;
    private final UserService userService;

    @Async("taskExecutor")
    public CompletableFuture<Boolean> hospitalNumberExist(String hospitalNumber) throws InterruptedException {
        Optional<User> currentUser = userService.getUserWithRoles();
        Thread.sleep(2000L);
        if (currentUser.isPresent()) {
            Long currentOrganisationUnitId = currentUser.get().getCurrentOrganisationUnitId();
            Optional<Person> person = personRepository.getPersonByHospitalNumberAndFacilityIdAndArchived(hospitalNumber, currentOrganisationUnitId, 0);
            if (person.isPresent()) {
                return CompletableFuture.completedFuture(true);
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    @Async("taskExecutor")
    public CompletableFuture<Boolean> isRegistrationDateBeforeDateOfBirth(LocalDate registrationDate, LocalDate birthDate) {
        if (registrationDate.isBefore(birthDate)) CompletableFuture.completedFuture(true);
        return CompletableFuture.completedFuture(false);
    }

    @Async("taskExecutor")
    public CompletableFuture<Boolean> ninNumberExist(String nin) throws InterruptedException {
        Optional<User> currentUser = userService.getUserWithRoles();
        Thread.sleep(2000L);
        if (currentUser.isPresent()) {
            Long currentOrganisationUnitId = currentUser.get().getCurrentOrganisationUnitId();
            Optional<Person> person = personRepository.getPersonByNinNumberAndFacilityIdAndArchived(nin, currentOrganisationUnitId, 0);
            if (person.isPresent()) {
                return CompletableFuture.completedFuture(true);
            }
        }
        return CompletableFuture.completedFuture(false);
    }

}
