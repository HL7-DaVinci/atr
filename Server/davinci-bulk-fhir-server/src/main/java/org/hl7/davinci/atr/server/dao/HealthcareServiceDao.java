package org.hl7.davinci.atr.server.dao;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafHealthcareService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.HealthcareService;

public interface HealthcareServiceDao {
	
	DafHealthcareService getHealthcareServiceById(int id);
	
	DafHealthcareService getHealthcareServiceByVersionId(int theId, String versionId);

	List<DafHealthcareService> search(SearchParameterMap paramMap);
	
	DafHealthcareService createHealthcareService(HealthcareService theHealthcareService);
	
	DafHealthcareService updateHealthcareServiceById(int theId, HealthcareService theHealthcareService);
}
