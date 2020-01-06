package org.hl7.davinci.atr.server.service;

import java.util.List;

import org.hl7.davinci.atr.server.model.DafBulkDataRequest;

public interface BulkDataRequestService {
	
	public DafBulkDataRequest saveBulkDataRequest(DafBulkDataRequest bdr);
	
	public DafBulkDataRequest getBulkDataRequestById(Integer id);
	
	public List<DafBulkDataRequest> getBulkDataRequestsByProcessedFlag(Boolean flag);
	
	public Integer deleteRequestById(Integer id);

}
