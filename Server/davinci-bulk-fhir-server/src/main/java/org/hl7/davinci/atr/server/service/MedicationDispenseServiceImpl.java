package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.MedicationDispenseDao;
import org.hl7.davinci.atr.server.model.DafMedicationDispense;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("medicationDispenseService")
@Transactional
public class MedicationDispenseServiceImpl implements MedicationDispenseService {
	
	public static final String RESOURCE_TYPE = "MedicationDispense";
	
	@Autowired
	FhirContext fhirContext;

	@Autowired
    private MedicationDispenseDao medicationDispenseDao;

	@Override
	@Transactional
	public MedicationDispense getMedicationDispenseById(int id) {
		MedicationDispense medicationDispense = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationDispense dafMedicationDispense = medicationDispenseDao.getMedicationDispenseById(id);
		if(dafMedicationDispense != null) {
			medicationDispense = jsonParser.parseResource(MedicationDispense.class, dafMedicationDispense.getData());
			medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispense.getId()));
		}
		return medicationDispense;
	}

	@Override
	@Transactional
	public MedicationDispense getMedicationDispenseByVersionId(int theId, String versionId) {
		MedicationDispense medicationDispense = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationDispense dafMedicationDispense = medicationDispenseDao.getMedicationDispenseByVersionId(theId, versionId);
		if(dafMedicationDispense != null) {
			medicationDispense = jsonParser.parseResource(MedicationDispense.class, dafMedicationDispense.getData());
			medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispense.getId()));
		}
		return medicationDispense;
	}

	@Override
	@Transactional
	public List<MedicationDispense> search(SearchParameterMap searchParameterMap) {
		MedicationDispense medicationDispense = null;
		List<MedicationDispense> medicationDispenseList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationDispense> dafMedicationDispenseList = medicationDispenseDao.search(searchParameterMap);
		if(dafMedicationDispenseList != null && !dafMedicationDispenseList.isEmpty()) {
			for(DafMedicationDispense dafMedicationDispense : dafMedicationDispenseList) {
				medicationDispense = jsonParser.parseResource(MedicationDispense.class, dafMedicationDispense.getData());
				medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispense.getId()));
				medicationDispenseList.add(medicationDispense);
			}
		}
		return medicationDispenseList;
	}

	@Override
	public DafMedicationDispense createMedicationDispense(MedicationDispense theMedicationDispense) {
		return medicationDispenseDao.createMedicationDispense(theMedicationDispense);
	}

	@Override
	public DafMedicationDispense updateMedicationDispenseById(int theId, MedicationDispense theMedicationDispense) {
		return medicationDispenseDao.updateMedicationDispenseById(theId, theMedicationDispense);
	}
	
	@Override
    public List<MedicationDispense> getMedicationDispenseForBulkData(List<String> patients, Date start, Date end) {
		MedicationDispense medicationDispense = null;
		List<MedicationDispense> medicationDispenseList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationDispense> dafMedicationDispenseList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafMedicationDispense> list = medicationDispenseDao.getMedicationDispenseForPatientsBulkData(id, start, end);
				dafMedicationDispenseList.addAll(list);
			}
		}
		else {
			dafMedicationDispenseList = medicationDispenseDao.getMedicationDispenseForBulkData(start, end);
		}
		if(dafMedicationDispenseList != null && !dafMedicationDispenseList.isEmpty()) {
			for(DafMedicationDispense dafMedicationDispense : dafMedicationDispenseList) {
				medicationDispense = jsonParser.parseResource(MedicationDispense.class, dafMedicationDispense.getData());
				medicationDispense.setId(new IdType(RESOURCE_TYPE, medicationDispense.getId()));
				medicationDispenseList.add(medicationDispense);
			}
		}
		return medicationDispenseList;
    }
}
