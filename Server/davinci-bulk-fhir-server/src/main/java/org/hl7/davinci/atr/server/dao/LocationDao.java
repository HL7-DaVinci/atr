package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafLocation;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Location;

public interface LocationDao {

	DafLocation getLocationById(int id);

	DafLocation getLocationByVersionId(int theId, String versionId);

	List<DafLocation> search(SearchParameterMap searchParameterMap);

	DafLocation createLocation(Location theLocation);

	DafLocation updateLocationById(int id, Location theLocation);
	
	List<DafLocation> getLocationForBulkData(List<String> patients, Date start, Date end);
}
