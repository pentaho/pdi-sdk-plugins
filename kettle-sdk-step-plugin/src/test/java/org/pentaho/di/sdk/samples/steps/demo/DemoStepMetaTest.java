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

package org.pentaho.di.sdk.samples.steps.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionDeep;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;

public class DemoStepMetaTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
  public void testGetStepData() {
    DemoStepMeta m = new DemoStepMeta();
    assertEquals( DemoStepData.class, m.getStepData().getClass() );
  }

  @Test
  public void testStepAnnotations() {

    // PDI Plugin Annotation-based Classloader checks
    Step stepAnnotation = DemoStepMeta.class.getAnnotation( Step.class );
    assertNotNull( stepAnnotation );
    assertFalse( Const.isEmpty( stepAnnotation.id() ) );
    assertFalse( Const.isEmpty( stepAnnotation.name() ) );
    assertFalse( Const.isEmpty( stepAnnotation.description() ) );
    assertFalse( Const.isEmpty( stepAnnotation.image() ) );
    assertFalse( Const.isEmpty( stepAnnotation.categoryDescription() ) );
    assertFalse( Const.isEmpty( stepAnnotation.i18nPackageName() ) );
    assertFalse( Const.isEmpty( stepAnnotation.documentationUrl() ) );
    assertFalse( Const.isEmpty( stepAnnotation.casesUrl() ) );
    assertFalse( Const.isEmpty( stepAnnotation.forumUrl() ) );
    assertEquals( DemoStepMeta.class.getPackage().getName(), stepAnnotation.i18nPackageName() );
    hasi18nValue( stepAnnotation.i18nPackageName(), stepAnnotation.name() );
    hasi18nValue( stepAnnotation.i18nPackageName(), stepAnnotation.description() );
    hasi18nValue( stepAnnotation.i18nPackageName(), stepAnnotation.documentationUrl() );
    hasi18nValue( stepAnnotation.i18nPackageName(), stepAnnotation.casesUrl() );
    hasi18nValue( stepAnnotation.i18nPackageName(), stepAnnotation.forumUrl() );

    // Metadata Injection Support Check
    InjectionSupported injectSupportAnnotation = DemoStepMeta.class.getAnnotation( InjectionSupported.class );
    assertNotNull( injectSupportAnnotation );

    // Metadata Injection may not support all Meta settings, so make sure it's used at least once
    boolean hasInjection = false;
    for ( Field field : DemoStepMeta.class.getDeclaredFields() ) {
      if ( field.isAnnotationPresent( Injection.class ) || field.isAnnotationPresent( InjectionDeep.class ) ) {
        hasInjection = true;
      }
    }
    for ( Method method : DemoStepMeta.class.getDeclaredMethods() ) {
      if ( method.isAnnotationPresent( Injection.class ) || method.isAnnotationPresent( InjectionDeep.class ) ) {
        hasInjection = true;
      }
    }
    assertTrue( hasInjection );
  }

  private void hasi18nValue( String i18nPackageName, String messageId ) {
    String fakeId = UUID.randomUUID().toString();
    String fakeLocalized = BaseMessages.getString( i18nPackageName, fakeId );
    assertEquals( "The way to identify a missing localization key has changed", "!" + fakeId + "!", fakeLocalized );

    // Real Test
    String localized = BaseMessages.getString( i18nPackageName, messageId );
    assertFalse( Const.isEmpty( localized ) );
    assertNotEquals( "!" + messageId + "!", localized );
  }

  @Test
  public void testDefaults() throws KettleStepException {
    DemoStepMeta m = new DemoStepMeta();
    m.setDefault();

    RowMetaInterface rowMeta = new RowMeta();
    m.getFields( rowMeta, "demo_step", null, null, null, null, null );

    // expect one field to be added to the row stream
    assertEquals( rowMeta.size(), 1 );

    // that field must be a string and named as configured
    assertEquals( rowMeta.getValueMeta(0).getType(), ValueMetaInterface.TYPE_STRING );
    assertEquals( rowMeta.getValueMeta(0).getStorageType(), ValueMetaInterface.STORAGE_TYPE_NORMAL );
    assertEquals( rowMeta.getFieldNames()[0], m.getOutputField() );
  }

  @Test
  public void testLoadSave() throws KettleException {

    /*
     * Declare any attributes used by the StepMeta that defines how the step operates.
     * These will be used to search for appropriate getter and setter methods.
     *
     * If needed, add a Map if the getter or setter method does not follow typical naming conventions.
     */
    List<String> attributes = Arrays.asList( "OutputField" );

    /*
     * If custom object types are used, additional arguments may need to be passed to the LoadSaveTester.
     * These are typically custom implementations of the FieldLoadSaveValidator interface for each Object type.
     */
    LoadSaveTester<DemoStepMeta> tester =
      new LoadSaveTester<DemoStepMeta>( DemoStepMeta.class, attributes );

    /*
     * Test the XML and Repository serialization of this StepMeta.
     * Also tests the clone() method for the Step Meta, which is used in distributed environments (e.g. Carte)
     */
    tester.testSerialization();
  }

  @Test
  public void testChecks() {
    DemoStepMeta m = new DemoStepMeta();

    // Test null input array
    List<CheckResultInterface> checkResults = new ArrayList<CheckResultInterface>();
    m.check( checkResults, new TransMeta(), new StepMeta(), null, null, null, null, new Variables(), new MemoryRepository(), null );
    assertFalse( checkResults.isEmpty() );
    boolean foundMatch = false;
    for ( CheckResultInterface result : checkResults ) {
      if ( result.getType() == CheckResultInterface.TYPE_RESULT_ERROR
          && result.getText().equals( BaseMessages.getString( DemoStepMeta.class, "Demo.CheckResult.ReceivingRows.ERROR" ) ) ) {
        foundMatch = true;
      }
    }
    assertTrue( "The step checks should fail if no input fields are given", foundMatch );

    // Test zero-length input array
    checkResults.clear();
    m.check( checkResults, new TransMeta(), new StepMeta(), null, new String[0], null, null, new Variables(), new MemoryRepository(), null );
    assertFalse( checkResults.isEmpty() );
    foundMatch = false;
    for ( CheckResultInterface result : checkResults ) {
      if ( result.getType() == CheckResultInterface.TYPE_RESULT_ERROR
          && result.getText().equals( BaseMessages.getString( DemoStepMeta.class, "Demo.CheckResult.ReceivingRows.ERROR" ) ) ) {
        foundMatch = true;
      }
    }
    assertTrue( "The step checks should fail if no input fields are given", foundMatch );

    // Test non-zero input array
    checkResults.clear();
    m.check( checkResults, new TransMeta(), new StepMeta(), null, new String[1], null, null, new Variables(), new MemoryRepository(), null );
    assertFalse( checkResults.isEmpty() );
    foundMatch = false;
    for ( CheckResultInterface result : checkResults ) {
      if ( result.getType() == CheckResultInterface.TYPE_RESULT_OK
          && result.getText().equals( BaseMessages.getString( DemoStepMeta.class, "Demo.CheckResult.ReceivingRows.OK" ) ) ) {
        foundMatch = true;
      }
    }
    assertTrue( "The step checks should fail if no input fields are given", foundMatch );
  }
}
