package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.FamilyMemberHistoryDao;
import org.hl7.davinci.atr.server.model.DafFamilyMemberHistory;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("familyMemberHistoryService")
@Transactional
public class FamilyMemberHistoryServiceImpl implements FamilyMemberHistoryService {
	
	public static final String RESOURCE_TYPE = "FamilyMemberHistory";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private FamilyMemberHistoryDao familyMemberHistoryDao;

	@Override
    @Transactional
    public FamilyMemberHistory getFamilyMemberHistoryById(int id) {
		FamilyMemberHistory familyMemberHistory = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafFamilyMemberHistory dafFamilyMemberHistory = familyMemberHistoryDao.getFamilyMemberHistoryById(id);
		if(dafFamilyMemberHistory != null) {
			familyMemberHistory = jsonParser.parseResource(FamilyMemberHistory.class, dafFamilyMemberHistory.getData());
			familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistory.getId()));
		}
		return familyMemberHistory;
    }
	
	@Override
	@Transactional
	public FamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId) {
		FamilyMemberHistory familyMemberHistory = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafFamilyMemberHistory dafFamilyMemberHistory = familyMemberHistoryDao.getFamilyMemberHistoryByVersionId(theId, versionId);
		if(dafFamilyMemberHistory != null) {
			familyMemberHistory = jsonParser.parseResource(FamilyMemberHistory.class, dafFamilyMemberHistory.getData());
			familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistory.getId()));
		}
		return familyMemberHistory;
	}

	@Override
	@Transactional
	public List<FamilyMemberHistory> search(SearchParameterMap paramMap) {
		FamilyMemberHistory familyMemberHistory = null;
		List<FamilyMemberHistory> familyMemberHistoryList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafFamilyMemberHistory> dafFamilyMemberHistoryList = familyMemberHistoryDao.search(paramMap);
		if(dafFamilyMemberHistoryList != null && !dafFamilyMemberHistoryList.isEmpty()) {
			for(DafFamilyMemberHistory dafFamilyMemberHistory : dafFamilyMemberHistoryList) {
				familyMemberHistory = jsonParser.parseResource(FamilyMemberHistory.class, dafFamilyMemberHistory.getData());
				familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistory.getId()));
				familyMemberHistoryList.add(familyMemberHistory);
			}
		}
		return familyMemberHistoryList;
	}

	@Override
	public DafFamilyMemberHistory createFamilyMemberHistory(FamilyMemberHistory theFamilyMemberHistory) {
		return familyMemberHistoryDao.createFamilyMemberHistory(theFamilyMemberHistory);
	}

	@Override
	public DafFamilyMemberHistory updateFamilyMemberHistoryById(int theId, FamilyMemberHistory theFamilyMemberHistory) {
		return familyMemberHistoryDao.updateFamilyMemberHistoryById(theId, theFamilyMemberHistory);
	}

	@Override
	public List<FamilyMemberHistory> getFamilyMemberHistoryForBulkDataRequest(List<String> patientList, Date start,
			Date end) {
		FamilyMemberHistory familyMemberHistory = null;
		List<FamilyMemberHistory> familyMemberHistoryList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafFamilyMemberHistory> dafFamilyMemberHistoryList = new ArrayList<>();
		if(patientList != null) {
			for(String id:patientList) {
				List<DafFamilyMemberHistory> list = familyMemberHistoryDao.getFamilyMemberHistoryForPatientsBulkDataRequest(id,start, end);
				dafFamilyMemberHistoryList.addAll(list);
			}
		}
		else {
			dafFamilyMemberHistoryList = familyMemberHistoryDao.getFamilyMemberHistoryForBulkDataRequest(start, end);
		}
		if(dafFamilyMemberHistoryList != null && !dafFamilyMemberHistoryList.isEmpty()) {
			for(DafFamilyMemberHistory dafFamilyMemberHistory : dafFamilyMemberHistoryList) {
				familyMemberHistory = jsonParser.parseResource(FamilyMemberHistory.class, dafFamilyMemberHistory.getData());
				familyMemberHistory.setId(new IdType(RESOURCE_TYPE, familyMemberHistory.getId()));
				familyMemberHistoryList.add(familyMemberHistory);
			}
		}
		return familyMemberHistoryList;
	}
}