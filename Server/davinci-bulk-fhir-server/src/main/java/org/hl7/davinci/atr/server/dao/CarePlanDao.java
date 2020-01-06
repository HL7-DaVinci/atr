package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCarePlan;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CarePlan;

public interface CarePlanDao {
	
	DafCarePlan getCarePlanById(int id);
	
	DafCarePlan getCarePlanByVersionId(int theId, String versionId);
	
	List<DafCarePlan> search(SearchParameterMap paramMap);
	
	DafCarePlan updateCarePlanById(int id, CarePlan theCarePlan);

	DafCarePlan createCarePlan(CarePlan theCarePlan);
	
	List<DafCarePlan> getCarePlanForBulkData(Date start, Date end);

	List<DafCarePlan> getCarePlanForPatientsBulkData(String patientId, Date start, Date end);
}
