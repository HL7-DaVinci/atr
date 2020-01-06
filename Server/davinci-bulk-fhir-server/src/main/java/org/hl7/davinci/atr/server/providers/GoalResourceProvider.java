package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hl7.davinci.atr.server.model.DafGoal;
import org.hl7.davinci.atr.server.service.GoalService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Goal;
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
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class GoalResourceProvider extends AbstractJaxRsResourceProvider<Goal> {

	public static final String RESOURCE_TYPE = "Goal";
    public static final String VERSION_ID = "1";
    
    @Autowired
    GoalService service;
    
    public GoalResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
    /**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Goal> getResourceType() {
		return Goal.class;
	}
	
	/**
	 * The "@Read" annotation indicates that this method supports the read operation. 
	 * The vread operation retrieves a specific version of a resource with a given ID. To support vread, simply add "version=true" to your @Read annotation. 
	 * This means that the read method will support both "Read" and "VRead". 
	 * The IdDt may or may not have the version populated depending on the client request.
	 * This operation retrieves a resource by ID. It has a single parameter annotated with the @IdParam annotation.
	 * Example URL to invoke this method: http://<server name>/<context>/fhir/Goal/1/_history/3.0
	 * @param theId : Id of the Goal
	 * @return : Object of Goal information
	 */
	@Read(version=true)
    public Goal readOrVread(@IdParam IdType theId) {
		int id;
		Goal goal;
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
			goal = service.getGoalByVersionId(id, theId.getVersionIdPart());
		   
		} else {
		   // this is a read
			goal = service.getGoalById(id);
		}
		return goal;
    }

	/**
	 * The "@Search" annotation indicates that this method supports the search operation. 
	 * You may have many different method annotated with this annotation, to support many different search criteria.
	 * The search operation returns a bundle with zero-to-many resources of a given type, matching a given set of parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theTargetDate
	 * @param theCategory
	 * @param theStartDate
	 * @param theAchievementStatus
	 * @param theLifecycleStatus
	 * @param thePatient
	 * @param theSubject
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
    public IBundleProvider search(
        javax.servlet.http.HttpServletRequest theServletRequest,

        @Description(shortDefinition = "The resource identity")
        @OptionalParam(name = Goal.SP_RES_ID)
        TokenAndListParam theId,

        @Description(shortDefinition = "A goal identifier")
        @OptionalParam(name = Goal.SP_IDENTIFIER)
        TokenAndListParam theIdentifier,
        
        @Description(shortDefinition = "Reach goal on or before")
        @OptionalParam(name = Goal.SP_TARGET_DATE)
        DateRangeParam theTargetDate,
        
        @Description(shortDefinition = "E.g. Treatment, dietary, behavioral, etc.")
        @OptionalParam(name = Goal.SP_CATEGORY)
        TokenAndListParam theCategory,
        
        @Description(shortDefinition = "When goal pursuit begins")
        @OptionalParam(name = Goal.SP_START_DATE)
        DateRangeParam theStartDate,
        
        @Description(shortDefinition = "in-progress | improving | worsening | no-change | achieved | sustaining | not-achieved | no-progress | not-attainable")
        @OptionalParam(name = Goal.SP_ACHIEVEMENT_STATUS)
        TokenAndListParam theAchievementStatus,
        
        @Description(shortDefinition = "proposed | planned | accepted | active | on-hold | completed | cancelled | entered-in-error | rejected")
        @OptionalParam(name = Goal.SP_LIFECYCLE_STATUS)
        TokenAndListParam theLifecycleStatus,
  
        @Description(shortDefinition = "Who this goal is intended for")
        @OptionalParam(name = Goal.SP_PATIENT)
        ReferenceAndListParam thePatient,
        
        @Description(shortDefinition = "Who this goal is intended for")
        @OptionalParam(name = Goal.SP_SUBJECT)
        ReferenceAndListParam theSubject,

        @IncludeParam(allow = {"*"})
        Set<Include> theIncludes,

        @Sort
        SortSpec theSort,

        @Count
        Integer theCount) {

            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add(Goal.SP_RES_ID, theId);
            paramMap.add(Goal.SP_IDENTIFIER, theIdentifier);
            paramMap.add(Goal.SP_TARGET_DATE, theTargetDate);
            paramMap.add(Goal.SP_CATEGORY, theCategory);
            paramMap.add(Goal.SP_START_DATE, theStartDate);
            paramMap.add(Goal.SP_ACHIEVEMENT_STATUS, theAchievementStatus);
            paramMap.add(Goal.SP_LIFECYCLE_STATUS, theLifecycleStatus);
            paramMap.add(Goal.SP_SUBJECT, theSubject);
            paramMap.add(Goal.SP_PATIENT, thePatient);

            paramMap.setIncludes(theIncludes);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);
            
            final List<Goal> results = service.search(paramMap);

            return new IBundleProvider() {
                final InstantDt published = InstantDt.withCurrentTime();
                @Override
                public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
                    List<IBaseResource> goalList = new ArrayList<>();
                    for(Goal theGoal : results){
                    	goalList.add(theGoal);
                    }
                    return goalList;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Goal
     * @param theGoal
     * @return
     */
    @Create
    public MethodOutcome createGoal(@ResourceParam Goal theGoal) {
         
    	// Save this Goal to the database...
    	DafGoal dafGoal = service.createGoal(theGoal);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafGoal.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Goal/1
     * @param theId
     * @param theGoal
     * @return
     */
    @Update
    public MethodOutcome updateGoalById(@IdParam IdType theId, 
    										@ResourceParam Goal theGoal) {
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
		theGoal.setMeta(meta);
    	// Update this Goal to the database...
    	DafGoal dafGoal = service.updateGoalById(id, theGoal);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafGoal.getId() + "", VERSION_ID));
		return retVal;
    }
    
    public List<Goal> getGoalsForBulkDataRequest(List<String> patients, Date start, Date end) {
    	
		List<Goal> goalsList = service.getGoalsForBulkData(patients, start, end);
		return goalsList;
	}
}
