package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.List;

import org.hl7.davinci.atr.server.dao.GroupDao;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {
	
	public static final String RESOURCE_TYPE = "Group";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private GroupDao groupDao;

	@Override
	public DafGroup updateGroupById(int id, Group theGroup) {
		return groupDao.updateGroupById(id, theGroup);
	}

	@Override
	public DafGroup createGroup(Group theGroup) {
		return groupDao.createGroup(theGroup);
	}

	@Override
	public DafGroup getGroupById(String id) {
		DafGroup dafGroup = groupDao.getGroupById(id);
		return dafGroup;
	}

	@Override
	public List<Group> search(SearchParameterMap paramMap) {
		Group group = null;
		List<Group> groupList = new ArrayList<>();
		try {
			IParser jsonParser = fhirContext.newJsonParser();
			List<DafGroup> dafGroupList = groupDao.search(paramMap);
			if(dafGroupList != null && !dafGroupList.isEmpty()) {
				for(DafGroup dafGroup : dafGroupList) {
					group = jsonParser.parseResource(Group.class, dafGroup.getData());
					String str = fhirContext.newJsonParser().encodeResourceToString(group);
					System.out.println("GROUP IN STRING :: \n"+str);
					group.setId(group.getId());
					groupList.add(group);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return groupList;
	}
}