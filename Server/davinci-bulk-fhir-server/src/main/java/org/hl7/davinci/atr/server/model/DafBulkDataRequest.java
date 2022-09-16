package org.hl7.davinci.atr.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bulk_data_requests")
public class DafBulkDataRequest {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer requestId;
	
	@Column(name="resource_name")
	private String resourceName;
	
	@Column(name="resource_id")
	private String resourceId;
	
	@Column(name="request_resource")
	private String requestResource;
	
	@Column(name="start")
	private Date start;
	
	@Column(name="end_date")
	private Date end;
	
	@Column(name="_type")
	private String type;
	
	@Column(name="content_location")
	private String contentLocation;
	
	@Column(name="files")
	private String files;
	
	@Column(name="status")
	private String status;
	
	@Column(name="patient_list")
	private String patientList;
	
	@Column(name="operation_type")
	private String operationType;
	
	@Column(name="processed_flag")
	private Boolean processedFlag;

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getRequestResource() {
		return requestResource;
	}

	public void setRequestResource(String requestResource) {
		this.requestResource = requestResource;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date date) {
		this.start = date;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date date) {
		this.end = date;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getProcessedFlag() {
		return processedFlag;
	}

	public void setProcessedFlag(Boolean processedFlag) {
		this.processedFlag = processedFlag;
	}

	public String getPatientList() {
		return patientList;
	}

	public void setPatientList(String patientList) {
		this.patientList = patientList;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}	
}
