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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.www.Carte;
import org.pentaho.di.www.SlaveServerConfig;

public abstract class BaseCarteServletTest {

  private static SlaveServerConfig carteConfig;
  private static Thread carteThread;
  public static String hostname = "localhost";
  public static String port;

  public static final String CARTE_USERNAME = "testUser";
  public static final String CARTE_PASSWORD = "testPass";

  private static SlaveServerConfig getSlaveServerConfig() {
    port = String.valueOf( findFreePort() );
    SlaveServer server = new SlaveServer( "testCarte", hostname, port, CARTE_USERNAME, CARTE_PASSWORD );
    SlaveServerConfig config = new SlaveServerConfig( server );
    return config;
  }

  /**
   * Returns a free port number on localhost.
   *
   * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
   * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
   *
   * https://gist.github.com/vorburger/3429822
   *
   * @return a free port number on localhost
   * @throws IllegalStateException if unable to find a free port
   */
  private static int findFreePort() {
    ServerSocket socket = null;
    try {
      socket = new ServerSocket( 0 );
      socket.setReuseAddress( true );
      int port = socket.getLocalPort();
      try {
        socket.close();
      } catch ( IOException e ) {
        // Ignore IOException on close()
      }
      return port;
    } catch ( IOException e ) {
    } finally {
      if ( socket != null ) {
        try {
          socket.close();
        } catch ( IOException e ) {
        }
      }
    }
    throw new IllegalStateException( "Could not find a free TCP/IP port to start embedded Jetty HTTP Server on" );
  }

  public static boolean serverReady(String host, String port) {
    boolean result = false;
    Socket s = null;
    try {
      s = new Socket(host, Integer.valueOf( port ) );
      result = true;
    } catch (Exception e) {
      result = false;
    } finally {
      if( s != null ) {
        try {
          s.close();
        } catch ( Exception e ) {
          // Ignore
        }
      }
    }
    return result;
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    carteConfig = getSlaveServerConfig();
    carteThread = new Thread() {
      @Override
      public void run() {
        try {
          Carte.runCarte( carteConfig );
        } catch ( Exception e ) {
          System.out.println( e );
          System.exit( 0 );
        }
      }
    };
    carteThread.start();
    System.out.println( "Started local Carte server on port " + port );

    // Allow up to 2 seconds for Carte to become available
    for ( int i = 0; i < 20; i++ ) {
      if ( serverReady( hostname, port ) ) {
        break;
      }
      Thread.sleep( 100 );
    }
  }

  @SuppressWarnings( "deprecation" )
  @AfterClass
  public static void tearDownAfterClass() {
    try {
      carteThread.stop();
    } catch ( Exception ignore ) {
      // Ignore, just shutting down Carte
    }
  }
}
