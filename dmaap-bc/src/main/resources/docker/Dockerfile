#########
#  ============LICENSE_START====================================================
#  org.onap.dmaap
#  ===========================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  Modifications Copyright (C) 2018 Nokia. All rights reserved.
#  Modifications Copyright (C) 2021 Nordix Foundation.
#  ===========================================================================
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ============LICENSE_END====================================================

FROM nexus3.onap.org:10001/onap/integration-java11:10.0.0

MAINTAINER DMAAP Team

WORKDIR /opt/app/dmaapbc

COPY /opt /opt
USER root
RUN apk add --no-cache curl jq net-tools procps \

&&  mkdir -p /opt/app/osaaf && \
    chmod +x bin/* && \
    mkdir logs && \
    mkdir doc && \
    mkdir config && \
    chown -R onap:onap /opt/

USER onap

ENTRYPOINT ["sh", "bin/dmaapbc", "deploy"]
