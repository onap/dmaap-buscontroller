#
# ============LICENSE_START==========================================
# org.onap.dmaap
# ===================================================================
# Copyright © 2018 AT&T Intellectual Property. All rights reserved.
# ===================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END============================================
# ECOMP is a trademark and service mark of AT&T Intellectual Property.
#
#
DMaaP Bus Controller API
=======================

Data Movement as a Platform (DMaaP) Bus Controller provides an API for other ONAP infrastructure components to provision DMaaP resources.
A typical DMaaP resource is a Data Router Feed or a Message Router Topic, and their associated publishers and subscribers.
Other infrastucture resources such as DR Nodes and MR Clusters are also provisioned through this API.

### Build Instructions for a Continuous Integration environment using Jenkins

When this component is included in a Continuous Integration environment, such as structured by the Linux Foundation, the artifacts can be created and deployed via Jenkins.  The following maven targets are currently supported in the Build step:
```
clean install
javadoc:javadoc
sonar:sonar
```

### Build Instructions for external developers

This project is organized as a mvn project for a jar package.
After cloning from this git repo:

```
mvn clean install javadoc:javadoc
```

A description of the API is generated, and found in targets/generated-source/swagger.json.

### Properties File

This section is intended to describe the behavior customization of Bus Controller that can be obtained via properties file used by the dbcapi library.
By default, this file is located in etc/dmaapbc.properties.
However, a java argument -DConfigFile  can be set to a different path.  (Our kubernetes deployment relies on this and points to a configmap, for example.)

The table below lists all the settings, default values (if not set), and shows any explicit setting in ONAP oom kubernetes deployment.

|-|-|-|-|
| Property | Description | Default | ONAP Kubernetes Setting | 
|-|-|-|-|
|UseAAF                | Flag for whether AAF authz API is to be used            | false                                  | false |
|-|-|-|-|
|csit                  | Flag for stubbing out many southbound calls in a CSIT environment | No                           | No |
|-|-|-|-|
|DR.provhost           | FQDN of Data Router Provisioning Server (deprecated - now set via API) | notSet                  | dcae-drps.domain.not.set |
|-|-|-|-|
|ProvisioningURI       | URI to retrieve dynamic DR configuration                | /internal/prov                         | /internal/prov |
|-|-|-|-|
|Feed.deleteHandling   | indicator for handling feed delete request              | DeleteOnDR                             | SimulateDelete |
|                      | DeleteOnDR - means use the DR API to DELETE a feed.  (default for backwards compatibility) | | |
|                      | SimulateDelete - means preserve the feed on DR (after cleaning it up), and mark as DELETED in DBCL. | | |
|-|-|-|-|
|UsePGSQL              | flag indicates whether to retain data in Postgresql     | false                                  | true |
|                      | when false, objects will be kept in memory but will be  |                                        | |
|                      | lost on restart and not shared between instances        |                                        | |
|-|-|-|-|
|DB.host               | FQDN or service name of Postresql host                  | dcae-pstg-write-ftl.domain.notset.com  | dbc-pg-primary |
|-|-|-|-|
|DB.name               | name of Postresql database                              | dmaap                                  | |
|-|-|-|-|
|DB.schema             | name of database schema                                 | public                                 | |
|-|-|-|-|
|DB.user               | username for Postgresql access                          | dmaap_admin                            | |
|-|-|-|-|
|DB.cred               | password for Postrgresql access                         | test234-ftl                            | onapdemodb |
|-|-|-|-|
|MR.multisite          | Indicates if there can be multiple sites (locations) where MR is deployed | true                 | false |
|-|-|-|-|
|MR.CentralCname       |  FQDN or service name of MR (deployed in central if multilocation is true) | MRcname.not.set     | message-router |
|-|-|-|-|
|MR ClientDeleteLevel  | MR Client Delete thoroughness                           | 0                                      |  1 |
|                      | 0 = don't delete | | |
|                      | 1 = delete from persistent store (PG) | | |
|                      | 2 = delete from persistent store (PG) and authorization store (AAF) | | |
|-|-|-|-|
|MR.TopicFactoryNS     | AAF namespace used to create perms for MR topics        | MR.topicFactoryNS.not.set              | org.onap.dmaap.mr.topicFactory |
|-|-|-|-|
|MR.TopicMgrRole       | AAF Role used by Buscontroller to create topics on MR   | MR.TopicMgrRole.not.set                | org.onap.dmaap-bc-topic-mgr.client |
|-|-|-|-|
|MR.projectID          | Value for some constructs of fully qualified topic names | 99999                                 | ONAP |
|-|-|-|-|
|MR.hostnameVerify     | Indicates if we want to relax hostname verification on SSL connection  | true                                 | false |
|-|-|-|-|
|MR.authentication     | Authentication method used when connecting to MR | none                                 | basicAuth |
|                      | none = no creds sent (default)                   |  |  |
|                      | basicAuth = formulate Basic Auth HTTP Header using name and pwd credentials |     |  |
|                      | cert = use client certificate                    |  |  |
|-|-|-|-|
|cadi.properties       | Path to CADI properties file                            | /opt/app/osaaf/local/org.onap.dmaap-bc.props | /opt/app/osaaf/lcoal/org.onap.dmaap-bc.props |
|-|-|-|-|
|aaf.URL               | URL of the AAF server                                   | https://authentication.domain.netset.com:8100/proxy/ | https://aaf-service.onap:8100/ |
|-|-|-|-|
|aaf.TopicMgrUser      | AAF Identity of Topic Mgr                               | noMechId@domain.netset.com             | dmaap-bc-topic-mgr@dmaap-bc-topic-mgr.onap.org | 
|-|-|-|-|
|aaf.TopicMgrPassword  | AAF Credential for Topic Mgr                            | notSet                                 | demo123456! |
|-|-|-|-|
|aaf.AdminUser         | AAF Identity of user with Admin role for API namespace  | noMechId@domain.netset.com             | aaf_admin@people.osaaf.org |
|-|-|-|-|
|aaf.AdminPassword     | AAF credential of AdminUser                             | notSet                                 | demo123456! |
|-|-|-|-|
|aaf.NsOwnerIdentity     | AAF Identity to be used as topic Namespace owner      | notSet                                 | aaf_admin@people.osaaf.org |
|topicNsRoot           | AAF namespace value used to create FQTN                 | org.onap.dcae.dmaap                    | org.onap.dcae.dmaap | 
|-|-|-|-|
|CredentialCodeKeyfile | location of the codec keyfile used to decrypt passwords | LocalKey                               | etc/LocalKey |
|                      | in this properties file before they are passed to AAF   | LocalKey                               | etc/LocalKey |
|-|-|-|-|
|AafDecryption.Class   | Specifies the Class to be used for decryption           | org.onap.dmaap.dbcapi.aaf.ClearDecrypt | |
|-|-|-|-|
|ApiNamespace          | Root namespace for AAF perms related to dbcapi access   | apiNamespace.not.set                   | org.onap.dmaap-bc.api |
|-|-|-|-|
|ApiPermission.Class   | the Class that determines if a call to API is authorized| allow                                  | | 
|-|-|-|-|
|MM.ProvRole           | AAF Role of client publishing MM prov cmds              | notSet                                 | org.onap.dmaap-bc-mm-prov.prov |
|-|-|-|-|
|MM.ProvUserMechId     | AAF Identity when publishing to MM command topic        | notSet                                 | dmaap-bc-mm-prov@dmaap-bc-mm-prov.onap.org|
|-|-|-|-|
|MM.ProvUserPwd        | AAF credenital for ProvUserMechId                       | notSet                                 | demo123456! | 
|-|-|-|-|
|MM.AgentRole          | AAF Role of client susbcribing to MM command topic      | notSet                                 | org.onal.dmaap-bc-mm-prov.agent |
|-|-|-|-|
|DR.provApi            | Version name of DR API (ONAP or AT&T)                   | ONAP                                   | ONAP |
|-|-|-|-|
|DR.onBehalfHeader     | String for "On Behalf Of" HTTP Header in DR API         | X-DR-ON-BEHALF-OF                      | X-DR-ON-BEHALF-OF |
|-|-|-|-|
|DR.feedContentType    | Value for Content-Type Header in DR Feed API            | application/vnd.dr.feed                | application/vnd.dr.feed |
|-|-|-|-|
|DR subContentType     | Value for Content-Type Header in DR Subscription API    | application/vnd.dr.subscription        | application/vnd.dr.subscription |
|-|-|-|-|
|HttpAllowed           | flag indicating whether http is supported               | false                                  | true |
|-|-|-|-|
|IntHttpPort           | Internal port for http service                          | 80                                     | 8080 |
|-|-|-|-|
|IntHttpsPort          | Internal port for https service (0 if no cert is avail) | 443                                    | 8443 |
|-|-|-|-|
|ExtHttpsPort          | Externally advertised port for https service (deprecated)| 443                                   | 443 |
|-|-|-|-|
|KeyStoreType          | Format of Java keystore                                 | jks                                    | jks |
|-|-|-|-|
|KeyStoreFile          | Path to java keystore                                   | etc/keystore                           | etc/keystore |
|-|-|-|-|
|KeyStorePassword      | Password for keystore                                   | changeit                               | <provided by Certificate Authority> |
|-|-|-|-|
|KeyPassword           | Password for private key in the https keystore          | changeit                               | <provided by Certificate Authority> |
|-|-|-|-|
|TrustStoreType        | Format of Trust Store file                              | jks                                    | jks |
|-|-|-|-|
|TrustStoreFile        | Path to Trust Store file                                |                                        | etc/org.onap.dmaap-bc.trust.jks |
|-|-|-|-|
|TrustStorePassword    | Password for Trust Store                                |                                        | <provided by Certificate Authority> |
|-|-|-|-|
|QuiesceFile           | Path to file which signals needs to queiesce            |                                        | etc/SHUTDOWN |
|-|-|-|-|

