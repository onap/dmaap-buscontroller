#!/bin/bash -x
#
# Copyright 2019 © Samsung Electronics Co., Ltd.
# Modifications copyright (C) 2021 Nordix Foundation..
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# This script installs common libraries required by CSIT tests
#

if [ -z "$WORKSPACE" ]; then
    export WORKSPACE=`git rev-parse --show-toplevel`
fi

TESTPLANDIR=${WORKSPACE}/${TESTPLAN}

if [ -f ${WORKSPACE}/env.properties ]; then
    source ${WORKSPACE}/env.properties
fi
if [ -f ${ROBOT_VENV}/bin/activate ]; then
    source ${ROBOT_VENV}/bin/activate
else
    rm -f ${WORKSPACE}/env.properties
    ROBOT_VENV=$(mktemp -d --suffix=robot_venv)
    echo "ROBOT_VENV=${ROBOT_VENV}" >> "${WORKSPACE}/env.properties"

    # The --system-site-packages parameter allows us to pick up system level
    # installed packages. This allows us to bake matplotlib which takes very long
    # to install into the image.
    virtualenv --system-site-packages "${ROBOT_VENV}"
    source "${ROBOT_VENV}/bin/activate"

    set -exu

    # Make sure pip itself us up-to-date.
    pip install --upgrade pip
    # To avoid a json issue related to specific versions related to https://gerrit.onap.org/r/c/ci-management/+/120747 
    # in the ci-management repo, we are reverting to the orig versions.
    pip install --upgrade --no-binary pycparser pycparser
    pip install --upgrade pyOpenSSL==16.2.0 docker-py importlib requests scapy netifaces netaddr ipaddr simplejson demjson
    pip install --upgrade robotframework{,-{httplibrary,requests,sshlibrary,selenium2library,xvfb}}

    pip install xvfbwrapper
    pip install PyVirtualDisplay
fi

# Print installed versions.
pip freeze

