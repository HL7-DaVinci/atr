package org.hl7.davinci.atr.server.service;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDocumentReference;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DocumentReference;

public interface DocumentReferenceService {
	
	DocumentReference getDocumentReferenceById(int id);
	
	DocumentReference getDocumentReferenceByVersionId(int theId, String versionId);
		
	List<DocumentReference> search(SearchParameterMap paramMap);
	
	DafDocumentReference createDocumentReference(DocumentReference theDocumentReference);
	
	DafDocumentReference updateDocumentReferenceById(int theId, DocumentReference theDocumentReference);

	List<DocumentReference> getDocumentReferenceForBulkData(List<String> patients, Date start, Date end);
}
