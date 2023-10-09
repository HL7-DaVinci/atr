package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hl7.davinci.atr.server.dao.CoverageDao;
import org.hl7.davinci.atr.server.model.DafCoverage;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Coverage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("coverageService")
@Transactional
public class CoverageServiceImpl implements CoverageService{
	private static final Logger logger = LoggerFactory.getLogger(CoverageServiceImpl.class);    
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
	private CoverageDao coverageDao;
	
	@Override
	public Coverage getCoverageByVersionId(String id, String versionIdPart) {
		Coverage coverage = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCoverage dafCoverage = coverageDao.getCoverageByVersionId(id, versionIdPart);
		if(dafCoverage != null) {
			coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
			coverage.setId(coverage.getId());
		}
		return coverage;
	}

	@Override
	public Coverage getCoverageById(String theId) {
		Coverage coverage = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafCoverage dafCoverage = coverageDao.getCoverageById(theId);
		if(dafCoverage != null) {
			coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
			coverage.setId(coverage.getId());
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
				coverage.setId(coverage.getId());
				CoverageList.add(coverage);
			}
		}
		return CoverageList;
	}

	@Override
	public Coverage createCoverage(Coverage theCoverage) {
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
				DafCoverage dafCoverage = coverageDao.getCoverageForPatientsBulkData(id, start, end);
				dafCoverageList.add(dafCoverage);
			}
		}
		else {
			dafCoverageList = coverageDao.getCoverageForBulkData(start, end);
		}
		if(dafCoverageList != null && !dafCoverageList.isEmpty()) {
			for(DafCoverage dafCoverage : dafCoverageList) {
				coverage = jsonParser.parseResource(Coverage.class, dafCoverage.getData());
				coverage.setId(coverage.getId());
				coverageList.add(coverage);
			}
		}
		return coverageList;
	}

	@Override
	public Coverage getCoverageByPatientReference(String patientId) {
		Coverage coverage = null;
		try {
			DafCoverage dafCoverage = coverageDao.getCoverageByPatientReference(patientId);
			if(dafCoverage != null) {
				coverage = parseCoverage(dafCoverage.getData());
			}
		}
		catch(Exception e) {
			logger.error("Exception in getCoverageByPatientReference of CoverageServiceImpl ", e);
	  	}
		return coverage;
	}
	
	/**
     * Parses the string data to fhir Coverage resource data
     * @param data
     * @return
     */
    private Coverage parseCoverage(String data) {
    	Coverage coverage = null;
    	try {
			if(StringUtils.isNotBlank(data)) {
	    		IParser jsonParser = fhirContext.newJsonParser();
	    		coverage = jsonParser.parseResource(Coverage.class, data);
			}
    	}
  	  	catch(Exception e) {
  	  		logger.error("Exception in parseCoverage of CoverageServiceImpl ", e);
  	  	}
  	  	return coverage;
    }

	@Override
	public Coverage getCoverageByIdentifier(String system, String value) {
		Coverage coverage = null;
		try {
			DafCoverage dafCoverage = coverageDao.getCoverageByIdentifier(system, value);;
			if(dafCoverage != null) {
				coverage = parseCoverage(dafCoverage.getData());
			}
		}
		catch(Exception e) {
			logger.error("Exception in getCoverageByIdentifier of CoverageServiceImpl ", e);
	  	}
		return coverage;
	}
}
