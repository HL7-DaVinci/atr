package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafGoal;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Goal;

public interface GoalService {
	
	Goal getGoalById(int id);
	
	Goal getGoalByVersionId(int theId, String versionId);

	List<Goal> search(SearchParameterMap paramMap);
	
	DafGoal createGoal(Goal theGoal);
	
	DafGoal updateGoalById(int theId, Goal theGoal);
	
	List<Goal> getGoalsForBulkData(List<String> patients, Date start, Date end);
}
