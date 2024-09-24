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
import org.pentaho.di.job.JobMeta;

public class GeneratingJobsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
	public void testGeneratedJob() throws KettleException{
		GeneratingJobs instance = new GeneratingJobs();
		JobMeta jobMeta = instance.generateJob();

		// make sure the transformation object has 3 steps
		assertEquals(jobMeta.getName(), "Generated Demo Job");
		assertEquals(jobMeta.getJobCopies().size(), 4);
	}
}
