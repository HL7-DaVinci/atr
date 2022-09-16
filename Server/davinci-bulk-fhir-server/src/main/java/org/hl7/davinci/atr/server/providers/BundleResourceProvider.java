package org.hl7.davinci.atr.server.providers;

import javax.servlet.http.HttpServletRequest;

import org.hl7.davinci.atr.server.model.DafBundle;
import org.hl7.davinci.atr.server.service.BundleService;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

@Component
public class BundleResourceProvider extends AbstractJaxRsResourceProvider<Bundle> {

	private static final Logger logger = LoggerFactory.getLogger(BundleResourceProvider.class);    

    @Autowired
    BundleService service;
    public BundleResourceProvider(FhirContext fhirContext) {
        super(fhirContext);
    }
    
	@Override
	public Class<Bundle> getResourceType() {
		return Bundle.class;
	}
	
    /**
     * The create  operation saves a new resource to the server, 
     * allowing the server to give that resource an ID and version ID.
     * Create methods must be annotated with the @Create annotation, 
     * and have a single parameter annotated with the @ResourceParam annotation. 
     * This parameter contains the resource instance to be created. 
     * Create methods must return an object of type MethodOutcome . 
     * This object contains the identity of the created resource.
     * Example URL to invoke this method (this would be invoked using an HTTP POST, 
     * with the resource in the POST body): http://<server name>/<context>/fhir/Bundle
     * @param theBundle
     * @return
     */
	@Create
    public MethodOutcome createBundle(@ResourceParam Bundle theBundle, HttpServletRequest request) throws Exception{
		MethodOutcome retVal = null;
		if (request.getHeader("Content-Type") != null
				&& request.getHeader("Content-Type").equals("application/fhir+json")) {
	        // Save this Bundle to the database...
			if(theBundle.hasType()) {
				if(theBundle.getType().toString().equalsIgnoreCase(BundleType.TRANSACTION.toString())) {
					DafBundle dafBundle = service.createBundle(theBundle);
		        	retVal = new MethodOutcome();
				}
				else {
					throw new UnprocessableEntityException("Bundle.type should be transaction.");
				}
			}
			else {
				throw new UnprocessableEntityException("Bundle should have type transaction.");
			}
		} else {
			throw new UnprocessableEntityException("Invalid header values!");
		}
		//retVal.setId(new IdType(RESOURCE_TYPE, theBundle.getIdElement().getIdPart(), theBundle.getMeta().getVersionId()));    		
		return retVal;
    }
	
	@Transaction
    public Bundle transactionBundle(@TransactionParam Bundle theBundle, HttpServletRequest request) throws Exception{
		Bundle retVal = null;
		if (request.getHeader("Content-Type") != null
				&& request.getHeader("Content-Type").equals("application/fhir+json")) {
		       // Save this Bundle to the database...
			if(theBundle.hasType()) {
				if(theBundle.getType().toString().equalsIgnoreCase(BundleType.TRANSACTION.toString())) {
					DafBundle dafBundle = service.createBundle(theBundle);
		        	retVal = new Bundle();
				}
				else {
					throw new UnprocessableEntityException("Bundle.type should be transaction.");
				}
			}
			else {
				throw new UnprocessableEntityException("Bundle should have type transaction.");
			}
		} else {
			throw new UnprocessableEntityException("Invalid header values!");
		}
		//retVal.setId(new IdType(RESOURCE_TYPE, theBundle.getIdElement().getIdPart(), theBundle.getMeta().getVersionId()));    		
		return retVal;
    }
}
