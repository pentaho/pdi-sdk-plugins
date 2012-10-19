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
