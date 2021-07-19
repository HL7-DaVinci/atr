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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.hl7.davinci.atr.server.model.DafOrganization;
import org.hl7.davinci.atr.server.service.OrganizationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class OrganizationResourceProvider extends AbstractJaxRsResourceProvider<Organization> {
	private static final Logger logger = LoggerFactory.getLogger(OrganizationResourceProvider.class);    

	public static final String RESOURCE_TYPE = "Organization";
	public static final String VERSION_ID = "1";
	
	@Autowired
	OrganizationService service;
	@Autowired
    FhirContext fhirContext;

	public OrganizationResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Organization> getResourceType() {
		return Organization.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Organization/1/_history/4
	 * 
	 * @param theId : Id of the organization
	 * @return : Object of organization information
	 */
	@Read(version = true)
	public Organization readOrVread(@IdParam IdType theId) {
		String id;
		Organization organization;
		try {
			id = theId.getIdPart();
		} catch (Exception e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			organization = service.getOrganizationByVersionId(id, theId.getVersionIdPart());

		} else {
			organization = service.getOrganizationById(id);
		}
		return organization;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Organization
     * @param theOrganization
     * @return
     */
    @Create
    public MethodOutcome createOrganization(@ResourceParam Organization theOrganization) {
		MethodOutcome retVal = new MethodOutcome();
    	try {
    		// Save this Organization to the database...
        	Organization organization = service.createOrganization(theOrganization);
    		retVal.setId(new IdType(RESOURCE_TYPE, organization.getIdElement().getIdPart(), organization.getMeta().getVersionId()));    		
    	}
    	catch(Exception e) {
    		logger.error("Exception in createPatient of PatientResourceProvider ", e);
    	}
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Organization/1
     * @param theId
     * @param theOrganization
     * @return
     */
    @Update
    public MethodOutcome updateOrganizationById(@IdParam IdType theId, 
    										@ResourceParam Organization theOrganization) {
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
		theOrganization.setMeta(meta);
    	// Update this Organization to the database...
    	DafOrganization dafOrganization = service.updateOrganizationById(id, theOrganization);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafOrganization.getId() + "", VERSION_ID));
		return retVal;
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
	 * @param thePartOff
	 * @param theAddress
	 * @param theActive
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalcode
	 * @param theAddressCountry
	 * @param theType
	 * @param theEndPoint
	 * @param theUse
	 * @param theName
	 * @param theTelecom
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,
			
		@Description(shortDefinition = "The resource identity")
	    @OptionalParam(name = Organization.SP_RES_ID)
	    StringAndListParam theId,
	
	    @Description(shortDefinition = "An Organization  identifier")
	    @OptionalParam(name = Organization.SP_IDENTIFIER)
	    TokenAndListParam theIdentifier,
	    
	    @Description(shortDefinition = "The organization of which this organization forms a part")
	    @OptionalParam(name = Organization.SP_PARTOF)
	    ReferenceAndListParam thePartOf,
	    
	    @Description(shortDefinition = "Visiting or addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS)
	    StringAndListParam theAddress,
	    
	    @Description(shortDefinition = "Visiting or state addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_STATE)
	    StringAndListParam theAddressState,
	    
	    @Description(shortDefinition = "Whether the organization record is active")
	    @OptionalParam(name = Organization.SP_ACTIVE)
	    TokenAndListParam theActive,
	    
	    @Description(shortDefinition = "The kind(s) of organization that this is.")
	    @OptionalParam(name = Organization.SP_TYPE)
	    TokenAndListParam theType,
	    
	    @Description(shortDefinition = "Visiting or postal addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE)
	    StringAndListParam thePostalCode,
	    
	    @Description(shortDefinition = "Visiting or country addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_COUNTRY)
	    StringAndListParam theAddressCountry,
	    
	    @Description(shortDefinition = "Technical endpoints providing access to services operated for the organization")
	    @OptionalParam(name = Organization.SP_ENDPOINT)
	    ReferenceAndListParam theEndPoint,
	  
	    @Description(shortDefinition = "Visiting or  addresses for the contact")
	    @OptionalParam(name = Organization.SP_ADDRESS_USE)
	    TokenAndListParam theAddressUse,
	    
	    @Description(shortDefinition = "An Organization  name")
	    @OptionalParam(name = Organization.SP_NAME)
	    StringAndListParam theName,
	    
	    @Description(shortDefinition = "A city specified in an address")
	    @OptionalParam(name = Organization.SP_ADDRESS_CITY)
	    StringAndListParam theAddressCity,
	    
	    @Description(shortDefinition = "The value in any kind of telecom details of the organization")
	    @OptionalParam(name = "telecom")
	    StringAndListParam theTelecom,
	    
	    @IncludeParam(allow = {"Organization.managingOrganization", "Organization.link.other", "*"})
	    Set<Include> theIncludes,
	
	    @Sort
	    SortSpec theSort,
	
	    @Count
	    Integer theCount) {

			SearchParameterMap paramMap = new SearchParameterMap();
			paramMap.add(Organization.SP_RES_ID, theId);
			paramMap.add(Organization.SP_IDENTIFIER, theIdentifier);
			paramMap.add(Organization.SP_PARTOF, thePartOf);
			paramMap.add(Organization.SP_ADDRESS, theAddress);
			paramMap.add(Organization.SP_ADDRESS_STATE, theAddressState);
			paramMap.add(Organization.SP_ACTIVE, theActive);
			paramMap.add(Organization.SP_TYPE, theType);
			paramMap.add(Organization.SP_ADDRESS_CITY, theAddressCity);
			paramMap.add(Organization.SP_NAME, theName);
			paramMap.add(Organization.SP_ADDRESS_POSTALCODE, thePostalCode);
			paramMap.add(Organization.SP_ADDRESS_COUNTRY, theAddressCountry);
			paramMap.add(Organization.SP_ENDPOINT, theEndPoint);
			paramMap.add(Organization.SP_ADDRESS_USE, theAddressUse);
			paramMap.add("telecom", theTelecom);
			paramMap.setIncludes(theIncludes);
			paramMap.setSort(theSort);
			paramMap.setCount(theCount);

			final List<Organization> results = service.search(paramMap);

			return new IBundleProvider() {
				final InstantDt published = InstantDt.withCurrentTime();

				@Override
				public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
					List<IBaseResource> organizationList = new ArrayList<>();
					for (Organization organization : results) {
						organizationList.add(organization);
					}
					return organizationList;
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
	
	public List<Organization> getOrganizationForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<Organization> organizationList = service.getOrganizationForBulkData(patients, start, end);
		return organizationList;
	}
}
