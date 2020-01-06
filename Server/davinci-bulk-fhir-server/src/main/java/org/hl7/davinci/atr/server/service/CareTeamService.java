package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCareTeam;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CareTeam;

public interface CareTeamService {
	
	CareTeam getCareTeamById(int id);
	
	CareTeam getCareTeamByVersionId(int theId, String versionId);
	
	List<CareTeam> search(SearchParameterMap paramMap);
	
	DafCareTeam createCareTeam(CareTeam theCareTeam);
	
	DafCareTeam updateCareTeamById(int theId, CareTeam theCareTeam);

	List<CareTeam> getCareTeamForBulkDataRequest(List<String> patientList, Date start, Date end);
}
