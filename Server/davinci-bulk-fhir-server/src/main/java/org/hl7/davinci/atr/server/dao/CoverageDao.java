package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;

public interface CoverageDao {

	DafCoverage getCoverageForPatientsBulkData(String patients, Date start, Date end);

	List<DafCoverage> getCoverageForBulkData(Date start, Date end);

	DafCoverage updateCoverageById(int id, Coverage theCoverage);

	Coverage createCoverage(Coverage theCoverage);

	DafCoverage getCoverageById(String theId);

	List<DafCoverage> search(SearchParameterMap paramMap);

	DafCoverage getCoverageByVersionId(String id, String versionIdPart);

	DafCoverage getCoverageByPatientReference(String patientMemberId);

	DafCoverage getCoverageByIdentifier(String system, String value);
}
