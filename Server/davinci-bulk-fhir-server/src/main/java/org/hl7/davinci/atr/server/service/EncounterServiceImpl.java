package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.EncounterDao;
import org.hl7.davinci.atr.server.model.DafEncounter;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("EncounterService")
@Transactional
public class EncounterServiceImpl implements EncounterService {
	
	public static final String RESOURCE_TYPE = "Encounter";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private EncounterDao encounterDao;
	
	@Override
    @Transactional
    public Encounter getEncounterById(int id) {
		Encounter encounter = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafEncounter dafEncounter = encounterDao.getEncounterById(id);
		if(dafEncounter != null) {
			encounter = jsonParser.parseResource(Encounter.class, dafEncounter.getData());
			encounter.setId(new IdType(RESOURCE_TYPE, encounter.getId()));
		}
		return encounter;
    }
	
	@Override
	@Transactional
	public Encounter getEncounterByVersionId(int theId, String versionId) {
		Encounter encounter = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafEncounter dafEncounter = encounterDao.getEncounterByVersionId(theId, versionId);
		if(dafEncounter != null) {
			encounter = jsonParser.parseResource(Encounter.class, dafEncounter.getData());
			encounter.setId(new IdType(RESOURCE_TYPE, encounter.getId()));
		}
		return encounter;
	}
	
	@Override
    @Transactional
    public List<Encounter> search(SearchParameterMap paramMap){
		Encounter encounter = null;
		List<Encounter> encounterList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafEncounter> dafEncounterList = encounterDao.search(paramMap);
		if(dafEncounterList != null && !dafEncounterList.isEmpty()) {
			for(DafEncounter dafEncounter : dafEncounterList) {
				encounter = jsonParser.parseResource(Encounter.class, dafEncounter.getData());
				encounter.setId(new IdType(RESOURCE_TYPE, encounter.getId()));
				encounterList.add(encounter);
			}
		}
		return encounterList;
    }

	@Override
	public DafEncounter createEncounter(Encounter theEncounter) {
		return encounterDao.createEncounter(theEncounter);
	}

	@Override
	public DafEncounter updateEncounterById(int theId, Encounter theEncounter) {
		return encounterDao.updateEncounterById(theId, theEncounter);
	}

	@Override
	public List<Encounter> getEncounterForBulkData(List<String> patientList, Date start, Date end) {
		Encounter encounter = null;
		List<Encounter> encounterList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafEncounter> dafEncounterList = new ArrayList<>();
		if(patientList != null) {
			for(String id:patientList) {
				List<DafEncounter> list = encounterDao.getEncounterForPatientsBulkData(id, start, end);
				dafEncounterList.addAll(list);
			}
		}
		else {
			dafEncounterList = encounterDao.getEncounterForBulkData(start, end);
		}
		if(dafEncounterList != null && !dafEncounterList.isEmpty()) {
			for(DafEncounter dafEncounter : dafEncounterList) {
				encounter = jsonParser.parseResource(Encounter.class, dafEncounter.getData());
				encounter.setId(new IdType(RESOURCE_TYPE, encounter.getId()));
				encounterList.add(encounter);
			}
		}
		return encounterList;
	}
}
