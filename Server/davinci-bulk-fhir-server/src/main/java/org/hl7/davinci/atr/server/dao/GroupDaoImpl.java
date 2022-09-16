package org.hl7.davinci.atr.server.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.CommonUtil;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("groupDao")
public class GroupDaoImpl extends AbstractDao implements GroupDao {
	private static final Logger logger = LoggerFactory.getLogger(GroupDaoImpl.class);    

	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	private FhirContext fhirContext;

	@Override
	public DafGroup getGroupById(String id) {
		DafGroup dafGroup = null;
		try {
			List<DafGroup> list = getSession().createNativeQuery(
					"select * from groups where data->>'id' = '" + id +
					"' order by data->'meta'->>'versionId' desc",
					DafGroup.class).getResultList();
			if(list != null && !list.isEmpty()) {
				dafGroup = new DafGroup();
				dafGroup = list.get(0);
			}
		}
		catch(Exception ex) {
			logger.error("Exception in getGroupById of GroupDaoImpl ", ex);
		}
		return dafGroup;
	}

	@Override
	public DafGroup createGroup(Group theGroup) {
		DafGroup dafGroup = new DafGroup();
		IParser jsonParser = fhirContext.newJsonParser();
		if(!theGroup.hasIdElement()) {
			String id = CommonUtil.getUniqueUUID();
			theGroup.setId(id);
			logger.info(" setting the uuid ");
		}
		jsonParser.encodeResourceToString(theGroup);
		dafGroup.setData(jsonParser.encodeResourceToString(theGroup));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(dafGroup);
		session.getTransaction().commit();
		session.close();
		return dafGroup;
	}

	@Override
	public DafGroup updateGroupById(int id, Group theGroup) {
		DafGroup dafGroup = new DafGroup();
		IParser jsonParser = fhirContext.newJsonParser();
		dafGroup.setId(id);
		dafGroup.setData(jsonParser.encodeResourceToString(theGroup));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafGroup);
		session.getTransaction().commit();
		session.close();
		return dafGroup;
	}

	@Override
	public List<DafGroup> search(SearchParameterMap paramMap) {
		List<DafGroup> groupList = new ArrayList<>();
		StringBuffer query = new StringBuffer();
		query.append("select * from groups");
		query = buildIdentifierQuery(paramMap, query);
		query = buildNameQuery(paramMap, query);
		final String finalQuery = query.toString();
		System.out.println("GROUP QUERY IS :: "+finalQuery);
		groupList = getSession().createNativeQuery(finalQuery,DafGroup.class).getResultList();
		return groupList;
	}
	
	private StringBuffer buildNameQuery(SearchParameterMap paramMap, StringBuffer query) {
		List<List<? extends IQueryParameterType>> list = paramMap.get("name");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam groupName = (StringParam) params;
					if(groupName.getValue() != null && !groupName.getValue().isEmpty()) {
						if(!CommonUtil.containsIgnoreCase(query.toString(), "WHERE")) {
 	                		query.append(" WHERE ");
 	                	}
						
						if(CommonUtil.containsIgnoreCase(query.toString(), ".")) {
 	                		query.append("AND ");
 	                	}
						query.append("data->>'name' like '%"+groupName.getValue()+"%' ");
					}
				}
			}
		}
		return query;
	}

	private StringBuffer buildIdentifierQuery(SearchParameterMap paramMap, StringBuffer query) {
		List<List<? extends IQueryParameterType>> list = paramMap.get("identifier");
	    if (list != null) {
	    	int i = 0;
	        for (List<? extends IQueryParameterType> values : list) {
	            for (IQueryParameterType params : values) {
	                TokenParam identifier = (TokenParam) params;
	                if (identifier.getValue() != null) {
 	                	if(CommonUtil.containsIgnoreCase(query.toString(), ".")) {
 	                		query.append("AND ");
 	                	}
 	                	String valuePair = identifier.getValue();
	                	String valueAndType[] = null;
	                	valueAndType = valuePair.split("\\|");
	                	if(valueAndType.length == 2) {
	                		//query.append(", json_array_elements(data->'identifier') obj, json_array_elements(obj->'type'->'coding') obj2 ");
	 	                	if(!CommonUtil.containsIgnoreCase(query.toString(), "WHERE")) {
	 	                		query.append(" WHERE ");
	 	                	}
	                		String type = valueAndType[0];
	                		String value = valueAndType[1];
	                		if (value.endsWith("|")) {
		             			value = value.substring(0, value.length() - 1);
		             		}
	                		
	                		query.append("data->'identifier'->"+i+"->'type'->'coding'->0->>'system' = '" + identifier.getSystem() + "' ");
	                		query.append("AND data->'identifier'->"+i+"->>'value' = '" + value + "' ");
	                		query.append("AND data->'identifier'->"+i+"->'type'->'coding'->0->>'code' = '" + type + "' ");
	                		
	                		query.append("OR data->'identifier'->1->'type'->'coding'->0->>'system' = '" + identifier.getSystem() + "' ");
	                		query.append("AND data->'identifier'->1->>'value' = '" + value + "' ");
	                		query.append("AND data->'identifier'->1->'type'->'coding'->0->>'code' = '" + type + "' ");

	                	}
	                	else {
	 	                	if(!CommonUtil.containsIgnoreCase(query.toString(), "WHERE")) {
	 	                		query.append(" WHERE ");
	 	                	}
	                		query.append("data->'identifier'->"+i+"->>'value' = '" + identifier.getValue() + "' ");
	                		query.append("OR data->'identifier'->1->>'value' = '" + identifier.getValue() + "' ");
	 	                	if(identifier.getSystem() != null) {
	 	                		query.append("AND data->'identifier'->"+i+"->>'system' = '"+identifier.getSystem()+"' ");
	 	                		query.append("OR data->'identifier'->1->>'system' = '"+identifier.getSystem()+"' ");
	 	                	}

	                	}
 	                }
	                i++;
	            }
	        }
	    }
	    return query;
	}

	@Override
	public DafGroup getGroupByVersionId(String idPart, String versionIdPart) {
		return getSession().createNativeQuery(
				"select * from groups where data->>'id' = '"+idPart+"' and data->'meta'->>'versionId' = '"+versionIdPart+"'", 
					DafGroup.class).getSingleResult();
	}
}