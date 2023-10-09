package org.hl7.davinci.atr.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hl7.davinci.atr.server.constants.TextConstants;
import org.hl7.davinci.atr.server.dao.GroupDao;
import org.hl7.davinci.atr.server.model.DafGroup;
import org.hl7.davinci.atr.server.util.FhirUtility;
import org.hl7.davinci.atr.server.util.SearchParameterMap;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Group;
import org.hl7.fhir.r4.model.Group.GroupMemberComponent;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {
	
	public static final String RESOURCE_TYPE = "Group";
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);  
	@Autowired
	private FhirContext fhirContext;
	
	@Autowired
    private GroupDao groupDao;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private PractitionerService practitionerService;
	
	@Autowired
	private PractitionerRoleService practitionerRoleService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private CoverageService coverageService;

	@Override
	public DafGroup updateGroupById(int id, Group theGroup) {
		return groupDao.updateGroupById(id, theGroup);
	}

	@Override
	public DafGroup createGroup(Group theGroup) {
		return groupDao.createGroup(theGroup);
	}

	@Override
	public DafGroup getGroupById(String id) {
		DafGroup dafGroup = null;
		try {
			dafGroup = groupDao.getGroupById(id);
		}
		catch(Exception e) {
			logger.error("Exception in getGroupById of GroupServiceImpl ", e);
		}
		return dafGroup;
	}

	@Override
	public List<Group> search(SearchParameterMap paramMap) {
		Group group = null;
		List<Group> groupList = new ArrayList<>();
		try {
			IParser jsonParser = fhirContext.newJsonParser();
			List<DafGroup> dafGroupList = groupDao.search(paramMap);
			if(dafGroupList != null && !dafGroupList.isEmpty()) {
				for(DafGroup dafGroup : dafGroupList) {
					group = jsonParser.parseResource(Group.class, dafGroup.getData());
					String str = fhirContext.newJsonParser().encodeResourceToString(group);
					logger.info("GROUP IN STRING :: \n {}", str);
					group.setId(group.getId());
					groupList.add(group);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return groupList;
	}

	@Override
	public DafGroup addMemberToGroup(Group group, DafGroup dafGroup, String patientMemberId, String providerId, String providerReferenceResource, String coverageId, Period attributionPeriod) {
		DafGroup createdGroup = null;
		try {
			List<GroupMemberComponent> memberList = new ArrayList<>();
			boolean isAttributionCoverageFound = false;
			boolean isMemberFound = false;
			if(group.hasMember()) {
				memberList = group.getMember();
				for (GroupMemberComponent memberGroup : new ArrayList<GroupMemberComponent>(memberList)) {
					//GroupMemberComponent memberGroup = iterator.next();
					String entityId = getEntityIdFromGroupMemberComponent(memberGroup);
					String attributeProviderId = getAttributeProviderIdFromGroupMemberComponent(memberGroup);
					if(StringUtils.isNotBlank(entityId) && StringUtils.isNotBlank(attributeProviderId)) {
						if(patientMemberId.equalsIgnoreCase(entityId) && providerId.equalsIgnoreCase(attributeProviderId)) {
							isMemberFound = true;
							if(StringUtils.isNotBlank(coverageId)) {
								isAttributionCoverageFound = updateGroupMemberComponentCoverageReferenceExtension(memberGroup, coverageId, isAttributionCoverageFound);
							}
							if(attributionPeriod != null) {
								updateGroupMemberComponentAttributionPeriod(memberGroup, isAttributionCoverageFound, attributionPeriod);
							}
						}
					}
				}
				if(!isMemberFound) {
					GroupMemberComponent theGroupMemberComponent = FhirUtility.getGroupMemberComponent(patientMemberId, providerId, providerReferenceResource, coverageId, attributionPeriod);
					if(theGroupMemberComponent != null) {
						memberList.add(theGroupMemberComponent);
						logger.info(" :: Adding one new GroupMemberComponent :: ");
						group.setMember(memberList);
					}
				}
			}
			else {
				List<GroupMemberComponent> newGroupMemberComponentList = null;
				GroupMemberComponent newGroupMemberComponent = FhirUtility.getGroupMemberComponent(patientMemberId, providerId, providerReferenceResource, coverageId, attributionPeriod);
				if(newGroupMemberComponent != null && !newGroupMemberComponent.isEmpty()) {
					newGroupMemberComponentList = new ArrayList<>();
					newGroupMemberComponentList.add(newGroupMemberComponent);
					logger.info(" :: Adding new Member for first time for group :: ");
					group.setMember(newGroupMemberComponentList);
				}
			}
			if(group.hasMeta()) {
    			if(group.getMeta().hasVersionId()) {
    				String versionId = group.getMeta().getVersionId();
    				int version = Integer.parseInt(versionId);
    				version = version + 1;
    				group.getMeta().setVersionId(String.valueOf(version));

    			}
    			else {
    				group.getMeta().setVersionId("1");
    			}
    		}
    		else {
    			Meta meta = new Meta();
        		meta.setVersionId("1");
        		group.setMeta(meta);
    		}
			createdGroup = groupDao.createGroup(group);
			String str = fhirContext.newJsonParser().encodeResourceToString(group);
			System.out.println("GROUP IN STRING :: \n"+str);
		}
		catch(Exception e) {
			logger.error("Exception in addMemberToGroup of GroupServiceImpl ", e);
		}
		return createdGroup;
	}

	private String getEntityIdFromGroupMemberComponent(GroupMemberComponent memberGroup) {
		String entityId = null;
		try {
			if(memberGroup.hasEntity() && memberGroup.getEntity().hasReferenceElement()) {
				entityId = memberGroup.getEntity().getReferenceElement().getIdPart();
			}
		}
		catch(Exception e) {
			logger.info("Exception in getEntityIdFromGroupMemberComponent of GroupServiceImpl ", e);
		}
		return entityId;
	}
	
	private String getAttributeProviderIdFromGroupMemberComponent(GroupMemberComponent memberGroup) {
		String attributeProviderId = null;
		try {
			if (memberGroup.hasExtension(TextConstants.MEMBER_PROVIDER_SYSTEM)) {
				if(memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).hasValue()) {
					Reference reference = (Reference) memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).getValue();
					attributeProviderId = reference.getReferenceElement().getIdPart();
				}
			}
		}
		catch(Exception e) {
			logger.info("Exception in getAttributeProviderIdFromGroupMemberComponent of GroupServiceImpl ", e);
		}
		return attributeProviderId;
	}
	/**
	 * updateGroupMemberComponentChangeTypeExtension
	 * @param memberGroup
	 */
	private void updateGroupMemberComponentChangeTypeExtension(GroupMemberComponent memberGroup, String changeCode) {
		try {
			if(StringUtils.isNotBlank(changeCode)) {
				if (memberGroup.hasExtension(TextConstants.MEMBER_CHANGETYPE_SYSTEM)) {
					CodeType codeType = FhirUtility.getCodeType(changeCode);
					memberGroup.getExtensionByUrl(TextConstants.MEMBER_CHANGETYPE_SYSTEM).setValue(codeType);
				}
				else {
					if(memberGroup.hasExtension()) {
						List<Extension> extensionList = memberGroup.getExtension();
						Extension codeExtension = FhirUtility.getExtensionForCodeType(changeCode);
						if(codeExtension != null && !codeExtension.isEmpty()) {
							extensionList.add(codeExtension);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception in updateGroupMemberComponentChangeTypeExtension of GroupServiceImpl ", e);
		}
	}
	
	/**
	 * updateGroupMemberComponentCoverageReferenceExtension
	 * @param memberGroup
	 * @param coverageId
	 */
	private boolean updateGroupMemberComponentCoverageReferenceExtension(GroupMemberComponent memberGroup, String coverageId, boolean isAttributionCoverageFound) {
		try {
			if(StringUtils.isNotBlank(coverageId)) {
				if (memberGroup.hasExtension(TextConstants.MEMBER_COVERAGE_SYSTEM)) {
					if(memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).hasValue()) {
						Reference reference = (Reference) memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).getValue();
						if(!coverageId.equalsIgnoreCase(reference.getReferenceElement().getIdPart())) {
							Reference coverageReference = FhirUtility.getReference(coverageId, "Coverage");
							memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).setValue(coverageReference);
							updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
							isAttributionCoverageFound = true;
						}
						else {
							updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.NOCHANGE_TYPE);
							logger.info(" Coverage nochange ");
							isAttributionCoverageFound = false;
						}
					}
					else {
						Reference coverageReference = FhirUtility.getReference(coverageId, "Coverage");
						memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).setValue(coverageReference);
						updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
						isAttributionCoverageFound = true;
					}
				}
				else {
					if(memberGroup.hasExtension()) {
						List<Extension> extensionList = memberGroup.getExtension();
						Extension coverageExtension = FhirUtility.getExtensionForReference(coverageId, "Coverage", TextConstants.MEMBER_COVERAGE_SYSTEM);
						if(coverageExtension != null && !coverageExtension.isEmpty()) {
							extensionList.add(coverageExtension);
							updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
							isAttributionCoverageFound = true;
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception in updateGroupMemberComponentCoverageReferenceExtension of GroupServiceImpl ", e);
		}
		return isAttributionCoverageFound;
	}
	
	/**
	 * updateGroupMemberComponentAttributionPeriod
	 * @param memberGroup
	 * @param attributionPeriod 
	 */
	private void updateGroupMemberComponentAttributionPeriod(GroupMemberComponent memberGroup, boolean isAttributionCoverageFound, Period attributionPeriod) {
		try {
			if(attributionPeriod != null) {
				Date startOne = null;
				Date endOne = null;
				Date memberStart = null;
				Date memberEnd = null;
				if (attributionPeriod.hasStart()) {
					startOne = attributionPeriod.getStart();
				}
				if (attributionPeriod.hasEnd()) {
					endOne = attributionPeriod.getEnd();
				}
				if(memberGroup.hasPeriod()) {
					Period memberPeriod = memberGroup.getPeriod();
					if(memberPeriod.hasStart()) {
						memberStart = memberPeriod.getStart();
					}
					if(memberPeriod.hasEnd()) {
						memberEnd = memberPeriod.getEnd();
					}
					if (!startOne.equals(memberStart) || !endOne.equals(memberEnd)) { 
						memberGroup.setPeriod(attributionPeriod);
						updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
					}
					else if(!isAttributionCoverageFound){
						updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.NOCHANGE_TYPE);
					}
				}
				else {
					memberGroup.setPeriod(attributionPeriod);
					updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
				}
			}
		}
		catch(Exception e) {
			logger.error("Exception in updateGroupMemberComponentAttributionPeriod of GroupServiceImpl ", e);
		}
	}
//	private void updateGroupMemberComponentProviderReferenceExtension(GroupMemberComponent memberGroup, String providerId, String providerReferenceResource, boolean isAttributionCoverageFound, Period attributionPeriod) {
//		try {
//			if(StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(providerReferenceResource)) {
//				if (memberGroup.hasExtension(TextConstants.MEMBER_PROVIDER_SYSTEM)) {
//					if(memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).hasValue()) {
//						Reference reference = (Reference) memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).getValue();
//						if(!providerId.equalsIgnoreCase(reference.getReferenceElement().getIdPart())) {
//							Reference coverageReference = FhirUtility.getReference(providerId, providerReferenceResource);
//							memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).setValue(coverageReference);
//							if(attributionPeriod != null) {
//								memberGroup.setPeriod(attributionPeriod);
//							}
//							updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
//						}
//						else {
//							logger.info(" isAttributionCoverageFound "+isAttributionCoverageFound);
//							if(!isAttributionCoverageFound) {
//								updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.NOCHANGE_TYPE);
//								logger.info(" Provider nochange ");
//							}
//						}
//					}
//					else {
//						Reference coverageReference = FhirUtility.getReference(providerId, providerReferenceResource);
//						memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).setValue(coverageReference);
//						if(attributionPeriod != null) {
//							memberGroup.setPeriod(attributionPeriod);
//						}
//						updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
//					}
//				}
//				else {
//					if(memberGroup.hasExtension()) {
//						List<Extension> extensionList = memberGroup.getExtension();
//						if(attributionPeriod != null) {
//							memberGroup.setPeriod(attributionPeriod);
//						}
//						Extension coverageExtension = FhirUtility.getExtensionForReference(providerId, providerReferenceResource, TextConstants.MEMBER_PROVIDER_SYSTEM);
//						if(coverageExtension != null && !coverageExtension.isEmpty()) {
//							extensionList.add(coverageExtension);
//							updateGroupMemberComponentChangeTypeExtension(memberGroup, TextConstants.CHANGE_TYPE);
//						}
//					}
//				}
//			}
//		}
//		catch(Exception e) {
//			logger.error("Exception in updateGroupMemberComponentProviderReferenceExtension of GroupServiceImpl ", e);
//		}
//	}

	@Override
	public DafGroup removeMemberFromGroup(Group group, DafGroup dafGroup, String patientMemberId,
			String providerId, String providerReferenceResource, String coverageId,
			Period attributionPeriod) throws Exception {
		logger.info(" patientMemberId :: "+patientMemberId);
		logger.info(" providerId :: "+providerId);
		logger.info(" providerReferenceResource :: "+providerReferenceResource);
		logger.info(" coverageId :: "+coverageId);
		DafGroup createdGroup = null;
			List<GroupMemberComponent> memberList = new ArrayList<>();
			boolean isGroupMemberRemoved = false;
			if(group.hasMember()) {
				memberList = group.getMember();
				for (GroupMemberComponent memberGroup : new ArrayList<GroupMemberComponent>(memberList)) {
					//GroupMemberComponent memberGroup = iterator.next();
					if(memberGroup.hasEntity() && memberGroup.getEntity().hasReferenceElement()) {
						String entityId = memberGroup.getEntity().getReferenceElement().getIdPart();
						logger.info(" entityId :: "+entityId);
						if(patientMemberId.equalsIgnoreCase(entityId)) {
							if(StringUtils.isNotBlank(providerId) && StringUtils.isNotBlank(providerReferenceResource)) {
								if (memberGroup.hasExtension(TextConstants.MEMBER_PROVIDER_SYSTEM)) {
									if(memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).hasValue()) {
										Reference reference = (Reference) memberGroup.getExtensionByUrl(TextConstants.MEMBER_PROVIDER_SYSTEM).getValue();
										if(providerId.equalsIgnoreCase(reference.getReferenceElement().getIdPart())
												&& providerReferenceResource.equalsIgnoreCase(reference.getReferenceElement().getResourceType())) {
											if(StringUtils.isNotBlank(coverageId)) {
												if (memberGroup.hasExtension(TextConstants.MEMBER_COVERAGE_SYSTEM)) {
													if(memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).hasValue()) {
														Reference coverageReference = (Reference) memberGroup.getExtensionByUrl(TextConstants.MEMBER_COVERAGE_SYSTEM).getValue();
														if(coverageId.equalsIgnoreCase(coverageReference.getReferenceElement().getIdPart())) {
															memberList.remove(memberGroup);
															isGroupMemberRemoved = true;
															logger.info(" Removing member from Group.member for memberId+providerNpi+attributionPeriod / "
																	+ "patientReference+providerReference+attributionPeriod. patientMemberId: "+patientMemberId+" providerId: "+providerId+
																	" coverageId : "+coverageId);	
														}
														else {
															throw new ResourceNotFoundException(" No coverage found for given attributionPeriod  "+coverageId);	
														}
													}
												}
											}
											else {
												memberList.remove(memberGroup);
												isGroupMemberRemoved= true;
												logger.info(" Removing member from Group.member for memberId+providerNpi / "
														+ "patientReference+providerReference. patientMemberId: "+patientMemberId+" providerId: "+providerId);	
											}
										}
										else {
											throw new ResourceNotFoundException(" No provider found for given provider "+providerId);	
										}
									}
								}
							}
							else {
								memberList.remove(memberGroup);
								isGroupMemberRemoved = true;
								logger.info(" Removing member from Group.member for memberId/patientReference. patientMemberId : "+patientMemberId);	
							}
							break;
						} 
					}
				}
			}
			else {
				logger.error(" :: Group doesn't have any members ");
			}
			if(isGroupMemberRemoved) {
				if(group.hasMeta()) {
	    			if(group.getMeta().hasVersionId()) {
	    				String versionId = group.getMeta().getVersionId();
	    				int version = Integer.parseInt(versionId);
	    				version = version + 1;
	    				group.getMeta().setVersionId(String.valueOf(version));

	    			}
	    			else {
	    				group.getMeta().setVersionId("1");
	    			}
	    		}
	    		else {
	    			Meta meta = new Meta();
	        		meta.setVersionId("1");
	        		group.setMeta(meta);
	    		}
				createdGroup = groupDao.createGroup(group);
			}
			else {
  			  	throw new UnprocessableEntityException("Group doesn't contain given memberId/patientReference");
			}
			String str = fhirContext.newJsonParser().encodeResourceToString(group);
			System.out.println("GROUP IN STRING :: \n"+str);
		return createdGroup;
	}

	@Override
	public DafGroup processAddMemberToGroup(Parameters theParameters,String groupId) throws Exception{
		String patientMemberId = null;
		String attributeProviderId = null;
		String attributeProviderReferenceResource = null;
		String coverageReference = null;
		DafGroup createdGroup = null;
		Period attributionPeriod = null;
		Group group = null;
		DafGroup dafGroup = getGroupById(groupId);
		if (dafGroup != null && StringUtils.isNotBlank(dafGroup.getData())) {
			group = parseGroup(dafGroup.getData());
			if (group != null && !group.isEmpty() && theParameters != null && !theParameters.isEmpty()) {
				if (theParameters.getParameter(TextConstants.MEMBER_ID) != null
						&& theParameters.getParameter(TextConstants.PROVIDER_NPI) != null) {
					String patientId = findPatientIdByIdentifier(theParameters);
					if(StringUtils.isNotBlank(patientId)) {
						patientMemberId = patientId;
						Map<String, String> providerMap = findProviderIdByIdentifier(theParameters);
						if(providerMap != null && !providerMap.isEmpty()) {
							for ( Map.Entry<String, String> entry : providerMap.entrySet()) {
							    attributeProviderId = entry.getValue();
							    attributeProviderReferenceResource = entry.getKey();
							}
						}
						else {
							throw new ResourceNotFoundException("Couldn't find any Providers with given providerNpi");
						}
						String coverageId = getCoverageByIdentifier(theParameters);
						if(StringUtils.isNotBlank(coverageId)) {
							coverageReference = coverageId;
						}
					}
					else {
						throw new ResourceNotFoundException("Couldn't find any Patient with given memberId");
					}
				}
				else if(theParameters.getParameter(TextConstants.PATIENT_REFERENCE) != null
						&& theParameters.getParameter(TextConstants.PROVIDER_REFERENCE) != null) {
					String patientId = findPatientIdByReference(theParameters);
					if(StringUtils.isNotBlank(patientId)) {
						patientMemberId = patientId;
						Map<String, String> providerMap = findProviderIdByReference(theParameters);
						if(providerMap != null && !providerMap.isEmpty()) {
							for ( Map.Entry<String, String> entry : providerMap.entrySet()) {
							    attributeProviderId = entry.getValue();
							    attributeProviderReferenceResource = entry.getKey();
							}
						}
						else {
							throw new ResourceNotFoundException("Couldn't find any Providers with given providerReference");
						}
						String coverageId = findCoverageIdByPatientId(patientId);
						if(StringUtils.isNotBlank(coverageId)) {
							coverageReference = coverageId;
						}
					}
					else {
						throw new ResourceNotFoundException("Couldn't find any Patient with given patientReference");
					}
				}
				else {
					throw new UnprocessableEntityException("Please provide memberId + providerNpi or patientReference + providerReference to $member-add.");
				}
				if(theParameters.getParameter(TextConstants.ATTRIBUTION_PERIOD) != null) {
					attributionPeriod = (Period) theParameters.getParameter(TextConstants.ATTRIBUTION_PERIOD);
				}
				if (StringUtils.isNotBlank(patientMemberId) && StringUtils.isNotBlank(attributeProviderId)) {
					logger.info(" patientMemberId :: "+patientMemberId);
					logger.info(" attributeProviderId :: "+attributeProviderId);
					logger.info(" attributeProviderReferenceResource :: "+attributeProviderReferenceResource);
					logger.info(" coverageReference :: "+coverageReference);
					if(attributionPeriod != null) {
						logger.info(" attributionPeriod.getStart() :: "+attributionPeriod.getStart());
						logger.info(" attributionPeriod.getEnd() :: "+attributionPeriod.getEnd());
					}
					createdGroup = addMemberToGroup(group, dafGroup, patientMemberId,
							attributeProviderId, attributeProviderReferenceResource, coverageReference,
							attributionPeriod);
					if(createdGroup == null) {
						throw new UnprocessableEntityException("Error while adding member to group");
					}
				} else {
					throw new ResourceNotFoundException("No Patient or Provider found. Please provide valid Patient/Provider");
				}
			} else {
				throw new UnprocessableEntityException("No Parameters/Group found!");
			}
		} else {
			throw new ResourceNotFoundException(" Gorup not found " + groupId);
		}
		return createdGroup;
	}
	
	private String getCoverageByIdentifier(Parameters theParameters) throws Exception {
		String coverageId = null;
		Identifier memberId = (Identifier) theParameters.getParameter(TextConstants.MEMBER_ID);
		Coverage coverage = coverageService.getCoverageByIdentifier(memberId.getSystem(), memberId.getValue());
		if(coverage != null && !coverage.isEmpty()) {
			coverageId = coverage.getIdElement().getIdPart();
		}
		return coverageId;
	}

	private Map<String, String> findProviderIdByIdentifier(Parameters theParameters) throws Exception {
		Map<String, String> providerMap = new HashMap<>();
		Identifier providerNpi = (Identifier) theParameters.getParameter(TextConstants.PROVIDER_NPI);
		Practitioner practitioner = practitionerService
				.getPractitionerByProviderNpi(providerNpi.getSystem(), providerNpi.getValue());
		if (practitioner == null) {
			PractitionerRole practitionerRole = practitionerRoleService
					.getPractitionerRoleByIdentifier(providerNpi.getSystem(), providerNpi.getValue());
			if (practitionerRole == null) {
				Organization organization = organizationService
						.getOrganizationByProviderIdentifier(providerNpi.getSystem(), providerNpi.getValue());
				if (organization != null) {
					providerMap.put("Organization", organization.getIdElement().getIdPart());
				} 
			} else {
				providerMap.put("PractitionerRole", practitionerRole.getIdElement().getIdPart());
			}
		} else {
			providerMap.put("Practitioner", practitioner.getIdElement().getIdPart());
		}
		return providerMap;
	}

	private String findPatientIdByIdentifier(Parameters theParameters) throws Exception {
		String patientId = null;
		Identifier memberId = (Identifier) theParameters.getParameter(TextConstants.MEMBER_ID);
		Patient patient = patientService.getPatientByMemeberId(memberId.getSystem(),
				memberId.getValue());
		if(patient != null && !patient.isEmpty()) {
			patientId = patient.getIdElement().getIdPart();
		}
		return patientId;
	}

	private String findCoverageIdByPatientId(String id) throws Exception {
		String coverageId = null;
		Coverage coverage = coverageService.getCoverageByPatientReference(id);
		if (coverage != null) {
			coverageId = coverage.getIdElement().getIdPart();
		}
		return coverageId;
	}
	
	private Map<String, String> findProviderIdByReference(Parameters theParameters) throws Exception {
		Map<String, String> providerMap = new HashMap<>();
		Reference providerReference = (Reference) theParameters.getParameter(TextConstants.PROVIDER_REFERENCE);
		String providerReferenceResource = providerReference.getReferenceElement().getResourceType();
		if (StringUtils.isNotBlank(providerReferenceResource) && providerReferenceResource.equalsIgnoreCase("Practitioner")) {
			Practitioner practitioner = practitionerService.getPractitionerById(providerReference.getReferenceElement().getIdPart());
			if (practitioner != null && !practitioner.isEmpty()) {
				providerMap.put("Practitioner", practitioner.getIdElement().getIdPart());
			}
		} 
		else if (StringUtils.isNotBlank(providerReferenceResource) && providerReferenceResource.equalsIgnoreCase("PractitionerRole")) {
			PractitionerRole practitionerRole = practitionerRoleService.getPractitionerRoleById(providerReference.getReferenceElement().getIdPart());
			if (practitionerRole != null && !practitionerRole.isEmpty()) {
				providerMap.put("PractitionerRole", practitionerRole.getIdElement().getIdPart());
			} 
		}
		else if (StringUtils.isNotBlank(providerReferenceResource) && providerReferenceResource.equalsIgnoreCase("Organization")) {
			Organization organization = organizationService.getOrganizationById(providerReference.getReferenceElement().getIdPart());
			if (organization != null && !organization.isEmpty()) {
				providerMap.put("Organization", organization.getIdElement().getIdPart());
			} 
		}
		return providerMap;
	}
	
	private String findPatientIdByReference(Parameters theParameters) throws Exception {
		String patientId = null;
		Reference patientReference = (Reference) theParameters.getParameter(TextConstants.PATIENT_REFERENCE);
		System.out.println(" patientReference.getReferenceElement().getIdPart() "+patientReference.getReferenceElement().getIdPart());
		System.out.println(" patientReference.getReference() "+patientReference.getReference());

		Patient patient = patientService
				.getPatientById(patientReference.getReferenceElement().getIdPart());
		if (patient != null) {
			patientId = patient.getIdElement().getIdPart();
		}
		return patientId;
	}
		
	private Group parseGroup(String data) {
		Group group = null;
		try {
			if (StringUtils.isNotBlank(data)) {
				IParser jsonParser = fhirContext.newJsonParser();
				group = jsonParser.parseResource(Group.class, data);
			}
		} catch (Exception e) {
			logger.error("Exception in parseGroup of GroupResourceProvider ", e);
		}
		return group;
	}

	@Override
	public DafGroup processRemoveMemberToGroup(Parameters theParameters, String groupId) throws Exception {
		String patientMemberId = null;
		String attributeProviderId = null;
		String attributeProviderReferenceResource = null;
		String coverageReference = null;
		DafGroup createdGroup = null;
		Period attributionPeriod = null;
		Group group = null;
		DafGroup dafGroup = getGroupById(groupId);
		if (dafGroup != null && StringUtils.isNotBlank(dafGroup.getData())) {
			group = parseGroup(dafGroup.getData());
			if (group != null && !group.isEmpty() && theParameters != null && !theParameters.isEmpty()) {
				if (theParameters.getParameter(TextConstants.MEMBER_ID) != null) {
					String patientId = findPatientIdByIdentifier(theParameters);
					if(StringUtils.isNotBlank(patientId)) {
						patientMemberId = patientId;
						if(theParameters.getParameter(TextConstants.PROVIDER_NPI) != null) {
							Map<String, String> providerMap = findProviderIdByIdentifier(theParameters);
							if(providerMap != null && !providerMap.isEmpty()) {
								for ( Map.Entry<String, String> entry : providerMap.entrySet()) {
								    attributeProviderId = entry.getValue();
								    attributeProviderReferenceResource = entry.getKey();
								}
							}
							else {
								throw new ResourceNotFoundException("Couldn't find any Providers with given providerNpi");
							}
						}
						String coverageId = getCoverageByIdentifier(theParameters);
						if(StringUtils.isNotBlank(coverageId)) {
							coverageReference = coverageId;
						}
					}
					else {
						throw new ResourceNotFoundException("Couldn't find any Patient with given memberId");
					}
				}
				else if(theParameters.getParameter(TextConstants.PATIENT_REFERENCE) != null) {
					String patientId = findPatientIdByReference(theParameters);
					if(StringUtils.isNotBlank(patientId)) {
						patientMemberId = patientId;
						if(theParameters.getParameter(TextConstants.PROVIDER_REFERENCE) != null) {
							Map<String, String> providerMap = findProviderIdByReference(theParameters);
							if(providerMap != null && !providerMap.isEmpty()) {
								for ( Map.Entry<String, String> entry : providerMap.entrySet()) {
								    attributeProviderId = entry.getValue();
								    attributeProviderReferenceResource = entry.getKey();
								}
							}
							else {
								throw new ResourceNotFoundException("Couldn't find any Providers with given providerReference");
							}
						}
						String coverageId = findCoverageIdByPatientId(patientId);
						if(StringUtils.isNotBlank(coverageId)) {
							coverageReference = coverageId;
						}
					}
					else {
						throw new ResourceNotFoundException("Couldn't find any Patient with given patientReference");
					}
				}
				else {
					throw new UnprocessableEntityException("Please provide memberId + providerNpi or patientReference + providerReference to $member-add.");
				}
				if(theParameters.getParameter(TextConstants.ATTRIBUTION_PERIOD) != null) {
					attributionPeriod = (Period) theParameters.getParameter(TextConstants.ATTRIBUTION_PERIOD);
				}
				if (StringUtils.isNotBlank(patientMemberId)) {
					logger.info(" patientMemberId :: "+patientMemberId);
					logger.info(" attributeProviderId :: "+attributeProviderId);
					logger.info(" attributeProviderReferenceResource :: "+attributeProviderReferenceResource);
					logger.info(" coverageReference :: "+coverageReference);
					if(attributionPeriod != null) {
						logger.info(" attributionPeriod.getStart() :: "+attributionPeriod.getStart());
						logger.info(" attributionPeriod.getEnd() :: "+attributionPeriod.getEnd());
					}
					createdGroup = removeMemberFromGroup(group, dafGroup, patientMemberId,
								attributeProviderId, attributeProviderReferenceResource, coverageReference,
								attributionPeriod);
					if(createdGroup == null) {
						throw new UnprocessableEntityException("Error while removing member from group!");
					}
					
				} else {
					throw new ResourceNotFoundException("No patient found ");
				}
			} else {
				throw new UnprocessableEntityException("No Parameters/Group found!");
			}
		} else {
			throw new ResourceNotFoundException(" Gorup not found " + groupId);
		}
		return createdGroup;
	}

	@Override
	public DafGroup getGroupByVersionId(String idPart, String versionIdPart) {
		return groupDao.getGroupByVersionId(idPart, versionIdPart);
	}
}