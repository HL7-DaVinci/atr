package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.PractitionerRoleDao;
import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.model.DafPractitionerRole;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("practitionerRoleService")
@Transactional
public class PractitionerRoleServiceImpl implements PractitionerRoleService {

	public static final String RESOURCE_TYPE = "PractitionerRole";
	
	@Autowired
    private PractitionerRoleDao practitionerRoleDao; 
	
	@Autowired
	FhirContext fhirContext;
	
	@Override
	public PractitionerRole getPractitionerRoleByVersionId(int id, String versionIdPart) {
		PractitionerRole practitionerRole = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPractitionerRole dafPractitioner = practitionerRoleDao.getPractitionerRoleByVersionId(id, versionIdPart);
		if(dafPractitioner != null) {
			practitionerRole = jsonParser.parseResource(PractitionerRole.class, dafPractitioner.getData());
			practitionerRole.setId(practitionerRole.getId());
		}
		return practitionerRole;
	}

	@Override
	public PractitionerRole getPractitionerRoleById(int id) {
		PractitionerRole practitionerRole = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafPractitionerRole dafPractitioner = practitionerRoleDao.getPractitionerRoleById(id);
		if(dafPractitioner != null) {
			practitionerRole = jsonParser.parseResource(PractitionerRole.class, dafPractitioner.getData());
			practitionerRole.setId(practitionerRole.getId());
		}
		return practitionerRole;
	}

	@Override
	public List<PractitionerRole> search(SearchParameterMap paramMap) {
		PractitionerRole practitionerRole = null;
		List<PractitionerRole> practitionerRoleList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafPractitionerRole> dafPractitionerRoleList = practitionerRoleDao.search(paramMap);
		if(dafPractitionerRoleList != null && !dafPractitionerRoleList.isEmpty()) {
			for(DafPractitionerRole dafPractitioner : dafPractitionerRoleList) {
				practitionerRole = jsonParser.parseResource(PractitionerRole.class, dafPractitioner.getData());
				practitionerRole.setId(practitionerRole.getId());
				practitionerRoleList.add(practitionerRole);
			}
		}
		return practitionerRoleList;
	}

	@Override
	public List<PractitionerRole> getPractitionerRoleForBulkData(List<String> patientList, Date start, Date end) {
		PractitionerRole practitionerRole = null;
		List<PractitionerRole> practitionerRoleList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafPractitionerRole> dafPractitionerRoleList = practitionerRoleDao.getPractitionerRoleForBulkData(patientList, start, end);
		if(dafPractitionerRoleList != null && !dafPractitionerRoleList.isEmpty()) {
			for(DafPractitionerRole dafPractitioner : dafPractitionerRoleList) {
				practitionerRole = jsonParser.parseResource(PractitionerRole.class, dafPractitioner.getData());
				practitionerRole.setId(practitionerRole.getId());
				practitionerRoleList.add(practitionerRole);
			}
		}
		return practitionerRoleList;
	}
}