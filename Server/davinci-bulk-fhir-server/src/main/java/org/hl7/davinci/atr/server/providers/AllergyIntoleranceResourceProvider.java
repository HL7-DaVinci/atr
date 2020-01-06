package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafAllergyIntolerance;
import org.hl7.davinci.atr.server.service.AllergyIntoleranceService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.IdType;
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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class AllergyIntoleranceResourceProvider extends AbstractJaxRsResourceProvider<AllergyIntolerance> {
	
	public static final String RESOURCE_TYPE = "AllergyIntolerance";
    public static final String VERSION_ID = "1";
    
	@Autowired
    private AllergyIntoleranceService service;

    public AllergyIntoleranceResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
	@Override
	public Class<AllergyIntolerance> getResourceType() {
		return AllergyIntolerance.class;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/AllergyIntolerance
     * @param theAllergyIntolerance
     * @return
     */
    @Create
    public MethodOutcome createAllergyIntolerance(@ResourceParam AllergyIntolerance theAllergyIntolerance) {
         
    	// Save this AllergyIntolerance to the database...
    	DafAllergyIntolerance dafAllergyIntolerance = service.createAllergyIntolerance(theAllergyIntolerance);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafAllergyIntolerance.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/AllergyIntolerance/1
     * @param theId
     * @param theAllergyIntolerance
     * @return
     */
    @Update
    public MethodOutcome updateAllergyIntoleranceById(@IdParam IdType theId, 
    										@ResourceParam AllergyIntolerance theAllergyIntolerance) {
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
		theAllergyIntolerance.setMeta(meta);
    	// Update this Patient to the database...
    	DafAllergyIntolerance dafAllergyIntolerance = service.updateAllergyIntoleranceById(id, theAllergyIntolerance);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafAllergyIntolerance.getId() + "", VERSION_ID));
		return retVal;
    }
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/AllergyIntolerance/1/_history/4
	 * @param theId : Id of the AllergyIntolerance
	 * @return : Object of AllergyIntolerance information
	 */
	@Read(version=true)
    public AllergyIntolerance readOrVread(@IdParam IdType theId) {
		int id;
		AllergyIntolerance allergyIntolerance;
		try {
		    id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
		   // this is a vread  
			allergyIntolerance = service.getAllergyIntoleranceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			allergyIntolerance = service.getAllergyIntoleranceById(id);
		}
		return allergyIntolerance;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theSeverity
	 * @param theManifestation
	 * @param theRecorder
	 * @param theCode
	 * @param theVerificationStatus
	 * @param theCriticality
	 * @param theClinicalStatus
	 * @param theType
	 * @param theOnset
	 * @param theRoute
	 * @param theAsserter
	 * @param thePatient
	 * @param theCategory
	 * @param theLastDate
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */

    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = AllergyIntolerance.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A AllergyIntolerance identifier")
        @OptionalParam(name = AllergyIntolerance.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "mild | moderate | severe (of event as a whole)")
        @OptionalParam(name = AllergyIntolerance.SP_SEVERITY)
        TokenAndListParam theSeverity,

        @Description(shortDefinition = "Date first version of the resource instance was recorded")
        @OptionalParam(name = AllergyIntolerance.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Clinical symptoms/signs associated with the Event")
        @OptionalParam(name = AllergyIntolerance.SP_MANIFESTATION)
        TokenAndListParam theManifestation,
        
        @Description(shortDefinition = "Who recorded the sensitivity")
        @OptionalParam(name = AllergyIntolerance.SP_RECORDER)
        ReferenceAndListParam theRecorder,
        
        @Description(shortDefinition = "Code that identifies the allergy or intolerance")
        @OptionalParam(name = AllergyIntolerance.SP_CODE)
        TokenAndListParam theCode,
        
        @Description(shortDefinition = "unconfirmed | confirmed | refuted | entered-in-error")
        @OptionalParam(name = AllergyIntolerance.SP_VERIFICATION_STATUS)
        TokenAndListParam theVerificationStatus,
        
        @Description(shortDefinition = "low | high | unable-to-assess")
        @OptionalParam(name = AllergyIntolerance.SP_CRITICALITY)
        TokenAndListParam theCriticality,
        
        @Description(shortDefinition = "active | inactive | resolved")
        @OptionalParam(name = AllergyIntolerance.SP_CLINICAL_STATUS)
        TokenAndListParam theClinicalStatus,
        
        @Description(shortDefinition = "allergy | intolerance - Underlying mechanism (if known)")
        @OptionalParam(name = AllergyIntolerance.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "Date(/time) when manifestations showed")
        @OptionalParam(name = AllergyIntolerance.SP_ONSET)
        DateRangeParam theOnset,
        
        @Description(shortDefinition = "How the subject was exposed to the substance")
        @OptionalParam(name = AllergyIntolerance.SP_ROUTE)
        TokenAndListParam theRoute,
        
        @Description(shortDefinition = "Source of the information about the allergy")
        @OptionalParam(name = AllergyIntolerance.SP_ASSERTER)
        ReferenceAndListParam theAsserter,
        
        @Description(shortDefinition = "Who the sensitivity is for")
        @OptionalParam(name = AllergyIntolerance.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "food | medication | environment | biologic")
        @OptionalParam(name = AllergyIntolerance.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "Date(/time) of last known occurrence of a reaction")
        @OptionalParam(name = AllergyIntolerance.SP_LAST_DATE)
        DateRangeParam theLastDate,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(AllergyIntolerance.SP_RES_ID, theId);
        paramMap.add(AllergyIntolerance.SP_IDENTIFIER, theIdentifier);
        paramMap.add(AllergyIntolerance.SP_SEVERITY, theSeverity);
        paramMap.add(AllergyIntolerance.SP_DATE, theDate);
        paramMap.add(AllergyIntolerance.SP_MANIFESTATION, theManifestation);
        paramMap.add(AllergyIntolerance.SP_RECORDER, theRecorder);
        paramMap.add(AllergyIntolerance.SP_CODE, theCode);
        paramMap.add(AllergyIntolerance.SP_VERIFICATION_STATUS, theVerificationStatus);
        paramMap.add(AllergyIntolerance.SP_CRITICALITY, theCriticality);
        paramMap.add(AllergyIntolerance.SP_CLINICAL_STATUS, theClinicalStatus);
        paramMap.add(AllergyIntolerance.SP_TYPE, theType);
        paramMap.add(AllergyIntolerance.SP_ONSET, theOnset);
        paramMap.add(AllergyIntolerance.SP_ROUTE, theRoute);
        paramMap.add(AllergyIntolerance.SP_ASSERTER, theAsserter);
        paramMap.add(AllergyIntolerance.SP_PATIENT, thePatient);
        paramMap.add(AllergyIntolerance.SP_CATEGORY, theCategory);
        paramMap.add(AllergyIntolerance.SP_LAST_DATE, theLastDate);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<AllergyIntolerance> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> allergyIntoleranceList = new ArrayList<IBaseResource>();
                for(AllergyIntolerance allergyIntolerance : results){
                	allergyIntoleranceList.add(allergyIntolerance);
                }
                return allergyIntoleranceList;
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
     * This method is implemented to get AllergyIntolerances for Bulk data request
     * @param patients
     * @param start
     * @return This method returns a list of AllergyIntolerance. This list may contain multiple matching resources, or it may also be empty.
     */
	public List<AllergyIntolerance> getAllergyIntoleranceForBulkDataRequest(List<String> patients, Date start, Date end) {
		System.out.println("===========******IN ALLERGY PROVIDERS START******================");

		List<AllergyIntolerance> allergyIntoleranceList = service.getAllergyIntoleranceForBulkData(patients, start, end);
		return allergyIntoleranceList;
	}
}
