/*-
 * ============LICENSE_START=======================================================
 * aai
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

package org.onap.policy.aai;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AAINQVServer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6718526692734215643L;

	@SerializedName("vserver-id")
	public String vserverId;
	
	@SerializedName("vserver-name")
	public String vserverName;
	
	@SerializedName("vserver-name2")
	public String vserverName2;
	
	@SerializedName("prov-status")
	public String provStatus;
	
	@SerializedName("vserver-selflink")
	public String vserverSelflink;
	
	@SerializedName("in-maint")
	public Boolean inMaint;
	
	@SerializedName("is-closed-loop-disabled")
	public Boolean isClosedLoopDisabled;
	
	@SerializedName("resource-version")
	public String resourceVersion;
	
	public AAINQVServer() {
	}

}
