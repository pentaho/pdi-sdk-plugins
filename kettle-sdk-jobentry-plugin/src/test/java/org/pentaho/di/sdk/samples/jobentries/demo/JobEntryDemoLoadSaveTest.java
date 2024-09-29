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

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;

public class JobEntryDemoLoadSaveTest extends JobEntryLoadSaveTestSupport<JobEntryDemo> {

  @Override
  protected Class<JobEntryDemo> getJobEntryClass() {
    return JobEntryDemo.class;
  }

  @Override
  protected List<String> listCommonAttributes() {
    return Arrays.asList( "outcome" );
  }
}
