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
import org.hl7.davinci.atr.server.model.DafGoal;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("goalDao")
public class GoalDaoImpl extends AbstractDao implements GoalDao {
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@Autowired
	private FhirContext fhirContext;
	
	/**
	 * This method builds criteria for fetching Goal record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Goal
	 */
	public DafGoal getGoalById(int id) {
		List<DafGoal> list = getSession().createNativeQuery(
				"select * from Goal where data->>'id' = '" + id +
				"' order by data->'meta'->>'versionId' desc",
				DafGoal.class).getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the Goal
	 * record by id.
	 * 
	 * @param theId     : ID of the Goal
	 * @param versionId : version of the Goal record
	 * @return : DAF object of the Goal
	 */
	public DafGoal getGoalByVersionId(int theId, String versionId) {
		return getSession()
				.createNativeQuery("select * from Goal where data->>'id' = '" + theId
						+ "' and data->'meta'->>'versionId' = '" + versionId + "'", DafGoal.class)
				.getSingleResult();
	}

	/**
	 * This method invokes various methods for search
	 * @param theMap : parameter for search
	 * @return criteria : DafGoal object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafGoal> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafGoal.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        //build criteria for id
        buildIdCriteria(theMap, criteria);

        //build criteria for identifier
        buildIdentifierCriteria(theMap, criteria);
        
        //build criteria for lifestatus
        buildLifecycleStatusCriteria(theMap, criteria);
        
        //build criteria for achievementstatus
        buildAchievementStatusCriteria(theMap, criteria);
        
        //build criteria for subject
        buildSubjectCriteria(theMap, criteria);
        
        //build criteria for patient
        buildPatientCriteria(theMap, criteria);
        
        //build criteria for category
        buildCategoryCriteria(theMap, criteria);
        
        //build criteria for start-date
        buildStartDateCriteria(theMap, criteria);
        
        //build criteria for target-date
        buildTargetDateCriteria(theMap, criteria);
        
		return criteria.list();
	}
	
	
	/**
	 * This method builds criteria for goal id
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
	 * This method builds criteria for goal identifier
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
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->2->>'value' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'system' ilike '%" + identifier.getValue() + "%'"),
	                    			Restrictions.sqlRestriction("{alias}.data->'identifier'->3->>'value' ilike '%" + identifier.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
    
	/**
	 * This method builds criteria for goal lifecyclestatus
	 * @param theMap : search parameter "lifecycle-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildLifecycleStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("lifecycle-status");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam lifecycleStatus = (TokenParam) params;
	                Criterion orCond= null;
	                if (lifecycleStatus.getValue() != null) {
	                	orCond = Restrictions.or(
	                    			Restrictions.sqlRestriction("{alias}.data->>'lifecycleStatus' ilike '%" + lifecycleStatus.getValue() + "%'")
	                			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	
	/**
	 * This method builds criteria for goal achievementstatus
	 * @param theMap : search parameter "achievement-status"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildAchievementStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("achievement-status");
		
	    if (list != null) {
	        for (List<? extends IQueryParameterType> values : list) {
	            Disjunction disjunction = Restrictions.disjunction();
	            for (IQueryParameterType params : values) {
	                TokenParam type = (TokenParam) params;
	                Criterion orCond= null;
	                if (type.getValue() != null) {
	        			orCond = Restrictions.or(
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->0->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->1->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'system' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'code' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->'coding'->2->>'display' ilike '%" + type.getValue() + "%'"),
	            				Restrictions.sqlRestriction("{alias}.data->'achievementStatus'->>'text' ilike '%" + type.getValue() + "%'")
	            			);
	                } 
	                disjunction.add(orCond);
	            }
	            criteria.add(disjunction);
	        }
	    }
	}
	/**
   	 * This method builds criteria for patient:Returns statements for a specific patient.
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
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + patient.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'type' ilike '%" + patient.getValue() + "%'")
    							);
    				}else if(patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL")
    							);
    				}else if(!patient.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
    /**
   	 * This method builds criteria for subject
   	 * @param theMap : search parameter "subject"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildSubjectCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("subject");
    	if (list != null) {
	
    		for (List<? extends IQueryParameterType> values : list) {
    			Disjunction disjunction = Restrictions.disjunction();
    			for (IQueryParameterType params : values) {
    				ReferenceParam subject = (ReferenceParam) params;
                    Criterion orCond= null;
                    if(subject.getValue() != null){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' ilike '%" + subject.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'display' ilike '%" + subject.getValue() + "%'"),
    								Restrictions.sqlRestriction("{alias}.data->'subject'->>'type' ilike '%" + subject.getValue() + "%'")
    							);
    				}else if(subject.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL")
    							);
    				}else if(!subject.getMissing()){
    					orCond = Restrictions.or(
    								Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL")
    							);
	                }
    				disjunction.add(orCond);
    			}
    			criteria.add(disjunction);
    		}
    	}
    }
    
	/**
	 * This method builds criteria for goal category
	 * @param theMap : search parameter "category"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildCategoryCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("category");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    TokenParam category = (TokenParam) params;
                    Criterion orCond= null;
                    if (category.getValue() != null) {
                    	orCond = Restrictions.or(
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->0->>'system' ilike '%" +category.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->0->'coding'->1->>'system' ilike '%" +category.getValue()+"%'"),
                    				
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->0->>'system' ilike '%" +category.getValue()+"%'"),

                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'code' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'display' ilike '%" +category.getValue()+"%'"),
                    				Restrictions.sqlRestriction("{alias}.data->'category'->1->'coding'->1->>'system' ilike '%" +category.getValue()+"%'")
                    			);
                    } 
                    disjunction.add(orCond);
                }
                criteria.add(disjunction);
            }
        }
	}
    
	/**
	 * This method builds criteria for target-date
	 * @param theMap : search parameter "target-date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
    private void buildTargetDateCriteria(SearchParameterMap theMap, Criteria criteria) {
        List<List<? extends IQueryParameterType>> list = theMap.get("target-date");
        if (list != null) {
            for (List<? extends IQueryParameterType> values : list) {
            	Disjunction disjunction = Restrictions.disjunction();
                for (IQueryParameterType params : values) {
                    DateParam targetDate = (DateParam) params;
                    String theTargetDate = targetDate.getValueAsString();
                    Criterion orCond= null;
                    if(targetDate.getPrefix() != null) {
                        if(targetDate.getPrefix().getValue() == "gt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("{alias}.data->'target'->0->>'dueDate' > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->1->>'dueDate' > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->2->>'dueDate' > '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->3->>'dueDate' > '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "lt"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("{alias}.data->'target'->0->>'dueDate' < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->1->>'dueDate' < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->2->>'dueDate' < '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->3->>'dueDate' < '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "ge"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("{alias}.data->'target'->0->>'dueDate' >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->1->>'dueDate' >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->2->>'dueDate' >= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->3->>'dueDate' >= '"+theTargetDate+ "'")
                        			);
                        }else if(targetDate.getPrefix().getValue() == "le"){
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("{alias}.data->'target'->0->>'dueDate' <= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->1->>'dueDate' <= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->2->>'dueDate' <= '"+theTargetDate+ "'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->3->>'dueDate' <= '"+theTargetDate+ "'")
                        			);
                        }else {
                        	orCond = Restrictions.or(
                        				Restrictions.sqlRestriction("{alias}.data->'target'->0->>'dueDate' = '"+theTargetDate+"'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->1->>'dueDate' = '"+theTargetDate+"'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->2->>'dueDate' = '"+theTargetDate+"'"),
                        				Restrictions.sqlRestriction("{alias}.data->'target'->3->>'dueDate' = '"+theTargetDate+"'")
                        			);
                        }
                        disjunction.add(orCond);
                    }
                    criteria.add(disjunction);
                }            
            }
        }
    }
    
    
   	/**
   	 * This method builds criteria for start-date
   	 * @param theMap : search parameter "start-date"
   	 * @param criteria : for retrieving entities by composing Criterion objects
   	 */
    private void buildStartDateCriteria(SearchParameterMap theMap, Criteria criteria) {
    	List<List<? extends IQueryParameterType>> list = theMap.get("start-date");
	    if (list != null) {
    	   for (List<? extends IQueryParameterType> values : list) {
    		   for (IQueryParameterType params : values) {
	               DateParam startDate = (DateParam) params;
	               String theStartDate = startDate.getValueAsString();
	               if(startDate.getPrefix() != null) {
	                   if(startDate.getPrefix().getValue() == "gt"){
	                       criteria.add(Restrictions.sqlRestriction("{alias}.data->>'startDate' > '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "lt"){
	                       criteria.add(Restrictions.sqlRestriction("{alias}.data->>'startDate' < '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "ge"){
	                       criteria.add(Restrictions.sqlRestriction("{alias}.data->>'startDate' >= '"+theStartDate+ "'"));
	                   }else if(startDate.getPrefix().getValue() == "le"){
	                       criteria.add(Restrictions.sqlRestriction("{alias}.data->>'startDate' <= '"+theStartDate+ "'"));
	                   }else {
	                   		criteria.add(Restrictions.sqlRestriction("{alias}.data->>'startDate' = '"+theStartDate+"'"));                    
	                   }
                   }
               }            
           }
	    }
    }

    /**
	 * This method builds criteria for Goal creation
	 * 
	 * @param theGoal
	 * @return 
	 */
	@Override
	@Transactional(value=TxType.REQUIRES_NEW)
	public DafGoal createGoal(Goal theGoal) {
		DafGoal dafGoal = new DafGoal();
		IParser jsonParser = fhirContext.newJsonParser();
		jsonParser.encodeResourceToString(theGoal);
		dafGoal.setData(jsonParser.encodeResourceToString(theGoal));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(dafGoal);
		session.getTransaction().commit();
		session.close();
		return dafGoal;
	}

	/**
	 * This method builds criteria for Goal 
	 * updation by id
	 * 
	 * @param theId
	 * @param theGoal
	 * @return 
	 */
	@Override
	public DafGoal updateGoalById(int theId, Goal theGoal) {
		DafGoal dafGoal = new DafGoal();
		IParser jsonParser = fhirContext.newJsonParser();
		dafGoal.setId(theId);
		dafGoal.setData(jsonParser.encodeResourceToString(theGoal));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafGoal);
		session.getTransaction().commit();
		session.close();
		return dafGoal;
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public List<DafGoal> getGoalsForPatientsBulkData(String patientId, Date start, Date end) {
    	
    	Criteria criteria = getSession().createCriteria(DafGoal.class);
		if(patientId != null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->'subject'->>'reference' = 'Patient/"+patientId+"'"));
		}
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
    }
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public List<DafGoal> getGoalsForBulkData(Date start, Date end) {
    	
    	Criteria criteria = getSession().createCriteria(DafGoal.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
    }
}
