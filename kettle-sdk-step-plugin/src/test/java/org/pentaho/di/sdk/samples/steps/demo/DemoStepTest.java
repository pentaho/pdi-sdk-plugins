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

package org.pentaho.di.sdk.samples.steps.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestFactory;

public class DemoStepTest {

  static final String STEP_NAME = "Test Demo Step";

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  // If the step does not receive any rows, the transformation should still run successfully
  @Test
  public void testNoInput() throws KettleException {
    DemoStepMeta meta = new DemoStepMeta();
    meta.setOutputField( "aFieldName" );
    TransMeta tm = TransTestFactory.generateTestTransformation( new Variables(), meta, STEP_NAME );

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( tm, TransTestFactory.INJECTOR_STEPNAME,
      STEP_NAME, TransTestFactory.DUMMY_STEPNAME, new ArrayList<RowMetaAndData>() );

    assertNotNull( result );
    assertEquals( 0, result.size() );
  }

  // If the step receives rows without any fields, there should be a single output field on each row
  @Test
  public void testInputNoFields() throws KettleException {
    DemoStepMeta meta = new DemoStepMeta();
    meta.setOutputField( "aFieldName" );
    TransMeta tm = TransTestFactory.generateTestTransformation( new Variables(), meta, STEP_NAME );

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( tm, TransTestFactory.INJECTOR_STEPNAME,
      STEP_NAME, TransTestFactory.DUMMY_STEPNAME, generateInputData( 50001, false ) );

    assertNotNull( result );
    assertEquals( 50001, result.size() );
    for ( int i = 0; i < 50001; i++ ) {
      assertEquals( 1, result.get( i ).size() );
      assertEquals( "Hello World!", result.get( i ).getString( 0, "default value" ) );
    }
  }

  // If the step receives rows with existing fields, there should be a new field at the end of each output row
  @Test
  public void testInput() throws KettleException {
    DemoStepMeta meta = new DemoStepMeta();
    meta.setOutputField( "aFieldName" );
    TransMeta tm = TransTestFactory.generateTestTransformation( new Variables(), meta, STEP_NAME );

    List<RowMetaAndData> result = TransTestFactory.executeTestTransformation( tm, TransTestFactory.INJECTOR_STEPNAME,
      STEP_NAME, TransTestFactory.DUMMY_STEPNAME, generateInputData( 5, true ) );

    assertNotNull( result );
    assertEquals( 5, result.size() );
    for ( int i = 0; i < 5; i++ ) {
      assertEquals( 2, result.get( i ).size() );
      assertEquals( "UUID", result.get( i ).getValueMeta( 0 ).getName() );
      try {
        UUID.fromString( result.get( i ).getString(0, "default value" ) );
      } catch ( IllegalArgumentException iae ) {
        fail(); // UUID field value was modified unexpectedly
      }
      assertEquals( "Hello World!", result.get( i ).getString( 1, "default value" ) );
    }
  }

  /**
   * 
   * @param rowCount  The number of rows that should be returned
   * @param hasFields Whether a "UUID" field should be added to each row
   * @return          A RowMetaAndData object that can be used for input data in a test transformation
   */
  public static List<RowMetaAndData> generateInputData( int rowCount, boolean hasFields ) {
    List<RowMetaAndData> retval = new ArrayList<RowMetaAndData>();
    RowMetaInterface rowMeta = new RowMeta();
    if ( hasFields ) {
      rowMeta.addValueMeta( new ValueMetaString( "UUID" ) );
	}

    for ( int i = 0; i < rowCount; i++ ) {
      Object[] data = new Object[0];
      if ( hasFields ) {
        data = new Object[] { UUID.randomUUID().toString() };
      }
      retval.add( new RowMetaAndData( rowMeta, data ) );
    }
    return retval;
  }
}
