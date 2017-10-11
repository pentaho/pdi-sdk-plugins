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
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.addsequence.AddSequenceMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;

/**
 * This class demonstrates how to create a PDI transformation definition 
 * in code, and save it to a ktr file.
 */
public class GeneratingTransformations {

  public static GeneratingTransformations instance;

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
      instance = new GeneratingTransformations();

      // generates a simple transformation, returning the TransMeta object describing it
      TransMeta transMeta = instance.generateTransformation();

      // get the xml of the definition and save it to a file for inspection in spoon
      String outputFilename = "etl/generated_transformation.ktr";
      System.out.println( "- Saving to " + outputFilename );
      String xml = transMeta.getXML();
      File file = new File( outputFilename );
      FileUtils.writeStringToFile( file, xml, "UTF-8" );

      System.out.println( "DONE" );
    } catch ( Exception e ) {
      e.printStackTrace();
      return;
    }
  }

  /**
   * This method generates a transformation definition from scratch. 
   *  
   * It demonstrates the following:
   * 
   * - Creating a new transformation
   * - Creating and connecting transformation steps
   * 
   * @return the generated transformation definition
   */
  public TransMeta generateTransformation() {
    try {
      System.out.println( "Generating a transformation definition" );

      // create empty transformation definition
      TransMeta transMeta = new TransMeta();
      transMeta.setName( "Generated Demo Transformation" );

      // The plug-in registry is used to determine the plug-in ID of each step used 
      PluginRegistry registry = PluginRegistry.getInstance();

      // ------------------------------------------------------------------------------------ 
      // Create Row Generator Step and put it into the transformation
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Row Generator Step" );

      // Create Step Definition and determine step ID 
      RowGeneratorMeta rowGeneratorMeta = new RowGeneratorMeta();
      String rowGeneratorPluginId = registry.getPluginId( StepPluginType.class, rowGeneratorMeta );

      // Step it is configured to generate 5 rows with 2 fields
      // field_1: "Hello World" (PDI Type: String)
      // field_2: 100 (PDI Type: Integer)

      rowGeneratorMeta.setRowLimit( "5" );

      rowGeneratorMeta.allocate( 2 );
      rowGeneratorMeta.setFieldName( new String[] { "field_1", "field_2" } );
      rowGeneratorMeta.setFieldType( new String[] { "String", "Integer" } );
      rowGeneratorMeta.setValue( new String[] { "Hello World", "100" } );

      StepMeta rowGeneratorStepMeta = new StepMeta( rowGeneratorPluginId, "Generate Some Rows", rowGeneratorMeta );

      // make sure the step appears on the canvas and is properly placed in spoon
      rowGeneratorStepMeta.setDraw( true );
      rowGeneratorStepMeta.setLocation( 100, 100 );

      // include step in transformation
      transMeta.addStep( rowGeneratorStepMeta );

      // ------------------------------------------------------------------------------------ 
      // Create "Add Sequence" Step and connect it the Row Generator
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Add Sequence Step" );

      // Create Step Definition 
      AddSequenceMeta addSequenceMeta = new AddSequenceMeta();
      String addSequencePluginId = registry.getPluginId( StepPluginType.class, addSequenceMeta );

      // configure counter options
      addSequenceMeta.setDefault();
      addSequenceMeta.setValuename( "counter" );
      addSequenceMeta.setCounterName( "counter_1" );
      addSequenceMeta.setStartAt( 1 );
      addSequenceMeta.setMaxValue( Long.MAX_VALUE );
      addSequenceMeta.setIncrementBy( 1 );

      StepMeta addSequenceStepMeta = new StepMeta( addSequencePluginId, "Add Counter Field", addSequenceMeta );

      // make sure the step appears on the canvas and is properly placed in spoon
      addSequenceStepMeta.setDraw( true );
      addSequenceStepMeta.setLocation( 300, 100 );

      // include step in transformation
      transMeta.addStep( addSequenceStepMeta );

      // connect row generator to add sequence step
      transMeta.addTransHop( new TransHopMeta( rowGeneratorStepMeta, addSequenceStepMeta ) );

      // ------------------------------------------------------------------------------------ 
      // Add a "Dummy" Step and connect it the previous step
      // ------------------------------------------------------------------------------------
      System.out.println( "- Adding Dummy Step" );
      // Create Step Definition 
      DummyTransMeta dummyMeta = new DummyTransMeta();
      String dummyPluginId = registry.getPluginId( StepPluginType.class, dummyMeta );

      StepMeta dummyStepMeta = new StepMeta( dummyPluginId, "Dummy", dummyMeta );

      // make sure the step appears alright in spoon
      dummyStepMeta.setDraw( true );
      dummyStepMeta.setLocation( 500, 100 );

      // include step in transformation
      transMeta.addStep( dummyStepMeta );

      // connect row generator to add sequence step
      transMeta.addTransHop( new TransHopMeta( addSequenceStepMeta, dummyStepMeta ) );

      return transMeta;
    } catch ( Exception e ) {
      // something went wrong, just log and return
      e.printStackTrace();
      return null;
    }
  }
}
