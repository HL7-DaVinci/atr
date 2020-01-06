package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCareTeam;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CareTeam;

public interface CareTeamDao {
	
	 DafCareTeam getCareTeamById(int id);
	 
	 DafCareTeam getCareTeamByVersionId(int theId, String versionId);

	 List<DafCareTeam> search(SearchParameterMap paramMap);
	 	 
	 DafCareTeam creatCareTeam(CareTeam theCareTeam);
	 
	 DafCareTeam updateCareTeamById(int theId, CareTeam theCareTeam);

	 List<DafCareTeam> getCareTeamForBulkDataRequest(Date start, Date end);

	 List<DafCareTeam> getCareTeamForPatientsBulkDataRequest(String id, Date start, Date end);
}
