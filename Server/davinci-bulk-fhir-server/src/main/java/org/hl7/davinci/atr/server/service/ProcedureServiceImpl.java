package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.ProcedureDao;
import org.hl7.davinci.atr.server.model.DafProcedure;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("procedureService")
@Transactional
public class ProcedureServiceImpl implements ProcedureService {
	
	public static final String RESOURCE_TYPE = "Procedure";

	@Autowired
	private ProcedureDao procedureDao;

	@Autowired
	FhirContext fhirContext;

	@Override
    @Transactional
    public Procedure getProcedureById(int theId) {
		Procedure procedure = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafProcedure dafProcedure = procedureDao.getProcedureById(theId);
		if(dafProcedure != null) {
			procedure = jsonParser.parseResource(Procedure.class, dafProcedure.getData());
			procedure.setId(new IdType(RESOURCE_TYPE, procedure.getId()));
		}
		return procedure;
    }
	
	@Override
	@Transactional
	public Procedure getProcedureByVersionId(int theId, String versionId) {
		Procedure procedure = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafProcedure dafProcedure = procedureDao.getProcedureByVersionId(theId, versionId);
		if(dafProcedure != null) {
			procedure = jsonParser.parseResource(Procedure.class, dafProcedure.getData());
			procedure.setId(new IdType(RESOURCE_TYPE, procedure.getId()));
		}
		return procedure;
	}
	
	@Override
    @Transactional
    public List<Procedure> search(SearchParameterMap paramMap){
		Procedure procedure = null;
		List<Procedure> procedureList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafProcedure> dafProcedureList = procedureDao.search(paramMap);
		if(dafProcedureList != null && !dafProcedureList.isEmpty()) {
			for(DafProcedure dafProcedure : dafProcedureList) {
				procedure = jsonParser.parseResource(Procedure.class, dafProcedure.getData());
				procedure.setId(new IdType(RESOURCE_TYPE, procedure.getId()));
				procedureList.add(procedure);
			}
		}
		return procedureList;
    }

	@Override
	public DafProcedure createProcedure(Procedure theProcedure) {
		return procedureDao.createProcedure(theProcedure);
	}

	@Override
	public DafProcedure updateProcedureById(int theId, Procedure theProcedure ) {
		return procedureDao.updateProcedureById(theId, theProcedure);
	}
	
	@Override
	public List<Procedure> getProcedureForBulkData(List<String> patients, Date start, Date end){
		Procedure procedure = null;
		List<Procedure> procedureList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafProcedure> dafProcedureList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafProcedure> list = procedureDao.getProcedureForPatientsBulkData(id, start, end);
				dafProcedureList.addAll(list);
			}
		}
		else {
			dafProcedureList = procedureDao.getProcedureForBulkData(start, end);
		}
	 
		if(dafProcedureList != null && !dafProcedureList.isEmpty()) {
			for(DafProcedure dafProcedure : dafProcedureList) {
				procedure = jsonParser.parseResource(Procedure.class, dafProcedure.getData());
				procedure.setId(new IdType(RESOURCE_TYPE, procedure.getId()));
				procedureList.add(procedure);
			}
		}
		return procedureList;
	}
}
