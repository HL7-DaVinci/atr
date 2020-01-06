 package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCondition;
import org.hl7.davinci.atr.server.service.ConditionService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
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
public class ConditionResourceProvider extends AbstractJaxRsResourceProvider<Condition> {

	public static final String RESOURCE_TYPE = "Condition";
	public static final String VERSION_ID = "1";
	
	@Autowired
	ConditionService service;

	public ConditionResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Condition> getResourceType() {
		return Condition.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Condition/1/_history/4
	 * 
	 * @param theId : Id of the Condition
	 * @return : Object of Condition information
	 */
	@Read(version = true)
	public Condition readOrVread(@IdParam IdType theId) {
		int id;
		Condition condition;
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
			condition = service.getConditionByVersionId(id, theId.getVersionIdPart());

		} else {

			condition = service.getConditionById(id);
		}

		return condition;
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
	 * @param theClinicalStatus
	 * @param theVerificationStatus
	 * @param theCategory
	 * @param theSevirity
	 * @param theCode
	 * @param theBodySite
	 * @param theSubject
	 * @param theEncounter
	 * @param theOnsetAge
	 * @param theOnsetDate
	 * @param theAbatementAge
	 * @param theAbatementDate
	 * @param theAbatementString
	 * @param theRecordedDate
	 * @param theAsseter
	 * @param theOnsetAge
	 * @param theStage
	 * @param theEvidence
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
		@OptionalParam(name = Condition.SP_RES_ID) 
		StringAndListParam theId,
		
		@Description(shortDefinition = "An Condition identifier")
		@OptionalParam(name = Condition.SP_IDENTIFIER)
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "The clinical status of the condition") 
		@OptionalParam(name = Condition.SP_CLINICAL_STATUS) 
		TokenAndListParam theClinicalStatus,

		@Description(shortDefinition = "The verification status to support the clinical status of the condition") 
		@OptionalParam(name = Condition.SP_VERIFICATION_STATUS) 
		TokenAndListParam theVerificationStatus,

		@Description(shortDefinition = "A category assigned to the condition") 
		@OptionalParam(name = Condition.SP_CATEGORY) 
		TokenAndListParam theCategory,

		@Description(shortDefinition = "A subjective assessment of the severity of the condition as evaluated by the clinician") 
		@OptionalParam(name = Condition.SP_SEVERITY) 
		TokenAndListParam theSeverity,

		@Description(shortDefinition = "Identification of the condition, problem or diagnosis") 
		@OptionalParam(name = Condition.SP_CODE)
		TokenAndListParam theCode,

		@Description(shortDefinition = "The anatomical location where this condition manifests itself")
		@OptionalParam(name = Condition.SP_BODY_SITE) 
		TokenAndListParam theBodySite,

		@Description(shortDefinition = "Indicates the patient or group who the condition record is associated with")
		@OptionalParam(name = Condition.SP_SUBJECT)
		ReferenceAndListParam theSubject,
		

		@Description(shortDefinition = "Indicates the patient or group who the condition record is associated with")
		@OptionalParam(name = Condition.SP_PATIENT)
		ReferenceAndListParam thePatient,

		@Description(shortDefinition = "The Encounter during which this Condition was created")
		@OptionalParam(name = Condition.SP_ENCOUNTER)
		ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "Age is generally used when the patient reports an age at which the Condition began to occur")
		@OptionalParam(name = Condition.SP_ONSET_AGE)
		QuantityAndListParam theOnsetAge,

		@Description(shortDefinition = "Estimated or actual date or date-time the condition began")
		@OptionalParam(name = Condition.SP_ONSET_DATE) 
		DateRangeParam theOnsetDate,

		@Description(shortDefinition = "The age or estimated age that the condition resolved or went into remission")
		@OptionalParam(name = Condition.SP_ABATEMENT_AGE)
		QuantityAndListParam theAbatementAge,

		@Description(shortDefinition = "The date or estimated date that the condition resolved or went into remission.") 
		@OptionalParam(name = Condition.SP_ABATEMENT_DATE)
		DateRangeParam theAbatementDate,

		@Description(shortDefinition = " When abatementString exists, it implies the condition is abated.")
		@OptionalParam(name = Condition.SP_ABATEMENT_STRING)
		StringAndListParam theAbatementString,

		@Description(shortDefinition = "The recordedDate represents when this particular Condition record was created in the system") 
		@OptionalParam(name = Condition.SP_RECORDED_DATE) 
		DateRangeParam theRecordedDate,

		@Description(shortDefinition = "Individual who is making the condition statement") 
		@OptionalParam(name = Condition.SP_ASSERTER) 
		ReferenceAndListParam theAsserter,

		@Description(shortDefinition = "Clinical stage or grade of a condition. May include formal severity assessments") 
		@OptionalParam(name = Condition.SP_STAGE) 
		TokenAndListParam theStage,

		@Description(shortDefinition = "Supporting evidence / manifestations that are the basis of the Condition's verification status") 
		@OptionalParam(name = Condition.SP_EVIDENCE) 
		TokenAndListParam theEvidence,

		@Sort SortSpec theSort,
		@Count Integer theCount) {
		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Condition.SP_RES_ID, theId);
		paramMap.add(Condition.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Condition.SP_CLINICAL_STATUS, theClinicalStatus);
		paramMap.add(Condition.SP_VERIFICATION_STATUS, theVerificationStatus);
		paramMap.add(Condition.SP_CATEGORY, theCategory);
		paramMap.add(Condition.SP_SEVERITY, theSeverity);
		paramMap.add(Condition.SP_CODE, theCode);
		paramMap.add(Condition.SP_BODY_SITE, theBodySite);
		paramMap.add(Condition.SP_SUBJECT, theSubject);
		paramMap.add(Condition.SP_PATIENT, thePatient);
		paramMap.add(Condition.SP_ENCOUNTER, theEncounter);
		paramMap.add(Condition.SP_ONSET_AGE, theOnsetAge);
		paramMap.add(Condition.SP_ONSET_DATE, theOnsetDate);
		paramMap.add(Condition.SP_ABATEMENT_AGE, theAbatementAge);
		paramMap.add(Condition.SP_ABATEMENT_DATE, theAbatementDate);
		paramMap.add(Condition.SP_ABATEMENT_STRING, theAbatementString);
		paramMap.add(Condition.SP_RECORDED_DATE, theRecordedDate);
		paramMap.add(Condition.SP_ASSERTER, theAsserter);
		paramMap.add(Condition.SP_STAGE, theStage);
		paramMap.add(Condition.SP_EVIDENCE, theEvidence);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<Condition> results = service.search(paramMap);
		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> conditionList = new ArrayList<>();
				for (Condition theCondition : results) {
					conditionList.add(theCondition);
				}
				return conditionList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Condition
     * @param theCondition
     * @return
     */
    @Create
    public MethodOutcome createCondition(@ResourceParam Condition theCondition) {
         
    	// Save this Condition to the database...
    	DafCondition dafCondition = service.createCondition(theCondition);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafCondition.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Condition/1
     * @param theId
     * @param theCondition
     * @return
     */
    @Update
    public MethodOutcome updateConditionById(@IdParam IdType theId, 
    										@ResourceParam Condition theCondition) {
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
		theCondition.setMeta(meta);
    	// Update this Condition to the database...
    	DafCondition dafCondition = service.updateConditionById(id, theCondition);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafCondition.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Condition> getConditionForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<Condition> conditionList = service.getConditionForBulkData(patients, start, end);
		return conditionList;
	}
}
