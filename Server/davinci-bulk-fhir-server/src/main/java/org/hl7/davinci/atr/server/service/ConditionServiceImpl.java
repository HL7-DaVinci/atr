package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.ConditionDao;
import org.hl7.davinci.atr.server.model.DafCondition;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("conditionService")
@Transactional
public class ConditionServiceImpl implements ConditionService {
	
	public static final String RESOURCE_TYPE = "Condition";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
	private ConditionDao conditionDao;

	@Override
	@Transactional
	public Condition getConditionById(int theId) {
		Condition condition = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCondition dafCondition = conditionDao.getConditionById(theId);
		if(dafCondition != null) {
			condition = jsonParser.parseResource(Condition.class, dafCondition.getData());
			condition.setId(new IdType(RESOURCE_TYPE, condition.getId()));
		}
		return condition;
	}

	@Override
	@Transactional
	public Condition getConditionByVersionId(int theId, String versionId) {
		Condition condition = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCondition dafCondition = conditionDao.getConditionByVersionId(theId, versionId);
		if(dafCondition != null) {
			condition = jsonParser.parseResource(Condition.class, dafCondition.getData());
			condition.setId(new IdType(RESOURCE_TYPE, condition.getId()));
		}
		return condition;
	}

	@Override
	@Transactional
	public List<Condition> search(SearchParameterMap paramMap) {
		Condition condition = null;
		List<Condition> conditionList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCondition> dafConditionList = conditionDao.search(paramMap);
		if(dafConditionList != null && !dafConditionList.isEmpty()) {
			for(DafCondition dafCondition : dafConditionList) {
				condition = jsonParser.parseResource(Condition.class, dafCondition.getData());
				condition.setId(new IdType(RESOURCE_TYPE, condition.getId()));
				conditionList.add(condition);
			}
		}
		return conditionList;
	}

	@Override
	public DafCondition createCondition(Condition theCondition) {
		return conditionDao.creatCondition(theCondition);
	}

	@Override
	public DafCondition updateConditionById(int theId, Condition theCondition) {
		return conditionDao.updateConditionById(theId, theCondition);
	}
	
	@Override
    public List<Condition> getConditionForBulkData(List<String> patients, Date start, Date end) {
		Condition condition = null;
		List<Condition> conditionList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCondition> dafConditionList  = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafCondition> dafConditionObj = conditionDao.getConditionForPatientsBulkData(id, start, end);
				dafConditionList.addAll(dafConditionObj);
			}
		}
		else {
			dafConditionList = conditionDao.getConditionForBulkData(start, end);
		}
		if(dafConditionList != null && !dafConditionList.isEmpty()) {
			for(DafCondition dafCondition : dafConditionList) {
				condition = jsonParser.parseResource(Condition.class, dafCondition.getData());
				condition.setId(new IdType(RESOURCE_TYPE, condition.getId()));
				conditionList.add(condition);
			}
		}
		return conditionList;
	}
}
