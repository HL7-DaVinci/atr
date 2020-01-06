package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafRelatedPerson;
import org.hl7.davinci.atr.server.service.RelatedPersonService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.RelatedPerson;
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
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class RelatedPersonResourceProvider extends AbstractJaxRsResourceProvider<RelatedPerson> {

	public static final String RESOURCE_TYPE = "RelatedPerson";
    public static final String VERSION_ID = "1";
    
    @Autowired
    RelatedPersonService service;

    public RelatedPersonResourceProvider(FhirContext fhirContext) {
       super(fhirContext);
    }
    
	@Override
	public Class<RelatedPerson> getResourceType() {
		return RelatedPerson.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/RelatedPerson/1/_history/4
	 * @param theId : Id of the RelatedPerson
	 * @return : Object of RelatedPerson information
	 */
	@Read(version=true)
    public RelatedPerson readOrVread(@IdParam IdType theId) {
		int id;
		RelatedPerson relatedPerson;
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
		   relatedPerson = service.getRelatedPersonByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       relatedPerson = service.getRelatedPersonById(id);
		}
		return relatedPerson;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param thePatient
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = RelatedPerson.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A RelatedPerson identifier")
        @OptionalParam(name = RelatedPerson.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Search by subject - a patient")
        @OptionalParam(name = RelatedPerson.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(RelatedPerson.SP_RES_ID, theId);
        paramMap.add(RelatedPerson.SP_IDENTIFIER, theIdentifier);
        paramMap.add(RelatedPerson.SP_PATIENT, thePatient);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<RelatedPerson> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> relatedPersonList = new ArrayList<>();
                for(RelatedPerson relatedPerson : results){
                	relatedPersonList.add(relatedPerson);
                }
                return relatedPersonList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/RelatedPerson
     * @param theRelatedPerson
     * @return
     */
    @Create
    public MethodOutcome createRelatedPerson(@ResourceParam RelatedPerson theRelatedPerson) {
         
    	// Save this RelatedPerson to the database...
    	DafRelatedPerson dafRelatedPerson = service.createRelatedPerson(theRelatedPerson);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafRelatedPerson.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/RelatedPerson/1
     * @param theId
     * @param theRelatedPerson
     * @return
     */
    @Update
    public MethodOutcome updateRelatedPersonById(@IdParam IdType theId, 
    										@ResourceParam RelatedPerson theRelatedPerson) {
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
		theRelatedPerson.setMeta(meta);
    	// Update this RelatedPerson to the database...
    	DafRelatedPerson dafRelatedPerson = service.updateRelatedPersonById(id, theRelatedPerson);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafRelatedPerson.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<RelatedPerson> getRelatedPersonForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<RelatedPerson> relatedPersonList = service.getRelatedPersonForBulkData(patients, start, end);
		return relatedPersonList;
	}
}
