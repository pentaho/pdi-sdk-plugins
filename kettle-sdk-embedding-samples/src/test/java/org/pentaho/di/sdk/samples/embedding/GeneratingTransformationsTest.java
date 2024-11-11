/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.sdk.samples.embedding;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.TransMeta;

public class GeneratingTransformationsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
	public void testGeneratedTransform() throws KettleException{
		GeneratingTransformations instance = new GeneratingTransformations();
		TransMeta transMeta = instance.generateTransformation();

		// make sure the transformation object has 3 steps
		assertEquals(transMeta.getName(), "Generated Demo Transformation");
		assertEquals(transMeta.getSteps().size(), 3);
	}
}
