#!/bin/bash
#
# ============LICENSE_START=======================================================
# ONAP DMAAP MR 
# ================================================================================
# Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
# Modifications copyright (C) 2021 Nordix Foundation..
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END============================================
# ===================================================================
# ECOMP is a trademark and service mark of AT&T Intellectual Property.
#
# This script is a copy of plans/dmaap/mrpubsub/setup.sh, placed in the scripts
# dir, and edited to be a callable function from other plans. e.g. dmaap-buscontroller needs it.


# function to launch DMaaP MR docker containers.
# sets global var IP with assigned IP address of MR container.
# (kafka and zk containers are not called externally)

function dmaap_mr_launch() {

  COMPOSE_PREFIX=${COMPOSE_PROJECT_NAME:-dockercompose}
  export COMPOSE_PROJECT_NAME=$COMPOSE_PREFIX

  mkdir -p ${WORKSPACE}/archives/dmaap/last_run_logs

  # start DMaaP MR containers with docker compose and configuration from docker-compose.yml
  docker login -u docker -p docker nexus3.onap.org:10001
  docker-compose -f "${WORKSPACE}"/scripts/dmaap-message-router/docker-compose/docker-compose.yml up -d
  docker ps

  # Wait for initialization of Docker containers for DMaaP MR, Kafka and Zookeeper
  for i in {1..50}; do
      if [ $(docker inspect --format '{{ .State.Running }}' dmaap-mr) ] && \
        [ $(docker inspect --format '{{ .State.Running }}' zookeeper) ] && \
        [ $(docker inspect --format '{{ .State.Running }}' kafka) ]
      then
          echo "DMaaP Service Running"
          break
      else
          echo sleep $i
          sleep $i
      fi
  done

  DMAAP_MR_IP=$(get-instance-ip.sh dmaap-mr)
  IP=${DMAAP_MR_IP}
  KAFKA_IP=$(get-instance-ip.sh kafka)
  ZOOKEEPER_IP=$(get-instance-ip.sh zookeeper)

  echo DMAAP_MR_IP="${DMAAP_MR_IP}"
  echo IP="${IP}"
  echo KAFKA_IP="${KAFKA_IP}"
  echo ZOOKEEPER_IP="${ZOOKEEPER_IP}"

  # Wait for initialization of docker services
  for i in {1..50}; do
      curl -sS -m 1 "${DMAAP_MR_IP}":3904/events/TestTopic && break
      echo sleep $i
      sleep $i
  done
}

