package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.InsurancePlanDao;
import org.hl7.davinci.atr.server.model.DafInsurancePlan;
import org.hl7.fhir.r4.model.InsurancePlan;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("InsurancePlanService")
@Transactional
public class InsurancePlanServiceImpl implements InsurancePlanService{


	public static final String RESOURCE_TYPE = "InsurancePlan";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private InsurancePlanDao insurancePlanDao;
	
	@Override
	public InsurancePlan getInsurancePlanById(int id) {
		InsurancePlan insurancePlan = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafInsurancePlan dafInsurancePlan = insurancePlanDao.getInsurancePlanById(id);
		if(dafInsurancePlan != null) {
			insurancePlan = jsonParser.parseResource(InsurancePlan.class, dafInsurancePlan.getData());
			insurancePlan.setId(new IdType(RESOURCE_TYPE, insurancePlan.getId()));
		}
		return insurancePlan;
	}

	@Override
	public InsurancePlan getInsurancePlanByVersionId(int theId, String versionId) {
		InsurancePlan insurancePlan = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafInsurancePlan dafInsurancePlan = insurancePlanDao.getInsurancePlanByVersionId(theId, versionId);
		if(dafInsurancePlan != null) {
			insurancePlan = jsonParser.parseResource(InsurancePlan.class, dafInsurancePlan.getData());
			insurancePlan.setId(insurancePlan.getId());
		}
		return insurancePlan;
	}

	@Override
	public DafInsurancePlan updateInsurancePlanById(int id, InsurancePlan theInsurancePlan) {
		return this.insurancePlanDao.updateInsurancePlanById(id, theInsurancePlan);
	}

	@Override
	public DafInsurancePlan createInsurancePlan(InsurancePlan theInsurancePlan) {
		return this.insurancePlanDao.createInsurancePlan(theInsurancePlan);
	}
	
	@Override
	public List<InsurancePlan> getInsurancePlanForBulkData(List<String> patients, Date start, Date end) {
		System.out.println("===========******IN ALLERGY SERVICE IMPL START******================");
		InsurancePlan InsurancePlan = null;
		List<InsurancePlan> InsurancePlanList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafInsurancePlan> dafInsurancePlanList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafInsurancePlan> dafAllergy = insurancePlanDao.getInsurancePlanForPatientsBulkData(id, start, end);
				dafInsurancePlanList.addAll(dafAllergy);
			}
		} else {
			dafInsurancePlanList = insurancePlanDao.getInsurancePlanForBulkData(start, end);
		}
		if(dafInsurancePlanList != null && !dafInsurancePlanList.isEmpty()) {
			for(DafInsurancePlan dafInsurancePlan : dafInsurancePlanList) {
				InsurancePlan = jsonParser.parseResource(InsurancePlan.class, dafInsurancePlan.getData());
				InsurancePlan.setId(InsurancePlan.getId());
				InsurancePlanList.add(InsurancePlan);
			}
		}
		return InsurancePlanList;
	}
}
