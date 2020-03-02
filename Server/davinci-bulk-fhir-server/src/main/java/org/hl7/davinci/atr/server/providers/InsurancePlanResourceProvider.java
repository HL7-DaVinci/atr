package org.hl7.davinci.atr.server.providers;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafInsurancePlan;
import org.hl7.davinci.atr.server.service.InsurancePlanService;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InsurancePlan;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class InsurancePlanResourceProvider extends AbstractJaxRsResourceProvider<InsurancePlan> {

	public static final String RESOURCE_TYPE = "InsurancePlan";
	public static final String VERSION_ID = "1";

	@Autowired
	private InsurancePlanService service;

	public InsurancePlanResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	@Override
	public Class<InsurancePlan> getResourceType() {
		return InsurancePlan.class;
	}

	/**
	 * The create operation saves a new resource to the server, allowing the server
	 * to give that resource an ID and version ID. Create methods must be annotated
	 * with the @Create annotation, and have a single parameter annotated with
	 * the @ResourceParam annotation. This parameter contains the resource instance
	 * to be created. Create methods must return an object of type MethodOutcome .
	 * This object contains the identity of the created resource. Example URL to
	 * invoke this method (this would be invoked using an HTTP POST, with the
	 * resource in the POST body): http://<server
	 * name>/<context>/fhir/AllergyIntolerance
	 * 
	 * @param theAllergyIntolerance
	 * @return
	 */
	@Create
	public MethodOutcome createInsurancePlan(@ResourceParam InsurancePlan theInsurancePlan) {

		// Save this AllergyIntolerance to the database...
		DafInsurancePlan dafInsurancePlan = service.createInsurancePlan(theInsurancePlan);

		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafInsurancePlan.getId() + "", VERSION_ID));

		return retVal;
	}

	/**
	 * The update operation updates a specific resource instance (using its ID).
	 * Update methods must be annotated with the @Update annotation, and have a
	 * parameter annotated with the @ResourceParam annotation. This parameter
	 * contains the resource instance to be created. Example URL to invoke this
	 * method (this would be invoked using an HTTP PUT, with the resource in the PUT
	 * body): http://<server name>/<context>/fhir/AllergyIntolerance/1
	 * 
	 * @param theId
	 * @param theAllergyIntolerance
	 * @return
	 */
	@Update
	public MethodOutcome updateInsurancePlanById(@IdParam IdType theId, @ResourceParam InsurancePlan theInsurancePlan) {
		int id;
		try {
			id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}

		Meta meta = new Meta();
		meta.setVersionId("1");
		Date date = new Date();
		meta.setLastUpdated(date);
		theInsurancePlan.setMeta(meta);
		// Update this Patient to the database...
		DafInsurancePlan dafInsurancePlan = service.updateInsurancePlanById(id, theInsurancePlan);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafInsurancePlan.getId() + "", VERSION_ID));
		return retVal;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/AllergyIntolerance/1/_history/4
	 * 
	 * @param theId : Id of the AllergyIntolerance
	 * @return : Object of AllergyIntolerance information
	 */
	@Read(version = true)
	public InsurancePlan readOrVread(@IdParam IdType theId) {
		int id;
		InsurancePlan insurancePlan;
		try {
			id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			insurancePlan = service.getInsurancePlanByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			insurancePlan = service.getInsurancePlanById(id);
		}
		return insurancePlan;
	}

	/**
	 * This method is implemented to get AllergyIntolerances for Bulk data request
	 * 
	 * @param patients
	 * @param start
	 * @return This method returns a list of AllergyIntolerance. This list may
	 *         contain multiple matching resources, or it may also be empty.
	 */
	public List<InsurancePlan> getInsurancePlanForBulkDataRequest(List<String> patients, Date start, Date end) {
		System.out.println("===========******IN INSURANCEPLAN PROVIDERS START******================");

		List<InsurancePlan> insurancePlanList = service.getInsurancePlanForBulkData(patients, start, end);
		return insurancePlanList;
	}
}
