package org.hl7.davinci.atr.server.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("groupDao")
public class GroupDaoImpl extends AbstractDao implements GroupDao {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	private FhirContext fhirContext;

	@Override
	public DafGroup getGroupById(String id) {
		List<DafGroup> list = getSession().createNativeQuery(
			"select * from groups where data->>'id' = '" + id +
			"' order by data->'meta'->>'versionId' desc",
			DafGroup.class).getResultList();
		return list.get(0);
	}

	@Override
	public DafGroup createGroup(Group theGroup) {
		DafGroup dafGroup = new DafGroup();
		IParser jsonParser = fhirContext.newJsonParser();
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
		Criteria criteria = getSession().createCriteria(DafGroup.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

	    List<List<? extends IQueryParameterType>> list = paramMap.get("identifier");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam identifier = (TokenParam) params;
	                Criterion andCondOne= null;
	                Criterion andCondTwo= null;
	                if (identifier.getValue() != null && identifier.getSystem() != null) {
	                	String valuePair = identifier.getValue();
	                	 String valueAndType[] = null;
	                	 String type = null;
	                	 String value = null;
	                	 valueAndType = valuePair.split("\\|");
	                	 if(valueAndType.length == 2) {
	                		 type = valueAndType[0];
	                		 value = valueAndType[1];
	                	 }
	                	 if (value.endsWith("|")) {
	             			value = value.substring(0, value.length() - 1);
	             		 }
	                	 andCondOne = Restrictions.and(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->'type'->'coding'->0->>'system' = '" + identifier.getSystem() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' = '" + value + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->'type'->'coding'->0->>'code' = '" + type + "'")
	                			);
	                	 andCondTwo = Restrictions.and(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->'type'->'coding'->0->>'system' = '" + identifier.getSystem() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' = '" + value + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->'type'->'coding'->0->>'code' = '" + type + "'")
	                			);
	                } 
	                disjunction.add(andCondOne);
	                disjunction.add(andCondTwo);
	            }
	            criteria.add(disjunction);
	        }
	    }
		return criteria.list();
	}
}