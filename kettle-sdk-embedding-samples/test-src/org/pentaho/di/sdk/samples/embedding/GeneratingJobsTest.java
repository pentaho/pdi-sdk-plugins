/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
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


import junit.framework.TestCase;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;

public class GeneratingJobsTest extends TestCase {

	public void testGeneratedJob() throws KettleException{

		KettleEnvironment.init();

		GeneratingJobs instance = new GeneratingJobs();
		JobMeta jobMeta = instance.generateJob();
		
		// make sure the transformation object has 3 steps
		assertEquals(jobMeta.getName(), "Generated Demo Job");
		assertEquals(jobMeta.getJobCopies().size(), 4);
		
	}
	
}
