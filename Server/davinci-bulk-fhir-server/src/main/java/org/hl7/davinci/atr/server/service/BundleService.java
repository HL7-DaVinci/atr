package org.hl7.davinci.atr.server.service;

import java.util.Map;

import org.hl7.davinci.atr.server.model.DafBundle;
import org.hl7.fhir.r4.model.Bundle;

public interface BundleService {

	Bundle createBundle(Bundle theBundle);

}
