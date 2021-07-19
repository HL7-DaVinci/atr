package org.hl7.davinci.atr.server.providers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.davinci.atr.server.model.BulkDataOutput;
import org.hl7.davinci.atr.server.model.BulkDataOutputInfo;
import org.hl7.davinci.atr.server.model.DafBulkDataRequest;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.service.AsyncService;
import org.hl7.davinci.atr.server.service.BulkDataRequestService;
import org.hl7.davinci.atr.server.service.GroupService;
import org.hl7.davinci.atr.server.util.CommonUtil;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import ch.qos.logback.classic.Logger;

@Controller
@RequestMapping("/bulkdata")
public class BulkDataRequestProvider {
	
	Logger log = (Logger) LoggerFactory.getLogger(BulkDataRequestProvider.class);
	@Autowired
	FhirContext fhirContext;
	//private static int a=0;
	private static List<String> ndjsonFiles = new ArrayList<>();

	@Autowired
	private BulkDataRequestService bdrService;

	@Autowired
	FhirContext ctx;
	
	@Autowired
	private AsyncService service;
	
	@Autowired
	private GroupService groupService;
//
//	public void seta()
//	{
//		a+=1;
//	}
//	
	/**
	 * Set ndjsonFiles
	 * 
	 * @param fileName
	 */
	public void setNDJSONFile(String fileName) {
		ndjsonFiles.add(fileName);
	}

	@RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
	@ResponseBody
	public String getContentLocationResponse(@PathVariable Integer requestId, HttpServletRequest request,
			HttpServletResponse response) {
		String body = "";
		if(request.getHeader("Accept") != null && request.getHeader("Accept").equals("application/json")) {
			
			DafBulkDataRequest bdr = bdrService.getBulkDataRequestById(requestId);
			if (bdr != null) {

				if (bdr.getStatus().equalsIgnoreCase("In Progress")) {
					response.setHeader("X-Progress", "In Progress");
					response.setStatus(202);
				}

				if (bdr.getStatus().equalsIgnoreCase("Accepted")) {
					response.setHeader("X-Progress", "Accepted");
					response.setStatus(202);
				}
				if (bdr.getStatus().equalsIgnoreCase("Completed")) {

					BulkDataOutput bdo = new BulkDataOutput();

					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(new Date());
					cal.setTimeZone(TimeZone.getTimeZone("GMT"));
					cal.add(Calendar.DATE, 10);
					String dt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz").format(cal.getTime());
					bdo.setTransactionStartTime(dt);

					bdo.setRequest(bdr.getRequestResource());
					bdo.setSecure("false");

					String[] links = bdr.getFiles().split(",");
					StringBuilder linksHeader = new StringBuilder();
					String uri = request.getScheme() + "://" + request.getServerName()
					+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
					|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
							: ":" + request.getServerPort())
					+ request.getContextPath();

					for (int i = 0; i < links.length; i++) {

						if (links[i] != null && !links[i].equals("null")) {

							BulkDataOutputInfo bdoi = new BulkDataOutputInfo();

							String linkForBody = uri + "/bulkdata/download/" + bdr.getRequestId() + "/" + links[i];
							String l = "<" + uri + "/bulkdata/download/" + bdr.getRequestId() + "/" + links[i] + ">";
							bdoi.setUrl(linkForBody);
							bdo.add(bdoi);

							linksHeader.append(l);

							if (i < links.length - 1) {
								linksHeader.append(",");
							}
						}
					}
					Gson g = new Gson();
					body = g.toJson(bdo);

					response.setHeader("Link", linksHeader.toString());
					//response.setHeader("Content-Type", "application/json");
				}
			} else {
				response.setStatus(404);
				throw new ResourceNotFoundException(
						"The requested Content-Location was not found. Please contact the Admin.");
			}
		}	
		else {
			OperationOutcome oo = new OperationOutcome();
			oo.addIssue().setSeverity(org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity.ERROR)
			.setDiagnostics("Invalid header values!");
			body = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(oo);
			response.setStatus(422);
			//throw new UnprocessableEntityException(body);
		}
		return body;
	}

	@RequestMapping(value = "/download/{id}/{fileName:.+}", method = RequestMethod.GET)
	@ResponseBody
	public int downloadFile(@PathVariable Integer id, @PathVariable String fileName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("Received request to download the file");
		if(request.getHeader("Accept") != null && request.getHeader("Accept").equals("application/fhir+ndjson")) {
			String contextPath = System.getProperty("catalina.base");
			String destDir = contextPath + "/bulkdata/" + id + "/";
			return CommonUtil.downloadFIleByName(new File(destDir + fileName), response);
		}	
		else {
			throw new UnprocessableEntityException("Invalid header values!");
		}
	}

	// delete content-location request
	@RequestMapping(value = "/{requestId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteContentRequest(@PathVariable Integer requestId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Integer res = bdrService.deleteRequestById(requestId);

		// delete folder with files
		if (res > 0) {
			String contextPath = System.getProperty("catalina.base");
			String destDir = contextPath + "/bulkdata/" + requestId + "/";
			File directory = new File(destDir);
			if (directory.exists()) {
				FileUtils.deleteDirectory(directory);
			}
		}
		log.info("Numer of records effected due to content-location delete : " + res +" for request id : "+requestId);

		response.setStatus(202);

	}

	@RequestMapping(value = "/load/request/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void loadRequestById(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {

		DafBulkDataRequest bdr = bdrService.getBulkDataRequestById(id);

		processBulkDataRequest(bdr);

	}

	@Scheduled(cron = "*/5 * * * * ?")
	public void processBulkDataRequestSchedular() {

		System.out.println("Schedular checking for pending requests...!");

		List<DafBulkDataRequest> requests = bdrService.getBulkDataRequestsByProcessedFlag(false);
		try {
			for (DafBulkDataRequest bdr : requests) {

				long startTime = System.nanoTime();
				log.info("request with id : "+bdr.getRequestId() +" is processing...  start time: "+startTime);

				processBulkDataRequest(bdr);


				long endTime   = System.nanoTime();
				long totalTime = endTime - startTime;
				log.info("request with id : "+bdr.getRequestId() +" - processing completed. The total time is : '"+totalTime+"' in nano seconds");

			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void processBulkDataRequest(DafBulkDataRequest bdr) throws IOException, InterruptedException {

		List<String> patientList = null;
		List<String> coverageList = null;
		List<String> organizationList = null;
		List<String> practitionerList = null;
		List<String> practitionerRoleList = null;
		Date start = null;
		Date end = null;
		if (bdr.getStart() != null) {
			DateDt dateDt = new DateDt();
			dateDt.setValueAsString(bdr.getStart());
			start = dateDt.getValue();
		}
		if (bdr.getEnd() != null) {
			DateDt endDateDt = new DateDt(); 
			endDateDt.setValueAsString(bdr.getEnd());
			end = endDateDt.getValue();
		}

		String contextPath = System.getProperty("catalina.base");
		File destDir = new File(contextPath + "/bulkdata/" + bdr.getRequestId() + "/");
		bdr.setStatus("In Progress");
		bdr.setProcessedFlag(true);
		bdrService.saveBulkDataRequest(bdr);

		//List<String> files = new ArrayList<String>();

		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		if (bdr.getResourceName() != null && bdr.getResourceName().equalsIgnoreCase("GROUP")) {

			DafGroup dafGroup = groupService.getGroupById(bdr.getResourceId());

			if (dafGroup != null) {
				patientList = new ArrayList<>();
				coverageList = new ArrayList<>();
				practitionerList = new ArrayList<>();
				practitionerRoleList = new ArrayList<>();
				organizationList = new ArrayList<>();
				JSONObject jsonData = new JSONObject(dafGroup.getData());
				if(!jsonData.isNull("member")) {
					JSONArray jsonArr = jsonData.getJSONArray("member");
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObj = jsonArr.getJSONObject(i);
						if (jsonObj.getJSONObject("entity") != null) {
							String resourceId = jsonObj.getJSONObject("entity").getString("reference").split("/")[1];
							//Integer patientId = Integer.parseInt(referenceId);
							//String referenceId = "Patient/"+resourceId;
							//System.out.println("Patient id ::: "+resourceId);
							patientList.add(resourceId);
						}
						if (jsonObj.getJSONArray("extension") != null) {
							JSONArray memberExtensions = jsonObj.getJSONArray("extension");
							for(int j = 0; j < memberExtensions.length(); j++) {
								JSONObject extObj = memberExtensions.getJSONObject(j);
								String url = extObj.getString("url");
								if(url.equalsIgnoreCase("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/ext-coverageReference")) {
									String coverageId = extObj.getJSONObject("valueReference").getString("reference").split("/")[1];
									coverageList.add(coverageId);
								}
								if(url.equalsIgnoreCase("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/ext-attributedProvider")) {
									String reference = extObj.getJSONObject("valueReference").getString("reference");
									String providerResource = reference.split("/")[0];
									String providerId = reference.split("/")[1];
									if(providerResource.equalsIgnoreCase("Practitioner")) {
										practitionerList.add(providerId);
									}
									if (providerResource.equalsIgnoreCase("PractitionerRole")) {
										practitionerRoleList.add(providerId);
									}
									if (providerResource.equalsIgnoreCase("Organzation")) {
										organizationList.add(providerId);
									}
								}
								System.out.println("practitionerList "+practitionerList);
								System.out.println("practitionerRoleList "+practitionerRoleList);
								System.out.println("organizationList "+organizationList);
							}
						}
					}
				}
			}

		}
		// Process Patient Bulk data request
		String type = bdr.getType();
		/*		String[] nameEx = {};
		if(type !=null)
		{
			nameEx = type.split(",");  // output is all necessart files
		}*/


		//a = 0;
		List<String> files = new ArrayList<>();
		if (type == null || Arrays.asList(type.split(",")).contains("Patient")) {
			Future<Long> patient = service.processPatientData(bdr, destDir, ctx, patientList, start, end);
			files.add("Patient.ndjson");
		}

		// Process AllergyIntolerance Bulk data request
		Future<Long> allergyintolerance = null;
		if (type == null || Arrays.asList(type.split(",")).contains("AllergyIntolerance")) {

			allergyintolerance = service.processAllergyIntoleranceData(bdr, destDir, ctx, patientList, start, end);
			files.add("AllergyIntolerance.ndjson");
		}
		
		// Process FamilyMemberHistory Bulk data request
		Future<Long> familymemberhistory = null;
		if (type == null || Arrays.asList(type.split(",")).contains("FamilyMemberHistory")) {

			familymemberhistory = service.processFamilyMemberHistoryData(bdr, destDir, ctx, patientList, start, end);
			files.add("FamilyMemberHistory.ndjson");
		}
		
		// Process Practitioner Bulk data request
		Future<Long> practitioner = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Practitioner") && practitionerList != null && !practitionerList.isEmpty()) {
			practitioner = service.processPractitionerData(bdr, destDir, ctx, practitionerList, start, end);
			files.add("Practitioner.ndjson");
		}
		
		// Process Coverage Bulk data request
		Future<Long> coverage = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Coverage") && coverageList != null && !coverageList.isEmpty()) {
			coverage = service.processCoverageData(bdr, destDir, ctx, coverageList, start, end, type);
			files.add("Coverage.ndjson");
		}
		
		// Process Person Bulk data request
//		Future<Long> person = null;
//		if (Arrays.asList(type.split(",")).contains("RelatedPerson")) {
//			person = service.processRelatedPersonData(bdr, destDir, ctx, patientList, start, end);
//			files.add("RelatedPerson.ndjson");
//		}
		// Process Encounter Bulk data request
		Future<Long> encounter = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Encounter")) {
			encounter = service.processEncounterData(bdr, destDir, ctx, patientList, start, end);
			files.add("Encounter.ndjson");
		}

		// Process CarePlan Bulk data request
		Future<Long> careplan = null;
		if (type == null || Arrays.asList(type.split(",")).contains("CarePlan")) {
			careplan = service.processCarePlanData(bdr, destDir, ctx, patientList, start, end);
			files.add("CarePlan.ndjson");
		}
		
		// Process CareTeam Bulk data request
		Future<Long> careteam = null;
		if (type == null || Arrays.asList(type.split(",")).contains("CareTeam")) {
			careteam = service.processCareTeamData(bdr, destDir, ctx, patientList, start, end);
			files.add("CareTeam.ndjson");
		}

		// Process Condition Bulk data request
		Future<Long> condition = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Condition")) {
			condition = service.processConditionData(bdr, destDir, ctx, patientList, start, end);
			files.add("Condition.ndjson");
		}

		// Process Device Bulk data request
		Future<Long> device = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Device")) {

			device = service.processDeviceData(bdr, destDir, ctx, patientList, start, end);
			files.add("Device.ndjson");
		}

		// Process DiagnosticReport Bulk data request
		Future<Long> diagnosticreport = null;
		if (type == null || Arrays.asList(type.split(",")).contains("DiagnosticReport")) {

			diagnosticreport = service.processDiagnosticReportData(bdr, destDir, ctx, patientList, start, end);
			files.add("DiagnosticReport.ndjson");
		}

		// Process DocumentReference Bulk data request
		Future<Long> documentreference = null;
		if (type == null || Arrays.asList(type.split(",")).contains("DocumentReference")) {

			documentreference = service.processDocumentReferenceData(bdr, destDir, ctx, patientList, start, end);
			files.add("DocumentReference.ndjson");
		}

		// Process Goal Bulk data request
		Future<Long> goal = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Goal")) {

			goal = service.processGoalsData(bdr, destDir, ctx, patientList, start, end);
			files.add("Goal.ndjson");
		}

		// Process Immunization Bulk data request
		Future<Long> immunization = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Immunization")) {

			immunization = service.processImmunizationData(bdr, destDir, ctx, patientList, start, end);
			files.add("Immunization.ndjson");
		}

		// Process Location Bulk data request
//		Future<Long> location = null;
//		if (Arrays.asList(type.split(",")).contains("Location")) {
//
//			location = service.processLocationData(bdr, destDir, ctx, patientList, start, end);
//			files.add("Location.ndjson");
//		}

		// Process MedicationAdministration Bulk data request
		Future<Long> medicationadministration = null;
		if (type == null || Arrays.asList(type.split(",")).contains("MedicationAdministration")) {

			medicationadministration = service.processMedicationAdministrationData(bdr, destDir, ctx, patientList, start, end);
			files.add("MedicationAdministration.ndjson");
		}

		// Process MedicationDispense Bulk data request
		Future<Long> medicationdispense = null;
		if (type == null || Arrays.asList(type.split(",")).contains("MedicationDispense")) {

			medicationdispense = service.processMedicationDispenseData(bdr, destDir, ctx, patientList, start, end);
			files.add("MedicationDispense.ndjson");
		}

		// Process PractitionerRole Bulk data request
		Future<Long> practitionerRole = null;
		if (type == null || Arrays.asList(type.split(",")).contains("PractitionerRole") && practitionerRoleList != null && !practitionerRoleList.isEmpty()) {

			practitionerRole = service.processPractitionerRoleData(bdr, destDir, ctx, practitionerRoleList, start, end, type);
			files.add("PractitionerRole.ndjson");
		}
		
		// Process Medication Bulk data request
		Future<Long> medication = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Medication")) {

			medication = service.processMedicationData(bdr, destDir, ctx, patientList, start, end);
			files.add("Medication.ndjson");
		}

		// Process MedicationStatement Bulk data request
		Future<Long> medicationstatement = null;
		if (type == null || Arrays.asList(type.split(",")).contains("MedicationStatement")) {

			medicationstatement = service.processMedicationStatementData(bdr, destDir, ctx, patientList, start, end);
			files.add("MedicationStatement.ndjson");
		}

		// Process Observation Bulk data request
		Future<Long> observation = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Observation")) {

			observation = service.processObservationData(bdr, destDir, ctx, patientList, start, end);
			files.add("Observation.ndjson");
		}

		// Process Organization Bulk data request
		Future<Long> organization = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Organization") && organizationList != null && !organizationList.isEmpty()) {

			organization = service.processOrganizationData(bdr, destDir, ctx, organizationList, start, end);
			files.add("Organization.ndjson");
		}

		// Process Procedure Bulk data request
		Future<Long> procedure = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Procedure")) {

			procedure = service.processProcedureData(bdr, destDir, ctx, patientList, start, end);
			files.add("Procedure.ndjson");
		}
		
		// Process MedicationRequest Bulk data request
		Future<Long> medicationrequest = null;
		if (type == null || Arrays.asList(type.split(",")).contains("MedicationRequest")) {

			medicationrequest = service.processMedicationRequestData(bdr, destDir, ctx, patientList, start, end);
			files.add("MedicationRequest.ndjson");
		}
		
		// Process Claim Bulk data request
		Future<Long> claim = null;
		if (type == null || Arrays.asList(type.split(",")).contains("Claim")) {

			claim = service.processClaimData(bdr, destDir, ctx, patientList, start, end);
			files.add("Claim.ndjson");
		}

		//wait until async processes gets complete
//		while(true)
//		{
//			if(a == files.size())
//			{
//				String strFiles = StringUtils.join(files, ',');
//				bdr.setStatus("Completed");
//				bdr.setFiles(strFiles);
//				bdrService.saveBulkDataRequest(bdr);
//				System.out.println("\n\n Task complete!.................. \n\n");
//				log.info("request with id : " + bdr.getRequestId() + " - is processed. The  time is : '"
//						+ System.nanoTime() + "' in nano seconds");
//				break;
//			}
//			Thread.sleep(100);
//		}
		while (true) {
			boolean checkFilesCreated = checkFilesInDestination(bdr, ndjsonFiles);
			if (checkFilesCreated) {
				updateBulkdataRequests(bdr);
				break;
			}
			Thread.sleep(100);
		}
	}
	
	/**
	 * Set and update bulk data request
	 * 
	 * @param bdr
	 */
	private void updateBulkdataRequests(DafBulkDataRequest bdr) {
		String createdFiles = StringUtils.join(ndjsonFiles, ',');
		bdr.setStatus("Completed");
		bdr.setFiles(createdFiles);
		bdrService.saveBulkDataRequest(bdr);
		ndjsonFiles = new ArrayList<>();
		log.info("\n\n Task complete!.................. \n\n");
		log.info("Request with id : " + bdr.getRequestId() + " - is processed. The  time is : '"
				+ System.nanoTime() + "' in nano seconds");
		log.info("**************Updating the Bulk Data Request Status to {} for Request Id: {}",
				"Completed", bdr.getRequestId());
		
	}
	
	/**
	 * Check files in destination path
	 * @param bdr
	 * @param ndjsonfiles
	 * @return
	 */
	public boolean checkFilesInDestination(DafBulkDataRequest bdr, List<String> ndjsonfiles) {
		String contextPath = System.getProperty("catalina.base");
		String destFilePath = contextPath + "/bulkdata" + "/" + bdr.getRequestId() + "/";
		log.info("Verifying the NDSJSON files in directory ----->" + destFilePath);
		String[] destFilesList = new File(destFilePath).list();
		log.info("No. of NDJSON files created in " + destFilePath + " is:" + ndjsonfiles.size());
		log.info("No. of NDJSON files Found in " + destFilePath + " is:" + destFilesList.length);
		if (ndjsonfiles.size() == destFilesList.length) {
			return true;
		} else {
			return false;
		}
	}
}
