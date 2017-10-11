/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.sdk.samples.carte;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.www.RegisterJobServlet;

public class RegisterJobSample extends AbstractSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length != 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password path_to_job" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster d:\\work\\dummy-job.kjb " );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String urlString = getUrlString( args[ 0 ], args[ 1 ] );

    //building auth token
    String auth = getAuthString( args[ 2 ], args[ 3 ] );

    //adding binary to servlet
    String response = addJobToServlet( urlString, buildJobConfig( args[ 4 ] ), auth );

    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String buildJobConfig( String jobname ) throws Exception {
    String xml = null;
    KettleEnvironment.init();
    JobMeta jm = new JobMeta( jobname, null );
    JobConfiguration jc = new JobConfiguration( jm, new JobExecutionConfiguration() );
    xml = jc.getXML();
    return xml;
  }

  public static String addJobToServlet( String urlString, String xml, String authentication ) throws Exception {
    HttpPost method = new HttpPost( urlString );
    method.setEntity( new ByteArrayEntity( xml.getBytes( "UTF-8" ) ) );
    HttpClientContext context = HttpClientUtil.createPreemptiveBasicAuthentication( host, port, user, password );
    method.addHeader( new BasicHeader( "Content-Type", "text/xml;charset=UTF-8" ) );
    //adding authorization token
    if ( authentication != null ) {
      method.addHeader( new BasicHeader( "Authorization", authentication ) );
    }

    //executing method
    HttpClient client = HttpClientManager.getInstance().createDefaultClient();
    HttpResponse httpResponse = context != null ? client.execute( method, context ) : client.execute( method );
    int code = httpResponse.getStatusLine().getStatusCode();
    String response = HttpClientUtil.responseToString( httpResponse );
    method.releaseConnection();
    if ( code >= HttpStatus.SC_BAD_REQUEST ) {
      System.out.println( "Error occurred during job submission." );
      return null;
    }
    return response;
  }

  public static String getUrlString( String realHostname, String port ) {
    return "http://" + realHostname + ":" + port + RegisterJobServlet.CONTEXT_PATH + "/?xml=Y";
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
