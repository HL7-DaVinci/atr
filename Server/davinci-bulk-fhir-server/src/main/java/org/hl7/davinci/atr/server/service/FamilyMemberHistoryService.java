package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafFamilyMemberHistory;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.FamilyMemberHistory;

public interface FamilyMemberHistoryService {
	
	FamilyMemberHistory getFamilyMemberHistoryById(int id);
	
	FamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId);

	List<FamilyMemberHistory> search(SearchParameterMap paramMap);
	
	DafFamilyMemberHistory createFamilyMemberHistory(FamilyMemberHistory theFamilyMemberHistory);
	
	DafFamilyMemberHistory updateFamilyMemberHistoryById(int id, FamilyMemberHistory theFamilyMemberHistory);

	List<FamilyMemberHistory> getFamilyMemberHistoryForBulkDataRequest(List<String> patientList, Date start, Date end);
	
}
