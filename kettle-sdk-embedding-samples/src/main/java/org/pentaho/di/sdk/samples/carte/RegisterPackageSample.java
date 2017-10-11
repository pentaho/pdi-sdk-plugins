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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.HttpClientUtil;
import org.pentaho.di.www.RegisterPackageServlet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterPackageSample extends AbstractSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length != 7 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
        + "Carte_login Carte_password Trans_or_job Trans_or_job_name path_to_zip" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster trans dummy-trans.ktr d:\\work\\export.zip " );
      System.exit( 1 );
    }
    init( args[ 0 ], Integer.parseInt( args[ 1 ] ), args[ 2 ], args[ 3 ] );
    // building target url
    String urlString = getUrlString( args[ 0 ], args[ 1 ], args[ 4 ], args[ 5 ] );

    //building auth token
    String auth = getAuthString( args[ 2 ], args[ 3 ] );

    // Open input stream to package
    FileInputStream is = new FileInputStream( args[ 6 ] );

    //adding packing to servlet
    addPackageToServlet( urlString, is, auth );

    // Close input stream
    is.close();
  }

  public static void addPackageToServlet( String urlString, InputStream is, String authentication ) throws Exception {
    HttpPost method = new HttpPost( urlString );
    method.setEntity( new InputStreamEntity( is ) );
    HttpClientContext context = HttpClientUtil.createPreemptiveBasicAuthentication( host, port, user, password );
    method.addHeader( new BasicHeader( "Content-Type", "binary/zip" ) );
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
      System.out.println( "Error occurred during export submission." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }

  public static String getUrlString( String realHostname, String port, String type, String load )
    throws UnsupportedEncodingException {
    String url = "http://" + realHostname + ":" + port + RegisterPackageServlet.CONTEXT_PATH
      + "/?type=" + type + "&load=" + URLEncoder.encode( load, "UTF-8" );
    return Const.replace( url, " ", "%20" );
  }

  public static String getAuthString( String user, String pass ) {
    String plainAuth = user + ":" + pass;
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );
    return auth;
  }
}
