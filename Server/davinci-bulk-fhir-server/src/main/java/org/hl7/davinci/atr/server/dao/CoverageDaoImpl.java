package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafCondition;
import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("coverageDao")
public class CoverageDaoImpl extends AbstractDao implements CoverageDao {
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public List<DafCoverage> getCoverageForPatientsBulkData(String patientId, Date start, Date end){
    	
		Criteria criteria = getSession().createCriteria(DafCoverage.class);
		if(patientId!=null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->'subscriber'->>'reference' = 'Patient/"+patientId+"'"));
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
	public List<DafCoverage> getCoverageForBulkData(Date start, Date end){
    	
		Criteria criteria = getSession().createCriteria(DafCoverage.class);
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'subscriber' IS NOT NULL"));
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return criteria.list();
    }

	@Override
	public DafCoverage updateCoverageById(int theId, Coverage theCoverage) {
		DafCoverage dafCoverage = new DafCoverage();
		IParser jsonParser = fhirContext.newJsonParser();
		dafCoverage.setId(theId);
		dafCoverage.setData(jsonParser.encodeResourceToString(theCoverage));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafCoverage);
		session.getTransaction().commit();
		session.close();
		return dafCoverage;
	}

	@Override
	public DafCoverage createCoverage(Coverage theCoverage) {
		Session session = sessionFactory.openSession();
		DafCoverage dafCoverage = new DafCoverage();
		IParser jsonParser = fhirContext.newJsonParser();;
		dafCoverage.setData(jsonParser.encodeResourceToString(theCoverage));
		session.beginTransaction();
		session.save(dafCoverage);
		session.getTransaction().commit();
		session.close();
		return dafCoverage;
	}

	@Override
	public DafCoverage getCoverageById(int id) {
		List<DafCoverage> list = getSession().createNativeQuery(
			"select * from coverage where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafCoverage.class)
				.getResultList();
			return list.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafCoverage> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafCondition.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(theMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(theMap, criteria);
		
		buildPatientCriteria(theMap, criteria);
		
		return criteria.list();
	}
	
	/**
	 * This method builds criteria for coverage id
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
	 * This method builds criteria for coverage identifier
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
	 * This method builds criteria for coverage patient
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
					ReferenceParam subject = (ReferenceParam) params;
					Criterion criterion = null;
					if (subject.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'beneficiary'->>'reference' ilike '%" + subject.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'beneficiary'->>'display' ilike '%" + subject.getValue() + "%'"));

					} else if (subject.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'beneficiary' IS NULL"));

					} else if (!subject.getMissing()) {
						criterion = Restrictions
								.or(Restrictions.sqlRestriction("{alias}.data->>'beneficiary' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}		
	}

	@Override
	public DafCoverage getCoverageByVersionId(int id, String versionIdPart) {
		return getSession().createNativeQuery(
				"select * from coverage where data->>'id' = '"+id+"' "
						+ "and data->'meta'->>'versionId' = '"+versionIdPart+"'", DafCoverage.class)
					.getSingleResult();
	}
}