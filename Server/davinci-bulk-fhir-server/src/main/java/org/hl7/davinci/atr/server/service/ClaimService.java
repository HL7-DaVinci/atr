package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Claim;

public interface ClaimService {

	Claim getClaimByVersionId(int id, String versionIdPart);

	Claim getClaimById(int id);
	
	List<Claim> getClaimForBulkDataRequest(List<String> patientList, Date start, Date end);

	List<Claim> search(SearchParameterMap paramMap);
}
