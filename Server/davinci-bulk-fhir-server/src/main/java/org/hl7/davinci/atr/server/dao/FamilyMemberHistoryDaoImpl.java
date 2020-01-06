package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafDevice;
import org.hl7.davinci.atr.server.model.DafFamilyMemberHistory;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;

@Repository("familyMemberHistoryDao")
public class FamilyMemberHistoryDaoImpl extends AbstractDao implements FamilyMemberHistoryDao {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	private FhirContext fhirContext;
	
	/**
	 * This method builds criteria for fetching FamilyMemberHistory record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the FamilyMemberHistory
	 */
	public DafFamilyMemberHistory getFamilyMemberHistoryById(int id) {
		List<DafFamilyMemberHistory> list = getSession().createNativeQuery(
				"select * from FamilyMemberHistory where data->>'id' = '" + id + 
				"' order by data->'meta'->>'versionId' desc",
				DafFamilyMemberHistory.class).getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the FamilyMemberHistory
	 * record by id.
	 * 
	 * @param theId     : ID of the FamilyMemberHistory
	 * @param versionId : version of the FamilyMemberHistory record
	 * @return : DAF object of the FamilyMemberHistory
	 */
	public DafFamilyMemberHistory getFamilyMemberHistoryByVersionId(int theId, String versionId) {
		return getSession()
				.createNativeQuery("select * from FamilyMemberHistory where data->>'id' = '" + theId
						+ "' and data->'meta'->>'versionId' = '" + versionId + "'", DafFamilyMemberHistory.class)
				.getSingleResult();
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafFamilyMemberHistory object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafFamilyMemberHistory> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		//build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for status
        buildStatusCriteria(theMap, criteria);
        
        //build criteria for relationship
        buildRelationshipCriteria(theMap, criteria);
        
        //build criteria for instantiatesUri
        buildInstantiatesUriCriteria(theMap, criteria);
        
        //build criteria for sex
        buildSexCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for code
        buildCodeCriteria(theMap, criteria);
        
        return criteria.list();
	}
	
	/**
	 * This method builds criteria for FamilyMemberHistory id
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
	 * This method builds criteria for FamilyMemberHistory identifier
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
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for Specimen status
	 * @param theMap : search parameter "status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam status = (TokenParam) params;
	                Criterion orCond= null;
	                if (status.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->>'status' ilike '%" + status.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for FamilyMemberHistory relationship
	 * @param theMap : search parameter "relationship"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildRelationshipCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("relationship");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam relationship = (TokenParam) params;
	                Criterion orCond= null;
	                if (relationship.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->0->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->1->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'system' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'code' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->'coding'->2->>'display' ilike '%" + relationship.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'relationship'->>'text' ilike '%" + relationship.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	 /**
   	 * This method builds criteria for instantiatesUri: Instantiates external protocol or definition
   	 * @param theMap : search parameter "instantiates-uri"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildInstantiatesUriCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("instantiates-uri");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				UriParam instantiatesUri = (UriParam) params;
                    Criterion orCond= null;
                    if(instantiatesUri.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>0 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>1 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>2 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>3 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>4 ilike '%" + instantiatesUri.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri'->>5 ilike '%" + instantiatesUri.getValue() + "%'")
    							);
    				}else if(instantiatesUri.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NULL")
    							);
    				}else if(!instantiatesUri.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'instantiatesUri' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    /**
	 * This method builds criteria for FamilyMemberHistory sex
	 * @param theMap : search parameter "sex"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildSexCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("sex");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam sex = (TokenParam) params;
	                Criterion orCond= null;
	                if (sex.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'system' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'code' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->0->>'display' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'system' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'code' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->1->>'display' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'system' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'code' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->'coding'->2->>'display' ilike '%" + sex.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'sex'->>'text' ilike '%" + sex.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
	 * This method builds criteria for FamilyMemberHistory patient
	 * @param theMap : search parameter "patient"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildPatientCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("patient");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam patient = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(patient.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'display' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'patient'->>'type' ilike '%" + patient.getValue() + "%'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'patient' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
		
	}
	/**
	 * This method builds criteria for FamilyMemberHistory code
	 * @param theMap : search parameter "code"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCodeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("code");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam code = (TokenParam) params;
	                Criterion orCond= null;
	                if (code.getValue() != null) {
	                	orCond = Restrictions.or(
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->0->'code'->>'text' ilike '%" + code.getValue() + "%'"),

	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->0->>'display' ilike '%" + code.getValue() + "%'"),
	                				
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'system' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'code' ilike '%" + code.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->'coding'->1->>'display' ilike '%" + code.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'condition'->1->'code'->>'text' ilike '%" + code.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}

	/**
	 * This method builds criteria for FamilyMemberHistory creation
	 * 
	 * @param theFamilyMemberHistory
	 * @return 
	 */
	@Override
	@Transactional(value=TxType.REQUIRES_NEW)
	public DafFamilyMemberHistory createFamilyMemberHistory(FamilyMemberHistory theFamilyMemberHistory) {
		DafFamilyMemberHistory dafFamilyMemberHistory = new DafFamilyMemberHistory();
		IParser jsonParser = fhirContext.newJsonParser();
		jsonParser.encodeResourceToString(theFamilyMemberHistory);
		dafFamilyMemberHistory.setData(jsonParser.encodeResourceToString(theFamilyMemberHistory));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(dafFamilyMemberHistory);
		session.getTransaction().commit();
		session.close();
		return dafFamilyMemberHistory;
	}

	/**
	 * This method builds criteria for FamilyMemberHistory 
	 * updation by id
	 * 
	 * @param theId
	 * @param theFamilyMemberHistory
	 * @return 
	 */
	@Override
	public DafFamilyMemberHistory updateFamilyMemberHistoryById(int theId, FamilyMemberHistory theFamilyMemberHistory) {
		DafFamilyMemberHistory dafFamilyMemberHistory = new DafFamilyMemberHistory();
		IParser jsonParser = fhirContext.newJsonParser();
		dafFamilyMemberHistory.setId(theId);
		dafFamilyMemberHistory.setData(jsonParser.encodeResourceToString(theFamilyMemberHistory));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafFamilyMemberHistory);
		session.getTransaction().commit();
		session.close();
		return dafFamilyMemberHistory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryForBulkDataRequest(Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class);
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
	public List<DafFamilyMemberHistory> getFamilyMemberHistoryForPatientsBulkDataRequest(String id, Date start,
			Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafFamilyMemberHistory.class);
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
}