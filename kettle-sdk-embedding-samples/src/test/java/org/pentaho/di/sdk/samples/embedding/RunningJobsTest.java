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
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;

public class RunningJobsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
	public void testRunningTransformations() throws KettleException {
		// Create an instance of this demo class for convenience
		RunningJobs instance = new RunningJobs();

		// run a transformation from the file system
		Job j = instance.runJobFromFileSystem( "etl/parameterized_job.kjb" );

		for ( int i = 0; i < 20; i++ ) {
		  if ( j.getStatus().equals( "Finished" ) ) {
		    break;
		  }
		  try {
        Thread.sleep( 100 );
      } catch ( InterruptedException e ) {
        // Ignore
      }
		}

		// A successfully completed job is in finished state
		assertEquals( "Finished", j.getStatus() );

		// A successfully completed job has no errors
		assertEquals( 0, j.getResult().getNrErrors() );

		// And a true grand result
		assertTrue( j.getResult().getResult() );
	}
}
