package org.hl7.davinci.atr.server.providers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.ResourceInteractionComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode;
import org.hl7.fhir.r4.model.CapabilityStatement.TypeRestfulInteraction;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.UriType;

import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;

public class CapabilityStatementResourceProvider extends ServerCapabilityStatementProvider {

	public static final String AUTH_SERVER = "/ix-auth-server";
	public static final String TOKEN_ENDPOINT = "/api/bulk/token";
			

	    @Metadata
	    public CapabilityStatement getConformance(HttpServletRequest request, RequestDetails theRequestDetails) {

	    	String uri = request.getScheme() + "://" + request.getServerName()
			+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
					|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
							: ":" + request.getServerPort())
			+ AUTH_SERVER;

	    	CapabilityStatement conformance = super.getServerConformance(request,theRequestDetails);
	        conformance.getFhirVersion();
	        conformance.setId("InteropXBulkServer");
	        conformance.setUrl("/fhir/metadata");
	        conformance.setVersion("2.0");
	        conformance.setName("InteropXBulkServerMetadata");
	        conformance.setStatus(PublicationStatus.ACTIVE);
	        conformance.setPublisher("InteropXBulkServer");

	        //Set Software
	        CapabilityStatementSoftwareComponent softwareComponent = new CapabilityStatementSoftwareComponent();
	        softwareComponent.setName("InteropXBulkServer");
	        softwareComponent.setVersion("1.6");
	        conformance.setSoftware(softwareComponent);
	    	//Set format
			List<CodeType> formats = new ArrayList<>();
			CodeType theFormat = new CodeType();
			theFormat.setValue("xml");
			formats.add(theFormat);
			theFormat = new CodeType();
			theFormat.setValue("json");
			formats.add(theFormat);
			conformance.setFormat(formats);
	        //Set Rest
	        List<CapabilityStatementRestComponent> restList = new ArrayList<CapabilityStatementRestComponent>();
	        CapabilityStatementRestComponent rest = new CapabilityStatementRestComponent();
	        rest.setMode(RestfulCapabilityMode.SERVER);

	        CapabilityStatementRestSecurityComponent restSecurity = new CapabilityStatementRestSecurityComponent();

	        Extension conformanceExtension = new Extension("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");

	        conformanceExtension.addExtension(new Extension("authorize", new UriType(uri + "/api/authorize")));
	        conformanceExtension.addExtension(new Extension("token", new UriType(uri + "/api/bulk/token")));

	        restSecurity.addExtension(conformanceExtension);
	        
	        List<CapabilityStatement.CapabilityStatementRestResourceComponent> resources = conformance.getRest().get(0)
					.getResource();
	        
	        for(CapabilityStatement.CapabilityStatementRestResourceComponent resource : resources) {
	        	if (resource.getType().equalsIgnoreCase("Group")) {
	        		CapabilityStatementRestResourceOperationComponent operationComp = new CapabilityStatementRestResourceOperationComponent();
	        		operationComp.setName("export");
	        		operationComp.setDefinition("http://hl7.org/fhir/uv/bulkdata/OperationDefinition/group-export");
	        		operationComp.setDocumentation("FHIR Operation to obtain a detailed set of FHIR resources of diverse resource types pertaining to all patients in specified [Group](https://www.hl7.org/fhir/group.html).\\\\n\\\\nIf a FHIR server supports Group-level data export, it SHOULD support reading and searching for `Group` resource. This enables clients to discover available groups based on stable characteristics such as `Group.identifier`.\\\\n\\\\nThe [Patient Compartment](https://www.hl7.org/fhir/compartmentdefinition-patient.html) SHOULD be used as a point of reference for recommended resources to be returned and, where applicable, Patient resources SHOULD be returned. Other resources outside of the patient compartment that are helpful in interpreting the patient data (such as Organization and Practitioner) MAY also be returned.");
	        		List<CapabilityStatementRestResourceOperationComponent> compList = new ArrayList<>();
	        		compList.add(operationComp);
					resource.setOperation(compList);
				}
	        }
	        
	        
//	        CapabilityStatementRestResourceComponent capabilityForGroupExport = new CapabilityStatementRestResourceComponent();
//	        
//	        List<CapabilityStatementRestResourceComponent> listCapability = new ArrayList<CapabilityStatement.CapabilityStatementRestResourceComponent>();
//	        
//	        
//	        //Set Group
//	        capabilityForGroupExport.setType("Group");
//	        ((CapabilityStatementRestResourceOperationComponent) capabilityForGroupExport.addOperation().addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/capabilitystatement-expectation", new CodeType("SHOULD"))))
//	        .setDefinition("http://hl7.org/fhir/uv/bulkdata/OperationDefinition/group-export")
//	        .setDocumentation("FHIR Operation to obtain a detailed set of FHIR resources of diverse resource types pertaining to all patients in specified [Group](https://www.hl7.org/fhir/group.html).\\n\\nIf a FHIR server supports Group-level data export, it SHOULD support reading and searching for `Group` resource. This enables clients to discover available groups based on stable characteristics such as `Group.identifier`.\\n\\nThe [Patient Compartment](https://www.hl7.org/fhir/compartmentdefinition-patient.html) SHOULD be used as a point of reference for recommended resources to be returned and, where applicable, Patient resources SHOULD be returned. Other resources outside of the patient compartment that are helpful in interpreting the patient data (such as Organization and Practitioner) MAY also be returned.")
//	        .setName("group-export");
//	        listCapability.add(capabilityForGroupExport);
//	        //Set Patient
//	        CapabilityStatementRestResourceComponent capabilityForPatientExport = new CapabilityStatementRestResourceComponent();
//	        capabilityForPatientExport.setType("Patient");
//	        ((CapabilityStatementRestResourceOperationComponent) capabilityForPatientExport.addOperation().addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/capabilitystatement-expectation", new CodeType("SHOULD"))))
//	        .setDefinition("http://hl7.org/fhir/uv/bulkdata/OperationDefinition/patient-export")
//	        .setDocumentation("FHIR Operation to obtain a detailed set of FHIR resources of diverse resource types pertaining to all patients.\\n\\nThe [Patient Compartment](https://www.hl7.org/fhir/compartmentdefinition-patient.html) SHOULD be used as a point of reference for recommended resources to be returned and, where applicable, Patient resources SHOULD be returned. Other resources outside of the patient compartment that are helpful in interpreting the patient data (such as Organization and Practitioner) MAY also be returned.")
//	        .setName("patient-export");
//	        listCapability.add(capabilityForPatientExport);
	        
	        CodeableConcept serviceCC = new CodeableConcept();
	        List<Coding> theCodingList = new ArrayList<>();
	        Coding theCoding = new Coding();
	        theCoding.setCode("SMART-on-FHIR");
	        theCoding.setSystem("http://terminology.hl7.org/CodeSystem/restful-security-service");
	        theCoding.setDisplay("SMART-on-FHIR");
	        theCodingList.add(theCoding);
	        serviceCC.setCoding(theCodingList);
	        serviceCC.setText("OAuth2 using SMART-on-FHIR profile");
	        restSecurity.getService().add(serviceCC);
	        restSecurity.setCors(true);
	        rest.setSecurity(restSecurity);

	        rest.setResource( resources);
	        rest.setMode(RestfulCapabilityMode.SERVER);
	        
	        List<CapabilityStatementRestResourceOperationComponent> operationList = new ArrayList<>();
	        CapabilityStatementRestResourceOperationComponent exportOperation = new CapabilityStatementRestResourceOperationComponent();
	        exportOperation.setName("export");
	        exportOperation.setDefinition("http://hl7.org/fhir/uv/bulkdata/OperationDefinition/export");
	        exportOperation.setDocumentation("FHIR Operation to export data from a FHIR server, whether or not it is associated with a patient. This supports use cases like backing up a server, or exporting terminology data by restricting the resources returned using the `_type` parameter.");
	        exportOperation.addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/capabilitystatement-expectation", new CodeType("SHOULD")));
	        operationList.add(exportOperation);
	        rest.setOperation(operationList);
	        
	        restList.add(rest);
	        conformance.setRest(restList);
	        return conformance;
	    }

	}

