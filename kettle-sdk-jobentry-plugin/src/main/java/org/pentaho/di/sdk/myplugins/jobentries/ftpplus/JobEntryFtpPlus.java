package org.pentaho.di.sdk.myplugins.jobentries.ftpplus;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

/**
 * @author shepf
 */
@JobEntry(
        id = "JobEntryFtpPlus",
        name = "JobEntryFtpPlus.Name",
        description = "JobEntryFtpPlus.TooltipDesc",
        categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.FileTransfer",
        i18nPackageName = "org.pentaho.di.sdk.myplugins.jobentries.ftpplus",
        documentationUrl = "JobEntryFtpPlus.DocumentationURL",
        casesUrl = "JobEntryFtpPlus.CasesURL",
        forumUrl = "JobEntryFtpPlus.ForumURL"
)
public class JobEntryFtpPlus extends JobEntryBase implements Cloneable, JobEntryInterface {

    private String jsonConfStr = "{}";
    private String className = "JobEntryFtpPlus";

    /**
     *  for i18n
     */
    private static Class<?> PKG = JobEntryFtpPlus.class;

    public String getJsonConfStr() {
        return jsonConfStr;
    }

    public void setJsonConfStr(String jsonConfStr) {
        this.jsonConfStr = jsonConfStr;
    }


    @Override
    public Result execute(Result prev_result, int nr) throws KettleException {

        //TODO ftp业务代码

        // indicate there are no errors
        prev_result.setNrErrors( 0 );
        // indicate the result as configured
        prev_result.setResult( true );
        return prev_result;
    }


    @Override
    public String getXML() {
        StringBuffer retval = new StringBuffer();

        retval.append(super.getXML());
        retval.append("      ").append(
                XMLHandler.addTagValue("configInfo", jsonConfStr));
        retval.append("      ").append(
                XMLHandler.addTagValue("className", className));

        return retval.toString();
    }

    @Override
    public void loadXML(Node entrynode, List<DatabaseMeta> databases,
                        List<SlaveServer> slaveServers, Repository rep, IMetaStore metaStore)
            throws KettleXMLException {
        try {
            super.loadXML(entrynode, databases, slaveServers);
            jsonConfStr = XMLHandler.getTagValue(entrynode, "configInfo");



            className = XMLHandler.getTagValue(entrynode, "className");
        } catch (Exception e) {
            throw new KettleXMLException(BaseMessages.getString(PKG,
                    "JobEntryKettleUtil.UnableToLoadFromXml"), e);
        }
    }




}
