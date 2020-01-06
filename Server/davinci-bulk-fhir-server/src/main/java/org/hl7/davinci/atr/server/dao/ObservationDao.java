package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafObservation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Observation;

public interface ObservationDao {
	
	 DafObservation getObservationById(int id);
	 
	 DafObservation getObservationByVersionId(int theId, String versionId);
	 
	 List<DafObservation> search(SearchParameterMap theMap);
	 
	 DafObservation createObservation(Observation theObservation);
	 
	 DafObservation updateObservationById(int theId, Observation theObservation);
	 
	 List<DafObservation> getObservationForBulkData(Date start, Date end);

	List<DafObservation> getObservationForPatientsBulkData(String id, Date start, Date end);
}
