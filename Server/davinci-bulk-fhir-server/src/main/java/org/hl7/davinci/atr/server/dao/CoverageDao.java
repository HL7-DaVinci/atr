package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;

public interface CoverageDao {

	List<DafCoverage> getCoverageForPatientsBulkData(String id, Date start, Date end);

	List<DafCoverage> getCoverageForBulkData(Date start, Date end);

	DafCoverage updateCoverageById(int id, Coverage theCoverage);

	DafCoverage createCoverage(Coverage theCoverage);

	DafCoverage getCoverageById(int theId);

	List<DafCoverage> search(SearchParameterMap paramMap);

	DafCoverage getCoverageByVersionId(int id, String versionIdPart);

}
