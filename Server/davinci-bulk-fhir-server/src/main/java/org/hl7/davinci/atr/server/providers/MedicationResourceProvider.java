package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafMedication;
import org.hl7.davinci.atr.server.service.MedicationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class MedicationResourceProvider extends AbstractJaxRsResourceProvider<Medication> {

	public static final String RESOURCE_TYPE = "Medication";
	public static final String VERSION_ID = "1";
	@Autowired
	MedicationService service;

	public MedicationResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Medication> getResourceType() {
		return Medication.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Medication/1/_history/4
	 * 
	 * @param theId : Id of the medication
	 * @return : Object of medication information
	 */
	@Read(version = true)
	public Medication readOrVread(@IdParam IdType theId) {
		int id;
		Medication medication;
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
			medication = service.getMedicationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			medication = service.getMedicationById(id);

		}
		return medication;
	}
	
	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIngredient
	 * @param theIdentifier
	 * @param theIngredientCode
	 * @param theCode
	 * @param theForm
	 * @param theLotNumber
	 * @param theExpirationDate
	 * @param theManufacturer
	 * @param theStatus
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
		@OptionalParam(name = Medication.SP_RES_ID) 
		StringAndListParam theId,

		@Description(shortDefinition = "The actual ingredient or content")
		@OptionalParam(name = Medication.SP_INGREDIENT) 
		ReferenceAndListParam theIngredient,

		@Description(shortDefinition = "Returns medications with this external identifier")
		@OptionalParam(name = Medication.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,
		
		@Description(shortDefinition = "Codes that identify this medication")
		@OptionalParam(name = Medication.SP_CODE) 
		TokenAndListParam theCode,

		@Description(shortDefinition = "The actual ingredient or content") 
		@OptionalParam(name = Medication.SP_INGREDIENT_CODE) 
		TokenAndListParam theIngredientCode,

		@Description(shortDefinition = "powder | tablets | capsule +") 
		@OptionalParam(name = Medication.SP_FORM) 
		TokenAndListParam theForm,

		@Description(shortDefinition = "Identifier assigned to batch") 
		@OptionalParam(name = Medication.SP_LOT_NUMBER) 
		TokenAndListParam theLotNumber,

		@Description(shortDefinition = "When batch will expire")
		@OptionalParam(name = Medication.SP_EXPIRATION_DATE) 
		DateAndListParam theExpirationDate,

		@Description(shortDefinition = "Manufacturer of the item") 
		@OptionalParam(name = Medication.SP_MANUFACTURER) 
		ReferenceAndListParam theManufacturer,

		@Description(shortDefinition = "active | inactive | entered-in-error") 
		@OptionalParam(name = Medication.SP_STATUS)
		TokenAndListParam theStatus,

		@IncludeParam(allow = { "*" }) Set<Include> theIncludes,

		@Sort SortSpec theSort,

		@Count Integer theCount) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Medication.SP_RES_ID, theId);
		paramMap.add(Medication.SP_INGREDIENT, theIngredient);
		paramMap.add(Medication.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Medication.SP_CODE, theCode);
		paramMap.add(Medication.SP_INGREDIENT_CODE, theIngredientCode);
		paramMap.add(Medication.SP_FORM, theForm);
		paramMap.add(Medication.SP_LOT_NUMBER, theLotNumber);
		paramMap.add(Medication.SP_EXPIRATION_DATE, theExpirationDate);
		paramMap.add(Medication.SP_MANUFACTURER, theManufacturer);
		paramMap.add(Medication.SP_STATUS, theStatus);
		paramMap.setIncludes(theIncludes);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<Medication> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> medicationList = new ArrayList<>();
				for (Medication theMedication : results) {
					medicationList.add(theMedication);
				}
				return medicationList;
			}

			@Override
			public Integer size() {
				return results.size();
			}

			@Override
			public InstantDt getPublished() {
				return published;
			}

			@Override
			public Integer preferredPageSize() {
				return null;
			}

			@Override
			public String getUuid() {
				return null;
			}
		};
	}
	
    /**	
     * The create  operation saves a new resource to the server, 
     * allowing the server to give that resource an ID and version ID.
     * Create methods must be annotated with the @Create annotation, 
     * and have a single parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Create methods must return an object of type MethodOutcome . 
     * This object contains the identity of the created resource.
     * Example URL to invoke this method (this would be invoked using an HTTP POST, 
     * with the resource in the POST body): http://<server name>/<context>/fhir/Medication
     * @param theMedication
     * @return
     */
    @Create
    public MethodOutcome createMedication(@ResourceParam Medication theMedication) {
         
    	// Save this Medication to the database...
    	DafMedication dafMedication = service.createMedication(theMedication);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedication.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Medication/1
     * @param theId
     * @param theMedication
     * @return
     */
    @Update
    public MethodOutcome updateMedicationById(@IdParam IdType theId, 
    										@ResourceParam Medication theMedication) {
    	int id;
    	try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
    	
    	Meta meta = new Meta();
		meta.setVersionId("1");
		Date date = new Date();
		meta.setLastUpdated(date);
		theMedication.setMeta(meta);
    	// Update this Medication to the database...
    	DafMedication dafMedication = service.updateMedicationById(id, theMedication);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedication.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Medication> getMedicationForBulkDataRequest(List<String> patients, Date start, Date end) {

		List<Medication> medList = service.getMedicationForBulkData(patients, start, end);
		return medList;
    }
}
