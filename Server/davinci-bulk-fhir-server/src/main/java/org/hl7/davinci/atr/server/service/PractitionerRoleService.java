package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.PractitionerRole;

public interface PractitionerRoleService {

	PractitionerRole getPractitionerRoleByVersionId(String id, String versionIdPart);

	PractitionerRole getPractitionerRoleById(String id);

	List<PractitionerRole> search(SearchParameterMap paramMap);

	List<PractitionerRole> getPractitionerRoleForBulkData(List<String> patientList, Date start, Date end);

	PractitionerRole getPractitionerRoleByIdentifier(String system, String value);
}
