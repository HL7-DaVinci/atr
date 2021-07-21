package org.hl7.davinci.atr.server.providers;

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

import org.apache.commons.lang3.StringUtils;
import org.hl7.davinci.atr.server.model.DafBulkDataRequest;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.service.BulkDataRequestService;
import org.hl7.davinci.atr.server.service.GroupService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.parser.IParser;
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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

@Component
public class GroupResourceProvider extends AbstractJaxRsResourceProvider<Group> {

	private static final Logger logger = LoggerFactory.getLogger(GroupResourceProvider.class);
	public static final String RESOURCE_TYPE = "Group";
	public static final String VERSION_ID = "1";

	@Autowired
	GroupService service;

	@Autowired
	FhirContext fhirContext;

	@Autowired
	BulkDataRequestService bdrService;
	
	public GroupResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	@Override
	public Class<Group> getResourceType() {
		return Group.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Group/1
	 * 
	 * @param theId : Id of the Group
	 * @return : Object of Group information
	 */
	@Read(version=true)
	public Group readOrVread(@IdParam IdType theId) {
		Group group = null;
		DafGroup dafGroup = null;
		try {
			if (theId != null && theId.getIdPart() != null) {
				if (theId.hasVersionIdPart()) {
					 // this is a vread  
					dafGroup = new DafGroup();
					dafGroup = service.getGroupByVersionId(theId.getIdPart(), theId.getVersionIdPart());
				   
				} else {
				   // this is a read
					dafGroup = new DafGroup();
					dafGroup = service.getGroupById(theId.getIdPart());
				}
				IParser jsonParser = fhirContext.newJsonParser();
				if (dafGroup != null) {
					group = jsonParser.parseResource(Group.class, dafGroup.getData());
				}
			}
		} catch (Exception e) {
			logger.error("Exception in read of GroupResourceProvider ", e);
		}
		return group;
	}

	/**
	 * The create operation saves a new resource to the server, allowing the server
	 * to give that resource an ID and version ID. Create methods must be annotated
	 * with the @Create annotation, and have a single parameter annotated with
	 * the @ResourceParam annotation. This parameter contains the resource instance
	 * to be created. Create methods must return an object of type MethodOutcome .
	 * This object contains the identity of the created resource. Example URL to
	 * invoke this method (this would be invoked using an HTTP POST, with the
	 * resource in the POST body): http://<server name>/<context>/fhir/Group
	 * 
	 * @param theGroup
	 * @return
	 */
	@Create
	public MethodOutcome createGroup(@ResourceParam Group theGroup) {

		// Save this Group to the database...
		DafGroup dafGroup = service.createGroup(theGroup);
		Group group = parseGroup(dafGroup.getData());
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, group.getIdElement().getIdPart(),group.getMeta().getVersionId()));
		return retVal;
	}

	/**
	 * The update operation updates a specific resource instance (using its ID).
	 * Update methods must be annotated with the @Update annotation, and have a
	 * parameter annotated with the @ResourceParam annotation. This parameter
	 * contains the resource instance to be created. Example URL to invoke this
	 * method (this would be invoked using an HTTP PUT, with the resource in the PUT
	 * body): http://<server name>/<context>/fhir/Group/1
	 * 
	 * @param theId
	 * @param theGroup
	 * @return
	 */
	@Update
	public MethodOutcome updateGroupById(@IdParam IdType theId, @ResourceParam Group theGroup) {
		int id;
		try {
			id = theId.getIdPartAsLong().intValue();
		} catch (NumberFormatException e) {
			/*
			 * If we can't parse the ID as a long, it's not valid so this is an unknown
			 * resource
			 */
			throw new ResourceNotFoundException(theId);
		}

		Meta meta = new Meta();
		meta.setVersionId("1");
		Date date = new Date();
		meta.setLastUpdated(date);
		theGroup.setMeta(meta);
		// Update this Group to the database...
		DafGroup dafGroup = service.updateGroupById(id, theGroup);
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafGroup.getId() + "", VERSION_ID));
		return retVal;
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
	 * @param theName
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(javax.servlet.http.HttpServletRequest theServletRequest,

			@Description(shortDefinition = "The resource identity") @OptionalParam(name = Group.SP_RES_ID) StringAndListParam theId,

			@Description(shortDefinition = "Group Name") @OptionalParam(name = "name") StringParam theName,

			@Description(shortDefinition = "A patient identifier") @OptionalParam(name = Group.SP_IDENTIFIER) TokenAndListParam theIdentifier,
			
			@Description(shortDefinition = "Search by Group.period") @OptionalParam(name = "period") DateRangeParam dateParam,

			@IncludeParam(allow = { "*" }) Set<Include> theIncludes,

			@Sort SortSpec theSort,

			@Count Integer theCount) {
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Group.SP_RES_ID, theId);
		paramMap.add("name", theName);
		paramMap.add(Group.SP_IDENTIFIER, theIdentifier);
		paramMap.setIncludes(theIncludes);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<Group> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> grouptList = new ArrayList<>();
				for (Group group : results) {
					grouptList.add(group);
				}
				return grouptList;
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

	@Operation(name = "$export", idempotent = true)
	public Binary patientTypeOperation(@IdParam IdType groupId,
			@OperationParam(name = "_since") DateRangeParam theStart,
			// @OperationParam(name = "end") DateDt theEnd,
			@OperationParam(name = "_type") String type, RequestDetails requestDetails, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Binary retVal = new Binary();
		if (requestDetails.getHeader("Prefer") != null && requestDetails.getHeader("Accept") != null) {
			if (requestDetails.getHeader("Prefer").equals("respond-async")
					&& requestDetails.getHeader("Accept").equals("application/fhir+json")) {
				String resourceId = groupId.getIdPart();
				Date start = null;
				DafBulkDataRequest bdr = new DafBulkDataRequest();

				if (theStart != null && theStart.getLowerBound() != null) {
					bdr.setStart(theStart.getLowerBound().getValueAsString());
				}
				if (theStart != null && theStart.getUpperBound() != null) {
					bdr.setEnd(theStart.getUpperBound().getValueAsString());
				}

				bdr.setResourceName("Group");
				bdr.setResourceId(resourceId);
				bdr.setStatus("Accepted");
				bdr.setProcessedFlag(false);
				bdr.setType(type);
				bdr.setRequestResource(request.getRequestURL().toString());

				DafBulkDataRequest responseBDR = bdrService.saveBulkDataRequest(bdr);

				String uri = request.getScheme() + "://" + request.getServerName()
						+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
								|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
										: ":" + request.getServerPort())
						+ request.getContextPath();

				response.setStatus(202);
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(new Date());
				cal.setTimeZone(TimeZone.getTimeZone("GMT"));
				cal.add(Calendar.DATE, 10);
				// HTTP header date format: Thu, 01 Dec 1994 16:00:00 GMT
				String o = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format(cal.getTime());
				System.out.println(o);
				response.setHeader("Expires", o);
				response.setHeader("Content-Location", uri + "/bulkdata/" + responseBDR.getRequestId());

				retVal.setContentType("application/json+fhir");
				return retVal;
			} else {
				throw new UnprocessableEntityException("Invalid header values!");
			}
		} else {
			throw new UnprocessableEntityException("Prefer or Accepted Header is missing!");
		}
	}

	@Operation(name = "$member-add", idempotent = false,manualResponse=true)
	public MethodOutcome groupMemberAddTypeOperation(@IdParam IdType groupId, @ResourceParam Parameters theParameters,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		MethodOutcome retVal = new MethodOutcome();
		if (request.getHeader("Content-Type") != null
				&& request.getHeader("Content-Type").equals("application/fhir+json")) {
			DafGroup updatedGroup = service.processAddMemberToGroup(theParameters, groupId.getIdPart());
			if (updatedGroup != null) {
				Group group = (Group) fhirContext.newJsonParser().parseResource(updatedGroup.getData());
				response.setStatus(201);
				retVal.setId(new IdType("Group", group.getIdElement().getIdPart(),
						group.getMeta().getVersionId()));
			}
		} else {
			throw new UnprocessableEntityException("Invalid header values!");
		}
		return retVal;
	}

	@Operation(name = "$member-remove", idempotent = false,manualResponse=true)
	public MethodOutcome groupMemberRemoveTypeOperation(@IdParam IdType groupId,
			@ResourceParam Parameters theParameters, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MethodOutcome retVal = new MethodOutcome();
		if (request.getHeader("Content-Type") != null
				&& request.getHeader("Content-Type").equals("application/fhir+json")) {
			DafGroup updatedGroup = service.processRemoveMemberToGroup(theParameters,groupId.getIdPart());
			if (updatedGroup != null) {
				Group group = (Group) fhirContext.newJsonParser().parseResource(updatedGroup.getData());
				retVal.setId(new IdType("Group", group.getIdElement().getIdPart(),
						group.getMeta().getVersionId()));
			}
		} else {
			throw new UnprocessableEntityException("Invalid header values!");
		}
		return retVal;
	}
	
	private Group parseGroup(String data) {
		Group group = null;
		try {
			if (StringUtils.isNotBlank(data)) {
				IParser jsonParser = fhirContext.newJsonParser();
				group = jsonParser.parseResource(Group.class, data);
			}
		} catch (Exception e) {
			logger.error("Exception in parseGroup of GroupResourceProvider ", e);
		}
		return group;
	}
}