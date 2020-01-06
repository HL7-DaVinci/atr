package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hl7.davinci.atr.server.dao.DeviceDao;
import org.hl7.davinci.atr.server.model.DafDevice;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("deviceService")
@Transactional
public  class DeviceServiceImpl implements DeviceService{
	
	public static final String RESOURCE_TYPE = "Device";

	@Autowired
    private DeviceDao deviceDao;

	@Autowired
	FhirContext fhirContext;
	
	@Override
    @Transactional
    public Device getDeviceById(int theId) {
		Device device = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDevice dafDevice = deviceDao.getDeviceById(theId);
		if(dafDevice != null) {
			device = jsonParser.parseResource(Device.class, dafDevice.getData());
			device.setId(new IdType(RESOURCE_TYPE, device.getId()));
		}
		return device;
    }
	
	@Override
	@Transactional
	public Device getDeviceByVersionId(int theId, String versionId) {
		Device device = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDevice dafDevice = deviceDao.getDeviceByVersionId(theId, versionId);
		if(dafDevice != null) {
			device = jsonParser.parseResource(Device.class, dafDevice.getData());
			device.setId(new IdType(RESOURCE_TYPE, device.getId()));
		}
		return device;
	}

	@Override
	@Transactional
	public List<Device> search(SearchParameterMap theMap) {
		Device device = null;
		List<Device> deviceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDevice> dafDeviceList = deviceDao.search(theMap);
		if(dafDeviceList != null && !dafDeviceList.isEmpty()) {
			for(DafDevice dafDevice : dafDeviceList) {
				device = jsonParser.parseResource(Device.class, dafDevice.getData());
				device.setId(new IdType(RESOURCE_TYPE, device.getId()));
				deviceList.add(device);
			}
		}
		return deviceList;
	}
	
	@Override
	public DafDevice updateDeviceById(int id, Device theDevice) {
		return this.deviceDao.updateDeviceById(id, theDevice);
	}
	
	@Override
	public DafDevice createDevice(Device theDevice) {
		return this.deviceDao.createDevice(theDevice);
	}
	
	@Override
    @Transactional
    public List<Device> getDeviceForBulkData(List<String> patients, Date start, Date end){
		Device device = null;
		List<Device> deviceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDevice> dafDeviceList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafDevice> dafDeviceObj = deviceDao.getDeviceForPatientsBulkData(id, start, end);
				dafDeviceList.addAll(dafDeviceObj);
			}
		}
		else {
			dafDeviceList = deviceDao.getDeviceForBulkData(start, end);

		}
		if(dafDeviceList != null && !dafDeviceList.isEmpty()) {
			for(DafDevice dafDevice : dafDeviceList) {
				device = jsonParser.parseResource(Device.class, dafDevice.getData());
				device.setId(new IdType(RESOURCE_TYPE, device.getId()));
				deviceList.add(device);
			}
		}
		return deviceList;
    }
}