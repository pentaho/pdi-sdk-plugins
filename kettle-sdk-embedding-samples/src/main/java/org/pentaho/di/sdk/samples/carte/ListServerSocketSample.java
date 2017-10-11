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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.www.ListServerSocketServlet;

public class ListServerSocketSample extends AbstractSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password host" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster 127.0.0.1" );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String realHostname = args[ 0 ];
    String port = args[ 1 ];

    String urlString = "http://" + realHostname + ":" + port + ListServerSocketServlet.CONTEXT_PATH
      + "?host=" + args[ 4 ];
    urlString = Const.replace( urlString, " ", "%20" );

    //building auth token
    String plainAuth = args[ 2 ] + ":" + args[ 3 ];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    //adding binary to servlet
    sendListServerSocketRequest( urlString, auth );
  }

  public static void sendListServerSocketRequest( String urlString, String authentication ) throws Exception {
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
      System.out.println( "Error occurred during getting allocated sockets." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }
}
