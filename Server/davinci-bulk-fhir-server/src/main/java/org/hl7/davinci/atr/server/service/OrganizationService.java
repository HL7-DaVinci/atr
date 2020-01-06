package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Organization;

public interface OrganizationService {

	Organization getOrganizationById(int id);

	Organization getOrganizationByVersionId(int theId, String versionId);
	
	List<Organization> search(SearchParameterMap theMap);

	DafOrganization createOrganization(Organization theOrganization);

	DafOrganization updateOrganizationById(int id, Organization theOrganization);
	
	List<Organization> getOrganizationForBulkData(List<String> patients, Date start, Date end);
}
