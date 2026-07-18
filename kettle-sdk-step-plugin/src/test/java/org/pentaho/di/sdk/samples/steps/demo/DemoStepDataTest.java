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



package org.pentaho.di.sdk.samples.steps.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DemoStepDataTest {

  @Test
  public void testDefaults() {
    DemoStepData data = new DemoStepData();
    assertNull( data.outputRowMeta );
    assertEquals( -1, data.outputFieldIndex );
  }
}
