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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Node;

public class JobServletsTest extends BaseCarteServletTest {

  @Test
  public void testJobServlets() throws Exception {

    // Add Job
    final String jobFile = "etl/parameterized_job.kjb";
    final String jobName = "parameterized_job";
    String xml = RegisterJobSample.buildJobConfig( jobFile );
    assertFalse( Const.isEmpty( xml ) );

    String addJobUrl = RegisterJobSample.getUrlString( hostname,  port );
    String auth = RegisterJobSample.getAuthString( CARTE_USERNAME, CARTE_PASSWORD );

    String response = RegisterJobSample.addJobToServlet( addJobUrl, xml, auth );
    assertNotNull( response );
    Node result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "webresult" );
    assertEquals( "OK", XMLHandler.getTagValue( result, "result" ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).contains( jobName ) );
    String jobUUID = XMLHandler.getTagValue( result, "id" );
    try {
      UUID.fromString( jobUUID );
    } catch ( IllegalArgumentException iae ) {
      fail();
    }

    // Carte Status
    String serverStatusUrl = GetStatusSample.getUrlString( hostname, port );
    response = GetStatusSample.sendGetStatusRequest( serverStatusUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "serverstatus" );
    result = XMLHandler.getSubNode( result, "jobstatuslist" );
    assertEquals( 1, XMLHandler.countNodes( result, "jobstatus" ) );
    result = XMLHandler.getSubNode( result, "jobstatus" );
    assertEquals( jobName, XMLHandler.getTagValue( result, "jobname" ) );
    assertEquals( jobUUID, XMLHandler.getTagValue( result, "id" ) );
    assertEquals( "Waiting", XMLHandler.getTagValue( result, "status_desc" ) );
    assertTrue( Const.isEmpty( XMLHandler.getTagValue( result, "error_desc" ) ) );
    assertEquals( "0", XMLHandler.getTagValue( result, "first_log_line_nr" ) );
    assertEquals( "0", XMLHandler.getTagValue( result, "last_log_line_nr" ) );

    // Job Status
    String jobStatusUrl = GetJobStatusSample.getUrlString( hostname, port, jobName );
    response = GetJobStatusSample.sendGetJobStatusRequest( jobStatusUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "jobstatus" );
    assertEquals( jobName, XMLHandler.getTagValue( result, "jobname" ) );
    assertEquals( jobUUID, XMLHandler.getTagValue( result, "id" ) );
    assertEquals( "Waiting", XMLHandler.getTagValue( result, "status_desc" ) );
    assertTrue( Const.isEmpty( XMLHandler.getTagValue( result, "error_desc" ) ) );
    assertNotNull( XMLHandler.getTagValue( result, "first_log_line_nr" ) );
    assertNotNull( XMLHandler.getTagValue( result, "last_log_line_nr" ) );

    // Job Status for a job that does not exist
    String fakeJobName = UUID.randomUUID().toString();
    assertNotEquals( "The fake job name should not match the real job used for testing", jobName, fakeJobName );
    jobStatusUrl = GetJobStatusSample.getUrlString( hostname, port, fakeJobName );
    response = GetJobStatusSample.sendGetJobStatusRequest( jobStatusUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "webresult" );
    assertEquals( "ERROR", XMLHandler.getTagValue( result, "result" ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).startsWith( "The specified job " ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).contains( fakeJobName ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).endsWith( " could not be found" ) );
    assertNull( XMLHandler.getTagValue( result, "id" ) );

    // Start Job
    String jobStartUrl = StartJobSample.getUrlString( hostname, port, jobName );
    response = StartJobSample.sendStartJobRequest( jobStartUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "webresult" );
    assertEquals( "OK", XMLHandler.getTagValue( result, "result" ) );
    assertEquals( jobUUID, XMLHandler.getTagValue( result, "id" ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).startsWith( "Job " ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).contains( jobName ) );
    assertTrue( XMLHandler.getTagValue( result, "message" ).contains( "was started." ) );

    Thread.sleep( 500 );

    // Job Status
    jobStatusUrl = GetJobStatusSample.getUrlString( hostname, port, jobName );
    response = GetJobStatusSample.sendGetJobStatusRequest( jobStatusUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "jobstatus" );
    assertEquals( jobName, XMLHandler.getTagValue( result, "jobname" ) );
    assertEquals( jobUUID, XMLHandler.getTagValue( result, "id" ) );
    assertEquals( "Finished", XMLHandler.getTagValue( result, "status_desc" ) );

    // Remove Job
    String jobRemoveUrl = RemoveJobSample.getUrlString( hostname, port, jobName );
    response = RemoveJobSample.sendRemoveJobRequest( jobRemoveUrl, auth );
    assertNotNull( response );
    result = XMLHandler.getSubNode( XMLHandler.loadXMLString( response ), "webresult" );
    assertEquals( "OK", XMLHandler.getTagValue( result, "result" ) );
    assertTrue( Const.isEmpty( XMLHandler.getTagValue( result, "message" ) ) );
    assertTrue( Const.isEmpty( XMLHandler.getTagValue( result, "id" ) ) );
  }
}
