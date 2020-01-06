package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafProcedure;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Procedure;

public interface ProcedureDao {
	
	 DafProcedure getProcedureById(int id);
	 
	 DafProcedure getProcedureByVersionId(int theId, String versionId);
	 
	 List<DafProcedure> search(SearchParameterMap theMap);
	 
	 DafProcedure createProcedure(Procedure theProcedure);
	 
	 DafProcedure updateProcedureById(int theId, Procedure theProcedure);

	List<DafProcedure> getProcedureForPatientsBulkData(String id, Date start, Date end);

	List<DafProcedure> getProcedureForBulkData(Date start, Date end);
}
