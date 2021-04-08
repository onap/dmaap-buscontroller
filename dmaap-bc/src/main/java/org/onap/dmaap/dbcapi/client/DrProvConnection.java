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
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;



public class DrProvConnection extends BaseLoggingClass {
	   
   
	private String provURL;
	private	String provApi;
	private	String	behalfHeader;
	private	String	feedContentType;
	private	String	subContentType;
	private	String unit_test;
	private	String	provURI;
	
	private HttpsURLConnection uc;


	public DrProvConnection() {
		provURL = new DmaapService().getDmaap().getDrProvUrl();
		if ( provURL.length() < 1 ) {
			errorLogger.error( DmaapbcLogMessageEnum.PREREQ_DMAAP_OBJECT, "DmaapService().getDmaap().getDrProvUrl()");
		}
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		provApi = p.getProperty( "DR.provApi", "ONAP" );
		behalfHeader = p.getProperty( "DR.onBehalfHeader", "X-DMAAP-DR-ON-BEHALF-OF");
		feedContentType = p.getProperty( "DR.feedContentType", "application/vnd.dmaap-dr.feed");
		subContentType = p.getProperty( "DR.subContentType", "application/vnd.dmaap-dr.subscription");
		provURI = p.getProperty( "DR.ProvisioningURI", "/internal/prov");
		logger.info( "provURL=" + provURL + " provApi=" + provApi + " behalfHeader=" + behalfHeader
				+ " feedContentType=" + feedContentType + " subContentType=" + subContentType );
		unit_test = p.getProperty( "UnitTest", "No" );
			
	}
	
	public boolean makeFeedConnection() {
		return makeConnection( provURL );
	}
	public boolean makeFeedConnection(String feedId) {
		return makeConnection( provURL + "/feed/" + feedId );	
	}
	public boolean makeSubPostConnection( String subURL ) {
		String[] parts = subURL.split("/");
		String revisedURL = provURL + "/" + parts[3] + "/" + parts[4];
		logger.info( "mapping " + subURL + " to " + revisedURL );
		return makeConnection( revisedURL );
	}
	public boolean makeSubPutConnection( String subId ) {
		String revisedURL = provURL + "/subs/" + subId;
		logger.info( "mapping " + subId + " to " + revisedURL );
		return makeConnection( revisedURL );
	}

	public boolean makeIngressConnection( String feed, String user, String subnet, String nodep ) {
		String uri = String.format("/internal/route/ingress/?feed=%s&user=%s&subnet=%s&nodepatt=%s", 
					feed, user, subnet, nodep );
		return makeConnection( provURL + uri );
	}
	public boolean makeEgressConnection( String sub, String nodep ) {
		String uri = String.format("/internal/route/egress/?sub=%s&node=%s", 
					sub,  nodep );
		return makeConnection( provURL + uri );
	}
	public boolean makeDumpConnection() {
		String url = provURL + provURI;
		return makeConnection( url );
	}
	public boolean makeNodesConnection( String varName ) {
		
		String uri = String.format("/internal/api/%s", varName);
		return makeConnection( provURL + uri );
	}
	
	public boolean makeNodesConnection( String varName, String val ) {

		if ( val == null ) {
			return false;
		} 
		String cv = val.replaceAll("\\|", "%7C");
		String uri = String.format( "/internal/api/%s?val=%s", varName, cv );

		return makeConnection( provURL + uri );
	}
	
	private boolean makeConnection( String pURL ) {
	
		try {
			URL u = new URL( pURL );
			uc = (HttpsURLConnection) u.openConnection();
			uc.setInstanceFollowRedirects(false);
			logger.info( "successful connect to " + pURL );
			uc.setSSLSocketFactory(DmaapConfig.getSSLSocketFactory());
			return(true);
		} catch (Exception e) {
			errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_ERROR,  pURL, e.getMessage() );
            return(false);
        }
	}
	
	public String bodyToString( InputStream is ) {
		logger.info( "is=" + is );
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader( new InputStreamReader(is));
		String line;
		try {
			while ((line = br.readLine()) != null ) {
				sb.append( line );
			}
		} catch (IOException ex ) {
			errorLogger.error( DmaapbcLogMessageEnum.IO_EXCEPTION, ex.getMessage());
		}
			
		return sb.toString();
	}
	

	public  String doPostFeed( Feed postFeed, ApiError err ) {

		byte[] postData = postFeed.getBytes();
		logger.info( "post fields=" + Arrays.toString(postData) );
		String responsemessage = null;
		String responseBody = null;
		int rc = -1;

		try {
			logger.info( "uc=" + uc );
			uc.setRequestMethod("POST");
			uc.setRequestProperty("Content-Type", feedContentType);
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, postFeed.getOwner() );
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
            } catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
            if (rc == 201 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );

            } else {
            	err.setCode( rc );
            	err.setMessage(responsemessage);
            }
            
		} catch (ConnectException ce) {
			errorLogger.error(DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage() );
            err.setCode( 500 );
        	err.setMessage("Backend connection refused");
		} catch (SocketException se) {
			errorLogger.error( DmaapbcLogMessageEnum.SOCKET_EXCEPTION, se.getMessage(), "response from prov server" );
			err.setCode( 500 );
			err.setMessage( "Unable to read response from DR");
        } catch (Exception e) {
           	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPostFeed because unit_test =" + unit_test );
           	} else {
	            logger.warn("Unable to read response  " );
	            errorLogger.error("Unable to read response  ", e.getMessage());
	            try {
		            err.setCode( uc.getResponseCode());
		            err.setMessage(uc.getResponseMessage());
	            } catch (Exception e2) {
	            	err.setCode( 500 );
	            	err.setMessage("Unable to determine response message");
	            }
           	}
        } 
		finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error(e.getMessage(), e);
			}
		}
		return responseBody;

	}

	
	// the POST for /internal/route/ingress doesn't return any data, so needs a different function
	// the POST for /internal/route/egress doesn't return any data, so needs a different function	
	public int doXgressPost( ApiError err ) {
		
		String responsemessage = null;
		int rc = -1;

		try {
			uc.setRequestMethod("POST");


			try {
                 uc.connect();

            } catch (ProtocolException pe) {
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                 	logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
			logger.info( "http response code:" + rc );
            responsemessage = uc.getResponseMessage();
            logger.info( "responsemessage=" + responsemessage );



            if (rc < 200 || rc >= 300 ) {
            	err.setCode( rc );
            	err.setMessage(responsemessage);
            }
		} catch (Exception e) {
           	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doXgressPost because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  " );
	            logger.error(e.getMessage(), e);
           	}
        }		
        finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error(e.getMessage(), e);
			}
		}
	
		return rc;

	}
	
	public String doPostDr_Sub( DR_Sub postSub, ApiError err ) {
		logger.info( "entry: doPostDr_Sub() "  );
		byte[] postData = postSub.getBytes(provApi );
		logger.info( "post fields=" + postData );
		String responsemessage = null;
		String responseBody = null;

		try {
	
			uc.setRequestMethod("POST");
		
			uc.setRequestProperty("Content-Type", subContentType );
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, "DGL" );
			uc.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
			uc.setUseCaches(false);
			uc.setDoOutput(true);
			OutputStream os = null;
			int rc = -1;
			
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
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
            if (rc == 201 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );

            } else {
            	err.setCode(rc);
            	err.setMessage(responsemessage);
            }
            
		} catch (Exception e) {
          	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPostDr_Sub because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  ", e.getMessage());
           	}
        }		
		finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error(e.getMessage(), e);
			}
		}
		return responseBody;

	}
	

	public String doPutFeed(Feed putFeed, ApiError err) {
		byte[] postData = putFeed.getBytes();
		logger.info( "post fields=" + Arrays.toString(postData) );
		String responsemessage = null;
		String responseBody = null;

		try {
			logger.info( "uc=" + uc );
			uc.setRequestMethod("PUT");
			uc.setRequestProperty("Content-Type", feedContentType );
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, putFeed.getOwner() );
			uc.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
			uc.setUseCaches(false);
			uc.setDoOutput(true);
			OutputStream os = null;
			int rc = -1;
			
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
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
            if (rc >= 200 && rc < 300 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );
    			err.setCode( rc );
            } else if ( rc == 404 ) {
            	err.setCode( rc );
            	err.setFields( "feedid");
            	String message =  "FeedId " + putFeed.getFeedId() + " not found on DR to update.  Out-of-sync condition?";
            	err.setMessage( message );
            	errorLogger.error( DmaapbcLogMessageEnum.PROV_OUT_OF_SYNC, "Feed", putFeed.getFeedId() );
            	
            } else {
            	err.setCode( rc );
            	err.setMessage(responsemessage);
            }
            
		} catch (ConnectException ce) {
			if ( unit_test.equals( "Yes" ) ) {
				err.setCode(200);
				err.setMessage( "simulated response");
				logger.info( "artificial 200 response from doPutFeed because unit_test =" + unit_test );
			} else {
				errorLogger.error(DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage());
				err.setCode(500);
				err.setMessage("Backend connection refused");
			}
		} catch (SocketException se) {
			errorLogger.error( DmaapbcLogMessageEnum.SOCKET_EXCEPTION, se.getMessage(), "response from Prov server" );
			err.setCode( 500 );
			err.setMessage( "Unable to read response from DR");
        } catch (Exception e) {
          	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPutFeed because unit_test =" + unit_test );
           	} else {
	            logger.warn("Unable to read response  " );
	            logger.error(e.getMessage(), e);
           	}
            try {
	            err.setCode( uc.getResponseCode());
	            err.setMessage(uc.getResponseMessage());
            } catch (Exception e2) {
            	err.setCode( 500 );
            	err.setMessage("Unable to determine response message");
            	logger.error(e2.getMessage(), e2);
            }
        } 		finally {
			try {
				uc.disconnect();
			} catch ( Exception e ) {
				logger.error(e.getMessage(), e);
			}
		}
		return responseBody;
	}
	public String doPutDr_Sub(DR_Sub postSub, ApiError err) {
		logger.info( "entry: doPutDr_Sub() "  );
		byte[] postData = postSub.getBytes(provApi);
		logger.info( "post fields=" + postData );
		String responsemessage = null;
		String responseBody = null;

		try {
	
			uc.setRequestMethod("PUT");
		
			uc.setRequestProperty("Content-Type", subContentType );
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, "DGL" );
			uc.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
			uc.setUseCaches(false);
			uc.setDoOutput(true);
			OutputStream os = null;
			int rc = -1;
			
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
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
            if (rc == 200 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );

            } else {
            	err.setCode(rc);
            	err.setMessage(responsemessage);
            }
            
		} catch (ConnectException ce) {
            errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage() );
            err.setCode( 500 );
        	err.setMessage("Backend connection refused");
        	logger.error(ce.getMessage(), ce);
		} catch (Exception e) {
          	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPutDr_Sub because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  " );
	            logger.error(e.getMessage(), e);
           	}
        } finally {
        	if(null != uc){
        	    uc.disconnect();
        	}
        }
		return responseBody;

	}
	
	public String doGetNodes( ApiError err ) {
		logger.info( "entry: doGetNodes() "  );
		//byte[] postData = postSub.getBytes();
		//logger.info( "get fields=" + postData );
		String responsemessage = null;
		String responseBody = null;

		try {
	
			uc.setRequestMethod("GET");
			int rc = -1;
			

			try {
                uc.connect();
	

            } catch (ProtocolException pe) {

                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
	
			rc = uc.getResponseCode();
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
	
        	err.setCode(rc);  // may not really be an error, but we save rc
            if (rc == 200 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );
            } else {
            	err.setMessage(responsemessage);
            }
            

		} catch (ConnectException ce) {
			if ( unit_test.equals( "Yes" ) ) {
				err.setCode(200);
				err.setMessage( "simulated response");
				logger.info( "artificial 200 response from doGetNodes because unit_test =" + unit_test );
			} else {
				errorLogger.error(DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage());
				err.setCode(500);
				err.setMessage("Backend connection refused");
				logger.error(ce.getMessage(), ce);
			}
		} catch (Exception e) {
         	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doGetNodes because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  ", e.getMessage());
           	}
        } finally {

			if ( uc != null ) uc.disconnect();
        }

		return responseBody;

	}
	public String doPutNodes( ApiError err ) {
		logger.info( "entry: doPutNodes() "  );
		String responsemessage = null;
		String responseBody = null;

		try {
			uc.setRequestMethod("PUT");
			uc.setUseCaches(false);
			int rc = -1;
			
			try {
                 uc.connect();
            } catch (ProtocolException pe) {
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
          	err.setCode(rc);
            if (rc == 200 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );

            } else {
  
            	err.setMessage(responsemessage);
            }
            
		} catch (Exception e) {
         	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doPutNodes because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  ", e.getMessage());
           	}
        } finally {
			if ( uc != null ) {
        		uc.disconnect();
			}
        }
		return responseBody;

	}
	
	public String doDeleteFeed(Feed putFeed, ApiError err) {
		String responsemessage = null;
		String responseBody = null;

		try {
			logger.info( "uc=" + uc );
			uc.setRequestMethod("DELETE");
			uc.setRequestProperty("Content-Type", feedContentType );
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, putFeed.getOwner() );
			uc.setUseCaches(false);
			uc.setDoOutput(true);
			OutputStream os = null;
			int rc = -1;
			
			try {
                 uc.connect();
                 os = uc.getOutputStream();
                 //os.write( postData );

            } catch (ProtocolException pe) {
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
            if (rc >= 200 && rc < 300 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );

            } else if ( rc == 404 ) {
            	err.setCode( rc );
            	err.setFields( "feedid");
            	String message =  "FeedId " + putFeed.getFeedId() + " not found on DR to update.  Out-of-sync condition?";
            	err.setMessage( message );
            	errorLogger.error( DmaapbcLogMessageEnum.PROV_OUT_OF_SYNC, "Feed", putFeed.getFeedId() );
            	
            } else {
            	err.setCode( rc );
            	err.setMessage(responsemessage);
            }
            
		} catch (ConnectException ce) {
			errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage() );
            err.setCode( 500 );
        	err.setMessage("Backend connection refused");
        	logger.error(ce.getMessage(), ce);
		} catch (SocketException se) {
			errorLogger.error( DmaapbcLogMessageEnum.SOCKET_EXCEPTION, se.getMessage(), "response from Prov server" );
			err.setCode( 500 );
			err.setMessage( "Unable to read response from DR");
			logger.error(se.getMessage(), se);
        } catch (Exception e) {
         	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doDeleteFeed because unit_test =" + unit_test );
           	} else {
	            logger.warn("Unable to read response  " );
	            logger.error(e.getMessage(), e);
	            try {
		            err.setCode( uc.getResponseCode());
		            err.setMessage(uc.getResponseMessage());
	            } catch (Exception e2) {
	            	err.setCode( 500 );
	            	err.setMessage("Unable to determine response message");
	            	logger.error(e2.getMessage(), e2);
	            }
           	}
        } 		finally {
			try {
				if(uc != null) {
				    uc.disconnect();
				}
			} catch ( Exception e ) {
				logger.error(e.getMessage(), e);
			}
		}
		return responseBody;
	}
	
	public String doDeleteDr_Sub(DR_Sub delSub, ApiError err) {
		logger.info( "entry: doDeleteDr_Sub() "  );
		byte[] postData = delSub.getBytes(provApi);
		logger.info( "post fields=" + Arrays.toString(postData));
		String responsemessage = null;
		String responseBody = null;

		try {
	
			uc.setRequestMethod("DELETE");
		
			uc.setRequestProperty("Content-Type", subContentType);
			uc.setRequestProperty( "charset", "utf-8");
			uc.setRequestProperty( behalfHeader, "DGL" );
			uc.setUseCaches(false);
			uc.setDoOutput(true);
			OutputStream os = null;
			int rc = -1;
			
			try {
                 uc.connect();
                 os = uc.getOutputStream();
                 //os.write( postData );

            } catch (ProtocolException pe) {
                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
			rc = uc.getResponseCode();
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
        	err.setCode(rc);
            if (rc == 204 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );
            } else {
            	err.setMessage(responsemessage);
            }
            
		} catch (ConnectException ce) {
        	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doDeleteDr_Sub because unit_test =" + unit_test );
           	} else {
	            errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage() );
	            err.setCode( 500 );
	        	err.setMessage("Backend connection refused");
           	}
		} catch (Exception e) {
         	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doDeleteDr_Sub because unit_test =" + unit_test );
           	} else {
	            logger.error("Unable to read response  ", e.getMessage());
           	}
        } finally {
        	if(uc != null){
        	    uc.disconnect();
        	}
        }
		return responseBody;

	}
	
	// add double-quotes around a value
	// hope his is easier to read than in-line escaping...
	private String dq( String v ) {
		return ( "\"" + v + "\"");
	}
	private String dq( String k, String v) {
		return( dq(k) + ":" + dq(v));
	}
	private String dqc( String k, String v) {
		return( dq(k) + ":" + dq(v) + ",");
	}
	
	private String dumpSimulation() {
		logger.info( "enter dumpSimulation()");
		String     				responseBody = 
				"{"
				+ dq("feeds") + ":["
				+ "{" + dq( "suspend") + ":false,"
					  + dq( "groupid") + ":0,"
					  + dqc( "description", "Some description" )
					  + dqc( "version", "m1.1") 
					  + dq( "authorization") + ":"
					  + "{" + dq( "endpoint_addrs" ) + ":[],"
					  		+ dq( "classification", "unclassified")
					  		+ dq( "endpoint_ids") + ":[{"
					  			+ dqc( "password", "dradmin" )
					  			+ dq( "id", "dradmin")
					  			+ "}]}"
					  	+ dq( "last_mod") + ":1553738110000,"
					  	+ dq( "deleted") + ":false,"
					  	+ dq( "feedid") + ":1,"
					  	+ dqc( "name", "Default PM Feed")
					  	+ dq( "business_description") + ":\"\","
					  	+ dqc( "publisher", "onap")
					  	+ dq( "links") + ":{"
					  		+ dqc( "subscribe", "https://dmaap-dr-prov/subscribe/1")
					  		+ dqc( "log", "https://dmaap-dr-prov/feedlog/1")
					  		+ dqc( "publish", "https://dmaap-dr-prov/publish/1")
					  		+ dq( "self", "https:/dmaap-dr-prov/feed/1")
					  		+ "}"
					  	+ dq( "created_date") + ":1553738110000 }"
			  	+ "],"
			  	+ dq( "groups") + ":["
			  	+ "],"
			  	+ dq( "subscriptions") + ":["
			  	+ "],"
			  	+ dq( "ingress") + ":["
			  	+ "],"
			  	+ dq( "egress") + ":{"
			  	+ "},"
			  	+ dq( "routing") + ":["
			  	+ "],"
			  + "}";
		return responseBody;
	}
	
	public String doGetDump( ApiError err ) {
		logger.info( "entry: doGetDump() "  );

		String responsemessage = null;
		String responseBody = null;

		try {
	
			uc.setRequestMethod("GET");
			int rc = -1;
			

			try {
                uc.connect();
	

            } catch (ProtocolException pe) {

                 // Rcvd error instead of 100-Continue
                 try {
                     // work around glitch in Java 1.7.0.21 and likely others
                     // without this, Java will connect multiple times to the server to run the same request
                     uc.setDoOutput(false);
                 } catch (Exception e) {
                	 logger.error(e.getMessage(), e);
                 }
			} catch (Exception e) {
				logger.info( "Exception: " + e.getMessage() );
				e.printStackTrace();
			}
	
			rc = uc.getResponseCode();
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
	
        	err.setCode(rc);  // may not really be an error, but we save rc
            if (rc == 200 ) {
     			responseBody = bodyToString( uc.getInputStream() );
    			logger.info( "responseBody=" + responseBody );
            } else {
            	err.setMessage(responsemessage);
            }
            

		} catch (ConnectException ce) {
        	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doGetNodes because unit_test =" + unit_test );
    				responseBody = dumpSimulation();
    							  
           	} else {
	            errorLogger.error( DmaapbcLogMessageEnum.HTTP_CONNECTION_EXCEPTION, provURL, ce.getMessage() );
	            err.setCode( 500 );
	        	err.setMessage("Backend connection refused");
	        	logger.error(ce.getMessage(), ce);
           	}
		} catch (Exception e) {
         	if ( unit_test.equals( "Yes" ) ) {
    				err.setCode(200);
    				err.setMessage( "simulated response");
    				logger.info( "artificial 200 response from doGetNodes because unit_test =" + unit_test );
    				responseBody = dumpSimulation();
    							  
           	} else {
	            logger.error("Unable to read response  ", e.getMessage());
           	}
        } finally {

			if ( uc != null ) uc.disconnect();
        }

		return responseBody;

	}
		
}
