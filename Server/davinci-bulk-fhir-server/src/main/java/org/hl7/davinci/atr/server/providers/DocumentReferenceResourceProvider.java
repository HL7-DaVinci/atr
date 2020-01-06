package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafDocumentReference;
import org.hl7.davinci.atr.server.service.DocumentReferenceService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DocumentReference;
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
public class DocumentReferenceResourceProvider extends AbstractJaxRsResourceProvider<DocumentReference> {
	
	public static final String RESOURCE_TYPE = "DocumentReference";
    public static final String VERSION_ID = "1";
    
    @Autowired
    DocumentReferenceService service;

    public DocumentReferenceResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<DocumentReference> getResourceType() {
		return DocumentReference.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/DocumentReference/1/_history/4
	 * @param theId : Id of the DocumentReference
	 * @return : Object of DocumentReference information
	 */
	@Read(version=true)
    public DocumentReference readOrVread(@IdParam IdType theId) {
		int id;
		DocumentReference documentReference;
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
		   documentReference = service.getDocumentReferenceByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       documentReference = service.getDocumentReferenceById(id);
		}
		return documentReference;
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
	 * @param theCategory
	 * @param theStatus
	 * @param thePeriod
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */

    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = DocumentReference.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A DocumentReference identifier")
        @OptionalParam(name = DocumentReference.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
       
        @Description(shortDefinition = "When this document reference was created")
        @OptionalParam(name = DocumentReference.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "Kind of document (LOINC if possible)")
        @OptionalParam(name = DocumentReference.SP_TYPE)
        TokenAndListParam theType,
         
        @Description(shortDefinition = "Who/what is the subject of the document")
        @OptionalParam(name = DocumentReference.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Categorization of document")
        @OptionalParam(name = DocumentReference.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "current | superseded | entered-in-error")
        @OptionalParam(name = DocumentReference.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Time of service that is being documented")
        @OptionalParam(name = DocumentReference.SP_PERIOD)
        DateRangeParam thePeriod,
       
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
    	
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(DocumentReference.SP_RES_ID, theId);
        paramMap.add(DocumentReference.SP_IDENTIFIER, theIdentifier);
        paramMap.add(DocumentReference.SP_DATE, theDate);
        paramMap.add(DocumentReference.SP_TYPE, theType);
        paramMap.add(DocumentReference.SP_PATIENT, thePatient);
        paramMap.add(DocumentReference.SP_CATEGORY, theCategory);
        paramMap.add(DocumentReference.SP_STATUS, theStatus);
        paramMap.add(DocumentReference.SP_PERIOD, thePeriod);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<DocumentReference> results = service.search(paramMap);

        return new IBundleProvider() {
        	final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> documentReferenceList = new ArrayList<>();
                for(DocumentReference dafDocumentReference : results){
                	documentReferenceList.add(dafDocumentReference);
                }
                return documentReferenceList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/DocumentReference
     * @param theDocumentReference
     * @return
     */
    @Create
    public MethodOutcome createDocumentReference(@ResourceParam DocumentReference theDocumentReference) {
         
    	// Save this DocumentReference to the database...
    	DafDocumentReference dafDocumentReference = service.createDocumentReference(theDocumentReference);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDocumentReference.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/DocumentReference/1
     * @param theId
     * @param theDocumentReference
     * @return
     */
    @Update
    public MethodOutcome updateDocumentReferenceById(@IdParam IdType theId, 
    										@ResourceParam DocumentReference theDocumentReference) {
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
		theDocumentReference.setMeta(meta);
    	// Update this DocumentReference to the database...
    	DafDocumentReference dafDocumentReference = service.updateDocumentReferenceById(id, theDocumentReference);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDocumentReference.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<DocumentReference> getDocumentReferenceForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<DocumentReference> docRefList = service.getDocumentReferenceForBulkData(patients, start, end);
		return docRefList;
	}
}
