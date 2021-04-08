#########
#  ============LICENSE_START====================================================
#  org.onap.dmaap
#  ===========================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  Modifications Copyright (C) 2018 Nokia. All rights reserved.
#  Modifications copyright (C) 2021 Nordix Foundation..
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
FROM alpine:3.8

MAINTAINER DMAAP Team

COPY /opt /opt

WORKDIR /opt/app/dbc-client

# Install AAF CA certificate
RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*
RUN mkdir -p /usr/local/share/ca-certificates && \
    mv misc/aaf-ca.crt /usr/local/share/ca-certificates/aaf-ca.crt
RUN update-ca-certificates

# Install curl
RUN apk add --no-cache curl

# Install client certs for curl and openssl
RUN apk add --no-cache openssl
#RUN mkdir -p /opt/app/osaaf/local  && \
#    mv etc/org.onap.dmaap-bc.p12 /opt/app/osaaf/local && \
#    mv etc/*.pem /opt/app/osaaf/local

RUN chmod +x /opt/app/dbc-client/bin/* && \
    mkdir /opt/app/dbc-client/logs

VOLUME /opt/app/dbc-client/log

RUN addgroup -S -g 1001 onap \
    && adduser -S -u 1000 dbc -G onap \
    && chown -R dbc:onap /opt/

USER dbc

ENTRYPOINT ["sh", "./bin/dbc-client" ]
