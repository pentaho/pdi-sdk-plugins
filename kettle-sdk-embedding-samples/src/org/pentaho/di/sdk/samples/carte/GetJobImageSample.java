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

import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.pentaho.di.core.Const;
import org.pentaho.di.www.GetJobImageServlet;


public class GetJobImageSample {

  public static void main( String[] args ) throws Exception {
    if ( args.length < 6 ) {
      System.out.println( " You must specify the following parameters Carte_host Carte_port "
          + "Carte_login Carte_password job_name full_ouput_filepath" );
      System.out.println( " For example 127.0.0.1 8088 cluster cluster dummy_job d:\\1.png" );
      System.exit( 1 );
    }
    // building target url
    String realHostname = args[0];
    String port = args[1];

    String urlString = "http://" + realHostname + ":" + port + GetJobImageServlet.CONTEXT_PATH + "?name=" + args[4];
    urlString = Const.replace( urlString, " ", "%20" );

    //building auth token
    String plainAuth = args[2] + ":" + args[3];
    String auth = "Basic " + Base64.encodeBase64String( plainAuth.getBytes() );

    //adding binary to servlet
    sendGetImageRequest( urlString, auth, args[5] );
  }

  public static void sendGetImageRequest( String urlString, String authentication, String fileName ) throws Exception {
    GetMethod method = new GetMethod( urlString );
    method.setDoAuthentication( true );
    //adding authorization token
    if ( authentication != null ) {
      method.addRequestHeader( new Header( "Authorization", authentication ) );
    }

    //executing method
    HttpClient client = new HttpClient(  );
    int code = client.executeMethod( method );
    byte[] response = method.getResponseBody();
    method.releaseConnection();
    if ( code >= 400 ) {
      System.out.println( "Error occurred during getting job image." );
    }
    System.out.println( "Image was stored to " + fileName );
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream( fileName );
      fos.write( response );
      fos.flush();
    } finally {
      fos.close();
    }
  }
}