package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafInsurancePlan;
import org.hl7.fhir.r4.model.InsurancePlan;

public interface InsurancePlanService {
	InsurancePlan getInsurancePlanById(int id);

	InsurancePlan getInsurancePlanByVersionId(int theId, String versionId);

	DafInsurancePlan updateInsurancePlanById(int id, InsurancePlan theInsurancePlan);

	DafInsurancePlan createInsurancePlan(InsurancePlan theInsurancePlan);

	List<InsurancePlan> getInsurancePlanForBulkData(List<String> patients, Date start, Date end);
}
