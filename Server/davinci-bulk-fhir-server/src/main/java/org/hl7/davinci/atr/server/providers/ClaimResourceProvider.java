package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.service.ClaimService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class ClaimResourceProvider extends AbstractJaxRsResourceProvider<Claim> {

	public static final String RESOURCE_TYPE = "Claim";
	public static final String VERSION_ID = "1";
	
	@Autowired
	ClaimService service;

	public ClaimResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}
	
	@Override
	public Class<Claim> getResourceType() {
		return Claim.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Claim/1/_history/4
	 * @param theId : Id of the Claim
	 * @return : Object of Claim information
	 */	
	@Read(version=true)
    public Claim readOrVread(@IdParam IdType theId) {
		int id;
		Claim claim;
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
			claim = service.getClaimByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
	       claim = service.getClaimById(id);
		}
		return claim;
    }
	
	public List<Claim> getClaimForBulkDataRequest(List<String> patientList, Date start, Date end) {
		List<Claim> claimList = service.getClaimForBulkDataRequest(patientList, start, end);
  		return claimList;
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

        @Description(shortDefinition = "The ID of the resource")
        @OptionalParam(name = Claim.SP_RES_ID)
        StringAndListParam theId,

        @Description(shortDefinition = "External Ids for this team")
        @OptionalParam(name = Claim.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "Who care team is for")
        @OptionalParam(name = Claim.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "The target payor/insurer for the Claim")
        @OptionalParam(name = Claim.SP_INSURER)
        ReferenceAndListParam theInsurer,

        @Description(shortDefinition="proposed | active | suspended | inactive | entered-in-error")
		@OptionalParam(name = Claim.SP_STATUS)
		TokenAndListParam theStatus, 
		
        @IncludeParam(allow = {"Claim.managingOrganization", "*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
		
        SearchParameterMap paramMap = new SearchParameterMap();
        paramMap.add(Claim.SP_RES_ID, theId);
        paramMap.add(Claim.SP_IDENTIFIER, theIdentifier);
        paramMap.add(Claim.SP_PATIENT, thePatient);
        paramMap.add(Claim.SP_INSURER, theInsurer);
        paramMap.add(Claim.SP_STATUS, theStatus);
        	            
        final List<Claim> results = service.search(paramMap);

        return new IBundleProvider() {
            final InstantDt published = InstantDt.withCurrentTime();
            @Override
            public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                List<IBaseResource> claimList = new ArrayList<>();
                for(Claim theClaim : results){
                	claimList.add(theClaim);
                }
                return claimList;
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

}
