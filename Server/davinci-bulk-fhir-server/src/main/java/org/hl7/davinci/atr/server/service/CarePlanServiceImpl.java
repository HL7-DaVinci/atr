package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.CarePlanDao;
import org.hl7.davinci.atr.server.model.DafCarePlan;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("carePlanService")
@Transactional
public class CarePlanServiceImpl implements CarePlanService {
	
	public static final String RESOURCE_TYPE = "CarePlan";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private CarePlanDao carePlanDao;

	@Override
	@Transactional
	public CarePlan getCarePlanById(int theId) {
		CarePlan carePlan = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCarePlan dafCarePlan = carePlanDao.getCarePlanById(theId);
		if(dafCarePlan != null) {
			carePlan = jsonParser.parseResource(CarePlan.class, dafCarePlan.getData());
			carePlan.setId(new IdType(RESOURCE_TYPE, carePlan.getId()));
		}
		return carePlan;
	}

	@Override
	@Transactional
	public CarePlan getCarePlanByVersionId(int theId, String versionId) {
		CarePlan carePlan = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCarePlan dafCarePlan = carePlanDao.getCarePlanByVersionId(theId, versionId);
		if(dafCarePlan != null) {
			carePlan = jsonParser.parseResource(CarePlan.class, dafCarePlan.getData());
			carePlan.setId(new IdType(RESOURCE_TYPE, carePlan.getId()));
		}
		return carePlan;
	}

	@Override
	public List<CarePlan> search(SearchParameterMap paramMap) {
		CarePlan carePlan = null;
		List<CarePlan> carePlanList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCarePlan> dafCarePlanList = carePlanDao.search(paramMap);
		if(dafCarePlanList != null && !dafCarePlanList.isEmpty()) {
			for(DafCarePlan dafCarePlan : dafCarePlanList) {
				carePlan = jsonParser.parseResource(CarePlan.class, dafCarePlan.getData());
				carePlan.setId(new IdType(RESOURCE_TYPE, carePlan.getId()));
				carePlanList.add(carePlan);
			}
		}
		return carePlanList;
	}

	@Override
	public DafCarePlan createCarePlan(CarePlan theCarePlan) {
		return carePlanDao.createCarePlan(theCarePlan);
	}

	@Override
	public DafCarePlan updateCarePlanById(int theId, CarePlan theCarePlan) {
		return carePlanDao.updateCarePlanById(theId, theCarePlan);
	}
	
	@Override
	public List<CarePlan> getCarePlanForBulkData(List<String> patients, Date start, Date end) {
		CarePlan carePlan = null;
		List<CarePlan> carePlanList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCarePlan> dafCarePlanList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafCarePlan> dafCarePlanObj = carePlanDao.getCarePlanForPatientsBulkData(id, start, end);
				dafCarePlanList.addAll(dafCarePlanObj);
			}
		}
		else {
			dafCarePlanList = carePlanDao.getCarePlanForBulkData(start, end);
		}
		if(dafCarePlanList != null && !dafCarePlanList.isEmpty()) {
			for(DafCarePlan dafCarePlan : dafCarePlanList) {
				carePlan = jsonParser.parseResource(CarePlan.class, dafCarePlan.getData());
				carePlan.setId(new IdType(RESOURCE_TYPE, carePlan.getId()));
				carePlanList.add(carePlan);
			}
		}
		return carePlanList;
	}
}
