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
