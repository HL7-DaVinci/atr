package org.hl7.davinci.atr.server.dao;

import org.hl7.davinci.atr.server.model.DafDiagnosticReport;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DiagnosticReport;

import java.util.Date;
import java.util.List;

public interface DiagnosticReportDao {
	
	DafDiagnosticReport getDiagnosticReportById(int id);

	DafDiagnosticReport getDiagnosticReportByVersionId(int theId, String versionId);

	List<DafDiagnosticReport> search(SearchParameterMap theMap);
	
	DafDiagnosticReport createDiagnosticReport(DiagnosticReport theDiagnosticReport);
	
	DafDiagnosticReport updateDiagnosticReportById(int theId, DiagnosticReport theDiagnosticReport);
	
	List<DafDiagnosticReport> getDiagnosticReportForPatientsBulkData(String patientId, Date start, Date end);

	List<DafDiagnosticReport> getDiagnosticReportForBulkData(Date start, Date end);
}
