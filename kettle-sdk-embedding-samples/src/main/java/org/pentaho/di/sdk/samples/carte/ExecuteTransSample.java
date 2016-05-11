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
import org.pentaho.di.www.ExecuteTransServlet;

public class ExecuteTransSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 8 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password di_repository_id di_repository_username di_repository_password "
          + "full_trans_path [log_level]" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster di user password home/admin/my_trans " );
      System.exit( 1 );
    }
    // building target url
    String realHostname = args[0];
    String port = args[1];
    String logLevel = ( args.length > 8 ) ? args[8] : "Debug";
    String serviceAndArguments = "/?rep=" + args[4] + "&user=" + args[5] + "&pass="
        + args[6] + "&trans=" + args[7] + "&level=" + logLevel;

    serviceAndArguments = Const.replace( serviceAndArguments, " ", "%20" );
    serviceAndArguments = Const.replace( serviceAndArguments, "/", "%2F" );

    String urlString = "http://" + realHostname + ":" + port + ExecuteTransServlet.CONTEXT_PATH + serviceAndArguments;

    //building auth token
    String plainAuth = args[2] + ":" + args[3];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    //adding binary to servlet
    sendExecuteRequest( urlString, auth );
  }

  public static void sendExecuteRequest( String urlString, String authentication ) throws Exception {
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
      System.out.println( "Error occurred during transformation execution." );
    }
    System.out.println( "Server response (expected to be empty):" );
    System.out.println( response );
  }
}
