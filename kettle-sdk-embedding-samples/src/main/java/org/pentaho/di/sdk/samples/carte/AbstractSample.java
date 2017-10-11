/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017 by Hitachi Vantara : http://www.pentaho.com
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
