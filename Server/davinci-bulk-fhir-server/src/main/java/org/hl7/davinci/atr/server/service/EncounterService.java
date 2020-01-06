package org.hl7.davinci.atr.server.service;

import org.hl7.davinci.atr.server.model.DafEncounter;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Encounter;

import java.util.Date;
import java.util.List;

public interface EncounterService {
	
	Encounter getEncounterById(int id);
	 
	Encounter getEncounterByVersionId(int theId, String versionId);
	 
	List<Encounter> search(SearchParameterMap theMap);
	
	DafEncounter createEncounter(Encounter theEncounter);
	
	DafEncounter updateEncounterById(int theId, Encounter theEncounter);

	List<Encounter> getEncounterForBulkData(List<String> patientList, Date start, Date end);
}
