package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Organization;

public interface OrganizationDao {
	
	DafOrganization getOrganizationById(String id);
	
	DafOrganization getOrganizationByVersionId(String theId, String versionId);
		
	List<DafOrganization> search(SearchParameterMap theMap);

	Organization createOrganization(Organization theOrganization);

	DafOrganization updateOrganizationById(int id, Organization theOrganization);
	
	DafOrganization getOrganizationForBulkData(String patients, Date start, Date end);

	DafOrganization getOrganizationByProviderIdentifier(String system, String value);
}
