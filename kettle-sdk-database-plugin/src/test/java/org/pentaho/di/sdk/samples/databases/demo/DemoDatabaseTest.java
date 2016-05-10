/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObject;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;

public class DemoDatabaseTest {

  @BeforeClass
  public static void setUpOnce() throws KettleException {
    // Register custom DatabaseMeta class
    DatabasePluginType dbPluginType = (DatabasePluginType) PluginRegistry.getInstance().getPluginType( DatabasePluginType.class );
    dbPluginType.registerCustom( DemoDatabaseMeta.class, null, "CSVJDBC", "CsvJdbc", null, null );

    KettleClientEnvironment.init();
  }

  @Test
  public void testReadDataIT() throws KettleDatabaseException, SQLException {
    DemoDatabaseMeta demoMeta = new DemoDatabaseMeta();
    demoMeta.setPluginId( "CSVJDBC" );
    DatabaseMeta dbMeta = new DatabaseMeta();
    dbMeta.setDatabaseInterface( demoMeta );
    dbMeta.addExtraOption( "CSVJDBC", "separator", ";" );
    dbMeta.addExtraOption( "CSVJDBC", "columnTypes.small_dataset", "Integer,String" );
    dbMeta.setDBName( "demo_transform" );

    Database db = new Database( new LoggingObject( this ), dbMeta );
    db.connect();
    ResultSet result = db.openQuery( "SELECT * FROM small_dataset" );
    assertNotNull( result );

    Object[] row = db.getRow( result );
    RowMetaInterface meta = db.getMetaFromRow( row, result.getMetaData() );
    assertNotNull( row );
    assertNotNull( meta );
    assertEquals( 2, meta.size() );
    assertEquals( 1L, row[0] );
    assertEquals( "Steve", row[1] );

    row = db.getRow( result );
    assertNotNull( row );
    assertEquals( 2L, row[0] );
    assertEquals( "Clara", row[1] );
    System.out.println( row[0] );

    row = db.getRow( result );
    assertNotNull( row );
    assertEquals( 3L, row[0] );
    assertEquals( "Megan", row[1] );

    row = db.getRow( result );
    assertNull( row );
    db.disconnect();
  }
}
