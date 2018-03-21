DMaaP Bus Controller API
=======================

Data Movement as a Platform (DMaaP) Bus Controller provides an API for other ONAP infrastructure components to provision DMaaP resources.
A typical DMaaP resource is a Data Router Feed or a Message Router Topic, and their associated publishers and subscribers.
Other infrastucture resources such as DR Nodes and MR Clusters are also provisioned through this API.

### Build Instructions for a Continuous Integration environment using Jenkins

When this component is included in a Continuous Integration environment, such as structured by the Linux Foundation, the artifacts can be created and deployed via Jenkins.  The following maven targets are currently supported in the Build step:
```
clean install
```


### Build Instructions for external developers

This project is organized as a mvn project for a jar package.
After cloning from this git repo:

```
mvn clean install 
```


### Docker Packaging

We can utilize docker to build and register the buscontroller container in a local dev repository.
Note the Dockerfile follows ONAP convention of running app as root.

```

<assuming DOCKER_HOST is set appropriately for your environment>

$ mvn -P docker docker:build
```


### ONAP deployment

Details TBD.

Prior to starting container, place environment specific vars in /tmp/docker-databus-controller.conf on the Docker host,
and map that file to /opt/app/config/conf.
Run the container which will run the dmaapbc deploy command, which will update the container runtime properties appropriately, and start the Bus Controller.

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

DMAAPBC_AAF_URL=https://aafapi.${CONT_DOMAIN}:8095/proxy/

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
$ docker pull ecomp-nexus:51212/dcae_dmaapbc:1.0.0
$ docker run -d -p 18080:8080 -v /tmp/docker-databus-controller.conf:/opt/app/config/conf onap-nexus:51212/dmaap/buscontroller:1.0.0
```

