package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafObservation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Observation;

public interface ObservationService {
	
	Observation getObservationById(int id);
	
	Observation getObservationByVersionId(int theId, String versionId);
		
	List<Observation> search(SearchParameterMap paramMap);
	
	DafObservation createObservation(Observation theObservation);
	
	DafObservation updateObservationById(int theId, Observation theObservation);
	
	List<Observation> getObservationForBulkData(List<String> patients, Date start, Date end);
}
