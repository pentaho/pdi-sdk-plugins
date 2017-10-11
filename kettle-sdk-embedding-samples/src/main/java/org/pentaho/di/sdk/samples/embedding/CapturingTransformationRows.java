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

package org.pentaho.di.sdk.samples.embedding;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;
import org.pentaho.di.trans.step.StepInterface;

/**
 * This class demonstrates how to execute a PDI transformation, and capture the
 * output rows of a certain step.
 */
public class CapturingTransformationRows {

  public static CapturingTransformationRows instance;
  public List<Object[]> capturedRows;
  public RowMetaInterface rowStructure;

  /**
   * @param args not used
   */
  public static void main( String[] args ) {

    // Kettle Environment must always be initialized first when using PDI
    // It bootstraps the PDI engine by loading settings, appropriate plugins
    // etc.
    try {
      KettleEnvironment.init();
    } catch ( KettleException e ) {
      e.printStackTrace();
      return;
    }

    // Create an instance of this demo class for convenience
    instance = new CapturingTransformationRows();

    // runs the transformation, returning "output" step's rows
    instance.runTransformation( "etl/capturing_rows.ktr" );
  }

  /**
   * This method executes a transformation defined in a ktr file and captures
   * all rows emitted by the step named "output". The rows captured are
   * printed to stdout.
   * 
   * It demonstrates the following:
   * 
   * - Executing a transformation definition from a ktr file
   * - Capturing rows from a given step in a running transformation
   * 
   * @param filename the file containing the transformation to execute (ktr file)
   * @return the transformation rows captured, or null if there was an error
   */
  public List<Object[]> runTransformation( String filename ) {

    try {
      System.out.println( "***************************************************************************************" );
      System.out.println( "Attempting to run transformation " + filename + " from file system" );
      System.out.println( "***************************************************************************************" );

      // load transformation definition file
      TransMeta transMeta = new TransMeta( filename, (Repository) null );

      // crate a transformation object
      Trans transformation = new Trans( transMeta );

      // set log level to avoid noise on the log
      transformation.setLogLevel( LogLevel.MINIMAL );

      // preparing the executing initializes all steps
      transformation.prepareExecution( new String[0] );

      // find the "output" step
      StepInterface step = transformation.getStepInterface( "output", 0 );

      // attach adapter receiving row events
      RowAdapter rowAdapter = new RowAdapter() {
        private boolean firstRow = true;

        public void rowWrittenEvent( RowMetaInterface rowMeta, Object[] row ) throws KettleStepException {
          if ( firstRow ) {
            firstRow = false;
            // a space to keep the captured rows
            capturedRows = new LinkedList<Object[]>();
            // keep the row structure for future reference
            rowStructure = rowMeta;
            // print a header before the first row
            System.out.println( StringUtils.join( rowMeta.getFieldNames(), "\t" ) );
          }
          try {
            // retrieve first field as integer
            System.out.print( rowMeta.getInteger( row, 0 ) );
            System.out.print( "\t" );
            // retrieve second field as string
            System.out.print( rowMeta.getString( row, 1 ) );
            System.out.print( "\n" );

            // keep the row 
            capturedRows.add( row );
          } catch ( KettleValueException e ) {
            e.printStackTrace();
          }
        }
      };
      step.addRowListener( rowAdapter );

      // after the transformation is prepared for execution it is started by calling startThreads()
      System.out.println( "\nStarting transformation\n" );
      transformation.startThreads();

      // waiting for the transformation to finish
      // The row adapter will receive notification of any rows written by the "output" step
      transformation.waitUntilFinished();

      // retrieve the result object, which captures the success of the
      // transformation
      Result result = transformation.getResult();

      // report on the outcome of the transformation
      String outcome = String.format( "\nTrans %s executed %s\n", filename,
        ( result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors" ) );
      System.out.println( outcome );

      return capturedRows;
    } catch ( Exception e ) {
      // something went wrong, just log and return
      e.printStackTrace();
      return null;
    }
  }
}
