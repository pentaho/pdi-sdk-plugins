/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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

import org.apache.commons.lang.RandomStringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;

/**
 * This class demonstrates how to load and execute a PDI job. It covers loading
 * from both file system and repositories, as well as setting parameters prior
 * to execution, evaluating the job result, and retrieving the job's log lines.
 */
public class RunningJobs {

  public static RunningJobs instance;

  /**
   * @param args
   *            not used
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
    instance = new RunningJobs();

    // run a job from the file system
    Job job = instance.runJobFromFileSystem( "etl/parameterized_job.kjb" );

    // retrieve logging appender
    LoggingBuffer appender = KettleLogStore.getAppender();
    // retrieve logging lines for job
    String logText = appender.getBuffer( job.getLogChannelId(), false ).toString();

    // report on logged lines
    System.out.println( "************************************************************************************************" );
    System.out.println( "LOG REPORT: Job generated the following log lines:\n" );
    System.out.println( logText );
    System.out.println( "END OF LOG REPORT" );
    System.out.println( "************************************************************************************************" );

    // run a job from the repository
    // NOTE: before running the repository example, you need to make sure
    // that the repository and job exist,
    // and can be accessed by the user and password used
    // uncomment and run after you've got a test repository in place

    // instance.runJobFromRepository("test-repository", "/home/joe",
    // "parameterized_job", "joe", "password");

  }

  /**
   * This method executes a job defined in a kjb file
   * 
   * It demonstrates the following:
   * 
   * - Loading a job definition from a kjb file - Setting named parameters for
   * the job - Setting the log level of the job - Executing the job, waiting
   * for it to finish - Examining the result of the job
   * 
   * @param filename
   *            the file containing the job to execute (kjb file)
   * @return the job that was executed, or null if there was an error
   */
  public Job runJobFromFileSystem( String filename ) {

    try {
      System.out.println( "***************************************************************************************" );
      System.out.println( "Attempting to run job " + filename + " from file system" );
      System.out.println( "***************************************************************************************\n" );
      // Loading the job file from file system into the JobMeta object.
      // The JobMeta object is the programmatic representation of a job
      // definition.
      JobMeta jobMeta = new JobMeta( filename, null );

      // The next section reports on the declared parameters and sets them
      // to arbitrary values
      // for demonstration purposes
      System.out.println( "Attempting to read and set named parameters" );
      String[] declaredParameters = jobMeta.listParameters();
      for ( int i = 0; i < declaredParameters.length; i++ ) {
        String parameterName = declaredParameters[i];

        // determine the parameter description and default values for
        // display purposes
        String description = jobMeta.getParameterDescription( parameterName );
        String defaultValue = jobMeta.getParameterDefault( parameterName );
        // set the parameter value to an arbitrary string
        String parameterValue = RandomStringUtils.randomAlphanumeric( 10 );

        String output = String.format( "Setting parameter %s to \"%s\" [description: \"%s\", default: \"%s\"]",
          parameterName, parameterValue, description, defaultValue );
        System.out.println( output );

        // assign the value to the parameter on the job
        jobMeta.setParameterValue( parameterName, parameterValue );
      }

      // Creating a Job object which is the programmatic representation of
      // a job
      // A Job object can be executed, report success, etc.
      Job job = new Job( null, jobMeta );

      // adjust the log level
      job.setLogLevel( LogLevel.MINIMAL );

      System.out.println( "\nStarting job" );

      // starting the job thread, which will execute asynchronously
      job.start();

      // waiting for the job to finish
      job.waitUntilFinished();

      // retrieve the result object, which captures the success of the job
      Result result = job.getResult();

      // report on the outcome of the job
      String outcome = String.format( "\nJob %s executed with result: %s and %d errors\n",
        filename, result.getResult(), result.getNrErrors() );
      System.out.println( outcome );

      return job;
    } catch ( Exception e ) {
      // something went wrong, just log and return
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This method executes a job stored in a repository.
   * 
   * It demonstrates the following:
   * 
   * - Loading a job definition from a repository - Setting named parameters
   * for the job - Setting the log level of the job - Executing the job,
   * waiting for it to finish - Examining the result of the job
   * 
   * When calling this method, kettle will look for the given repository name
   * in $KETTLE_HOME/.kettle/repositories.xml
   * 
   * If $KETTLE_HOME is not set explicitly, the user's home directory is
   * assumed
   * 
   * @param repositoryName
   *            the name of the repository to use
   * @param directory
   *            the directory the job definition lives in (i.e. "/home/joe")
   * @param jobName
   *            the name of the job to execute (i.e. "parameterized_job")
   * @param username
   *            the username to connect with
   * @param password
   *            the password to connect with
   * 
   * @return the job that was executed, or null if there was an error
   */
  public Job runJobFromRepository( String repositoryName, String directory, String jobName, String username, String password ) {

    try {
      System.out.println( "***************************************************************************************" );
      System.out.println( "Attempting to run job " + directory + "/" + jobName + " from repository: " + repositoryName );
      System.out.println( "***************************************************************************************\n" );

      // read the repositories.xml file to determine available
      // repositories
      RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
      repositoriesMeta.readData();

      // find the repository definition using its name
      RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( repositoryName );

      if ( repositoryMeta == null ) {
        throw new KettleException( "Cannot find repository \"" + repositoryName + "\". Please make sure it is defined in your " + Const.getKettleUserRepositoriesFile() + " file" );
      }

      // use the plug-in system to get the correct repository
      // implementation
      // the actual implementation will vary depending on the type of
      // given
      // repository (File-based, DB-based, EE, etc.)
      PluginRegistry registry = PluginRegistry.getInstance();
      Repository repository = registry.loadClass( RepositoryPluginType.class, repositoryMeta, Repository.class );

      // connect to the repository using given username and password
      repository.init( repositoryMeta );
      repository.connect( username, password );

      // find the directory we want to load from
      RepositoryDirectoryInterface tree = repository.loadRepositoryDirectoryTree();
      RepositoryDirectoryInterface dir = tree.findDirectory( directory );

      if ( dir == null ) {
        throw new KettleException( "Cannot find directory \"" + directory + "\" in repository." );
      }

      // load latest revision of the job
      // The JobMeta object is the programmatic representation of a job
      // definition.
      JobMeta jobMeta = repository.loadJob( jobName, dir, null, null );

      // The next section reports on the declared parameters and sets them
      // to arbitrary values
      // for demonstration purposes
      System.out.println( "Attempting to read and set named parameters" );
      String[] declaredParameters = jobMeta.listParameters();
      for ( int i = 0; i < declaredParameters.length; i++ ) {
        String parameterName = declaredParameters[i];

        // determine the parameter description and default values for
        // display purposes
        String description = jobMeta.getParameterDescription( parameterName );
        String defaultValue = jobMeta.getParameterDefault( parameterName );
        // set the parameter value to an arbitrary string
        String parameterValue = RandomStringUtils.randomAlphanumeric( 10 );

        String output = "Setting parameter " + parameterName + " to \"" + parameterValue + "\" [description: \"" + description + "\", default: \"" + defaultValue + "\"]";
        System.out.println( output );

        // assign the value to the parameter on the job
        jobMeta.setParameterValue( parameterName, parameterValue );

      }

      // Creating a Job object which is the programmatic representation of
      // a job
      // A Job object can be executed, report success, etc.
      Job job = new Job( repository, jobMeta );

      // adjust the log level
      job.setLogLevel( LogLevel.MINIMAL );

      System.out.println( "\nStarting job" );

      // starting the job, which will execute asynchronously
      job.start();

      // waiting for the job to finish
      job.waitUntilFinished();

      // retrieve the result object, which captures the success of the job
      Result result = job.getResult();

      // report on the outcome of the job
      String outcome = "\nJob " + directory + "/" + jobName + " executed with result: " + result.getResult() + " and " + result.getNrErrors() + " errors";
      System.out.println( outcome );

      return job;
    } catch ( Exception e ) {
      // something went wrong, just log and return
      e.printStackTrace();
      return null;
    }
  }
}
