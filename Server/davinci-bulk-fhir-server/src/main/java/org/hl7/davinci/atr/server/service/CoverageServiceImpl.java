package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.CoverageDao;
import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("coverageService")
@Transactional
public class CoverageServiceImpl implements CoverageService{

	public static final String RESOURCE_TYPE = "Coverage";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
	private CoverageDao coverageDao;
	
	@Override
	public Coverage getCoverageByVersionId(int id, String versionIdPart) {
		Coverage coverage = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCoverage dafCoverage = coverageDao.getCoverageByVersionId(id, versionIdPart);
		if(dafCoverage != null) {
			coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
			coverage.setId(new IdType(RESOURCE_TYPE, coverage.getId()));
		}
		return coverage;
	}

	@Override
	public Coverage getCoverageById(int theId) {
		Coverage coverage = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCoverage dafCoverage = coverageDao.getCoverageById(theId);
		if(dafCoverage != null) {
			coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
			coverage.setId(new IdType(RESOURCE_TYPE, coverage.getId()));
		}
		return coverage;
	}

	@Override
	public List<Coverage> search(SearchParameterMap paramMap) {
		Coverage coverage = null;
		List<Coverage> CoverageList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCoverage> dafCoverageList = coverageDao.search(paramMap);
		if(dafCoverageList != null && !dafCoverageList.isEmpty()) {
			for(DafCoverage dafCoverage : dafCoverageList) {
				coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
				coverage.setId(new IdType(RESOURCE_TYPE, coverage.getId()));
				CoverageList.add(coverage);
			}
		}
		return CoverageList;
	}

	@Override
	public DafCoverage createCoverage(Coverage theCoverage) {
		return coverageDao.createCoverage(theCoverage);
	}

	@Override
	public DafCoverage updateCoverageById(int id, Coverage theCoverage) {
		return coverageDao.updateCoverageById(id, theCoverage);
	}

	@Override
	public List<Coverage> getCoverageForBulkData(List<String> patients, Date start, Date end) {
		Coverage coverage = null;
		List<Coverage> coverageList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafCoverage> dafCoverageList  = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafCoverage> dafCoverageObj = coverageDao.getCoverageForPatientsBulkData(id, start, end);
				dafCoverageList.addAll(dafCoverageObj);
			}
		}
		else {
			dafCoverageList = coverageDao.getCoverageForBulkData(start, end);
		}
		if(dafCoverageList != null && !dafCoverageList.isEmpty()) {
			for(DafCoverage dafCoverage : dafCoverageList) {
				coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
				coverage.setId(new IdType(RESOURCE_TYPE, coverage.getId()));
				coverageList.add(coverage);
			}
		}
		return coverageList;
	}
}
