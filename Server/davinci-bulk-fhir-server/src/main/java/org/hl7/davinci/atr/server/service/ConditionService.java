package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCondition;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Condition;

public interface ConditionService {

	Condition getConditionById(int id);
	
	Condition getConditionByVersionId(int theId, String versionId);
		
	List<Condition> search(SearchParameterMap theMap);
	
	DafCondition createCondition(Condition theCondition);
	
	DafCondition updateConditionById(int theId, Condition theCondition);
	
	List<Condition> getConditionForBulkData(List<String> patients, Date start, Date end);
}
