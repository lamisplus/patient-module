package org.lamisplus.modules.patient.config;


import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableAcrossJpaRepositories(basePackages = {
        "org.lamisplus.modules.patient.repository"
})
@EnableTransactionManagement
public class DomainConfiguration {
}
