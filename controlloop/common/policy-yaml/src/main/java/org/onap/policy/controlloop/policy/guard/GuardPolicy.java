/*-
 * ============LICENSE_START=======================================================
 * policy-yaml
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

package org.onap.policy.controlloop.policy.guard;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GuardPolicy {

    private String id = UUID.randomUUID().toString();
    private String name;
    private String description;
    private MatchParameters match_parameters;
    private LinkedList<Constraint> limit_constraints;
    
    public GuardPolicy() {
        //Do Nothing Empty Constructor. 
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MatchParameters getMatch_parameters() {
        return match_parameters;
    }

    public void setMatch_parameters(MatchParameters match_parameters) {
        this.match_parameters = match_parameters;
    }

    public LinkedList<Constraint> getLimit_constraints() {
        return  limit_constraints;
    }

    public void setLimit_constraints(LinkedList<Constraint> limit_constraints) {
        this.limit_constraints = limit_constraints;
    }

    public GuardPolicy(String id) {
        this.id = id;
    }
    
    public GuardPolicy(String name, MatchParameters matchParameters) {
        this.name = name;
        this.match_parameters = matchParameters;
    }
    
    public GuardPolicy(String id, String name, String description, MatchParameters matchParameters) {
        this(name, matchParameters);
        this.id = id;
        this.description = description;
    }
    
    public GuardPolicy(String name, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, matchParameters);
        if (limit_constraints != null) {
            this.limit_constraints = (LinkedList<Constraint>) Collections.unmodifiableList(limitConstraints);
        }
    }
    
    public GuardPolicy(String name, String description, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, matchParameters, limitConstraints);
        this.description = description;
    }
    
    public GuardPolicy(String id, String name, String description, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, description, matchParameters, limitConstraints);
        this.id = id;
    }
    
    public GuardPolicy(GuardPolicy policy) {
        this.id = policy.id;
        this.name = policy.name;
        this.description = policy.description;
        this.match_parameters = new MatchParameters(policy.match_parameters);
        if (policy.limit_constraints != null) {
            this.limit_constraints = (LinkedList<Constraint>) Collections.unmodifiableList(policy.limit_constraints);
        }
    }
    
    public boolean isValid() {
        return (id==null || name ==null)? false : true;
    }
    
    @Override
    public String toString() {
        return "Policy [id=" + id + ", name=" + name + ", description=" + description + ", match_parameters=" 
                +match_parameters + ", limitConstraints=" + limit_constraints + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((limit_constraints == null) ? 0 : limit_constraints.hashCode());
        result = prime * result + ((match_parameters == null) ? 0 : match_parameters.hashCode());
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
        GuardPolicy other = (GuardPolicy) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (limit_constraints == null) {
            if (other.limit_constraints != null)
                return false;
        } else if (!limit_constraints.equals(other.limit_constraints))
            return false;
        if (match_parameters==null){
            if(other.match_parameters !=null)
                return false;
        } else if(!match_parameters.equals(other.match_parameters))
            return false;
        return true;
    }
    
    
}
