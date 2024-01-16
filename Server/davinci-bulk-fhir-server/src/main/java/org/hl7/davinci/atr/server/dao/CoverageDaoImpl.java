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
import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.util.CommonUtil;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(CoverageDaoImpl.class);    
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public DafCoverage getCoverageForPatientsBulkData(String coverageIds, Date start, Date end){
    	
		Criteria criteria = getSession().createCriteria(DafCoverage.class);
//		if(patientId!=null) {
//			criteria.add(Restrictions.sqlRestriction("{alias}.data->'subscriber'->>'reference' = 'Patient/"+patientId+"'"));
//		}
		if(coverageIds!=null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '"+ coverageIds+"' order by {alias}.data->'meta'->>'versionId' desc"));
		}
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return (DafCoverage) criteria.list().get(0);
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
	public Coverage createCoverage(Coverage theCoverage) {
		Session session = sessionFactory.openSession();
		DafCoverage dafCoverage = new DafCoverage();
		IParser jsonParser = fhirContext.newJsonParser();
		Meta meta = new Meta();
		if(theCoverage.hasMeta()) {
			if(!theCoverage.getMeta().hasVersionId()) {
        		meta.setVersionId("1");
        		theCoverage.setMeta(meta);

			}
			if(!theCoverage.getMeta().hasLastUpdated()) {
				Date date = new Date();
        		meta.setLastUpdated(date);
        		theCoverage.setMeta(meta);
			}
		}
		else {
    		meta.setVersionId("1");
    		Date date = new Date();
    		meta.setLastUpdated(date);
    		theCoverage.setMeta(meta);
		}
		if(!theCoverage.hasIdElement()) {
			String id = CommonUtil.getUniqueUUID();
			theCoverage.setId(id);
			logger.info(" setting the uuid ");
		}
		dafCoverage.setData(jsonParser.encodeResourceToString(theCoverage));
		session.beginTransaction();
		session.save(dafCoverage);
		session.getTransaction().commit();
		session.close();
		return theCoverage;
	}

	@Override
	public DafCoverage getCoverageById(String id) {
		DafCoverage dafCoverage = null;
		try {
			List<DafCoverage> list = getSession().createNativeQuery(
					"select * from coverage where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", DafCoverage.class)
						.getResultList();
			if(list != null && !list.isEmpty()) {
				dafCoverage = new DafCoverage();
				dafCoverage = list.get(0);
			}
		}
		catch(Exception ex) {
			logger.error("Exception in getCoverageById of CoverageDaoImpl ", ex);
		}
		return dafCoverage;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DafCoverage> search(SearchParameterMap theMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafCoverage.class)
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
	public DafCoverage getCoverageByVersionId(String id, String versionIdPart) {
		return getSession().createNativeQuery(
				"select * from coverage where data->>'id' = '"+id+"' "
						+ "and data->'meta'->>'versionId' = '"+versionIdPart+"'", DafCoverage.class)
					.getSingleResult();
	}

	@Override
	public DafCoverage getCoverageByPatientReference(String patientMemberId) {
		DafCoverage dafCoverage = null;
		try {
			//select id as id, data as data, last_updated_ts as last_upd from patient where id in (select distinct(id) from patient r, LATERAL json_array_elements(r.data->'identifier') segment WHERE  ( segment ->>'value' = '"+memberId+"'  and  segment ->>'system' = '"+TextConstants.MEMBERID_SYSTEM+"' ) )
			List<DafCoverage> list = getSession().createNativeQuery("select * from coverage where data->'beneficiary'->>'reference'='Patient/"+patientMemberId+"' order by data->'meta'->>'versionId' desc", DafCoverage.class).getResultList();	
			if(list != null && !list.isEmpty()) {
				dafCoverage = list.get(0);
			}
		}
		catch(Exception e) {
			logger.error("Exception in getCoverageByPatientReference of CoverageDaoImpl ", e);
		}
		return dafCoverage;
	}

	@Override
	public DafCoverage getCoverageByIdentifier(String system, String value) {
		DafCoverage dafCoverage = null;
		try {
			List<DafCoverage> list = getSession().createNativeQuery("select * from coverage where id in (select distinct(id) from coverage r, LATERAL json_array_elements(r.data->'identifier') segment WHERE  ( segment ->>'value' = '"+value+"'  and  segment ->>'system' = '"+system+"' ) ) order by data->'meta'->>'versionId' desc ", DafCoverage.class).getResultList();	
			if(list != null && !list.isEmpty()) {
				dafCoverage = list.get(0);
			}
		}
		catch(Exception e) {
			logger.error("Exception in getCoverageByIdentifier of CoverageDaoImpl ", e);
		}
		return dafCoverage;
	}
}