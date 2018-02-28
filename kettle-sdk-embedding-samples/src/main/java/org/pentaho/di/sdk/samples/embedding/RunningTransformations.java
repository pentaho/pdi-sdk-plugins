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
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

/**
 * This class demonstrates how to load and execute a PDI transformation.
 * It covers loading from both file system and repositories, 
 * as well as setting parameters prior to execution, and evaluating
 * the result.
 */
public class RunningTransformations {

  public static RunningTransformations instance;

  /**
   * @param args not used
   */
  public static void main( String[] args ) {

    // Kettle Environment must always be initialized first when using PDI
    // It bootstraps the PDI engine by loading settings, appropriate plugins etc.
    try {
      KettleEnvironment.init();
    } catch ( KettleException e ) {
      e.printStackTrace();
      return;
    }

    // Create an instance of this demo class for convenience
    instance = new RunningTransformations();

    // run a transformation from the file system
    Trans trans = instance.runTransformationFromFileSystem( "etl/parameterized_transformation.ktr" );

    // retrieve logging appender
    LoggingBuffer appender = KettleLogStore.getAppender();
    // retrieve logging lines for job
    String logText = appender.getBuffer( trans.getLogChannelId(), false ).toString();

    // report on logged lines
    System.out.println( "************************************************************************************************" );
    System.out.println( "LOG REPORT: Transformation generated the following log lines:\n" );
    System.out.println( logText );
    System.out.println( "END OF LOG REPORT" );
    System.out.println( "************************************************************************************************" );


    // run a transformation from the repository
    // NOTE: before running the repository example, you need to make sure that the 
    // repository and transformation exist, and can be accessed by the user and password used
    // uncomment and run after you've got a test repository in place

    // instance.runTransformationFromRepository("test-repository", "/home/joe", "parameterized_transformation", "joe", "password");

  }

  /**
   * This method executes a transformation defined in a ktr file
   * 
   * It demonstrates the following:
   * 
   * - Loading a transformation definition from a ktr file
   * - Setting named parameters for the transformation
   * - Setting the log level of the transformation
   * - Executing the transformation, waiting for it to finish
   * - Examining the result of the transformation
   * 
   * @param filename the file containing the transformation to execute (ktr file)
   * @return the transformation that was executed, or null if there was an error
   */
  public Trans runTransformationFromFileSystem( String filename ) {

    try {
      System.out.println( "***************************************************************************************" );
      System.out.println( "Attempting to run transformation " + filename + " from file system" );
      System.out.println( "***************************************************************************************\n" );
      // Loading the transformation file from file system into the TransMeta object.
      // The TransMeta object is the programmatic representation of a transformation definition.
      TransMeta transMeta = new TransMeta( filename, (Repository) null );

      // The next section reports on the declared parameters and sets them to arbitrary values
      // for demonstration purposes
      System.out.println( "Attempting to read and set named parameters" );
      String[] declaredParameters = transMeta.listParameters();
      for ( int i = 0; i < declaredParameters.length; i++ ) {
        String parameterName = declaredParameters[i];

        // determine the parameter description and default values for display purposes
        String description = transMeta.getParameterDescription( parameterName );
        String defaultValue = transMeta.getParameterDefault( parameterName );
        // set the parameter value to an arbitrary string
        String parameterValue =  RandomStringUtils.randomAlphanumeric( 10 );

        String output = String.format( "Setting parameter %s to \"%s\" [description: \"%s\", default: \"%s\"]",
          parameterName, parameterValue, description, defaultValue );
        System.out.println( output );

        // assign the value to the parameter on the transformation
        transMeta.setParameterValue( parameterName, parameterValue );
      }

      // Creating a transformation object which is the programmatic representation of a transformation 
      // A transformation object can be executed, report success, etc.
      Trans transformation = new Trans( transMeta );

      // adjust the log level
      transformation.setLogLevel( LogLevel.MINIMAL );

      System.out.println( "\nStarting transformation" );

      // starting the transformation, which will execute asynchronously
      transformation.execute( new String[0] );

      // waiting for the transformation to finish
      transformation.waitUntilFinished();

      // retrieve the result object, which captures the success of the transformation
      Result result = transformation.getResult();

      // report on the outcome of the transformation
      String outcome = String.format( "\nTrans %s executed %s", filename,
        ( result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors" ) );
      System.out.println( outcome );

      return transformation;
    } catch ( Exception e ) {

      // something went wrong, just log and return 
      e.printStackTrace();
      return null;
    }
  }

  /**
   * This method executes a transformation stored in a repository. 
   * 
   * It demonstrates the following:
   * 
   * - Loading a transformation definition from a repository
   * - Setting named parameters for the transformation
   * - Setting the log level of the transformation
   * - Executing the transformation, waiting for it to finish
   * - Examining the result of the transformation
   * 
   * When calling this method, kettle will look for the given repository
   * name in $KETTLE_HOME/.kettle/repositories.xml
   * 
   * If $KETTLE_HOME is not set explicitly, the user's home directory is assumed
   * 
   * @param repositoryName the name of the repository to use 
   * @param directory the directory the transformation definition lives in (i.e. "/home/joe")
   * @param transName the name of the transformation to execute  (i.e. "parameterized_transformation")
   * @param username the username to connect with
   * @param password the password to connect with
   * 
   * @return the transformation that was executed, or null if there was an error
   */
  public Trans runTransformationFromRepository( String repositoryName, String directory, String transName, String username, String password ) {

    try {
      System.out.println( "***************************************************************************************" );
      System.out.println( "Attempting to run transformation " + directory + "/" + transName + " from repository: " + repositoryName );
      System.out.println( "***************************************************************************************\n" );

      // read the repositories.xml file to determine available repositories
      RepositoriesMeta repositoriesMeta = new RepositoriesMeta();
      repositoriesMeta.readData();

      // find the repository definition using its name
      RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( repositoryName );

      if ( repositoryMeta == null ) {
        throw new KettleException( "Cannot find repository \"" + repositoryName + "\". Please make sure it is defined in your " + Const.getKettleUserRepositoriesFile() + " file" );
      }

      // use the plug-in system to get the correct repository implementation
      // the actual implementation will vary depending on the type of given
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

      // load latest revision of the transformation
      // The TransMeta object is the programmatic representation of a transformation definition.
      TransMeta transMeta = repository.loadTransformation( transName, dir, null, true, null );

      // The next section reports on the declared parameters and sets them to arbitrary values
      // for demonstration purposes
      System.out.println( "Attempting to read and set named parameters" );
      String[] declaredParameters = transMeta.listParameters();
      for ( int i = 0; i < declaredParameters.length; i++ ) {
        String parameterName = declaredParameters[i];

        // determine the parameter description and default values for display purposes
        String description = transMeta.getParameterDescription( parameterName );
        String defaultValue = transMeta.getParameterDefault( parameterName );
        // set the parameter value to an arbitrary string
        String parameterValue =  RandomStringUtils.randomAlphanumeric( 10 );

        String output = String.format( "Setting parameter %s to \"%s\" [description: \"%s\", default: \"%s\"]",
          parameterName, parameterValue, description, defaultValue );
        System.out.println( output );

        // assign the value to the parameter on the transformation
        transMeta.setParameterValue( parameterName, parameterValue );
      }

      // Creating a transformation object which is the programmatic representation of a transformation 
      // A transformation object can be executed, report success, etc.
      Trans transformation = new Trans( transMeta );

      // adjust the log level
      transformation.setLogLevel( LogLevel.MINIMAL );

      System.out.println( "\nStarting transformation" );

      // starting the transformation, which will execute asynchronously
      transformation.execute( new String[0] );

      // waiting for the transformation to finish
      transformation.waitUntilFinished();

      // retrieve the result object, which captures the success of the transformation
      Result result = transformation.getResult();

      // report on the outcome of the transformation
      String outcome = String.format( "\nTrans %s/%s executed %s", directory, transName,
        ( result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors" ) );
      System.out.println( outcome );

      return transformation;
    } catch ( Exception e ) {

      // something went wrong, just log and return 
      e.printStackTrace();
      return null;
    }
  }
}
