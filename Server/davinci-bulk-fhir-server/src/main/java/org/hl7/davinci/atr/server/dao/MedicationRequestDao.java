package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationDispense;
import org.hl7.davinci.atr.server.model.DafMedicationRequest;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationRequest;

public interface MedicationRequestDao {

	DafMedicationRequest getMedicationRequestById(int id);

	DafMedicationRequest getMedicationRequestByVersionId(int theId, String versionId);

	List<DafMedicationRequest> search(SearchParameterMap paramMap);
	
	DafMedicationRequest createMedicationRequest(MedicationRequest theMedicationRequest);
	
	DafMedicationRequest updateMedicationRequestById(int theId, MedicationRequest theMedicationRequest);
	List<DafMedicationRequest> getMedicationRequestForBulkData(Date start, Date end);
	
	List<DafMedicationRequest> getMedicationRequestForPatientsBulkData(String id, Date start, Date end);
}
