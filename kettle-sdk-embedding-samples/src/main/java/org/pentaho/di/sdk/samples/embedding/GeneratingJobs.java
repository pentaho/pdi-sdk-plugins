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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.abort.JobEntryAbort;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.success.JobEntrySuccess;
import org.pentaho.di.job.entries.writetolog.JobEntryWriteToLog;
import org.pentaho.di.job.entry.JobEntryCopy;

/**
 * This class demonstrates how to create a PDI job definition 
 * in code, and save it to a kjb file.
 */
public class GeneratingJobs {

  public static GeneratingJobs instance;

  /**
   * @param args not used
   */
  public static void main( String[] args ) {

    try {
      // Kettle Environment must always be initialized first when using PDI
      // It bootstraps the PDI engine by loading settings, appropriate plugins
      // etc.
      KettleEnvironment.init( false );

      // Create an instance of this demo class for convenience
      instance = new GeneratingJobs();

      // generates a simple job, returning the JobMeta object describing it
      JobMeta jobMeta = instance.generateJob();

      // get the xml of the definition and save it to a file for inspection in spoon
      String outputFilename = "etl/generated_job.kjb";
      System.out.println( "- Saving to " + outputFilename );
      String xml = jobMeta.getXML();
      File file = new File( outputFilename );
      FileUtils.writeStringToFile( file, xml, "UTF-8" );

      System.out.println( "DONE" );
    } catch ( Exception e ) {
      e.printStackTrace();
      return;
    }

  }

  /**
   * This method generates a job definition from scratch. 
   *  
   * It demonstrates the following:
   * 
   * - Creating a new job
   * - Creating and connecting job entries
   * 
   * @return the generated job definition
   */
  public JobMeta generateJob() {

    try {
      System.out.println( "Generating a job definition" );

      // create empty transformation definition
      JobMeta jobMeta = new JobMeta();
      jobMeta.setName( "Generated Demo Job" );

      // ------------------------------------------------------------------------------------ 
      // Create start entry and put it into the job
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Start Entry" );

      // Create and configure start entry
      JobEntrySpecial start = new JobEntrySpecial();
      start.setName( "START" );
      start.setStart( true );

      // wrap into JobEntryCopy object, which holds generic job entry information
      JobEntryCopy startEntry = new JobEntryCopy( start );

      // place it on Spoon canvas properly
      startEntry.setDrawn( true );
      startEntry.setLocation( 100, 100 );

      jobMeta.addJobEntry( startEntry );

      // ------------------------------------------------------------------------------------ 
      // Create "write to log" entry and put it into the job
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Write To Log Entry" );

      // Create and configure entry
      JobEntryWriteToLog writeToLog = new JobEntryWriteToLog();
      writeToLog.setName( "Output PDI Stats" );
      writeToLog.setLogLevel( LogLevel.MINIMAL );
      writeToLog.setLogSubject( "Logging PDI Build Information:" );
      writeToLog.setLogMessage( "Version: ${Internal.Kettle.Version}\n"
        + "Build Date: ${Internal.Kettle.Build.Date}" );

      // wrap into JobEntryCopy object, which holds generic job entry information
      JobEntryCopy writeToLogEntry = new JobEntryCopy( writeToLog );

      // place it on Spoon canvas properly
      writeToLogEntry.setDrawn( true );
      writeToLogEntry.setLocation( 200, 100 );

      jobMeta.addJobEntry( writeToLogEntry );

      // connect start entry to logging entry using simple hop
      jobMeta.addJobHop( new JobHopMeta( startEntry, writeToLogEntry ) );

      // ------------------------------------------------------------------------------------ 
      // Create "success" entry and put it into the job
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Success Entry" );

      // crate and configure entry
      JobEntrySuccess success = new JobEntrySuccess();
      success.setName( "Success" );

      // wrap into JobEntryCopy object, which holds generic job entry information
      JobEntryCopy successEntry = new JobEntryCopy( success );

      // place it on Spoon canvas properly
      successEntry.setDrawn( true );
      successEntry.setLocation( 400, 100 );

      jobMeta.addJobEntry( successEntry );

      // connect logging entry to success entry on TRUE evaluation 
      JobHopMeta greenHop = new JobHopMeta( writeToLogEntry, successEntry );
      greenHop.setEvaluation( true );
      jobMeta.addJobHop( greenHop );

      // ------------------------------------------------------------------------------------ 
      // Create "abort" entry and put it into the job
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Abort Entry" );

      // crate and configure entry
      JobEntryAbort abort = new JobEntryAbort();
      abort.setName( "Abort Job" );

      // wrap into JobEntryCopy object, which holds generic job entry information
      JobEntryCopy abortEntry = new JobEntryCopy( abort );

      // place it on Spoon canvas properly
      abortEntry.setDrawn( true );
      abortEntry.setLocation( 400, 300 );

      jobMeta.addJobEntry( abortEntry );

      // connect logging entry to abort entry on FALSE evaluation 
      JobHopMeta redHop = new JobHopMeta( writeToLogEntry, abortEntry );
      redHop.setEvaluation( false );
      jobMeta.addJobHop( redHop );

      return jobMeta;

    } catch ( Exception e ) {

      // something went wrong, just log and return
      e.printStackTrace();
      return null;
    }
  }
}
