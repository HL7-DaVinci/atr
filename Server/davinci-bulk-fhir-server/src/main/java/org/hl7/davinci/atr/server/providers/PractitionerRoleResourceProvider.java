package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.service.PractitionerRoleService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
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
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class PractitionerRoleResourceProvider extends AbstractJaxRsResourceProvider<PractitionerRole> {

	public static final String RESOURCE_TYPE = "PractitionerRole";
    public static final String VERSION_ID = "4.0";
    
    @Autowired
    PractitionerRoleService service;
    
    public PractitionerRoleResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
	@Override
	public Class<PractitionerRole> getResourceType() {
		return PractitionerRole.class;
	}
	
	/**
	 * The "@Read" annotation indicate
	 * s that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/PractitionerRole/1/_history/4
	 * @param theId: ID of practitionerrole
	 * @return : PractitionerRole object
	 */
	@Read(version=true)
    public PractitionerRole readOrVread(@IdParam IdType theId) {
		int id;
		PractitionerRole practitionerRole;
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
			practitionerRole = service.getPractitionerRoleByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			practitionerRole = service.getPractitionerRoleById(id);
		}
		
		return practitionerRole;
    }
	
	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * 
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theTelecom
	 * @param theEmail
	 * @param theActive
	 * @param thePhone
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
    @Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = PractitionerRole.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A PractitionerRole identifier")
        @OptionalParam(name = PractitionerRole.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,

        @Description(shortDefinition = "The value in any kind of telecom details of the PractitionerRole")
        @OptionalParam(name = PractitionerRole.SP_TELECOM)
        StringAndListParam theTelecom,

        @Description(shortDefinition = "A value in an email contact.PractitionerRole.telecom.where(system='email')")
        @OptionalParam(name = PractitionerRole.SP_EMAIL)
        StringAndListParam theEmail,

        @Description(shortDefinition = "Whether the PractitionerRole record is active")
        @OptionalParam(name = PractitionerRole.SP_ACTIVE)
        TokenAndListParam theActive,

        @Description(shortDefinition = "A value in a phone contact. Path: PractitionerRole.telecom(system=phone)")
        @OptionalParam(name = PractitionerRole.SP_PHONE)
        StringAndListParam thePhone,
        
        @Description(shortDefinition = "Practitioner that is able to provide the defined services for the organization")
        @OptionalParam(name = PractitionerRole.SP_PRACTITIONER)
        ReferenceAndListParam thePractitioner,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {
            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(PractitionerRole.SP_RES_ID, theId);
            paramMap.add(PractitionerRole.SP_IDENTIFIER, theIdentifier);
            paramMap.add(PractitionerRole.SP_TELECOM, theTelecom);
            paramMap.add(PractitionerRole.SP_EMAIL, theEmail);
            paramMap.add(PractitionerRole.SP_ACTIVE, theActive);
            paramMap.add(PractitionerRole.SP_PRACTITIONER, thePractitioner);
            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<PractitionerRole> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> practitionerRoleList = new ArrayList<>();
                    for(PractitionerRole dafPractitionerRole : results){
                    	practitionerRoleList.add(dafPractitionerRole);
                    }
                    return practitionerRoleList;
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

	public List<PractitionerRole> getPractitionerRoleForBulkDataRequest(List<String> patientList, Date start,
			Date end) {
		List<PractitionerRole> docPracRoleList = service.getPractitionerRoleForBulkData(patientList, start, end);
		return docPracRoleList;
	}
}