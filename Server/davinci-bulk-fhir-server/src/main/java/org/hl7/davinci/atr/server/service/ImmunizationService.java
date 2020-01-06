package org.hl7.davinci.atr.server.service;

import org.hl7.davinci.atr.server.model.DafImmunization;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Immunization;

import java.util.Date;
import java.util.List;

public interface ImmunizationService {
	
	Immunization getImmunizationById(int id);
	 
	Immunization getImmunizationByVersionId(int theId, String versionId);
	 
	List<Immunization> search(SearchParameterMap theMap);
	 
	DafImmunization createImmunization(Immunization theImmunization);
	 
	DafImmunization updateImmunizationById(int theId, Immunization theImmunization);
	
	List<Immunization> getImmunizationForBulkData(List<String> patients, Date start, Date end);
}
