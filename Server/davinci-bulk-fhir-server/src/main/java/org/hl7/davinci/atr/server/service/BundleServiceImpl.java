package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.davinci.atr.server.dao.CoverageDao;
import org.hl7.davinci.atr.server.dao.OrganizationDao;
import org.hl7.davinci.atr.server.dao.PatientDao;
import org.hl7.davinci.atr.server.dao.PractitionerDao;
import org.hl7.davinci.atr.server.model.DafBundle;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

@Service("bundleService")
@Transactional
public class BundleServiceImpl implements BundleService {
	private static final Logger logger = LoggerFactory.getLogger(BundleServiceImpl.class);    

	@Autowired
    private PatientDao patientDao;
	@Autowired
    private PractitionerDao practitionerDao;
	@Autowired
    private OrganizationDao organizationDao;
	@Autowired
    private CoverageDao coverageDao;
	@Override
	public Bundle createBundle(Bundle theBundle) {
		Map<String,String> savedResources = new HashMap<String, String>();
		Bundle bundle = new Bundle();
		try {
			if(theBundle != null && !theBundle.isEmpty()) {
				if(theBundle.hasEntry() && !theBundle.getEntry().isEmpty()) {
					List<BundleEntryComponent> bundleEntryComponentList = theBundle.getEntry();
					if(bundleEntryComponentList != null && !bundleEntryComponentList.isEmpty()) {
						ArrayList<String> patients = new ArrayList<>();
						ArrayList<String> practitioners = new ArrayList<>();
						ArrayList<String> organizations = new ArrayList<>();
						ArrayList<String> generalPractitioners = new ArrayList<>();
						ArrayList<String> managingOrganizations = new ArrayList<>();
						ArrayList<String> beneficiaries = new ArrayList<>();
						ArrayList<String> coverages = new ArrayList<>();
						for(BundleEntryComponent component:bundleEntryComponentList) {
							if(component.hasRequest() && component.getRequest().hasMethod()) {
								if(component.getRequest().getMethod().equals(HTTPVerb.POST)) {
									if(component.hasResource()) {
										String resourceName = component.getResource().getResourceType().name();
										if(resourceName.equalsIgnoreCase("Practitioner")) {
											practitioners.add(component.getResource().getIdElement().getIdPart());
										}
										else if(resourceName.equalsIgnoreCase("Organization")) {
											organizations.add(component.getResource().getIdElement().getIdPart());
										}
										else if(resourceName.equalsIgnoreCase("Coverage")) {
											coverages.add(component.getResource().getIdElement().getIdPart());
											Coverage coverage = (Coverage) component.getResource();
											if(coverage.hasBeneficiary()) {
												String beneficiaryId = coverage.getBeneficiary().getReferenceElement().getIdPart();
												beneficiaries.add(beneficiaryId);
											}
										}
										else if(resourceName.equalsIgnoreCase("Patient")) {
											Patient patient = (Patient) component.getResource();
											patients.add(patient.getIdElement().getIdPart());
											if(patient.hasGeneralPractitioner()) {
												List<Reference> generalList = new ArrayList<>();
												generalList = patient.getGeneralPractitioner();
												for( Reference reference:generalList) {
													try {
														if(reference.hasReference()) {
															String referenceId = reference.getReferenceElement().getIdPart();
															String referenceResource = reference.getReferenceElement().getResourceType();
															if(referenceResource.equalsIgnoreCase("Practitioner")) {
																generalPractitioners.add(referenceId);
															}
															else if(referenceResource.equalsIgnoreCase("Organization")) {
																managingOrganizations.add(referenceId);
															}
														}
													}
													catch(Exception e) {
														logger.error("Exception while iterating generalPractitioner refrences in BundleServiceImpl ", e);
													} 
												}
											}
											if(patient.hasManagingOrganization()) {
												String referenceId = patient.getManagingOrganization().getReferenceElement().getIdPart();
												managingOrganizations.add(referenceId);
											}
										}
									}
									else {
										logger.info(" BundleEntryComponent doesn't have resource. Hence skipping the BundleEntryComponent.");
									}
								}
								else {
									logger.info(" BundleEntryComponent request.method is not POST. Hence skipping the BundleEntryComponent.");
								}
							}
							else {
								logger.info(" BundleEntryComponent doesn't have request or request.method object. Hence skipping the BundleEntryComponent.");
							}
						}
						System.out.println(" bundleEntryComponentList.size() :: "+bundleEntryComponentList.size());
						for(BundleEntryComponent bundleComponent:bundleEntryComponentList) {
							try {
								if(bundleComponent.hasResource()){
									String resourceName = bundleComponent.getResource().getResourceType().name();
									logger.info(" resourceName :: "+resourceName);
									if(resourceName.equalsIgnoreCase("Patient")) {
										Patient thePatient = (Patient) bundleComponent.getResource();
										logger.info(" Patient.id {} "+thePatient.getIdElement().getIdPart());
										if(thePatient.hasGeneralPractitioner()) {
											List<Reference> referenceList = new ArrayList<>();
											referenceList = thePatient.getGeneralPractitioner();
											for( Reference reference:referenceList) {
												try {
													if(reference.hasReference()) {
														String referenceId = reference.getReferenceElement().getIdPart();
														String referenceResource = reference.getReferenceElement().getResourceType();
														if(referenceResource.equalsIgnoreCase("Practitioner") && practitioners != null && !practitioners.isEmpty()) {
															boolean isIdPresent = false;
															for(String practitioner:practitioners) {
																if(practitioner.equalsIgnoreCase(referenceId)) {
																	isIdPresent = true;
																	break;
																}
															}
															if(!isIdPresent) {
																patients.remove(thePatient.getIdElement().getIdPart());
																logger.info("Patient.generalPractitioner {} : Practitioner/"+referenceId+" not found in Bundle. Hence Patient.id "+thePatient.getIdElement().getIdPart()+" will not be saved.");
																//break;
															}
														}
														else if(referenceResource.equalsIgnoreCase("Organization") && organizations != null 
																&& !organizations.isEmpty()) {
															boolean isIdPresent = false;
															for(String organization:organizations) {
																if(organization.equalsIgnoreCase(referenceId)) {
																	isIdPresent = true;
																	break;
																}
															}
															if(!isIdPresent) {
																patients.remove(thePatient.getIdElement().getIdPart());
																logger.info("Patient.generalPractitioner {} : Organization/"+referenceId+" not found in Bundle. Hence Patient.id "+thePatient.getIdElement().getIdPart()+" will not be saved.");
																//break;
															}
														}
													}
												}
												catch(Exception e) {
													logger.error("Exception while iterating Patient.generalPractitioner {} refrences in BundleServiceImpl ", e);
												} 
											}
										}
										if(thePatient.hasManagingOrganization()) {
											Reference organizationReference = thePatient.getManagingOrganization();
											String referenceId = organizationReference.getReferenceElement().getIdPart();
											String referenceResource = organizationReference.getReferenceElement().getResourceType();
											if(referenceResource.equalsIgnoreCase("Organization") && organizations != null && !organizations.isEmpty()) {
												boolean isIdPresent = false;
												for(String organization:organizations) {
													if(organization.equalsIgnoreCase(referenceId)) {
														isIdPresent = true;
														break;
													}
												}
												if(!isIdPresent) {
													patients.remove(thePatient.getIdElement().getIdPart());
													logger.info("Patient.managingOrganization {} : Organization/"+referenceId+" not found in Bundle. Hence Patient.id "+thePatient.getIdElement().getIdPart()+" will not be saved.");
													continue;
												}
											}
										}
										if(patients != null && !patients.isEmpty()) {
											for(String patientId:patients) {
												if(patientId.equalsIgnoreCase(thePatient.getIdElement().getIdPart())) {
													savedResources.put(thePatient.getIdElement().getIdPart(), "Patient");
													patientDao.createPatient(thePatient);
													logger.info(" Patient.id {} : "+thePatient.getIdElement().getIdPart()+" saved.");	
													break;
												}
											}
										}
									}
									else if(resourceName.equalsIgnoreCase("Practitioner") && generalPractitioners != null && !generalPractitioners.isEmpty()) {
										Practitioner thePractitioner = (Practitioner) bundleComponent.getResource();
										for(String practitioner:generalPractitioners) {
											if(practitioner.equalsIgnoreCase(thePractitioner.getIdElement().getIdPart())) {
												savedResources.put(thePractitioner.getIdElement().getIdPart(), "Practitioner");
												practitionerDao.createPractitioner(thePractitioner);
												logger.info(" Practitioner.id {} : "+thePractitioner.getIdElement().getIdPart()+" has reference with Patient.generalPractitioner. Hence Practitioner is saved.");	
											}
										}
									}
									else if(resourceName.equalsIgnoreCase("Organization") && managingOrganizations != null && !managingOrganizations.isEmpty()) {
										Organization theOrganization = (Organization) bundleComponent.getResource();
										for(String organization:managingOrganizations) {
											if(organization.equalsIgnoreCase(theOrganization.getIdElement().getIdPart())) {
												savedResources.put(theOrganization.getIdElement().getIdPart(), "Organization");
												organizationDao.createOrganization(theOrganization);
												logger.info(" Organization.id {} : "+theOrganization.getIdElement().getIdPart()+" has reference with Patient.generalPractitioner/Patient.managingOrganization. Hence Practitioner/Organization is saved.");	
											}
										}
									}
									else if(resourceName.equalsIgnoreCase("Coverage") && patients != null && !patients.isEmpty()) {
										Coverage theCoverage = (Coverage) bundleComponent.getResource();
										if(theCoverage.hasBeneficiary()) {
											String referenceId = theCoverage.getBeneficiary().getReferenceElement().getIdPart();
											for(String patient:patients) {
												if(referenceId.equalsIgnoreCase(patient)) {
													savedResources.put(theCoverage.getIdElement().getIdPart(), "Coverage");
													coverageDao.createCoverage(theCoverage);
													logger.info(" Coverage.beneficiary.reference : Patient/"+referenceId+" has reference with Patient.id {} "+referenceId+". Hence Coverage.id "+theCoverage.getIdElement().getIdPart()+" is saved.");
													break;
												}
											}
										}
										else {
											logger.info(" Coverage.id {} : "+theCoverage.getIdElement().getIdPart()+" not saved. Because does not have beneficiary");	
										}
									}
								}
							}
							catch(Exception e) {
								logger.error("Exception in createBundle of BundleServiceImpl ", e);
							}
						}
					}
				} else {
					throw new UnprocessableEntityException("Bundle doesn't contain any Entries to process");
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception in createBundle of BundleServiceImpl ", e);
		}
		if(savedResources.size()>0) {
			List<BundleEntryComponent> entryComps = new ArrayList<BundleEntryComponent>();
			for(Map.Entry<String, String> entry: savedResources.entrySet()) {
				BundleEntryComponent entryComp = new BundleEntryComponent();
				BundleEntryResponseComponent responseComp = new BundleEntryResponseComponent();
				responseComp.setStatus("201 Created");
				responseComp.setLocation(entry.getValue()+"/"+entry.getKey());
				responseComp.setLastModified(new Date());
				entryComp.setResponse(responseComp);
				entryComps.add(entryComp);
			}
			bundle.setEntry(entryComps);
		}
		return bundle;
	}
}
