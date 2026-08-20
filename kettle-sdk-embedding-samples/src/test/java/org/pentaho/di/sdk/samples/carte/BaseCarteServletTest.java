/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.di.sdk.samples.carte;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.www.Carte;
import org.pentaho.di.www.SlaveServerConfig;

public abstract class BaseCarteServletTest {

  private static SlaveServerConfig carteConfig;
  private static Thread carteThread;
  private static final AtomicReference<Throwable> carteStartupFailure = new AtomicReference<>();
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

  public static boolean serverReady( String host, String port ) {
    HttpURLConnection connection = null;
    try {
      URL statusUrl = new URL( "http://" + host + ":" + port + "/kettle/status?xml=Y" );
      connection = (HttpURLConnection) statusUrl.openConnection();
      connection.setConnectTimeout( 100 );
      connection.setReadTimeout( 100 );
      int responseCode = connection.getResponseCode();
      return responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
    } catch ( IOException e ) {
      return false;
    } finally {
      if ( connection != null ) {
        connection.disconnect();
      }
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    carteConfig = getSlaveServerConfig();
    SlaveServerConfig config = carteConfig;
    carteStartupFailure.set( null );
    carteThread = new Thread() {
      @Override
      public void run() {
        try {
          Carte.runCarte( config );
        } catch ( Throwable throwable ) {
          carteStartupFailure.compareAndSet( null, throwable );
        }
      }
    };
    carteThread.setDaemon( true );
    carteThread.start();
    System.out.println( "Started local Carte server on port " + port );

    // Wait until the status servlet, rather than only the TCP port, accepts requests.
    for ( int i = 0; i < 100; i++ ) {
      Throwable startupFailure = carteStartupFailure.get();
      if ( startupFailure != null ) {
        throw new IllegalStateException( "Unable to start local Carte server", startupFailure );
      }
      if ( serverReady( hostname, port ) ) {
        return;
      }
      Thread.sleep( 100 );
    }
    throw new IllegalStateException( "Timed out waiting for local Carte server on port " + port );
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
