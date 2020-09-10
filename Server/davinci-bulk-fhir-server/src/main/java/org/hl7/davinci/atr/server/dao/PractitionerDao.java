package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Practitioner;

public interface PractitionerDao {

	DafPractitioner getPractitionerByVersionId(int theId, String versionId);

	DafPractitioner getPractitionerById(int theId);
	
	DafPractitioner createPractitioner(Practitioner thePatient);

	DafPractitioner updatePractitionerById(int id, Practitioner thePatient);

	List<DafPractitioner> search(SearchParameterMap paramMap);

	DafPractitioner getPractitionerForBulkData(String patients, Date start, Date end);
}
