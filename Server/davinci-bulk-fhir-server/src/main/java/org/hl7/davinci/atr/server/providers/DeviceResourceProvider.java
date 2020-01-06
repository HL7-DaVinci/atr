package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDevice;
import org.hl7.davinci.atr.server.service.DeviceService;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Device;
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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@Component
public class DeviceResourceProvider extends AbstractJaxRsResourceProvider<Device> {

	public static final String RESOURCE_TYPE = "Device";
	public static final String VERSION_ID = "1";
	
	@Autowired
	DeviceService service;

	public DeviceResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
     * The getResourceType method comes from IResourceProvider, and must
     * be overridden to indicate what type of resource this provider
     * supplies.
     */
	@Override
	public Class<Device> getResourceType() {
		return Device.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. The vread operation retrieves a specific version of a resource
	 * with a given ID. To support vread, simply add "version=true" to your @Read
	 * annotation. This means that the read method will support both "Read" and
	 * "VRead". The IdDt may or may not have the version populated depending on the
	 * client request. This operation retrieves a resource by ID. It has a single
	 * parameter annotated with the @IdParam annotation. Example URL to invoke this
	 * method: http://<server name>/<context>/fhir/Device/1/_history/4
	 * @param theId : Id of the Device
	 * @return : Object of Device information
	 */
	@Read(version = true)
	public Device readOrVread(@IdParam IdType theId) {
		int id;
		Device device;
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
			device = service.getDeviceByVersionId(id, theId.getVersionIdPart());

		} else {

			device = service.getDeviceById(id);
		}

		return device;
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
     * with the resource in the POST body): http://<server name>/<context>/fhir/Device
     * @param theDevice
     * @return
     */
    @Create
    public MethodOutcome createDevice(@ResourceParam Device theDevice) {
         
    	// Save this Device to the database...
    	DafDevice dafDevice = service.createDevice(theDevice);
     
		MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDevice.getId() + "", VERSION_ID));
  
		return retVal;
    }

    /**
     * The update  operation updates a specific resource instance (using its ID).
     * Update methods must be annotated with the @Update annotation, 
     * and have a parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Example URL to invoke this method (this would be invoked using an HTTP PUT, 
     * with the resource in the PUT body): 
     * http://<server name>/<context>/fhir/Device/1
     * @param theId
     * @param theDevice
     * @return
     */
    @Update
    public MethodOutcome updateDeviceById(@IdParam IdType theId, 
    										@ResourceParam Device theDevice) {
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
		theDevice.setMeta(meta);
    	// Update this Device to the database...
    	DafDevice dafDevice = service.updateDeviceById(id, theDevice);  
    	MethodOutcome retVal = new MethodOutcome();
		retVal.setId(new IdType(RESOURCE_TYPE, dafDevice.getId() + "", VERSION_ID));
		return retVal;
    }

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 * @param theServletRequest
	 * @param theId
	 * @param theIdentifier
	 * @param theStatus
	 * @param theCarrier
	 * @param theManufacturer
	 * @param theExpirationDate
	 * @param theLotNumber
	 * @param theSerialNumber
	 * @param theDeviceName
	 * @param theType
	 * @param theModelNumber
	 * @param thePatient
	 * @param theOwner
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public IBundleProvider search(javax.servlet.http.HttpServletRequest theServletRequest,

		@Description(shortDefinition = "The resource identity")
		@OptionalParam(name = Device.SP_RES_ID)
		StringAndListParam theId,

		@Description(shortDefinition = "An Device  identifier") 
		@OptionalParam(name = Device.SP_IDENTIFIER) 
		TokenAndListParam theIdentifier,

		@Description(shortDefinition = "Unique device identifier (UDI) assigned to device label or package")
		@OptionalParam(name = Device.SP_UDI_CARRIER) 
		StringAndListParam theCarrier,

		@Description(shortDefinition = "Status of the Device availability")
		@OptionalParam(name = Device.SP_STATUS) 
		TokenAndListParam theStatus,

		@Description(shortDefinition = "A name of the manufacturer")
		@OptionalParam(name = Device.SP_MANUFACTURER) 
		StringAndListParam theManufacturer,

		@Description(shortDefinition = "he date and time beyond which this device is no longer valid or should not be used")
		@OptionalParam(name = "expiration-date") 
		DateRangeParam theExpirationDate,

		@Description(shortDefinition = "Lot number assigned by the manufacturer") 
		@OptionalParam(name = "lot-number")
		StringAndListParam thelotNumber,

		@Description(shortDefinition = "The serial number assigned by the organization when the device was manufactured") 
		@OptionalParam(name = "serial-number") 
		StringAndListParam theSerialNumber,

		@Description(shortDefinition = "The type of deviceName")
		@OptionalParam(name = Device.SP_DEVICE_NAME)
		StringAndListParam theDeviceName,

		@Description(shortDefinition = "Technical endpoints providing access to services operated for the organization")
		@OptionalParam(name = Device.SP_TYPE) 
		TokenAndListParam theType,

		@Description(shortDefinition = "The model number for the device") 
		@OptionalParam(name = "model-number")
		StringAndListParam theModelNumber,

		@Description(shortDefinition = "Patient information, If the device is affixed to a person")
		@OptionalParam(name = Device.SP_PATIENT)
		StringAndListParam thePatient,

		@Description(shortDefinition = "An organization that is responsible for the provision and ongoing maintenance of the device")
		@OptionalParam(name = "owner")
		StringAndListParam theOwner,

		@Sort SortSpec theSort,

		@Count Integer theCount) {
		
		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Device.SP_RES_ID, theId);
		paramMap.add(Device.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Device.SP_UDI_CARRIER, theCarrier);
		paramMap.add(Device.SP_STATUS, theStatus);
		paramMap.add(Device.SP_MANUFACTURER, theManufacturer);
		paramMap.add("expiration-date", theExpirationDate);
		paramMap.add("lot-number", thelotNumber);
		paramMap.add("serial-number", theSerialNumber);
		paramMap.add(Device.SP_DEVICE_NAME, theDeviceName);
		paramMap.add(Device.SP_TYPE, theType);
		paramMap.add(Device.SP_PATIENT, thePatient);
		paramMap.add("owner", theOwner);
		paramMap.add("model-number", theModelNumber);
		paramMap.setSort(theSort);
		paramMap.setCount(theCount);

		final List<Device> results = service.search(paramMap);

		return new IBundleProvider() {
			final InstantDt published = InstantDt.withCurrentTime();

			@Override
			public List<IBaseResource> getResources(int theFromIndex, int theToIndex) {
				List<IBaseResource> deviceList = new ArrayList<IBaseResource>();
				for (Device device : results) {
					deviceList.add(device);
				}
				return deviceList;
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
	
	public List<Device> getDeviceForBulkDataRequest(List<String> patients, Date start, Date end) {
		List<Device> deviceList = service.getDeviceForBulkData(patients, start, end);
		return deviceList;
	}
    
}