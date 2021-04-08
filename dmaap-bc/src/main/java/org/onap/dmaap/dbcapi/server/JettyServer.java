/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property.
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

package org.onap.dmaap.dbcapi.server;

import com.google.common.collect.Sets;
import java.util.Properties;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

/**
 * A  Jetty server which supports:
 * 	- http and https (simultaneously for dev env)
 *  - REST API context
 *  - static html pages (for documentation).
 */
public class JettyServer extends BaseLoggingClass {

    private static final CertificateManager certificateManager =
        new CertficateManagerFactory(DmaapConfig.getConfig()).initCertificateManager();
    private final Server server;


    public Server getServer() {
        return server;
    }

    public static CertificateManager getCertificateManager() {
        return certificateManager;
    }

    public JettyServer(Properties params) {

        server = new Server();
        int httpPort = Integer.parseInt(params.getProperty("IntHttpPort", "80"));
        int sslPort = Integer.parseInt(params.getProperty("IntHttpsPort", "443"));
        boolean allowHttp = Boolean.parseBoolean(params.getProperty("HttpAllowed", "false"));
        serverLogger.info("port params: http=" + httpPort + " https=" + sslPort);
        serverLogger.info("allowHttp=" + allowHttp);

        // HTTP Server
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(sslPort);
        httpConfig.setOutputBufferSize(32768);

        try (ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig))) {
            httpConnector.setPort(httpPort);
            httpConnector.setIdleTimeout(30000);

            // HTTPS Server
            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());
            SslContextFactory sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setWantClientAuth(true);

            if ( ! certificateManager.isReady()) {
            	serverLogger.error("CertificateManager is not ready.  NOT starting https!");
            } else {
            	setUpKeystore(sslContextFactory);
            	setUpTrustStore(sslContextFactory);
          

	            if (sslPort != 0) {
	                try (ServerConnector sslConnector = new ServerConnector(server,
	                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
	                    new HttpConnectionFactory(httpsConfig))) {
	                    sslConnector.setPort(sslPort);
	                    server.addConnector(sslConnector);
	                    serverLogger.info("Starting sslConnector on port " + sslPort + " for https");
	                }
	            } else {
	                serverLogger.info("NOT starting sslConnector because InHttpsPort param is " + sslPort );
	            }
            } 
            if (allowHttp) {
                serverLogger.info("Starting httpConnector on port " + httpPort);
                server.addConnector(httpConnector);
            } else {
                serverLogger.info("NOT starting httpConnector because HttpAllowed param is " + allowHttp);
            }
        }

        // Set context for servlet.  This is shared for http and https
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder jerseyServlet = context
            .addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/webapi/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "org.onap.dmaap.dbcapi.resources");
        jerseyServlet.setInitParameter("javax.ws.rs.Application", "org.onap.dmaap.dbcapi.server.ApplicationConfig");

        // also serve up some static pages...
        ServletHolder staticServlet = context.addServlet(DefaultServlet.class, "/*");
        staticServlet.setInitParameter("resourceBase", "www");
        staticServlet.setInitParameter("pathInfoOnly", "true");

        registerAuthFilters(context);

        try {

            serverLogger.info("Starting jetty server");
            String unitTest = params.getProperty("UnitTest", "No");
            serverLogger.info("UnitTest=" + unitTest);
            if (unitTest.equals("No")) {
                server.start();
                server.dumpStdErr();
                server.join();
            }
        } catch (Exception e) {
            errorLogger.error("Exception " + e);
        } finally {
            server.destroy();
        }

    }

    private void registerAuthFilters(ServletContextHandler context) {
        context.addFilter("org.onap.dmaap.dbcapi.resources.AAFAuthenticationFilter", "/webapi/*",
            Sets.newEnumSet(Sets.newHashSet(DispatcherType.FORWARD, DispatcherType.REQUEST), DispatcherType.class));
        context.addFilter("org.onap.dmaap.dbcapi.resources.AAFAuthorizationFilter", "/webapi/*",
            Sets.newEnumSet(Sets.newHashSet(DispatcherType.FORWARD, DispatcherType.REQUEST), DispatcherType.class));
    }

    private void setUpKeystore(SslContextFactory sslContextFactory) {
        String keystore = JettyServer.certificateManager.getKeyStoreFile();
        logger.info("https Server using keystore at " + keystore);
        sslContextFactory.setKeyStorePath(keystore);
        sslContextFactory.setKeyStoreType(JettyServer.certificateManager.getKeyStoreType());
        sslContextFactory.setKeyStorePassword(JettyServer.certificateManager.getKeyStorePassword());
        sslContextFactory.setKeyManagerPassword(JettyServer.certificateManager.getKeyStorePassword());
    }

    private void setUpTrustStore(SslContextFactory sslContextFactory) {
        String truststore = JettyServer.certificateManager.getTrustStoreFile();
        logger.info("https Server using truststore at " + truststore);
        sslContextFactory.setTrustStorePath(truststore);
        sslContextFactory.setTrustStoreType(JettyServer.certificateManager.getTrustStoreType());
        sslContextFactory.setTrustStorePassword(JettyServer.certificateManager.getTrustStorePassword());
    }
}
