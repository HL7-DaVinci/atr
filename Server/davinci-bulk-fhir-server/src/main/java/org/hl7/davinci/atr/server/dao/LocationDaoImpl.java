package org.hl7.davinci.atr.server.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.SpecialParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hl7.davinci.atr.server.model.DafLocation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository("locationDao")
public class LocationDaoImpl extends AbstractDao implements LocationDao {
	
	@Autowired
	FhirContext fhirContext;

	@Autowired
    private SessionFactory sessionFactory;
	
	/**
	 * This method builds criteria for fetching location record by id.
	 * 
	 * @param id : ID of the resource
	 * @return : DAF object of the location
	 */
	@Override
	public DafLocation getLocationById(int id) {
		List<DafLocation> list = getSession().createNativeQuery(
				"select * from location where data->>'id' = '"+id+"' order by data->'meta'->>'versionId' desc", 
				DafLocation.class)
					.getResultList();
			return list.get(0);
	}

	/**
	 * This method builds criteria for fetching particular version of the location
	 * record by id.
	 * 
	 * @param theId     : ID of the location
	 * @param versionId : version of the location record
	 * @return : DAF object of the location
	 */
	@Override
	public DafLocation getLocationByVersionId(int theId, String versionId) {
		return getSession().createNativeQuery(
				"select * from location where id = '"+theId+"' and data->'meta'->>'versionId' = '"+versionId+"'", 
				DafLocation.class).getSingleResult();
	}

	/**
	 * This method invokes various methods for search
	 * 
	 * @param searchParameterMap : parameter for search
	 * @return criteria : DAF location object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DafLocation> search(SearchParameterMap searchParameterMap) {
		@SuppressWarnings("deprecation")
		Criteria criteria = getSession().createCriteria(DafLocation.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// build criteria for id
		buildIdCriteria(searchParameterMap, criteria);

		// build criteria for identifier
		buildIdentifierCriteria(searchParameterMap, criteria);

		// build criteria for name
		buildNameCriteria(searchParameterMap, criteria);

		// build criteria for address
		buildAddressCriteria(searchParameterMap, criteria);

		// build criteria for address state
		buildAddressStateCriteriea(searchParameterMap, criteria);

		// build criteria for address county
		buildAddressCountryCriteria(searchParameterMap, criteria);

		// build criteria for address postal code
		buildAddressPostalcodeCriteria(searchParameterMap, criteria);

		// build criteria for address-city
		buildAddressCityCriteria(searchParameterMap, criteria);

		// build criteria for organization
		buildOrganizationCriteria(searchParameterMap, criteria);

		// build criteria for status
		buildStausCriteria(searchParameterMap, criteria);

		// build criteria for operational status
		buildOperationalStausCriteria(searchParameterMap, criteria);

		// build criteria for partof
		buildPartofCriteria(searchParameterMap, criteria);

		// build criteria for near
		buildNearCriteria(searchParameterMap, criteria);

		// build criteria for address-use
		buildAddressUseCriteria(searchParameterMap, criteria);

		// build criteria for type
		buildTypeCriteria(searchParameterMap, criteria);

		// build criteria for endpoint
		buildEndpointCriteria(searchParameterMap, criteria);

		return criteria.list();
	}

	/**
	 * This method builds criteria for endpoint
	 * 
	 * @param searchParameterMap : search parameter "endpoint"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildEndpointCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("endpoint");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam endpoint = (ReferenceParam) params;
					Criterion criterion = null;
					if (endpoint.getValue() != null) {
						criterion = Restrictions.or(
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->0->>'reference' ilike '%" + endpoint.getValue() + "%'"),
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->0->>'display' ilike '%" + endpoint.getValue() + "%'"),
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->0->>'type' ilike '%" + endpoint.getValue() + "%'"),
							
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->1->>'reference' ilike '%" + endpoint.getValue() + "%'"),
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->1->>'display' ilike '%" + endpoint.getValue() + "%'"),
							Restrictions.sqlRestriction(
									"{alias}.data->'endpoint'->1->>'type' ilike '%" + endpoint.getValue() + "%'")
						);

					} else if (endpoint.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NULL"));

					} else if (!endpoint.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'endpoint' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
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
	private void buildTypeCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("type");
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
											+ type.getValue() + "%'"),
									
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->0->>'system' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->0->>'code' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->0->>'display' ilike '%"
											+ type.getValue() + "%'"),
									
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'system' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'code' ilike '%"
											+ type.getValue() + "%'"),
									Restrictions.sqlRestriction("{alias}.data->'type'->1->'coding'->1->>'display' ilike '%"
											+ type.getValue() + "%'")	
								);
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
	 * This method builds criteria for address-use
	 * 
	 * @param searchParameterMap : search parameter "address-use"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressUseCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address-use");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					TokenParam addressUse = (TokenParam) params;
					if (!addressUse.isEmpty()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'use' ilike '%" + addressUse.getValue() + "%'"));
					} else if (addressUse.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'address'->>'use' IS NULL"));
					} else if (!addressUse.getMissing()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'address'->>'use' IS NOT NULL"));
					}

				}
			}
		}
	}

	/**
	 * This method builds criteria for near
	 * 
	 * @param searchParameterMap : search parameter "near"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildNearCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("near");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					SpecialParam position = (SpecialParam) params;
					Criterion criterion = null;
					if (!position.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'position'->>'longitude' ilike '%" + position.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'position'->>'latitude' ilike '%" + position.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'position'->>'altitude' ilike '%" + position.getValue() + "%'"));
					} else if (position.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'position' IS NULL"));
					} else if (!position.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'position' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}

	}

	/**
	 * This method builds criteria for partof
	 * 
	 * @param searchParameterMap : search parameter "partof"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildPartofCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("partof");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam partOf = (ReferenceParam) params;
					Criterion criterion = null;
					if (partOf.getValue() != null) {
						criterion = Restrictions.or(Restrictions.sqlRestriction(
								"{alias}.data->'partOf'->>'reference' ilike '%" + partOf.getValue() + "%'"));

					} else if (partOf.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NULL"));

					} else if (!partOf.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'partOf' IS NOT NULL"));

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
	private void buildStausCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("status");
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
	 * This method builds criteria for operational-status
	 * 
	 * @param searchParameterMap : search parameter "operational-status"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildOperationalStausCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("operational-status");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					TokenParam operationalStatus = (TokenParam) params;
					Criterion criterion = null;
					if (!operationalStatus.isEmpty()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'operationalStatus'->>'system' ilike '%"
										+ operationalStatus.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'operationalStatus'->>'code' ilike '%"
										+ operationalStatus.getValue() + "%'"),
								Restrictions.sqlRestriction("{alias}.data->'operationalStatus'->>'display' ilike '%"
										+ operationalStatus.getValue() + "%'"));
					} else if (operationalStatus.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'operationalStatus' IS NULL"));

					} else if (!operationalStatus.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'operationalStatus' IS NOT NULL"));

					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for location id
	 * 
	 * @param searchParameterMap : search parameter "_id"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildIdCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("_id");
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
	 * This method builds criteria for location identifier
	 * 
	 * @param searchParameterMap : search parameter "identifier"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildIdentifierCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("identifier");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType param : values) {
					TokenParam identifier = (TokenParam) param;
					if (identifier.getValue() != null) {
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'identifier'->0->>'value' ilike '" + identifier.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'identifier'->1->>'value' ilike '" + identifier.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'identifier'->0->>'system' ilike '" + identifier.getValue() + "%'"));
						disjunction.add(Restrictions.sqlRestriction(
								"{alias}.data->'identifier'->1->>'system' ilike '" + identifier.getValue() + "%'"));
					}
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for organization
	 * 
	 * @param searchParameterMap : search parameter "organization"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildOrganizationCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("organization");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					ReferenceParam organisation = (ReferenceParam) params;
					Criterion criterion = null;
					if (organisation.getValue() != null) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'reference' = '"
										+ organisation.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'display' = '"
										+ organisation.getValue() + "'"),
								Restrictions.sqlRestriction("{alias}.data->'managingOrganization'->>'type' = '"
										+ organisation.getValue() + "'")
								);

					} else if (organisation.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'managingOrganization' IS NULL"));

					} else if (!organisation.getMissing()) {
						criterion = Restrictions.or(Restrictions.sqlRestriction("{alias}.data->>'managingOrganization' IS NOT NULL"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
	 * This method builds criteria for location name
	 * 
	 * @param searchParameterMap : search parameter "name"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildNameCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("name");

		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam name = (StringParam) params;
					if (name.isExact()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->>'name'= '" + name.getValue() + "'"));
					} else if (name.isContains()) {
						criteria.add(
								Restrictions.sqlRestriction("{alias}.data->>'name' ilike '%" + name.getValue() + "%'"));
					} else {
						criteria.add(
								Restrictions.sqlRestriction("{alias}.data->>'name' ilike '" + name.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for location address country
	 * 
	 * @param searchParameterMap : search parameter "address-country"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressCountryCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address-country");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam addressCountry = (StringParam) params;
					if (addressCountry.isExact()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'country' = '" + addressCountry.getValue() + "'"));
					} else if (addressCountry.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'country' ilike '%" + addressCountry.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'country' ilike '" + addressCountry.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for location address postal code
	 * 
	 * @param searchParameterMap : search parameter "address-postalcode"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressPostalcodeCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address-postalcode");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam addressPostalcode = (StringParam) params;
					if (addressPostalcode.isExact()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'postalCode' = '" + addressPostalcode.getValue() + "'"));
					} else if (addressPostalcode.isContains()) {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'address'->>'postalCode' ilike '%"
								+ addressPostalcode.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction("{alias}.data->'address'->>'postalCode' ilike '"
								+ addressPostalcode.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for location address state
	 * 
	 * @param searchParameterMap : search parameter "address-state"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressStateCriteriea(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address-state");
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam addressState = (StringParam) params;
					if (addressState.isExact()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'state' = '" + addressState.getValue() + "'"));
					} else if (addressState.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'state' ilike '%" + addressState.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'state' ilike '" + addressState.getValue() + "%'"));
					}
				}

			}
		}
	}

	/**
	 * This method builds criteria for location address city
	 * 
	 * @param searchParameterMap : search parameter "address-city"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressCityCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address-city");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					StringParam addressCity = (StringParam) params;
					if (addressCity.isExact()) {
						criteria.add(Restrictions
								.sqlRestriction("{alias}.data->'address'->>'city' = '" + addressCity.getValue() + "'"));
					} else if (addressCity.isContains()) {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'city' ilike '%" + addressCity.getValue() + "%'"));
					} else {
						criteria.add(Restrictions.sqlRestriction(
								"{alias}.data->'address'->>'city' ilike '" + addressCity.getValue() + "%'"));
					}
				}
			}
		}
	}

	/**
	 * This method builds criteria for location address
	 * 
	 * @param searchParameterMap : search parameter "address"
	 * @param criteria           : for retrieving entities by composing Criterion
	 *                           objects
	 */
	private void buildAddressCriteria(SearchParameterMap searchParameterMap, Criteria criteria) {
		List<List<? extends IQueryParameterType>> list = searchParameterMap.get("address");
		if (list != null) {

			for (List<? extends IQueryParameterType> values : list) {
				Disjunction disjunction = Restrictions.disjunction();
				for (IQueryParameterType params : values) {
					StringParam address = (StringParam) params;
					Criterion criterion = null;
					if (address.isExact()) {
						criterion = Restrictions.or(
								Restrictions
										.sqlRestriction("{alias}.data->'address'->>'use'='" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>0 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>1 = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'city' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'state' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'postalCode' = '" + address.getValue() + "'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'country' = '" + address.getValue() + "'"));
					} else if (address.isContains()) {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'use' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>0 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>1 ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'city' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'state' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'postalCode' ilike '%" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'country' ilike '%" + address.getValue() + "%'")

						);
					} else {
						criterion = Restrictions.or(
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'use' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>0 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->'line'->>1 ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'city' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'state' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'postalCode' ilike '" + address.getValue() + "%'"),
								Restrictions.sqlRestriction(
										"{alias}.data->'address'->>'country' ilike '" + address.getValue() + "%'"));
					}
					disjunction.add(criterion);
				}
				criteria.add(disjunction);
			}
		}
	}

	/**
     * This method builds criteria for creating the patient
     * @return : patient record
     */
    @Override
    @Transactional
	public DafLocation createLocation(Location theLocation) {
		Session session = sessionFactory.openSession();
		DafLocation dafLocation = new DafLocation();
		IParser jsonParser = fhirContext.newJsonParser();;
		dafLocation.setData(jsonParser.encodeResourceToString(theLocation));
		session.beginTransaction();
		session.save(dafLocation);
		session.getTransaction().commit();
		session.close();
		return dafLocation;
	}

	@Override
	public DafLocation updateLocationById(int theId, Location theLocation) {
		DafLocation dafLocation = new DafLocation();
		IParser jsonParser = fhirContext.newJsonParser();
		dafLocation.setId(theId);
		dafLocation.setData(jsonParser.encodeResourceToString(theLocation));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(dafLocation);
		session.getTransaction().commit();
		session.close();
		return dafLocation;
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public DafLocation getLocationForBulkData(String patients, Date start, Date end){
	
		Criteria criteria = getSession().createCriteria(DafLocation.class, "location");
		//criteria.add(Restrictions.sqlRestriction("{alias}.data->>'patient' IS NOT NULL"));
		// .createAlias("location.patient", "dp");
		/*if(patients!=null) {
        	criteria.add(Restrictions.in("dp.id", patients));
		}*/
		if(patients!=null) {
			criteria.add(Restrictions.sqlRestriction("{alias}.data->>'id' = '"+ patients+"' order by {alias}.data->'meta'->>'versionId' desc"));
		}
		if(start != null) {
			criteria.add(Restrictions.ge("timestamp", start));
		}
		if(end != null) {
			criteria.add(Restrictions.le("timestamp", end));
		}
    	return (DafLocation) criteria.list().get(0);
	}
}
