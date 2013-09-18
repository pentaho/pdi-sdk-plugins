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

package pentaho.di.sdk.samples.partitioners.demo;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMeta;

import junit.framework.TestCase;

public class DemoDataBaseMetaTest extends TestCase {

	
	public void testPartitionerAlgorithm() throws KettleException{
		
		// create and configure partitioner
		DemoPartitioner p = new DemoPartitioner();
		p.setNrPartitions(3);
		p.setFieldName("testfield");
		
		// create row definition
		RowMeta rowMeta = new RowMeta();
		rowMeta.addValueMeta(new ValueMeta("testfield", ValueMeta.TYPE_STRING));
		
		// test short string -> partition 0 
		Object[] row1 = {"a"};
		assertEquals(p.getPartition(rowMeta, row1),0);
		
		// test medium string -> partition 1
		Object[] row2 = {"abcdefghijk"};
		assertEquals(p.getPartition(rowMeta, row2),1);

		// test long string -> partition 2
		Object[] row3 = {"abcdefghijklmnopqrstuvwxyz"};
		assertEquals(p.getPartition(rowMeta, row3),2);

	}
	
}
