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

package org.pentaho.di.sdk.samples.embedding;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

public class RunningTransformationsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    KettleEnvironment.init( false );
  }

  @AfterClass
  public static void tearDownAfterClass() throws KettleException {
    KettleEnvironment.shutdown();
  }

  @Test
  public void testRunningTransformations() throws KettleException {
    for ( final String transPath : RunningTransformations.SAMPLE_TRANSFORMATIONS ) {
      testRunningTransformationsImpl( transPath );
    }
  }

  private void testRunningTransformationsImpl( final String transPath ) throws KettleException {
    // Create an instance of this demo class for convenience
    RunningTransformations instance = new RunningTransformations();

    // run a transformation from the file system
    Trans t = instance.runTransformationFromFileSystem( transPath );

    // A successfully completed transformation is in waiting state
    assertEquals( "Finished", t.getStatus() );

    // A successfully completed transformation has no errors
    assertEquals( 0, t.getResult().getNrErrors() );
  }

  @Test
  public void testGetTramsName() throws Exception {

    final Class clazz = RunningTransformations.class;
    final String methodName = "getTransName";

    Assert.assertEquals( "", Whitebox.invokeMethod( clazz, methodName, null ) );
    Assert.assertEquals( "", Whitebox.invokeMethod( clazz, methodName, "" ) );
    Assert.assertEquals( "", Whitebox.invokeMethod( clazz, methodName, " " ) );

    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "name" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "name.ktr" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "foo/name" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "/foo/name" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "/foo/name.ktr" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "c:\\foo\\name" ) );
    Assert.assertEquals( "name", Whitebox.invokeMethod( clazz, methodName, "c:\\foo\\name.ktr" ) );
  }
}
