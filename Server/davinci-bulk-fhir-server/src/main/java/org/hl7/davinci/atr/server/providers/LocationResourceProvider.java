package org.hl7.davinci.atr.server.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.SpecialAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.hl7.davinci.atr.server.model.DafLocation;
import org.hl7.davinci.atr.server.service.LocationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class LocationResourceProvider extends AbstractJaxRsResourceProvider<Location> {

	public static final String RESOURCE_TYPE = "Location";
	public static final String VERSION_ID = "1";
	
	@Autowired
	LocationService service;

	public LocationResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Location> getResourceType() {
		return Location.class;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Location
     * @param theLocation
     * @return
     */
    @Create
    public MethodOutcome createLocation(@ResourceParam Location theLocation) {
         
    	// Save this Location to the database...
    	DafLocation dafLocation = service.createLocation(theLocation);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafLocation.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Location/1
     * @param theId
     * @param theLocation
     * @return
     */
    @Update
    public MethodOutcome updateLocationById(@IdParam IdType theId, 
    										@ResourceParam Location theLocation) {
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
		theLocation.setMeta(meta);
    	// Update this Location to the database...
    	DafLocation dafLocation = service.updateLocationById(id, theLocation);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafLocation.getId() + "", VERSION_ID));
		return retVal;
    }

	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Location/1/_history/2
	 * @param theId : Id of the location
	 * @return : Object of location information
	 */
	
	@Read(version = true)
	public Location readOrVread(@IdParam IdType theId) {
		int id;
		Location theLocation;
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
			theLocation = service.getLocationByVersionId(id, theId.getVersionIdPart());

		} else {
			// this is a read
			theLocation = service.getLocationById(id);
		}
		return theLocation;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theAddress
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalCode
	 * @param theAddressCountry 
	 * @param theOrganization
	 * @param thePartOf
	 * @param theOperationalStatus
	 * @param theType
	 * @param theEndpoint
	 * @param theAddressUse
	 * @param theNear
	 * @param theStatus
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,
        
        @Description(shortDefinition="The ID of the resource")
        @OptionalParam(name=Location.SP_RES_ID)
        StringAndListParam theId,
        
		@Description(shortDefinition="An identifier for the location")
        @OptionalParam(name=Location.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition="A portion of the location's name or alias")
        @OptionalParam(name=Location.SP_NAME)
        StringAndListParam theName,
        
		@Description(shortDefinition="A location of which this location is a part")
        @OptionalParam(name=Location.SP_PARTOF)
        ReferenceAndListParam thePartOf,
        
		@Description(shortDefinition="A (part of the) address of the location")
        @OptionalParam(name=Location.SP_ADDRESS)
        StringAndListParam theAddress,
        
		@Description(shortDefinition="A state specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_STATE)
        StringAndListParam theAddressState,
        
		@Description(shortDefinition="Searches for locations (typically bed/room) that have an operational status (e.g. contaminated, housekeeping)")
        @OptionalParam(name=Location.SP_OPERATIONAL_STATUS)
        TokenAndListParam theOperationalStatus,
        
		@Description(shortDefinition="A code for the type of location")
        @OptionalParam(name=Location.SP_TYPE)
        TokenAndListParam theType,
  
		@Description(shortDefinition="A postal code specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_POSTALCODE)
        StringAndListParam theAddressPostalCode,

		@Description(shortDefinition="A country specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_COUNTRY)
        StringAndListParam theAddressCountry,
        
        @Description(shortDefinition="Technical endpoints providing access to services operated for the location")
        @OptionalParam(name=Location.SP_ENDPOINT)
        ReferenceAndListParam theEndpoint,
        
        @Description(shortDefinition="Searches for locations that are managed by the provided organization")
        @OptionalParam(name=Location.SP_ORGANIZATION)
        ReferenceAndListParam theOrganization,
        
        @Description(shortDefinition="A use code specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_USE)
        TokenAndListParam theAddressUse,
        
        @Description(shortDefinition="Search for locations where the location.position is near to, or within a specified distance of, the provided coordinates expressed as [latitude]|[longitude]|[distance]|[units] (using the WGS84 datum, see notes).\r\n" + 
        		"If the units are omitted, then kms should be assumed. If the distance is omitted, then the server can use its own discression as to what distances should be considered near (and units are irrelevant)\r\n" + 
        		"\r\n" + 
        		"Servers may search using various techniques that might have differing accuracies, depending on implementation efficiency.\r\n" + 
        		"\r\n" + 
        		"Requires the near-distance parameter to be provided also")
        @OptionalParam(name=Location.SP_NEAR)
        SpecialAndListParam theNear,
   
        @Description(shortDefinition="A city specified in an address")
        @OptionalParam(name=Location.SP_ADDRESS_CITY)
        StringAndListParam theAddressCity,
    
        @Description(shortDefinition="Searches for locations with a specific kind of status")
        @OptionalParam(name=Location.SP_STATUS)
        TokenAndListParam theStatus,
        
        @IncludeParam(allow = { "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		SearchParameterMap searchParameterMap = new  SearchParameterMap();
		searchParameterMap.add(Location.SP_RES_ID,theId );
		searchParameterMap.add(Location.SP_IDENTIFIER,theIdentifier );
		searchParameterMap.add(Location.SP_NAME,theName );
		searchParameterMap.add(Location.SP_ADDRESS,theAddress );
		searchParameterMap.add(Location.SP_ADDRESS_CITY,theAddressCity );
		searchParameterMap.add(Location.SP_ADDRESS_COUNTRY,theAddressCountry );
		searchParameterMap.add(Location.SP_ADDRESS_POSTALCODE,theAddressPostalCode );
		searchParameterMap.add(Location.SP_ADDRESS_STATE,theAddressState );
		searchParameterMap.add(Location.SP_ADDRESS_USE,theAddressUse );
		searchParameterMap.add(Location.SP_ENDPOINT,theEndpoint );
		searchParameterMap.add(Location.SP_NEAR,theNear );
		searchParameterMap.add(Location.SP_ORGANIZATION,theOrganization );
		searchParameterMap.add(Location.SP_STATUS,theStatus );
		searchParameterMap.add(Location.SP_PARTOF,thePartOf);
		searchParameterMap.add(Location.SP_TYPE,theType );
		searchParameterMap.add(Location.SP_OPERATIONAL_STATUS,theOperationalStatus );
		searchParameterMap.setIncludes(theIncludes);
		searchParameterMap.setSort(theSort);
		searchParameterMap.setCount(theCount);
		
		final List<Location> results = service.search(searchParameterMap);
		 return new IBundleProvider() {
             final InstantDt published = InstantDt.withCurrentTime();
             @Override
             public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                 List<IBaseResource> locationList = new ArrayList<IBaseResource>();
                 for(Location location : results){
                	 locationList.add(location);
                 }
                 return locationList;
             }
			@Override
			public InstantDt getPublished() {
				return published;
			}
			@Override
			public String getUuid() {
				return null;
			}
			@Override
			public Integer preferredPageSize() {
				return null;
			}
			@Override
			public Integer size() {
				return results.size();
			}
		 
		 };
	}
	
	public List<Location> getLocationForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<Location> locationList = service.getLocationForBulkData(patients, start, end);
		return locationList;
	}
}
		

        

