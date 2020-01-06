package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDiagnosticReport;
import org.hl7.davinci.atr.server.service.DiagnosticReportService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class DiagnosticReportResourceProvider extends AbstractJaxRsResourceProvider<DiagnosticReport> {

	public static final String RESOURCE_TYPE = "DiagnosticReport";
	public static final String VERSION_ID = "1";
	
	@Autowired
	DiagnosticReportService service;

	public DiagnosticReportResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<DiagnosticReport> getResourceType() {
		return DiagnosticReport.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * 
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/DiagnosticReport/1/_history/4
	 * 
	 * @param theId : Id of the DiagnosticReport
	 * @return : Object of DiagnosticReport information
	 */
	@Read(version = true)
	public DiagnosticReport readOrVread(@IdParam IdType theId) {
		int id;
		DiagnosticReport diagnosticReport;
		try {
			id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}
		if (theId.hasVersionIdPart()) {
			// this is a vread
			diagnosticReport = service.getDiagnosticReportByVersionId(id, theId.getVersionIdPart());

		} else {

			diagnosticReport = service.getDiagnosticReportById(id);
		}

		return diagnosticReport;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCode
	 * @param theSubject
	 * @param theCategory
	 * @param theConclusion
	 * @param thePerformer
	 * @param theResult
	 * @param theBasedOn
	 * @param theSpecimen
	 * @param theResultInterpreter
	 * @param theIssused
	 * @param theDate
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(
		javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
        @OptionalParam(name = DiagnosticReport.SP_RES_ID)
		StringAndListParam theId,

		@Description(shortDefinition = "An DiagnosticReport  identifier")
		@OptionalParam(name = DiagnosticReport.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "The status of the diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_STATUS)
		TokenAndListParam theStatus,

		@Description(shortDefinition = "A code or name that describes this diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_CODE) 
		TokenAndListParam theCode,

		@Description(shortDefinition = "The subject of the report")
		@OptionalParam(name = DiagnosticReport.SP_SUBJECT) 
		ReferenceAndListParam theSubject,

		@Description(shortDefinition = "The healthcare event which this DiagnosticReport is about")
		@OptionalParam(name = DiagnosticReport.SP_ENCOUNTER) 
		ReferenceAndListParam theEncounter,

		@Description(shortDefinition = "This is used for searching, sorting and display purposes.")
		@OptionalParam(name = DiagnosticReport.SP_CATEGORY) 
		TokenAndListParam theCategory,

		@Description(shortDefinition = "Concise and clinically contextualized summary conclusion of the diagnostic report")
		@OptionalParam(name = DiagnosticReport.SP_CONCLUSION)
		TokenAndListParam theConclusion,

		@Description(shortDefinition = "The diagnostic service that is responsible for issuing the report")
		@OptionalParam(name = DiagnosticReport.SP_PERFORMER) 
		ReferenceAndListParam thePerformer,

		@Description(shortDefinition = "Observations that are part of this diagnostic report") 
		@OptionalParam(name = DiagnosticReport.SP_RESULT) 
		ReferenceAndListParam theResult,

		@Description(shortDefinition = "Details concerning a service requested") 
		@OptionalParam(name = DiagnosticReport.SP_BASED_ON) 
		ReferenceAndListParam theBasedOn,

		@Description(shortDefinition = "Details about the specimens on which this diagnostic report is based")
		@OptionalParam(name = DiagnosticReport.SP_SPECIMEN) 
		ReferenceAndListParam theSpecimen,

		@Description(shortDefinition = "The practitioner or organization that is responsible for the report's conclusions and interpretations")
		@OptionalParam(name = DiagnosticReport.SP_RESULTS_INTERPRETER) 
		ReferenceAndListParam theInterpreter,

		@Description(shortDefinition = "The date and time that this version of the report was made available to providers") 
		@OptionalParam(name = DiagnosticReport.SP_ISSUED)
		DateAndListParam theIssued,

		@Description(shortDefinition = "The time or time-period the observed values are related to") 
		@OptionalParam(name = DiagnosticReport.SP_DATE)
		DateAndListParam theDate,
		
		@Description(shortDefinition = "The subject of the report if a patient")
		@OptionalParam(name = DiagnosticReport.SP_PATIENT) 
		ReferenceAndListParam thePatient,

		@Sort SortSpec theSort,
		@Count Integer theCount) {
		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(DiagnosticReport.SP_RES_ID, theId);
		paramMap.add(DiagnosticReport.SP_IDENTIFIER, theIdentifier);
		paramMap.add(DiagnosticReport.SP_STATUS, theStatus);
		paramMap.add(DiagnosticReport.SP_CODE, theCode);
		paramMap.add(DiagnosticReport.SP_ENCOUNTER, theEncounter);
		paramMap.add(DiagnosticReport.SP_SUBJECT, theSubject);
		paramMap.add(DiagnosticReport.SP_CATEGORY, theCategory);
		paramMap.add(DiagnosticReport.SP_CONCLUSION, theConclusion);
		paramMap.add(DiagnosticReport.SP_PERFORMER, thePerformer);
		paramMap.add(DiagnosticReport.SP_RESULT, theResult);
		paramMap.add(DiagnosticReport.SP_BASED_ON, theBasedOn);
		paramMap.add(DiagnosticReport.SP_SPECIMEN, theSpecimen);
		paramMap.add(DiagnosticReport.SP_RESULTS_INTERPRETER, theInterpreter);
		paramMap.add(DiagnosticReport.SP_DATE, theDate);
		paramMap.add(DiagnosticReport.SP_ISSUED, theIssued);
		paramMap.add(DiagnosticReport.SP_PATIENT, thePatient);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<DiagnosticReport> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> diagnosticReportList = new ArrayList<>();
				for (DiagnosticReport diagnosticReport : results) {
					diagnosticReportList.add(diagnosticReport);
				}
				return diagnosticReportList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/DiagnosticReport
     * @param theDiagnosticReport
     * @return
     */
	@Create
	public MethodOutcome createDiagnosticReport(@ResourceParam DiagnosticReport theDiagnosticReport) {	

		// Save this DiagnosticReport to the database...
		DafDiagnosticReport dafDiagnosticReport = service.createDiagnosticReport(theDiagnosticReport);

		// This method returns a MethodOutcome object which contains
		// the ID (composed of the type DiagnosticReport, the logical ID 3746, and the
		// version ID 1)
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDiagnosticReport.getId().toString()));

		return retVal;
	}
	
	 /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/DiagnosticReport/1
     * @param theId
     * @param theDiagnosticReport
     * @return
     */
    @Update
    public MethodOutcome updateDiagnosticReportById(@IdParam IdType theId, 
    										@ResourceParam DiagnosticReport theDiagnosticReport) {
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
		theDiagnosticReport.setMeta(meta);
    	// Update this DiagnosticReport to the database...
    	DafDiagnosticReport dafDiagnosticReport = service.updateDiagnosticReportById(id, theDiagnosticReport);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDiagnosticReport.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<DiagnosticReport> getDiagnosticReportForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<DiagnosticReport> diagnosticList = service.getDiagnosticReportForBulkData(patients, start, end);
		return diagnosticList;
	}
}
