package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafPractitionerRole;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("practitionerRoleDao")
public class PractitionerRoleDaoImpl extends AbstractDao implements PractitionerRoleDao {

	@Autowired
	FhirContext fhirContext;

	@Override
	public DafPractitionerRole getPractitionerRoleByVersionId(int id, String versionIdPart) {
		return getSession().createNativeQuery(
				"select * from practitionerrole where id = '"+id+"' and data->'meta'->>'versionId' = '"+versionIdPart+"'", 
				DafPractitionerRole.class).getSingleResult();
	}

	@Override
	public DafPractitionerRole getPractitionerRoleById(int id) {
		List<DafPractitionerRole> list = getSession().createNativeQuery(
				"select * from practitionerrole where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", 
				DafPractitionerRole.class)
					.getResultList();
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafPractitionerRole> search(SearchParameterMap paramMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(paramMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(paramMap, criteria);
        
        //build criteria for identifier
        buildPractitionerCriteria(paramMap, criteria);
        
		return criteria.list();
	}

	private void buildPractitionerCriteria(SearchParameterMap paramMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = paramMap.get("practitioner");
		if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    ReferenceParam link = (ReferenceParam) params;
                    if(link.getValue() != null) {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'reference' = '" +"Practitioner/"+ link.getValue() + "'"));		
                    }
                    else if(link.getMissing()) {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'reference' IS NULL"));
                    }
                    else if(!link.getMissing()) {
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->'practitioner'->>'reference' IS NOT NULL"));
                    }
                }
            }
		}
	}

	/**
	 * This method builds criteria for practitioner role id
	 * @param theMap : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("_id");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam id = (TokenParam) params;
                    if (id.getValue() != null) {
        				criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id.getValue()+"'"));

                    }
                }
            }
        }
	}
	
	/**
	 * This method builds criteria for practitioner role identifier
	 * @param theMap : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
	    List<List<? extends IQueryParameterType>> list = theMap.get("identifier");
	
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam identifier = (TokenParam) params;
	                Criterion orCond= null;
	                if (identifier.getValue() != null && identifier.getSystem() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' = '" + identifier.getSystem() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' = '" + identifier.getValue() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' = '" + identifier.getSystem() + "'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' = '" + identifier.getValue() + "'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafPractitionerRole> getPractitionerRoleForBulkData(List<String> patientList, Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafPractitionerRole.class);
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
	}
}