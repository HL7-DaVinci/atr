package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafInsurancePlan;
import org.hl7.fhir.r4.model.InsurancePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Repository("InsurancePlanDao")
public class InsurancePlanDaoImpl extends AbstractDao implements InsurancePlanDao{

	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	/**
	 * This method builds criteria for fetching InsurancePlan record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the InsurancePlan
	 */
	public DafInsurancePlan getInsurancePlanById(int id) {
		List<DafInsurancePlan> list = getSession().createNativeQuery(
				"select * from insuranceplan where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", 
				DafInsurancePlan.class)
					.getResultList();
			return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the
	 * InsurancePlan record by id.
	 * 
	 * @param theId     : ID of the InsurancePlan
	 * @param versionId : version of the InsurancePlan record
	 * @return : DAF object of the InsurancePlan
	 */
	public DafInsurancePlan getInsurancePlanByVersionId(int theId, String versionId) {
		return getSession().createNativeQuery(
				"select * from insuranceplan where id = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", 
				DafInsurancePlan.class).getSingleResult();
	}

	/**
     * This method builds criteria for creating the patient
     * @return : InsurancePlan record
     */
    @Override
	@Transactional
	public DafInsurancePlan createInsurancePlan(InsurancePlan theInsurancePlan) {
		Session session = sessionFactory.openSession();
		DafInsurancePlan dafInsurancePlan = new DafInsurancePlan();
		IParser jsonParser = fhirContext.newJsonParser();;
		dafInsurancePlan.setData(jsonParser.encodeResourceToString(theInsurancePlan));
		session.beginTransaction();
		session.save(dafInsurancePlan);
		session.getTransaction().commit();
		session.close();
		return dafInsurancePlan;
	}

	@Override
	public DafInsurancePlan updateInsurancePlanById(int theId, InsurancePlan theInsurancePlan) {
		DafInsurancePlan dafInsurancePlan = new DafInsurancePlan();
		IParser jsonParser = fhirContext.newJsonParser();
		dafInsurancePlan.setId(theId);
		dafInsurancePlan.setData(jsonParser.encodeResourceToString(theInsurancePlan));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafInsurancePlan);
		session.getTransaction().commit();
		session.close();
		return dafInsurancePlan;
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	public List<DafInsurancePlan> getInsurancePlanForBulkData(Date start, Date end) {

		Criteria criteria = getSession().createCriteria(DafInsurancePlan.class, "InsurancePlan");
        criteria.add(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
		return criteria.list();
	}

	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	public List<DafInsurancePlan> getInsurancePlanForPatientsBulkData(String patientId, Date start,
			Date end) {
		Criteria criteria = getSession().createCriteria(DafInsurancePlan.class, "InsurancePlan");
		if(patientId != null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->'patient'->>'reference' = 'Patient/"+patientId+"'"));
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
