#!/bin/bash
#
# ============LICENSE_START=======================================================
# org.onap.dmaap
# ================================================================================
# Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
# Modifications copyright (C) 2021 Nordix Foundation..
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
# ============LICENSE_END=========================================================

source ${WORKSPACE}/scripts/dmaap-message-router/dmaap-mr-teardown.sh
dmaap_mr_teardown
source ${WORKSPACE}/scripts/dmaap-datarouter/datarouter-teardown.sh
teardown_dmaap_dr
docker cp dmaap-bc:/opt/app/dmaapbc/logs/ONAP ${WORKSPACE}/archives/dmaap/last_run_logs/bc_logs
docker-compose -f ${WORKSPACE}/scripts/dmaap-buscontroller/docker-compose/docker-compose-bc.yml rm -sf
docker system prune -f
