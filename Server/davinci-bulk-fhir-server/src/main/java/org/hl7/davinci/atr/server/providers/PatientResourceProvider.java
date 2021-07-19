package org.hl7.davinci.atr.server.providers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.davinci.atr.server.model.DafBulkDataRequest;
import org.hl7.davinci.atr.server.model.DafPatient;
import org.hl7.davinci.atr.server.service.BulkDataRequestService;
import org.hl7.davinci.atr.server.service.PatientService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

@Component
public class PatientResourceProvider extends AbstractJaxRsResourceProvider<Patient> {
	
	public static final String RESOURCE_TYPE = "Patient";
    public static final String VERSION_ID = "1";
	private static final Logger logger = LoggerFactory.getLogger(PatientResourceProvider.class);    
	@Autowired
    FhirContext fhirContext;
    @Autowired
	private PatientService service;
    
    @Autowired
    BulkDataRequestService bdrService;
    
    public PatientResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
    @Override
    public Class<Patient> getResourceType() {
        return Patient.class;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Patient
     * @param thePatient
     * @return
     */
    @Create
    public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
		MethodOutcome retVal = null;
    	try {
        	// Save this patient to the database...
    		Patient newPatient = service.createPatient(thePatient);
//        	IParser jsonParser = fhirContext.newJsonParser();
//        	Patient patient = null;
//			if(newPatient != null) {
//				patient = jsonParser.parseResource(Patient.class, jsonParser.encodeResourceToString(newPatient)); 
//			}
        	retVal = new MethodOutcome();
    		retVal.setId(new IdType(RESOURCE_TYPE, thePatient.getIdElement().getIdPart(), thePatient.getMeta().getVersionId()));    		
    	}
    	catch(Exception e) {
    		logger.error("Exception in createPatient of PatientResourceProvider ", e);
    	}
		return retVal;
    }

    /**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. 
	 * To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. 
	 * It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Patient/1/_history/1
	 * @param theId: Id of the patient
	 * @return thePatient: Object of patient information
	 */
	@Read(version=true)
    public Patient readOrVread(@IdParam IdType theId) {
		String id;
		Patient thePatient = null;
		try {
		    id = theId.getIdPart();
		} catch (NumberFormatException e) {
		    /*
		     * If we can't parse the ID as a long, it's not valid so this is an unknown resource
			 */
		    throw new ResourceNotFoundException(theId);
		}

		if (theId.hasVersionIdPart()) {
		   // this is a vread  
			thePatient = service.getPatientByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			thePatient = service.getPatientById(id);
		}
		return thePatient;
    }
	
	/**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Patient/1
     * @param theId
     * @param thePatient
     * @return
     */
    @Update
    public MethodOutcome updatePatientById(@IdParam IdType theId, 
    										@ResourceParam Patient thePatient) {
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
		thePatient.setMeta(meta);
    	// Update this Patient to the database...
    	DafPatient dafQuestionnaireResponse = service.updatePatientById(id, thePatient);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafQuestionnaireResponse.getId() + "", VERSION_ID));
		return retVal;
    }
    
    /**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
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
        @OptionalParam(name = Patient.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "A patient identifier")
        @OptionalParam(name = Patient.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "A portion of either family or given name of the patient")
        @OptionalParam(name = Patient.SP_NAME)
        StringAndListParam theName,

        @Description(shortDefinition = "A portion of the family name of the patient")
        @OptionalParam(name = Patient.SP_FAMILY)
        StringAndListParam theFamily,

        @Description(shortDefinition = "A portion of the given name of the patient")
        @OptionalParam(name = Patient.SP_GIVEN)
        StringAndListParam theGiven,

        @Description(shortDefinition = "The organization that is the custodian of the patient record")
        @OptionalParam(name = Patient.SP_ORGANIZATION)
        StringAndListParam theOrganization,

        @Description(shortDefinition = "The value in any kind of telecom details of the patient")
        @OptionalParam(name = Patient.SP_TELECOM)
        StringAndListParam theTelecom,

        @Description(shortDefinition = "An address in any kind of address/part of the patient")
        @OptionalParam(name = Patient.SP_ADDRESS)
        StringAndListParam theAddress,
        
        @Description(shortDefinition="A city specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_CITY)
		StringAndListParam theAddressCity, 
  
		@Description(shortDefinition="A state specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_STATE)
		StringAndListParam theAddressState, 
  
		@Description(shortDefinition="A postalCode specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE)
		StringAndListParam theAddressPostalcode, 
  
		@Description(shortDefinition="A country specified in an address")
		@OptionalParam(name = Patient.SP_ADDRESS_COUNTRY)
		StringAndListParam theAddressCountry, 

        @Description(shortDefinition = "Gender of the patient")
        @OptionalParam(name = Patient.SP_GENDER)
        TokenAndListParam theGender,

        @Description(shortDefinition = "Language code (irrespective of use value)")
        @OptionalParam(name = Patient.SP_LANGUAGE)
        StringAndListParam theLanguage,

        @Description(shortDefinition = "The patient's date of birth")
        @OptionalParam(name = Patient.SP_BIRTHDATE)
        DateRangeParam theBirthdate,

        @Description(shortDefinition = "Whether the patient record is active")
        @OptionalParam(name = Patient.SP_ACTIVE)
        TokenAndListParam theActive,
        
        @Description(shortDefinition = "A use code specified in an address.Patient.address.use")
        @OptionalParam(name = Patient.SP_ADDRESS_USE)
        TokenAndListParam theAddressUse,

        @Description(shortDefinition = "All patients linked to the given patient")
        @OptionalParam(name = Patient.SP_LINK, targetTypes = {Patient.class})
        ReferenceAndListParam theLink,
        
        @Description(shortDefinition = "This patient has been marked as deceased, or as a death date entered")
        @OptionalParam(name = Patient.SP_DECEASED)
        TokenAndListParam theDeceased,

        @IncludeParam(allow = {"Patient.managingOrganization", "Patient.link.other", "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount
    ) {
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Patient.SP_RES_ID, theId);
        paramMap.add(Patient.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Patient.SP_NAME, theName);
        paramMap.add(Patient.SP_FAMILY, theFamily);
        paramMap.add(Patient.SP_GIVEN, theGiven);
        paramMap.add(Patient.SP_ORGANIZATION, theOrganization);
        paramMap.add(Patient.SP_TELECOM, theTelecom);
        paramMap.add(Patient.SP_ADDRESS, theAddress);
        paramMap.add(Patient.SP_ADDRESS_CITY, theAddressCity);
        paramMap.add(Patient.SP_ADDRESS_STATE, theAddressState);
        paramMap.add(Patient.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
        paramMap.add(Patient.SP_ADDRESS_COUNTRY, theAddressCountry);
        paramMap.add(Patient.SP_GENDER, theGender);
        paramMap.add(Patient.SP_LANGUAGE, theLanguage);
        paramMap.add(Patient.SP_BIRTHDATE, theBirthdate);
        paramMap.add(Patient.SP_ACTIVE, theActive);
        paramMap.add(Patient.SP_LINK, theLink);
        paramMap.setIncludes(theIncludes);
        paramMap.setSort(theSort);
        paramMap.setCount(theCount);
        
        final List<Patient> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> patientList = new ArrayList<>();
                for(Patient patient : results){
                    patientList.add(patient);
                }
                return patientList;
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
    
    public List<Patient> getPatientForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<Patient> patientList = service.getPatientJsonForBulkData(patients, start, end);
		return patientList;
	}
    
    @Operation(name="$export", idempotent=true)
    public Binary patientTypeOperation(
    		@OperationParam(name="_since") DateRangeParam theStart,
    		//@OperationParam(name="_end") DateDt theEnd,
    		@OperationParam(name="_type") String type,
    		RequestDetails requestDetails,
    		HttpServletRequest request,
    		HttpServletResponse response) throws IOException {
    	if(requestDetails.getHeader("Prefer") != null && requestDetails.getHeader("Accept") != null) {
    		if(requestDetails.getHeader("Prefer").equals("respond-async") && requestDetails.getHeader("Accept").equals("application/fhir+json")) {	
		    	DafBulkDataRequest bdr = new DafBulkDataRequest();
		    	bdr.setResourceName("Patient");
		    	bdr.setStatus("Accepted");
		    	bdr.setProcessedFlag(false);
		    	if(theStart != null && theStart.getLowerBound() != null) {
		    		bdr.setStart(theStart.getLowerBound().getValueAsString()); 
		    	}
		    	if(theStart != null && theStart.getUpperBound() != null) {
		    		bdr.setEnd(theStart.getUpperBound().getValueAsString()); 
		    	}
		    	bdr.setType(type);
		    	bdr.setRequestResource(request.getRequestURL().toString());
		        
		    	DafBulkDataRequest responseBDR = bdrService.saveBulkDataRequest(bdr);
		    	
		    	String uri = request.getScheme() + "://" +
		                request.getServerName() + 
		                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
		                +request.getContextPath();
				System.out.println("URI =============>>>>> "+ uri+"/bulkdata/"+responseBDR.getRequestId());	
		    	response.setStatus(202);
		    	response.setHeader("Content-Location", uri+"/bulkdata/"+responseBDR.getRequestId());
		    	GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(new Date());
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
				cal.add(Calendar.DATE, 10);
		        //HTTP header date format: Thu, 01 Dec 1994 16:00:00 GMT
		        String o = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format( cal.getTime() );    
		        response.setHeader("Expires", o);
		    	
		    	Binary retVal = new Binary();
				retVal.setContentType("application/fhir+json");
				return retVal;
    	}else {
    		throw new UnprocessableEntityException("Invalid header values!");
    	}
    	}else {
    		throw new UnprocessableEntityException("Prefer or Accepted Header is missing!");
    	}
    }
}