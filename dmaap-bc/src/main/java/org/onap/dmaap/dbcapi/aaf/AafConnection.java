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

package org.onap.dmaap.dbcapi.aaf;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.ConnectException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.binary.Base64;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.util.DmaapConfig;


public class AafConnection extends BaseLoggingClass {





	private String aafCred;
	private String unit_test;


	private HttpsURLConnection uc;


	public AafConnection( String cred ) {
		aafCred = cred;
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
        unit_test = p.getProperty( "UnitTest", "No" );

	}


	private boolean makeConnection( String pURL ) {

		try {
			URL u = new URL( pURL );
			uc = (HttpsURLConnection) u.openConnection();
			uc.setInstanceFollowRedirects(false);
			logger.info( "successful connect to " + pURL );
			return(true);
		} catch ( UnknownHostException uhe ) {			
	        errorLogger.error(DmaapbcLogMessageEnum.UNKNOWN_HOST_EXCEPTION,  pURL, uhe.getMessage() );
	        logger.error("Error", uhe);
            return(false);
		} catch (Exception e) {
			logger.error("Error", e);
	        errorLogger.error(DmaapbcLogMessageEnum.HTTP_CONNECTION_ERROR,  pURL, e.getMessage());
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
			errorLogger.error( DmaapbcLogMessageEnum.IO_EXCEPTION + ex.getMessage(),ex);
		}

		return sb.toString();
	}
	


	public int postAaf( AafObject obj, String pURL ) {
		logger.info( "entry: postAaf() to  " + pURL  );
		String auth =  "Basic " + Base64.encodeBase64String(aafCred.getBytes());
		int rc = -1;


		if ( ! makeConnection( pURL ) ) {
			return rc;
		};


		byte[] postData = obj.getBytes();
		//logger.info( "post fields=" + postData );  //byte isn't very readable
		String responsemessage = null;
		String responseBody = null;

		try {
			if (auth != null) {
				uc.setRequestProperty("Authorization", auth);
	        }
			uc.setRequestMethod("POST");
			uc.setRequestProperty("Content-Type", "application/json");
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
			uc.setUseCaches(false);
			uc.setDoOutput(true);

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			uc.setSSLSocketFactory(sc.getSocketFactory());
			OutputStream os = null;

			
			try {
                 uc.connect();
                 os = uc.getOutputStream();
                 os.write( postData );

            } catch (ProtocolException pe) {
            	logger.error("Error", pe);
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error("Error", e);
                 }
            } catch ( SSLHandshakeException she ) {
            	logger.error("Error", she);
               	errorLogger.error( DmaapbcLogMessageEnum.SSL_HANDSHAKE_ERROR, pURL);
			} catch ( UnknownHostException uhe ) {
				logger.error("Error", uhe);
				errorLogger.error(DmaapbcLogMessageEnum.UNKNOWN_HOST_EXCEPTION,  pURL, uhe.getMessage() );
            	rc = 500;
            	return rc;
            } catch ( ConnectException ce ) {
            	logger.error("Error", ce);
				if ( "Yes".equals(unit_test) ) {
					rc = 201;
					return rc;
				}
				errorLogger.error(DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION,  pURL, ce.getMessage() );
            	rc = 500;
            	return rc;
			} 
			try {
				rc = uc.getResponseCode();
			} catch ( SSLHandshakeException she ) {
				logger.error("Error", she);
				errorLogger.error( DmaapbcLogMessageEnum.SSL_HANDSHAKE_ERROR, pURL);
            	rc = 500;
            	return rc;
            }
			logger.info( "http response code:" + rc );
            responsemessage = uc.getResponseMessage();
            logger.info( "responsemessage=" + responsemessage );

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
            if ( rc >= 200 && rc < 300 ) {
            	responseBody = bodyToString( uc.getInputStream() );
            	logger.info( "responseBody=" + responseBody );
            } else {
            		logger.warn( "Unsuccessful response: " + responsemessage );
            } 
            
		} catch (Exception e) {
            logger.error("Unable to read response  ");
            logger.error("Error", e);
        }
		finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error("Error", e);
			}
		}	
		return rc;
		
	}
	
	public int delAaf(AafObject obj, String pURL) {
		logger.info( "entry: delAaf() to  " + pURL  );
		String auth =  "Basic " + Base64.encodeBase64String(aafCred.getBytes());
		int rc = -1;

		
		if ( ! makeConnection( pURL ) ) {
			return rc;
		};
		

		byte[] postData = obj.getBytes();
		//logger.info( "post fields=" + postData );  //byte isn't very readable
		String responsemessage = null;
		String responseBody = null;

		try {
			if (auth != null) {
				uc.setRequestProperty("Authorization", auth);
	        }
			uc.setRequestMethod("DELETE");
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
            	logger.error("Error", pe);
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error("Error", e);
                 }
            } catch ( SSLHandshakeException she ) {
            	errorLogger.error( DmaapbcLogMessageEnum.SSL_HANDSHAKE_ERROR +"For:- "+pURL,she);
            }
			try {
				rc = uc.getResponseCode();
			} catch ( SSLHandshakeException she ) {
				logger.error("Error", she);
				errorLogger.error( DmaapbcLogMessageEnum.SSL_HANDSHAKE_ERROR, pURL);
            	rc = 500;
            	return rc;
            }
			logger.info( "http response code:" + rc );
            responsemessage = uc.getResponseMessage();
            logger.info( "responsemessage=" + responsemessage );

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
            if ( rc >= 200 && rc < 300 ) {
            	responseBody = bodyToString( uc.getInputStream() );
            	logger.info( "responseBody=" + responseBody );
            } else {
            		logger.warn( "Unsuccessful response: " + responsemessage );
            } 
            
		} catch (Exception e) {
            logger.error("Unable to read response  ");
            logger.error("Error", e);
        }	
		return rc;
		
	}

	private TrustManager[] trustAllCerts = new TrustManager[]{
		new X509TrustManager() {

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
			{
				//No need to implement.
			}
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
			{
				//No need to implement.
			}
		}
	};
	

}
