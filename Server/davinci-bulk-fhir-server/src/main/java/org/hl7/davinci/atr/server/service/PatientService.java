package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Patient;

public interface PatientService {
	
	Patient getPatientById(int id);

	Patient getPatientByVersionId(int id, String versionIdPart);

	DafPatient createPatient(Patient thePatient);

	DafPatient updatePatientById(int id, Patient thePatient);

	List<Patient> search(SearchParameterMap paramMap);
	
	List<Patient> getPatientJsonForBulkData(List<String> patients, Date start, Date end);
}
