package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafProcedure;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Procedure;

public interface ProcedureService {
	
	Procedure getProcedureById(int id);
	
	Procedure getProcedureByVersionId(int theId, String versionId);
		
	List<Procedure> search(SearchParameterMap paramMap);
	
	DafProcedure createProcedure(Procedure theProcedure);
	
	DafProcedure updateProcedureById(int theId, Procedure theProcedure);
	
	List<Procedure> getProcedureForBulkData(List<String> patients, Date start, Date end);
}
