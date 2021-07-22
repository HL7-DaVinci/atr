package org.hl7.davinci.atr.server.configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.davinci.atr.server.providers.CapabilityStatementResourceProvider;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkServerConfiguration implements IResourceProvider, FhirRestfulServerCustomizer {

  private final Logger logger = LoggerFactory.getLogger(BulkServerConfiguration.class);

  @Override
  public void customize(RestfulServer server) {
    try {
      FhirContext.forR4();
      Collection<IResourceProvider> c = server.getResourceProviders();
      List<IResourceProvider> l = c.stream().filter(p -> p != this).collect(Collectors.toList());
      server.setServerConformanceProvider(new CapabilityStatementResourceProvider());
      server.setResourceProviders(l);
    } catch (FHIRException e) {
      logger.info("Error in Instantiating the FHIR Server:::::{}", e.getMessage());
    } finally {
      logger.info("In Finally Block");
    }
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return null;
  }
}
