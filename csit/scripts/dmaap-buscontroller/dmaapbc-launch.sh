#!/bin/bash

# script to launch DMaaP buscontroller docker container
# sets global var IP with assigned IP address

function dmaapbc_launch() {
    if [ $# == 1 ]; then
      export dmaap_prov_ip=$1
      export dmaap_mr_ip=$2
    else
      export dmaap_prov_ip=$1
      export dmaap_mr_ip=$1
    fi

    docker-compose -f ${WORKSPACE}/scripts/dmaap-buscontroller/docker-compose/docker-compose-bc.yml up -d

    sleep 10

    DMAAP_BC_IP=`get-instance-ip.sh dmaap-bc`

    # Wait for initialization
    for i in 1 2 3 4 5 6 7 8 9 10; do
        curl -sS ${DMAAP_BC_IP}:8080 && break
        echo sleep ${i}
        sleep ${i}
    done
}
