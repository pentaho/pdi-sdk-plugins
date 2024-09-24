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

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.database.BaseDatabaseMeta;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;

/** 
 * This class implements the PDI DatabaseInterface, which is used to add a new 
 * database type to PDI. The DatabaseInterface has numerous methods that help
 * PDI determine the best way of interacting with the database in question, including
 * SQL generation and JDBC-specific peculiarities of the drivers. Implementations
 * are typically based on the BaseDatabaseMeta class, which provides a large portion of
 * reusable default implementation for the interface. 
 * 
 * This class implements the interface for the CsvJdbc driver available on
 * http://csvjdbc.sourceforge.net/
 * 
 * CsvJdbc is a JDBC driver that allows treating a directory like a database and 
 * contained csv files as tables. After the plug-in is installed, you should
 * be able to open and execute the demo_database.ktr transformation that
 * reads from the small_dataset.csv file using CsvJdbc driver.   
 * 
 * This implementation is a minimal example of how to implement a custom database
 * type in PDI. Please review the DatabaseInterface and its javadoc as well as
 * the implementations in the org.pentaho.di.core.database package in PDI sources
 * for more complete examples of database implementations. 
 *
 */

// the annotation allows PDI to recognize this class as a database plug-in 
@DatabaseMetaPlugin(
  type = "CSVJDBC",
  typeDescription = "CsvJdbc"
  )
// The BaseDatabaseMeta class provides common implementations for most DatabaseInterface methods.
// Be sure however to check if the default implementation is a good choice for the database in question.
public class DemoDatabaseMeta extends BaseDatabaseMeta implements DatabaseInterface {

  /**
   * Returns the list of possible access types for a database. 
   * Most common choices are JDBC and JNDI.
   */
  public int[] getAccessTypeList() {
    return new int[] { DatabaseMeta.TYPE_ACCESS_NATIVE, DatabaseMeta.TYPE_ACCESS_JNDI };
  }

  /**
   * No port is required for the CsvJdbc driver 
   */
  public int getDefaultDatabasePort() {
    return -1;
  }

  /**
   * Returns the SQL query to execute when PDI needs to determine the field layout of a table
   */
  public String getSQLQueryFields( String tableName ) {
    return "SELECT * FROM " + tableName + " WHERE 1=0";
  }

  /**
   * Returns the SQL query to execute in order to determine if a table exists. If an exception is
   * thrown in the process, PDI will assume that the table does not exist.
   */
  public String getSQLTableExists( String tablename ) {
    return getSQLQueryFields( tablename );
  }

  /**
   * Returns the SQL query to execute in order to determine if a field in a table exists. If an
   * exception is thrown in the process, PDI will assume that the field does not exist.
   */
  public String getSQLColumnExists( String columnname, String tablename ) {
    return "SELECT " + columnname + " FROM " + tablename + " WHERE 1=0";
  }

  /**
   * Returns the name of the JDBC driver class to use for this type of database 
   */
  public String getDriverClass() {
    return "org.relique.jdbc.csv.CsvDriver";
  }

  /**
   * @param hostname     ignored in this implementation
   * @param port         ignored in this implementation
   * @param databaseName the directory containing CSV files.
   * 
   * @return the connection string based on hostname, port and databasename.
   */
  public String getURL( String hostname, String port, String databaseName ) throws KettleDatabaseException {
    File dbName = new File( databaseName );
    if ( dbName != null && !dbName.exists() ) {
      // CSV-JDBC requires local file paths
      // It may be a VFS path, test and convert to local if it is a VFS path
      FileObject vfsObject;
      try {
        vfsObject = KettleVFS.getFileObject( databaseName );
        if ( vfsObject != null && vfsObject.exists() ) {
          File temp = new File( vfsObject.getURL().getPath() );
          if ( temp.exists() ) {
            dbName = temp;
          }
        }
      } catch ( KettleFileException | FileSystemException e ) {
        throw new KettleDatabaseException( e );
      }
    }
    return "jdbc:relique:csv:" + dbName.getPath();
  }

  /**
   * Indicates that CsvJdbc does not support appending parameters in the connection URL. Instead the connection properties
   * object must be used. The options are editable on the "options" section in PDI's database connection dialog.
   * For available options see: http://csvjdbc.sourceforge.net/
   */
  public boolean supportsOptionsInURL() {
    return false;
  }

  /**
   * Returns an URL showing the supported options in the "options" section of the database connection dialog.
   */
  public String getExtraOptionsHelpText() {
    return "http://csvjdbc.sourceforge.net/";
  }

  /**
   * Returns reserved words for CsvJdbc
   */
  public String[] getReservedWords() {
    return new String[] { "SELECT", "DISTINCT", "AS", "FROM", "WHERE", "NOT", "AND", "OR", "ORDER", "BY", "ASC", "DESC",
      "NULL", "COUNT", "LOWER", "MAX", "MIN", "ROUND", "UPPER", "BETWEEN", "IS", "LIKE" };
  }

  /**
   * Returns the jar files required for the driver to work.
   */
  public String[] getUsedLibraries() {
    return new String[] { "csvjdbc-1.0.29.jar" };
  }

  /**
   * Returns whether a prepared JDBC statement is enough to determine the result field layout.
   */
  public boolean supportsPreparedStatementMetadataRetrieval() {
    return false;
  }

  /**
   * Returns whether the only way to get the field layout of a query, is to actually look at the result set.
   * Some databases provide more efficient ways (looking at a prepared or executed statement for instance)
   */
  public boolean supportsResultSetMetadataRetrievalOnly() {
    return true;
  }

  /**
   * Returns whether the database in question supports release of savepoints.  
   */
  public boolean releaseSavepoint() {
    return false;
  }

  /**
   * Returns whether the database in question supports transactions.
   */
  public boolean supportsTransactions() {
    return false;
  }

  /**
   * This method is used to generate DDL for create table statements etc. in Spoon.  
   * Creating and modifying fields is not supported by the csv driver
   */
  public String getFieldDefinition( ValueMetaInterface v, String tk, String pk, boolean use_autoinc, boolean add_fieldname, boolean add_cr ) {
    return "";
  }

  /**
   * This method is used to generate DDL for create table statements etc. in Spoon.  
   * Adding fields is not supported by the csv driver
   */
  public String getAddColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean use_autoinc, String pk, boolean semicolon ) {
    return "";
  }

  /**
   * This method is used to generate DDL for create table statements etc. in Spoon.  
   * Modifying fields is not supported by the csv driver
   */
  public String getModifyColumnStatement( String tablename, ValueMetaInterface v, String tk, boolean use_autoinc, String pk, boolean semicolon ) {
    return "";
  }
}
