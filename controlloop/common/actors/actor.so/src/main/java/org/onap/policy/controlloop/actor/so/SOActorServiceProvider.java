/*-
 * ============LICENSE_START=======================================================
 * SOActorServiceProvider
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.controlloop.actor.so;

import java.util.*;

import org.drools.core.ClassObjectFilter;
import org.drools.core.WorkingMemory;
import org.kie.api.runtime.rule.FactHandle;
import org.onap.policy.aai.*;
import org.onap.policy.controlloop.ControlLoopOperation;
import org.onap.policy.controlloop.VirtualControlLoopEvent;
import org.onap.policy.controlloop.actorServiceProvider.spi.Actor;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.drools.system.PolicyEngine;
import org.onap.policy.so.SOCloudConfiguration;
import org.onap.policy.so.SOManager;
import org.onap.policy.so.SOModelInfo;
import org.onap.policy.so.SORelatedInstance;
import org.onap.policy.so.SORelatedInstanceListElement;
import org.onap.policy.so.SORequest;
import org.onap.policy.so.SORequestDetails;
import org.onap.policy.so.SORequestInfo;
import org.onap.policy.so.SORequestParameters;
import org.onap.policy.so.util.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SOActorServiceProvider implements Actor {
	
	private static final Logger logger = LoggerFactory.getLogger(SOActorServiceProvider.class);

	private static String vnfItemVnfId;

	private String vnfItemVnfType;

	private String vnfItemModelInvariantId;

	private String vnfItemModelVersionId;

	private String vnfItemModelName;

	private String vnfItemModelVersion;

	private String vnfItemModelNameVersionId;

	private static String serviceItemServiceInstanceId;

	private String serviceItemModelInvariantId;

	private String serviceItemModelName;

	private String serviceItemModelType;

	private String serviceItemModelVersion;

	private String serviceItemModelNameVersionId;

	private String vfModuleItemVfModuleName;

	private static String vfModuleItemVfModuleId;

	private String vfModuleItemModelInvariantId;

	private String vfModuleItemModelVersionId;

	private String vfModuleItemModelName;

	private String vfModuleItemModelNameVersionId;

	private String tenantItemTenantId;

	private String cloudRegionItemCloudRegionId;

	private final String MODEL_VERSION_PROPERTY_KEY = "model-ver.model-version";

	private static final ImmutableList<String> recipes = ImmutableList.of(
			"VF Module Create");
	private static final ImmutableMap<String, List<String>> targets = new ImmutableMap.Builder<String, List<String>>()
			.put("VF Module Create", ImmutableList.of("VFC"))
			.build();
	
	@Override
	public String actor() {
		return "SO";
	}

	@Override
	public List<String> recipes() {
		return ImmutableList.copyOf(recipes);
	}

	@Override
	public List<String> recipeTargets(String recipe) {
		return ImmutableList.copyOf(targets.getOrDefault(recipe, Collections.emptyList()));
	}

	@Override
	public List<String> recipePayloads(String recipe) {
		return Collections.emptyList();
	}
	
	/**
	 * SOActorServiceProvider Constructor
	 * 
	 */
	public SOActorServiceProvider() {
		
	}
	
        /**
	 * Constructs and sends an AAI vserver Named Query
	 * 
	 * @param onset
	 * @returns the response to the AAI Named Query
	 */
	private AAINQResponseWrapper AaiNamedQueryRequest(VirtualControlLoopEvent onset) {
		
		// create AAI named-query request with UUID started with ""
		AAINQRequest aainqrequest = new AAINQRequest();
		AAINQQueryParameters aainqqueryparam = new AAINQQueryParameters();
		AAINQNamedQuery aainqnamedquery = new AAINQNamedQuery();
		AAINQInstanceFilters aainqinstancefilter = new AAINQInstanceFilters();

		// queryParameters
		aainqnamedquery.namedQueryUUID = UUID.fromString("4ff56a54-9e3f-46b7-a337-07a1d3c6b469"); // UUID.fromString($params.getAaiNamedQueryUUID()) TO DO: AaiNamedQueryUUID 
		aainqqueryparam.namedQuery = aainqnamedquery;
		aainqrequest.queryParameters = aainqqueryparam;
		//
		// instanceFilters
		//
		Map<String, Map<String, String>> aainqinstancefiltermap = new HashMap<>();
		Map<String, String> aainqinstancefiltermapitem = new HashMap<>();
		aainqinstancefiltermapitem.put("vserver-name", onset.AAI.get("vserver.vserver-name")); // TO DO: get vserver.vname from dcae onset.AAI.get("vserver.vserver-name")
		aainqinstancefiltermap.put("vserver", aainqinstancefiltermapitem);
		aainqinstancefilter.instanceFilter.add(aainqinstancefiltermap);
		aainqrequest.instanceFilters = aainqinstancefilter;
		//
		// print aainqrequest for debug
		//
  		logger.debug("AAI Request sent:");
  		logger.debug(Serialization.gsonPretty.toJson(aainqrequest));
		//
		// Create AAINQRequestWrapper
		//
//		AAINQRequestWrapper aainqRequestWrapper = new AAINQRequestWrapper(onset.requestID, aainqrequest);
		//
		// insert aainqrequest into memory
		//
//		insert(aainqRequestWrapper);
		
  		/*
         * Obtain A&AI credentials from properties.environment file
         * TODO: What if these are null?
         */
        String aaiUrl = PolicyEngine.manager.getEnvironmentProperty("aai.url");
        String aaiUsername = PolicyEngine.manager.getEnvironmentProperty("aai.username");
        String aaiPassword = PolicyEngine.manager.getEnvironmentProperty("aai.password");
		
		//***** send the request *****\\
		AAINQResponse aainqresponse = AAIManager.postQuery(aaiUrl, aaiUsername, aaiPassword,
				aainqrequest, onset.requestID);

		// Check AAI response
		if (aainqresponse == null) {
			System.err.println("Failed to get AAI response");
			
			// Fail and retract everything
			return null;
		} else {
			// Create AAINQResponseWrapper
			AAINQResponseWrapper aainqResponseWrapper = new AAINQResponseWrapper(onset.requestID, aainqresponse);

			// insert aainqResponseWrapper to memory -- Is this needed?
//			insert(aainqResponseWrapper);

			if (logger.isDebugEnabled()) {
				logger.debug("AAI Named Query Response: ");
				logger.debug(Serialization.gsonPretty.toJson(aainqResponseWrapper.aainqresponse));
			}

			extractSOFieldsFromNamedQuery(aainqResponseWrapper, onset);
			return aainqResponseWrapper;
		}
	}

		
	/**
	 * Extract the required fields from the named query response
	 * @param namedQueryResponseWrapper
	 * @param onset
	 */
	private void extractSOFieldsFromNamedQuery(AAINQResponseWrapper namedQueryResponseWrapper, VirtualControlLoopEvent onset) {
		try {
			// vnfItem
			setVnfItemVnfId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.vnfID);
			setVnfItemVnfType(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.vnfType);
			setVnfItemVnfType(vnfItemVnfType.substring(vnfItemVnfType.lastIndexOf("/")+1));
			setVnfItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.modelInvariantId);
			setVnfItemModelVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.modelVersionId);
			setVnfItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).modelName);
			setVnfItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.modelVersionId);
//			setVnfItemModelVersion(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(1).propertyValue);

			AAINQExtraProperties vnfItemExtraProperties = namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties;
			for (AAINQExtraProperty prop : vnfItemExtraProperties.extraProperty) {
				if (prop.propertyName.equals(MODEL_VERSION_PROPERTY_KEY)) {
					setVnfItemModelVersion(prop.propertyValue);
					break;
				}
			}
			// serviceItem
			setServiceItemServiceInstanceId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.serviceInstanceID);
			setServiceItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.modelInvariantId);
			setServiceItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).modelName);
			setServiceItemModelType(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).modelName);
			setServiceItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.modelVersionId);
//			setServiceItemModelVersion(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(1).propertyValue);

			AAINQExtraProperties serviceItemExtraProperties = namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties;
			for (AAINQExtraProperty prop : serviceItemExtraProperties.extraProperty) {
				if (prop.propertyName.equals(MODEL_VERSION_PROPERTY_KEY)) {
					setServiceItemModelVersion(prop.propertyValue);
					break;
				}
			}

			// Find the index for base vf module and non-base vf module
			int baseIndex = -1;
			int nonBaseIndex = -1;
			List<AAINQInventoryResponseItem> inventoryItems = namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems;
			for (AAINQInventoryResponseItem m : inventoryItems) {
				if (m.vfModule != null) {
					if (m.vfModule.isBaseVfModule) {
						baseIndex = inventoryItems.indexOf(m);
					} else if (m.vfModule.isBaseVfModule == false) {
						nonBaseIndex = inventoryItems.indexOf(m);
					}
				}
				//
				if (baseIndex != -1 && nonBaseIndex != -1) {
					break;
				}
			}
			
			// This comes from the base module
			setVfModuleItemVfModuleName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(baseIndex).vfModule.vfModuleName);
			setVfModuleItemVfModuleId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(baseIndex).vfModule.vfModuleId);
			setVfModuleItemVfModuleName(vfModuleItemVfModuleName.replace("Vfmodule", "vDNS"));
			int inventoryItemIndex = nonBaseIndex;
			if(nonBaseIndex == -1) {
				inventoryItemIndex = baseIndex;
			}
			// vfModuleItem - NOT the base module
			setVfModuleItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(inventoryItemIndex).vfModule.modelInvariantId);
			setVfModuleItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(inventoryItemIndex).vfModule.modelVersionId);
			setVfModuleItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(inventoryItemIndex).modelName);
//			setVfModuleItemModelVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(inventoryItemIndex).extraProperties.extraProperty.get(1).propertyValue);

			AAINQExtraProperties vfModuleItemExtraProperties = namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(inventoryItemIndex).extraProperties;
			for (AAINQExtraProperty prop : vfModuleItemExtraProperties.extraProperty) {
				if (prop.propertyName.equals(MODEL_VERSION_PROPERTY_KEY)) {
					setVfModuleItemModelVersionId(prop.propertyValue);
					break;
				}
			}

			// tenantItem
			setTenantItemTenantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(1).tenant.tenantId);

			// cloudRegionItem
			setCloudRegionItemCloudRegionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(1).items.inventoryResponseItems.get(0).cloudRegion.cloudRegionId);
					
		} catch (Exception e) {
			logger.warn("Problem extracting SO data from AAI query response because of {}", e.getMessage(), e);
			return;
		}
	}
	
	/**
	 * Checks whether extracted fields from AAI Named Query are null or not
	 * @return false if some extracted fields are missing, true otherwise
	 */
	private boolean checkExtractedFields() {
		
		if ((getVnfItemVnfId() == null) || (getVnfItemVnfType() == null) ||
			    (getVnfItemModelInvariantId() == null) || (getVnfItemModelName() == null) ||
			    (getVnfItemModelVersion() == null) || (getVnfItemModelNameVersionId() == null) ||
			    (getServiceItemServiceInstanceId() == null) || (getServiceItemModelName() == null) ||
			    (getServiceItemModelType() == null) || (getServiceItemModelVersion() == null) ||
			    (getServiceItemModelNameVersionId() == null) || (getVfModuleItemVfModuleName() == null) ||
			    (getVfModuleItemModelInvariantId() == null) || (getVfModuleItemModelVersionId() == null) ||
			    (getVfModuleItemModelName() == null) || (getVfModuleItemModelNameVersionId() == null) ||
			    (getTenantItemTenantId() == null) || (getCloudRegionItemCloudRegionId() == null)) {
				return false;
			}
		return true;
	}
		
	/**
	 * Construct SO Request
	 * 
	 * @param onset
	 * @param operation
	 * @param policy
	 * @return SORequest
	 * @throws IllegalAccessException 
	 */
	public SORequest constructRequest(VirtualControlLoopEvent onset, ControlLoopOperation operation, Policy policy, WorkingMemory wm) {

		if ("SO".equals(policy.getActor()) && ("VF Module Create".equals(policy.getRecipe())) || "VF Module Delete".equals(policy.getRecipe())) {

			AAINQResponseWrapper aaiNQResponseWrapper = null;
			if (wm != null) {
				Collection<FactHandle> factHandles = wm.getFactHandles(new ClassObjectFilter(AAINQResponseWrapper.class));
				for (FactHandle factHandle : factHandles) {
					if(wm.getObject(factHandle) instanceof AAINQResponseWrapper) {
						AAINQResponseWrapper cachedAaiNQResponseWrapper = (AAINQResponseWrapper) wm.getObject(factHandle);
						if (cachedAaiNQResponseWrapper.requestID.equals(onset.requestID) && onset.target.equals("vserver.vserver-name")
								&& cachedAaiNQResponseWrapper.aainqresponse.inventoryResponseItems.get(0).vserver.vserverName.equals(onset.AAI.get("vserver.vserver-name"))) {
							logger.debug("AAI cached response: " + Serialization.gsonPretty.toJson(cachedAaiNQResponseWrapper.aainqresponse));
							logger.debug("extracting info from A&AI response to construct SO request...");
							extractSOFieldsFromNamedQuery(cachedAaiNQResponseWrapper, onset);
							wm.retract(factHandle);
							aaiNQResponseWrapper = cachedAaiNQResponseWrapper;
							break;
						}
					}
				}
			}

			if (aaiNQResponseWrapper == null) {
				// perform named query request and handle response
				aaiNQResponseWrapper = AaiNamedQueryRequest(onset);
				if (wm != null) {
					logger.debug("Inserting the AAI response to the drools memory");
					wm.insert(aaiNQResponseWrapper);
				}
			}
		} else {
			// for future extension
			return null;
		}

		// check if the fields extracted from named query response are 
		// not null so we can proceed with SO request
		if (!checkExtractedFields()) {
			logger.warn("AAI response is missing some required fields. Cannot proceed with SO Request construction.");
			return null;

		} else {

			// Construct SO Request
			SORequest request = new SORequest();
			request.requestType = policy.getRecipe();
//			request.requestId = onset.requestID;
			request.requestDetails = new SORequestDetails();
			request.requestDetails.modelInfo = new SOModelInfo();
			request.requestDetails.cloudConfiguration = new SOCloudConfiguration();
			request.requestDetails.requestInfo = new SORequestInfo();
			request.requestDetails.requestParameters = new SORequestParameters();
			request.requestDetails.requestParameters.userParams = null;
			//
			// cloudConfiguration
			//
			request.requestDetails.cloudConfiguration.lcpCloudRegionId = getCloudRegionItemCloudRegionId();
			request.requestDetails.cloudConfiguration.tenantId = getTenantItemTenantId();
			//
			// modelInfo
			//
			request.requestDetails.modelInfo.modelType = "vfModule";
			request.requestDetails.modelInfo.modelInvariantId = getVfModuleItemModelInvariantId();
			request.requestDetails.modelInfo.modelVersionId = getVfModuleItemModelNameVersionId();
			request.requestDetails.modelInfo.modelName = getVfModuleItemModelName();
			request.requestDetails.modelInfo.modelVersion = getVfModuleItemModelVersionId();
			//
			// requestInfo
			//
			request.requestDetails.requestInfo.instanceName = getVfModuleItemVfModuleName();
			request.requestDetails.requestInfo.source = "POLICY";
			request.requestDetails.requestInfo.suppressRollback = false;
			request.requestDetails.requestInfo.requestorId = "policy";
			//
			// relatedInstanceList
			//
			SORelatedInstanceListElement relatedInstanceListElement1 = new SORelatedInstanceListElement();
			SORelatedInstanceListElement relatedInstanceListElement2 = new SORelatedInstanceListElement();
			relatedInstanceListElement1.relatedInstance = new SORelatedInstance();
			relatedInstanceListElement2.relatedInstance = new SORelatedInstance();
			//
			relatedInstanceListElement1.relatedInstance.instanceId = getServiceItemServiceInstanceId();
			relatedInstanceListElement1.relatedInstance.modelInfo = new SOModelInfo();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelType = "service";
			relatedInstanceListElement1.relatedInstance.modelInfo.modelInvariantId = getServiceItemModelInvariantId();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelVersionId = getServiceItemModelNameVersionId();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelName = getServiceItemModelName();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelVersion = getServiceItemModelVersion();
			//
			relatedInstanceListElement2.relatedInstance.instanceId = getVnfItemVnfId();
			relatedInstanceListElement2.relatedInstance.modelInfo = new SOModelInfo();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelType = "vnf";
			relatedInstanceListElement2.relatedInstance.modelInfo.modelInvariantId = getVnfItemModelInvariantId();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelVersionId = getVnfItemModelNameVersionId();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelName = getVnfItemModelName();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelVersion = getVnfItemModelVersion();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelCustomizationName = getVnfItemVnfType();
			//	
			request.requestDetails.relatedInstanceList.add(relatedInstanceListElement1);
			request.requestDetails.relatedInstanceList.add(relatedInstanceListElement2);

			if (policy.getPayload() != null && policy.getPayload().get("usePreload") != null && policy.getPayload().get("usePreload").equalsIgnoreCase("true")) {
				request.requestDetails.requestParameters.usePreload = true;
			}

			//
			// print SO request for debug
			//
			logger.debug("SO request sent:");
			logger.debug(Serialization.gsonPretty.toJson(request));

			return request;
		}
	}
	
	/**
	 * This method is needed to get the serviceInstanceId and vnfInstanceId which is used
	 * in the asyncSORestCall 
	 * 
	 * @param wm
	 * @param request
	 */
	public static void sendRequest(String requestID, WorkingMemory wm, Object request) {
		SOManager Mm = new SOManager();
		Mm.asyncSORestCall(requestID, wm, getServiceItemServiceInstanceId(), getVnfItemVnfId(), getVfModuleItemVfModuleId(), (SORequest)request);
	}
		
	/**
	 * @return the vnfItemVnfId
	 */
	public static String getVnfItemVnfId() {
		return vnfItemVnfId;
	}

	/**
	 * @param vnfItemVnfId the vnfItemVnfId to set
	 */
	private static void setVnfItemVnfId(String vnfItemVnfId) {
		SOActorServiceProvider.vnfItemVnfId = vnfItemVnfId;
	}

	/**
	 * @return the vnfItemVnfType
	 */
	public String getVnfItemVnfType() {
		return this.vnfItemVnfType;
	}

	/**
	 * @param vnfItemVnfType the vnfItemVnfType to set
	 */
	private void setVnfItemVnfType(String vnfItemVnfType) {
		this.vnfItemVnfType = vnfItemVnfType;
	}

	/**
	 * @return the vnfItemModelInvariantId
	 */
	public String getVnfItemModelInvariantId() {
		return this.vnfItemModelInvariantId;
	}

	/**
	 * @param vnfItemModelInvariantId the vnfItemModelInvariantId to set
	 */
	private void setVnfItemModelInvariantId(String vnfItemModelInvariantId) {
		this.vnfItemModelInvariantId = vnfItemModelInvariantId;
	}

	/**
	 * @return the vnfItemModelVersionId
	 */
	public String getVnfItemModelVersionId() {
		return this.vnfItemModelVersionId;
	}

	/**
	 * @param vnfItemModelVersionId the vnfItemModelVersionId to set
	 */
	private void setVnfItemModelVersionId(String vnfItemModelVersionId) {
		this.vnfItemModelVersionId = vnfItemModelVersionId;
	}

	/**
	 * @return the vnfItemModelName
	 */
	public String getVnfItemModelName() {
		return this.vnfItemModelName;
	}

	/**
	 * @param vnfItemModelName the vnfItemModelName to set
	 */
	private void setVnfItemModelName(String vnfItemModelName) {
		this.vnfItemModelName = vnfItemModelName;
	}

	/**
	 * @return the vnfItemModelVersion
	 */
	public String getVnfItemModelVersion() {
		return this.vnfItemModelVersion;
	}

	/**
	 * @param vnfItemModelVersion the vnfItemModelVersion to set
	 */
	private void setVnfItemModelVersion(String vnfItemModelVersion) {
		this.vnfItemModelVersion = vnfItemModelVersion;
	}

	/**
	 * @return the vnfItemModelNameVersionId
	 */
	public String getVnfItemModelNameVersionId() {
		return this.vnfItemModelNameVersionId;
	}

	/**
	 * @param vnfItemModelNameVersionId the vnfItemModelNameVersionId to set
	 */
	private void setVnfItemModelNameVersionId(String vnfItemModelNameVersionId) {
		this.vnfItemModelNameVersionId = vnfItemModelNameVersionId;
	}

	/**
	 * @return the serviceItemServiceInstanceId
	 */
	public static String getServiceItemServiceInstanceId() {
		return serviceItemServiceInstanceId;
	}

	/**
	 * @param serviceItemServiceInstanceId the serviceItemServiceInstanceId to set
	 */
	private static void setServiceItemServiceInstanceId(
			String serviceItemServiceInstanceId) {
		SOActorServiceProvider.serviceItemServiceInstanceId = serviceItemServiceInstanceId;
	}

	/**
	 * @return the serviceItemModelInvariantId
	 */
	public String getServiceItemModelInvariantId() {
		return this.serviceItemModelInvariantId;
	}

	/**
	 * @param serviceItemModelInvariantId the serviceItemModelInvariantId to set
	 */
	private void setServiceItemModelInvariantId(String serviceItemModelInvariantId) {
		this.serviceItemModelInvariantId = serviceItemModelInvariantId;
	}

	/**
	 * @return the serviceItemModelName
	 */
	public String getServiceItemModelName() {
		return this.serviceItemModelName;
	}

	/**
	 * @param serviceItemModelName the serviceItemModelName to set
	 */
	private void setServiceItemModelName(String serviceItemModelName) {
		this.serviceItemModelName = serviceItemModelName;
	}

	/**
	 * @return the serviceItemModelType
	 */
	public String getServiceItemModelType() {
		return this.serviceItemModelType;
	}

	/**
	 * @param serviceItemModelType the serviceItemModelType to set
	 */
	private void setServiceItemModelType(String serviceItemModelType) {
		this.serviceItemModelType = serviceItemModelType;
	}

	/**
	 * @return the serviceItemModelVersion
	 */
	public String getServiceItemModelVersion() {
		return this.serviceItemModelVersion;
	}

	/**
	 * @param serviceItemModelVersion the serviceItemModelVersion to set
	 */
	private void setServiceItemModelVersion(String serviceItemModelVersion) {
		this.serviceItemModelVersion = serviceItemModelVersion;
	}

	/**
	 * @return the serviceItemModelNameVersionId
	 */
	public String getServiceItemModelNameVersionId() {
		return this.serviceItemModelNameVersionId;
	}

	/**
	 * @param serviceItemModelNameVersionId the serviceItemModelNameVersionId to set
	 */
	private void setServiceItemModelNameVersionId(
			String serviceItemModelNameVersionId) {
		this.serviceItemModelNameVersionId = serviceItemModelNameVersionId;
	}

	/**
	 * @return the vfModuleItemVfModuleName
	 */
	public String getVfModuleItemVfModuleName() {
		return this.vfModuleItemVfModuleName;
	}

	/**
	 * @param vfModuleItemVfModuleName the vfModuleItemVfModuleName to set
	 */
	private void setVfModuleItemVfModuleName(String vfModuleItemVfModuleName) {
		this.vfModuleItemVfModuleName = vfModuleItemVfModuleName;
	}

	/**
	 * @return the vfModuleItemModelInvariantId
	 */
	public String getVfModuleItemModelInvariantId() {
		return this.vfModuleItemModelInvariantId;
	}

	/**
	 * @param vfModuleItemModelInvariantId the vfModuleItemModelInvariantId to set
	 */
	private void setVfModuleItemModelInvariantId(String vfModuleItemModelInvariantId) {
		this.vfModuleItemModelInvariantId = vfModuleItemModelInvariantId;
	}

	/**
	 * @return the vfModuleItemModelVersionId
	 */
	public String getVfModuleItemModelVersionId() {
		return this.vfModuleItemModelVersionId;
	}

	/**
	 * @param vfModuleItemModelVersionId the vfModuleItemModelVersionId to set
	 */
	private void setVfModuleItemModelVersionId(
			String vfModuleItemModelVersionId) {
		this.vfModuleItemModelVersionId = vfModuleItemModelVersionId;
	}

	/**
	 * @return the vfModuleItemModelName
	 */
	public String getVfModuleItemModelName() {
		return this.vfModuleItemModelName;
	}

	/**
	 * @param vfModuleItemModelName the vfModuleItemModelName to set
	 */
	private void setVfModuleItemModelName(String vfModuleItemModelName) {
		this.vfModuleItemModelName = vfModuleItemModelName;
	}

	/**
	 * @return the vfModuleItemModelNameVersionId
	 */
	public String getVfModuleItemModelNameVersionId() {
		return this.vfModuleItemModelNameVersionId;
	}

	/**
	 * @param vfModuleItemModelNameVersionId the vfModuleItemModelNameVersionId to set
	 */
	private void setVfModuleItemModelNameVersionId(
			String vfModuleItemModelNameVersionId) {
		this.vfModuleItemModelNameVersionId = vfModuleItemModelNameVersionId;
	}

	/**
	 * @return the tenantItemTenantId
	 */
	public String getTenantItemTenantId() {
		return this.tenantItemTenantId;
	}

	/**
	 * @param tenantItemTenantId the tenantItemTenantId to set
	 */
	private void setTenantItemTenantId(String tenantItemTenantId) {
		this.tenantItemTenantId = tenantItemTenantId;
	}

	/**
	 * @return the cloudRegionItemCloudRegionId
	 */
	public String getCloudRegionItemCloudRegionId() {
		return this.cloudRegionItemCloudRegionId;
	}

	/**
	 * @param cloudRegionItemCloudRegionId the cloudRegionItemCloudRegionId to set
	 */
	private void setCloudRegionItemCloudRegionId(
			String cloudRegionItemCloudRegionId) {
		this.cloudRegionItemCloudRegionId = cloudRegionItemCloudRegionId;
	}

	public static String getVfModuleItemVfModuleId() {
		return vfModuleItemVfModuleId;
	}

	public void setVfModuleItemVfModuleId(String vfModuleItemVfModuleId) {
		this.vfModuleItemVfModuleId = vfModuleItemVfModuleId;
	}
}
