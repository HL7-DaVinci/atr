package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafFamilyMemberHistory;
import org.hl7.davinci.atr.server.service.FamilyMemberHistoryService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
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
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class FamilyMemberHistoryResourceProvider extends AbstractJaxRsResourceProvider<FamilyMemberHistory> {
	

	public static final String RESOURCE_TYPE = "FamilyMemberHistory";
    public static final String VERSION_ID = "1";
    
    @Autowired
    FamilyMemberHistoryService service;
    
    public FamilyMemberHistoryResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<FamilyMemberHistory> getResourceType() {
		return FamilyMemberHistory.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/FamilyMemberHistory/1/_history/4
	 * @param theId : Id of the FamilyMemberHistory
	 * @return : Object of FamilyMemberHistory information
	 */
	@Read(version=true)
    public FamilyMemberHistory readOrVread(@IdParam IdType theId) {
		int id;
		FamilyMemberHistory familyMemberHistory;
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
		   familyMemberHistory = service.getFamilyMemberHistoryByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       familyMemberHistory = service.getFamilyMemberHistoryById(id);
		}
		return familyMemberHistory;
    }

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theRelationship
	 * @param theInstantiatesUri
	 * @param theSex
	 * @param thePatient
	 * @param theCode
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = FamilyMemberHistory.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A familymember identifier")
        @OptionalParam(name = FamilyMemberHistory.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "partial | completed | entered-in-error | health-unknown")
        @OptionalParam(name = FamilyMemberHistory.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "A search by a relationship type")
        @OptionalParam(name = FamilyMemberHistory.SP_RELATIONSHIP)
        TokenAndListParam theRelationship,
        
        @Description(shortDefinition = "Instantiates external protocol or definition")
        @OptionalParam(name = FamilyMemberHistory.SP_INSTANTIATES_URI)
        UriAndListParam theInstantiatesUri,

        @Description(shortDefinition = "The identity of a subject to list family member history items for")
        @OptionalParam(name = FamilyMemberHistory.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "A search by a condition code")
        @OptionalParam(name = FamilyMemberHistory.SP_CODE)
        TokenAndListParam theCode,
        
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
	        SearchParameterMap paramMap = new SearchParameterMap();
	        paramMap.add(FamilyMemberHistory.SP_RES_ID, theId);
	        paramMap.add(FamilyMemberHistory.SP_IDENTIFIER, theIdentifier);
	        paramMap.add(FamilyMemberHistory.SP_STATUS, theStatus);
	        paramMap.add(FamilyMemberHistory.SP_RELATIONSHIP, theRelationship);
	        paramMap.add(FamilyMemberHistory.SP_INSTANTIATES_URI, theInstantiatesUri);
	        paramMap.add(FamilyMemberHistory.SP_PATIENT, thePatient);
	        paramMap.add(FamilyMemberHistory.SP_CODE, theCode);
	        paramMap.setIncludes(theIncludes);
	        paramMap.setSort(theSort);
	        paramMap.setCount(theCount);
	        
	        final List<FamilyMemberHistory> results = service.search(paramMap);
	
	        return new IBundleProvider() {
	            final InstantDt published = InstantDt.withCurrentTime();
	            @Override
	            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
	                List<IBaseResource> familyMemberHistoryList = new ArrayList<>();
	                for(FamilyMemberHistory theFamilyMemberHistory : results){
	                	familyMemberHistoryList.add(theFamilyMemberHistory);
	                }
	                return familyMemberHistoryList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/FamilyMemberHistory
     * @param theFamilyMemberHistory
     * @return
     */
    @Create
    public MethodOutcome createFamilyMemberHistory(@ResourceParam FamilyMemberHistory theFamilyMemberHistory) {
         
    	// Save this FamilyMemberHistory to the database...
    	DafFamilyMemberHistory dafFamilyMemberHistory = service.createFamilyMemberHistory(theFamilyMemberHistory);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafFamilyMemberHistory.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/FamilyMemberHistory/1
     * @param theId
     * @param theFamilyMemberHistory
     * @return
     */
    @Update
    public MethodOutcome updateFamilyMemberHistoryById(@IdParam IdType theId, 
    										@ResourceParam FamilyMemberHistory theFamilyMemberHistory) {
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
		theFamilyMemberHistory.setMeta(meta);
    	// Update this FamilyMemberHistory to the database...
    	DafFamilyMemberHistory dafFamilyMemberHistory = service.updateFamilyMemberHistoryById(id, theFamilyMemberHistory);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafFamilyMemberHistory.getId() + "", VERSION_ID));
		return retVal;
    }

	public List<FamilyMemberHistory> getFamilyMemberHistoryForBulkDataRequest(List<String> patientList, Date start,
			Date end) {
		List<FamilyMemberHistory> familyMemberHistoryList = service.getFamilyMemberHistoryForBulkDataRequest(patientList, start, end);
		return familyMemberHistoryList;
	}
}
