package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafRelatedPerson;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.RelatedPerson;

public interface RelatedPersonService {

	List<RelatedPerson> search(SearchParameterMap paramMap);

	RelatedPerson getRelatedPersonByVersionId(int id, String versionIdPart);

	RelatedPerson getRelatedPersonById(int id);

	DafRelatedPerson createRelatedPerson(RelatedPerson theRelatedPerson);

	List<RelatedPerson> getRelatedPersonForBulkData(List<String> patients, Date start, Date end);

	DafRelatedPerson updateRelatedPersonById(int id, RelatedPerson theRelatedPerson);

}
