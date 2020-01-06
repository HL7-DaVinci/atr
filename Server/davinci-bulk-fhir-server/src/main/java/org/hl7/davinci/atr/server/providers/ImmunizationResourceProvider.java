package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafImmunization;
import org.hl7.davinci.atr.server.service.ImmunizationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
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
public class ImmunizationResourceProvider extends AbstractJaxRsResourceProvider<Immunization> {
	
	public static final String RESOURCE_TYPE = "Immunization";
    public static final String VERSION_ID = "1";
    @Autowired
    ImmunizationService service;

    public ImmunizationResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Immunization> getResourceType() {
		return Immunization.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Immunization/1/_history/4
	 * @param theId : Id of the Immunization
	 * @return : Object of Immunization information
	 */
	@Read(version=true)
    public Immunization readOrVread(@IdParam IdType theId) {
		int id;
		Immunization immunization;
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
		   immunization = service.getImmunizationByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       immunization = service.getImmunizationById(id);
		}
		return immunization;
    }

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param thePerformer
	 * @param theReaction
	 * @param theLotNumber 
	 * @param theStatusReason
	 * @param theReasonCode
	 * @param theManufacturer
	 * @param theTargetDisease
	 * @param thePatient
	 * @param theSeries
	 * @param theVaccineCode
	 * @param theReasonReference
	 * @param theLocation
	 * @param theStatus
	 * @param theReactionDate
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Immunization.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "Business identifier")
        @OptionalParam(name = Immunization.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Vaccination  (non)-Administration Date")
        @OptionalParam(name = Immunization.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "The practitioner or organization who played a role in the vaccination")
        @OptionalParam(name = Immunization.SP_PERFORMER)
        ReferenceAndListParam thePerformer,
        
        @Description(shortDefinition = "Additional information on reaction")
        @OptionalParam(name = Immunization.SP_REACTION)
        ReferenceAndListParam theReaction,
        
        @Description(shortDefinition = "Vaccine Lot Number")
        @OptionalParam(name = Immunization.SP_LOT_NUMBER)
        StringAndListParam theLotNumber,
        
        @Description(shortDefinition = "Reason why the vaccine was not administered")
        @OptionalParam(name = Immunization.SP_STATUS_REASON)
        TokenAndListParam theStatusReason,
        
        @Description(shortDefinition = "Reason why the vaccine was administered")
        @OptionalParam(name = Immunization.SP_REASON_CODE)
        TokenAndListParam theReasonCode,
        
        @Description(shortDefinition = "Vaccine Manufacturer")
        @OptionalParam(name = Immunization.SP_MANUFACTURER)
        ReferenceAndListParam theManufacturer,
        
        @Description(shortDefinition = "The target disease the dose is being administered against")
        @OptionalParam(name = Immunization.SP_TARGET_DISEASE)
        TokenAndListParam theTargetDisease,
        
        @Description(shortDefinition = "The patient for the vaccination record")
        @OptionalParam(name = Immunization.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "The series being followed by the provider")
        @OptionalParam(name = Immunization.SP_SERIES)
        StringAndListParam theSeries,
        
        @Description(shortDefinition = "Vaccine Product Administered")
        @OptionalParam(name = Immunization.SP_VACCINE_CODE)
        TokenAndListParam theVaccineCode,
        
        @Description(shortDefinition = "Why immunization occurred")
        @OptionalParam(name = Immunization.SP_REASON_REFERENCE)
        ReferenceAndListParam theReasonReference,
        
        @Description(shortDefinition="The service delivery location or facility in which the vaccine was / was to be administered")
        @OptionalParam(name=Immunization.SP_LOCATION)
        ReferenceAndListParam theLocation,
        
        @Description(shortDefinition = "Immunization event status")
        @OptionalParam(name = Immunization.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Why immunization occurred")
        @OptionalParam(name = Immunization.SP_REACTION_DATE)
        DateRangeParam theReactionDate,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Immunization.SP_RES_ID, theId);
        paramMap.add(Immunization.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Immunization.SP_DATE, theDate);
        paramMap.add(Immunization.SP_PERFORMER, thePerformer);
        paramMap.add(Immunization.SP_REACTION, theReaction);
        paramMap.add(Immunization.SP_LOT_NUMBER, theLotNumber);
        paramMap.add(Immunization.SP_STATUS_REASON, theStatusReason);
        paramMap.add(Immunization.SP_REASON_CODE, theReasonCode);
        paramMap.add(Immunization.SP_MANUFACTURER, theManufacturer);
        paramMap.add(Immunization.SP_TARGET_DISEASE, theTargetDisease);
        paramMap.add(Immunization.SP_PATIENT, thePatient);
        paramMap.add(Immunization.SP_SERIES, theSeries);
        paramMap.add(Immunization.SP_VACCINE_CODE, theVaccineCode);
        paramMap.add(Immunization.SP_REASON_REFERENCE, theReasonReference);
        paramMap.add(Immunization.SP_LOCATION, theLocation);
        paramMap.add(Immunization.SP_STATUS, theStatus);
        paramMap.add(Immunization.SP_REACTION_DATE, theReactionDate);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<Immunization> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> immunizationList = new ArrayList<>();
                for(Immunization theImmunization : results){
                	immunizationList.add(theImmunization);
                }
                return immunizationList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Immunization
     * @param theImmunization
     * @return
     */
    @Create
    public MethodOutcome createImmunization(@ResourceParam Immunization theImmunization) {
         
    	// Save this Immunization to the database...
    	DafImmunization dafImmunization = service.createImmunization(theImmunization);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafImmunization.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Immunization/1
     * @param theId
     * @param theImmunization
     * @return
     */
    @Update
    public MethodOutcome updateImmunizationById(@IdParam IdType theId, 
    										@ResourceParam Immunization theImmunization) {
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
		theImmunization.setMeta(meta);
    	// Update this Immunization to the database...
    	DafImmunization dafImmunization = service.updateImmunizationById(id, theImmunization);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafImmunization.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Immunization> getImmunizationForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<Immunization> immunizationList = service.getImmunizationForBulkData(patients, start, end);
		return immunizationList;
	}
}
