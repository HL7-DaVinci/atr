package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafEncounter;
import org.hl7.davinci.atr.server.service.EncounterService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Goal;
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
public class EncounterResourceProvider extends AbstractJaxRsResourceProvider<Encounter> {
	
	public static final String RESOURCE_TYPE = "Encounter";
    public static final String VERSION_ID = "1";
    
    @Autowired
    EncounterService service;

    public EncounterResourceProvider(FhirContext fhirContext) {
       super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Encounter> getResourceType() {
		return Encounter.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Encounter/1/_history/4
	 * @param theId : Id of the Encounter
	 * @return : Object of Encounter information
	 */
	@Read(version=true)
    public Encounter readOrVread(@IdParam IdType theId) {
		int id;
		Encounter encounter;
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
		   encounter = service.getEncounterByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       encounter = service.getEncounterById(id);
		}
		return encounter;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theType
	 * @param thePatient
	 * @param theStatus
	 * @param theClass
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */

    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Encounter.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "Identifier(s) by which this encounter is knownr")
        @OptionalParam(name = Encounter.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "A date within the period the Encounter lasted")
        @OptionalParam(name = Encounter.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Specific type of encounter")
        @OptionalParam(name = Encounter.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "The patient or group present at the encounter")
        @OptionalParam(name = Encounter.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Classification of patient encounter")
        @OptionalParam(name = Encounter.SP_CLASS)
        TokenAndListParam theClass,
        
        @Description(shortDefinition = "planned | arrived | triaged | in-progress | onleave | finished | cancelled +")
        @OptionalParam(name = Encounter.SP_STATUS)
        TokenAndListParam theStatus,
      
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Encounter.SP_RES_ID, theId);
        paramMap.add(Encounter.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Encounter.SP_DATE, theDate);
        paramMap.add(Encounter.SP_TYPE, theType);
        paramMap.add(Encounter.SP_PATIENT, thePatient);
        paramMap.add(Encounter.SP_CLASS, theClass);
        paramMap.add(Encounter.SP_STATUS, theStatus);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<Encounter> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> encounterList = new ArrayList<>();
                for(Encounter encounter : results){
                	encounterList.add(encounter);
                }
                return encounterList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Encounter
     * @param theEncounter
     * @return
     */
    @Create
    public MethodOutcome createEncounter(@ResourceParam Encounter theEncounter) {
         
    	// Save this Encounter to the database...
    	DafEncounter dafEncounter = service.createEncounter(theEncounter);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafEncounter.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Encounter/1
     * @param theId
     * @param theEncounter
     * @return
     */
    @Update
    public MethodOutcome updateEncounterById(@IdParam IdType theId, 
    										@ResourceParam Encounter theEncounter) {
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
		theEncounter.setMeta(meta);
    	// Update this Encounter to the database...
    	DafEncounter dafEncounter = service.updateEncounterById(id, theEncounter);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafEncounter.getId() + "", VERSION_ID));
		return retVal;
    }

	public List<Encounter> getEncounterForBulkDataRequest(List<String> patientList, Date start, Date end) {
		List<Encounter> encounterList = service.getEncounterForBulkData(patientList, start, end);
		return encounterList;
	}
}
