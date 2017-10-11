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

package org.pentaho.di.sdk.samples.jobentries.demo;

import java.util.List;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * This class is part of the demo job entry plug-in implementation.
 * It demonstrates the basics of developing a plug-in job entry for PDI. 
 * 
 * The demo job entry is configurable to yield a positive or negative 
 * result. The job logic will follow the respective path during execution. 
 *    
 * This class is the implementation of JobEntryInterface.
 * Classes implementing this interface need to:
 * 
 * - keep track of the job entry settings
 * - serialize job entry settings both to XML and a repository
 * - indicate to PDI which class implements the dialog for this job entry  
 * - indicate to PDI whether the job entry supports unconditional, true or false, or both types of results
 * - execute the job entry logic and provide a job entry result
 * 
 */
@JobEntry(
    id = "DemoJobEntry",
    name = "DemoJobEntry.Name",
    description = "DemoJobEntry.TooltipDesc",
    image = "org/pentaho/di/sdk/samples/jobentries/demo/resources/demo.svg",
    categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.Conditions",
    i18nPackageName = "org.pentaho.di.sdk.samples.jobentries.demo",
    documentationUrl = "DemoJobEntry.DocumentationURL",
    casesUrl = "DemoJobEntry.CasesURL",
    forumUrl = "DemoJobEntry.ForumURL"
  )
public class JobEntryDemo extends JobEntryBase implements Cloneable, JobEntryInterface {

  /**
   *  The PKG member is used when looking up internationalized strings.
   *  The properties file with localized keys is expected to reside in 
   *  {the package of the class specified}/messages/messages_{locale}.properties   
   */
  private static Class<?> PKG = JobEntryDemo.class; // for i18n purposes $NON-NLS-1$

  // This field holds the configured result of the job entry.
  // It is configured in the JobEntryDemoDialog
  private boolean outcome;

  /**
   * The JobEntry constructor executes super() and initializes its fields
   * with sensible defaults for new instances of the job entry. 
   * 
   * @param name the name of the new job entry
   */
  public JobEntryDemo( String name ) {
    super( name, "" );

    // the default is to generate a positive outcome 
    outcome = true;
  }

  /**
   * No-Arguments constructor for convenience purposes.
   */
  public JobEntryDemo() {
    this( "" );
  }

  /**
   * Let PDI know the class name to use for the dialog. 
   * @return the class name to use for the dialog for this job entry
   */
  public String getDialogClassName() {
    return JobEntryDemoDialog.class.getName();
  }

  /**
   * This method is used when a job entry is duplicated in Spoon. It needs to return a deep copy of this
   * job entry object. Be sure to create proper deep copies if the job entry configuration is stored in
   * modifiable objects.
   * 
   * See org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta.clone() for an example on creating
   * a deep copy of an object.
   * 
   * @return a deep copy of this
   */
  public Object clone() {
    JobEntryDemo je = (JobEntryDemo) super.clone();
    return je;
  }

  /**
   * This method is called by Spoon when a job entry needs to serialize its configuration to XML. The expected
   * return value is an XML fragment consisting of one or more XML tags.  
   * 
   * Please use org.pentaho.di.core.xml.XMLHandler to conveniently generate the XML.
   * 
   * Note: the returned string must include the output of super.getXML() as well
   * @return a string containing the XML serialization of this job entry
   */
  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder();

    retval.append( super.getXML() );
    retval.append( "      " ).append( XMLHandler.addTagValue( "outcome", outcome ) );

    return retval.toString();
  }

  /**
   * This method is called by PDI when a job entry needs to load its configuration from XML.
   * 
   * Please use org.pentaho.di.core.xml.XMLHandler to conveniently read from the
   * XML node passed in.
   * 
   * Note: the implementation must call super.loadXML() to ensure correct behavior
   * 
   * @param entrynode    the XML node containing the configuration
   * @param databases    the databases available in the job
   * @param slaveServers the slave servers available in the job
   * @param rep          the repository connected to, if any
   * @param metaStore    the metastore to optionally read from
   */
  @Override
  public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep, IMetaStore metaStore ) throws KettleXMLException {
    try {
      super.loadXML( entrynode, databases, slaveServers );
      outcome = "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "outcome" ) );
    } catch ( Exception e ) {
      throw new KettleXMLException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromXML" ), e );
    }
  }

  /**
   * This method is called by Spoon when a job entry needs to serialize its configuration to a repository.
   * The repository implementation provides the necessary methods to save the job entry attributes.
   *
   * @param rep    the repository to save to
   * @param id_job  the id to use for the job when saving
   * @param metaStore    the metastore to optionally write to 
   */
  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
    try {
      rep.saveJobEntryAttribute( id_job, getObjectId(), "outcome", outcome );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToSaveToRepository" ) + id_job, dbe );
    }
  }

  /**
   * This method is called by PDI when a job entry needs to read its configuration from a repository.
   * The repository implementation provides the necessary methods to read the job entry attributes.
   * 
   * @param rep      the repository to read from
   * @param metaStore    the metastore to optionally read from
   * @param id_jobentry  the id of the job entry being read
   * @param databases    the databases available in the job
   * @param slaveServers  the slave servers available in the job
   */
  @Override
  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases, List<SlaveServer> slaveServers ) throws KettleException {
    try {
      outcome = rep.getJobEntryAttributeBoolean( id_jobentry, "outcome" );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromRepository" ) + id_jobentry, dbe );
    }
  }

  /**
   * This method is called when it is the job entry's turn to run during the execution of a job.
   * It should return the passed in Result object, which has been updated to reflect the outcome
   * of the job entry. The execute() method should call setResult(), setNrErrors() and modify the
   * rows or files attached to the result object if required.
   *
   * @param prev_result The result of the previous execution
   * @return The Result of the execution.
   */
  public Result execute( Result prev_result, int nr ) {
    // indicate there are no errors
    prev_result.setNrErrors( 0 );
    // indicate the result as configured
    prev_result.setResult( outcome );
    return prev_result;
  }

  /**
   * Returns true if the job entry offers a genuine true/false result upon execution,
   * and thus supports separate "On TRUE" and "On FALSE" outgoing hops. 
   */
  public boolean evaluates() {
    return true;
  }

  /**
   * Returns true if the job entry supports unconditional outgoing hops.
   */
  public boolean isUnconditional() {
    return false;
  }

  /**
   * Getter.
   * @return the configured outcome for the job entry
   */
  public boolean getOutcome() {
    return outcome;
  }

  /**
   * Setter.
   * @param outcome the configured outcome for the job entry 
   */
  public void setOutcome( boolean outcome ) {
    this.outcome = outcome;
  }
}
