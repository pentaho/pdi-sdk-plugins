/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.steps.addsequence.AddSequenceMeta;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;

public class CapturingTransformationRowsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    StepPluginType.getInstance().handlePluginAnnotation(
      AddSequenceMeta.class,
      AddSequenceMeta.class.getAnnotation( org.pentaho.di.core.annotations.Step.class ),
      emptyList(), false, null );
  }

  @Test
  public void testRows() {
    CapturingTransformationRows.main( new String[ 0 ] );
    List<Object[]> rows = CapturingTransformationRows.instance.capturedRows;

    // make sure some rows were collected
    assertFalse( rows.isEmpty() );
  }
}
