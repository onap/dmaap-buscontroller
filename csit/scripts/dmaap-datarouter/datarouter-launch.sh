#!/bin/bash
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2021 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#

function dmaap_dr_launch() {
  COMPOSE_PREFIX=${COMPOSE_PROJECT_NAME:-dockercompose}
  export COMPOSE_PROJECT_NAME=$COMPOSE_PREFIX

  mkdir -p ${WORKSPACE}/archives/dmaap/last_run_logs

  # start DMaaP DR containers with docker compose and configuration from docker-compose.yml
  docker login -u docker -p docker nexus3.onap.org:10001
  docker-compose -f ${WORKSPACE}/scripts/dmaap-datarouter/docker-compose/docker-compose.yml up -d

  # Wait for initialization of Docker container for datarouter-node, datarouter-prov and mariadb
  for i in 1 2 3 4 5 6 7 8 9 10; do
      if [[ $(docker inspect --format '{{ .State.Running }}' datarouter-node) ]] && \
          [[ $(docker inspect --format '{{ .State.Running }}' datarouter-prov) ]] && \
          [[ $(docker inspect --format '{{ .State.Running }}' mariadb) ]]
      then
          echo "DR Service Running"
          break
      else
          echo sleep ${i}
          sleep ${i}
      fi
  done

  # Wait for healthy container datarouter-prov
  for i in 1 2 3 4 5 6 7 8 9 10; do
      if [[ "$(docker inspect --format '{{ .State.Health.Status }}' datarouter-prov)" = 'healthy' ]]
      then
          echo datarouter-prov.State.Health.Status is $(docker inspect --format '{{ .State.Health.Status }}' datarouter-prov)
          echo "DR Service Running, datarouter-prov container is healthy"
          break
      else
          echo datarouter-prov.State.Health.Status is $(docker inspect --format '{{ .State.Health.Status }}' datarouter-prov)
          echo sleep ${i}
          sleep ${i}
          if [[ ${i} = 10 ]]
          then
              echo datarouter-prov container is not in healthy state - the test is not made, teardown...
              docker-compose rm -sf
              exit 1
          fi
      fi
  done

  DR_PROV_IP=`get-instance-ip.sh datarouter-prov`
  DR_NODE_IP=`get-instance-ip.sh datarouter-node`
  echo DR_PROV_IP=${DR_PROV_IP}
  echo DR_NODE_IP=${DR_NODE_IP}
  #Pass any variables required by Robot test suites in ROBOT_VARIABLES
  ROBOT_VARIABLES="-v DR_PROV_IP:${DR_PROV_IP} -v DR_NODE_IP:${DR_NODE_IP} -v DR_SUB_IP:${DR_SUB_IP} -v DR_SUB2_IP:${DR_SUB2_IP}"
}