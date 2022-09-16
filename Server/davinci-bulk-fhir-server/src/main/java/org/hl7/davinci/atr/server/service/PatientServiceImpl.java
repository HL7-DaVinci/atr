package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hl7.davinci.atr.server.dao.PatientDao;
import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("patientService")
@Transactional
public class PatientServiceImpl implements PatientService {
	private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);    
	public static final String RESOURCE_TYPE = "Patient";

	@Autowired
    private PatientDao patientDao;

	@Autowired
	FhirContext fhirContext;
	
	@Override
	public Patient getPatientByVersionId(String theId, String versionId) {
		Patient patient = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPatient dafPatient = patientDao.getPatientByVersionId(theId, versionId);
		if(dafPatient != null) {
			patient = jsonParser.parseResource(Patient.class, dafPatient.getData());
			patient.setId(patient.getId());
		}
		return patient;
	}

	@Override
	public Patient getPatientById(String theId) {
		Patient patient = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPatient dafPatient = patientDao.getPatientById(theId);
		if(dafPatient != null) {
			patient = jsonParser.parseResource(Patient.class, dafPatient.getData());
			patient.setId(patient.getId());
		}
		return patient;
	}

	@Override
	public Patient createPatient(Patient thePatient) {
		return patientDao.createPatient(thePatient);
	}

	@Override
	public DafPatient updatePatientById(int id, Patient thePatient) {
		return patientDao.updatePatientById(id, thePatient);
	}

	@Override
	public List<Patient> search(SearchParameterMap paramMap) {
		Patient patient = null;
		List<Patient> patientList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafPatient> dafPatientList = patientDao.search(paramMap);
		if(dafPatientList != null && !dafPatientList.isEmpty()) {
			for(DafPatient dafPatient : dafPatientList) {
				patient = jsonParser.parseResource(Patient.class, dafPatient.getData());
				patient.setId(patient.getId());
				patientList.add(patient);
			}
		}
		return patientList;
	}
	
	@Override
	@Transactional
    public List<Patient> getPatientJsonForBulkData(List<String> patients, Date start, Date end) {
    	Patient patient = null;
		List<Patient> patientList = new ArrayList<>();
		List<DafPatient> dafPatientList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		if(patients != null) {
			 
			for(String id : patients) {
				DafPatient dafPatientObj = patientDao.getPatientJsonForBulkData(id, start, end);
				dafPatientList.add(dafPatientObj);
			}
		}
		else {
			dafPatientList = patientDao.getAllPatientJsonForBulkData(start, end);
		}
		
		if(dafPatientList != null && !dafPatientList.isEmpty()) {
			for(DafPatient dafPatient : dafPatientList) {
				patient = jsonParser.parseResource(Patient.class, dafPatient.getData());
				patient.setId(patient.getId());
				patientList.add(patient);
			}
		}
		return patientList;
    }

	@Override
	public Patient getPatientByMemeberId(String memberSystem, String memberId) {
		Patient patient = null;
		try {
			DafPatient dafPatient = patientDao.getPatientByMemeberId(memberSystem, memberId);
			if(dafPatient != null) {
				patient = parsePatient(dafPatient.getData());
			}
		}
		catch(Exception e) {
			logger.error("Exception in getPatientByMemeberId of PatientServiceImpl ", e);
	  	}
		return patient;
	}
	
	/**
     * Parses the string data to fhir Patient resource data
     * @param data
     * @return
     */
    private Patient parsePatient(String data) {
    	Patient patient = null;
    	try {
			if(StringUtils.isNotBlank(data)) {
	    		IParser jsonParser = fhirContext.newJsonParser();
				patient = jsonParser.parseResource(Patient.class, data);
			}
    	}
  	  	catch(Exception e) {
  	  		logger.error("Exception in parsePatient of PatientServiceImpl ", e);
  	  	}
  	  	return patient;
    }
}
