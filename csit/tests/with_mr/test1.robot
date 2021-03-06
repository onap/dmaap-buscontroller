*** Settings ***
Library           Collections
Library           OperatingSystem
Library           RequestsLibrary


*** Variables ***
${DBC_URI}      webapi
${DBC_URL}      http://${DMAAP_BC_IP}:8080/${DBC_URI}
${TOPIC_NS}     org.onap.dmaap.onapCSIT
${LOC}          csit-sanfrancisco
${PUB_CORE}     "dcaeLocationName": "${LOC}", "clientRole": "org.onap.dmaap.client.pub", "action": [ "pub", "view" ] 
${SUB_CORE}     "dcaeLocationName": "${LOC}", "clientRole": "org.onap.dmaap.client.sub", "action": [ "sub", "view" ] 
${PUB}          { ${PUB_CORE} }
${SUB}          { ${SUB_CORE} }
${TOPIC1_DATA}  { "topicName":"singleMRtopic1", "topicDescription":"generated for CSIT", "owner":"dgl"}
${TOPIC2_DATA}  { "topicName":"singleMRtopic2", "topicDescription":"generated for CSIT", "owner":"dgl", "clients": [ ${PUB}, ${SUB}] }
${TOPIC3_DATA}  { "topicName":"singleMRtopic3", "topicDescription":"generated for CSIT", "owner":"dgl"}
${PUB3_DATA}    { "fqtn": "${TOPIC_NS}.singleMRtopic3", ${PUB_CORE} }
${SUB3_DATA}    { "fqtn": "${TOPIC_NS}.singleMRtopic3", ${SUB_CORE} }


*** Test Cases ***
(DMAAP-293)
    [Documentation]        Create Topic w no clients POST ${DBC_URI}/topics endpoint
    ${resp}=         PostCall    ${DBC_URL}/topics    ${TOPIC1_DATA}
    Should Be Equal As Integers  ${resp.status_code}  201   

(DMAAP-294)
    [Documentation]        Create Topic w pub and sub clients POST ${DBC_URI}/topics endpoint
    ${resp}=         PostCall    ${DBC_URL}/topics    ${TOPIC2_DATA}
    Should Be Equal As Integers  ${resp.status_code}  201

(DMAAP-295)
    [Documentation]        Create Topic w no clients and then add a client POST ${DBC_URI}/mr_clients endpoint
    ${resp}=         PostCall    ${DBC_URL}/topics    ${TOPIC3_DATA}
    Should Be Equal As Integers  ${resp.status_code}  201   
    ${resp}=         PostCall    ${DBC_URL}/mr_clients    ${PUB3_DATA}
    Should Be Equal As Integers  ${resp.status_code}  200   
    ${resp}=         PostCall    ${DBC_URL}/mr_clients    ${SUB3_DATA}
    Should Be Equal As Integers  ${resp.status_code}  200   

(DMAAP-297)
    [Documentation]    Query for all topics and specific topic
    ${resp}=           Evaluate    requests.get('${DBC_URL}/topics', verify=False)    requests
    Should Be Equal As Integers  ${resp.status_code}  200
    ${resp}=       Evaluate    requests.get('${DBC_URL}/topics/${TOPIC_NS}.singleMRtopic3', verify=False)    requests
    Should Be Equal As Integers  ${resp.status_code}  200

(DMAAP-301)
    [Documentation]    Delete a subscriber
    ${resp}=           Evaluate    requests.get('${DBC_URL}/topics/${TOPIC_NS}.singleMRtopic3', verify=False)    requests
    Should Be Equal As Integers  ${resp.status_code}  200
    ${JSON}=           Evaluate      json.loads(r"""${resp.content}""", strict=False)
    ${clientId}=       Set Variable  ${JSON['clients'][1]['mrClientId']}
    ${resp}=           DelCall   ${DBC_URL}/mr_clients/${clientId}
    Should Be Equal As Integers  ${resp.status_code}  204

(DMAAP-302)
    [Documentation]    Delete a publisher
    ${resp}=           Evaluate    requests.get('${DBC_URL}/topics/${TOPIC_NS}.singleMRtopic3', verify=False)    requests
    Should Be Equal As Integers  ${resp.status_code}  200
    ${JSON}=           Evaluate      json.loads(r"""${resp.content}""", strict=False)
    ${clientId}=       Set Variable  ${JSON['clients'][0]['mrClientId']}
    ${resp}=           DelCall   ${DBC_URL}/mr_clients/${clientId}
    Should Be Equal As Integers  ${resp.status_code}  204


*** Keywords ***
PostCall
    [Arguments]    ${url}           ${data}
    ${headers}=    Create Dictionary    Accept=application/json    Content-Type=application/json
    ${resp}=       Evaluate    requests.post('${url}',data='${data}', headers=${headers},verify=False)    requests
    [Return]       ${resp}

DelCall
    [Arguments]    ${url}           
    ${headers}=    Create Dictionary    Accept=application/json    Content-Type=application/json
    ${resp}=       Evaluate    requests.delete('${url}', headers=${headers},verify=False)    requests
    [Return]       ${resp}
