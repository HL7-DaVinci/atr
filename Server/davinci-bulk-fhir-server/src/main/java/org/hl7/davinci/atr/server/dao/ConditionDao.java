package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCondition;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Condition;

public interface ConditionDao {
	
	DafCondition getConditionById(int id);
	
	DafCondition getConditionByVersionId(int theId, String versionId);

	List<DafCondition> search(SearchParameterMap theMap);
	
	DafCondition creatCondition(Condition theCondition);
	 
	DafCondition updateConditionById(int theId, Condition theCondition);
	
	List<DafCondition> getConditionForBulkData(Date start, Date end);

	List<DafCondition> getConditionForPatientsBulkData(String id, Date start, Date end);
}
