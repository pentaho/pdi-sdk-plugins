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

/**
 * Created by Yury_Bakhmutski on 6/27/2017.
 */
public abstract class AbstractSample {
  protected static String host;
  protected static int port;
  protected static String user;
  protected static String password;

  protected static void init( String host, int port, String user, String password ) {
    AbstractSample.host = host;
    AbstractSample.port = port;
    AbstractSample.user = user;
    AbstractSample.password = password;
  }
}
