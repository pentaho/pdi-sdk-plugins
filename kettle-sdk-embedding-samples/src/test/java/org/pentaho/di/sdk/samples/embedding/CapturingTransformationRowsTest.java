/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
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
