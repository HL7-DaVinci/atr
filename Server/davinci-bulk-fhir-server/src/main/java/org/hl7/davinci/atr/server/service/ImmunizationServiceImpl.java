package org.hl7.davinci.atr.server.service;

import org.hl7.fhir.r4.model.Immunization;
import org.hl7.davinci.atr.server.dao.ImmunizationDao;
import org.hl7.davinci.atr.server.model.DafImmunization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("immunizationService")
@Transactional
public class ImmunizationServiceImpl implements ImmunizationService {
	
	public static final String RESOURCE_TYPE = "Immunization";
	
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private ImmunizationDao immunizationDao;
	
	@Override
    @Transactional
    public Immunization getImmunizationById(int id) {
		Immunization immunization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafImmunization dafImmunization = immunizationDao.getImmunizationById(id);
		if(dafImmunization != null) {
			immunization = jsonParser.parseResource(Immunization.class, dafImmunization.getData());
			immunization.setId(immunization.getId());
		}
		return immunization;
    }
	
	@Override
	@Transactional
	public Immunization getImmunizationByVersionId(int theId, String versionId) {
		Immunization immunization = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafImmunization dafImmunization = immunizationDao.getImmunizationByVersionId(theId, versionId);
		if(dafImmunization != null) {
			immunization = jsonParser.parseResource(Immunization.class, dafImmunization.getData());
			immunization.setId(immunization.getId());
		}
		return immunization;
	}
	
	@Override
    @Transactional
    public List<Immunization> search(SearchParameterMap paramMap){
		Immunization immunization = null;
		List<Immunization> immunizationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafImmunization> dafImmunizationList = immunizationDao.search(paramMap);
		if(dafImmunizationList != null && !dafImmunizationList.isEmpty()) {
			for(DafImmunization dafImmunization : dafImmunizationList) {
				immunization = jsonParser.parseResource(Immunization.class, dafImmunization.getData());
				immunization.setId(immunization.getId());
				immunizationList.add(immunization);
			}
		}
		return immunizationList;
    }

	@Override
	public DafImmunization createImmunization(Immunization theImmunization) {
		return immunizationDao.createImmunization(theImmunization);
	}
	
	@Override
	public DafImmunization updateImmunizationById(int theId, Immunization theImmunization) {
		return immunizationDao.updateImmunizationById(theId, theImmunization);
	}
	
	@Override
    @Transactional
    public List<Immunization> getImmunizationForBulkData(List<String> patients, Date start, Date end) {
		Immunization immunization = null;
		List<Immunization> immunizationList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafImmunization> dafImmunizationList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafImmunization> dafImmunizationObj = immunizationDao.getImmunizationForPatientsBulkData(id, start, end); 
				dafImmunizationList.addAll(dafImmunizationObj);
			}
		}
		else {
			dafImmunizationList = immunizationDao.getImmunizationForBulkData(start, end);
		}
		if(dafImmunizationList != null && !dafImmunizationList.isEmpty()) {
			for(DafImmunization dafImmunization : dafImmunizationList) {
				immunization = jsonParser.parseResource(Immunization.class, dafImmunization.getData());
				immunization.setId(immunization.getId());
				immunizationList.add(immunization);
			}
		}
		return immunizationList;
    }

}
