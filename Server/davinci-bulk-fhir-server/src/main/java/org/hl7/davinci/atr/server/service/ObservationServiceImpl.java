package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.ObservationDao;
import org.hl7.davinci.atr.server.model.DafObservation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("observationService")
@Transactional
public class ObservationServiceImpl implements ObservationService {
	
	public static final String RESOURCE_TYPE = "Observation";
	
	@Autowired
	FhirContext fhirContext;
 
	@Autowired
    private ObservationDao observationDao;
	
	@Override
    @Transactional
    public Observation getObservationById(int id) {
		Observation  observation  = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafObservation  dafObservation  = observationDao.getObservationById(id);
		if(dafObservation  != null) {
			observation  = jsonParser.parseResource(Observation.class, dafObservation.getData());
			observation.setId(observation.getId());
		}
		return observation;
    }
	
	@Override
	@Transactional
	public Observation getObservationByVersionId(int theId, String versionId) {
		Observation  observation  = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafObservation  dafObservation  = observationDao.getObservationByVersionId(theId, versionId);
		if(dafObservation  != null) {
			observation  = jsonParser.parseResource(Observation.class, dafObservation.getData());
			observation.setId(observation.getId());
		}
		return observation;
	}
	
	@Override
    @Transactional
    public List<Observation> search(SearchParameterMap paramMap){
		Observation observation = null;
		List<Observation> observationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafObservation> dafObservationList = observationDao.search(paramMap);
		if(dafObservationList != null && !dafObservationList.isEmpty()) {
			for(DafObservation dafObservation : dafObservationList) {
				observation = jsonParser.parseResource(Observation.class, dafObservation.getData());
				observation.setId(observation.getId());
				observationList.add(observation);
			}
		}
		return observationList;
    }

	@Override
	public DafObservation createObservation(Observation theObservation) {
		return observationDao.createObservation(theObservation);
	}

	@Override
	public DafObservation updateObservationById(int theId, Observation theObservation) {
		return observationDao.updateObservationById(theId, theObservation);
	}
	
	@Override
    @Transactional
    public List<Observation> getObservationForBulkData(List<String> patients, Date start, Date end){
    	Observation observation = null;
		List<Observation> observationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser(); 
		List<DafObservation> dafObservationList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafObservation> list = observationDao.getObservationForPatientsBulkData(id, start, end);
				dafObservationList.addAll(list);
			}
		}
		else {
			dafObservationList = observationDao.getObservationForBulkData(start, end);
		}
		
		if(dafObservationList != null && !dafObservationList.isEmpty()) {
			for(DafObservation dafObservation : dafObservationList) {
				observation = jsonParser.parseResource(Observation.class, dafObservation.getData());
				observation.setId(observation.getId());
				observationList.add(observation);
			}
		}
		return observationList;
    }
}
