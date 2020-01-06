package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafAllergyIntolerance;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.AllergyIntolerance;

public interface AllergyIntoleranceDao {
	
	DafAllergyIntolerance getAllergyIntoleranceById(int id);
	 
	DafAllergyIntolerance getAllergyIntoleranceByVersionId(int theId, String versionId);
	 
	List<DafAllergyIntolerance> search(SearchParameterMap theMap);
	 
	DafAllergyIntolerance updateAllergyIntoleranceById(int id, AllergyIntolerance theAllergyIntolerance);

	DafAllergyIntolerance createAllergyIntolerance(AllergyIntolerance theAllergyIntolerance);
	
	List<DafAllergyIntolerance> getAllergyIntoleranceForBulkData(Date start, Date end);
	
	List<DafAllergyIntolerance> getAllergyIntoleranceForPatientsBulkData(String patientId, Date start, Date end);
}
