package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedication;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Medication;

public interface MedicationDao {
	
	DafMedication getMedicationById(int id);
	 
	DafMedication getMedicationByVersionId(int theId, String versionId);
	 
	List<DafMedication> search(SearchParameterMap theMap);
	 
	DafMedication createMedication(Medication theMedication);
	 
	DafMedication updateMedicationById(int theId, Medication theMedication);
	
	List<DafMedication> getMedicationForBulkData(List<String> patients, Date start, Date end);
}
