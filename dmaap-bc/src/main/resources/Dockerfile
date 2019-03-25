#########
#  ============LICENSE_START====================================================
#  org.onap.dmaap
#  ===========================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  Modifications Copyright (C) 2018 Nokia. All rights reserved.
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
#
FROM openjdk:8-jre-alpine

MAINTAINER DMAAP Team

COPY /opt /opt

WORKDIR /opt/app/dmaapbc

# Install AAF CA certificate
RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*
RUN mkdir -p /usr/local/share/ca-certificates && \
    mv misc/aaf-ca.crt /usr/local/share/ca-certificates/aaf-ca.crt
RUN update-ca-certificates

# Install curl
RUN apk add --no-cache curl

# Install bash
RUN apk update  && apk add bash

RUN mv etc/org.onap.dmaap-bc.jks etc/keystore && \
    chmod 600 etc/keystore && \
    chmod 600 etc/org.onap.dmaap-bc.trust.jks && \
    chmod +x bin/* && \
    mkdir logs && \
    mkdir www && \
    mkdir doc && \
    mkdir config

VOLUME /opt/app/dmaapbc/log

ENTRYPOINT ["sh", "./bin/dmaapbc", "deploy"]
