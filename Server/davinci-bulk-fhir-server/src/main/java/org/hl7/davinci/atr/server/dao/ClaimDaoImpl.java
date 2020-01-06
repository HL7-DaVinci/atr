package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafCareTeam;
import org.hl7.davinci.atr.server.model.DafClaim;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;

@Repository("claimDao")
public class ClaimDaoImpl extends AbstractDao implements ClaimDao{

	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	/**
	 * This method builds criteria for fetching Claim record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Claim
	 */
	@Override
	public DafClaim getClaimById(int id) {
		List<DafClaim> list = getSession().createNativeQuery(
			"select * from claim where data->>'id' = '"+id+
			"' order by data->'meta'->>'versionId' desc", DafClaim.class)
				.getResultList();
		return list.get(0);
	}


	/**
	 * This method builds criteria for fetching particular 
	 * version of the Claim record by id.
	 * 
	 * @param theId : ID of the Claim
	 * @param versionId : version of the Claim record
	 * @return : DAF object of the Claim
	 */
	@Override
	public DafClaim getClaimByVersionId(int theId, String versionId) {
		return getSession().createNativeQuery(
			"select * from claim where data->>'id' = '"+theId+
			"' and data->'meta'->>'versionId' = '"+versionId+"'", DafClaim.class)
				.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DafClaim> getClaimForBulkDataRequest(Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafClaim.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));
   
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
		return criteria.list();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DafClaim> getClaimForPatientsBulkDataRequest(String id, Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafClaim.class);
		if(id != null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = 'Patient/"+id+"'"));
		}
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
	}


	@Override
	public List<DafClaim> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafClaim.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for insurer
        buildInsurerCriteria(theMap, criteria);

        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for status
        buildPatientCriteria(theMap, criteria);
      
        return criteria.list();
	}
	
	/**
	 * This method builds criteria for careTeam id
	 * @param theMap : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */		
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("_id");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    StringParam id = (StringParam) params;
                    if (id.getValue() != null) {
        				criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" +id.getValue()+"'"));

                    }
                }
            }
        }
	}
	
	/**
	 * This method builds criteria for careTeam identifier
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
	                if (identifier.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
    /**
	 * This method builds criteria for condition patient
	 * 
	 * @param theMap   : search parameter "patient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("patient");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam patient = (ReferenceParam) params;
					Criterion orCond = null;
					if (patient.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'reference' = 'Patient/" + patient.getValue() + "'"));
					} else if (patient.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NULL"));
					} else if (!patient.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
	
	/**
	 * This method builds criteria for careTeam status
	 * @param theMap : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                	TokenParam status = (TokenParam) params;
                	if(status.getModifier() != null) {
                        TokenParamModifier modifier = status.getModifier();
                        if(modifier.getValue() == ":not") {
                        	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' not ilike '"+status.getValue()+"'"));
                        }
                	}else if(StringUtils.isNoneEmpty(status.getValue())){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' ilike '%" + status.getValue() + "%'"));
                    }else if(status.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NULL"));
                    } else if(!status.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NOT NULL"));
                    }
                }
            }
        }	
		
	}
	
	/**
	 * This method builds criteria for condition patient
	 * 
	 * @param theMap   : search parameter "patient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildInsurerCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("insurer");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam insurer = (ReferenceParam) params;
					Criterion orCond = null;
					if (insurer.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'insurer'->>'reference' = 'Organization/" + insurer.getValue() + "'"));
					} else if (insurer.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'insurer' IS NULL"));
					} else if (!insurer.getMissing()) {
						orCond = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'insurer' IS NOT NULL"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
	
}
