package org.hl7.davinci.atr.server.providers;

import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hl7.fhir.r4.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode;
import org.hl7.fhir.r4.model.CapabilityStatement.SystemInteractionComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.SystemRestfulInteraction;
import org.hl7.fhir.r4.model.ContactDetail;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.ResourceType;

public class CapabilityStatementResourceProvider extends ServerCapabilityStatementProvider {


  @Metadata
  public CapabilityStatement getConformance(
      HttpServletRequest request, RequestDetails theRequestDetails) {

    CapabilityStatement conformance = super.getServerConformance(request, theRequestDetails);
    conformance.getFhirVersion();
    conformance.setId("Davinci-Bulk-FHIR-Server");
    conformance.setUrl("http://hl7.org/fhir/us/davinci-atr/CapabilityStatement/atr-producer");
    conformance.setVersion("2.0.0");
    conformance.setName("Davinci Bulk FHIR Server");
    conformance.setStatus(PublicationStatus.ACTIVE);
    conformance.setPublisher("Drajer");
    conformance.setDate(new Date());

    // Set Contact
    List<ContactDetail> contactDetails = new ArrayList<ContactDetail>();
    ContactDetail details = new ContactDetail();
    List<ContactPoint> contactPointList = new ArrayList<ContactPoint>();
    ContactPoint telecom = new ContactPoint();
    telecom.setSystem(ContactPointSystem.OTHER);
    telecom.setValue("http://www.drajer.com/");
    contactPointList.add(telecom);
    details.setTelecom(contactPointList);
    contactDetails.add(details);
    conformance.setContact(contactDetails);

    // Set Description
    conformance.setDescription(
        "Standard Conformance Statement for the open source Reference Davinci Bulk FHIR Server provided by Drajer");
    // Set Instantiates
    /*
     * List<CanonicalType> canonicalTypes = new ArrayList<CanonicalType>();
     * CanonicalType type = new CanonicalType();
     * type.addChild("http://hl7.org/fhir/CapabilityStatement/terminology-server");
     * canonicalTypes.add(type); conformance.setInstantiates(canonicalTypes);
     */
    // Set Software
    CapabilityStatementSoftwareComponent softwareComponent =
        new CapabilityStatementSoftwareComponent();
    softwareComponent.setName("Davinci Bulk FHIR Server");
    softwareComponent.setVersion("1.0");
    softwareComponent.setReleaseDate(new Date());
    conformance.setSoftware(softwareComponent);

    // Set Rest
    List<CapabilityStatementRestComponent> restList =
        new ArrayList<CapabilityStatementRestComponent>();
    CapabilityStatementRestComponent rest = new CapabilityStatementRestComponent();
    rest.setMode(RestfulCapabilityMode.SERVER);

    CapabilityStatementRestSecurityComponent restSecurity =
        new CapabilityStatementRestSecurityComponent();
    restSecurity.setCors(true);
    rest.setSecurity(restSecurity);

    List<SystemInteractionComponent> systemInteractionComponentsList =
        new ArrayList<SystemInteractionComponent>();
    SystemInteractionComponent component = new SystemInteractionComponent();
    component.setCode(SystemRestfulInteraction.TRANSACTION);
    systemInteractionComponentsList.add(component);
    rest.setInteraction(systemInteractionComponentsList);

    List<CapabilityStatementRestResourceOperationComponent> operationsList =
        conformance.getRest().get(0).getOperation();
    // CapabilityStatementRestResourceOperationComponent operation = new
    // CapabilityStatementRestResourceOperationComponent();
    List<CapabilityStatementRestResourceOperationComponent> newList = new ArrayList<CapabilityStatement.CapabilityStatementRestResourceOperationComponent>();
    for(CapabilityStatementRestResourceOperationComponent comp :operationsList) {
    	CapabilityStatementRestResourceOperationComponent newComp = comp;
    	if(comp.getName().equals("member-add")){
    		newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$member-add");
    	}
    	if(comp.getName().equals("member-remove")){
    		newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$member-remove");
    	}
    	if(comp.getName().equals("export") && comp.getDefinition().equals("OperationDefinition/Group-i-export")){
    		newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$export");
    	}
    	if(comp.getName().equals("export") && comp.getDefinition().equals("OperationDefinition/Patient--export")){
    		newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Patient/$export");
    	}
        if(comp.getName().equals("davinci-data-export") && comp.getDefinition().equals("OperationDefinition/Group-i-davinci-data-export")){
            newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$davinci-data-export");
        }
        if(comp.getName().equals("confirm-attribution-list") && comp.getDefinition().equals("OperationDefinition/Group-i-confirm-attribution-list")){
            newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$confirm-attribution-list");
        }
        if(comp.getName().equals("attribution-status") && comp.getDefinition().equals("OperationDefinition/Group-i-attribution-status")){
            newComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$attribution-status");
        }
    	newList.add(newComp);
    }
    rest.setOperation(newList);

    List<CapabilityStatement.CapabilityStatementRestResourceComponent> resources = conformance.getRest().get(0).getResource();
    for(CapabilityStatement.CapabilityStatementRestResourceComponent resource:resources){
    	if(resource.getType().equals(ResourceType.Group.toString())) {
    		List<CapabilityStatementRestResourceOperationComponent> opsList = new ArrayList<CapabilityStatement.CapabilityStatementRestResourceOperationComponent>();
    		CapabilityStatementRestResourceOperationComponent opComp = new CapabilityStatementRestResourceOperationComponent();
    		opComp.setName("export");
    		opComp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$export");
    		opsList.add(opComp);

            CapabilityStatementRestResourceOperationComponent davDataExp = new CapabilityStatementRestResourceOperationComponent();
            davDataExp.setName("$davinci-data-export");
            davDataExp.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$davinci-data-export");
            opsList.add(davDataExp);

            CapabilityStatementRestResourceOperationComponent memberAdd = new CapabilityStatementRestResourceOperationComponent();
            memberAdd.setName("$member-add");
            memberAdd.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$member-add");
            opsList.add(memberAdd);

            CapabilityStatementRestResourceOperationComponent memberRemove = new CapabilityStatementRestResourceOperationComponent();
            memberRemove.setName("$member-remove");
            memberRemove.setDefinition("http://ecr.drajer.com/mal/fhir/Group/{id}/$member-remove");
            opsList.add(memberRemove);
    		resource.setOperation(opsList);
    		
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-group");
    	}
    	if(resource.getType().equals(ResourceType.Coverage.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-coverage");
    	}
    	if(resource.getType().equals(ResourceType.Location.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-location");
    	}
    	if(resource.getType().equals(ResourceType.Organization.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-organization");
    	}
    	if(resource.getType().equals(ResourceType.Patient.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-patient");
    	}
    	if(resource.getType().equals(ResourceType.Practitioner.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-practitioner");
    	}
    	if(resource.getType().equals(ResourceType.PractitionerRole.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-practitionerrole");
    	}
    	if(resource.getType().equals(ResourceType.RelatedPerson.toString())) {
    		resource.setProfile("http://hl7.org/fhir/us/davinci-atr/StructureDefinition/atr-relatedperson");
    	}
        resource.setSearchRevInclude(resource.getSearchInclude()); // HAPI Fhir is not setting this value as per design, manually setting the values
    }
    rest.setResource(resources);

    // rest.setResource(conformance.getRest().get(0).getResource());
    restList.add(rest);
    conformance.setRest(restList);

    return conformance;
  }
}
