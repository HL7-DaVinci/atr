package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.constants.TextConstants;
import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.model.DafPractitionerRole;
import org.hl7.davinci.atr.server.util.CommonUtil;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.TokenParamModifier;

@Repository("practitionerDao")
public class PractitionerDaoImpl extends AbstractDao implements PractitionerDao {
	private static final Logger logger = LoggerFactory.getLogger(PractitionerDaoImpl.class);    

	@Autowired
	FhirContext fhirContext;

	/**
	 * This method builds criteria for fetching practitioner record by id.
	 * @param id : ID of the resource
	 * @return : DafPractitioner object
	 */
	public DafPractitioner getPractitionerById(String id) {
		DafPractitioner dafPractitioner = null;
		try {
			List<DafPractitioner> list = getSession().createNativeQuery(
					"select * from practitioner where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", 
					DafPractitioner.class)
						.getResultList();
			if(list != null && !list.isEmpty()) {
				dafPractitioner = new DafPractitioner();
				dafPractitioner = list.get(0);
			}
		}
		catch(Exception ex) {
			logger.error("Exception in getPractitionerById of PractitionerDaoImpl ", ex);
		}
		return dafPractitioner;
    }

	/**
	 * This method builds query for fetching particular version of 
	 * the practitioner record by id.
	 * @param theId : ID of the practitioner
	 * @param versionId : version of the practitioner record
	 * @return : DafPractitioner object
	 */
	public DafPractitioner getPractitionerByVersionId(String theId, String versionId) {
		return getSession().createNativeQuery(
			"select * from practitioner where id = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", 
			DafPractitioner.class).getSingleResult();
	}
	
	/**
     * This method builds criteria for creating the practitioner
     * @return : practitioner record
     */
	public Practitioner createPractitioner(Practitioner thePractitioner) {
		DafPractitioner dafPractitioner = new DafPractitioner();
    	try {
    		IParser jsonParser = fhirContext.newJsonParser();
    		Meta meta = new Meta();
    		if(thePractitioner.hasMeta()) {
    			if(!thePractitioner.getMeta().hasVersionId()) {
            		meta.setVersionId("1");
            		thePractitioner.setMeta(meta);

    			}
    			if(!thePractitioner.getMeta().hasLastUpdated()) {
    				Date date = new Date();
            		meta.setLastUpdated(date);
            		thePractitioner.setMeta(meta);
    			}
    		}
    		else {
        		meta.setVersionId("1");
        		Date date = new Date();
        		meta.setLastUpdated(date);
        		thePractitioner.setMeta(meta);
    		}
    		if(!thePractitioner.hasIdElement()) {
    			String id = CommonUtil.getUniqueUUID();
    			thePractitioner.setId(id);
    			logger.info(" setting the uuid ");
    		}
    		dafPractitioner.setData(jsonParser.encodeResourceToString(thePractitioner));
    		getSession().saveOrUpdate(dafPractitioner);
    	}
    	catch(Exception e) {
    		logger.error("Exception in createPractitioner of PractitionerDaoImpl ", e);
    	}
		return thePractitioner;
	}
    
    /**
     * 
     */
	public DafPractitioner updatePractitionerById(int theId, Practitioner thePractitioner) {
		DafPractitioner dafPractitioner = new DafPractitioner();
		IParser jsonParser = fhirContext.newJsonParser();
		dafPractitioner.setId(theId);
		dafPractitioner.setData(jsonParser.encodeResourceToString(thePractitioner));
		getSession().saveOrUpdate(dafPractitioner);
		return dafPractitioner;
	}
	
	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafPractitioner object
	 */
	@SuppressWarnings("unchecked")
	public List<DafPractitioner> search(SearchParameterMap theMap) {
        @SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafPractitioner.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);

        //build criteria for name
        buildNameCriteria(theMap, criteria);

        //build criteria for given
        buildGivenNameCriteria(theMap, criteria);

        //build criteria for family
        buildFamilyNameCriteria(theMap, criteria);

        //build criteria for gender
        buildGenderCriteria(theMap, criteria);
        
        //build criteria for address
        buildAddressCriteria(theMap, criteria);
        
        //build criteria for address-city
        buildAddressCityCriteria(theMap, criteria);
        
        //build criteria for address-state
        buildAddressStateCriteria(theMap, criteria);
        
        //build criteria for address-country
        buildAddressCountryCriteria(theMap, criteria);
        
        //build criteria for address-postalcode
        buildAddressPostalcodeCriteria(theMap, criteria);
        
        //build criteria for communication
        buildCommunicationCriteria(theMap, criteria);
        
        //build criteria for active
        buildActiveCriteria(theMap, criteria);
        
        //build criteria for telecom
        buildTelecomCriteria(theMap, criteria);
        
        //build criteria for name
        buildNameCriteria(theMap, criteria);
        
        //build criteria for email
        buildEmailCriteria(theMap, criteria);
        
        //build criterid for address-use
        buildAddressUseCriteria(theMap, criteria);
        
        //build criterid for organization
        buildPhoneCriteria(theMap, criteria);
          
        return criteria.list();
    }
	
	/**
	 * This method builds criteria for practitioner id
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
	 * This method builds criteria for practitioner identifier
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
	 * This method builds criteria for practitioner given name
	 * @param theMap : search parameter "given"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildGivenNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("given");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam givenName = (StringParam) params;
                    Criterion orCond= null;
                    if (givenName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 = '" +givenName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 = '" +givenName.getValue()+"'")
                    			);
                    } else if (givenName.isContains()) {
                    	orCond = Restrictions.or(
                        			Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
                        			Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '%" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '%" + givenName.getValue() + "%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '" + givenName.getValue() + "%'"),
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '" + givenName.getValue() + "%'")
                    			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner family name
	 * @param theMap : search parameter "family"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildFamilyNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("family");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam familyName = (StringParam) params;
                    Criterion orCond= null;
                    if (familyName.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' = '" +familyName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' = '" +familyName.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' = '" +familyName.getValue()+"'")
                    			);
                    } else if (familyName.isContains()) {
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '%" +familyName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '%" +familyName.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '%" +familyName.getValue()+"%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '" +familyName.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '" +familyName.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '" +familyName.getValue()+"%'")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner telecom
	 * 
	 * @param theMap : search parameter "telecom"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildTelecomCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("telecom");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam telecom = (StringParam) params;
                    Criterion orCond= null;
                    if (telecom.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' = '" +telecom.getValue()+"'"),
                    				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' = '" +telecom.getValue()+"'")
                    			);
                    } else if (telecom.isContains()) {
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '%" +telecom.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '%" +telecom.getValue()+"%'")
                    			);
                    } else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '" +telecom.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '" +telecom.getValue()+"%'")
                			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner gender
	 * @param theMap : search parameter "gender"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildGenderCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("gender");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam gender = (TokenParam) params;
                    if(gender.getModifier() != null) {
                        TokenParamModifier modifier = gender.getModifier();
                        if(modifier.getValue() == ":not") {
                        	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' not ilike '"+gender.getValue()+"'"));
                        }
                    }else if(StringUtils.isNoneEmpty(gender.getValue())){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' ilike '%" + gender.getValue() + "%'"));
                    }else if(gender.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' IS NULL"));
                    }else if(!gender.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'gender' IS NOT NULL"));
                    }
                }
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner active
	 * @param theMap : search parameter "active"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildActiveCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("active");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                for (IQueryParameterType params : values) {
                    TokenParam active = (TokenParam) params;
                    if(StringUtils.isNoneEmpty(active.getValue())){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' ilike '%" + active.getValue() + "%'"));
                    }else if(active.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NULL"));

                    }else if(!active.getMissing()){
                    	criteria.add(Restrictions.sqlRestriction("{alias}.data->>'active' IS NOT NULL"));
                    }
                }
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner name
	 * @param theMap : search parameter "name"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildNameCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("name");

        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam name = (StringParam) params;
                    Criterion orCond= null;
                    if (name.isExact()) {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 = '" +name.getValue()+"'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 = '" +name.getValue()+"'")
                				
                                );
                    } else if (name.isContains()) {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '%" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '%" +name.getValue()+"%'")
                                );
                    } else {
                        orCond= Restrictions.or(
	                        		Restrictions.sqlRestriction("{alias}.data->'name'->0->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->>'family' ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->0->'given'->>1 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->1->'given'->>1 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>0 ilike '" +name.getValue()+"%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'name'->2->'given'->>1 ilike '" +name.getValue()+"%'")
                                );
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner communication
	 * @param theMap : search parameter "communication"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildCommunicationCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("communication");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam communication = (TokenParam) params;
                    if(communication.getValue() != null) {
                    	//display
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->0->>'display' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->1->>'display' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->0->>'display' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->1->>'display' ilike '%" + communication.getValue() + "%'"));			
                    	//system
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->0->>'system' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->1->>'system' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->0->>'system' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->1->>'system' ilike '%" + communication.getValue() + "%'"));	
                    	//code
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->0->>'code' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->0->'coding'->1->>'code' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->0->>'code' ilike '%" + communication.getValue() + "%'"));
                    	disjunction.add(Restrictions.sqlRestriction("{alias}.data->'communication'->1->'coding'->1->>'code' ilike '%" + communication.getValue() + "%'"));			

                    }
                } 
                criteria.add(disjunction);
            }
        }
    }

    /**
	 * This method builds criteria for practitioner address
	 * @param theMap : search parameter "address"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildAddressCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("address");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
                Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam address = (StringParam) params;
                    Criterion orCond= null;
                    if(address.isExact()){
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' = '" + address.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' = '" + address.getValue() + "'")
                    			);
                    }else if(address.isContains()){
                    	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '%" + address.getValue() + "%'"),
	                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%" + address.getValue() + "%'")
                			);
                    }else {
                    	orCond = Restrictions.or(
                    			Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>0 ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->'line'->>1 ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'district' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '" + address.getValue() + "%'"),
                				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '" + address.getValue() + "%'")
            			);
                    }
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
    }
    
    /**
	 * This method builds criteria for practitioner address city
	 * @param theMap : search parameter "address-city"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildAddressCityCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("address-city");
        if (list != null) {

            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunctions = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    StringParam addressCity = (StringParam) params;
                    Criterion orCond= null;
                    if(addressCity.isExact()) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' = '" + addressCity.getValue() + "'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' = '" + addressCity.getValue() + "'")
                    			);
                    }else if(addressCity.isContains()){
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '%" + addressCity.getValue() + "%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' ilike '%" + addressCity.getValue() + "%'")
                    			);

                    }else {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'address'->0->>'city' ilike '" + addressCity.getValue() + "%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'address'->1->>'city' ilike '" + addressCity.getValue() + "%'")
                    			);
                   }
                   disjunctions.add(orCond);
                }
                criteria.add(disjunctions);
            }
        }
    }
    
    /**
   	 * This method builds criteria for practitioner address country
   	 * @param theMap : search parameter "address-country"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressCountryCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-country");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunctions = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressCountry = (StringParam) params;
	               Criterion orCond= null;
	               if(addressCountry.isExact()){
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' = '" + addressCountry.getValue() + "'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' = '" + addressCountry.getValue() + "'")
	            			   );
	               }else if(addressCountry.isContains()){
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '%" + addressCountry.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' ilike '%" + addressCountry.getValue() + "%'")
	            			   );
	               }else {
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'country' ilike '" + addressCountry.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'country' ilike '" + addressCountry.getValue() + "%'")
	            			   );
	               }
	               disjunctions.add(orCond);
	           }
	           criteria.add(disjunctions);
	       }
    	}
    }
    
    /**
   	 * This method builds criteria for practitioner address state
   	 * @param theMap : search parameter "address-state"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressStateCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-state");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunctions = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressState = (StringParam) params;
	               Criterion orCond= null;
	               if(addressState.isExact()){
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' = '" + addressState.getValue() + "'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' = '" + addressState.getValue() + "'")
	            			   );
	               }else if(addressState.isContains()){
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '%" + addressState.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' ilike '%" + addressState.getValue() + "%'")
	            			   );
	               }else {
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'state' ilike '" + addressState.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'state' ilike '" + addressState.getValue() + "%'")
	            			  );
	               }
	               disjunctions.add(orCond);
	           }
	           criteria.add(disjunctions);
	       }
    	}
    }
    
    /**
   	 * This method builds criteria for practitioner address postalcode
   	 * @param theMap : search parameter "address-postalcode"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildAddressPostalcodeCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("address-postalcode");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunctions = Restrictions.disjunction();
	           for (IQueryParameterType params : values) {
	               StringParam addressPostalcode = (StringParam) params;
	               Criterion orCond= null;
	               if(addressPostalcode.isExact()){
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' = '" + addressPostalcode.getValue() + "'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' = '" + addressPostalcode.getValue() + "'")
	            			   );
	               }else if(addressPostalcode.isContains()) {
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '%" + addressPostalcode.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '%" + addressPostalcode.getValue() + "%'")
	            			   );
	               }else {
	            	   orCond = Restrictions.or(
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->0->>'postalCode' ilike '" + addressPostalcode.getValue() + "%'"),
	            			   		Restrictions.sqlRestriction("{alias}.data->'address'->1->>'postalCode' ilike '" + addressPostalcode.getValue() + "%'")
	            			   );
	               }
	               disjunctions.add(orCond);
	           }
	           criteria.add(disjunctions);
	       }
    	}
    }
    
    /**
	 * This method builds criteria for practittioner email
	 * @param theMap : search parameter "email"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildEmailCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("email");
        if (list != null) {
        	for (List<? extends IQueryParameterType> values : list) {
                 Disjunction disjunction = Restrictions.disjunction();
                 for (IQueryParameterType params : values) {
                     StringParam email = (StringParam) params;
                     Criterion andCond1 = null;
                     Criterion andCond2 = null;
                     Criterion andCond3 = null;
                     Criterion andCond4 = null;
                     if (email.isExact()) {
                    	 andCond1 = Restrictions.and(
                 				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' = '" +email.getValue()+"'"),
                 				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'email'")
                    		);
                    	 andCond2 = Restrictions.and(
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' = '" +email.getValue()+"'"),
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'email'")
                    		);
                    	 andCond3 = Restrictions.and(
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' = '" +email.getValue()+"'"),
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'email'")
                    		);
                    	 andCond4 = Restrictions.and(
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' = '" +email.getValue()+"'"),
              					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'email'")
                    		);
                     } else if (email.isContains()) {
                    	 andCond1 = Restrictions.and(
                  				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '%" +email.getValue()+"%'"),
                  				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'email'")
                    		);
                    	 andCond2 = Restrictions.and(
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '%" +email.getValue()+"%'"),
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'email'")
                 			 );
                    	 andCond3 = Restrictions.and(
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '%" +email.getValue()+"%'"),
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'email'")
                 			 );
                    	 andCond4 = Restrictions.and(
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' ilike '%" +email.getValue()+"%'"),
               					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'email'")
                 			 );
                     } else {
                    	 andCond1 = Restrictions.and(
                   				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '" +email.getValue()+"%'"),
                   				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'email'")
                   			);
                     	 andCond2 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '" +email.getValue()+"%'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'email'")
                  			 );
                     	 andCond3 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '" +email.getValue()+"%'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'email'")
                  			 );
                     	 andCond4 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' ilike '" +email.getValue()+"%'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'email'")
                  			 );
                     }
                     disjunction.add(andCond1);
                     disjunction.add(andCond2);
                     disjunction.add(andCond3);
                     disjunction.add(andCond4);
                 }
                 criteria.add(disjunction);
             }
        }
    }
    
    /**
	 * This method builds criteria for practitioner address-use
	 * @param theMap : search parameter "address-use"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildAddressUseCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("address-use");
        if (list != null) {

        	for (List<? extends IQueryParameterType> values : list) {
        		Disjunction disjunction = Restrictions.disjunction();
        		for (IQueryParameterType params : values) {
        			TokenParam addressUse = (TokenParam) params;
        			Criterion orCond = null;
        			if(addressUse.getValue() != null ){
        				orCond = Restrictions.or(
        							Restrictions.sqlRestriction("{alias}.data->'address'->0->>'use' ilike '%" + addressUse.getValue() + "%'"),
        							Restrictions.sqlRestriction("{alias}.data->'address'->1->>'use' ilike '%" + addressUse.getValue() + "%'")
        						);
        			}
        			disjunction.add(orCond);
        		}
        		criteria.add(disjunction);
 	       	}
        }
    }
    
    /**
   	 * This method builds criteria for practitioner phone contact
   	 * @param theMap : search parameter "phone"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildPhoneCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("phone");
    	if (list != null) {
	
	       for (List<? extends IQueryParameterType> values : list) {
	    	   Disjunction disjunction = Restrictions.disjunction();
	    	   for (IQueryParameterType params : values) {
                   StringParam phone = (StringParam) params;
                   Criterion andCond1 = null;
                   Criterion andCond2 = null;
                   Criterion andCond3 = null;
                   Criterion andCond4 = null;
                   if (phone.isExact()) {
                  	 andCond1 = Restrictions.and(
               				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' = '" +phone.getValue()+"'"),
               				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'phone'")
                  		);
                  	 andCond2 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' = '" +phone.getValue()+"'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'phone'")
                  		);
                  	 andCond3 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' = '" +phone.getValue()+"'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'phone'")
                  		);
                  	 andCond4 = Restrictions.and(
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' = '" +phone.getValue()+"'"),
            					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'phone'")
                  		);
                   } else if (phone.isContains()) {
                  	 andCond1 = Restrictions.and(
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '%" +phone.getValue()+"%'"),
                				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'phone'")
                  		);
                  	 andCond2 = Restrictions.and(
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '%" +phone.getValue()+"%'"),
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'phone'")
               			 );
                  	 andCond3 = Restrictions.and(
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '%" +phone.getValue()+"%'"),
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'phone'")
               			 );
                  	 andCond4 = Restrictions.and(
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' ilike '%" +phone.getValue()+"%'"),
             					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'phone'")
               			 );
                   } else {
                  	 andCond1 = Restrictions.and(
                 				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'value' ilike '" +phone.getValue()+"%'"),
                 				Restrictions.sqlRestriction("{alias}.data->'telecom'->0->>'system' = 'phone'")
                 			);
                   	 andCond2 = Restrictions.and(
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'value' ilike '" +phone.getValue()+"%'"),
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->1->>'system' = 'phone'")
                			 );
                   	 andCond3 = Restrictions.and(
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'value' ilike '" +phone.getValue()+"%'"),
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->2->>'system' = 'phone'")
                			 );
                   	 andCond4 = Restrictions.and(
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'value' ilike '" +phone.getValue()+"%'"),
          					Restrictions.sqlRestriction("{alias}.data->'telecom'->3->>'system' = 'phone'")
                			 );
                   }
                   disjunction.add(andCond1);
                   disjunction.add(andCond2);
                   disjunction.add(andCond3);
                   disjunction.add(andCond4);
               }
	    	   criteria.add(disjunction);
	       }
    	}
    }
    
    public DafPractitioner getPractitionerForBulkData(String practitioners, Date start, Date end) {
		Criteria criteria = getSession().createCriteria(DafPractitioner.class);
		if(practitioners!=null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '"+ practitioners+"' order by {alias}.data->'meta'->>'versionId' desc"));
		}
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return (DafPractitioner) criteria.list().get(0);
	}

	public DafPractitioner getPractitionerByProviderNpi(String providerNpiSystem,String providerNpi) {
		DafPractitioner dafPractitioner = null;
		try {
			//select id as id, data as data, last_updated_ts as last_upd from patient where id in (select distinct(id) from patient r, LATERAL json_array_elements(r.data->'identifier') segment WHERE  ( segment ->>'value' = '"+memberId+"'  and  segment ->>'system' = '"+TextConstants.MEMBERID_SYSTEM+"' ) )
			List<DafPractitioner> list = getSession().createNativeQuery("select * from practitioner where id in (select distinct(id) from practitioner r, LATERAL json_array_elements(r.data->'identifier') segment WHERE  ( segment ->>'value' = '"+providerNpi+"'  and  segment ->>'system' = '"+providerNpiSystem+"' ) ) order by data->'meta'->>'versionId' desc", DafPractitioner.class).getResultList();	
			if(list != null && !list.isEmpty()) {
				dafPractitioner = list.get(0);
			}
		}
		catch(Exception e) {
			logger.error("Exception in getPractitionerByProviderNpi of PractitionerDaoImpl ", e);
		}
		return dafPractitioner;
	}
}
