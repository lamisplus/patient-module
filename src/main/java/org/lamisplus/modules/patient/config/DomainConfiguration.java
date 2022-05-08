package org.lamisplus.modules.patient.config;


import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.patient.domain.PatientDomain;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableAcrossJpaRepositories(basePackageClasses = PersonRepository.class)
public class DomainConfiguration {
}
