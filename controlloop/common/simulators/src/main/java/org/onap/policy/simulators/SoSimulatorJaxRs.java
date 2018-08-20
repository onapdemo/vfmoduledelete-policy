/*-
 * ============LICENSE_START=======================================================
 * simulators
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

package org.onap.policy.simulators;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SoSimulatorJaxRs {

    @POST
    @Path("/serviceInstances/v5/{serviceInstanceId}/vnfs/{vnfInstanceId}/vfModules")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String SoPostQuery(@PathParam("serviceInstanceId") String serviceInstanceId, @PathParam("vnfInstanceId") String vnfInstanceId) {

        // the requestID contained in the SO Response is a newly generated requestID
        // with no relation to the requestID in Policy controlLoopEvent
        return "{\"requestReferences\": {\"instanceId\": \"ff305d54-75b4-ff1b-bdb2-eb6b9e5460ff\", \"requestId\": \"rq1234d1-5a33-ffdf-23ab-12abad84e331\" }}";

    }

    @DELETE
    @Path("/serviceInstances/v5/{serviceInstanceId}/vnfs/{vnfInstanceId}/vfModules/{vfmoduleInstanceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String SoDeleteQuery(@PathParam("serviceInstanceId") String serviceInstanceId, @PathParam("vnfInstanceId") String vnfInstanceId, @PathParam("vfmoduleInstanceId") String vfmoduleInstanceId) {
        return "{\"requestReferences\": {\"instanceId\": \"ff305d54-75b4-ff1b-bdb2-eb6b9e5460ff\", \"requestId\": \"rq1234d1-5a33-ffdf-23ab-12abad84e331\" }}";
    }

    @GET
    @Path("/orchestrationRequests/v5/{requestId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String SoGetQuery(@PathParam("requestId") String requestId) {
        return "{\n" +
                "            \"request\": {\n" +
                "                \"requestId\": \"fc01343f-c7f2-4f1c-ac71-074709063753\",\n" +
                "                \"startTime\": \"Wed, 27 Jun 2018 09:10:06 GMT\",\n" +
                "                \"requestScope\": \"vfModule\",\n" +
                "                \"requestType\": \"createInstance\",\n" +
                "                \"requestDetails\": {\n" +
                "                    \"modelInfo\": {\n" +
                "                        \"modelInvariantId\": \"356a1cff-71f2-4086-9980-a2927ce11c1c\",\n" +
                "                        \"modelType\": \"vfModule\",\n" +
                "                        \"modelName\": \"Vloadbalancer..dnsscaling..module-1\",\n" +
                "                        \"modelVersion\": \"1\",\n" +
                "                        \"modelVersionId\": \"6b93d804-cfc8-4be3-92cc-9336d135859a\"\n" +
                "                    },\n" +
                "                    \"requestInfo\": {\n" +
                "                        \"source\": \"POLICY\",\n" +
                "                        \"instanceName\": \"vDNS_vLB1113-1\",\n" +
                "                        \"suppressRollback\": false,\n" +
                "                        \"requestorId\": \"policy\"\n" +
                "                    },\n" +
                "                    \"relatedInstanceList\": [\n" +
                "                        {\n" +
                "                            \"relatedInstance\": {\n" +
                "                                \"instanceId\": \"3b12f31f-8f2d-4f5c-b875-61ff1194b941\",\n" +
                "                                \"modelInfo\": {\n" +
                "                                    \"modelInvariantId\": \"1321d60d-f7ff-4300-96c2-6bf0b3268b7a\",\n" +
                "                                    \"modelType\": \"service\",\n" +
                "                                    \"modelName\": \"vLoadBalancer-1106\",\n" +
                "                                    \"modelVersion\": \"1.0\",\n" +
                "                                    \"modelVersionId\": \"732d4692-4b97-46f9-a996-0b3339e88c50\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"relatedInstance\": {\n" +
                "                                \"instanceId\": \"db373a8d-f7be-4d02-8ac8-6ca4c305d144\",\n" +
                "                                \"modelInfo\": {\n" +
                "                                    \"modelCustomizationName\": \"vLoadBalancer 0\",\n" +
                "                                    \"modelInvariantId\": \"cee050ed-92a5-494f-ab04-234307a846dc\",\n" +
                "                                    \"modelType\": \"vnf\",\n" +
                "                                    \"modelName\": \"vLoadBalancer\",\n" +
                "                                    \"modelVersion\": \"1.0\",\n" +
                "                                    \"modelVersionId\": \"fd65becc-6b2c-4fe8-ace9-cc29db9a3da2\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"cloudConfiguration\": {\n" +
                "                        \"tenantId\": \"41d6d38489bd40b09ea8a6b6b852dcbd\",\n" +
                "                        \"lcpCloudRegionId\": \"RegionOne\"\n" +
                "                    },\n" +
                "                    \"requestParameters\": {\n" +
                "                        \"aLaCarte\": false,\n" +
                "                        \"autoBuildVfModules\": false,\n" +
                "                        \"cascadeDelete\": false,\n" +
                "                        \"usePreload\": true,\n" +
                "                        \"rebuildVolumeGroups\": false\n" +
                "                    }\n" +
                "                },\n" +
                "                \"instanceReferences\": {\n" +
                "                    \"serviceInstanceId\": \"3b12f31f-8f2d-4f5c-b875-61ff1194b941\",\n" +
                "                    \"vnfInstanceId\": \"db373a8d-f7be-4d02-8ac8-6ca4c305d144\",\n" +
                "                    \"vfModuleInstanceName\": \"vDNS_vLB1113-1\",\n" +
                "                    \"requestorId\": \"policy\"\n" +
                "                },\n" +
                "                \"requestStatus\": {\n" +
                "                    \"requestState\": \"Complete\",\n" +
                "                    \"statusMessage\": \"Request Complted\",\n" +
                "                    \"percentProgress\": 100,\n" +
                "                    \"finishTime\": \"Wed, 27 Jun 2018 09:10:06 GMT\"\n" +
                "                }\n" +
                "            }\n" +
                "        }";
    }

}
