package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;

public interface CoverageService {

	Coverage getCoverageByVersionId(String id, String versionIdPart);

	Coverage getCoverageById(String id);

	List<Coverage> search(SearchParameterMap paramMap);

	Coverage createCoverage(Coverage theCoverage);

	DafCoverage updateCoverageById(int id, Coverage theCoverage);

	List<Coverage> getCoverageForBulkData(List<String> patients, Date start, Date end);

	Coverage getCoverageByIdentifier(String system, String value);

	Coverage getCoverageByPatientReference(String id);
}
