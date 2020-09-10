package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.PractitionerDao;
import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("practitionerService")
@Transactional
public class PractitionerServiceImpl implements PractitionerService {

	public static final String RESOURCE_TYPE = "Practitioner";

	@Autowired
    private PractitionerDao practitionerDao;

	@Autowired
	FhirContext fhirContext;
	

	@Override
	public Practitioner getPractitionerByVersionId(int theId, String versionId) {
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
	public Practitioner getPractitionerById(int theId) {
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
	public DafPractitioner createPractitioner(Practitioner thePractitioner) {
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
}
