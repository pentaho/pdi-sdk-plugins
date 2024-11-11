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
