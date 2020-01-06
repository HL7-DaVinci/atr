package org.hl7.davinci.atr.server.service;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafHealthcareService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.HealthcareService;

public interface HealthcareServiceService {
	
	HealthcareService getHealthcareServiceById(int id);
	
	HealthcareService getHealthcareServiceByVersionId(int theId, String versionId);

	List<HealthcareService> search(SearchParameterMap paramMap);
	
	DafHealthcareService createHealthcareService(HealthcareService theHealthcareService);
	
	DafHealthcareService updateHealthcareServiceById(int theId, HealthcareService theHealthcareService);

}
