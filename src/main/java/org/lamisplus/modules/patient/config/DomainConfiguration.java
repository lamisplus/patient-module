package org.lamisplus.modules.patient.config;


import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAcrossJpaRepositories(basePackages = {"org.lamisplus.modules.patient.repository"})
public class DomainConfiguration {


}
