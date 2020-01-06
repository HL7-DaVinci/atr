package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafMedicationStatement;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.MedicationStatement;

public interface MedicationStatementDao {
	
	DafMedicationStatement getMedicationStatementById(int id);
	
	DafMedicationStatement getMedicationStatementByVersionId(int theId, String versionId);
		
	List<DafMedicationStatement> search(SearchParameterMap paramMap);
	
	DafMedicationStatement createMedicationStatement(MedicationStatement theMedicationStatement);
	
	DafMedicationStatement updateMedicationStatementById(int theId, MedicationStatement theMedicationStatement);
	
	List<DafMedicationStatement> getMedicationStatementForBulkData(Date start, Date end);

	List<DafMedicationStatement> getMedicationStatementForPatientsBulkData(String id, Date start, Date end);
}
