package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDevice;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Device;

public interface DeviceService {
	
	Device getDeviceById(int id);
	
	Device getDeviceByVersionId(int theId, String versionId);
		
	List<Device> search(SearchParameterMap theMap);
	
	DafDevice updateDeviceById(int id, Device theDevice);

	DafDevice createDevice(Device theDevice);
	
	List<Device> getDeviceForBulkData(List<String> patients, Date start, Date end);
}
