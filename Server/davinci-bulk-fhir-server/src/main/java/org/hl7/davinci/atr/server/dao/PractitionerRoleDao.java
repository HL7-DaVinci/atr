package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPractitionerRole;
import org.hl7.davinci.atr.server.util.SearchParameterMap;

public interface PractitionerRoleDao {

	DafPractitionerRole getPractitionerRoleByVersionId(String id, String versionIdPart);

	DafPractitionerRole getPractitionerRoleById(String id);

	List<DafPractitionerRole> search(SearchParameterMap paramMap);

	DafPractitionerRole getPractitionerRoleForBulkData(String patientList, Date start, Date end);

	DafPractitionerRole getPractitionerRoleByIdentifier(String system, String value);
}
