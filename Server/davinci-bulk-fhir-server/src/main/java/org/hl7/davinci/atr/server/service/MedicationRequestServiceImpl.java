package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.MedicationRequestDao;
import org.hl7.davinci.atr.server.model.DafMedicationRequest;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("medicationRequestService")
@Transactional
public class MedicationRequestServiceImpl implements MedicationRequestService {
	
	public static final String RESOURCE_TYPE = "MedicationRequest";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private MedicationRequestDao medicationRequestDao;
	
	@Transactional
    public MedicationRequest getMedicationRequestById(int id) {
		MedicationRequest medicationRequest = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationRequest dafMedicationRequest = medicationRequestDao.getMedicationRequestById(id);
		if(dafMedicationRequest != null) {
			medicationRequest = jsonParser.parseResource(MedicationRequest.class, dafMedicationRequest.getData());
			medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequest.getId()));
		}
		return medicationRequest;
    }
	
	@Transactional
	public MedicationRequest getMedicationRequestByVersionId(int theId, String versionId) {
		MedicationRequest medicationRequest = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafMedicationRequest dafMedicationRequest = medicationRequestDao.getMedicationRequestByVersionId(theId, versionId);
		if(dafMedicationRequest != null) {
			medicationRequest = jsonParser.parseResource(MedicationRequest.class, dafMedicationRequest.getData());
			medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequest.getId()));
		}
		return medicationRequest;
	}
	
	@Transactional
    public List<MedicationRequest> search(SearchParameterMap searchParameterMap){
		MedicationRequest medicationRequest = null;
		List<MedicationRequest> medicationRequestList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationRequest> dafMedicationRequestList = medicationRequestDao.search(searchParameterMap);
		if(dafMedicationRequestList != null && !dafMedicationRequestList.isEmpty()) {
			for(DafMedicationRequest dafMedicationRequest : dafMedicationRequestList) {
				medicationRequest = jsonParser.parseResource(MedicationRequest.class, dafMedicationRequest.getData());
				medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequest.getId()));
				medicationRequestList.add(medicationRequest);
			}
		}
		return medicationRequestList;
    }

	@Override
	public DafMedicationRequest createMedicationRequest(MedicationRequest theMedicationRequest) {
		return medicationRequestDao.createMedicationRequest(theMedicationRequest);
	}

	@Override
	public DafMedicationRequest updateMedicationRequestById(int theId, MedicationRequest theMedicationRequest) {
		return medicationRequestDao.updateMedicationRequestById(theId, theMedicationRequest);
	}
	
	@Override
    public List<MedicationRequest> getMedicationRequestForBulkData(List<String> patients, Date start, Date end) {
		MedicationRequest medicationRequest = null;
		List<MedicationRequest> medicationRequestList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafMedicationRequest> dafMedicationRequestList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafMedicationRequest> list = medicationRequestDao.getMedicationRequestForPatientsBulkData(id, start, end);
				dafMedicationRequestList.addAll(list);
			}
		}
		else {
			dafMedicationRequestList = medicationRequestDao.getMedicationRequestForBulkData(start, end);
		}
		if(dafMedicationRequestList != null && !dafMedicationRequestList.isEmpty()) {
			for(DafMedicationRequest dafMedicationRequest : dafMedicationRequestList) {
				medicationRequest = jsonParser.parseResource(MedicationRequest.class, dafMedicationRequest.getData());
				medicationRequest.setId(new IdType(RESOURCE_TYPE, medicationRequest.getId()));
				medicationRequestList.add(medicationRequest);
			}
		}
		return medicationRequestList;
    }
}
