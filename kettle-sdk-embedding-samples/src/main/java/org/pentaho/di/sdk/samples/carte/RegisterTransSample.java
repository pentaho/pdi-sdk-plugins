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
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.trans.TransConfiguration;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.RegisterTransServlet;

public class RegisterTransSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length != 5 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password path_to_trans" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster d:\\work\\dummy-trans.ktr " );
      System.exit( 1 );
    }
    // building target url
    String urlString = getUrlString( args[0], args[1] );

    //building auth token
    String auth = getAuthString( args[2], args[3] );

    //adding binary to servlet
    String response = addTransToServlet( urlString, buildTransConfig( args[4] ), auth );
    if ( response != null ) {
      System.out.println( "Server response:" );
      System.out.println( response );
    }
  }

  public static String buildTransConfig( String transname ) throws Exception {
    String xml = null;
    KettleEnvironment.init();
    TransMeta tm = new TransMeta( transname );
    TransConfiguration tc = new TransConfiguration( tm, new TransExecutionConfiguration() );
    xml = tc.getXML();
    return xml;
  }

  public static String addTransToServlet( String urlString, String xml, String authentication ) throws Exception {
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
      System.out.println( "Error occurred during transformation  submission." );
      return null;
    }
    return response;
  }

  public static String getUrlString( String realHostname, String port ) {
    String url = "http://" + realHostname + ":" + port + RegisterTransServlet.CONTEXT_PATH + "?xml=Y";
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
