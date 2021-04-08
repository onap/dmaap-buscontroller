/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.dbcapi.service;

import org.onap.dmaap.dbcapi.aaf.AafService.ServiceType;
import org.onap.dmaap.dbcapi.aaf.AafServiceFactory;
import org.onap.dmaap.dbcapi.client.MrProvConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MR_ClientService extends BaseLoggingClass {

    private static final String MR_CLIENT_ID = "mrClientId";
    private int deleteLevel;
    private Map<String, MR_Client> mr_clients = DatabaseClass.getMr_clients();
    private Map<String, MR_Cluster> clusters = DatabaseClass.getMr_clusters();
    private Map<String, DcaeLocation> locations = DatabaseClass.getDcaeLocations();
    private DmaapService dmaap = new DmaapService();
    private AafPermissionService aafPermissionService =
            new AafPermissionService(new AafServiceFactory().initAafService(ServiceType.AAF_TopicMgr), dmaap);
    private String centralCname;

    public MR_ClientService() {
        DmaapConfig p = (DmaapConfig) DmaapConfig.getConfig();

        centralCname = p.getProperty("MR.CentralCname", "MRcname.not.set");
        deleteLevel = Integer.valueOf(p.getProperty("MR.ClientDeleteLevel", "0"));
    }

    public List<MR_Client> getAllMr_Clients() {
        return new ArrayList<>(mr_clients.values());
    }

    List<MR_Client> getAllMrClients(String fqtn) {
        ArrayList<MR_Client> results = new ArrayList<>();
        for (Map.Entry<String, MR_Client> entry : mr_clients.entrySet()) {
            MR_Client client = entry.getValue();
            if (fqtn.equals(client.getFqtn())) {
                results.add(client);
            }
        }
        return results;
    }

    List<MR_Client> getClientsByLocation(String location) {
        List<MR_Client> results = new ArrayList<>();
        for (Map.Entry<String, MR_Client> entry : mr_clients.entrySet()) {
            MR_Client client = entry.getValue();
            if (location.equals(client.getDcaeLocationName())) {
                results.add(client);
            }
        }
        return results;
    }

    public MR_Client getMr_Client(String key, ApiError apiError) {
        MR_Client c = mr_clients.get(key);
        if (c == null) {
            apiError.setCode(Status.NOT_FOUND.getStatusCode());
            apiError.setFields(MR_CLIENT_ID);
            apiError.setMessage(MR_CLIENT_ID + " " + key + " not found");
        } else {
            apiError.setCode(200);
        }
        return c;
    }

    public MR_Client addMr_Client(MR_Client client, Topic topic, ApiError err) {
        if (client.getDcaeLocationName().isEmpty()) {
            logger.info("Client  dcaeLocation that doesn't exist or not specified");
            return null;
        }
        // original style: clients specified Role.  This has precedence for backwards
        //                 compatibility.
        // ONAP style: clients specify Identity to be assigned to generated Role
        String role = client.getClientRole();
        if (role != null) {
            updateApiError(err, aafPermissionService.grantClientRolePerms(client));
        } else if (client.hasClientIdentity()) {
            if (client.isSubscriber()) {
                role = topic.getSubscriberRole();
                updateApiError(err, aafPermissionService.assignClientToRole(client, role));
            }
            if (client.isPublisher()) {
                role = topic.getPublisherRole();
                updateApiError(err, aafPermissionService.assignClientToRole(client, role));
            }
        }
        if (!client.isStatusValid()) {
            return null;
        }
        String centralFqdn = null;
        DcaeLocation candidate = locations.get(client.getDcaeLocationName());

        MR_Cluster cluster = clusters.get(client.getDcaeLocationName());
        if (cluster != null && candidate != null) {
            if (candidate.isCentral() && !topic.getReplicationCase().involvesFQDN()) {
                centralFqdn = centralCname;
            }
            client.setTopicURL(cluster.genTopicURL(centralFqdn, client.getFqtn()));
            if (centralFqdn == null) {
                client.setStatus(addTopicToCluster(cluster, topic, err));
                if (!err.is2xx() && err.getCode() != 409) {
                    topic.setFqtn(err.getMessage());
                    return null;
                }

            } else {
                MR_ClusterService clusters = new MR_ClusterService();
                //  MM should only exist for edge-to-central
                //  we use a cname for the central target (default resiliency with no replicationGroup set)
                // but still need to provision topics on all central MRs
                for (MR_Cluster central : clusters.getCentralClusters()) {
                    client.setStatus(addTopicToCluster(central, topic, err));
                    if (!err.is2xx() && err.getCode() != 409) {
                        topic.setFqtn(err.getMessage());
                        return null;
                    }
                }
            }

        } else {
            logger.warn("Client references a dcaeLocation that doesn't exist:" + client.getDcaeLocationName());
            client.setStatus(DmaapObject_Status.STAGED);
        }

        mr_clients.put(client.getMrClientId(), client);

        err.setCode(200);

        return client;
    }

    private DmaapObject_Status addTopicToCluster(MR_Cluster cluster, Topic topic, ApiError err) {

        MrProvConnection prov = new MrProvConnection();
        logger.info("POST topic " + topic.getFqtn() + " to cluster " + cluster.getFqdn() + " in loc " + cluster.getDcaeLocationName());
        if (prov.makeTopicConnection(cluster)) {
            prov.doPostTopic(topic, err);
            logger.info("response code: " + err.getCode());
            if (err.is2xx() || err.getCode() == 409) {
                return DmaapObject_Status.VALID;
            }
        }
        return DmaapObject_Status.INVALID;
    }

    public MR_Client updateMr_Client(MR_Client client, ApiError apiError) {
        MR_Client c = mr_clients.get(client.getMrClientId());
        if (c == null) {
            apiError.setCode(Status.NOT_FOUND.getStatusCode());
            apiError.setFields(MR_CLIENT_ID);
            apiError.setMessage("mrClientId " + client.getMrClientId() + " not found");
        } else {
            apiError.setCode(200);
        }
        mr_clients.put(client.getMrClientId(), client);
        return client;
    }

    public void removeMr_Client(String key, boolean updateTopicView, ApiError apiError) {
        MR_Client client = mr_clients.get(key);
        if (client == null) {
            apiError.setCode(Status.NOT_FOUND.getStatusCode());
            apiError.setFields(MR_CLIENT_ID);
            apiError.setMessage("mrClientId " + key + " not found");
            return;
        } else {
            apiError.setCode(200);
        }

        if (updateTopicView) {

            TopicService topics = new TopicService();

            Topic t = topics.getTopic(client.getFqtn(), apiError);
            if (t != null) {
                List<MR_Client> tc = t.getClients();
                for (MR_Client c : tc) {
                    if (c.getMrClientId().equals(client.getMrClientId())) {
                        tc.remove(c);
                        break;
                    }
                }
                t.setClients(tc);
                topics.updateTopic(t, apiError);
            }

        }

        // remove from DB
        if (deleteLevel >= 1) {
            mr_clients.remove(key);
        }
    }

    private void updateApiError(ApiError err, ApiError permissionServiceError) {
        err.setCode(permissionServiceError.getCode());
        err.setMessage(permissionServiceError.getMessage());
        err.setFields(permissionServiceError.getFields());
    }
}
