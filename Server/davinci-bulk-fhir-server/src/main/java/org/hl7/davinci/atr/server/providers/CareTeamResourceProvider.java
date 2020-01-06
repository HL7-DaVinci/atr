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
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.hl7.davinci.atr.server.model.DafCareTeam;
import org.hl7.davinci.atr.server.service.CareTeamService;
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
public class CareTeamResourceProvider extends AbstractJaxRsResourceProvider<CareTeam> {
	
	public static final String RESOURCE_TYPE = "CareTeam";
    public static final String VERSION_ID = "1";
    
    @Autowired
    CareTeamService service;
    
    public CareTeamResourceProvider(FhirContext fhirContext) {
	    super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<CareTeam> getResourceType() {
		return CareTeam.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/CareTeam/1/_history/4
	 * @param theId : Id of the careTeam
	 * @return : Object of careTeam information
	 */	
	@Read(version=true)
    public CareTeam readOrVread(@IdParam IdType theId) {
		int id;
		CareTeam careTeam;
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
			careTeam = service.getCareTeamByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       careTeam = service.getCareTeamById(id);
		}
		return careTeam;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theDate
	 * @param theIdentifier
	 * @param thePatient
	 * @param theSubject
	 * @param theContext
	 * @param theEncounter
	 * @param theCategory
	 * @param theParticipant
	 * @param theStatus
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "Time period team covers")
        @OptionalParam(name = CareTeam.SP_DATE)
        DateAndListParam theDate,

        @Description(shortDefinition = "The ID of the resource")
        @OptionalParam(name = CareTeam.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "External Ids for this team")
        @OptionalParam(name = CareTeam.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Who care team is for")
        @OptionalParam(name = CareTeam.SP_SUBJECT)
        StringAndListParam theSubject,
        
        @Description(shortDefinition = "Who care team is for")
        @OptionalParam(name = CareTeam.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Encounter or episode associated with CareTeam")
        @OptionalParam(name = CareTeam.SP_ENCOUNTER)
        StringAndListParam theEncounter,

        @Description(shortDefinition = "Type of team")
        @OptionalParam(name = CareTeam.SP_CATEGORY)
        StringAndListParam theCategory,

        @Description(shortDefinition = "Who is involved")
        @OptionalParam(name = CareTeam.SP_PARTICIPANT)
        StringAndListParam theParticipant,
        
        @Description(shortDefinition="proposed | active | suspended | inactive | entered-in-error")
		@OptionalParam(name = CareTeam.SP_STATUS)
		TokenAndListParam theStatus, 
  
			
        @IncludeParam(allow = {"CareTeam.managingOrganization", "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(CareTeam.SP_DATE, theDate);
        paramMap.add(CareTeam.SP_RES_ID, theId);
        paramMap.add(CareTeam.SP_IDENTIFIER, theIdentifier);
        paramMap.add(CareTeam.SP_SUBJECT, theSubject);
        paramMap.add(CareTeam.SP_PATIENT, thePatient);
        paramMap.add(CareTeam.SP_ENCOUNTER, theEncounter);
        paramMap.add(CareTeam.SP_CATEGORY, theCategory);
        paramMap.add(CareTeam.SP_PARTICIPANT, theParticipant);
        paramMap.add(CareTeam.SP_STATUS, theStatus);
        	            
        final List<CareTeam> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> careTeamList = new ArrayList<>();
                for(CareTeam theCareTeam : results){
                	careTeamList.add(theCareTeam);
                }
                return careTeamList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/CareTeam
     * @param theCareTeam
     * @return
     */
    @Create
    public MethodOutcome createCareTeam(@ResourceParam CareTeam theCareTeam) {
         
    	// Save this CareTeam to the database...
    	DafCareTeam dafCareTeam = service.createCareTeam(theCareTeam);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafCareTeam.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/CareTeam/1
     * @param theId
     * @param theCareTeam
     * @return
     */
    @Update
    public MethodOutcome updateCareTeamById(@IdParam IdType theId, 
    										@ResourceParam CareTeam theCareTeam) {
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
		theCareTeam.setMeta(meta);
    	// Update this CareTeam to the database...
    	DafCareTeam dafCareTeam = service.updateCareTeamById(id, theCareTeam);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafCareTeam.getId() + "", VERSION_ID));
		return retVal;
    }

	public List<CareTeam> getCareTeamForBulkDataRequest(List<String> patientList, Date start, Date end) {
		List<CareTeam> careteamList = service.getCareTeamForBulkDataRequest(patientList, start, end);
  		return careteamList;
	}
}