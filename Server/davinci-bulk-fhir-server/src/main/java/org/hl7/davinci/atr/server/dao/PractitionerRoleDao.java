package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPractitionerRole;
import org.hl7.davinci.atr.server.util.SearchParameterMap;

public interface PractitionerRoleDao {

	DafPractitionerRole getPractitionerRoleByVersionId(int id, String versionIdPart);

	DafPractitionerRole getPractitionerRoleById(int id);

	List<DafPractitionerRole> search(SearchParameterMap paramMap);

	DafPractitionerRole getPractitionerRoleForBulkData(String patientList, Date start, Date end);
}
