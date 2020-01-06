package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.service.PractitionerService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Practitioner;
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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class PractitionerResourceProvider extends AbstractJaxRsResourceProvider<Practitioner> {
	
	public static final String RESOURCE_TYPE = "Practitioner";
    public static final String VERSION_ID = "1";
    
    @Autowired
    PractitionerService service;
    
    public PractitionerResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
	@Override
	public Class<Practitioner> getResourceType() {
		return Practitioner.class;
	}
	
	/**
	 * The "@Read" annotation indicate
	 * s that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Practitioner/1/_history/4
	 * 
	 * @param theId: ID of practitioner
	 * @return : Practitioner object
	 */
	@Read(version=true)
    public Practitioner readOrVread(@IdParam IdType theId) {
		int id;
		Practitioner thePractitioner;
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
			thePractitioner = service.getPractitionerByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			thePractitioner = service.getPractitionerById(id);
		}
		return thePractitioner;
    }

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theFamily
	 * @param theGiven
	 * @param theOrganization
	 * @param theTelecom
	 * @param theAddress
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalcode
	 * @param theAddressCountry
	 * @param theGender
	 * @param theLanguage
	 * @param theBirthdate
	 * @param theActive
	 * @param theLink
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Practitioner.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A Practitioner identifier")
        @OptionalParam(name = Practitioner.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "A portion of either family or given name of the Practitioner")
        @OptionalParam(name = Practitioner.SP_NAME)
        StringAndListParam theName,

        @Description(shortDefinition = "A portion of the family name")
        @OptionalParam(name = Practitioner.SP_FAMILY)
        StringAndListParam theFamily,

        @Description(shortDefinition = "A portion of the given name of the Practitioner")
        @OptionalParam(name = Practitioner.SP_GIVEN)
        StringAndListParam theGiven,

        @Description(shortDefinition = "One of the languages that the practitioner can communicate with")
        @OptionalParam(name = Practitioner.SP_COMMUNICATION)
        TokenAndListParam theCommunication,

        @Description(shortDefinition = "The value in any kind of telecom details of the Practitioner")
        @OptionalParam(name = Practitioner.SP_TELECOM)
        StringAndListParam theTelecom,

        @Description(shortDefinition = "An address in any kind of address/part of the Practitioner")
        @OptionalParam(name = Practitioner.SP_ADDRESS)
        StringAndListParam theAddress,
        
        @Description(shortDefinition="A city specified in an address")
		@OptionalParam(name = Practitioner.SP_ADDRESS_CITY)
		StringAndListParam theAddressCity, 
  
		@Description(shortDefinition="A state specified in an address")
		@OptionalParam(name = Practitioner.SP_ADDRESS_STATE)
		StringAndListParam theAddressState, 
  
		@Description(shortDefinition="A postalCode specified in an address")
		@OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE)
		StringAndListParam theAddressPostalcode, 
  
		@Description(shortDefinition="A country specified in an address")
		@OptionalParam(name = Practitioner.SP_ADDRESS_COUNTRY)
		StringAndListParam theAddressCountry, 

        @Description(shortDefinition = "Gender of the Practitioner")
        @OptionalParam(name = Practitioner.SP_GENDER)
        TokenAndListParam theGender,

        @Description(shortDefinition = "A value in an email contact.Practitioner.telecom.where(system='email')")
        @OptionalParam(name = Practitioner.SP_EMAIL)
        StringAndListParam theEmail,

        @Description(shortDefinition = "A use code specified in an address.Practitioner.address.use")
        @OptionalParam(name = Practitioner.SP_ADDRESS_USE)
        TokenAndListParam theAddressUse,

        @Description(shortDefinition = "Whether the Practitioner record is active")
        @OptionalParam(name = Practitioner.SP_ACTIVE)
        TokenAndListParam theActive,

        @Description(shortDefinition = "A value in a phone contact. Path: Practitioner.telecom(system=phone)")
        @OptionalParam(name = Practitioner.SP_PHONE)
        StringAndListParam thePhone,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(Practitioner.SP_RES_ID, theId);
            paramMap.add(Practitioner.SP_IDENTIFIER, theIdentifier);
            paramMap.add(Practitioner.SP_NAME, theName);
            paramMap.add(Practitioner.SP_FAMILY, theFamily);
            paramMap.add(Practitioner.SP_GIVEN, theGiven);
            paramMap.add(Practitioner.SP_COMMUNICATION, theCommunication);
            paramMap.add(Practitioner.SP_TELECOM, theTelecom);
            paramMap.add(Practitioner.SP_ADDRESS, theAddress);
            paramMap.add(Practitioner.SP_ADDRESS_CITY, theAddressCity);
            paramMap.add(Practitioner.SP_ADDRESS_STATE, theAddressState);
            paramMap.add(Practitioner.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
            paramMap.add(Practitioner.SP_ADDRESS_COUNTRY, theAddressCountry);
            paramMap.add(Practitioner.SP_GENDER, theGender);
            paramMap.add(Practitioner.SP_EMAIL, theEmail);
            paramMap.add(Practitioner.SP_ADDRESS_USE, theAddressUse);
            paramMap.add(Practitioner.SP_ACTIVE, theActive);
            paramMap.add(Practitioner.SP_PHONE, thePhone);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<Practitioner> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> practitionerList = new ArrayList<>();
                    for(Practitioner practitioner : results){
                        practitionerList.add(practitioner);
                    }
                    return practitionerList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Practitioner
     * @param thePractitioner
     * @return
     */
    @Create
    public MethodOutcome createPractitioner(@ResourceParam Practitioner thePractitioner) {
         
    	// Save this Practitioner to the database...
    	DafPractitioner dafPractitioner = service.createPractitioner(thePractitioner);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafPractitioner.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Practitioner/1
     * @param theId
     * @param thePractitioner
     * @return
     */
    @Update
    public MethodOutcome updatePractitionerById(@IdParam IdType theId, 
    										@ResourceParam Practitioner thePractitioner) {
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
		thePractitioner.setMeta(meta);
    	// Update this Practitioner to the database...
    	DafPractitioner dafPractitioner = service.updatePractitionerById(id, thePractitioner);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafPractitioner.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Practitioner> getPractitionerForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<Practitioner> docPracList = service.getPractitionerForBulkData(patients, start, end);
		return docPracList;
	}
}
