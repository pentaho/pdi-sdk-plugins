package org.pentaho.di.sdk.myplugins.jobentries.ftpplus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
import java.util.List;

/**
 * configInfo json字符串格式
 * getDefaultConfigInfo() 格式化 json str
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
@Getter
@Setter
@ToString
public class JobEntryFtpPlus extends JobEntryBase implements Cloneable, JobEntryInterface {

    private String configInfo = "{}";
    private String className = this.getClass().getName();
    private JobEntryFtpPlusParamsDO jobEntryFtpPlusParamsDO;

    /**
     *  for i18n
     */
    private static Class<?> PKG = JobEntryFtpPlus.class;


    @Override
    public Result execute(Result prev_result, int nr) throws KettleException {

        //TODO ftp业务代码
        log.logBasic("good: " + jobEntryFtpPlusParamsDO.toString());

        FTPClient ftp = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();

        //config.setXXX(YYY); // change required options
        // for example config.setServerTimeZoneId("Pacific/Pitcairn")
        ftp.configure(config );
        boolean error = false;
        try {
            int reply;
            String server = "127.0.0.1";
            ftp.connect(server);
            ftp.login("ftp1",null);


            System.out.println("Connected to " + server + ".");
            System.out.print(ftp.getReplyString());

            // After connection attempt, you should check the reply code to verify
            // success.
            reply = ftp.getReplyCode();

            if(!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }
            // transfer files
            ftp.logout();
        } catch(IOException e) {
            error = true;
            e.printStackTrace();
        } finally {
            if(ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch(IOException ioe) {
                    // do nothing
                }
            }
            System.exit(error ? 1 : 0);
        }

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
        JSONObject.toJSONString(jobEntryFtpPlusParamsDO);

        retval.append("      ").append(
                XMLHandler.addTagValue("configInfo", configInfo));
        retval.append("      ").append(
                XMLHandler.addTagValue("className", className));

        return retval.toString();
    }

    @Override
    public void loadXML(Node entryNode, List<DatabaseMeta> databases,
                        List<SlaveServer> slaveServers, Repository rep, IMetaStore metaStore)
            throws KettleXMLException {
        try {
            super.loadXML(entryNode, databases, slaveServers);
            configInfo = XMLHandler.getTagValue(entryNode, "configInfo");

            //解析json到java类
            jobEntryFtpPlusParamsDO=  JSON.parseObject(configInfo,JobEntryFtpPlusParamsDO.class);

            className = XMLHandler.getTagValue(entryNode, "className");
        } catch (Exception e) {
            throw new KettleXMLException(BaseMessages.getString(PKG,
                    "JobEntryKettleUtil.UnableToLoadFromXml"), e);
        }
    }

}
