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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.pentaho.di.core.Const;
import org.pentaho.di.www.AddExportServlet;


public class AddExportSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length != 7 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password Trans_or_job Trans_or_job_name path_to_zip" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster trans dummy-trans.ktr d:\\work\\export.zip " );
      System.exit( 1 );
    }
    // building target url
    String realHostname = args[0];
    String port = args[1];

    String serviceAndArguments = AddExportServlet.CONTEXT_PATH
        + "/?" + AddExportServlet.PARAMETER_TYPE + "=" + args[4] + "&" + AddExportServlet.PARAMETER_LOAD + "="
          + URLEncoder.encode( args[5], "UTF-8" );

    String urlString = "http://" + realHostname + ":" + port + serviceAndArguments;
    urlString = Const.replace( urlString, " ", "%20" );
    FileInputStream is = new FileInputStream(  args[6] );

    //building auth token
    String plainAuth = args[2] + ":" + args[3];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    //adding binary to servlet
    addExportToServlet( urlString, is, auth );

    is.close();
  }

  public static void addExportToServlet( String urlString, InputStream is, String authentication ) throws Exception {
    PostMethod method = new PostMethod( urlString );
    method.setRequestEntity( new InputStreamRequestEntity( is ) );
    method.setDoAuthentication( true );
    method.addRequestHeader( new Header( "Content-Type", "binary/zip" ) );
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
      System.out.println( "Error occurred during export submission." );
    }
    System.out.println( "Server response:" );
    System.out.println( response );
  }
}