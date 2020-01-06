package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafMedicationAdministration;
import org.hl7.davinci.atr.server.service.MedicationAdministrationService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationAdministration;
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
public class MedicationAdministrationResourceProvider extends AbstractJaxRsResourceProvider<MedicationAdministration> {

	public static final String RESOURCE_TYPE = "MedicationAdministration";
    public static final String VERSION_ID = "1";
    
    @Autowired
    MedicationAdministrationService service;
    
    public MedicationAdministrationResourceProvider(FhirContext fhirContext) {
      super(fhirContext);
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<MedicationAdministration> getResourceType() {
		return MedicationAdministration.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/MedicationAdministration/1/_history/3.0
	 * @param theId : Id of the MedicationAdministration
	 * @return : Object of MedicationAdministration information
	 */
	@Read(version=true)
    public MedicationAdministration readOrVread(@IdParam IdType theId) {
		int id;
		MedicationAdministration medicationAdministration;
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
			medicationAdministration = service.getMedicationAdministrationByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			medicationAdministration = service.getMedicationAdministrationById(id);
		}
		return medicationAdministration;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theDevice
	 * @param theReasonNotGiven
	 * @param theContext
	 * @param theEffectiveTime
	 * @param thePatient
	 * @param theReasonGiven
	 * @param theMedication
	 * @param theSubject
	 * @param thePerformer
	 * @param theRequest
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = MedicationAdministration.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A MedicationAdministration identifier")
        @OptionalParam(name = MedicationAdministration.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "MedicationAdministration event status (for example one of active/paused/completed/nullified)")
        @OptionalParam(name = MedicationAdministration.SP_STATUS)
        TokenAndListParam theStatus,
        
        @Description(shortDefinition = "Return administrations with this administration device identity")
        @OptionalParam(name = MedicationAdministration.SP_DEVICE)
        ReferenceAndListParam theDevice,
        
        @Description(shortDefinition = "Reasons for not administering the medication")
        @OptionalParam(name = MedicationAdministration.SP_REASON_NOT_GIVEN)
        TokenAndListParam theReasonNotGiven,
        
        @Description(shortDefinition = "Return administrations that share this encounter or episode of care")
        @OptionalParam(name = MedicationAdministration.SP_CONTEXT)
        ReferenceAndListParam theContext,
        
        @Description(shortDefinition = "Date administration happened (or did not happen)")
        @OptionalParam(name = MedicationAdministration.SP_EFFECTIVE_TIME)
        DateRangeParam theEffectiveTime,
        
        @Description(shortDefinition = "The identity of a patient to list administrations  for")
        @OptionalParam(name = MedicationAdministration.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Reasons for not administering the medication")
        @OptionalParam(name = MedicationAdministration.SP_REASON_GIVEN)
        TokenAndListParam theReasonGiven,
        
        @Description(shortDefinition = "Return administrations of this medication resource")
        @OptionalParam(name = MedicationAdministration.SP_MEDICATION)
        ReferenceAndListParam theMedication,
        
        @Description(shortDefinition = "The identity of the individual or group to list administrations for")
        @OptionalParam(name = MedicationAdministration.SP_SUBJECT)
        ReferenceAndListParam theSubject,
        
        @Description(shortDefinition = "The identity of the individual who administered the medication")
        @OptionalParam(name = MedicationAdministration.SP_PERFORMER)
        ReferenceAndListParam thePerformer,
        
        @Description(shortDefinition = "The identity of a request to list administrations from")
        @OptionalParam(name = MedicationAdministration.SP_REQUEST)
        ReferenceAndListParam theRequest,
        
        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(MedicationAdministration.SP_RES_ID, theId);
            paramMap.add(MedicationAdministration.SP_IDENTIFIER, theIdentifier);
            paramMap.add(MedicationAdministration.SP_STATUS, theStatus);
            paramMap.add(MedicationAdministration.SP_DEVICE, theDevice);
            paramMap.add(MedicationAdministration.SP_REASON_NOT_GIVEN, theReasonNotGiven);
            paramMap.add(MedicationAdministration.SP_CONTEXT, theContext);
            paramMap.add(MedicationAdministration.SP_EFFECTIVE_TIME, theEffectiveTime);
            paramMap.add(MedicationAdministration.SP_PATIENT, thePatient);
            paramMap.add(MedicationAdministration.SP_REASON_GIVEN, theReasonGiven);
            paramMap.add(MedicationAdministration.SP_MEDICATION, theMedication);
            paramMap.add(MedicationAdministration.SP_SUBJECT, theSubject);
            paramMap.add(MedicationAdministration.SP_PERFORMER, thePerformer);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<MedicationAdministration> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> medicationAdministrationList = new ArrayList<IBaseResource>();
                    for(MedicationAdministration medicationAdministration : results){
                    	medicationAdministrationList.add(medicationAdministration);
                    }
                    return medicationAdministrationList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/MedicationAdministration
     * @param theMedicationAdministration
     * @return
     */
    @Create
    public MethodOutcome createMedicationAdministration(@ResourceParam MedicationAdministration theMedicationAdministration) {
         
    	// Save this MedicationAdministration to the database...
    	DafMedicationAdministration dafMedicationAdministration = service.createMedicationAdministration(theMedicationAdministration);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationAdministration.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/MedicationAdministration/1
     * @param theId
     * @param theMedicationAdministration
     * @return
     */
    @Update
    public MethodOutcome updateMedicationAdministrationById(@IdParam IdType theId, 
    										@ResourceParam MedicationAdministration theMedicationAdministration) {
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
		theMedicationAdministration.setMeta(meta);
    	// Update this MedicationAdministration to the database...
    	DafMedicationAdministration dafMedicationAdministration = service.updateMedicationAdministrationById(id, theMedicationAdministration);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafMedicationAdministration.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<MedicationAdministration> getMedicationAdministrationForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<MedicationAdministration> medAdministrationList = service.getMedicationAdministrationForBulkData(patients, start, end);
		return medAdministrationList;
	}
}
