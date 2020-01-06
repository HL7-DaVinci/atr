package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCarePlan;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CarePlan;

public interface CarePlanService {
	
	CarePlan getCarePlanById(int id);
	
	CarePlan getCarePlanByVersionId(int theId, String versionId);
		
	List<CarePlan> search(SearchParameterMap paramMap);
	
	DafCarePlan createCarePlan(CarePlan theCarePlan);
	
	DafCarePlan updateCarePlanById(int theId, CarePlan theCarePlan);
	
	List<CarePlan> getCarePlanForBulkData(List<String> patients, Date start, Date end);
}
