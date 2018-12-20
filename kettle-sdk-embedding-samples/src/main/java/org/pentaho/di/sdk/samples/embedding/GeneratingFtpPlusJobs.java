package org.pentaho.di.sdk.samples.embedding;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.success.JobEntrySuccess;
import org.pentaho.di.job.entries.writetolog.JobEntryWriteToLog;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.sdk.samples.embedding.entries.ftpplus.JobEntryFtpPlus;
import org.pentaho.di.sdk.samples.embedding.entries.ftpplus.JobEntryFtpPlusParamsDO;

import java.io.File;

public class GeneratingFtpPlusJobs {

    public static GeneratingFtpPlusJobs instance;

    /**
     * @param args not used
     */
    public static void main( String[] args ) {

        try {
            // Kettle Environment must   be initialized first when using PDI
            // It bootstraps the PDI engine by loading settings, appropriate plugins
            // etc.
            KettleEnvironment.init( false );

            // Create an instance of this demo class for convenience
            instance = new GeneratingFtpPlusJobs();

            // generates a simple job, returning the JobMeta object describing it
            JobMeta jobMeta = instance.generateJob();

            // get the xml of the definition and save it to a file for inspection in spoon
            String outputFilename = "etl/generated_ftp_job.kjb";
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
            System.out.println( "Generating a FTP job definition" );

            // create empty transformation definition
            JobMeta jobMeta = new JobMeta();
            jobMeta.setName( "Generated Demo FtpPlus Job" );

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
            writeToLog.setName( "This is FTP job example" );
            writeToLog.setLogLevel( LogLevel.MINIMAL );
            writeToLog.setLogSubject( "Logging PDI Build Information:" );
            writeToLog.setLogMessage( "Version: ${Internal.Kettle.Version}\n"
                    + "Build Date: ${Internal.Kettle.Build.Date}" );

            // wrap into JobEntryCopy object, which holds generic job entry information
            JobEntryCopy writeToLogEntry = new JobEntryCopy( writeToLog );

            // place it on Spoon canvas properly
            writeToLogEntry.setDrawn( true );
            writeToLogEntry.setLocation( 300, 100 );

            jobMeta.addJobEntry( writeToLogEntry );

            // connect start entry to logging entry using simple hop
            jobMeta.addJobHop( new JobHopMeta( startEntry, writeToLogEntry ) );


            // ------------------------------------------------------------------------------------
            // Create "FTP" entry and put it into the job
            // ------------------------------------------------------------------------------------
            System.out.println( "- Adding FTP Entry" );

            // crate and configure entry
            //
            JobEntryFtpPlus ftp = new JobEntryFtpPlus();
            JobEntryFtpPlusParamsDO jobEntryFtpPlusParamsDO = new JobEntryFtpPlusParamsDO();
            jobEntryFtpPlusParamsDO.setServerName("127.0.0.1");
            jobEntryFtpPlusParamsDO.setPort("21");
            jobEntryFtpPlusParamsDO.setUserName("ftp1");
            jobEntryFtpPlusParamsDO.setPassword("ftp1");
            jobEntryFtpPlusParamsDO.setFtpDirectory("/");
            jobEntryFtpPlusParamsDO.setWildcard(".*");
            jobEntryFtpPlusParamsDO.setTargetDirectory("/tmp");

            ftp.setName("FtpPlus");
            ftp.setPluginId("JobEntryFtpPlus");
            String jsonString = JSONObject.toJSONString(jobEntryFtpPlusParamsDO);
            ftp.setJsonConfStr(jsonString);


            // wrap into JobEntryCopy object, which holds generic job entry information
            JobEntryCopy ftpEntry = new JobEntryCopy( ftp );

            // place it on Spoon canvas properly
            ftpEntry.setDrawn( true );
            ftpEntry.setLocation( 500, 100 );

            jobMeta.addJobEntry( ftpEntry );

            // connect logging entry to FTP entry on true evaluation
            JobHopMeta greenHop1 = new JobHopMeta( writeToLogEntry, ftpEntry );
            greenHop1.setEvaluation( true );
            jobMeta.addJobHop( greenHop1 );


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
            successEntry.setLocation( 700, 100 );

            jobMeta.addJobEntry( successEntry );

            // connect logging entry to success entry on TRUE evaluation
            JobHopMeta greenHop = new JobHopMeta( ftpEntry, successEntry );
            greenHop.setEvaluation( true );
            jobMeta.addJobHop( greenHop );



            return jobMeta;

        } catch ( Exception e ) {

            // something went wrong, just log and return
            e.printStackTrace();
            return null;
        }
    }


}
