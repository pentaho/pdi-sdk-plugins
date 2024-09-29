/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.di.sdk.samples.carte;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.www.NextSequenceValueServlet;

public class NextSequenceSample extends AbstractSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password job_name" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster my_sequense" );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String urlString = getUrlString( args[ 0 ], args[ 1 ], args[ 4 ] );

    //building auth token
    String auth = getAuthString( args[ 2 ], args[ 3 ] );

    //adding binary to servlet
    String response = sendNextSequenceRequest( urlString, auth );
    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String sendNextSequenceRequest( String urlString, String authentication ) throws Exception {
    HttpGet method = new HttpGet( urlString );
    HttpClientContext context = HttpClientUtil.createPreemptiveBasicAuthentication( host, port, user, password );
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
      System.out.println( "Error occurred during sequense allocation. "
        + "Most probably you don't have the sequence configured for your Carte server." );
      return null;
    }
    return response;
  }

  public static String getUrlString( String realHostname, String port, String sequenceName ) {
    String url = "http://" + realHostname + ":" + port + NextSequenceValueServlet.CONTEXT_PATH
      + "?name=" + sequenceName;
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
