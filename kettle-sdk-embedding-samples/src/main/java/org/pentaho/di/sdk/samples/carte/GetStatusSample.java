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
import org.pentaho.di.www.GetStatusServlet;

public class GetStatusSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 4 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster" );
      System.exit( 1 );
    }
    // building target url
    String urlString = getUrlString( args[0], args[1] );

    //building auth token
    String auth = getAuthString( args[2], args[3] );

    String response = sendGetStatusRequest( urlString, auth );
    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String sendGetStatusRequest( String urlString, String authentication ) throws Exception {
    GetMethod method = new GetMethod( urlString );
    method.setDoAuthentication( true );
    //adding authorization token
    if ( authentication != null ) {
      method.addRequestHeader( new Header( "Authorization", authentication ) );
    }

    //executing method
    HttpClient client = new HttpClient(  );
    int code = client.executeMethod( method );
    if ( code >= 400 ) {
      System.out.println( "Error occurred during getting server status." );
      return null;
    }
    String response = method.getResponseBodyAsString();
    method.releaseConnection();
    return response;
  }

  public static String getUrlString( String realHostname, String port ) {
    String urlString = "http://" + realHostname + ":" + port + GetStatusServlet.CONTEXT_PATH
        + "?xml=Y";
    urlString = Const.replace( urlString, " ", "%20" );
    return urlString;
  }

  public static String getAuthString( String username, String password ) {
    String plainAuth = username + ":" + password;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
