package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Patient;

public interface PatientService {
	
	Patient getPatientById(String id);

	Patient getPatientByVersionId(String id, String versionIdPart);

	Patient createPatient(Patient thePatient);

	DafPatient updatePatientById(int id, Patient thePatient);

	List<Patient> search(SearchParameterMap paramMap);
	
	List<Patient> getPatientJsonForBulkData(List<String> patients, Date start, Date end);
	
	Patient getPatientByMemeberId(String memberSystem, String memberId);
}
