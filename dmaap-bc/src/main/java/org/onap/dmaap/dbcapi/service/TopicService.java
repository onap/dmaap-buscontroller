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
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.model.ReplicationType;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.Fqdn;
import org.onap.dmaap.dbcapi.util.Graph;

import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TopicService extends BaseLoggingClass {


    // REF: https://wiki.web.att.com/pages/viewpage.action?pageId=519703122
    private static String defaultGlobalMrHost;

    private Map<String, Topic> mr_topics;

    private static DmaapService dmaapSvc = new DmaapService();
    private MR_ClientService clientService;
    private MR_ClusterService clusters;
    private DcaeLocationService locations;
    private MirrorMakerService bridge;
    private AafTopicSetupService aafTopicSetupService;

    private static String centralCname;
    private boolean strictGraph = true;
    private boolean mmPerMR;


    public TopicService() {
        this(DatabaseClass.getTopics(), new MR_ClientService(), (DmaapConfig) DmaapConfig.getConfig(),
                new MR_ClusterService(), new DcaeLocationService(), new MirrorMakerService(),
                new AafTopicSetupService(
                        new AafServiceFactory().initAafService(ServiceType.AAF_TopicMgr),
                        dmaapSvc, (DmaapConfig) DmaapConfig.getConfig()));
    }

    TopicService(Map<String, Topic> mr_topics, MR_ClientService clientService, DmaapConfig p,
                 MR_ClusterService clusters, DcaeLocationService locations, MirrorMakerService bridge, AafTopicSetupService aafTopicSetupService) {
        this.mr_topics = mr_topics;
        this.clientService = clientService;
        defaultGlobalMrHost = p.getProperty("MR.globalHost", "global.host.not.set");
        centralCname = p.getProperty("MR.CentralCname");
        String unit_test = p.getProperty("UnitTest", "No");
        if ("Yes".equals(unit_test)) {
            strictGraph = false;
        }
        mmPerMR = "true".equalsIgnoreCase(p.getProperty("MirrorMakerPerMR", "true"));
        logger.info("TopicService properties: CentralCname=" + centralCname +
                "   defaultGlobarlMrHost=" + defaultGlobalMrHost +
                " mmPerMR=" + mmPerMR);
        this.clusters = clusters;
        this.locations = locations;
        this.bridge = bridge;
        this.aafTopicSetupService = aafTopicSetupService;
    }

    public Map<String, Topic> getTopics() {
        return mr_topics;
    }

    public List<Topic> getAllTopics() {
        return getAllTopics(true);
    }

    public List<Topic> getAllTopicsWithoutClients() {
        return getAllTopics(false);
    }

    private List<Topic> getAllTopics(Boolean withClients) {
        ArrayList<Topic> topics = new ArrayList<>(mr_topics.values());
        if (withClients) {
            for (Topic topic : topics) {
                topic.setClients(clientService.getAllMrClients(topic.getFqtn()));
            }
        }
        return topics;
    }

    public Topic getTopic(String key, ApiError apiError) {
        logger.info("getTopic: key=" + key);
        Topic t = mr_topics.get(key);
        if (t == null) {
            apiError.setCode(Status.NOT_FOUND.getStatusCode());
            apiError.setFields("fqtn");
            apiError.setMessage("topic with fqtn " + key + " not found");
            return null;
        }
        t.setClients(clientService.getAllMrClients(key));
        apiError.setCode(Status.OK.getStatusCode());
        return t;
    }

    public Topic addTopic(Topic topic, ApiError err, Boolean useExisting) {
        logger.info("Entry: addTopic");
        logger.info("Topic name=" + topic.getTopicName() + " fqtnStyle=" + topic.getFqtnStyle());
        String nFqtn = topic.genFqtn();
        logger.info("FQTN=" + nFqtn);
        Topic pTopic = getTopic(nFqtn, err);
        if (pTopic != null) {
            String t = "topic already exists: " + nFqtn;
            logger.info(t);
            if (useExisting) {
                err.setCode(Status.OK.getStatusCode());
                return pTopic;
            }
            err.setMessage(t);
            err.setFields("fqtn");
            err.setCode(Status.CONFLICT.getStatusCode());
            return null;
        }
        err.reset();  // err filled with NOT_FOUND is expected case, but don't want to litter...

        topic.setFqtn(nFqtn);

        ApiError topicSetupError = aafTopicSetupService.aafTopicSetup(topic);
        updateApiError(err, topicSetupError);
        if (err.getCode() >= 400) {
            return null;
        }

        if (topic.getReplicationCase().involvesGlobal()) {
            if (topic.getGlobalMrURL() == null) {
                topic.setGlobalMrURL(defaultGlobalMrHost);
            }
            if (!Fqdn.isValid(topic.getGlobalMrURL())) {
                logger.error("GlobalMR FQDN not valid: " + topic.getGlobalMrURL());
                topic.setStatus(DmaapObject_Status.INVALID);
                err.setCode(500);
                err.setMessage("Value is not a valid FQDN:" + topic.getGlobalMrURL());
                err.setFields("globalMrURL");

                return null;
            }
        }


        if (topic.getNumClients() > 0) {
            ArrayList<MR_Client> clients = new ArrayList<MR_Client>(topic.getClients());


            ArrayList<MR_Client> clients2 = new ArrayList<MR_Client>();
            for (Iterator<MR_Client> it = clients.iterator(); it.hasNext(); ) {
                MR_Client c = it.next();

                logger.info("c fqtn=" + c.getFqtn() + " ID=" + c.getMrClientId() + " url=" + c.getTopicURL());
                MR_Client nc = new MR_Client(c.getDcaeLocationName(), topic.getFqtn(), c.getClientRole(), c.getAction());
                nc.setFqtn(topic.getFqtn());
                nc.setClientIdentity(c.getClientIdentity());
                logger.info("nc fqtn=" + nc.getFqtn() + " ID=" + nc.getMrClientId() + " url=" + nc.getTopicURL());
                clients2.add(clientService.addMr_Client(nc, topic, err));
                if (!err.is2xx()) {
                    return null;
                }
            }

            topic.setClients(clients2);
        }

        Topic ntopic = checkForBridge(topic, err);
        if (ntopic == null) {
            topic.setStatus(DmaapObject_Status.INVALID);
            if (!err.is2xx()) {
                return null;
            }
        }


        mr_topics.put(nFqtn, ntopic);

        err.setCode(Status.OK.getStatusCode());
        return ntopic;
    }


    public Topic updateTopic(Topic topic, ApiError err) {
        logger.info("updateTopic: entry");
        logger.info("updateTopic: topic=" + topic);
        logger.info("updateTopic: fqtn=" + topic.getFqtn());
        if (topic.getFqtn().isEmpty()) {
            return null;
        }
        logger.info("updateTopic: call checkForBridge");
        Topic ntopic = checkForBridge(topic, err);
        if (ntopic == null) {
            topic.setStatus(DmaapObject_Status.INVALID);
            if (!err.is2xx()) {
                return null;
            }
        }
        if (ntopic != null) {
            logger.info("updateTopic: call put");
            mr_topics.put(ntopic.getFqtn(), ntopic);
        }
        err.setCode(Status.OK.getStatusCode());
        return ntopic;
    }

    public Topic removeTopic(String pubId, ApiError apiError) {
        Topic topic = mr_topics.get(pubId);
        if (topic == null) {
            apiError.setCode(Status.NOT_FOUND.getStatusCode());
            apiError.setMessage("Topic " + pubId + " does not exist");
            apiError.setFields("fqtn");
            return null;
        }

        ApiError topicSetupError = aafTopicSetupService.aafTopicCleanup(topic);
        updateApiError(apiError, topicSetupError);
        if (apiError.getCode() >= 400) {
            return null;
        }

        ArrayList<MR_Client> clients = new ArrayList<MR_Client>(clientService.getAllMrClients(pubId));
        for (Iterator<MR_Client> it = clients.iterator(); it.hasNext(); ) {
            MR_Client c = it.next();
            clientService.removeMr_Client(c.getMrClientId(), false, apiError);
            if (!apiError.is2xx()) {
                return null;
            }
        }
        apiError.setCode(Status.OK.getStatusCode());
        return mr_topics.remove(pubId);
    }

    public static ApiError setBridgeClientPerms(MR_Cluster node) {
        DmaapConfig p = (DmaapConfig) DmaapConfig.getConfig();
        String mmProvRole = p.getProperty("MM.ProvRole");
        String mmAgentRole = p.getProperty("MM.AgentRole");
        String[] Roles = {mmProvRole, mmAgentRole};
        String[] actions = {"view", "pub", "sub"};
        Topic bridgeAdminTopic = new Topic().init();
        bridgeAdminTopic.setTopicName(dmaapSvc.getBridgeAdminFqtn());
        bridgeAdminTopic.setTopicDescription("RESERVED topic for MirroMaker Provisioning");
        bridgeAdminTopic.setOwner("DBC");

        ArrayList<MR_Client> clients = new ArrayList<MR_Client>();
        for (String role : Roles) {
            MR_Client client = new MR_Client();
            client.setAction(actions);
            client.setClientRole(role);
            client.setDcaeLocationName(node.getDcaeLocationName());
            clients.add(client);
        }
        bridgeAdminTopic.setClients(clients);

        TopicService ts = new TopicService();
        ApiError err = new ApiError();
        ts.addTopic(bridgeAdminTopic, err, true);

        if (err.is2xx() || err.getCode() == 409) {
            err.setCode(200);
            return err;
        }

        errorLogger.error(DmaapbcLogMessageEnum.TOPIC_CREATE_ERROR, bridgeAdminTopic.getFqtn(), Integer.toString(err.getCode()), err.getFields(), err.getMessage());
        return err;
    }


    public Topic checkForBridge(Topic topic, ApiError err) {
        logger.info("checkForBridge: entry");
        logger.info("fqtn=" + topic.getFqtn() + " replicatonType=" + topic.getReplicationCase());
        if (topic.getReplicationCase() == ReplicationType.REPLICATION_NONE) {
            topic.setStatus(DmaapObject_Status.VALID);
            return topic;
        }

        boolean anythingWrong = false;

        Set<String> groups = clusters.getGroups();
        for (String g : groups) {
            logger.info("buildBridge for " + topic.getFqtn() + " on group" + g);
            anythingWrong |= buildBridge(topic, err, g);
        }
        if (anythingWrong) {
            topic.setStatus(DmaapObject_Status.INVALID);
            if (!err.is2xx()) {
                return null;
            }
        } else {
            topic.setStatus(DmaapObject_Status.VALID);
        }
        return topic;
    }

    private boolean buildBridge(Topic topic, ApiError err, String group) {
        logger.info("buildBridge: entry");
        boolean anythingWrong = false;
        Graph graph;
        logger.info("buildBridge: strictGraph=" + strictGraph);
        if (group == null || group.isEmpty()) {
            graph = new Graph(topic.getClients(), strictGraph);
        } else {
            graph = new Graph(topic.getClients(), strictGraph, group);
        }
        logger.info("buildBridge: graph=" + graph);
        MR_Cluster groupCentralCluster = null;


        if (graph.isEmpty()) {
            logger.info("buildBridge: graph is empty.  return false");
            return false;
        } else if (group == null && topic.getReplicationCase().involvesFQDN()) {
            logger.info("buildBridge: group is null and replicationCaseInvolvesFQDN. return false");
            return false;
        } else if (!graph.hasCentral()) {
            logger.warn("Topic " + topic.getFqtn() + " wants to be " + topic.getReplicationCase() + " but has no central clients");
            return true;
        } else {
            groupCentralCluster = clusters.getMr_ClusterByLoc(graph.getCentralLoc());
        }
        Collection<String> clientLocations = graph.getKeys();
        for (String loc : clientLocations) {
            logger.info("loc=" + loc);
            DcaeLocation location = locations.getDcaeLocation(loc);
            MR_Cluster cluster = clusters.getMr_ClusterByLoc(loc);
            logger.info("cluster=" + cluster + " at " + cluster.getDcaeLocationName());
            logger.info("location.isCentral()=" + location.isCentral() + " getCentralLoc()=" + graph.getCentralLoc());


            String source = null;
            String target = null;

            /*
             * Provision Edge to Central bridges...
             */
            if (!location.isCentral() && !graph.getCentralLoc().equals(cluster.getDcaeLocationName())) {
                switch (topic.getReplicationCase()) {
                    case REPLICATION_EDGE_TO_CENTRAL:
                    case REPLICATION_EDGE_TO_CENTRAL_TO_GLOBAL:  // NOTE: this is for E2C portion only
                        source = cluster.getFqdn();
                        target = (mmPerMR) ? groupCentralCluster.getFqdn() : centralCname;
                        logger.info("REPLICATION_EDGE_TO_CENTRAL: source=" + source + " target=" + target);
                        break;
                    case REPLICATION_CENTRAL_TO_EDGE:
                    case REPLICATION_GLOBAL_TO_CENTRAL_TO_EDGE:  // NOTE: this is for C2E portion only
                        source = (mmPerMR) ? groupCentralCluster.getFqdn() : centralCname;
                        target = cluster.getFqdn();
                        break;
                    case REPLICATION_CENTRAL_TO_GLOBAL:
                    case REPLICATION_GLOBAL_TO_CENTRAL:
                    case REPLICATION_FQDN_TO_GLOBAL:
                    case REPLICATION_GLOBAL_TO_FQDN:
                        break;

                    case REPLICATION_EDGE_TO_FQDN:
                    case REPLICATION_EDGE_TO_FQDN_TO_GLOBAL:  // NOTE: this is for E2C portion only
                        source = cluster.getFqdn();
                        target = groupCentralCluster.getFqdn();
                        break;
                    case REPLICATION_FQDN_TO_EDGE:
                    case REPLICATION_GLOBAL_TO_FQDN_TO_EDGE:  // NOTE: this is for F2E portion only
                        source = groupCentralCluster.getFqdn();
                        target = cluster.getFqdn();
                        break;

                    default:
                        logger.error("Unexpected value for ReplicationType (" + topic.getReplicationCase() + ") for topic " + topic.getFqtn());
                        anythingWrong = true;
                        err.setCode(400);
                        err.setFields("topic=" + topic.genFqtn() + " replicationCase="
                                + topic.getReplicationCase());
                        err.setMessage("Unexpected value for ReplicationType");
                        continue;
                }

            } else if (location.isCentral() && graph.getCentralLoc().equals(cluster.getDcaeLocationName())) {
                /*
                 * Provision Central to Global bridges
                 */
                switch (topic.getReplicationCase()) {

                    case REPLICATION_CENTRAL_TO_GLOBAL:
                    case REPLICATION_EDGE_TO_CENTRAL_TO_GLOBAL:
                        source = centralCname;
                        target = topic.getGlobalMrURL();
                        break;
                    case REPLICATION_GLOBAL_TO_CENTRAL:
                    case REPLICATION_GLOBAL_TO_CENTRAL_TO_EDGE:  // NOTE: this is for G2C portion only
                        source = topic.getGlobalMrURL();
                        target = centralCname;
                        break;

                    case REPLICATION_EDGE_TO_FQDN_TO_GLOBAL:  // NOTE: this is for E2F portion only
                        source = groupCentralCluster.getFqdn();
                        target = topic.getGlobalMrURL();
                        break;

                    case REPLICATION_FQDN_TO_GLOBAL:
                        source = groupCentralCluster.getFqdn();
                        target = topic.getGlobalMrURL();
                        break;

                    case REPLICATION_GLOBAL_TO_FQDN:
                    case REPLICATION_GLOBAL_TO_FQDN_TO_EDGE:  // NOTE: this is for G2F portion only
                        source = topic.getGlobalMrURL();
                        target = groupCentralCluster.getFqdn();
                        break;

                    case REPLICATION_FQDN_TO_EDGE:
                    case REPLICATION_EDGE_TO_FQDN:
                    case REPLICATION_EDGE_TO_CENTRAL:
                    case REPLICATION_CENTRAL_TO_EDGE:
                        break;
                    default:
                        logger.error("Unexpected value for ReplicationType (" + topic.getReplicationCase() + ") for topic " + topic.getFqtn());
                        anythingWrong = true;
                        err.setCode(400);
                        err.setFields("topic=" + topic.genFqtn() + " replicationCase="
                                + topic.getReplicationCase());
                        err.setMessage("Unexpected value for ReplicationType");
                        continue;
                }
            } else {
                logger.warn("dcaeLocation " + loc + " is neither Edge nor Central so no mmagent provisioning was done");
                anythingWrong = true;
                continue;
            }
            if (source != null && target != null) {
                try {
                    logger.info("Create a MM from " + source + " to " + target);
                    MirrorMaker mm = bridge.findNextMM(source, target, topic.getFqtn());
                    mm.addTopic(topic.getFqtn());
                    bridge.updateMirrorMaker(mm);
                } catch (Exception ex) {
                    err.setCode(500);
                    err.setFields("mirror_maker.topic");
                    err.setMessage("Unexpected condition: " + ex);
                    anythingWrong = true;
                    break;
                }
            }


        }
        return anythingWrong;

    }


    /*
     * Prior to 1707, we only supported EDGE_TO_CENTRAL replication.
     * This was determined automatically based on presence of edge publishers and central subscribers.
     * The following method is a modification of that original logic, to preserve some backwards compatibility,
     * i.e. to be used when no ReplicationType is specified.
     */

    public ReplicationType reviewTopic(Topic topic) {


        if (topic.getNumClients() > 1) {
            Graph graph = new Graph(topic.getClients(), false);

            String centralFqdn = new String();
            if (graph.hasCentral()) {
                DmaapConfig p = (DmaapConfig) DmaapConfig.getConfig();
                centralFqdn = p.getProperty("MR.CentralCname");
            }

            Collection<String> locations = graph.getKeys();
            for (String loc : locations) {
                logger.info("loc=" + loc);
                MR_Cluster cluster = clusters.getMr_ClusterByLoc(loc);
                if (cluster == null) {
                    logger.info("No MR cluster for location " + loc);
                    continue;
                }
                if (graph.hasCentral() && !graph.getCentralLoc().equals(cluster.getDcaeLocationName())) {
                    logger.info("Detected case for EDGE_TO_CENTRAL from " + cluster.getFqdn() + " to " + centralFqdn);
                    return ReplicationType.REPLICATION_EDGE_TO_CENTRAL;

                }

            }
        }

        return ReplicationType.REPLICATION_NONE;
    }

    private void updateApiError(ApiError err, ApiError topicSetupError) {
        err.setCode(topicSetupError.getCode());
        err.setMessage(topicSetupError.getMessage());
        err.setFields(topicSetupError.getFields());
    }
}
