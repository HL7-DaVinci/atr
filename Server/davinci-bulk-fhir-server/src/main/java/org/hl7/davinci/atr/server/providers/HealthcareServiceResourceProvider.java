package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafHealthcareService;
import org.hl7.davinci.atr.server.service.HealthcareServiceService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.HealthcareService;
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
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class HealthcareServiceResourceProvider extends AbstractJaxRsResourceProvider<HealthcareService> {
	
	public static final String RESOURCE_TYPE = "HealthcareService";
    public static final String VERSION_ID = "1";
    @Autowired
    HealthcareServiceService service;
    
    public HealthcareServiceResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<HealthcareService> getResourceType() {
		return HealthcareService.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/HealthcareService/1/_history/3.0
	 * @param theId : Id of the HealthcareService
	 * @return : Object of HealthcareService information
	 */
	@Read(version=true)
    public HealthcareService readOrVread(@IdParam IdType theId) {
		int id;
		HealthcareService healthcareService;
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
			healthcareService = service.getHealthcareServiceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			healthcareService = service.getHealthcareServiceById(id);
		}
		return healthcareService;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theEndpoint
	 * @param theProgramName
	 * @param theOrganization
	 * @param theTelecom
	 * @param theLocation
	 * @param theCategory
	 * @param theType
	 * @param theCharacteristic
	 * @param theActive
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
    		javax.servlet.http.HttpServletRequest theServletRequest,

            @Description(shortDefinition = "The resource identity")
            @OptionalParam(name = HealthcareService.SP_RES_ID)
            TokenAndListParam theId,

            @Description(shortDefinition = "External identifiers for this item")
            @OptionalParam(name = HealthcareService.SP_IDENTIFIER)
            TokenAndListParam theIdentifier,

            @Description(shortDefinition = "A portion of the Healthcare service name")
            @OptionalParam(name = HealthcareService.SP_NAME)
            StringAndListParam theName,

            @Description(shortDefinition = "Technical endpoints providing access to services operated for the location")
            @OptionalParam(name = HealthcareService.SP_ENDPOINT)
            ReferenceAndListParam theEndpoint,

            @Description(shortDefinition = "The organization that provides this Healthcare Service")
            @OptionalParam(name = HealthcareService.SP_ORGANIZATION)
            ReferenceAndListParam theOrganization,

            @Description(shortDefinition = "Contacts related to the healthcare service")
            @OptionalParam(name = "telecom")
            StringAndListParam theTelecom,

            @Description(shortDefinition = "The location of the Healthcare Service")
            @OptionalParam(name = HealthcareService.SP_LOCATION)
            ReferenceAndListParam theLocation,
            
            @Description(shortDefinition="Service Category of the Healthcare Service")
			@OptionalParam(name = HealthcareService.SP_SERVICE_CATEGORY)
			TokenAndListParam theCategory, 

  			@Description(shortDefinition="One of the HealthcareService's characteristics")
			@OptionalParam(name = HealthcareService.SP_CHARACTERISTIC)
            TokenAndListParam theCharacteristic, 

            @Description(shortDefinition = "The Healthcare Service is currently marked as active")
            @OptionalParam(name = HealthcareService.SP_ACTIVE)
            TokenAndListParam theActive,
            
            @Description(shortDefinition = "The type of service provided by this healthcare service")
            @OptionalParam(name = HealthcareService.SP_SERVICE_TYPE)
            TokenAndListParam theServiceType,
            
            @Description(shortDefinition = "Location(s) service is intended for/available to")
            @OptionalParam(name = HealthcareService.SP_COVERAGE_AREA)
            ReferenceAndListParam theCoverageArea,
            
            @Description(shortDefinition = "The specialty of the service provided by this healthcare service")
            @OptionalParam(name = HealthcareService.SP_SPECIALTY)
            TokenAndListParam theSpecialty,
 
            @IncludeParam(allow = {"*"})
            Set<Include> theIncludes,

            @Sort
            SortSpec theSort,

            @Count
            Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(HealthcareService.SP_RES_ID, theId);
            paramMap.add(HealthcareService.SP_IDENTIFIER, theIdentifier);
            paramMap.add(HealthcareService.SP_NAME, theName);
            paramMap.add(HealthcareService.SP_ORGANIZATION, theOrganization);
            paramMap.add(HealthcareService.SP_ENDPOINT, theEndpoint);
            paramMap.add("telecom", theTelecom);
            paramMap.add(HealthcareService.SP_LOCATION, theLocation);
            paramMap.add(HealthcareService.SP_CHARACTERISTIC, theCharacteristic);
            paramMap.add(HealthcareService.SP_ACTIVE, theActive);
            paramMap.add(HealthcareService.SP_SERVICE_TYPE, theServiceType);
            paramMap.add(HealthcareService.SP_SERVICE_CATEGORY, theCategory);
            paramMap.add(HealthcareService.SP_COVERAGE_AREA, theCoverageArea);
            paramMap.add(HealthcareService.SP_SPECIALTY, theSpecialty);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<HealthcareService> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> healthcareServiceList = new ArrayList<IBaseResource>();
                    for(HealthcareService healthcareService : results){
                    	healthcareServiceList.add(healthcareService);
                    }
                    return healthcareServiceList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/HealthcareService
     * @param theHealthcareService
     * @return
     */
    @Create
    public MethodOutcome createHealthcareService(@ResourceParam HealthcareService theHealthcareService) {
         
    	// Save this HealthcareService to the database...
    	DafHealthcareService dafHealthcareService = service.createHealthcareService(theHealthcareService);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafHealthcareService.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/HealthcareService/1
     * @param theId
     * @param theHealthcareService
     * @return
     */
    @Update
    public MethodOutcome updateHealthcareServiceById(@IdParam IdType theId, 
    										@ResourceParam HealthcareService theHealthcareService) {
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
		theHealthcareService.setMeta(meta);
    	// Update this HealthcareService to the database...
    	DafHealthcareService dafHealthcareService = service.updateHealthcareServiceById(id, theHealthcareService);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafHealthcareService.getId() + "", VERSION_ID));
		return retVal;
    }
}