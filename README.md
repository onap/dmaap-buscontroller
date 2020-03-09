
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

### Sub-components

This project produces the following mvn modules in their respective sub-directories:
- dmaap-bc is a docker image intended to be used to instantiate the DMaaP Bus Controller container
- dbc-client is a docker image intended to serve as a temporary http client of the Bus Controller API.  It will be instantiated in a container that is a Helm hook (e.g. post-install hook) that knows how to invoke the Bus Controller API.

### Build Instructions for a Continuous Integration environment using Jenkins

When this component is included in a Continuous Integration environment, such as structured by the Linux Foundation, the artifacts can be created and deployed via Jenkins.  The following maven targets are currently supported in the Build step:
```
clean install
```


### Build Instructions for external developers

This project is organized as a mvn project for a docker image.
After cloning from this git repo:

```
mvn clean install 
```


### Docker Packaging

We can utilize docker to build and register the buscontroller container in a local dev repository.

```

<assuming DOCKER_HOST is set appropriately for your environment>

$ mvn -P docker 
```


### ONAP deployment

Two styles of deployment are supported for ONAP.
1. OOM - by the time of Release Casablanca, this is the preferred deployment method which relies on a kubernetes environment, and uses helm charts.  Buscontroller is part of a larger overall dmaap chart which also deploys Message Router and Data Router.  See https://gerrit.onap.org/r/#/admin/projects/oom
2. HEAT - an older style deployment which assumes VM running docker.

#### ONAP OOM Deployment

The default DMaaP charts for an OOM deployment should be able to be used without modification because the interdependencies between components rely on kubernetes service names.
However, overrides can be made to the buscontroller values.yaml file (found in oom/kubernetes/dmaap/charts/dmaap-bus-controller/values.yaml) as needed.

Following a convention where Release is set to the component name (--name=dmaap) and using a dev namespace:
```
<clone oom>
cd kubernetes
<edit values.yaml if necessary>
make dmaap
helm install dmaap --debug --name=dmaap --namespace=dev

```

#### ONAP Heat deployment
Prior to starting container, place environment specific vars in /tmp/docker-databus-controller.conf on the Docker host,
and map that file to /opt/app/config/conf.
Run the buscontroller image which starts execution of the dmaapbc deploy command, which will update the container runtime properties appropriately, and start the Bus Controller.

For example, in ONAP Future Lab environment, /tmp/docker-databus-controller.conf looks like:
```

# DMaaP Bus Controller OpenSource environment vars
CONT_DOMAIN=demo.dmaap.onap.org
DMAAPBC_INSTANCE_NAME=ONAPfuture

#   The https port
#   set to 0 if certificate is not ready
DMAAPBC_INT_HTTPS_PORT=0

DMAAPBC_KSTOREFILE=/opt/app/dcae-certificates
DMAAPBC_KSTOREPASS=foofoofoo
DMAAPBC_PVTKEYPASS=barbarbar

DMAAPBC_PG_ENABLED=true
DMAAPBC_PGHOST=zldciad1vipstg00.simpledemo.openecomp.org
DMAAPBC_PGCRED=test234-ftl

DMAAPBC_DRPROV_FQDN=zldciad1vidrps00.simpledemo.openecomp.org

DMAAPBC_AAF_URL=https://aafapi.${CONT_DOMAIN}:8100/proxy/

DMAAPBC_TOPICMGR_USER=m99751@dmaapBC.openecomp.org
DMAAPBC_TOPICMGR_PWD=enc:zyRL9zbI0py3rJAjMS0dFOnYfEw_mJhO
DMAAPBC_ADMIN_USER=m99501@dcae.openecomp.org
DMAAPBC_ADMIN_PWD=enc:YEaHwOJrwhDY8a6usetlhbB9mEjUq9m

DMAAPBC_PE_ENABLED=false
DMAAPBC_PE_AAF_ENV=TBD
```
Then the following steps could be used to pull and run the Bus Controller.  (onap-nexus is just an example)
```
$ 
$ docker pull nexus3.onap.org:10003/onap/dmaap/dmaap-bc:latest
$ docker run -d -p 18080:8080 -p 18443:8443 -v /tmp/docker-databus-controller.conf:/opt/app/config/conf nexus3.onap.org:10003/onap/dmaap/dmaap-bc:latest
```

### Properties

This section is intended to describe the behavior customization of Bus Controller that can be obtained via properties file used by the dbcapi library.
By default, this file is located in etc/dmaapbc.properties.
However, a java argument -DConfigFile  can be set to a different path.  (Our kubernetes deployment relies on this and points to a configmap, for example.)

Refer to dbcapi/README.md for a table describing all the properties.


### Environment Variables
The following environment variables can be set in the container environment to further effect behavior:
- DMAAPBC_WAIT_TOEXIT=Y   when set this will attempt to keep the dmaap-bc container running.  Sometimes needed to get a better look at logs when the main proces is exiting for some reason.

- CONFIGMAP_ROOT defaults to /opt/app/config/conf  but can be overwritten if needed.  This value serves as the root to where the buscontroller.env file is found.

- CONFIGMAP_PROPS defaults to /opt/app/config/conf/dmaapbc.properties but can be overwritten if needed
