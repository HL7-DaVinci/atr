package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafAllergyIntolerance;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.AllergyIntolerance;

public interface AllergyIntoleranceService {
	
	AllergyIntolerance getAllergyIntoleranceById(int id);
	
	AllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId);
		
	List<AllergyIntolerance> search(SearchParameterMap paramMap);

	DafAllergyIntolerance updateAllergyIntoleranceById(int id, AllergyIntolerance theAllergyIntolerance);

	DafAllergyIntolerance createAllergyIntolerance(AllergyIntolerance theAllergyIntolerance);
	
	List<AllergyIntolerance> getAllergyIntoleranceForBulkData(List<String> patients, Date start, Date end);
}
