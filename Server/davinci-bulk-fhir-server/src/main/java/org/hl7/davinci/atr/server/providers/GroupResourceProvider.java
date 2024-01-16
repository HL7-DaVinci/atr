package org.hl7.davinci.atr.server.providers;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.uhn.fhir.rest.param.*;
import org.apache.commons.lang3.StringUtils;
import org.hl7.davinci.atr.server.model.DafBulkDataRequest;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.service.BulkDataRequestService;
import org.hl7.davinci.atr.server.service.GroupService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
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
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnclassifiedServerFailureException;
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
    @Read(version = true)
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
                    List<Extension> ext = new ArrayList<>();
                    ext.add(new Extension().setUrl("https://example.org/fhir").setValue(new StringType().setValue("code")));
                    List<Group.GroupCharacteristicComponent> groupCharacteristicComponents = new ArrayList<>();
                    Group.GroupCharacteristicComponent  component = new Group.GroupCharacteristicComponent();
                    component.setExtension(ext);
                    groupCharacteristicComponents.add(component);
                    group.setCharacteristic(groupCharacteristicComponents);
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
        retVal.setId(new IdType(RESOURCE_TYPE, group.getIdElement().getIdPart(), group.getMeta().getVersionId()));
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

                                  @Description(shortDefinition = "A patient identifier with Type") @OptionalParam(name = Group.SP_IDENTIFIER + ":of-type") TokenAndListParam theIdentifierWithType,

                                  @Description(shortDefinition = "A patient identifier with Type") @OptionalParam(name = Group.SP_CHARACTERISTIC) TokenAndListParam theCharacteristic,

                                  @Description(shortDefinition = "A patient identifier with Composite Type") @OptionalParam(name = "Group-characteristic-value-reference", compositeTypes = {TokenParam.class, ReferenceParam.class}) CompositeParam<TokenParam, ReferenceParam> theCharacteristicValue,

                                  @Description(shortDefinition = "Search by Group.period") @OptionalParam(name = "period") DateRangeParam dateParam,

                                  @IncludeParam(allow = {"*"}) Set<Include> theIncludes,

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
                    && (requestDetails.getHeader("Accept").contains("application/fhir+json") || requestDetails.getHeader("Accept").contains("application/json"))) {
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
                logger.info("{}", o);
                response.setHeader("Expires", o);
                response.setHeader("Content-Location", uri + "/bulkdata/" + responseBDR.getRequestId());

                retVal.setContentType("application/json+fhir");
                return retVal;
            } else {
                //throw new UnprocessableEntityException("Invalid header values!");
                throw new UnclassifiedServerFailureException(400, "Invalid header values!");
            }
        } else {
            //throw new UnprocessableEntityException("Prefer or Accepted Header is missing!");
            throw new UnclassifiedServerFailureException(400, "Prefer or Accepted Header is missing!");
        }
    }

    @Operation(name = "$davinci-data-export", idempotent = true)
    public Binary patientTypeDataExport(@IdParam IdType groupId,
                                        @OperationParam(name = "_since") DateRangeParam theStart,
                                        // @OperationParam(name = "end") DateDt theEnd,
                                        @OperationParam(name = "exportType") String exportType,
                                        @OperationParam(name = "resourceTypes") String type, RequestDetails requestDetails, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        Binary retVal = new Binary();
        if (requestDetails.getHeader("Prefer") != null && requestDetails.getHeader("Accept") != null) {
            if (requestDetails.getHeader("Prefer").equals("respond-async")
                    && (requestDetails.getHeader("Accept").contains("application/fhir+json") || requestDetails.getHeader("Accept").contains("application/json"))) {
                if (exportType != null) {
                    if (exportType.equalsIgnoreCase("hl7.fhir.us.davinci-atr")) {
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
                        throw new UnclassifiedServerFailureException(400, "Provided exportType parameter is invalid!");
                    }
                } else {
                    throw new UnclassifiedServerFailureException(400, "exportType parameter is missing!");
                }
            } else {
                //throw new UnprocessableEntityException("Invalid header values!");
                throw new UnclassifiedServerFailureException(400, "Invalid header values!");
            }
        } else {
            //throw new UnprocessableEntityException("Prefer or Accepted Header is missing!");
            throw new UnclassifiedServerFailureException(400, "Prefer or Accepted Header is missing!");
        }
    }


    @Operation(name = "$member-add", idempotent = false, manualResponse = true)
    public MethodOutcome groupMemberAddTypeOperation(@IdParam IdType groupId, @ResourceParam Parameters theParameters,
                                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        MethodOutcome retVal = new MethodOutcome();
        if (request.getHeader("Content-Type") != null
                && request.getHeader("Content-Type").contains("application/fhir+json")) {
            DafGroup updatedGroup = service.processAddMemberToGroup(theParameters, groupId.getIdPart());
            if (updatedGroup != null) {
                Group group = (Group) fhirContext.newJsonParser().parseResource(updatedGroup.getData());
                response.setStatus(200);
                retVal.setId(new IdType("Group", group.getIdElement().getIdPart(),
                        group.getMeta().getVersionId()));
            }
        } else {
            throw new UnprocessableEntityException("Invalid header values!");
        }
        return retVal;
    }


    @Operation(name = "$confirm-attribution-list", idempotent = false, manualResponse = true)
    public MethodOutcome groupConfirmAttrList(@IdParam IdType groupId,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        MethodOutcome retVal = new MethodOutcome();
        DafGroup dafgroup = service.getGroupById(groupId.getIdPart().toString());
        if (dafgroup != null) {
            Group group = (Group) fhirContext.newJsonParser().parseResource(dafgroup.getData());
            logger.info("Resource Version::::{}", group.getMeta().getVersionId());
            List<Extension> extensionList = group.getExtension();
            for (Extension extension : extensionList) {
                if (extension.getUrl().equals("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/ext-attributionListStatus")) {
                    CodeType extCode = (CodeType) extension.getValue();
                    logger.info("Actual code value:::::{}", extCode.getCode());
                    extCode.setValue("final");
                }
            }
            List<Group.GroupMemberComponent> memberComponentList = new ArrayList<>();
            if(group.hasMember()){
                for(Group.GroupMemberComponent memberComponent:group.getMember()){
                    if(memberComponent.hasInactive()){
                        if(!memberComponent.getInactive()){
                            memberComponentList.add(memberComponent);
                        }
                    }
                }
            }
            group.getMember().clear();
            group.setMember(memberComponentList);
            if (group.hasMeta()) {
                if (group.getMeta().hasVersionId()) {
                    String versionId = group.getMeta().getVersionId();
                    int version = Integer.parseInt(versionId);
                    version = version + 1;
                    group.getMeta().setVersionId(String.valueOf(version));

                } else {
                    group.getMeta().setVersionId("1");
                }
            } else {
                Meta meta = new Meta();
                meta.setVersionId("1");
                group.setMeta(meta);
            }
            DafGroup dafGroup = service.createGroup(group);
            Group savedGroup = parseGroup(dafGroup.getData());
            response.setStatus(201);
            retVal.setId(new IdType("Group", savedGroup.getIdElement().getIdPart(),
                    savedGroup.getMeta().getVersionId()));
        } else {
            throw new ResourceNotFoundException("Group resource not found with the provided Id:::::{}" + groupId.getIdPart());
        }
        return retVal;
    }

    @Operation(name = "$member-remove", idempotent = false, manualResponse = true)
    public MethodOutcome groupMemberRemoveTypeOperation(@IdParam IdType groupId,
                                                        @ResourceParam Parameters theParameters, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        MethodOutcome retVal = new MethodOutcome();
        if (request.getHeader("Content-Type") != null
                && request.getHeader("Content-Type").contains("application/fhir+json")) {
            DafGroup updatedGroup = service.processRemoveMemberToGroup(theParameters, groupId.getIdPart());
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


    @Operation(name = "$attribution-status", idempotent = true)
    public Bundle getAttrStatus(@IdParam IdType groupId,
                                @ResourceParam Parameters theParameters, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Bundle retVal = new Bundle();
        retVal.setId(UUID.randomUUID().toString());
        retVal.setType(Bundle.BundleType.COLLECTION);
        if (request.getHeader("Content-Type") != null
                && request.getHeader("Content-Type").contains("application/fhir+json")) {
            retVal = service.processAttrStatus(theParameters, groupId.getIdPart());
            return retVal;
        } else {
            throw new UnprocessableEntityException("Invalid header values!");
        }
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