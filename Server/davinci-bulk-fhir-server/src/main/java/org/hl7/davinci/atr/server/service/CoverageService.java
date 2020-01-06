package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;

public interface CoverageService {

	Coverage getCoverageByVersionId(int id, String versionIdPart);

	Coverage getCoverageById(int id);

	List<Coverage> search(SearchParameterMap paramMap);

	DafCoverage createCoverage(Coverage theCoverage);

	DafCoverage updateCoverageById(int id, Coverage theCoverage);

	List<Coverage> getCoverageForBulkData(List<String> patients, Date start, Date end);

}
