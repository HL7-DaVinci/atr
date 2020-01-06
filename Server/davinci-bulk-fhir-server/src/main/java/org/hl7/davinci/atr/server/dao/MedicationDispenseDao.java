package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationDispense;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationDispense;

public interface MedicationDispenseDao {
	
	DafMedicationDispense getMedicationDispenseById(int id);
	
	DafMedicationDispense getMedicationDispenseByVersionId(int theId, String versionId);
		
	List<DafMedicationDispense> search(SearchParameterMap paramMap);
	
	DafMedicationDispense createMedicationDispense(MedicationDispense theMedicationDispense);
	
	DafMedicationDispense updateMedicationDispenseById(int theId, MedicationDispense theMedicationDispense);
	
	List<DafMedicationDispense> getMedicationDispenseForBulkData(Date start, Date end);
	
	List<DafMedicationDispense> getMedicationDispenseForPatientsBulkData(String id, Date start, Date end);
}
