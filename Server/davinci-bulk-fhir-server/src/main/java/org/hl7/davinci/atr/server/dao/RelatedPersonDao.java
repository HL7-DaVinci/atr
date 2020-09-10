package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafRelatedPerson;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.RelatedPerson;

public interface RelatedPersonDao {

	DafRelatedPerson getRelatedPersonById(int theId);

	DafRelatedPerson getRelatedPersonByVersionId(int id, String versionIdPart);

	DafRelatedPerson createRelatedPerson(RelatedPerson theRelatedPerson);

	DafRelatedPerson updateRelatedPersonById(int id, RelatedPerson theRelatedPerson);

	List<DafRelatedPerson> search(SearchParameterMap paramMap);

	DafRelatedPerson getRelatedPersonForPatientsBulkData(String id, Date start, Date end);

	List<DafRelatedPerson> getRelatedPersonForBulkData(Date start, Date end);
}
