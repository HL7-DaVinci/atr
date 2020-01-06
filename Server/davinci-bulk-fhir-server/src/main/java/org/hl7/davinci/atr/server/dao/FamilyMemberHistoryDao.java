package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafFamilyMemberHistory;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.FamilyMemberHistory;

public interface FamilyMemberHistoryDao {
	
	DafFamilyMemberHistory getFamilyMemberHistoryById(int id);
	
	DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId);

	List<DafFamilyMemberHistory> search(SearchParameterMap paramMap);
	
	DafFamilyMemberHistory createFamilyMemberHistory(FamilyMemberHistory theFamilyMemberHistory);
	
	DafFamilyMemberHistory updateFamilyMemberHistoryById(int theId, FamilyMemberHistory theFamilyMemberHistory);

	List<DafFamilyMemberHistory> getFamilyMemberHistoryForBulkDataRequest(Date start, Date end);

	List<DafFamilyMemberHistory> getFamilyMemberHistoryForPatientsBulkDataRequest(String id, Date start, Date end);

}
