package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.OrganizationDao;
import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

public static final String RESOURCE_TYPE = "Organization";
	
	@Autowired
	FhirContext fhirContext;

	@Autowired
    private OrganizationDao organizationDao;
	
	@Override
    @Transactional
    public Organization getOrganizationById(int id) {
		Organization organization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafOrganization dafOrganization = organizationDao.getOrganizationById(id);
		if(dafOrganization != null) {
			organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
			organization.setId(organization.getId());
		}
		return organization;
    }

	@Override
	@Transactional
	public Organization getOrganizationByVersionId(int theId, String versionId) {
		Organization organization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafOrganization dafOrganization = organizationDao.getOrganizationByVersionId(theId, versionId);
		if(dafOrganization != null) {
			organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
			organization.setId(organization.getId());
		}
		return organization;
	}

	@Override
	@Transactional
	public List<Organization> search(SearchParameterMap searchParameterMap) {
		Organization organization = null;
		List<Organization> organizationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafOrganization> dafOrganizationList = organizationDao.search(searchParameterMap);
		if(dafOrganizationList != null && !dafOrganizationList.isEmpty()) {
			for(DafOrganization dafOrganization : dafOrganizationList) {
				organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
				organization.setId(organization.getId());
				organizationList.add(organization);
			}
		}
		return organizationList;
	}

	@Override
	public DafOrganization createOrganization(Organization theOrganization) {
		return organizationDao.createOrganization(theOrganization);
	}

	@Override
	public DafOrganization updateOrganizationById(int id, Organization theOrganization) {
		return organizationDao.updateOrganizationById(id, theOrganization);
	}
	
	@Override
    @Transactional
    public List<Organization> getOrganizationForBulkData(List<String> patients, Date start, Date end) {
    	Organization organization = null;
		List<Organization> organizationList = new ArrayList<>();
		List<DafOrganization> dafOrganizationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		for(String id: patients) {
			DafOrganization dafOrganization = organizationDao.getOrganizationForBulkData(id, start, end);
			dafOrganizationList.add(dafOrganization);
		}
		if(dafOrganizationList != null && !dafOrganizationList.isEmpty()) {
			for(DafOrganization dafOrganization : dafOrganizationList) {
				organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
				organization.setId(organization.getId());
				organizationList.add(organization);
			}
		}
		return organizationList;
    }
}
