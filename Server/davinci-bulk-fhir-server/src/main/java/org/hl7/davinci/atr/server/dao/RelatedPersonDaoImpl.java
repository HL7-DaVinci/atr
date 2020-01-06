package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafObservation;
import org.hl7.davinci.atr.server.model.DafProcedure;
import org.hl7.davinci.atr.server.model.DafRelatedPerson;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("relatedPersonDao")
public class RelatedPersonDaoImpl extends AbstractDao implements RelatedPersonDao {

	@Autowired
	FhirContext fhirContext;

	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public DafRelatedPerson getRelatedPersonById(int theId) {
		List<DafRelatedPerson> list = getSession().createNativeQuery(
				"select * from relatedperson where data->>'id' = '"+theId+
				"' order by data->'meta'->>'versionId' desc", DafRelatedPerson.class)
					.getResultList();
			return list.get(0);	
	}

	@Override
	public DafRelatedPerson getRelatedPersonByVersionId(int theId, String versionIdPart) {
		return getSession().createNativeQuery(
				"select * from relatedperson where data->>'id' = '"+theId+
				"' and data->'meta'->>'versionId' = '"+versionIdPart+"'", DafRelatedPerson.class)
					.getSingleResult();
	}

	@Override
	public DafRelatedPerson createRelatedPerson(RelatedPerson theRelatedPerson) {
		Session session = sessionFactory.openSession();
		DafRelatedPerson dafRelatedPerson = new DafRelatedPerson();
		IParser jsonParser = fhirContext.newJsonParser();;
		dafRelatedPerson.setData(jsonParser.encodeResourceToString(theRelatedPerson));
		session.beginTransaction();
		session.save(dafRelatedPerson);
		session.getTransaction().commit();
		session.close();
		return dafRelatedPerson;
	}

	@Override
	public DafRelatedPerson updateRelatedPersonById(int id, RelatedPerson theRelatedPerson) {
		DafRelatedPerson dafRelatedPerson = new DafRelatedPerson();
		IParser jsonParser = fhirContext.newJsonParser();
		dafRelatedPerson.setId(id);
		dafRelatedPerson.setData(jsonParser.encodeResourceToString(theRelatedPerson));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafRelatedPerson);
		session.getTransaction().commit();
		session.close();
		return dafRelatedPerson;
	}

	@Override
	public List<DafRelatedPerson> search(SearchParameterMap paramMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafProcedure.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

	   // build criteria for id
	   buildIdCriteria(paramMap, criteria);

	   // build criteria for identifier
	   buildIdentifierCriteria(paramMap, criteria);
	   
	   buildPatientCriteria(paramMap, criteria);
	   
	   return criteria.list();
	}
	
	/**
	 * This method builds criteria for Observation id
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
	 * This method builds criteria for Observation identifier
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
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'value' = '"
										+ identifier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'value' = '"
										+ identifier.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->0->>'system' = '"
										+ identifier.getSystem() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'identifier'->1->>'system' = '"
										+ identifier.getSystem() + "'")
								);
					}
					disjunction.add(orCond);
				}
				criteria.add(disjunction);
			}
		}
	}
	
	/**
	 * This method builds criteria for Observation patient
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
										"{alias}.data->'patient'->>'reference' = '" + patient.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'patient'->>'display' = '" + patient.getValue() + "'"));

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

	@SuppressWarnings("unchecked")
	@Override
	public List<DafRelatedPerson> getRelatedPersonForPatientsBulkData(String id, Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafRelatedPerson.class);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<DafRelatedPerson> getRelatedPersonForBulkData(Date start, Date end) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafRelatedPerson.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
	}
}
