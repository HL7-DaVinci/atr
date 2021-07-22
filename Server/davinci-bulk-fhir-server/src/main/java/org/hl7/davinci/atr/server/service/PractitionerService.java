package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafPractitioner;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.Practitioner;

public interface PractitionerService {

	Practitioner getPractitionerByVersionId(String id, String versionIdPart);

	Practitioner getPractitionerById(String id);

	Practitioner createPractitioner(Practitioner thePatient);

	public DafPractitioner updatePractitionerById(int id, Practitioner thePatient);

	List<Practitioner> search(SearchParameterMap paramMap);

	List<Practitioner> getPractitionerForBulkData(List<String> patients, Date start, Date end);

	Practitioner getPractitionerByProviderNpi(String provideSystem, String providerNpi);
}
