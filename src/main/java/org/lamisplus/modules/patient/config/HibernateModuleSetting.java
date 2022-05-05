package org.lamisplus.modules.patient.config;

import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModuleSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.Database;

@Configuration
@Slf4j
public class HibernateModuleSetting extends AcrossHibernateJpaModuleSettings {
    @Override
    public String getPersistenceUnitName() {
        String persistenceUnitName = super.getPersistenceUnitName ();
        log.info ("persistenceUnitName {} ", persistenceUnitName);
        return persistenceUnitName;
    }

    @Override
    public Boolean getPrimary() {
        Boolean primary = super.getPrimary ();
        log.info ("primary for patient {}", primary);
        return primary;
    }

    @Override
    public Database getDatabase() {
        Database database = super.getDatabase ();
        log.info ("database  patient {}", database.name ());
        return database;
    }

}
