package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationRequest;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationRequest;

public interface MedicationRequestService {

	MedicationRequest getMedicationRequestById(int id);

	MedicationRequest getMedicationRequestByVersionId(int theId, String versionId);

	List<MedicationRequest> search(SearchParameterMap paramMap);
	
	DafMedicationRequest createMedicationRequest(MedicationRequest theMedicationRequest);
	
	DafMedicationRequest updateMedicationRequestById(int theId, MedicationRequest theMedicationRequest);
	
	List<MedicationRequest> getMedicationRequestForBulkData(List<String> patients, Date start, Date end);
}
