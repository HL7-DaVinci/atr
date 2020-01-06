package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedication;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Medication;

public interface MedicationService {
	
	Medication getMedicationById(int id);
	
	Medication getMedicationByVersionId(int theId, String versionId);
		
	List<Medication> search(SearchParameterMap paramMap);
	
	DafMedication createMedication(Medication theMedication);
	
	DafMedication updateMedicationById(int theId, Medication theMedication);
	
	List<Medication> getMedicationForBulkData(List<String> patients, Date start, Date end);
}
