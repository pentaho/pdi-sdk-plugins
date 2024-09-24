/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
