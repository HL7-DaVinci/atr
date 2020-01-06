package org.hl7.davinci.atr.server.dao;

import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.model.DafDocumentReference;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DocumentReference;

public interface DocumentReferenceDao {
	
	 DafDocumentReference getDocumentReferenceById(int id);
	 
	 DafDocumentReference getDocumentReferenceByVersionId(int theId, String versionId);
	 
	 List<DafDocumentReference> search(SearchParameterMap theMap);

	 DafDocumentReference createDocumentReference(DocumentReference theDocumentReference);
	 
	 DafDocumentReference updateDocumentReferenceById(int theId, DocumentReference theDocumentReference);
	 
	 List<DafDocumentReference> getDocumentReferenceForBulkData(Date start, Date end);

	List<DafDocumentReference> getDocumentReferenceForPatientsBulkData(String id, Date start, Date end);
}
