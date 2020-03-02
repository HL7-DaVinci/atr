package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.List;

import org.hl7.davinci.atr.server.dao.HealthcareServiceDao;
import org.hl7.davinci.atr.server.model.DafHealthcareService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.HealthcareService;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("healthcareServiceService")
@Transactional
public class HealthcareServiceServiceImpl implements HealthcareServiceService {
	
	public static final String RESOURCE_TYPE = "HealthcareService";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private HealthcareServiceDao healthcareServiceDao;

	@Override
	@Transactional
	public HealthcareService getHealthcareServiceById(int id) {
		HealthcareService healthcareService = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafHealthcareService dafHealthcareService = healthcareServiceDao.getHealthcareServiceById(id);
		if(dafHealthcareService != null) {
			healthcareService = jsonParser.parseResource(HealthcareService.class, dafHealthcareService.getData());
			healthcareService.setId(healthcareService.getId());
		}
		return healthcareService;
	}

	@Override
	@Transactional
	public HealthcareService getHealthcareServiceByVersionId(int theId, String versionId) {
		HealthcareService healthcareService = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafHealthcareService dafHealthcareService = healthcareServiceDao.getHealthcareServiceByVersionId(theId, versionId);
		if(dafHealthcareService != null) {
			healthcareService = jsonParser.parseResource(HealthcareService.class, dafHealthcareService.getData());
			healthcareService.setId(healthcareService.getId());
		}
		return healthcareService;
	}

	@Override
	public DafHealthcareService createHealthcareService(HealthcareService theHealthcareService) {
		return healthcareServiceDao.createHealthcareService(theHealthcareService);
	}

	@Override
	public DafHealthcareService updateHealthcareServiceById(int theId, HealthcareService theHealthcareService) {
		return healthcareServiceDao.updateHealthcareServiceById(theId, theHealthcareService);
	}
	
	@Override
	public List<HealthcareService> search(SearchParameterMap paramMap) {
		HealthcareService healthcareService = null;
		List<HealthcareService> healthcareServiceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafHealthcareService> dafHealthcareServiceList = healthcareServiceDao.search(paramMap);
		if(dafHealthcareServiceList != null && !dafHealthcareServiceList.isEmpty()) {
			for(DafHealthcareService dafHealthcareService : dafHealthcareServiceList) {
				healthcareService = jsonParser.parseResource(HealthcareService.class, dafHealthcareService.getData());
				healthcareService.setId(healthcareService.getId());
				healthcareServiceList.add(healthcareService);
			}
		}
		return healthcareServiceList;
	}
}
