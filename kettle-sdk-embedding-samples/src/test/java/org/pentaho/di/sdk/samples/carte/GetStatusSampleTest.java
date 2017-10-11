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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Node;

public class GetStatusSampleTest extends BaseCarteServletTest {

  @Test
  public void testGetAuthString() {
    assertEquals( "Basic YWRtaW46cGFzc3dvcmQ=", GetStatusSample.getAuthString( "admin", "password" ) );
  }

  @Test
  public void testGetUrlString() {
    assertEquals( "http://sample:1000/kettle/status?xml=Y", GetStatusSample.getUrlString( "sample", "1000" ) );
  }

  @Test
  public void testXMLResponse() throws Exception {
    String url = GetStatusSample.getUrlString( hostname, port );
    String auth = GetStatusSample.getAuthString( CARTE_USERNAME, CARTE_PASSWORD );

    String response = GetStatusSample.sendGetStatusRequest( url, auth );
    assertNotNull( response );
    Node result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "serverstatus" ); 

    assertEquals( "Online", XMLHandler.getTagValue( result, "statusdesc" ) );
    Long memoryFree = Long.valueOf( XMLHandler.getTagValue( result, "memory_free" ) );
    Long memoryTotal = Long.valueOf( XMLHandler.getTagValue( result, "memory_total" ) );
    assertTrue( memoryFree > 0L );
    assertTrue( memoryTotal > 0L );
    assertTrue( memoryFree < memoryTotal );
    assertTrue( Long.valueOf( XMLHandler.getTagValue( result, "cpu_cores" ) ) > 0L );
    assertTrue( Long.valueOf( XMLHandler.getTagValue( result, "cpu_process_time" ) ) > 0L );
    assertTrue( Long.valueOf( XMLHandler.getTagValue( result, "uptime" ) ) > 0L );
    assertTrue( Long.valueOf( XMLHandler.getTagValue( result, "thread_count" ) ) > 0L );
    try {
      Double.valueOf( XMLHandler.getTagValue( result, "load_avg" ) );
    } catch ( NumberFormatException e ) {
      fail();
    }
    assertFalse( Const.isEmpty( XMLHandler.getTagValue( result, "os_name" ) ) );
    assertFalse( Const.isEmpty( XMLHandler.getTagValue( result, "os_version" ) ) );
    assertFalse( Const.isEmpty( XMLHandler.getTagValue( result, "os_arch" ) ) );
    assertTrue( Const.isEmpty( Const.trim( Const.removeCRLF( XMLHandler.getTagValue( result, "transstatuslist" ) ) ) ) );
    assertTrue( Const.isEmpty( Const.trim( Const.removeCRLF( XMLHandler.getTagValue( result, "jobstatuslist" ) ) ) ) );
  }

  @Test
  public void testMain() throws Exception {
    GetStatusSample.main( new String[]{ hostname, port, CARTE_USERNAME, CARTE_PASSWORD } );
  }
}
