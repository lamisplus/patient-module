package org.lamisplus.modules.patient.config;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.hibernate.provider.HibernatePackageConfigurer;
import com.foreach.across.modules.hibernate.provider.HibernatePackageRegistry;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.BaseDomain;
import org.lamisplus.modules.patient.domain.PatientDomain;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ModuleConfiguration({"AcrossHibernateJpaModule"})
@Configuration
public class EntityScanConfiguration implements HibernatePackageConfigurer {

    public EntityScanConfiguration() {
    }

    public void configureHibernatePackage(HibernatePackageRegistry hibernatePackageRegistry) {
        hibernatePackageRegistry.addPackageToScan(new Class[]{PatientDomain.class, BaseDomain.class});
    }
}
