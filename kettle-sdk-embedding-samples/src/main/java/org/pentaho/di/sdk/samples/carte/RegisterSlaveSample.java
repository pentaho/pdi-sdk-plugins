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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.www.RegisterSlaveServlet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterSlaveSample extends AbstractSample {
  public static void main( String[] args ) throws Exception {
    if ( args.length < 9 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password server_name host port username password" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster my-localhost localhost 9100 cluster cluster" );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String urlString = getUrlString( args[ 0 ], args[ 1 ] );

    //building auth token
    String auth = getAuthString( args[ 2 ], args[ 3 ] );

    String xml = generateSlaveDetectionXML( args[ 4 ], args[ 5 ], args[ 6 ], args[ 7 ], args[ 8 ] );
    String response = sendRegisterSlaveRequest( urlString, auth, xml );
    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String sendRegisterSlaveRequest( String urlString, String authentication, String xml )
    throws Exception {
    HttpPost method = new HttpPost( urlString );
    method.setEntity( new ByteArrayEntity( xml.getBytes( "UTF-8" ) ) );
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
      System.out.println( "Error occurred during registering slave." );
      return null;
    }
    return response;
  }

  public static String generateSlaveDetectionXML( String name, String host, String port,
                                                  String userName, String pass ) {
    String lastActiveDate = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss.SSS" ).format( new Date() );
    String xml = "<SlaveServerDetection>"
      + "  <slaveserver>"
      + "    <name>" + StringEscapeUtils.escapeXml( name ) + "</name>"
      + "    <hostname>" + host + "</hostname>"
      + "    <port>" + port + "</port>"
      + "    <webAppName/>"
      + "    <username>" + userName + "</username>"
      + "    <password>" + pass + "</password>"
      + "    <proxy_hostname/>"
      + "    <proxy_port/>"
      + "    <non_proxy_hosts/>"
      + "    <master>N</master>"
      + "  </slaveserver>"
      + "  <active>Y</active>"
      + "  <last_active_date>" + StringEscapeUtils.escapeXml( lastActiveDate ) + "</last_active_date>"
      + "  <last_inactive_date/>"
      + "</SlaveServerDetection>";
    return xml;
  }

  public static String getUrlString( String realHostname, String port ) {
    String url = "http://" + realHostname + ":" + port + RegisterSlaveServlet.CONTEXT_PATH + "/";
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
