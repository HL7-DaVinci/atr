package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafInsurancePlan;
import org.hl7.fhir.r4.model.InsurancePlan;

public interface InsurancePlanDao {
	DafInsurancePlan getInsurancePlanById(int id);
	 
	DafInsurancePlan getInsurancePlanByVersionId(int theId, String versionId);
	 
	DafInsurancePlan updateInsurancePlanById(int id, InsurancePlan theInsurancePlan);

	DafInsurancePlan createInsurancePlan(InsurancePlan theInsurancePlan);
	
	List<DafInsurancePlan> getInsurancePlanForBulkData(Date start, Date end);
	
	List<DafInsurancePlan> getInsurancePlanForPatientsBulkData(String patientId, Date start, Date end);
}
