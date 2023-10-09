package org.hl7.davinci.atr.server.model;

import java.util.ArrayList;

public class BulkDataOutput {
	
	private String transactionTime;
	private String request;
	private String requiresAccessToken;
	ArrayList<BulkDataOutputInfo> output;
	
	public BulkDataOutput() {
		output = new ArrayList<BulkDataOutputInfo>();
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public ArrayList<BulkDataOutputInfo> getOutput() {
		return output;
	}

	public void setOutput(ArrayList<BulkDataOutputInfo> output) {
		this.output = output;
	}
	
	public void add(BulkDataOutputInfo bdoi) {
		output.add(bdoi);
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public String getRequiresAccessToken() {
		return requiresAccessToken;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public void setRequiresAccessToken(String requiresAccessToken) {
		this.requiresAccessToken = requiresAccessToken;
	}
}
