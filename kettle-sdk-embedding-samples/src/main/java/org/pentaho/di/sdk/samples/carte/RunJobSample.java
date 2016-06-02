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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.di.core.Const;
import org.pentaho.di.www.RunJobServlet;

public class RunJobSample {
  public static void main( String[] args ) throws Exception {
    if ( args.length < 6 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password full_job_path log_level" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster home/admin/dummy_job Debug" );
      System.exit( 1 );
    }
    // building target url
    String urlString = getUrlString( args[0], args[1], args[4], args[5] );

    //building auth token
    String auth = getAuthString( args[2], args[3] );

    String response = sendRunJobRequest( urlString, auth );
    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String sendRunJobRequest( String urlString, String authentication ) throws Exception {
    GetMethod method = new GetMethod( urlString );
    method.setDoAuthentication( true );
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
      System.out.println( "Error occurred during stopping transformation." );
      return null;
    }
    return response;
  }

  public static String getUrlString( String realHostname, String port, String jobPath,
      String level ) throws UnsupportedEncodingException {
    String url = "http://" + realHostname + ":" + port + RunJobServlet.CONTEXT_PATH
      + "?job=" + URLEncoder.encode( jobPath, "UTF-8" ) + "&level=" + level;
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
