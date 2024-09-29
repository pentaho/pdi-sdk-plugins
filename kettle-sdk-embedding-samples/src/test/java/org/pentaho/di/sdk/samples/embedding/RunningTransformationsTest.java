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

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;

public class RunningTransformationsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
	public void testRunningTransformations() throws KettleException {
		// Create an instance of this demo class for convenience
		RunningTransformations instance = new RunningTransformations();

		// run a transformation from the file system
		Trans t = instance.runTransformationFromFileSystem( "etl/parameterized_transformation.ktr" );

		// A successfully completed transformation is in waiting state
		assertEquals( "Finished", t.getStatus() );

		// A successfully completed transformation has no errors
		assertEquals( 0, t.getResult().getNrErrors() );
	}
}
