package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Organization;

public interface OrganizationDao {
	
	DafOrganization getOrganizationById(int id);
	
	DafOrganization getOrganizationByVersionId(int theId, String versionId);
		
	List<DafOrganization> search(SearchParameterMap theMap);

	DafOrganization createOrganization(Organization theOrganization);

	DafOrganization updateOrganizationById(int id, Organization theOrganization);
	
	DafOrganization getOrganizationForBulkData(String patients, Date start, Date end);
}
