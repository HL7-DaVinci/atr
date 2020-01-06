package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafLocation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Location;

public interface LocationService {

	Location getLocationById(int id);

	Location getLocationByVersionId(int theId, String versionId);

	List<Location> search(SearchParameterMap searchParameterMap);

	DafLocation createLocation(Location theLocation);

	DafLocation updateLocationById(int id, Location theLocation);
	
	List<Location> getLocationForBulkData(List<String> patients, Date start, Date end);
}
