package org.hl7.davinci.atr.server.dao;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Group;

public interface GroupDao {

	DafGroup getGroupById(String id);

	DafGroup createGroup(Group theGroup);

	DafGroup updateGroupById(int id, Group theGroup);

	List<DafGroup> search(SearchParameterMap paramMap);

	DafGroup getGroupByVersionId(String idPart, String versionIdPart);
}
