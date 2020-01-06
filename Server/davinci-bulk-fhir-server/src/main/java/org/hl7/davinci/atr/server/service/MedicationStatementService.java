package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationStatement;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationStatement;

public interface MedicationStatementService {
	
	MedicationStatement getMedicationStatementById(int id);
	
	MedicationStatement getMedicationStatementByVersionId(int theId, String versionId);
		
	List<MedicationStatement> search(SearchParameterMap paramMap);
	
	DafMedicationStatement createMedicationStatement(MedicationStatement theMedicationStatement);
	
	DafMedicationStatement updateMedicationStatementById(int theId, MedicationStatement theMedicationStatement);
	
	List<MedicationStatement> getMedicationStatementForBulkData(List<String> patients, Date start, Date end);
}
