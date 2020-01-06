package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.MedicationStatementDao;
import org.hl7.davinci.atr.server.model.DafMedicationStatement;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("medicationStatementService")
@Transactional
public class MedicationStatementServiceImpl implements MedicationStatementService {
	
	public static final String RESOURCE_TYPE = "MedicationStatement";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private MedicationStatementDao medicationStatementDao;

	@Override
	public MedicationStatement getMedicationStatementById(int theId) {
		MedicationStatement medicationStatement = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationStatement dafMedicationStatement = medicationStatementDao.getMedicationStatementById(theId);
		if(dafMedicationStatement != null) {
			medicationStatement = jsonParser.parseResource(MedicationStatement.class, dafMedicationStatement.getData());
			medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatement.getId()));
		}
		return medicationStatement;
	}

	@Override
	public MedicationStatement getMedicationStatementByVersionId(int theId, String versionId) {
		MedicationStatement medicationStatement = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationStatement dafMedicationStatement = medicationStatementDao.getMedicationStatementByVersionId(theId, versionId);
		if(dafMedicationStatement != null) {
			medicationStatement = jsonParser.parseResource(MedicationStatement.class, dafMedicationStatement.getData());
			medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatement.getId()));
		}
		return medicationStatement;
	}

	@Override
	@Transactional
	public List<MedicationStatement> search(SearchParameterMap searchParameterMap) {
		MedicationStatement medicationStatement = null;
		List<MedicationStatement> medicationStatementList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationStatement> dafMedicationStatementList = medicationStatementDao.search(searchParameterMap);
		if(dafMedicationStatementList != null && !dafMedicationStatementList.isEmpty()) {
			for(DafMedicationStatement dafMedication : dafMedicationStatementList) {
				medicationStatement = jsonParser.parseResource(MedicationStatement.class, dafMedication.getData());
				medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatement.getId()));
				medicationStatementList.add(medicationStatement);
			}
		}
		return medicationStatementList;
	}

	@Override
	public DafMedicationStatement createMedicationStatement(MedicationStatement theMedicationStatement) {
		return medicationStatementDao.createMedicationStatement(theMedicationStatement);
	}

	@Override
	public DafMedicationStatement updateMedicationStatementById(int theId, MedicationStatement theMedicationStatement) {
		return medicationStatementDao.updateMedicationStatementById(theId, theMedicationStatement);
	}
	
	@Override
    public List<MedicationStatement> getMedicationStatementForBulkData(List<String> patients, Date start, Date end){
    	MedicationStatement medicationStatement = null;
		List<MedicationStatement> medicationStatementList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationStatement> dafMedicationStatementList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafMedicationStatement> dafMedicationStatementObj = medicationStatementDao.getMedicationStatementForPatientsBulkData(id, start, end);
				dafMedicationStatementList.addAll(dafMedicationStatementObj);
			}
		}
		else {
			dafMedicationStatementList = medicationStatementDao.getMedicationStatementForBulkData(start, end);
		}
		if(dafMedicationStatementList != null && !dafMedicationStatementList.isEmpty()) {
			for(DafMedicationStatement dafMedication : dafMedicationStatementList) {
				medicationStatement = jsonParser.parseResource(MedicationStatement.class, dafMedication.getData());
				medicationStatement.setId(new IdType(RESOURCE_TYPE, medicationStatement.getId()));
				medicationStatementList.add(medicationStatement);
			}
		}
		return medicationStatementList;
    }
}
