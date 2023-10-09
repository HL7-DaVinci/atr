package org.hl7.davinci.atr.server;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.davinci.atr.server.providers.CapabilityStatementResourceProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class DavinciFHIRApplication extends SpringBootServletInitializer implements IResourceProvider, FhirRestfulServerCustomizer{

	public static void main(String[] args) {
		SpringApplication.run(DavinciFHIRApplication.class, args);
	}
	
	@Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }
	
	@Override
	public void customize(RestfulServer server) {
		try {
			FhirContext.forR4();
			Collection<IResourceProvider> c = server.getResourceProviders();			
			List<IResourceProvider> l = c.stream().filter(p -> p != this).collect(Collectors.toList());

//			for (IResourceProvider resourceProvider : l) {
//				System.out.println(resourceProvider.getResourceType().getCanonicalName());
//			}
			server.setServerConformanceProvider(new CapabilityStatementResourceProvider());
			server.setResourceProviders(l);
//			FifoMemoryPagingProvider pp = new FifoMemoryPagingProvider(10);
//			pp.setDefaultPageSize(10);
//			pp.setMaximumPageSize(100);
//			server.setPagingProvider(pp);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return null;
	}
}
