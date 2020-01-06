package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.DiagnosticReportDao;
import org.hl7.davinci.atr.server.model.DafDiagnosticReport;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("diagnosticReportService")
@Transactional
public class DiagnosticReportServiceImpl implements DiagnosticReportService {

	public static final String RESOURCE_TYPE = "DiagnosticReport";

	@Autowired
	private DiagnosticReportDao diagnosticReportDao;

	@Autowired
	FhirContext fhirContext;
	
	@Override
	@Transactional
	public DiagnosticReport getDiagnosticReportById(int id) {
		DiagnosticReport diagnosticReport = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDiagnosticReport dafDiagnosticReport = diagnosticReportDao.getDiagnosticReportById(id);
		if(dafDiagnosticReport != null) {
			diagnosticReport = jsonParser.parseResource(DiagnosticReport.class, dafDiagnosticReport.getData());
			diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReport.getId()));
		}
		return diagnosticReport;
	}

	@Override
	@Transactional
	public DiagnosticReport getDiagnosticReportByVersionId(int theId, String versionId) {
		DiagnosticReport diagnosticReport = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDiagnosticReport dafDiagnosticReport = diagnosticReportDao.getDiagnosticReportByVersionId(theId, versionId);
		if(dafDiagnosticReport != null) {
			diagnosticReport = jsonParser.parseResource(DiagnosticReport.class, dafDiagnosticReport.getData());
			diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReport.getId()));
		}
		return diagnosticReport;
	}

	@Override
	@Transactional
	public List<DiagnosticReport> search(SearchParameterMap paramMap) {
		DiagnosticReport diagnosticReport = null;
		List<DiagnosticReport> diagnosticReportList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDiagnosticReport> dafDiagnosticReportList = diagnosticReportDao.search(paramMap);
		if(dafDiagnosticReportList != null && !dafDiagnosticReportList.isEmpty()) {
			for(DafDiagnosticReport dafDiagnosticReport : dafDiagnosticReportList) {
				diagnosticReport = jsonParser.parseResource(DiagnosticReport.class, dafDiagnosticReport.getData());
				diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReport.getId()));
				diagnosticReportList.add(diagnosticReport);
			}
		}
		return diagnosticReportList;
	}

	@Override
	public DafDiagnosticReport createDiagnosticReport(DiagnosticReport theDiagnosticReport) {
		return diagnosticReportDao.createDiagnosticReport(theDiagnosticReport);
	}

	@Override
	public DafDiagnosticReport updateDiagnosticReportById(int theId, DiagnosticReport theDiagnosticReport) {
		return diagnosticReportDao.updateDiagnosticReportById(theId, theDiagnosticReport);
	}
	
	public List<DiagnosticReport> getDiagnosticReportForBulkData(List<String> patients, Date start, Date end){
		DiagnosticReport diagnosticReport = null;
		List<DiagnosticReport> diagnosticReportList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDiagnosticReport> dafDiagnosticReportList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafDiagnosticReport> dafDiagnosticReportObj = diagnosticReportDao.getDiagnosticReportForPatientsBulkData(id, start, end);
				dafDiagnosticReportList.addAll(dafDiagnosticReportObj);
			}
		}
		else {
			dafDiagnosticReportList = diagnosticReportDao.getDiagnosticReportForBulkData(start, end);
		}
		if(dafDiagnosticReportList != null && !dafDiagnosticReportList.isEmpty()) {
			for(DafDiagnosticReport dafDiagnosticReport : dafDiagnosticReportList) {
				diagnosticReport = jsonParser.parseResource(DiagnosticReport.class, dafDiagnosticReport.getData());
				diagnosticReport.setId(new IdType(RESOURCE_TYPE, diagnosticReport.getId()));
				diagnosticReportList.add(diagnosticReport);
			}
		}
		return diagnosticReportList;
    }
}
