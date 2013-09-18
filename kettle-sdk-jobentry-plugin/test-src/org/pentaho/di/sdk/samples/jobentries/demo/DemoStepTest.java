/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
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

package org.pentaho.di.sdk.samples.jobentries.demo;


import org.pentaho.di.core.Result;

import junit.framework.TestCase;

public class DemoStepTest extends TestCase {

	public void testJobEntry(){
		
		
		JobEntryDemo m = new JobEntryDemo("Test Entry");
		
		Result r = new Result();
		
		// execute with desired outcome: false
		m.setOutcome(false);
		m.execute(r, 0);
		assertFalse(r.getResult());
		
		// execute with desired outcome: true
		m.setOutcome(true);
		m.execute(r, 0);
		assertTrue(r.getResult());
		
		
	}
	
}
