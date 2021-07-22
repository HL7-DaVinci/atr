package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Patient;

public interface PatientDao {
	
	DafPatient getPatientById(String id);

	DafPatient getPatientByVersionId(String theId, String versionId);

	Patient createPatient(Patient thePatient);

	DafPatient updatePatientById(int id, Patient thePatient);

	List<DafPatient> search(SearchParameterMap paramMap);
	
	DafPatient getPatientJsonForBulkData(String patientId, Date start, Date end);
	
	List<DafPatient> getAllPatientJsonForBulkData(Date start, Date end);

	DafPatient getPatientByMemeberId(String memberSystem, String memberId);
}
