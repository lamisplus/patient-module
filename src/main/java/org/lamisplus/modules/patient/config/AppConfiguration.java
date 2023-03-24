package org.lamisplus.modules.patient.config;

import io.github.jhipster.config.JHipsterProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

    @Bean
    @Primary
    public JHipsterProperties jHipsterProperties() {
        return new JHipsterProperties();
    }
}
