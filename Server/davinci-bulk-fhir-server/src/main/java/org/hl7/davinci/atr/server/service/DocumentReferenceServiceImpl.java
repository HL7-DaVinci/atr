package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.davinci.atr.server.dao.DocumentReferenceDao;
import org.hl7.davinci.atr.server.model.DafDocumentReference;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@Service("DocumentReferenceService")
@Transactional
public class DocumentReferenceServiceImpl implements DocumentReferenceService {
	
	public static final String RESOURCE_TYPE = "DocumentReference";
	
	@Autowired
	FhirContext fhirContext;
	
	@Autowired
    private DocumentReferenceDao documentReferenceDao;
	
	@Override
    @Transactional
    public DocumentReference getDocumentReferenceById(int id) {
		DocumentReference documentReference = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDocumentReference dafDocumentReference = documentReferenceDao.getDocumentReferenceById(id);
		if(dafDocumentReference != null) {
			documentReference = jsonParser.parseResource(DocumentReference.class, dafDocumentReference.getData());
			documentReference.setId(new IdType(RESOURCE_TYPE, documentReference.getId()));
		}
		return documentReference;
    }
	
	@Override
	@Transactional
	public DocumentReference getDocumentReferenceByVersionId(int theId, String versionId) {
		DocumentReference documentReference = null;
		IParser jsonParser = fhirContext.newJsonParser();
		DafDocumentReference dafDocumentReference = documentReferenceDao.getDocumentReferenceByVersionId(theId, versionId);
		if(dafDocumentReference != null) {
			documentReference = jsonParser.parseResource(DocumentReference.class, dafDocumentReference.getData());
			documentReference.setId(new IdType(RESOURCE_TYPE, documentReference.getId()));
		}
		return documentReference;	
	}
	
	@Override
    @Transactional
    public List<DocumentReference> search(SearchParameterMap paramMap){
		DocumentReference documentReference = null;
		List<DocumentReference> documentReferenceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDocumentReference> dafDocumentReferenceList = documentReferenceDao.search(paramMap);
		if(dafDocumentReferenceList != null && !dafDocumentReferenceList.isEmpty()) {
			for(DafDocumentReference dafDocumentReference : dafDocumentReferenceList) {
				documentReference = jsonParser.parseResource(DocumentReference.class, dafDocumentReference.getData());
				documentReference.setId(new IdType(RESOURCE_TYPE, documentReference.getId()));
				documentReferenceList.add(documentReference);
			}
		}
		return documentReferenceList;
    }

   @Override
   public DafDocumentReference createDocumentReference(DocumentReference theDocumentReference) {
	   return documentReferenceDao.createDocumentReference(theDocumentReference);
   }
	
   @Override
   public DafDocumentReference updateDocumentReferenceById(int theId, DocumentReference theDocumentReference) {
	   return documentReferenceDao.updateDocumentReferenceById(theId, theDocumentReference);
   }
   
   @Override
   public List<DocumentReference> getDocumentReferenceForBulkData(List<String> patients, Date start, Date end){
	   DocumentReference documentReference = null;
		List<DocumentReference> documentReferenceList = new ArrayList<>();
		IParser jsonParser = fhirContext.newJsonParser();
		List<DafDocumentReference> dafDocumentReferenceList = new ArrayList<>();
		if(patients != null) {
			for(String id : patients) {
				List<DafDocumentReference> dafDocumentReferenceObj =  documentReferenceDao.getDocumentReferenceForPatientsBulkData(id, start, end);
				dafDocumentReferenceList.addAll(dafDocumentReferenceObj);
			}
		}
		else {
			dafDocumentReferenceList = documentReferenceDao.getDocumentReferenceForBulkData(start, end);
		}
		if(dafDocumentReferenceList != null && !dafDocumentReferenceList.isEmpty()) {
			for(DafDocumentReference dafDocumentReference : dafDocumentReferenceList) {
				documentReference = jsonParser.parseResource(DocumentReference.class, dafDocumentReference.getData());
				documentReference.setId(new IdType(RESOURCE_TYPE, documentReference.getId()));
				documentReferenceList.add(documentReference);
			}
		}
		return documentReferenceList;
   }
}
