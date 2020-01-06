package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDevice;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Device;

public interface DeviceDao {
	
	DafDevice getDeviceById(int id);
	
	DafDevice getDeviceByVersionId(int theId, String versionId);
		
	List<DafDevice> search(SearchParameterMap theMap);
	
	DafDevice updateDeviceById(int id, Device theDevice);

	DafDevice createDevice(Device theDevice);
	
	List<DafDevice> getDeviceForBulkData(Date start, Date end);

	List<DafDevice> getDeviceForPatientsBulkData(String patientId, Date start, Date end);
}
