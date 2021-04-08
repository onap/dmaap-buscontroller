/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.client;

import org.apache.commons.codec.binary.Base64;
import org.onap.dmaap.dbcapi.aaf.AafDecrypt;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class MrProvConnection extends BaseLoggingClass{
        
    private String provURL;
    
    private HttpURLConnection uc;

    
    private String topicMgrCred;
    private String authMethod;
    private    String    user;
    private    String    encPwd;
    private	String	unit_test;
    private boolean hostnameVerify;
    
    public MrProvConnection() {
        String mechIdProperty = "aaf.TopicMgrUser";
        String pwdProperty = "aaf.TopicMgrPassword";
        DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
        user = p.getProperty( mechIdProperty, "noMechId@domain.netset.com" );
        encPwd = p.getProperty( pwdProperty, "notSet" );
        authMethod = p.getProperty("MR.authentication", "none");
        topicMgrCred =  getCred();
        hostnameVerify= "true".equalsIgnoreCase(p.getProperty("MR.hostnameVerify", "true"));
        unit_test = p.getProperty( "UnitTest", "No" );
        
    }
    
    private String getCred( ) {


        String pwd = "";
        AafDecrypt decryptor = new AafDecrypt();    
        pwd = decryptor.decrypt(encPwd);
        return user + ":" + pwd;    
    }
    
    
    public boolean makeTopicConnection( MR_Cluster cluster ) {
        boolean rc = false;
    	logger.info( "connect to cluster: " + cluster.getDcaeLocationName());
        

        provURL = cluster.getTopicProtocol() + "://" + cluster.getFqdn() + ":" + cluster.getTopicPort() + "/topics/create";

        if ( cluster.getTopicProtocol().equals( "https" ) ) {
            rc = makeSecureConnection( provURL );
        } else {
        	rc = makeConnection( provURL );
        }
      	if ( rc  && unit_test.equals( "Yes" ) ) {
      		// set timeouts low so we don't hold up unit tests in build process
            uc.setReadTimeout(5);
            uc.setConnectTimeout(5);   		
      	}
      	return rc;
        
    }

    private boolean makeSecureConnection( String pURL ) {
        logger.info( "makeConnection to " + pURL );
    
        try {
    	
			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				@Override
				public boolean verify( String hostname, SSLSession session ) {
					return true;
				}
			
			};
            URL u = new URL( pURL );
            uc = (HttpsURLConnection) u.openConnection();
            uc.setInstanceFollowRedirects(false);
            if ( ! hostnameVerify ) {
				HttpsURLConnection ucs = (HttpsURLConnection) uc;
				ucs.setHostnameVerifier(hostnameVerifier);
			}
            logger.info( "open secure connect to " + pURL );
            return(true);
        } catch( UnknownHostException uhe ){
            logger.error( "Caught UnknownHostException for " + pURL);
            return(false);
        } catch (Exception e) {
            logger.error("Unexpected error during openConnection of " + pURL );
            logger.error("Unexpected error during openConnection of ",e );
            return(false);
        } 

    }
    private boolean makeConnection( String pURL ) {
        logger.info( "makeConnection to " + pURL );
    
        try {
            URL u = new URL( pURL );
            uc = (HttpURLConnection) u.openConnection();
            uc.setInstanceFollowRedirects(false);			

            logger.info( "open connect to " + pURL );
            return(true);
        } catch( UnknownHostException uhe ){
            logger.error( "Caught UnknownHostException for " + pURL);
            return(false);
        } catch (Exception e) {
            logger.error("Unexpected error during openConnection of " + pURL );
            logger.error("Unexpected error during openConnection of ",e );
            return(false);
        } 

    }
    
    static String bodyToString( InputStream is ) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader( new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null ) {
                sb.append( line );
            }
        } catch (IOException ex ) {
            errorLogger.error( "IOexception:" + ex);
        }
            
        return sb.toString();
    }
    
    public String doPostTopic( Topic postTopic, ApiError err ) {
        String auth =  "Basic " + Base64.encodeBase64String(topicMgrCred.getBytes());


        String responsemessage = null;
        int rc = -1;


        try {
            byte[] postData = postTopic.getBytes();
            logger.info( "post fields=" + Arrays.toString(postData));
            
			if ( authMethod.equalsIgnoreCase("basicAuth") ) {
				uc.setRequestProperty("Authorization", auth);
				logger.info( "Authenticating with " + auth );
			} else if ( authMethod.equalsIgnoreCase("cert")) {
				logger.error( "MR.authentication set for client certificate.  Not supported yet.");
			}
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setRequestProperty( "charset", "utf-8");
            uc.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
            uc.setUseCaches(false);
            uc.setDoOutput(true);
            OutputStream os = null;

            
            try {
                 uc.connect();
                 os = uc.getOutputStream();
                 os.write( postData );

            } catch (ProtocolException pe) {
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                 }
            } catch ( UnknownHostException uhe ) {
                errorLogger.error( DmaapbcLogMessageEnum.UNKNOWN_HOST_EXCEPTION , "Unknown Host Exception" , provURL );
                err.setCode(500);
                err.setMessage("Unknown Host Exception");
                err.setFields( uc.getURL().getHost());
                return new String( "500: " + uhe.getMessage());
            }catch ( ConnectException ce ) {
               	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPostMessage because unit_test =" + unit_test );
            	} else { 
	                errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, "HTTP Connection Exception"  );
	                err.setCode(500);
	                err.setMessage("HTTP Connection Exception");
	                err.setFields( uc.getURL().getHost());
                return new String( "500: " + ce.getMessage());
            	}
            }
            rc = uc.getResponseCode();
            logger.info( "http response code:" + rc );
            err.setCode(rc);
            responsemessage = uc.getResponseMessage();
            logger.info( "responsemessage=" + responsemessage );
            err.setMessage(responsemessage);


            if (responsemessage == null) {
                 // work around for glitch in Java 1.7.0.21 and likely others
                 // When Expect: 100 is set and a non-100 response is received, the response message is not set but the response code is
                 String h0 = uc.getHeaderField(0);
                 if (h0 != null) {
                     int i = h0.indexOf(' ');
                     int j = h0.indexOf(' ', i + 1);
                     if (i != -1 && j != -1) {
                         responsemessage = h0.substring(j + 1);
                     }
                 }
            }
            if (rc >= 200 && rc < 300 ) {
                String responseBody = null;
                 responseBody = bodyToString( uc.getInputStream() );
                logger.info( "responseBody=" + responseBody );
                return responseBody;

            } 
            
        } catch (Exception e) {
            errorLogger.error("Unable to read response:  " + e.getMessage() );
           
        }
        finally {
            try {
                uc.disconnect();
            } catch ( Exception e ) {
                errorLogger.error("Unable to disconnect");
            }
        }
        return new String( rc +": " + responsemessage );

    }
    


        
}
