package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafEncounter;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Encounter;

public interface EncounterDao {
	
	DafEncounter getEncounterById(int id);
	 
	DafEncounter getEncounterByVersionId(int theId, String versionId);
	 
	List<DafEncounter> search(SearchParameterMap theMap);
	
	DafEncounter createEncounter(Encounter theEncounter);
	
	DafEncounter updateEncounterById(int theId, Encounter theEncounter);
	List<DafEncounter> getEncounterForBulkData(Date start, Date end);
	List<DafEncounter> getEncounterForPatientsBulkData(String id, Date start, Date end);
}
