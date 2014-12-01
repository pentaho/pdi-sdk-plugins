/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
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
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.www.AddJobServlet;


public class AddJobSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length != 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password path_to_job" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster d:\\work\\dummy-job.kjb " );
      System.exit( 1 );
    }
    // building target url
    String realHostname = args[0];
    String port = args[1];
    String serviceAndArguments = AddJobServlet.CONTEXT_PATH + "/?xml=Y";
    String urlString = "http://" + realHostname + ":" + port + serviceAndArguments;

    //building auth token
    String plainAuth = args[2] + ":" + args[3];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    //adding binary to servlet
    addJobToServlet( urlString, buildJobConfig( args[4] ), auth );
  }

  public static String buildJobConfig( String jobname ) throws Exception {
    String xml = null;
    KettleEnvironment.init();
    JobMeta jm = new JobMeta( jobname, null );
    JobConfiguration jc = new JobConfiguration( jm, new JobExecutionConfiguration() );
    xml = jc.getXML();
    return xml;
  }

  public static void addJobToServlet( String urlString, String xml, String authentication ) throws Exception {
    PostMethod method = new PostMethod( urlString );
    method.setRequestEntity( new ByteArrayRequestEntity( xml.getBytes( "UTF-8" ) ) );
    method.setDoAuthentication( true );
    method.addRequestHeader( new Header( "Content-Type", "text/xml;charset=UTF-8" ) );
    //adding authorization token
    if ( authentication != null ) {
      method.addRequestHeader( new Header( "Authorization", authentication ) );
    }

    //executing method
    HttpClient client = new HttpClient(  );
    int code = client.executeMethod( method );
    String response = method.getResponseBodyAsString();
    method.releaseConnection();
    if ( code >= 400 ) {
      System.out.println( "Error occurred during job submission." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }
}