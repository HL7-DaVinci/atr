package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafMedicationRequest;
import org.hl7.davinci.atr.server.service.MedicationRequestService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
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
public class MedicationRequestResourceProvider extends AbstractJaxRsResourceProvider<MedicationRequest> {

	public static final String RESOURCE_TYPE = "MedicationRequest";
    public static final String VERSION_ID = "1";
    @Autowired
    MedicationRequestService service;

    public MedicationRequestResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<MedicationRequest> getResourceType() {
		return MedicationRequest.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationRequest/1/_history/4
	 * @param theId : Id of the MedicationRequest
	 * @return : Object of MedicationRequest information
	 */
	@Read(version=true)
    public MedicationRequest readOrVread(@IdParam IdType theId) {
		int id;
		
		MedicationRequest medicationRequest;
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
		   medicationRequest = service.getMedicationRequestByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       medicationRequest = service.getMedicationRequestById(id);
		}
		return medicationRequest;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theSubject
	 * @param theStatus
	 * @param theIntent
	 * @param theCategory
	 * @param theContext
	 * @param thePriority
	 * @param thePerformer
	 * @param thePerformerType
	 * @param theAuthoredOn
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = MedicationRequest.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A medicationRequest identifier")
        @OptionalParam(name = MedicationRequest.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "The identity of a patient to list orders  for")
        @OptionalParam(name = MedicationRequest.SP_SUBJECT)
        StringAndListParam theSubject,

        @Description(shortDefinition = "Status of the prescription")
        @OptionalParam(name = MedicationRequest.SP_STATUS)
        StringAndListParam theStatus,

        @Description(shortDefinition = "Returns prescriptions with different intents")
        @OptionalParam(name = MedicationRequest.SP_INTENT)
        TokenAndListParam theIntent,

        @Description(shortDefinition = "Returns prescriptions with different categories")
        @OptionalParam(name = MedicationRequest.SP_CATEGORY)
        StringAndListParam theCategory,

        @Description(shortDefinition = "Return prescriptions with this encounter or episode of care identifier")
        @OptionalParam(name = MedicationRequest.SP_ENCOUNTER)
        StringAndListParam theContext,

        @Description(shortDefinition = "Returns prescriptions with different priorities")
        @OptionalParam(name = MedicationRequest.SP_PRIORITY)
        TokenAndListParam thePriority,
        
        @Description(shortDefinition="Returns prescriptions prescribed by this prescriber")
		@OptionalParam(name = MedicationRequest.SP_REQUESTER)
		ReferenceAndListParam theRequester, 
  
		@Description(shortDefinition="Returns requests for a specific type of performer")
		@OptionalParam(name = MedicationRequest.SP_INTENDED_PERFORMERTYPE)
        TokenAndListParam thePerformerType, 
  
		@Description(shortDefinition="Returns requests for a specific type of performer")
		@OptionalParam(name = MedicationRequest.SP_INTENDED_PERFORMER)
        ReferenceAndListParam thePerformer, 

        @Description(shortDefinition = "Return prescriptions written on this date")
        @OptionalParam(name = MedicationRequest.SP_AUTHOREDON)
        DateAndListParam theAuthoredOn,
        
        @Description(shortDefinition="Returns prescriptions for a specific patient")
        @OptionalParam(name = MedicationRequest.SP_PATIENT)
        ReferenceAndListParam thePatient,
        

        @IncludeParam(allow = {"", "", "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(MedicationRequest.SP_RES_ID, theId);
            paramMap.add(MedicationRequest.SP_IDENTIFIER, theIdentifier);
            paramMap.add(MedicationRequest.SP_SUBJECT, theSubject);
            paramMap.add(MedicationRequest.SP_STATUS, theStatus);
            paramMap.add(MedicationRequest.SP_INTENT, theIntent);
            paramMap.add(MedicationRequest.SP_CATEGORY, theCategory);
            paramMap.add(MedicationRequest.SP_ENCOUNTER, theContext);
            paramMap.add(MedicationRequest.SP_PRIORITY, thePriority);
            paramMap.add(MedicationRequest.SP_REQUESTER, theRequester);
            paramMap.add(MedicationRequest.SP_INTENDED_PERFORMERTYPE, thePerformerType);
            paramMap.add(MedicationRequest.SP_INTENDED_PERFORMER, thePerformer);
            paramMap.add(MedicationRequest.SP_AUTHOREDON, theAuthoredOn);
            paramMap.add(MedicationRequest.SP_PATIENT, thePatient);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<MedicationRequest> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> medicationRequestList = new ArrayList<IBaseResource>();
                    for(MedicationRequest theMedicationRequest : results){
                    	medicationRequestList.add(theMedicationRequest);
                    }
                    return medicationRequestList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/MedicationRequest
     * @param theMedicationRequest
     * @return
     */
    @Create
    public MethodOutcome createMedicationRequest(@ResourceParam MedicationRequest theMedicationRequest) {
         
    	// Save this MedicationRequest to the database...
    	DafMedicationRequest dafMedicationRequest = service.createMedicationRequest(theMedicationRequest);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationRequest.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/MedicationRequest/1
     * @param theId
     * @param theMedicationRequest
     * @return
     */
    @Update
    public MethodOutcome updateMedicationRequestById(@IdParam IdType theId, 
    										@ResourceParam MedicationRequest theMedicationRequest) {
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
		theMedicationRequest.setMeta(meta);
    	// Update this MedicationRequest to the database...
    	DafMedicationRequest dafMedicationRequest = service.updateMedicationRequestById(id, theMedicationRequest);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationRequest.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<MedicationRequest> getMedicationRequestForBulkDataRequest(List<String> patients, Date start, Date end) {

  		List<MedicationRequest> medRequestList = service.getMedicationRequestForBulkData(patients, start, end);
  		return medRequestList;
  	}
}
