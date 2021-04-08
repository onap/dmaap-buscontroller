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

package org.onap.dmaap.dbcapi.database;

import java.util.*;
import java.sql.*;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.Singleton;


public class DatabaseClass extends BaseLoggingClass {

    private static Singleton<Dmaap> dmaap;
    private static Map<String, DcaeLocation> dcaeLocations;
    private static Map<String, DR_Node> dr_nodes;
    private static Map<String, DR_Pub> dr_pubs;
    private static Map<String, DR_Sub> dr_subs;
    private static Map<String, MR_Client> mr_clients;
    private static Map<String, MR_Cluster> mr_clusters;
    private static Map<String, Feed> feeds;
    private static Map<String, Topic> topics;
    private static Map<String, MirrorMaker> mirrors;

    private static long lastTime = 0L;
    private static DBType databaseType;

    private enum DBType {
        PGSQL, MEMORY
    }

    public static Singleton<Dmaap> getDmaap() {
        return dmaap;
    }


    public static Map<String, DcaeLocation> getDcaeLocations() {
        return dcaeLocations;
    }

    public static Map<String, DR_Node> getDr_nodes() {
        return dr_nodes;
    }

    public static Map<String, DR_Sub> getDr_subs() {
        return dr_subs;
    }

    public static Map<String, DR_Pub> getDr_pubs() {
        return dr_pubs;
    }

    public static Map<String, MR_Client> getMr_clients() {
        return mr_clients;
    }


    public static Map<String, MR_Cluster> getMr_clusters() {
        return mr_clusters;
    }

    public static Map<String, Feed> getFeeds() {
        return feeds;
    }

    public static Map<String, Topic> getTopics() {
        return topics;
    }

    public static Map<String, MirrorMaker> getMirrorMakers() {
        return mirrors;
    }

    static {
        try {
            appLogger.info("begin static initialization");
            appLogger.info("initializing dmaap");
            determineDatabaseType();

            switch (databaseType) {
                case PGSQL:
                    databaseResourceInit();
                    break;
                case MEMORY:
                    inMemoryResourceInit();
                    break;
            }

            dmaap.init(new Dmaap.DmaapBuilder().setVer("0").setTnr("").setDn("").setDpu("").setLu("").setBat("").setNk("").setAko("").createDmaap());
            // force initial read from DB, if it exists
            @SuppressWarnings("unused")
            Dmaap dmx = dmaap.get();

            // old code in this spot would read from properties file as part of init.
            // but all those properties are now set via /dmaap API

        } catch (Exception e) {
            errorLogger.error("Error", e);
            errorLogger.error(DmaapbcLogMessageEnum.DB_UPDATE_ERROR, e.getMessage());
        }

    }

    public static synchronized String getNextClientId() {

        long id = System.currentTimeMillis();
        if (id <= lastTime) {
            id = lastTime + 1;
        }
        lastTime = id;
        return Long.toString(id);
    }

    public static synchronized void clearDatabase() {
        switch (databaseType) {
            case PGSQL:
                try {
                    initDatabase();
                } catch (Exception e) {
                    errorLogger.error("Error initializing database access " + e, e);
                }
                break;
            case MEMORY:
                initMemoryDatabase();
                break;
        }
    }

    private static void inMemoryResourceInit() {
        appLogger.info("Data from memory");
        dmaap = new Singleton<Dmaap>() {
            private Dmaap dmaap;

            public void remove() {
                dmaap = null;
            }

            public void init(Dmaap val) {
                if (dmaap == null) {
                    dmaap = val;
                } else {
                    update(val);
                }
            }

            public Dmaap get() {
                return (dmaap);
            }

            public void update(Dmaap nd) {
                dmaap.setVersion(nd.getVersion());
                dmaap.setTopicNsRoot(nd.getTopicNsRoot());
                dmaap.setDmaapName(nd.getDmaapName());
                dmaap.setDrProvUrl(nd.getDrProvUrl());
                dmaap.setBridgeAdminTopic(nd.getBridgeAdminTopic());
                dmaap.setLoggingUrl(nd.getLoggingUrl());
                dmaap.setNodeKey(nd.getNodeKey());
                dmaap.setAccessKeyOwner(nd.getAccessKeyOwner());
            }
        };
        initMemoryDatabase();
    }

    private static void databaseResourceInit() {
        appLogger.info("Data from database");
        try {
            LoadSchema.loadSchema();
        } catch (Exception e) {
            appLogger.warn("Problem updating DB schema", e);
        }
        try {
            Thread.sleep(5000);
            dmaap = new DBSingleton<>(Dmaap.class, "dmaap");
            TableHandler.setSpecialCase("topic", "replication_case", new TopicReplicationTypeHandler());
            TableHandler.setSpecialCase("mirror_maker", "topics", new MirrorTopicsHandler());
            initDatabase();
        } catch (Exception e) {
            errorLogger.error("Error initializing database access " + e, e);
            System.exit(1);
        }
    }

    private static class MirrorTopicsHandler implements DBFieldHandler.SqlOp {

        public Object get(ResultSet rs, int index) throws Exception {
            String val = rs.getString(index);
            if (val == null) {
                return (null);
            }
            List<String> rv = new ArrayList<>();
            for (String s : val.split(",")) {
                rv.add(new String(s));
            }
            return (rv);
        }

        public void set(PreparedStatement ps, int index, Object val) throws Exception {
            if (val == null) {
                ps.setString(index, null);
                return;
            }
            @SuppressWarnings("unchecked")
            List<String> xv = (List<String>) val;
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for (Object o : xv) {
                String rv = (String) o;
                sb.append(sep).append(DBFieldHandler.fesc(rv));
                sep = ",";
            }
            ps.setString(index, sb.toString());
        }
    }

    private static class TopicReplicationTypeHandler implements DBFieldHandler.SqlOp {

        public Object get(ResultSet rs, int index) throws Exception {
            int val = rs.getInt(index);

            return (ReplicationType.valueOf(val));
        }

        public void set(PreparedStatement ps, int index, Object val) throws Exception {
            if (val == null) {
                ps.setInt(index, 0);
                return;
            }
            @SuppressWarnings("unchecked")
            ReplicationType rep = (ReplicationType) val;
            ps.setInt(index, rep.getValue());
        }
    }

    private static void initMemoryDatabase() {
        dcaeLocations = new HashMap<>();
        dr_nodes = new HashMap<>();
        dr_pubs = new HashMap<>();
        dr_subs = new HashMap<>();
        mr_clients = new HashMap<>();
        mr_clusters = new HashMap<>();
        feeds = new HashMap<>();
        topics = new HashMap<>();
        mirrors = new HashMap<>();
    }

    private static void initDatabase() throws Exception {
        dcaeLocations = new DBMap<>(DcaeLocation.class, "dcae_location", "dcae_location_name");
        dr_nodes = new DBMap<>(DR_Node.class, "dr_node", "fqdn");
        dr_pubs = new DBMap<>(DR_Pub.class, "dr_pub", "pub_id");
        dr_subs = new DBMap<>(DR_Sub.class, "dr_sub", "sub_id");
        mr_clients = new DBMap<>(MR_Client.class, "mr_client", "mr_client_id");
        mr_clusters = new DBMap<>(MR_Cluster.class, "mr_cluster", "dcae_location_name");
        feeds = new DBMap<>(Feed.class, "feed", "feed_id");
        topics = new DBMap<>(Topic.class, "topic", "fqtn");
        mirrors = new DBMap<>(MirrorMaker.class, "mirror_maker", "mm_name");
    }

    private static void determineDatabaseType() {
        DmaapConfig dmaapConfig = (DmaapConfig) DmaapConfig.getConfig();
        String isPgSQLset = dmaapConfig.getProperty("UsePGSQL", "false");
        databaseType = isPgSQLset.equalsIgnoreCase("true") ? DBType.PGSQL : DBType.MEMORY;
    }
}
