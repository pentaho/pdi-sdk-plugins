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

package org.pentaho.di.sdk.samples.databases.demo;

import junit.framework.TestCase;

public class DemoDataBaseMetaTest extends TestCase {

	/**
	 * Test to see that the class is in the class path and returns the correct driver information
	 */
	public void testDriverClass(){
		
		DemoDatabaseMeta dbMeta = new DemoDatabaseMeta();
		assertEquals("org.relique.jdbc.csv.CsvDriver", dbMeta.getDriverClass());
		
	}
	
}
