package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.CareTeamDao;
import org.hl7.davinci.atr.server.model.DafCareTeam;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("careTeamService")
@Transactional
public class CareTeamServiceImpl implements CareTeamService {
	
	public static final String RESOURCE_TYPE = "CareTeam";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private CareTeamDao careTeamDao;

	@Override
	@Transactional
	public CareTeam getCareTeamById(int theId) {
		CareTeam careTeam = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCareTeam dafCareTeam = careTeamDao.getCareTeamById(theId);
		if(dafCareTeam != null) {
			careTeam = jsonParser.parseResource(CareTeam.class, dafCareTeam.getData());
			careTeam.setId(new IdType(RESOURCE_TYPE, careTeam.getId()));
		}
		return careTeam;
	}

	@Override
	@Transactional
	public CareTeam getCareTeamByVersionId(int theId, String versionId) {
		CareTeam careTeam = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCareTeam dafCareTeam = careTeamDao.getCareTeamByVersionId(theId, versionId);
		if(dafCareTeam != null) {
			careTeam = jsonParser.parseResource(CareTeam.class, dafCareTeam.getData());
			careTeam.setId(new IdType(RESOURCE_TYPE, careTeam.getId()));
		}
		return careTeam;
	}
	
	@Override
    @Transactional
    public List<CareTeam> search(SearchParameterMap paramMap){
		CareTeam careTeam = null;
		List<CareTeam> careTeamList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCareTeam> dafCareTeamList = careTeamDao.search(paramMap);
		if(dafCareTeamList != null && !dafCareTeamList.isEmpty()) {
			for(DafCareTeam dafCareTeam : dafCareTeamList) {
				careTeam = jsonParser.parseResource(CareTeam.class, dafCareTeam.getData());
				careTeam.setId(new IdType(RESOURCE_TYPE, careTeam.getId()));
				careTeamList.add(careTeam);
			}
		}
		return careTeamList;
    }


	@Override
	public DafCareTeam createCareTeam(CareTeam theCareTeam) {
		return careTeamDao.creatCareTeam(theCareTeam);
	}

	@Override
	public DafCareTeam updateCareTeamById(int theId, CareTeam theCareTeam) {
		return careTeamDao.updateCareTeamById(theId, theCareTeam);
	}

	@Override
	public List<CareTeam> getCareTeamForBulkDataRequest(List<String> patientList, Date start, Date end) {
		CareTeam careTeam = null;
		List<CareTeam> careTeamList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCareTeam> dafCareTeamList = new ArrayList<>();
		if(patientList != null) {
			for(String id:patientList) {
				List<DafCareTeam> list = careTeamDao.getCareTeamForPatientsBulkDataRequest(id, start, end);
				dafCareTeamList.addAll(list);
			}
		}
		else {
			dafCareTeamList = careTeamDao.getCareTeamForBulkDataRequest(start, end);
		}
		if(dafCareTeamList != null && !dafCareTeamList.isEmpty()) {
			for(DafCareTeam dafCareTeam : dafCareTeamList) {
				careTeam = jsonParser.parseResource(CareTeam.class, dafCareTeam.getData());
				careTeam.setId(new IdType(RESOURCE_TYPE, careTeam.getId()));
				careTeamList.add(careTeam);
			}
		}
		return careTeamList;
	}	
}
