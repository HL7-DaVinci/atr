package org.hl7.davinci.atr.server.service;

import org.hl7.davinci.atr.server.model.DafDiagnosticReport;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DiagnosticReport;

import java.util.Date;
import java.util.List;

public interface DiagnosticReportService {
	
	DiagnosticReport getDiagnosticReportById(int id);

	DiagnosticReport getDiagnosticReportByVersionId(int theId, String versionId);

	List<DiagnosticReport> search(SearchParameterMap theMap);
	
	DafDiagnosticReport createDiagnosticReport(DiagnosticReport theDiagnosticReport);
	
	DafDiagnosticReport updateDiagnosticReportById(int theId, DiagnosticReport theDiagnosticReport);
	
	List<DiagnosticReport> getDiagnosticReportForBulkData(List<String> patients, Date start, Date end);
}
