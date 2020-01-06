package org.hl7.davinci.atr.server.service;

import org.hl7.davinci.atr.server.dao.AllergyIntoleranceDao;
import org.hl7.davinci.atr.server.model.DafAllergyIntolerance;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("AllergyIntoleranceService")
@Transactional
public class AllergyIntoleranceServiceImpl implements AllergyIntoleranceService {
	
	public static final String RESOURCE_TYPE = "AllergyIntolerance";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private AllergyIntoleranceDao allergyIntoleranceDao;
	
	@Override
    @Transactional
    public List<AllergyIntolerance> search(SearchParameterMap paramMap){
        AllergyIntolerance allergyIntolerance = null;
		List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafAllergyIntolerance> dafAllergyIntoleranceList = allergyIntoleranceDao.search(paramMap);
		if(dafAllergyIntoleranceList != null && !dafAllergyIntoleranceList.isEmpty()) {
			for(DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
				allergyIntolerance = jsonParser.parseResource(AllergyIntolerance.class, dafAllergyIntolerance.getData());
				allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntolerance.getId()));
				allergyIntoleranceList.add(allergyIntolerance);
			}
		}
		return allergyIntoleranceList;
    }

	@Override
	public AllergyIntolerance getAllergyIntoleranceById(int id) {
		AllergyIntolerance allergyIntolerance = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafAllergyIntolerance dafAllergyIntolerance = allergyIntoleranceDao.getAllergyIntoleranceById(id);
		if(dafAllergyIntolerance != null) {
			allergyIntolerance = jsonParser.parseResource(AllergyIntolerance.class, dafAllergyIntolerance.getData());
			allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntolerance.getId()));
		}
		return allergyIntolerance;
	}

	@Override
	public AllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId) {
		AllergyIntolerance allergyIntolerance = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafAllergyIntolerance dafAllergyIntolerance = allergyIntoleranceDao.getAllergyIntoleranceByVersionId(theId, versionId);
		if(dafAllergyIntolerance != null) {
			allergyIntolerance = jsonParser.parseResource(AllergyIntolerance.class, dafAllergyIntolerance.getData());
			allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntolerance.getId()));
		}
		return allergyIntolerance;
	}

	@Override
	public DafAllergyIntolerance updateAllergyIntoleranceById(int id, AllergyIntolerance theAllergyIntolerance) {
		return this.allergyIntoleranceDao.updateAllergyIntoleranceById(id, theAllergyIntolerance);
	}

	@Override
	public DafAllergyIntolerance createAllergyIntolerance(AllergyIntolerance theAllergyIntolerance) {
		return this.allergyIntoleranceDao.createAllergyIntolerance(theAllergyIntolerance);
	}
	
	@Override
	public List<AllergyIntolerance> getAllergyIntoleranceForBulkData(List<String> patients, Date start, Date end) {
		System.out.println("===========******IN ALLERGY SERVICE IMPL START******================");
		AllergyIntolerance allergyIntolerance = null;
		List<AllergyIntolerance> allergyIntoleranceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafAllergyIntolerance> dafAllergyIntoleranceList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafAllergyIntolerance> dafAllergy = allergyIntoleranceDao.getAllergyIntoleranceForPatientsBulkData(id, start, end);
				dafAllergyIntoleranceList.addAll(dafAllergy);
			}
		} else {
			dafAllergyIntoleranceList = allergyIntoleranceDao.getAllergyIntoleranceForBulkData(start, end);
		}
		if(dafAllergyIntoleranceList != null && !dafAllergyIntoleranceList.isEmpty()) {
			for(DafAllergyIntolerance dafAllergyIntolerance : dafAllergyIntoleranceList) {
				allergyIntolerance = jsonParser.parseResource(AllergyIntolerance.class, dafAllergyIntolerance.getData());
				allergyIntolerance.setId(new IdType(RESOURCE_TYPE, allergyIntolerance.getId()));
				allergyIntoleranceList.add(allergyIntolerance);
			}
		}
		return allergyIntoleranceList;
	}
}