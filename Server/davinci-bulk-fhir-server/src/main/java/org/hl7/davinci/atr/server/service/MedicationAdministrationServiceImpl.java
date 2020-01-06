package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.MedicationAdministrationDao;
import org.hl7.davinci.atr.server.model.DafMedicationAdministration;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("medicationAdministrationService")
@Transactional
public class MedicationAdministrationServiceImpl implements MedicationAdministrationService {

	public static final String RESOURCE_TYPE = "MedicationAdministration";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private MedicationAdministrationDao medicationAdministrationDao;

	@Override
	@Transactional
	public MedicationAdministration getMedicationAdministrationById(int id) {
		MedicationAdministration medicationAdministration = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationAdministration dafMedicationAdministration = medicationAdministrationDao.getMedicationAdministrationById(id);
		if(dafMedicationAdministration != null) {
			medicationAdministration = jsonParser.parseResource(MedicationAdministration.class, dafMedicationAdministration.getData());
			medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationAdministration.getId()));
		}
		return medicationAdministration;
	}

	@Override
	@Transactional
	public MedicationAdministration getMedicationAdministrationByVersionId(int theId, String versionId) {
		MedicationAdministration medicationAdministration = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationAdministration dafMedicationAdministration = medicationAdministrationDao.getMedicationAdministrationByVersionId(theId, versionId);
		if(dafMedicationAdministration != null) {
			medicationAdministration = jsonParser.parseResource(MedicationAdministration.class, dafMedicationAdministration.getData());
			medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationAdministration.getId()));
		}
		return medicationAdministration;
	}

	@Override
	public List<MedicationAdministration> search(SearchParameterMap searchParameterMap) {
		MedicationAdministration medicationAdministration = null;
		List<MedicationAdministration> medicationAdministrationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationAdministration> dafMedicationAdministrationList = medicationAdministrationDao.search(searchParameterMap);
		if(dafMedicationAdministrationList != null && !dafMedicationAdministrationList.isEmpty()) {
			for(DafMedicationAdministration dafMedicationAdministration : dafMedicationAdministrationList) {
				medicationAdministration = jsonParser.parseResource(MedicationAdministration.class, dafMedicationAdministration.getData());
				medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationAdministration.getId()));
				medicationAdministrationList.add(medicationAdministration);
			}
		}
		return medicationAdministrationList;
	}

	@Override
	public DafMedicationAdministration createMedicationAdministration(
			MedicationAdministration theMedicationAdministration) {
		return medicationAdministrationDao.createMedicationAdministration(theMedicationAdministration);
	}

	@Override
	public DafMedicationAdministration updateMedicationAdministrationById(int theId,
			MedicationAdministration theMedicationAdministration) {
		return medicationAdministrationDao.updateMedicationAdministrationById(theId, theMedicationAdministration);
	}
	
	@Override
    public List<MedicationAdministration> getMedicationAdministrationForBulkData(List<String> patients, Date start, Date end) {
		MedicationAdministration medicationAdministration = null;
		List<MedicationAdministration> medicationAdministrationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationAdministration> dafMedicationAdministrationList =  new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafMedicationAdministration> dafMedicationAdministrationObj = medicationAdministrationDao.getMedicationAdministrationForPatientsBulkData(id, start, end);
				dafMedicationAdministrationList.addAll(dafMedicationAdministrationObj);
			}
		}
		else {
			dafMedicationAdministrationList = medicationAdministrationDao.getMedicationAdministrationForBulkData(start, end);
		}
		if(dafMedicationAdministrationList != null && !dafMedicationAdministrationList.isEmpty()) {
			for(DafMedicationAdministration dafMedicationAdministration : dafMedicationAdministrationList) {
				medicationAdministration = jsonParser.parseResource(MedicationAdministration.class, dafMedicationAdministration.getData());
				medicationAdministration.setId(new IdType(RESOURCE_TYPE, medicationAdministration.getId()));
				medicationAdministrationList.add(medicationAdministration);
			}
		}
		return medicationAdministrationList;
    }
}
