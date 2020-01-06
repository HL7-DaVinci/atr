package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafObservation;
import org.hl7.davinci.atr.server.service.ObservationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Observation;
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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.QuantityAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class ObservationResourceProvider extends AbstractJaxRsResourceProvider<Observation> {

	public static final String RESOURCE_TYPE = "Observation";
	public static final String VERSION_ID = "1";
	@Autowired
	ObservationService service;

	public ObservationResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Observation> getResourceType() {
		return Observation.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Observation/1/_history/4
	 * 
	 * @param theId : Id of the observation
	 * @return : Object of observation information
	 */
	@Read(version = true)
	public Observation readOrVread(@IdParam IdType theId) {
		int id;
		Observation observation;
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
			observation = service.getObservationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			observation = service.getObservationById(id);
		}
		return observation;
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
	 * @param theIdentifier
	 * @param theSubject
	 * @param theValueQuantity
	 * @param theBasedOn
	 * @param theHasMember
	 * @param thePerformer
	 * @param theDevice
	 * @param theStatus
	 * @param thePartOf
	 * @param theDerivedFrom
	 * @param theCategory
	 * @param theCode
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity") 
		@OptionalParam(name = Observation.SP_RES_ID) 
		StringAndListParam theId,

		@Description(shortDefinition = "The unique id for a particular observation") 
		@OptionalParam(name = Observation.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "Obtained date/time. If the obtained element is a period, a date that falls in the period") 
		@OptionalParam(name = Observation.SP_DATE) 
		DateRangeParam theDate,

		@Description(shortDefinition = "The reason why the expected value in the element Observation.value[x] or Observation.component.value[x] is missing.")
		@OptionalParam(name = Observation.SP_COMBO_DATA_ABSENT_REASON) 
		TokenAndListParam theComboDataAbsentReason,

		@Description(shortDefinition = "The code of the observation type") 
		@OptionalParam(name = Observation.SP_CODE) 
		TokenAndListParam theCode,

		@Description(shortDefinition = "The subject that the observation is about")
		@OptionalParam(name = Observation.SP_SUBJECT) 
		ReferenceAndListParam theSubject,

		@Description(shortDefinition = "The reason why the expected value in the element Observation.component.value[x] is missing")
		@OptionalParam(name = Observation.SP_COMPONENT_DATA_ABSENT_REASON) 
		TokenAndListParam theComponentDataAbsentReason,

		@Description(shortDefinition = "The value of the observation, if the value is a CodeableConcept") 
		@OptionalParam(name = Observation.SP_VALUE_CONCEPT) 
		TokenAndListParam theValueConcept,

		@Description(shortDefinition = "The value of the observation, if the value is a date or period of time") 
		@OptionalParam(name = Observation.SP_VALUE_DATE)
		DateRangeParam theValueDate,

		@Description(shortDefinition = "The focus of an observation when the focus is not the patient of record.") 
		@OptionalParam(name = Observation.SP_FOCUS)
		ReferenceAndListParam theFocus,

		@Description(shortDefinition = "Related measurements the observation is made from")
		@OptionalParam(name = Observation.SP_DERIVED_FROM)
		ReferenceAndListParam theDerivedFrom,

		@Description(shortDefinition = "Part of referenced event")
		@OptionalParam(name = Observation.SP_PART_OF)
		ReferenceAndListParam thePartOf,

		@Description(shortDefinition = "Related resource that belongs to the Observation group")
		@OptionalParam(name = Observation.SP_HAS_MEMBER)
		ReferenceAndListParam theHasMember,

		@Description(shortDefinition = "Reference to the service request.") 
		@OptionalParam(name = Observation.SP_BASED_ON)
		ReferenceAndListParam theBasedOn,

		@Description(shortDefinition = "The subject that the observation is about (if patient)")
		@OptionalParam(name = Observation.SP_PATIENT)
		ReferenceAndListParam thePatient,

		@Description(shortDefinition = "Specimen used for this observation")
		@OptionalParam(name = Observation.SP_SPECIMEN) 
		ReferenceAndListParam theSpecimen,

		@Description(shortDefinition = "The component code of the observation type") 
		@OptionalParam(name = Observation.SP_COMPONENT_CODE)
		TokenAndListParam thecomponentCode,

		@Description(shortDefinition = "The value of the observation, if the value is a string, and also searches in CodeableConcept.text") 
		@OptionalParam(name = Observation.SP_VALUE_STRING) 
		StringAndListParam theValueString,

		@Description(shortDefinition = "Who performed the observation") 
		@OptionalParam(name = Observation.SP_PERFORMER)
		ReferenceAndListParam thePerformer,

		@Description(shortDefinition = "The code of the observation type or component type")
		@OptionalParam(name = Observation.SP_COMBO_CODE)
		TokenAndListParam theComboCOde,

		@Description(shortDefinition = "The method used for the observation")
		@OptionalParam(name = Observation.SP_METHOD) 
		TokenAndListParam theMethod,

		@Description(shortDefinition = "The value of the observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)")
		@OptionalParam(name = Observation.SP_VALUE_QUANTITY) 
		QuantityAndListParam theValueQuantity,

		@Description(shortDefinition = "The value of the component observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)")
		@OptionalParam(name = Observation.SP_COMPONENT_VALUE_QUANTITY) 
		QuantityAndListParam theComponentValueQuantity,

		@Description(shortDefinition = "The reason why the expected value in the element Observation.value[x] is missing.")
		@OptionalParam(name = Observation.SP_DATA_ABSENT_REASON) 
		TokenAndListParam theDataAbsentReason,

		@Description(shortDefinition = "The value or component value of the observation, if the value is a Quantity, or a SampledData (just search on the bounds of the values in sampled data)") 
		@OptionalParam(name = Observation.SP_COMBO_VALUE_QUANTITY) 
		QuantityAndListParam theComboValueQuantity,

		@Description(shortDefinition = "Encounter related to the observation")
		@OptionalParam(name = Observation.SP_ENCOUNTER) 
		ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "The classification of the type of observation") 
		@OptionalParam(name = Observation.SP_CATEGORY) 
		TokenAndListParam theCategory,

		@Description(shortDefinition = "The status of the observation")
		@OptionalParam(name = Observation.SP_STATUS) 
		TokenAndListParam theStatus,

		@Description(shortDefinition = "The Device that generated the observation data.")
		@OptionalParam(name = Observation.SP_DEVICE) 
		ReferenceAndListParam theDevice,

		@IncludeParam(allow = { "*" }) Set<Include> theIncludes,

		@Sort SortSpec theSort,

		@Count Integer theCount) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Observation.SP_RES_ID, theId);
		paramMap.add(Observation.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Observation.SP_SUBJECT, theSubject);
		paramMap.add(Observation.SP_VALUE_QUANTITY, theValueQuantity);
		paramMap.add(Observation.SP_BASED_ON, theBasedOn);
		paramMap.add(Observation.SP_HAS_MEMBER, theHasMember);
		paramMap.add(Observation.SP_PERFORMER, thePerformer);
		paramMap.add(Observation.SP_DEVICE, theDevice);
		paramMap.add(Observation.SP_STATUS, theStatus);
		paramMap.add(Observation.SP_PART_OF, thePartOf);
		paramMap.add(Observation.SP_DERIVED_FROM, theDerivedFrom);
		paramMap.add(Observation.SP_CATEGORY, theCategory);
		paramMap.add(Observation.SP_CODE, theCode);
		paramMap.add(Observation.SP_PATIENT, thePatient);

		paramMap.setIncludes(theIncludes);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<Observation> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> observationList = new ArrayList<>();
				for (Observation observation : results) {
					observationList.add(observation);
				}
				return observationList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Observation
     * @param theObservation
     * @return
     */
    @Create
    public MethodOutcome createObservation(@ResourceParam Observation theObservation) {
         
    	// Save this Observation to the database...
    	DafObservation dafObservation = service.createObservation(theObservation);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafObservation.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Observation/1
     * @param theId
     * @param theObservation
     * @return
     */
    @Update
    public MethodOutcome updateObservationById(@IdParam IdType theId, 
    										@ResourceParam Observation theObservation) {
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
		theObservation.setMeta(meta);
    	// Update this Observation to the database...
    	DafObservation dafObservation = service.updateObservationById(id, theObservation);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafObservation.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Observation> getObservationForBulkDataRequest(List<String> patients, Date start, Date end) {

		List<Observation> observationList = service.getObservationForBulkData(patients, start, end);
		return observationList;
	}

}