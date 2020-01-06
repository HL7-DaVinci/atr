package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafMedicationDispense;
import org.hl7.davinci.atr.server.service.MedicationDispenseService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationDispense;
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
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class MedicationDispenseResourceProvider extends AbstractJaxRsResourceProvider<MedicationDispense> {

	public static final String RESOURCE_TYPE = "MedicationDispense";
    public static final String VERSION_ID = "1";
    @Autowired
    MedicationDispenseService service;
    
    public MedicationDispenseResourceProvider(FhirContext fhirContext) {
       super(fhirContext);
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<MedicationDispense> getResourceType() {
		return MedicationDispense.class;
	}
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationDispense/1/_history/3.0
	 * @param theId : Id of the MedicationDispense
	 * @return : Object of MedicationDispense information
	 */
	@Read(version=true)
    public MedicationDispense readOrVread(@IdParam IdType theId) {
		int id;
		MedicationDispense medicationDispense;
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
			medicationDispense = service.getMedicationDispenseByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			medicationDispense = service.getMedicationDispenseById(id);
		}
		return medicationDispense;
    }
	
	/**
	 *  The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theContext
	 * @param thePatient
	 * @param thePrescription
	 * @param theMedication
	 * @param theWhenPrepared
	 * @param theWhenHandedOver
	 * @param theType
	 * @param theDestination
	 * @param theReceiver
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
        @OptionalParam(name = MedicationDispense.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A MedicationAdministration identifier")
        @OptionalParam(name = MedicationDispense.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "Returns dispenses with a specified dispense status")
        @OptionalParam(name = MedicationDispense.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Returns dispenses with a specific context (episode or episode of care)")
        @OptionalParam(name = MedicationDispense.SP_CONTEXT)
        ReferenceAndListParam theContext,
        
        @Description(shortDefinition = "The identity of a patient to list dispenses  for")
        @OptionalParam(name = MedicationDispense.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "The identity of a prescription to list dispenses from")
        @OptionalParam(name = MedicationDispense.SP_PRESCRIPTION)
        ReferenceAndListParam thePrescription,
        
        @Description(shortDefinition = "Returns dispenses of this medicine resource")
        @OptionalParam(name = MedicationDispense.SP_MEDICATION)
        ReferenceAndListParam theMedication,
        
        @Description(shortDefinition = "Returns dispenses prepared on this date")
        @OptionalParam(name = MedicationDispense.SP_WHENPREPARED)
        DateRangeParam theWhenPrepared,
        
        @Description(shortDefinition = "Returns dispenses handed over on this date")
        @OptionalParam(name = MedicationDispense.SP_WHENHANDEDOVER)
        DateRangeParam theWhenHandedOver,
        
        @Description(shortDefinition = "Returns dispenses of a specific type")
        @OptionalParam(name = MedicationDispense.SP_TYPE)
        TokenAndListParam theType,
        
        @Description(shortDefinition = "Returns dispenses that should be sent to a specific destination")
        @OptionalParam(name = MedicationDispense.SP_DESTINATION)
        ReferenceAndListParam theDestination,
        
        @Description(shortDefinition = "The identity of a receiver to list dispenses for")
        @OptionalParam(name = MedicationDispense.SP_RECEIVER)
        ReferenceAndListParam theReceiver,
        
        @Description(shortDefinition = "Returns dispenses of this medicine code")
        @OptionalParam(name = MedicationDispense.SP_CODE)
        TokenAndListParam theCode,


        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(MedicationDispense.SP_RES_ID, theId);
        paramMap.add(MedicationDispense.SP_IDENTIFIER, theIdentifier);
        paramMap.add(MedicationDispense.SP_STATUS, theStatus);
        paramMap.add(MedicationDispense.SP_CONTEXT, theContext);
        paramMap.add(MedicationDispense.SP_PATIENT, thePatient);
        paramMap.add(MedicationDispense.SP_PRESCRIPTION, thePatient);
        paramMap.add(MedicationDispense.SP_WHENPREPARED, theWhenPrepared);
        paramMap.add(MedicationDispense.SP_WHENHANDEDOVER, theWhenHandedOver);
        paramMap.add(MedicationDispense.SP_TYPE, theType);
        paramMap.add(MedicationDispense.SP_MEDICATION, theMedication);
        paramMap.add(MedicationDispense.SP_DESTINATION, theDestination);
        paramMap.add(MedicationDispense.SP_RECEIVER, theReceiver);
        paramMap.add(MedicationDispense.SP_CODE, theCode);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<MedicationDispense> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> medicationDispenseList = new ArrayList<IBaseResource>();
                for(MedicationDispense medicationDispense : results){
                	medicationDispenseList.add(medicationDispense);
                }
                return medicationDispenseList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/MedicationDispense
     * @param theMedicationDispense
     * @return
     */
    @Create
    public MethodOutcome createMedicationDispense(@ResourceParam MedicationDispense theMedicationDispense) {
         
    	// Save this MedicationDispense to the database...
    	DafMedicationDispense dafMedicationDispense = service.createMedicationDispense(theMedicationDispense);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationDispense.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/MedicationDispense/1
     * @param theId
     * @param theMedicationDispense
     * @return
     */
    @Update
    public MethodOutcome updateMedicationDispenseById(@IdParam IdType theId, 
    										@ResourceParam MedicationDispense theMedicationDispense) {
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
		theMedicationDispense.setMeta(meta);
    	// Update this MedicationDispense to the database...
    	DafMedicationDispense dafMedicationDispense = service.updateMedicationDispenseById(id, theMedicationDispense);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationDispense.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<MedicationDispense> getMedicationDispenseForBulkDataRequest(List<String> patients, Date start, Date end) {

  		List<MedicationDispense> medDispenseList = service.getMedicationDispenseForBulkData(patients, start, end);
  		return medDispenseList;
  	}

}