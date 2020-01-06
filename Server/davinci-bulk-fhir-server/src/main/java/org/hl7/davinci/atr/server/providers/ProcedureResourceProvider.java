package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafProcedure;
import org.hl7.davinci.atr.server.service.ProcedureService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Procedure;
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
public class ProcedureResourceProvider extends AbstractJaxRsResourceProvider<Procedure> {
	
	public static final String RESOURCE_TYPE = "Procedure";
    public static final String VERSION_ID = "1";
    
    @Autowired
    ProcedureService service;

    public ProcedureResourceProvider(FhirContext fhirContext) {
       super(fhirContext);
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Procedure> getResourceType() {
		return Procedure.class;
	}
	
 	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Procedure/1/_history/4
	 * @param theId : Id of the Procedure
	 * @return : Object of Procedure information
	 */
	@Read(version=true)
    public Procedure readOrVread(@IdParam IdType theId) {
		int id;
		Procedure procedure;
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
		   procedure = service.getProcedureByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       procedure = service.getProcedureById(id);
		}
		return procedure;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theCode
	 * @param thePerformer
	 * @param theSubject
	 * @param theInstantiatesCanonical
	 * @param thePartOf
	 * @param theEncounter
	 * @param theReasonCode
	 * @param theBasedOn
	 * @param thePatient
	 * @param theReasonReference
	 * @param theLocation
	 * @param theInstantiatesUri
	 * @param theCategory
	 * @param theStatus
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Procedure.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A Procedure identifier")
        @OptionalParam(name = Procedure.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "When the procedure was performed")
        @OptionalParam(name = Procedure.SP_DATE)
        DateRangeParam theDate,
        
        @Description(shortDefinition = "A code to identify a  procedure")
        @OptionalParam(name = Procedure.SP_CODE)
        TokenAndListParam theCode,
        
        @Description(shortDefinition = "The reference to the Procedure")
        @OptionalParam(name = Procedure.SP_PERFORMER)
        ReferenceAndListParam thePerformer,
        
        @Description(shortDefinition = "A code to identify a  procedure")
        @OptionalParam(name = Procedure.SP_SUBJECT)
        ReferenceAndListParam theSubject,
        
        @Description(shortDefinition = "Instantiates FHIR protocol or definition")
        @OptionalParam(name = Procedure.SP_INSTANTIATES_CANONICAL)
        ReferenceAndListParam theInstantiatesCanonical,
        
        @Description(shortDefinition = "Part of referenced event")
        @OptionalParam(name = Procedure.SP_PART_OF)
        ReferenceAndListParam thePartOf,
        
        @Description(shortDefinition = "Encounter created as part of")
        @OptionalParam(name = Procedure.SP_ENCOUNTER)
        ReferenceAndListParam theEncounter,
        
        @Description(shortDefinition = "Coded reason procedure performed")
        @OptionalParam(name = Procedure.SP_REASON_CODE)
        TokenAndListParam theReasonCode,
       
        @Description(shortDefinition = "A request for this procedure")
        @OptionalParam(name = Procedure.SP_BASED_ON)
        ReferenceAndListParam theBasedOn,
        
        @Description(shortDefinition = "Search by subject - a patient")
        @OptionalParam(name = Procedure.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition="The justification that the procedure was performed")
        @OptionalParam(name=Procedure.SP_REASON_REFERENCE)
        ReferenceAndListParam theReasonReference,
        
        @Description(shortDefinition="Where the procedure happened")
        @OptionalParam(name=Procedure.SP_LOCATION)
        ReferenceAndListParam theLocation,
        
        @Description(shortDefinition="Instantiates external protocol or definition")
        @OptionalParam(name=Procedure.SP_INSTANTIATES_URI)
        UriAndListParam theInstantiatesUri,
        
        @Description(shortDefinition="Classification of the procedure")
        @OptionalParam(name=Procedure.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition="preparation | in-progress | not-done | suspended | aborted | completed | entered-in-error | unknown")
        @OptionalParam(name=Procedure.SP_STATUS)
        TokenAndListParam theStatus,
        
      

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Procedure.SP_RES_ID, theId);
        paramMap.add(Procedure.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Procedure.SP_DATE, theDate);
        paramMap.add(Procedure.SP_CODE, theCode);
        paramMap.add(Procedure.SP_PERFORMER, thePerformer);
        paramMap.add(Procedure.SP_SUBJECT, theSubject);
        paramMap.add(Procedure.SP_INSTANTIATES_CANONICAL, theInstantiatesCanonical);
        paramMap.add(Procedure.SP_PART_OF, thePartOf);
        paramMap.add(Procedure.SP_ENCOUNTER, theEncounter);
        paramMap.add(Procedure.SP_REASON_CODE, theReasonCode);
        paramMap.add(Procedure.SP_BASED_ON, theBasedOn);
        paramMap.add(Procedure.SP_PATIENT, thePatient);
        paramMap.add(Procedure.SP_REASON_REFERENCE, theReasonReference);
        paramMap.add(Procedure.SP_LOCATION, theLocation);
        paramMap.add(Procedure.SP_INSTANTIATES_URI, theInstantiatesUri);
        paramMap.add(Procedure.SP_CATEGORY, theCategory);
        paramMap.add(Procedure.SP_STATUS, theStatus);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<Procedure> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> procedureList = new ArrayList<>();
                for(Procedure procedure : results){
                	procedureList.add(procedure);
                }
                return procedureList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Procedure
     * @param theProcedure
     * @return
     */
    @Create
    public MethodOutcome createProcedure(@ResourceParam Procedure theProcedure) {
         
    	// Save this Procedure to the database...
    	DafProcedure dafProcedure = service.createProcedure(theProcedure);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafProcedure.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Procedure/1
     * @param theId
     * @param theProcedure
     * @return
     */
    @Update
    public MethodOutcome updateProcedureById(@IdParam IdType theId, 
    										@ResourceParam Procedure theProcedure) {
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
		theProcedure.setMeta(meta);
    	// Update this Procedure to the database...
    	DafProcedure dafProcedure = service.updateProcedureById(id, theProcedure);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafProcedure.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Procedure> getProcedureForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<Procedure> procedureList = service.getProcedureForBulkData(patients, start, end);
		return procedureList;
	}
}
