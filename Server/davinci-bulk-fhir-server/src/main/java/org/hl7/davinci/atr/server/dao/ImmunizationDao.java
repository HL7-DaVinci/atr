package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafImmunization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Immunization;

public interface ImmunizationDao {
	
	 DafImmunization getImmunizationById(int id);
	 
	 DafImmunization getImmunizationByVersionId(int theId, String versionId);
	 
	 List<DafImmunization> search(SearchParameterMap theMap);
	 
	 DafImmunization createImmunization(Immunization theImmunization);
	 
	 DafImmunization updateImmunizationById(int theId, Immunization theImmunization);
	 
	 List<DafImmunization> getImmunizationForBulkData(Date start, Date end);

	List<DafImmunization> getImmunizationForPatientsBulkData(String id, Date start, Date end);
}
