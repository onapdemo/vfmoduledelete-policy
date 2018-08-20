This source repository contains ONAP Policy application code. To build it:
1. using Maven 3
2. git clone http://gerrit.onap.org/r/oparent and copy
oparent/settings.xml to ~/.m2
3. mvn clean install

The Demo template rule is located in template.demo sub-project. Use that project to protoype and test the .drl demo rule. When finished update the archetype-closedloop-demo-rules project with the .drl. Be sure to remove the Setup rule and comment out any simulation/test code.

The other projects are supporting code used by the template.demo project.

Enhancement (on top of ONAP Amsterdam release)
Support for the ‘VF Module Delete’ recipe
Construct and send VF module delete request to SO
Store A&AI vserver named-query response in the drools memory
After deleting the vf-module, vserver named-query response will not have the vf-module details those are required to create SO vf-module create request body. So, store the named-query response in the drools memory before deleting the vf-module.
Retrieve the named-query response from the drools memory and use it for creating the SO vf-module create request.
To address above points, need to pass the drools WorkingMemory reference to the SOActorServiceProvider.java

Impacted files :
actor.so-1.1.1.jar
	org.onap.policy.controlloop.actor.so.SOActorServiceProvider.java
eventmanager-1.1.1.jar
	org.onap.policy.controlloop.eventmanager.ControlLoopOperationManager.java
rest-1.1.1.jar
	org.onap.policy.rest.RESTManager.java
	org.onap.policy.rest.HttpDeleteWithBody.java (new class)
so-1.1.1.jar
	org.onap.policy.so.SORequestParameters.java
	org.onap.policy.so.SORequest.java
	org.onap.policy.so.SOManager.java
simulators-1.1.1.jar
	org.onap.policy.simulators.SoSimulatorJaxRs.java
