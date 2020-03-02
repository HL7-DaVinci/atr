package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.LocationDao;
import org.hl7.davinci.atr.server.model.DafLocation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("locationService")
@Transactional
public class LocationServiceImpl implements LocationService {

	public static final String RESOURCE_TYPE = "Location";
	
	@Autowired
	FhirContext fhirContext;

	@Autowired
	private LocationDao locationDao;

	@Override
	@Transactional
	public Location getLocationById(int id) {
		Location location = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafLocation dafLocation = locationDao.getLocationById(id);
		if(dafLocation != null) {
			location = jsonParser.parseResource(Location.class, dafLocation.getData());
			location.setId(location.getId());
		}
		return location;
	}

	@Override
	@Transactional
	public Location getLocationByVersionId(int theId, String versionId) {
		Location location = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafLocation dafLocation = locationDao.getLocationByVersionId(theId, versionId);
		if(dafLocation != null) {
			location = jsonParser.parseResource(Location.class, dafLocation.getData());
			location.setId(location.getId());
		}
		return location;
	}

	@Override
	@Transactional
	public List<Location> search(SearchParameterMap searchParameterMap) {
		Location location = null;
		List<Location> locationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafLocation> dafLocationList = locationDao.search(searchParameterMap);
		if(dafLocationList != null && !dafLocationList.isEmpty()) {
			for(DafLocation dafLocation : dafLocationList) {
				location = jsonParser.parseResource(Location.class, dafLocation.getData());
				location.setId(location.getId());
				locationList.add(location);
			}
		}
		return locationList;
	}

	@Override
	public DafLocation createLocation(Location theLocation) {
		return locationDao.createLocation(theLocation);
	}

	@Override
	public DafLocation updateLocationById(int id, Location theLocation) {
		return locationDao.updateLocationById(id, theLocation);
	}
	
	public List<Location> getLocationForBulkData(List<String> patients, Date start, Date end){
		Location location = null;
		List<Location> locationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafLocation> dafLocationList = locationDao.getLocationForBulkData(patients, start, end);
		if(dafLocationList != null && !dafLocationList.isEmpty()) {
			for(DafLocation dafLocation : dafLocationList) {
				location = jsonParser.parseResource(Location.class, dafLocation.getData());
				location.setId(location.getId());
				locationList.add(location);
			}
		}
		return locationList;
	}
}
