package org.hl7.davinci.atr.server.service;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Period;

public interface GroupService {

	DafGroup updateGroupById(int id, Group theGroup);

	DafGroup createGroup(Group theGroup);

	DafGroup getGroupById(String id);

	List<Group> search(SearchParameterMap paramMap);

	DafGroup addMemberToGroup(Group group, DafGroup dafGroup, String patientMemberId, String providerId, String providerReference, String coverageReference, Period attributionPeriod);

	DafGroup removeMemberFromGroup(Group group, DafGroup dafGroup, String patientMemberId, String attributeProviderId,
			String attributeProviderReferenceResource, String coverageReference, Period attributionPeriod) throws Exception;

	DafGroup processAddMemberToGroup(Parameters theParameters,String groupId) throws Exception;
	
	DafGroup processRemoveMemberToGroup(Parameters theParameters,String groupId) throws Exception;

	DafGroup getGroupByVersionId(String idPart, String versionIdPart);

	Bundle processAttrStatus(Parameters theParameters,String groupId) throws Exception;
}
