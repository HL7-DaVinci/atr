package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.MedicationDao;
import org.hl7.davinci.atr.server.model.DafMedication;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("medicationService")
@Transactional
public class MedicationServiceImpl implements MedicationService {
	
	public static final String RESOURCE_TYPE = "MedicationService";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private MedicationDao medicationDao;
	
	@Override
    public Medication getMedicationById(int theId) {
		Medication medication = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedication dafMedication = medicationDao.getMedicationById(theId);
		if(dafMedication != null) {
			medication = jsonParser.parseResource(Medication.class, dafMedication.getData());
			medication.setId(medication.getId());
		}
		return medication;
    }
	
	@Override
	public Medication getMedicationByVersionId(int theId, String versionId) {
		Medication medication = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedication dafMedication = medicationDao.getMedicationByVersionId(theId, versionId);
		if(dafMedication != null) {
			medication = jsonParser.parseResource(Medication.class, dafMedication.getData());
			medication.setId(medication.getId());
		}
		return medication;
	}
	
	@Override
    public List<Medication> search(SearchParameterMap searchParameterMap){
		Medication medication = null;
		List<Medication> medicationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedication> dafMedicationList = medicationDao.search(searchParameterMap);
		if(dafMedicationList != null && !dafMedicationList.isEmpty()) {
			for(DafMedication dafMedication : dafMedicationList) {
				medication = jsonParser.parseResource(Medication.class, dafMedication.getData());
				medication.setId(medication.getId());
				medicationList.add(medication);
			}
		}
		return medicationList;
    }

	@Override
	public DafMedication createMedication(Medication theMedication) {
		return medicationDao.createMedication(theMedication);
	}

	@Override
	public DafMedication updateMedicationById(int theId, Medication theMedication) {
		return medicationDao.updateMedicationById(theId, theMedication);
	}
	
	@Override
    public List<Medication> getMedicationForBulkData(List<String> patients, Date start, Date end) {
		Medication medication = null;
		List<Medication> medicationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedication> dafMedicationList = medicationDao.getMedicationForBulkData(patients, start, end);
		if(dafMedicationList != null && !dafMedicationList.isEmpty()) {
			for(DafMedication dafMedication : dafMedicationList) {
				medication = jsonParser.parseResource(Medication.class, dafMedication.getData());
				medication.setId(medication.getId());
				medicationList.add(medication);
			}
		}
		return medicationList;
    }
}
