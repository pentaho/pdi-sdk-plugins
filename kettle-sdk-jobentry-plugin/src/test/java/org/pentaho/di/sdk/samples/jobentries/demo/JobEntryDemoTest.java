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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.i18n.BaseMessages;

public class JobEntryDemoTest {

  @Test
	public void testJobEntry(){
		JobEntryDemo m = new JobEntryDemo( "Test Entry" );
		Result r = new Result();

		// execute with desired outcome: false
		m.setOutcome( false );
		m.execute( r, 0 );
		assertFalse( r.getResult() );

		// execute with desired outcome: true
		m.setOutcome( true );
		m.execute( r, 0 );
		assertTrue( r.getResult() );
	}

  @Test
  public void testAnnotations() {
    JobEntry jobEntryAnnotation = JobEntryDemo.class.getAnnotation( JobEntry.class );
    assertNotNull( jobEntryAnnotation );
    assertFalse( Const.isEmpty( jobEntryAnnotation.id() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.name() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.description() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.image() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.categoryDescription() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.i18nPackageName() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.documentationUrl() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.casesUrl() ) );
    assertFalse( Const.isEmpty( jobEntryAnnotation.forumUrl() ) );
    assertEquals( JobEntryDemo.class.getPackage().getName(), jobEntryAnnotation.i18nPackageName() );
    hasi18nValue( jobEntryAnnotation.i18nPackageName(), jobEntryAnnotation.name() );
    hasi18nValue( jobEntryAnnotation.i18nPackageName(), jobEntryAnnotation.description() );
    hasi18nValue( jobEntryAnnotation.i18nPackageName(), jobEntryAnnotation.documentationUrl() );
    hasi18nValue( jobEntryAnnotation.i18nPackageName(), jobEntryAnnotation.casesUrl() );
    hasi18nValue( jobEntryAnnotation.i18nPackageName(), jobEntryAnnotation.forumUrl() );
  }

  private void hasi18nValue( String i18nPackageName, String messageId ) {
    String fakeId = UUID.randomUUID().toString();
    String fakeLocalized = BaseMessages.getString( i18nPackageName, fakeId );
    assertEquals( "The way to identify a missing localization key has changed", "!" + fakeId + "!", fakeLocalized );

    // Real Test
    String localized = BaseMessages.getString( i18nPackageName, messageId );
    assertFalse( "No messages bundle entry found for " + messageId, Const.isEmpty( localized ) );
    assertNotEquals( "!" + messageId + "!", localized );
  }

  @Test
  public void testGetDialogClassName() {
    JobEntryDemo m = new JobEntryDemo( "Test" );
    try {
      Class.forName( m.getDialogClassName() );
    } catch ( ClassNotFoundException e ) {
      fail();
    }
  }
}
