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

public class Relationship implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -68508443869003054L;
	
	@SerializedName("related-to")
	public String relatedTo;
	@SerializedName("related-link")
	public String relatedLink; 
	
	@SerializedName("relationship-data")
	public RelationshipData relationshipData = new RelationshipData();
	
	@SerializedName("related-to-property")
	public RelatedToProperty relatedToProperty =  new RelatedToProperty();
	
	public Relationship() {
	}
}
