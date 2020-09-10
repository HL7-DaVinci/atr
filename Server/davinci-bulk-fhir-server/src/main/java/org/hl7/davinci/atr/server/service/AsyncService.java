package org.hl7.davinci.atr.server.service;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.hl7.davinci.atr.server.model.DafBulkDataRequest;
import org.hl7.davinci.atr.server.providers.AllergyIntoleranceResourceProvider;
import org.hl7.davinci.atr.server.providers.BulkDataRequestProvider;
import org.hl7.davinci.atr.server.providers.CarePlanResourceProvider;
import org.hl7.davinci.atr.server.providers.CareTeamResourceProvider;
import org.hl7.davinci.atr.server.providers.ClaimResourceProvider;
import org.hl7.davinci.atr.server.providers.ConditionResourceProvider;
import org.hl7.davinci.atr.server.providers.CoverageResourceProvider;
import org.hl7.davinci.atr.server.providers.DeviceResourceProvider;
import org.hl7.davinci.atr.server.providers.DiagnosticReportResourceProvider;
import org.hl7.davinci.atr.server.providers.DocumentReferenceResourceProvider;
import org.hl7.davinci.atr.server.providers.EncounterResourceProvider;
import org.hl7.davinci.atr.server.providers.FamilyMemberHistoryResourceProvider;
import org.hl7.davinci.atr.server.providers.GoalResourceProvider;
import org.hl7.davinci.atr.server.providers.ImmunizationResourceProvider;
import org.hl7.davinci.atr.server.providers.LocationResourceProvider;
import org.hl7.davinci.atr.server.providers.MedicationAdministrationResourceProvider;
import org.hl7.davinci.atr.server.providers.MedicationDispenseResourceProvider;
import org.hl7.davinci.atr.server.providers.MedicationRequestResourceProvider;
import org.hl7.davinci.atr.server.providers.MedicationResourceProvider;
import org.hl7.davinci.atr.server.providers.MedicationStatementResourceProvider;
import org.hl7.davinci.atr.server.providers.ObservationResourceProvider;
import org.hl7.davinci.atr.server.providers.OrganizationResourceProvider;
import org.hl7.davinci.atr.server.providers.PatientResourceProvider;
import org.hl7.davinci.atr.server.providers.PractitionerResourceProvider;
import org.hl7.davinci.atr.server.providers.PractitionerRoleResourceProvider;
import org.hl7.davinci.atr.server.providers.ProcedureResourceProvider;
import org.hl7.davinci.atr.server.providers.RelatedPersonResourceProvider;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationDispense;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ch.qos.logback.classic.Logger;

@Service
public class AsyncService {
	Logger log = (Logger) LoggerFactory.getLogger(AsyncService.class);
	@Autowired
	BulkDataRequestProvider bdd;
	@Autowired
	private PatientResourceProvider patientResourceProvider;
	@Autowired
	private AllergyIntoleranceResourceProvider allergyIntoleranceProvider;
	@Autowired
	private CarePlanResourceProvider carePlanProvider;
	@Autowired
	MedicationResourceProvider medicationResourceProvider;
	@Autowired
	private ConditionResourceProvider conditionProvider;
	@Autowired
	private DeviceResourceProvider deviceProvider;
	@Autowired
	private DiagnosticReportResourceProvider diagnosticReportProvider;
	@Autowired
	private DocumentReferenceResourceProvider documentReferenceResourceProvider;
	@Autowired
	private GoalResourceProvider goalsResourceProvider;
	@Autowired
	private ImmunizationResourceProvider immunizationResourceProvider;
	@Autowired
	private LocationResourceProvider locationResourceProvider;
	@Autowired
	private MedicationAdministrationResourceProvider medicationAdministrationResourceProvider;
	@Autowired
	private MedicationDispenseResourceProvider medicationDispenseResourceProvider;
	@Autowired
	private MedicationStatementResourceProvider medicationStatementResourceProvider;
	@Autowired
	private ObservationResourceProvider observationResourceProvider;
	@Autowired
	private ProcedureResourceProvider procedureResourceProvider;
	@Autowired
	private PractitionerResourceProvider practitionerResourceProvider;
	@Autowired
	private PractitionerRoleResourceProvider practitionerRoleResourceProvider;
	@Autowired
	private CareTeamResourceProvider careTeamResourceProvider;
	@Autowired
	private EncounterResourceProvider encounterResourceProvider;
	@Autowired
	private FamilyMemberHistoryResourceProvider familyMemberHistoryProvider;
	@Autowired
	private RelatedPersonResourceProvider relatedPersonResourceProvider;
	@Autowired
	private CoverageResourceProvider coverageResourceProvider;
	@Autowired
	OrganizationResourceProvider organizationResourceProvider;
	@Autowired
	MedicationRequestResourceProvider medicationRequestResourceProvider;
	@Autowired
	ClaimResourceProvider claimResourceProvider;
	@Async("asyncExecutor")
    public Future<Long> processPatientData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Patient.ndjson";
		
		try {
			List<Patient> patients = patientResourceProvider.getPatientForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < patients.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(patients.get(i)));
				if (i < patients.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processAllergyIntoleranceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "AllergyIntolerance.ndjson";
		try {
			List<AllergyIntolerance> allergyIntoleranceList = allergyIntoleranceProvider
					.getAllergyIntoleranceForBulkDataRequest(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < allergyIntoleranceList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(allergyIntoleranceList.get(i)));

				if (i < allergyIntoleranceList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}
	
	@Async("asyncExecutor")
	public Future<Long> processPractitionerData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> practitionerIdList, Date start, Date end) {
		String fileName = "Practitioner.ndjson";
		try {
			List<Practitioner> practitionerList = practitionerResourceProvider.getPractitionerForBulkDataRequest(practitionerIdList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < practitionerList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(practitionerList.get(i)));

				if (i < practitionerList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}
	
	@Async("asyncExecutor")
	public Future<Long> processPractitionerRoleData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end, String type) {
		String fileName = "PractitionerRole.ndjson";
		try {
			List<PractitionerRole> practitionerRoleList = practitionerRoleResourceProvider.getPractitionerRoleForBulkDataRequest(patientList, start, end);
			if (Arrays.asList(type.split(",")).contains("Location")) {
				List<String> locationIds = new ArrayList<>();
				for(PractitionerRole c:practitionerRoleList) {
					if(c.hasLocation()) {
						List<Reference> referenceList = c.getLocation();
						for(Reference reference : referenceList) {
							if(reference.hasReferenceElement() && 
									reference.getReferenceElement().hasResourceType()) {
								 if(reference.getReferenceElement().getResourceType().equalsIgnoreCase("Location")
										 && reference.getReferenceElement().hasIdPart()) {
									 locationIds.add(reference.getReferenceElement().getIdPart());
								 } 
							 }
						}
					}
				}
				if(locationIds != null && !locationIds.isEmpty()) {
					processLocationData(bdr, destDir, ctx, locationIds, start, end);
				}
			}
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < practitionerRoleList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(practitionerRoleList.get(i)));

				if (i < practitionerRoleList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processCarePlanData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "CarePlan.ndjson";
		try {
			List<CarePlan> carePlanList = carePlanProvider.getCarePlanForBulkData(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < carePlanList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(carePlanList.get(i)));

				if (i < carePlanList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processConditionData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Condition.ndjson";
		try {
			List<Condition> conditionList = conditionProvider.getConditionForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < conditionList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(conditionList.get(i)));

				if (i < conditionList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processDeviceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Device.ndjson";
		try {
			List<Device> deviceList = deviceProvider.getDeviceForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < deviceList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(deviceList.get(i)));

				if (i < deviceList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDiagnosticReportData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "DiagnosticReport.ndjson";
		try {
			List<DiagnosticReport> diagnosticReportList = diagnosticReportProvider
					.getDiagnosticReportForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < diagnosticReportList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(diagnosticReportList.get(i)));

				if (i < diagnosticReportList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processDocumentReferenceData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "DocumentReference.ndjson";
		try {
			List<DocumentReference> documentReferenceList = documentReferenceResourceProvider
					.getDocumentReferenceForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < documentReferenceList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(documentReferenceList.get(i)));

				if (i < documentReferenceList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processGoalsData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Goal.ndjson";

		try {
			List<Goal> goalList = goalsResourceProvider.getGoalsForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < goalList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(goalList.get(i)));

				if (i < goalList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processImmunizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "Immunization.ndjson";
		try {
			List<Immunization> immunizationList = immunizationResourceProvider
					.getImmunizationForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < immunizationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(immunizationList.get(i)));

				if (i < immunizationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long>  processLocationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Location.ndjson";
		try {
			List<Location> locationList = locationResourceProvider.getLocationForBulkDataRequest(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < locationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(locationList.get(i)));

				if (i < locationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationAdministrationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "MedicationAdministration.ndjson";
	
		try {
			List<MedicationAdministration> medicationAdministrationList = medicationAdministrationResourceProvider
					.getMedicationAdministrationForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationAdministrationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(medicationAdministrationList.get(i)));
				if (i < medicationAdministrationList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationDispenseData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "MedicationDispense.ndjson";

		try {
			List<MedicationDispense> medicationDispenseList = medicationDispenseResourceProvider
					.getMedicationDispenseForBulkDataRequest(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < medicationDispenseList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationDispenseList.get(i)));

				if (i < medicationDispenseList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationRequestData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "MedicationRequest.ndjson";

		try {
			List<MedicationRequest> medicationRequestList = medicationRequestResourceProvider
					.getMedicationRequestForBulkDataRequest(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationRequestList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationRequestList.get(i)));

				if (i < medicationRequestList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Medication.ndjson";
		try {
			List<Medication> medicationList = medicationResourceProvider.getMedicationForBulkDataRequest(patientList,
					start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < medicationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(medicationList.get(i)));

				if (i < medicationList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processMedicationStatementData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "MedicationStatement.ndjson";

		try {
			List<MedicationStatement> medicationStatementList = medicationStatementResourceProvider
					.getMedicationStatementForBulkDataRequest(patientList, start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < medicationStatementList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(medicationStatementList.get(i)));

				if (i < medicationStatementList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processObservationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Observation.ndjson";

		try {
			List<Observation> observationList = observationResourceProvider
					.getObservationForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < observationList.size(); i++) {

				pw.write(ctx.newJsonParser().encodeResourceToString(observationList.get(i)));
				if (i < observationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processOrganizationData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "Organization.ndjson";

		try {
			List<Organization> organizationList = organizationResourceProvider
					.getOrganizationForBulkDataRequest(patientList, start, end);
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);
			for (int i = 0; i < organizationList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(organizationList.get(i)));

				if (i < organizationList.size() - 1) {
					pw.write('\n');
				}
			}

			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());

	}

	@Async("asyncExecutor")
	public Future<Long> processProcedureData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList,
			Date start, Date end) {
		String fileName = "Procedure.ndjson";

		try {
			List<Procedure> procedureList = procedureResourceProvider.getProcedureForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processCareTeamData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "CareTeam.ndjson";

		try {
			List<CareTeam> careTeamList = careTeamResourceProvider.getCareTeamForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < careTeamList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(careTeamList.get(i)));

				if (i < careTeamList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processEncounterData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "Encounter.ndjson";

		try {
			List<Encounter> procedureList = encounterResourceProvider.getEncounterForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}

	@Async("asyncExecutor")
	public Future<Long> processFamilyMemberHistoryData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "FamilyMemberHistory.ndjson";


		try {
			List<FamilyMemberHistory> procedureList = familyMemberHistoryProvider.getFamilyMemberHistoryForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}
	
	@Async("asyncExecutor")
	public Future<Long> processCoverageData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList, Date start, Date end, String type) {
		String fileName = "Coverage.ndjson";
		try {
			List<Coverage> procedureList = coverageResourceProvider.getCoverageForBulkDataRequest(patientList,
					start, end);
			if(Arrays.asList(type.split(",")).contains("RelatedPerson")) {
				List<String> relatedPerson = new ArrayList<>();
				for(Coverage c:procedureList) {
					if(c.hasPolicyHolder()) {
						Reference reference = c.getPolicyHolder();
						if(reference.hasReferenceElement() && 
								reference.getReferenceElement().hasResourceType()) {
							 if(reference.getReferenceElement().getResourceType().equalsIgnoreCase("RelatedPerson")
									 && reference.getReferenceElement().hasIdPart()) {
								 relatedPerson.add(reference.getReferenceElement().getIdPart());
							 } 
						 }
					}
				}
				if(relatedPerson != null && !relatedPerson.isEmpty()) {
					processRelatedPersonData(bdr, destDir, ctx, relatedPerson, start, end);
				}
			}
			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}
	
	@Async("asyncExecutor")
	public Future<Long> processRelatedPersonData(DafBulkDataRequest bdr, File destDir, FhirContext ctx, List<String> patientList, Date start, Date end) {
		String fileName = "RelatedPerson.ndjson";
		try {
			List<RelatedPerson> procedureList = relatedPersonResourceProvider.getRelatedPersonForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}
	
	@Async("asyncExecutor")
	public Future<Long> processClaimData(DafBulkDataRequest bdr, File destDir, FhirContext ctx,
			List<String> patientList, Date start, Date end) {
		String fileName = "Claim.ndjson";
		try {
			List<Claim> procedureList = claimResourceProvider.getClaimForBulkDataRequest(patientList,
					start, end);

			File ndJsonFile = new File(destDir.getAbsolutePath() + "/" + fileName);
			PrintWriter pw = new PrintWriter(ndJsonFile);

			for (int i = 0; i < procedureList.size(); i++) {
				pw.write(ctx.newJsonParser().encodeResourceToString(procedureList.get(i)));

				if (i < procedureList.size() - 1) {
					pw.write('\n');
				}
			}
			pw.close();
			bdd.setNDJSONFile(fileName);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new AsyncResult<>(System.nanoTime());
	}
}
