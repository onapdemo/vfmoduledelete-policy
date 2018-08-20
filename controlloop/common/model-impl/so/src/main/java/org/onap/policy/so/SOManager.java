/*-
 * ============LICENSE_START=======================================================
 * mso
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

package org.onap.policy.so;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.so.util.Serialization;
import org.onap.policy.drools.system.PolicyEngine;
import org.onap.policy.rest.RESTManager;
import org.onap.policy.rest.RESTManager.Pair;
import org.drools.core.WorkingMemory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public final class SOManager {

	private static final Logger logger = LoggerFactory.getLogger(SOManager.class);
	private static final Logger netLogger = LoggerFactory.getLogger(org.onap.policy.drools.event.comm.Topic.NETWORK_LOGGER);
	private static ExecutorService executors = Executors.newCachedThreadPool();
	private static final int SO_RESPONSE_ERROR = 999;
	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final int  GET_REQUESTS_BEFORE_TIMEOUT = 20;
	private static final long GET_REQUEST_WAIT_INTERVAL = 20000;
	private static final String MEDIA_TYPE = "application/json";
	// The REST manager used for processing REST calls for this VFC manager
	private RESTManager restManager;

	/**
	 * Default constructor
	 */
	public SOManager() {
		restManager = new RESTManager();
	}


	public static SOResponse createModuleInstance(String url, String urlBase, String username, String password, SORequest request) {

		//
		// Call REST
		//
		Map<String, String> headers = new HashMap<String, String>();
		//headers.put("X-FromAppId", "POLICY");
		//headers.put("X-TransactionId", requestID.toString());
		headers.put("Accept", "application/json");
		
		//
		// 201 - CREATED - you are done just return 
		//
		String requestJson = Serialization.gsonPretty.toJson(request);
		netLogger.info("[OUT|{}|{}|]{}{}", "SO", url, System.lineSeparator(), requestJson);
		Pair<Integer, String> httpDetails = RESTManager.post(url, username, password, headers, "application/json", requestJson);
		
		if (httpDetails == null) {
			return null;
		}
		
		if (httpDetails.a == 202) {
			try {
				SOResponse response = Serialization.gsonPretty.fromJson(httpDetails.b, SOResponse.class);
				
				String body = Serialization.gsonPretty.toJson(response);
				logger.debug("***** Response to post:");
				logger.debug(body);
				
				String requestId = response.requestReferences.requestId;
				int attemptsLeft = GET_REQUESTS_BEFORE_TIMEOUT;
				
				//String getUrl = "/orchestrationRequests/v2/"+requestId;
				String urlGet = urlBase + "/orchestrationRequests/v2/"+requestId;
				SOResponse responseGet = null;
				
				while(attemptsLeft-- > 0){
					
					Pair<Integer, String> httpDetailsGet = RESTManager.get(urlGet, username, password, headers);
					responseGet = Serialization.gsonPretty.fromJson(httpDetailsGet.b, SOResponse.class);
					netLogger.info("[IN|{}|{}|]{}{}", "SO", urlGet, System.lineSeparator(), httpDetailsGet.b);
                    
					body = Serialization.gsonPretty.toJson(responseGet);
					logger.debug("***** Response to get:");
					logger.debug(body);
					
					if(httpDetailsGet.a == 200){
						if(responseGet.request.requestStatus.requestState.equalsIgnoreCase("COMPLETE") || 
								responseGet.request.requestStatus.requestState.equalsIgnoreCase("FAILED")){
							logger.debug("***** ########  VF Module Creation "+responseGet.request.requestStatus.requestState);
							return responseGet;
						}
					}
					Thread.sleep(GET_REQUEST_WAIT_INTERVAL);
				}

				if (responseGet != null
				 && responseGet.request != null
				 &&	responseGet.request.requestStatus != null
				 && responseGet.request.requestStatus.requestState != null) {
					logger.warn("***** ########  VF Module Creation timeout. Status: ( {})", responseGet.request.requestStatus.requestState);
				}

				return responseGet;
			} catch (JsonSyntaxException e) {
				logger.error("Failed to deserialize into SOResponse: ", e);
			} catch (InterruptedException e) {
				logger.error("Interrupted exception: ", e);
				Thread.currentThread().interrupt();
			}
		}
		
		
		
		
		return null;
	}

	/**
	 * 
	 * @param requestID
	 * @param wm
	 * @param serviceInstanceId
	 * @param vnfInstanceId
	 * @param vfModuleId
	 * @param request
	 * 
	 * This method makes an asynchronous Rest call to MSO and inserts the response into the Drools working memory
	 */
	  public void asyncSORestCall(String requestID, WorkingMemory wm, String serviceInstanceId, String vnfInstanceId, String vfModuleId, SORequest request) {
		  executors.submit(new Runnable()
		  	{
			  @Override
			  	public void run()
			  {
				  try {
					  String serverRoot = PolicyEngine.manager.getEnvironmentProperty("so.url");
					  String username = PolicyEngine.manager.getEnvironmentProperty("so.username");
					  String password = PolicyEngine.manager.getEnvironmentProperty("so.password");
					  

					  String auth = username + ":" + password;
					  
					  Map<String, String> headers = new HashMap<String, String>();
					  byte[] encodedBytes = Base64.getEncoder().encode(auth.getBytes());
					  headers.put("Accept", "application/json");
					  headers.put("Authorization", "Basic " + new String(encodedBytes));
					  
					  Gson gsonPretty = new GsonBuilder().disableHtmlEscaping()
							  .setPrettyPrinting()
							  .create();
					  
					  String soJson = gsonPretty.toJson(request);
					  
					  SOResponse so = new SOResponse();
					  Pair<Integer, String> httpResponse = null;
					  String url = null;
					  if(request.requestType.equalsIgnoreCase("VF Module Create")){
					  		url = serverRoot + "/serviceInstances/v5/" + serviceInstanceId + "/vnfs/" + vnfInstanceId + "/vfModules";
					  		netLogger.info("[OUT|{}|{}|]{}{}", "SO", url, System.lineSeparator(), soJson);
					  		httpResponse = RESTManager.post(url, "policy", "policy", headers, "application/json", soJson);
					  } else if(request.requestType.equalsIgnoreCase("VF Module Delete")) {
					  		url = serverRoot + "/serviceInstances/v5/" + serviceInstanceId + "/vnfs/" + vnfInstanceId + "/vfModules/" + vfModuleId;
					  		netLogger.info("[OUT|{}|{}|]{}{}", "SO", url, System.lineSeparator(), soJson);
					  		httpResponse = RESTManager.delete(url, "policy", "policy", headers, "application/json", soJson);
					  }
						logger.info("SO response : " + httpResponse);
					  // Process the response from SO
					  SOResponse response =  waitForSOOperationCompletion(serverRoot, username, password, url, httpResponse);

					  // Return the response to Drools in its working memory
					  SOResponseWrapper soWrapper = new SOResponseWrapper(response, requestID);
					  wm.insert(soWrapper);
					  logger.info("SOResponse inserted " + gsonPretty.toJson(soWrapper));
				  } catch (Exception e) {
					  logger.error("Error while performing asyncSORestCall: "+ e.getMessage(),e);
					  
					  // create dummy SO object to trigger cleanup
					  SOResponse so = new SOResponse();
					  so.httpResponseCode = SO_RESPONSE_ERROR;
					  wm.insert(so);
				  }
			  }
		  	});
	  }

	/**
	 * Wait for the SO operation we have ordered to complete.
	 * @param urlBaseSO The base URL for SO
	 * @param username user name on SO
	 * @param password password on SO
	 * @param initialRequestURL The URL of the initial HTTP request
	 * @param initialHTTPResponse The initial HTTP message returned from SO
	 * @return The parsed final response of SO to the request
	 */
	private SOResponse waitForSOOperationCompletion(final String urlBaseSO, final String username, final String password,
													final String initialRequestURL, final Pair<Integer, String> initialHTTPResponse) {
		// Process the initial response from SO, the response to a post
		SOResponse response = processSOResponse(initialRequestURL, initialHTTPResponse);
		if (SO_RESPONSE_ERROR == response.httpResponseCode) {
			return response;
		}

		// The SO URL to use to get the status of orchestration requests
		String urlGet = urlBaseSO + "/orchestrationRequests/v5/" + response.requestReferences.requestId;

		// The HTTP status code of the latest response
		Pair<Integer, String> latestHTTPResponse = initialHTTPResponse;

		// Wait for the response from SO
		for (int attemptsLeft = GET_REQUESTS_BEFORE_TIMEOUT; attemptsLeft >= 0; attemptsLeft--) {
			// The SO request may have completed even on the first request so we check the response here before
			// issuing any other requests
			if (isRequestStateFinished(latestHTTPResponse, response)) {
				return response;
			}

			// Wait for the defined interval before issuing a get
			try {
				Thread.sleep(GET_REQUEST_WAIT_INTERVAL);
			}
			catch (InterruptedException e) {
				logger.error("Interrupted exception: ", e);
				Thread.currentThread().interrupt();
				response.httpResponseCode = SO_RESPONSE_ERROR;
				return response;
			}

			// Issue a GET to find the current status of our request
			netLogger.info("[OUT|{}|{}|{}|{}|{}|{}|]{}", "SO", urlGet, username, password, createSimpleHeaders(), MEDIA_TYPE, LINE_SEPARATOR);
			Pair<Integer, String> httpResponse = restManager.get(urlGet, username, password, createSimpleHeaders());
			logger.info("Query orchestrationRequests Response: " + httpResponse.b);

			// Get our response
			response = processSOResponse(urlGet, httpResponse);
			if (SO_RESPONSE_ERROR == response.httpResponseCode) {
				return response;
			}

			// Our latest HTTP response code
			latestHTTPResponse = httpResponse;
		}

		// We have timed out on the SO request
		response.httpResponseCode = SO_RESPONSE_ERROR;
		return response;
	}

	/**
	 * Parse the response message from SO into a SOResponse object.
	 * @param requestURL  The URL of the HTTP request
	 * @param httpResponse The HTTP message returned from SO
	 * @return The parsed response
	 */
	private SOResponse processSOResponse(final String requestURL, final Pair<Integer, String> httpResponse) {
		SOResponse response = new SOResponse();

		// A null httpDetails indicates a HTTP problem, a valid response from SO must be either 200 or 202
		if (!httpResultIsNullFree(httpResponse) || (httpResponse.a != 200 && httpResponse.a != 202)) {
			logger.error("Invalid HTTP response received from SO");
			response.httpResponseCode = SO_RESPONSE_ERROR;
			return response;
		}

		// Parse the JSON of the response into our POJO
		try {
			response = Serialization.gsonPretty.fromJson(httpResponse.b, SOResponse.class);
		}
		catch (JsonSyntaxException e) {
			logger.error("Failed to deserialize HTTP response into SOResponse: ", e);
			response.httpResponseCode = SO_RESPONSE_ERROR;
			return response;
		}

		// Set the HTTP response code of the response if needed
		if (response.httpResponseCode == 0) {
			response.httpResponseCode = httpResponse.a;
		}

		netLogger.info("[IN|{}|{}|]{}{}", "SO", requestURL, LINE_SEPARATOR, httpResponse.b);

		if (logger.isDebugEnabled()) {
			logger.debug("***** Response to SO Request to URL {}:", requestURL);
			logger.debug(httpResponse.b);
		}

		return response;
	}

	/**
	 * Check that a HTTP operation result has no nulls.
	 * @param httpOperationResult the result to check
	 * @return true if no nulls are found
	 */
	private boolean httpResultIsNullFree(Pair<Integer, String> httpOperationResult) {
		return httpOperationResult != null && httpOperationResult.a != null && httpOperationResult.b != null;
	}

	/**
	 * Check that the request state of a response is finished.
	 * @param latestHTTPDetails the HTTP details of the response
	 * @param response The response to check
	 * @return true if the request for the response is finished
	 */
	private boolean isRequestStateFinished(final Pair<Integer, String> latestHTTPDetails, final SOResponse response) {
		if (latestHTTPDetails != null && 200 == latestHTTPDetails.a && isRequestStateDefined(response)) {
			String requestState = response.request.requestStatus.requestState;
			return "COMPLETE".equalsIgnoreCase(requestState) || "FAILED".equalsIgnoreCase(requestState);
		}
		else {
			return false;
		}
	}

	/**
	 * Check that the request state of a response is defined.
	 * @param response The response to check
	 * @return true if the request for the response is defined
	 */
	private boolean isRequestStateDefined(final SOResponse response) {
		return response != null &&
				response.request != null &&
				response.request.requestStatus != null &&
				response.request.requestStatus.requestState != null;
	}

	/**
	 * Create simple HTTP headers for unauthenticated requests to SO.
	 * @return the HTTP headers
	 */
	private Map<String, String> createSimpleHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept", MEDIA_TYPE);
		return headers;
	}

}
