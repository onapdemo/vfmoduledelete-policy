/*-
 * ============LICENSE_START=======================================================
 * controlloop
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

package org.onap.policy.controlloop;

import java.io.Serializable;
import java.time.Instant;

public class ControlLoopOperation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8662706581293017099L;
	
	public String	actor;
	public String	operation;
	public String	target;
	public Instant	start = Instant.now();
	public Instant	end;
	public String	subRequestId;
	public String	outcome;
	public String	message;
	
	public ControlLoopOperation() {
		
	}
	
	public ControlLoopOperation(ControlLoopOperation op) {
		this.actor = op.actor;
		this.operation = op.operation;
		this.target = op.target;
		this.start = op.start;
		this.end = op.end;
		this.subRequestId = op.subRequestId;
		this.outcome = op.outcome;
		this.message = op.message;
	}

	public String	toMessage() {
		return "actor="+actor+",operation="+operation+",target="+target+",subRequestId="+subRequestId;
	}
	
	public String	toHistory() {
		return "actor="+actor+",operation="+operation+",target="+target+",start="+start+",end="+end+",subRequestId="+subRequestId+",outcome="+outcome+",message="+message;
	}
	
	@Override
	public String toString() {
		return "ControlLoopOperation [actor=" + actor + ", operation=" + operation + ", target=" + target + ", start="
				+ start + ", end=" + end + ", subRequestId=" + subRequestId + ", outcome=" + outcome + ", message="
				+ message + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actor == null) ? 0 : actor.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((outcome == null) ? 0 : outcome.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((subRequestId == null) ? 0 : subRequestId.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControlLoopOperation other = (ControlLoopOperation) obj;
		if (actor == null) {
			if (other.actor != null)
				return false;
		} else if (!actor.equals(other.actor))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (outcome == null) {
			if (other.outcome != null)
				return false;
		} else if (!outcome.equals(other.outcome))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (subRequestId == null) {
			if (other.subRequestId != null)
				return false;
		} else if (!subRequestId.equals(other.subRequestId))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}
