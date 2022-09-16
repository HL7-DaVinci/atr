package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hl7.davinci.atr.server.dao.PractitionerDao;
import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("practitionerService")
@Transactional
public class PractitionerServiceImpl implements PractitionerService {
	private static final Logger logger = LoggerFactory.getLogger(PractitionerServiceImpl.class);    
	@Autowired
    private PractitionerDao practitionerDao;

	@Autowired
	FhirContext fhirContext;
	

	@Override
	public Practitioner getPractitionerByVersionId(String theId, String versionId) {
		Practitioner practitioner = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPractitioner dafPractitioner = practitionerDao.getPractitionerByVersionId(theId, versionId);
		if(dafPractitioner != null) {
			practitioner = jsonParser.parseResource(Practitioner.class, dafPractitioner.getData());
			practitioner.setId(practitioner.getId());
		}
		return practitioner;
	}

	@Override
	public Practitioner getPractitionerById(String theId) {
		Practitioner practitioner = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPractitioner dafPractitioner = practitionerDao.getPractitionerById(theId);
		if(dafPractitioner != null) {
			practitioner = jsonParser.parseResource(Practitioner.class, dafPractitioner.getData());
			practitioner.setId(practitioner.getId());
		}
		return practitioner;
	}
	
	@Override
	public Practitioner createPractitioner(Practitioner thePractitioner) {
		return practitionerDao.createPractitioner(thePractitioner);
	}

	@Override
	public DafPractitioner updatePractitionerById(int id, Practitioner thePractitioner) {
		return practitionerDao.updatePractitionerById(id, thePractitioner);
	}

	@Override
	public List<Practitioner> search(SearchParameterMap paramMap) {
		Practitioner practitioner = null;
		List<Practitioner> practitionerList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafPractitioner> dafPractitionerList = practitionerDao.search(paramMap);
		if(dafPractitionerList != null && !dafPractitionerList.isEmpty()) {
			for(DafPractitioner dafPractitioner : dafPractitionerList) {
				practitioner = jsonParser.parseResource(Practitioner.class, dafPractitioner.getData());
				practitioner.setId(practitioner.getId());
				practitionerList.add(practitioner);
			}
		}
		return practitionerList;
	}
	
	@Override
    @Transactional
    public List<Practitioner> getPractitionerForBulkData(List<String> practitioners, Date start, Date end){
    	Practitioner practitioner = null;
		List<Practitioner> practitionerList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafPractitioner> dafPractitionerList = new ArrayList<>();
		for(String id : practitioners) {
			DafPractitioner dafPractitioner = practitionerDao.getPractitionerForBulkData(id, start, end);
			dafPractitionerList.add(dafPractitioner);
		}
		if(dafPractitionerList != null && !dafPractitionerList.isEmpty()) {
			for(DafPractitioner dafPractitioner : dafPractitionerList) {
				practitioner = jsonParser.parseResource(Practitioner.class, dafPractitioner.getData());
				practitioner.setId(practitioner.getId());
				practitionerList.add(practitioner);
			}
		}
		return practitionerList;
    }

	@Override
	public Practitioner getPractitionerByProviderNpi(String providerNpiSystem, String providerNpi) {
		Practitioner practitioner = null;
		try {
			DafPractitioner dafPractitioner = practitionerDao.getPractitionerByProviderNpi(providerNpiSystem,providerNpi);
			if(dafPractitioner != null) {
				practitioner = parsePractitioner(dafPractitioner.getData());
			}
		}
		catch(Exception e) {
			logger.error("Exception in getPractitionerByProviderNpi of PractitionerServiceImpl ", e);
	  	}
		return practitioner;
	}
	
	/**
     * Parses the string data to fhir Practitioner resource data
     * @param data
     * @return
     */
    private Practitioner parsePractitioner(String data) {
    	Practitioner practitioner = null;
    	try {
			if(StringUtils.isNotBlank(data)) {
	    		IParser jsonParser = fhirContext.newJsonParser();
				practitioner = jsonParser.parseResource(Practitioner.class, data);
			}
    	}
  	  	catch(Exception e) {
  	  		logger.error("Exception in parsePractitioner of PractitionerServiceImpl ", e);
  	  	}
  	  	return practitioner;
    }
}
