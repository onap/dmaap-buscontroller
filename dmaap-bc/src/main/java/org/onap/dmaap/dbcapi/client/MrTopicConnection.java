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

package org.onap.dmaap.dbcapi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.apache.commons.codec.binary.Base64;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class MrTopicConnection extends BaseLoggingClass  {
	private String topicURL;
	
	private HttpURLConnection uc;

	
	private  String mmProvCred; 
	private	String unit_test;
	private String authMethod;
	private boolean hostnameVerify;

	public MrTopicConnection(String user, String pwd ) {
		mmProvCred = new String( user + ":" + pwd );
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
        unit_test = p.getProperty( "UnitTest", "No" );
    	authMethod = p.getProperty("MR.authentication", "none");
    	hostnameVerify= "true".equalsIgnoreCase(p.getProperty("MR.hostnameVerify", "true"));
	}
	
	public boolean makeTopicConnection( MR_Cluster cluster, String topic, String overrideFqdn ) {
		String fqdn = overrideFqdn != null ? overrideFqdn : cluster.getFqdn();
		logger.info( "connect to cluster: " + fqdn + " for topic: " + topic );
	

		topicURL = cluster.getTopicProtocol() + "://" + fqdn + ":" + cluster.getTopicPort() + "/events/" + topic ;

		if ( "https".equals(cluster.getTopicProtocol())) {
			return makeSecureConnection( topicURL );
		}
		return makeConnection( topicURL );
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
	
			logger.info( "open connection to " + pURL );
			return(true);
		} catch (Exception e) {
            logger.error("Unexpected error during openConnection of " + pURL );
            logger.error("Error", e);;
            return(false);
        }

	}
	private boolean makeConnection( String pURL ) {
		logger.info( "makeConnection to " + pURL );
	
		try {
			URL u = new URL( pURL );
			uc = (HttpURLConnection) u.openConnection();
			uc.setInstanceFollowRedirects(false);
			logger.info( "open connection to " + pURL );
			return(true);
		} catch (Exception e) {
            logger.error("Unexpected error during openConnection of " + pURL );
            logger.error("error", e);
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
	
	public ApiError doPostMessage( String postMessage ) {
		ApiError response = new ApiError();
		String auth =  "Basic " + Base64.encodeBase64String(mmProvCred.getBytes());



		try {
			byte[] postData = postMessage.getBytes();
			logger.info( "post fields=" + postMessage );
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
            	callSetDoOutputOnError();
                 
            }  catch ( SSLException se ) {
            	logger.error("Error", se);
        		response.setCode(500);
    			response.setMessage( se.getMessage());
    			return response;
            	
            }
			response.setCode( uc.getResponseCode());
			logger.info( "http response code:" + response.getCode());
            response.setMessage( uc.getResponseMessage() ); 
            logger.info( "response message=" + response.getMessage() );


            if ( response.getMessage() == null) {
                 // work around for glitch in Java 1.7.0.21 and likely others
                 // When Expect: 100 is set and a non-100 response is received, the response message is not set but the response code is
                 String h0 = uc.getHeaderField(0);
                 if (h0 != null) {
                     int i = h0.indexOf(' ');
                     int j = h0.indexOf(' ', i + 1);
                     if (i != -1 && j != -1) {
                         response.setMessage( h0.substring(j + 1) );
                     }
                 }
            }
            if ( response.is2xx() ) {
         		response.setFields( bodyToString( uc.getInputStream() ) );
    			logger.info( "responseBody=" + response.getFields() );
    			return response;

            } 
            
		} catch (Exception e) {
        	if ( unit_test.equals( "Yes" ) ) {
				response.setCode(201);
				response.setMessage( "simulated response");
				logger.info( "artificial 201 response from doPostMessage because unit_test =" + unit_test );
        	} else {

				response.setCode(500);
				response.setMessage( "Unable to read response");
				logger.warn( response.getMessage() );
            	logger.error("Error", e);
			}
        }
		finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error("Error", e);
			}
		}
		return response;

	}
	
	public void callSetDoOutputOnError() {
		try {
            // work around glitch in Java 1.7.0.21 and likely others
            // without this, Java will connect multiple times to the server to run the same request
            uc.setDoOutput(false);
        } catch (Exception e) {
       	 	logger.error("Error", e);
        }
	}

}
