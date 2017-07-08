/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
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
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.di.core.Const;
import org.pentaho.di.www.AllocateServerSocketServlet;

public class AllocateServerSocketSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password range_start [host_id cluster_id trans_id source_slave "
          + "source_step source_copy target_slave target_step target_copies]" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster 100 " );
      System.exit( 1 );
    }
    // building target url
    String srcHost = ( args.length > 5 ) ? args[5] : "localhost";
    String clusterId = ( args.length > 6 ) ? args[6] : "my_cluster";
    String transName = ( args.length > 7 ) ? args[7] : "my_trans";
    String sourceSlave = ( args.length > 8 ) ? args[8] : "slave_1";
    String sourceStep = ( args.length > 9 ) ? args[9] : "200";
    String sourceCopy = ( args.length > 10 ) ? args[10] : "1";
    String targetSlave = ( args.length > 11 ) ? args[11] : "slave_2";
    String targetStep = ( args.length > 12 ) ? args[12] : "50";
    String targetCopy = ( args.length > 13 ) ? args[13] : "1";
    String realHostname = args[0];
    String port = args[1];
    String rangeStart = args[4];
    String urlString = getUrlString( realHostname, port, rangeStart, srcHost, clusterId, transName, sourceSlave,
      sourceStep, sourceCopy, targetSlave, targetStep, targetCopy );

    //building auth token
    String auth = getAuthString( args[2], args[3] );

    //adding binary to servlet
    allocateServerSocket( urlString, auth );
  }


  public static void allocateServerSocket( String urlString,  String authentication ) throws Exception {
    GetMethod method = new GetMethod( urlString );
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
      System.out.println( "Error occurred during ports allocation." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }

  public static String getUrlString( String realHostname, String port, String rangeStart, String srcHost,
      String clusterId, String transName, String sourceSlave, String sourceStep, String sourceCopy,
      String targetSlave, String targetStep, String targetCopy ) {
    String url = "http://" + realHostname + ":" + port + AllocateServerSocketServlet.CONTEXT_PATH
      + "/?xml=Y&rangeStart=" + rangeStart + "&host=" + srcHost + "&id=" + clusterId + "&trans="
      + transName + "&sourceSlave=" + sourceSlave + "&sourceStep=" + sourceStep + "&sourceCopy="
      + sourceCopy + "&targetSlave=" + targetSlave + "&targetStep=" + targetStep + "&targetCopy=" + targetCopy;
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
