package org.hl7.davinci.atr.server.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafEncounter;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@Repository("EncounterDao")
public class EncounterDaoImpl extends AbstractDao implements EncounterDao {
	
	@Autowired
	FhirContext fhirContext;

	@Autowired
    private SessionFactory sessionFactory;
	
	/**
	 * This method builds criteria for fetching Encounter record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the Encounter
	 */
	public DafEncounter getEncounterById(int id) {
		List<DafEncounter> list = getSession().createNativeQuery(
				"select * from encounter where data->>'id' = '" + id + "' order by data->'meta'->>'versionId' desc",
				DafEncounter.class).getResultList();
		return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the Encounter
	 * record by id.
	 * 
	 * @param theId     : ID of the Encounter
	 * @param versionId : version of the Encounter record
	 * @return : DAF object of the Encounter
	 */
	public DafEncounter getEncounterByVersionId(int theId, String versionId) {
		return getSession()
				.createNativeQuery("select * from encounter where data->>'id' = '" + theId
						+ "' and data->'meta'->>'versionId' = '" + versionId + "'", DafEncounter.class)
				.getSingleResult();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param theMap : parameter for search
	 * @return criteria : DAF Encounter object
	 */
	@SuppressWarnings("unchecked")
	public List<DafEncounter> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafEncounter.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);

		// build criteria for date
		buildDateCriteria(theMap, criteria);

		// build criteria for Type
		buildTypeCriteria(theMap, criteria);

		// build criteria for patient
		buildPatientCriteria(theMap, criteria);

		// build criteria for status
		buildStatusCriteria(theMap, criteria);

		// build criteria for class
		buildClassCriteria(theMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for class
	 * 
	 * @param searchParameterMap : search parameter "class"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildClassCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("class");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam theClass = (TokenParam) params;
					Criterion criterion = null;
					if (!theClass.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'class'->>'system' ilike '%" + theClass.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'class'->>'version' ilike '%" + theClass.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'class'->>'code' ilike '%" + theClass.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'class'->>'display' ilike '%" + theClass.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'class'->>'userSelected' ilike '%"
										+ theClass.getValue() + "%'"));
					} else if (theClass.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'class' IS NULL"));
					} else if (!theClass.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'class' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for status
	 * 
	 * @param searchParameterMap : search parameter "status"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildStatusCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam status = (TokenParam) params;
					if (!status.isEmpty()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->>'status' ilike '%" + status.getValue() + "%'"));
					} else if (status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NULL"));

					} else if (!status.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'status' IS NOT NULL"));

					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for type
	 * 
	 * @param searchParameterMap : search parameter "type"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildTypeCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("type");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam type = (TokenParam) params;
					Criterion criterion = null;
					if (!type.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'system' ilike '%"
										+ type.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'code' ilike '%"
										+ type.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->0->>'display' ilike '%"
										+ type.getValue() + "%'"),

								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->1->>'system' ilike '%"
										+ type.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->1->>'code' ilike '%"
										+ type.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'type'->0->'coding'->1->>'display' ilike '%"
										+ type.getValue() + "%'"));
					} else if (type.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'type' IS NULL"));
					} else if (!type.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'type' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for Encounter patient
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
					Criterion criterion = null;
					if (patient.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'reference' ilike '%" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'subject'->>'display' ilike '%" + patient.getValue() + "%'"));

					} else if (patient.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NULL"));

					} else if (!patient.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for Encounter date
	 * 
	 * @param theMap   : search parameter "date"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildDateCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("date");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					DateParam date = (DateParam) params;
					String dateFormat = date.getValueAsString();
					Criterion orCond = null;
					if (date.getPrefix() != null) {
						if (date.getPrefix().getValue() == "gt") {
							orCond = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'start' > '" + dateFormat + "'"),
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'end' > '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "lt") {
							orCond = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'start' < '" + dateFormat + "'"),
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'end' < '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "ge") {
							orCond = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'start' >= '" + dateFormat + "'"),
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'end' >= '" + dateFormat + "'"));
						} else if (date.getPrefix().getValue() == "le") {
							orCond = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'start' <= '" + dateFormat + "'"),
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'end' <= '" + dateFormat + "'"));
						} else {
							orCond = Restrictions.or(
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'start' = '" + dateFormat + "'"),
									Restrictions
											.sqlRestriction("{alias}.data->'period'->>'end' = '" + dateFormat + "'"));
						}
						disjunction.add(orCond);
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for Encounter id
	 * 
	 * @param theMap   : search parameter "_id"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("_id");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam id = (StringParam) params;
					if (id.getValue() != null) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '" + id.getValue() + "'"));

					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for Encounter identifier
	 * 
	 * @param theMap   : search parameter "identifier"
	 * @param criteria : for retrieving entities by composing Criterion objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap theMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = theMap.get("identifier");

		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam identifier = (TokenParam) params;
					Criterion orCond = null;
					if (identifier.getValue() != null) {
						orCond = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'use' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'use' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' ilike '%"
										+ identifier.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' ilike '%"
										+ identifier.getValue() + "%'"));
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
	
	/**
	 * This method builds criteria for encounter creation
	 * 
	 * @param theEncounter
	 * @return 
	 */
	@Override
	@Transactional(value=TxType.REQUIRES_NEW)
	public DafEncounter createEncounter(Encounter theEncounter) {
		DafEncounter dafEncounter = new DafEncounter();
		IParser jsonParser = fhirContext.newJsonParser();
		jsonParser.encodeResourceToString(theEncounter);
		dafEncounter.setData(jsonParser.encodeResourceToString(theEncounter));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(dafEncounter);
		session.getTransaction().commit();
		session.close();
		return dafEncounter;
	}

	/**
	 * This method builds criteria for encounter 
	 * updation by id
	 * 
	 * @param theId
	 * @param theEncounter
	 * @return 
	 */
	@Override
	public DafEncounter updateEncounterById(int theId, Encounter theEncounter) {
		DafEncounter dafEncounter = new DafEncounter();
		IParser jsonParser = fhirContext.newJsonParser();
		dafEncounter.setId(theId);
		dafEncounter.setData(jsonParser.encodeResourceToString(theEncounter));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafEncounter);
		session.getTransaction().commit();
		session.close();
		return dafEncounter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafEncounter> getEncounterForBulkData(Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafEncounter.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'subject' IS NOT NULL"));
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
	public List<DafEncounter> getEncounterForPatientsBulkData(String patientId, Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafEncounter.class);
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
}
