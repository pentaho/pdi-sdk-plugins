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

package org.pentaho.di.sdk.samples.partitioners.demo;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.PartitionerPlugin;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.BasePartitioner;
import org.pentaho.di.trans.Partitioner;
import org.w3c.dom.Node;

/**
 * This class is part of the demo partitioner plug-in implementation.
 * It demonstrates the basics of developing a plug-in partitioner for PDI. 
 * 
 * This demo partitioner selects a partition based on the length of a
 * supplied string field. Longer strings will get increasingly higher
 * partition numbers. For string lengths 20 and above, the maximum
 * partition number will be used.    
 *   
 * This class is the implementation of the Partitioner interface. Implementations
 * of this interface are typically based on the BasePartitioner class, which 
 * provides a default implementation for the interface. 
 * 
 * Classes implementing this interface need to:
 * 
 * - keep track of configuration for the partitioning (usually the field to use
 *   for partitioning + additional configuration if required)
 * - implement clone() to create a deep copy of the object  
 * - decide which partition a row should go to
 * - indicate which dialog class is used for configuring the partitioner settings
 * - serialize the partitioner configuration to/from XML and repository
 * 
 */

@PartitionerPlugin (
    id = "DemoPartitioner",
    name = "Demo Partitioner",
    description = "Partition by length of string field"
  )
public class DemoPartitioner extends BasePartitioner implements Partitioner {

  /**
   *  The PKG member is used when looking up internationalized strings.
   *  The properties file with localized keys is expected to reside in 
   *  {the package of the class specified}/messages/messages_{locale}.properties   
   */
  private static final Class<?> PKG = DemoPartitionerDialog.class; // for i18n purposes $NON-NLS-1$

  // the field name to partition on
  private String fieldName;

  // the index of the field to partition on
  protected int partitionColumnIndex = -1;

  /**
   * The No-Arguments constructor of a partitioner
   */
  public DemoPartitioner() {
    super();
  }

  /**
   * The getInstance() method is part of the Partitioner interface and should return
   * a new instance of the partitioner, taking over the id and description from the
   * object it is called on.
   */
  public Partitioner getInstance() {
    Partitioner partitioner = new DemoPartitioner();
    partitioner.setId( getId() );
    partitioner.setDescription( getDescription() );
    return partitioner;
  }

  /**
   * The clone() method is generating a deep copy of a partitioner. This method is called
   * when steps are duplicated in Spoon.
   */
  public DemoPartitioner clone() {
    DemoPartitioner demoPartitioner = (DemoPartitioner) super.clone();
    demoPartitioner.fieldName = fieldName;

    return demoPartitioner;
  }

  /**
   * This method is called by Spoon to determine which class is used to configure the
   * partitioner's settings.
   */
  public String getDialogClassName() {
    return DemoPartitionerDialog.class.getName();
  }

  /**
   * The getPartition() method is used during the execution of the transformation. It is
   * implementing the actual partitioning algorithm. 
   * 
   * @return the partition number the row should go to
   */
  public int getPartition( RowMetaInterface rowMeta, Object[] row ) throws KettleException {

    // init() should be called first, so fields provided by BasePartitioner are guaranteed to be
    // initialized
    init( rowMeta );

    // determine the index of the field the partitioner uses for partitioning 
    if ( partitionColumnIndex < 0 ) {
      partitionColumnIndex = rowMeta.indexOfValue( fieldName );
      if ( partitionColumnIndex < 0 ) {
        throw new KettleStepException( "Unable to find partitioning field name [" + fieldName + "] in the output row..." + rowMeta );
      }
    }

    // get the string representation of the field (conversion to string is done if necessary)
    String value = rowMeta.getString( row, partitionColumnIndex );

    // Determine the length of the string value and cap it at 20
    int len = 0;
    if ( value != null ) {
      len = Math.min( value.length(), 20 );
    }

    // partition based on string length:
    // short strings go into partition [0], long strings (20 chars+) go into partition [nrPartitions-1]
    // The variable nrPartitions is provided by BasePartitioner
    return (int) Math.round( len / 20.0 * ( nrPartitions - 1 ) );
  }

  /**
   *  This method is called when Spoon needs to display the name
   *  of the partitioner on dialogs etc. 
   *  
   *  @return a string containing a descriptive string for the partitioner 
   */
  public String getDescription() {
    String description = "String length based demo partitioner";
    if ( !Const.isEmpty( fieldName ) ) {
      description += " (" + fieldName + ")";
    }
    return description;
  }

  /**
   * This method is called by Spoon when a partitioner needs to serialize its configuration to XML.
   * The expected return value is an XML fragment consisting of one or more XML tags.  
   * 
   * Please use org.pentaho.di.core.xml.XMLHandler to conveniently generate the XML.
   * 
   * @return a string containing the XML serialization of this partitioner
   */
  public String getXML() {
    StringBuilder xml = new StringBuilder();
    xml.append( "           " ).append( XMLHandler.addTagValue( "field_name", fieldName ) );
    return xml.toString();
  }

  public void loadXML( Node partitioningMethodNode ) throws KettleXMLException {
    fieldName = XMLHandler.getTagValue( partitioningMethodNode, "field_name" );
  }

  public void saveRep( Repository rep, ObjectId id_transformation, ObjectId id_step ) throws KettleException {
    rep.saveStepAttribute( id_transformation, id_step, "PARTITIONING_FIELDNAME", fieldName ); // The fieldname to partition on
  }

  public void loadRep( Repository rep, ObjectId id_step ) throws KettleException {
    fieldName = rep.getStepAttributeString( id_step, "PARTITIONING_FIELDNAME" );
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName( String fieldName ) {
    this.fieldName = fieldName;
  }
}
