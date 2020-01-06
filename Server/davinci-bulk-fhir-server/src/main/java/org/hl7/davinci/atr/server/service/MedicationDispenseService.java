package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationDispense;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationDispense;

public interface MedicationDispenseService {
	
	MedicationDispense getMedicationDispenseById(int id);
	
	MedicationDispense getMedicationDispenseByVersionId(int theId, String versionId);
		
	List<MedicationDispense> search(SearchParameterMap paramMap);
	
	DafMedicationDispense createMedicationDispense(MedicationDispense theMedicationDispense);
	
	DafMedicationDispense updateMedicationDispenseById(int theId, MedicationDispense theMedicationDispense);
	
	List<MedicationDispense> getMedicationDispenseForBulkData(List<String> patients, Date start, Date end);
}
