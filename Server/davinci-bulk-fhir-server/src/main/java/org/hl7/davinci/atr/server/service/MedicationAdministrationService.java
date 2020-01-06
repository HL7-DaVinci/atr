package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationAdministration;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationAdministration;

public interface MedicationAdministrationService {
	
	MedicationAdministration getMedicationAdministrationById(int id);
	
	MedicationAdministration getMedicationAdministrationByVersionId(int theId, String versionId);
		
	List<MedicationAdministration> search(SearchParameterMap paramMap);
	
	DafMedicationAdministration createMedicationAdministration(MedicationAdministration theMedicationAdministration);
	
	DafMedicationAdministration updateMedicationAdministrationById(int theId, MedicationAdministration theMedicationAdministration);
	
	List<MedicationAdministration> getMedicationAdministrationForBulkData(List<String> patients, Date start, Date end);
}
