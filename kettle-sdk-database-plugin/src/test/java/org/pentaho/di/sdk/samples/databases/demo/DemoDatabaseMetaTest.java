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


package org.pentaho.di.sdk.samples.databases.demo;

import static org.junit.Assert.assertEquals;
import java.io.File;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.vfs.KettleVFS;

public class DemoDatabaseMetaTest {

  /**
   * Test to see that the class is in the class path and returns the correct driver information
   */
  @Test
  public void testDriverClass(){
    DemoDatabaseMeta dbMeta = new DemoDatabaseMeta();
    assertEquals( "org.relique.jdbc.csv.CsvDriver", dbMeta.getDriverClass() );
  }

  @Test
  public void testGetUrl() throws KettleException, FileSystemException {
    final String prefix = "jdbc:relique:csv:";
    DemoDatabaseMeta dbMeta = new DemoDatabaseMeta();

    // Test working directory
    assertEquals( prefix + System.getProperty( "user.dir" ), dbMeta.getURL( null, null, "" ) );

    // Test local file path
    String url = dbMeta.getURL( null, null, new File( "target" ).getAbsolutePath() );
    assertEquals( prefix + System.getProperty( "user.dir" ) + Const.FILE_SEPARATOR + "target", url );

    // Test VFS
    FileObject vfsFile = KettleVFS.getFileObject( System.getProperty( "user.dir" ) );
    url = dbMeta.getURL( null, null, vfsFile.getURL().toString() );
    assertEquals( prefix + System.getProperty( "user.dir" ), url );
  }
}
