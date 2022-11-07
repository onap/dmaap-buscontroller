.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Installation
============

Bus Controller is developed using Postgresql.  An embedded Jetty server is used to create the REST service.
The service is packaged as a Docker container image.
Helm charts for Bus Controller are part of the overall dmaap chart set.

Steps for OOM Deployment
------------------------

1) Clone the oom repo
2) cd oom/kubernetes
3) (optionally, customize the buscontroller configuration - see below)
4) make dmaap
5) helm install dmaap --name=dmaap --namespace=mr


Customizing the Bus Controller configuration
--------------------------------------------

The Bus Controller is highly configurable, but by default has settings that should work for a standard ONAP oom deployment.
However, if some customization is desired, there are places to change behavior:

1) The --namespace argument of the helm install step is also refernced to compose the topic namespace used.  i.e. the value is appended to org.onap.dmaap.   Since Message Router uses org.onap.dmaap.mr by default, we also use --namespace=mr.  But this can be changed to a value that matches a different deployment of MR.
2) oom/kubernetes/dmaap/charts/dmaap-bus-controller/values.yaml  contains the set of tags used within the charts.  These can be modified if necessary.
3) oom/kubernetes/dmaap/charts/dmaap-bus-controller/resources/config/buscontroller.env contains some environment settings for the container.  These can be modified.  For example, to indicate that AAF integration should be enabled, set USE_AAF=true in this file.
4) oom/kubernetes/dmaap/charts/dmaap-bus-controller/resources/config/dmaapbc.properties  contains many properties which can be modified.  For example, if a differerent Postgresql instance needed to be used, the value could be specified here.


Steps for local development and test
------------------------------------
On Intel dev machine, in terminal (> indicates prompt) :
1) Build buscontroller images

    > git clone https://gerrit.onap.org/r/dmaap/buscontroller
        - anonymous http, can't push changes

    > cd buscontroller

    > mvn clean install -P docker

        - builds dmaap-bc and dbc-client images

2) Run tests

    > cd dmaap-bc/src/main/resources/

    > cp docker-databus-controller.conf /var/tmp/

        - set docker preferences/file sharing to access /var/tmp

    - edit docker-compose.yml

        - remove "nexus3.onap.org:10001/" from dmaap-bc:image: and dbc-client:image: to use local images

    > docker-compose up -d
    - create sample.txt file (as above)(content of file not important)

    > curl http://localhost:30241/webapi/bridge

On Arm:
1) Build buscontroller images

    > git clone https://gerrit.onap.org/r/dmaap/buscontroller
        - anonymous http, can't push changes

    > cd buscontroller

    > mvn clean install -P docker  -Ddocker.pull.registry=docker.io
        - ensure we pull Arm version of base image

2) Run tests

    > cd dmaap-bc/src/main/resources/

    > cp docker-databus-controller.conf /var/tmp/
        - set docker preferences/file sharing to access /var/tmp

    - edit docker-compose.yml
        - remove "nexus3.onap.org:10001/" from dmaap-bc:image: and dbc-client:image: to
            use local images
        - replace 'crunchydata/crunchy-postgres:centos7-10.4-2.0.0' with
            multi-platform 'postgres:9.6-alpine' normative image

    > docker-compose up -d
    - create sample.txt file (as above)(content of file not important)

    > curl http://localhost:30241/webapi/bridge

