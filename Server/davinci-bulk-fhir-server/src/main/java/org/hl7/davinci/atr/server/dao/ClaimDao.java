package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafClaim;
import org.hl7.davinci.atr.server.util.SearchParameterMap;

public interface ClaimDao {

	DafClaim getClaimById(int id);

	DafClaim getClaimByVersionId(int id, String versionIdPart);

	List<DafClaim> getClaimForBulkDataRequest(Date start, Date end);

	List<DafClaim> getClaimForPatientsBulkDataRequest(String id, Date start, Date end);

	List<DafClaim> search(SearchParameterMap paramMap);
}
