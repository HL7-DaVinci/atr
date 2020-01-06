package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.RelatedPersonDao;
import org.hl7.davinci.atr.server.model.DafRelatedPerson;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("relatedPersonService")
@Transactional
public class RelatedPersonServiceImpl implements RelatedPersonService {

	public static final String RESOURCE_TYPE = "RelatedPerson";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
	private RelatedPersonDao relatedPersonDao;

	@Override
	public List<RelatedPerson> search(SearchParameterMap paramMap) {
		RelatedPerson relatedPerson = null;
		List<RelatedPerson> relatedPersonList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafRelatedPerson> dafRelatedPersonList = relatedPersonDao.search(paramMap);
		if(dafRelatedPersonList != null && !dafRelatedPersonList.isEmpty()) {
			for(DafRelatedPerson dafRelatedPerson : dafRelatedPersonList) {
				relatedPerson = jsonParser.parseResource(RelatedPerson.class, dafRelatedPerson.getData());
				relatedPerson.setId(new IdType(RESOURCE_TYPE, relatedPerson.getId()));
				relatedPersonList.add(relatedPerson);
			}
		}
		return relatedPersonList;
	}

	@Override
	public RelatedPerson getRelatedPersonByVersionId(int id, String versionIdPart) {
		RelatedPerson relatedPerson = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafRelatedPerson dafRelatedPerson = relatedPersonDao.getRelatedPersonByVersionId(id, versionIdPart);
		if(dafRelatedPerson != null) {
			relatedPerson = jsonParser.parseResource(RelatedPerson.class, dafRelatedPerson.getData());
			relatedPerson.setId(new IdType(RESOURCE_TYPE, relatedPerson.getId()));
		}
		return relatedPerson;
	}

	@Override
	public RelatedPerson getRelatedPersonById(int theId) {
		RelatedPerson relatedPerson = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafRelatedPerson dafRelatedPerson = relatedPersonDao.getRelatedPersonById(theId);
		if(dafRelatedPerson != null) {
			relatedPerson = jsonParser.parseResource(RelatedPerson.class, dafRelatedPerson.getData());
			relatedPerson.setId(new IdType(RESOURCE_TYPE, relatedPerson.getId()));
		}
		return relatedPerson;
	}

	@Override
	public DafRelatedPerson createRelatedPerson(RelatedPerson theRelatedPerson) {
		return relatedPersonDao.createRelatedPerson(theRelatedPerson);
	}

	@Override
	public List<RelatedPerson> getRelatedPersonForBulkData(List<String> patients, Date start, Date end) {
		RelatedPerson relatedPerson = null;
		List<RelatedPerson> relatedPersonList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafRelatedPerson> dafRelatedPersonList = new ArrayList<>();
		if(patients != null) {
			for(String id:patients) {
				List<DafRelatedPerson> list = relatedPersonDao.getRelatedPersonForPatientsBulkData(id, start, end);
				dafRelatedPersonList.addAll(list);
			}
		}
		else {
			dafRelatedPersonList = relatedPersonDao.getRelatedPersonForBulkData(start, end);
		}
		if(dafRelatedPersonList != null && !dafRelatedPersonList.isEmpty()) {
			for(DafRelatedPerson dafRelatedPerson : dafRelatedPersonList) {
				relatedPerson = jsonParser.parseResource(RelatedPerson.class, dafRelatedPerson.getData());
				relatedPerson.setId(new IdType(RESOURCE_TYPE, relatedPerson.getId()));
				relatedPersonList.add(relatedPerson);
			}
		}
		return relatedPersonList;
	}

	@Override
	public DafRelatedPerson updateRelatedPersonById(int id, RelatedPerson theRelatedPerson) {
		return relatedPersonDao.updateRelatedPersonById(id, theRelatedPerson);
	}
}
