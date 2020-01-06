package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.GoalDao;
import org.hl7.davinci.atr.server.model.DafGoal;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("goalService")
@Transactional
public class GoalServiceImpl implements GoalService {
	
	public static final String RESOURCE_TYPE = "Goal";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private GoalDao goalDao;
	
	@Override
	public Goal getGoalById(int id) {
		Goal goal = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafGoal dafGoal = goalDao.getGoalById(id);
		if(dafGoal != null) {
			goal = jsonParser.parseResource(Goal.class, dafGoal.getData());
			goal.setId(new IdType(RESOURCE_TYPE, goal.getId()));
		}
		return goal;
	}

	@Override
	public Goal getGoalByVersionId(int theId, String versionId) {
		Goal goal = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafGoal dafGoal = goalDao.getGoalByVersionId(theId, versionId);
		if(dafGoal != null) {
			goal = jsonParser.parseResource(Goal.class, dafGoal.getData());
			goal.setId(new IdType(RESOURCE_TYPE, goal.getId()));
		}
		return goal;
	}

	@Override
	public List<Goal> search(SearchParameterMap paramMap) {
		Goal goal = null;
		List<Goal> goalList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafGoal> dafGoalList = goalDao.search(paramMap);
		if(dafGoalList != null && !dafGoalList.isEmpty()) {
			for(DafGoal dafGoal : dafGoalList) {
				goal = jsonParser.parseResource(Goal.class, dafGoal.getData());
				goal.setId(new IdType(RESOURCE_TYPE, goal.getId()));
				goalList.add(goal);
			}
		}
		return goalList;
	}

	@Override
	public DafGoal createGoal(Goal theGoal) {
		return goalDao.createGoal(theGoal);
	}

	@Override
	public DafGoal updateGoalById(int theId, Goal theGoal) {
		return goalDao.updateGoalById(theId, theGoal);
	}
	
	@Override
	@Transactional
	public List<Goal> getGoalsForBulkData(List<String> patients, Date start, Date end){
		Goal goal = null;
		List<Goal> goalList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafGoal> dafGoalList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafGoal> dafGoalObj = goalDao.getGoalsForPatientsBulkData(id, start, end);
				dafGoalList.addAll(dafGoalObj);
			}
		}
		else {
			dafGoalList = goalDao.getGoalsForBulkData(start, end);
		}
		if(dafGoalList != null && !dafGoalList.isEmpty()) {
			for(DafGoal dafGoal : dafGoalList) {
				goal = jsonParser.parseResource(Goal.class, dafGoal.getData());
				goal.setId(new IdType(RESOURCE_TYPE, goal.getId()));
				goalList.add(goal);
			}
		}
		return goalList;
	}
}
