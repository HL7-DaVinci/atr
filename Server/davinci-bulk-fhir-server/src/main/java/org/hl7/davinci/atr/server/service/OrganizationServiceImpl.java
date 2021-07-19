package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hl7.davinci.atr.server.dao.OrganizationDao;
import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);    

	@Autowired
	FhirContext fhirContext;

	@Autowired
    private OrganizationDao organizationDao;
	
    public Organization getOrganizationById(String id) {
		Organization organization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafOrganization dafOrganization = organizationDao.getOrganizationById(id);
		if(dafOrganization != null) {
			organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
			organization.setId(organization.getId());
		}
		return organization;
    }

	public Organization getOrganizationByVersionId(String theId, String versionId) {
		Organization organization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafOrganization dafOrganization = organizationDao.getOrganizationByVersionId(theId, versionId);
		if(dafOrganization != null) {
			organization = jsonParser.parseResource(Organization.class, dafOrganization.getData());
			organization.setId(organization.getId());
		}
		return organization;
	}

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

	public Organization createOrganization(Organization theOrganization) {
		return organizationDao.createOrganization(theOrganization);
	}

	public DafOrganization updateOrganizationById(int id, Organization theOrganization) {
		return organizationDao.updateOrganizationById(id, theOrganization);
	}
	
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

	public Organization getOrganizationByProviderIdentifier(String system, String value) {
		Organization organization = null;
		try {
			DafOrganization dafOrganization = organizationDao.getOrganizationByProviderIdentifier(system, value);
			if(dafOrganization != null) {
				organization = parseOrganization(dafOrganization.getData());
			}
		}
		catch(Exception e) {
			logger.error("Exception in getOrganizationByProviderNpi of OrganizationServiceImpl ", e);
	  	}
		return organization;
	}
	
	/**
     * Parses the string data to fhir Practitioner resource data
     * @param data
     * @return
     */
    private Organization parseOrganization(String data) {
    	Organization organization = null;
    	try {
			if(StringUtils.isNotBlank(data)) {
	    		IParser jsonParser = fhirContext.newJsonParser();
	    		organization = jsonParser.parseResource(Organization.class, data);
			}
    	}
  	  	catch(Exception e) {
  	  		logger.error("Exception in parsePractitioner of OrganizationServiceImpl ", e);
  	  	}
  	  	return organization;
    }
}
