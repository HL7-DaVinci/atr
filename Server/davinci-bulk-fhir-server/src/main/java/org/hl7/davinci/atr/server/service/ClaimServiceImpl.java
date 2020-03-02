package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.ClaimDao;
import org.hl7.davinci.atr.server.model.DafClaim;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("claimService")
@Transactional
public class ClaimServiceImpl implements ClaimService {

public static final String RESOURCE_TYPE = "Claim";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private ClaimDao claimDao;
	
	@Override
	public Claim getClaimByVersionId(int id, String versionIdPart) {
		Claim claim = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafClaim dafClaim = claimDao.getClaimByVersionId(id, versionIdPart);
		if(dafClaim != null) {
			claim = jsonParser.parseResource(Claim.class, dafClaim.getData());
			claim.setId(claim.getId());
		}
		return claim;
	}

	@Override
	public Claim getClaimById(int id) {
		Claim claim = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafClaim dafClaim = claimDao.getClaimById(id);
		if(dafClaim != null) {
			claim = jsonParser.parseResource(Claim.class, dafClaim.getData());
			claim.setId(claim.getId());
		}
		return claim;
	}
	
	@Override
	public List<Claim> getClaimForBulkDataRequest(List<String> patientList, Date start, Date end) {
		Claim claim = null;
		List<Claim> claimList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafClaim> dafClaimList = new ArrayList<>();
		if(patientList != null) {
			for(String id:patientList) {
				List<DafClaim> list = claimDao.getClaimForPatientsBulkDataRequest(id, start, end);
				dafClaimList.addAll(list);
			}
		}
		else {
			dafClaimList = claimDao.getClaimForBulkDataRequest(start, end);
		}
		if(dafClaimList != null && !dafClaimList.isEmpty()) {
			for(DafClaim dafClaim : dafClaimList) {
				claim = jsonParser.parseResource(Claim.class, dafClaim.getData());
				claim.setId(claim.getId());
				claimList.add(claim);
			}
		}
		return claimList;
	}

	@Override
	public List<Claim> search(SearchParameterMap paramMap) {
		Claim claim = null;
		List<Claim> ClaimList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafClaim> dafClaimList = claimDao.search(paramMap);
		if(dafClaimList != null && !dafClaimList.isEmpty()) {
			for(DafClaim dafClaim : dafClaimList) {
				claim = jsonParser.parseResource(Claim.class, dafClaim.getData());
				claim.setId(claim.getId());
				ClaimList.add(claim);
			}
		}
		return ClaimList;
	}	

}
