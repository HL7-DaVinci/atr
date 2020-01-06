package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationAdministration;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationAdministration;

public interface MedicationAdministrationDao {
	
	DafMedicationAdministration getMedicationAdministrationById(int id);
	
	DafMedicationAdministration getMedicationAdministrationByVersionId(int theId, String versionId);

	List<DafMedicationAdministration> search(SearchParameterMap paramMap);
	
	DafMedicationAdministration createMedicationAdministration(MedicationAdministration theMedicationAdministration);

	DafMedicationAdministration updateMedicationAdministrationById(int theId, MedicationAdministration theMedicationAdministration);

	List<DafMedicationAdministration> getMedicationAdministrationForBulkData(Date start, Date end);

	List<DafMedicationAdministration> getMedicationAdministrationForPatientsBulkData(String id, Date start, Date end);
}
