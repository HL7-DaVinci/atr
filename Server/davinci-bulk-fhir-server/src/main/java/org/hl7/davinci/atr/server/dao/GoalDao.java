package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafGoal;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Goal;

public interface GoalDao {
	
	DafGoal getGoalById(int id);
	
	DafGoal getGoalByVersionId(int theId, String versionId);

	List<DafGoal> search(SearchParameterMap paramMap);
	
	DafGoal createGoal(Goal theGoal);
	
	DafGoal updateGoalById(int theId, Goal theGoal);
	
	List<DafGoal> getGoalsForBulkData(Date start, Date end);

	List<DafGoal> getGoalsForPatientsBulkData(String id, Date start, Date end);
}
