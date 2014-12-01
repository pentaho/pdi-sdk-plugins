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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.www.RegisterSlaveServlet;


public class RegisterSlaveSample {
  public static void main( String[] args ) throws Exception {
    if ( args.length < 9 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password server_name host port username password" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster my-localhost localhost 9100 cluster cluster" );
      System.exit( 1 );
    }
    // building target url
    String realHostname = args[0];
    String port = args[1];
    String urlString = "http://" + realHostname + ":" + port + RegisterSlaveServlet.CONTEXT_PATH + "/";
    urlString = Const.replace( urlString, " ", "%20" );

    //building auth token
    String plainAuth = args[2] + ":" + args[3];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    String xml = generateSlaveDetectionXML( args[4], args[5], args[6], args[7], args[8] );
    sendRegisterSlaveRequest( urlString, auth, xml );
  }

  public static void sendRegisterSlaveRequest( String urlString, String authentication, String xml ) throws Exception {
    PostMethod method = new PostMethod( urlString );
    method.setRequestEntity( new ByteArrayRequestEntity( xml.getBytes( "UTF-8" ) ) );
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
      System.out.println( "Error occurred during registering slave." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }

  public static String generateSlaveDetectionXML( String name, String host, String port,
    String userName, String pass ) {
    String xml = "<SlaveServerDetection>"
        + " <slaveserver><name>" + StringEscapeUtils.escapeXml( name ) + "</name><hostname>"
        + host + "</hostname><port>" + port + "</port>"
        + "<webAppName/><username>" + userName + "</username><password>" + pass + "</password><proxy_hostname/>"
        + "<proxy_port/><non_proxy_hosts/><master>N</master></slaveserver><active>Y</active>"
        + "<last_active_date>" + StringEscapeUtils.escapeXml( new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss.SSS" )
          .format( new Date() ) ) + "</last_active_date><last_inactive_date/>"
        + "</SlaveServerDetection>";

    return xml;
  }
}