package org.hl7.davinci.atr.server.service;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Group;

public interface GroupService {

	DafGroup updateGroupById(int id, Group theGroup);

	DafGroup createGroup(Group theGroup);

	DafGroup getGroupById(String id);

	List<Group> search(SearchParameterMap paramMap);
}
