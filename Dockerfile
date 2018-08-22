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
FROM java:openjdk-8-jre
MAINTAINER Dominic Lunanuova
ENV insdir  /opt/app/dmaapbc
RUN \
    mkdir -p ${insdir}/lib \
    && mkdir -p ${insdir}/etc \
    && mkdir -p ${insdir}/logs \
    && mkdir -p ${insdir}/www && mkdir -p ${insdir}/www/doc \
	&& mkdir -p ${insdir}/config \
	&& mkdir -p ${insdir}/misc \
	&& mkdir -p ${insdir}/bin
WORKDIR ${insdir}
USER root
COPY target/buscontroller.jar ${insdir}/lib/
COPY misc/LocalKey ${insdir}/etc/
COPY misc/logback.xml ${insdir}/etc/
COPY misc/dbc-api.jks ${insdir}/etc/keystore
RUN chmod 600 ${insdir}/etc/keystore
COPY misc/org.onap.dmaap-bc.trust.jks ${insdir}/etc
RUN chmod 600 ${insdir}/etc/org.onap.dmaap-bc.trust.jks
COPY ./version.properties ${insdir}/etc
COPY misc/opensource.env ${insdir}/misc/
COPY misc/*.tmpl ${insdir}/misc/
COPY misc/cert-client-init.sh ${insdir}/misc/
RUN chmod +x ${insdir}/misc/cert-client-init.sh
COPY misc/dmaapbc ${insdir}/bin/
RUN chmod +x ${insdir}/bin/*
COPY misc/doaction ${insdir}/bin/

VOLUME ${insdir}/log
CMD ["./bin/dmaapbc", "deploy" ]
