/*-
 * ============LICENSE_START=======================================================
 * appclcm
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

package org.onap.policy.appclcm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.UUID;

import org.junit.Test;
import org.onap.policy.appclcm.util.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppcLcmTest {

    private static final Logger logger = LoggerFactory.getLogger(AppcLcmTest.class);
    
    private static LCMRequestWrapper dmaapRequest;
    private static LCMResponseWrapper dmaapResponse;

    static {
        /*
         * Construct an APPCLCM Request to be Serialized
         */
        dmaapRequest = new LCMRequestWrapper();
        dmaapRequest.setCorrelationId("664be3d2-6c12-4f4b-a3e7-c349acced200" + "-" + "1");
        dmaapRequest.setRpcName("restart");
        dmaapRequest.setType("request");

        dmaapResponse = new LCMResponseWrapper();
        dmaapResponse.setCorrelationId("664be3d2-6c12-4f4b-a3e7-c349acced200" + "-" + "1");
        dmaapResponse.setRpcName("restart");
        dmaapResponse.setType("response");

        LCMRequest appcRequest = new LCMRequest();
        
        appcRequest.setAction("restart");
        
        HashMap<String, String> actionIdentifiers = new HashMap<>();
        actionIdentifiers.put("vnf-id", "trial-vnf-003");
        actionIdentifiers.put("vserver-id", "08f6c1f9-99e7-49f3-a662-c62b9f200d79");
        
        appcRequest.setActionIdentifiers(actionIdentifiers);
        
        LCMCommonHeader commonHeader = new LCMCommonHeader();
        commonHeader.setRequestId(UUID.fromString("664be3d2-6c12-4f4b-a3e7-c349acced200"));
        commonHeader.setSubRequestId("1");
        commonHeader.setOriginatorId("664be3d2-6c12-4f4b-a3e7-c349acced200");
        
        appcRequest.setCommonHeader(commonHeader);
        
        appcRequest.setPayload(null);

        dmaapRequest.setBody(appcRequest);
        
        /*
         * Construct an APPCLCM Response to be Serialized
         */
        LCMResponse appcResponse = new LCMResponse(appcRequest);
        appcResponse.getStatus().setCode(400);
        appcResponse.getStatus().setMessage("Restart Successful");
        appcResponse.setPayload(null);

        dmaapResponse.setBody(appcResponse);
    }

    @Test
    public void testRequestSerialization() {
        
        /*
         * Use the gson serializer to obtain json
         */
        String jsonRequest = Serialization.gson.toJson(dmaapRequest, LCMRequestWrapper.class);
        assertNotNull(jsonRequest);
        
        /*
         * The serializer should have added an extra 
         * sub-tag called "input" that wraps the request
         */
        assertTrue(jsonRequest.contains("input"));
        
        /*
         * The common-header, request-id, and
         * sub-request-id should exist
         */
        assertTrue(jsonRequest.contains("common-header"));
        assertTrue(jsonRequest.contains("request-id"));
        assertTrue(jsonRequest.contains("sub-request-id"));
        
        /*
         * action-identifiers should exist and contain 
         * a vnf-id
         */
        assertTrue(jsonRequest.contains("action-identifiers"));
        assertTrue(jsonRequest.contains("vnf-id"));
        
        /*
         * The action sub-tag should exist
         */
        assertTrue(jsonRequest.contains("action"));
        
        logger.debug("Request as JSON: " + jsonRequest + "\n\n");
    }

    @Test
    public void testRequestDeserialization() {
        
        /*
         * Convert the LCM request object into json
         * so we have a string of json to use for testing
         */
        String jsonRequest = Serialization.gson.toJson(dmaapRequest, LCMRequestWrapper.class);
        
        /*
         * Use the serializer to convert the json string
         * into a java object
         */
        LCMRequestWrapper dmaapRequest = Serialization.gson.fromJson(jsonRequest, LCMRequestWrapper.class);
        assertNotNull(dmaapRequest);
        
        /*
         * The type of the DMAAP wrapper should be request
         */
        assertEquals(dmaapRequest.getType(), "request");
        
        /*
         * The DMAAP wrapper must have a body as that is
         * the true APPC request 
         */
        assertNotNull(dmaapRequest.getBody());
        LCMRequest appcRequest = dmaapRequest.getBody();
        assertNotNull(appcRequest);

        /*
         * The common header should not be null
         */
        assertNotNull(appcRequest.getCommonHeader());

        /*
         * The action should not be null and should be
         * set to restart
         */
        assertNotNull(appcRequest.getAction());
        assertEquals(appcRequest.getAction(), "restart");

        /*
         * The action-identifiers should not be null
         * and should contain a vnf-id
         */
        assertNotNull(appcRequest.getActionIdentifiers());
        assertNotNull(appcRequest.getActionIdentifiers().get("vnf-id"));
        
        logger.debug("Request as a Java Object: \n" + appcRequest.toString() + "\n\n");
    }

    @Test
    public void testResponseSerialization() {
        
        /*
         * Use the serializer to convert the object into json
         */
        String jsonResponse = Serialization.gson.toJson(dmaapResponse, LCMResponseWrapper.class);
        assertNotNull(jsonResponse);
        
        /*
         * The serializer should have added an extra 
         * sub-tag called "input" that wraps the request
         */
        assertTrue(jsonResponse.contains("output"));
        
        /*
         * The response should contain a common-header,
         * request-id, sub-request-id, and status
         */
        assertTrue(jsonResponse.contains("common-header"));
        assertTrue(jsonResponse.contains("request-id"));
        assertTrue(jsonResponse.contains("sub-request-id"));
        assertTrue(jsonResponse.contains("status"));
        
        logger.debug("Response as JSON: " + jsonResponse + "\n\n");
    }
    
    @Test
    public void testResponseDeserialization() {
        /*
         * Convert the LCM response object into json
         * so we have a string of json to use for testing
         */
        String jsonResponse = Serialization.gson.toJson(dmaapResponse, LCMResponseWrapper.class);
        
        /*
         * Use the serializer to convert the json string
         * into a java object
         */
        LCMResponseWrapper dmaapResponse = Serialization.gson.fromJson(jsonResponse, LCMResponseWrapper.class);
        assertNotNull(dmaapResponse);
        
        /*
         * The type of the DMAAP wrapper should be response
         */
        assertEquals(dmaapResponse.getType(), "response");
       
        /*
         * The DMAAP wrapper must have a body as that is
         * the true APPC response 
         */
        assertNotNull(dmaapResponse.getBody());
        LCMResponse appcResponse = dmaapResponse.getBody();
        assertNotNull(appcResponse);
        
        /*
         * The common header should not be null
         */
        assertNotNull(appcResponse.getCommonHeader());
        
        /*
         * The status should not be null and the 
         * status code should be 400
         */
        assertNotNull(appcResponse.getStatus());
        assertEquals(appcResponse.getStatus().getCode(), 400);

        logger.debug("Response as a Java Object: \n" + appcResponse.toString() + "\n\n");
    }
}
