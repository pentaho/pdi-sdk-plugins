package org.pentaho.di.sdk.samples.embedding.entries.ftpplus;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * FtpPlus 下载插件参数
 * @author shepf
 */
@Getter
@Setter
@ToString
public class JobEntryFtpPlusParamsDO {
    private String serverName;
    private String userName;
    private String password;
    private String ftpDirectory;
    private String targetDirectory;
    private String wildcard;
    private boolean binaryMode;
    private int timeout;
    private boolean remove;
    private boolean onlyGettingNewFiles; /* Don't overwrite files */
    private boolean activeConnection;
    private String controlEncoding; /* how to convert list of filenames e.g. */
    /**
     * Implicit encoding used before PDI v2.4.1
     */
    private static String LEGACY_CONTROL_ENCODING = "US-ASCII";
    /**
     * Default encoding when making a new ftp job entry instance.
     */
    private static String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
    private boolean movefiles;
    private String movetodirectory;
    private boolean adddate;
    private boolean addtime;
    private boolean SpecifyFormat;
    private String date_time_format;
    private boolean AddDateBeforeExtension;
    private boolean isaddresult;
    private boolean createmovefolder;
    private String port;
    private String proxyHost;
    private String proxyPort; /* string to allow variable substitution */
    private String proxyUsername;
    private String proxyPassword;
    private String socksProxyHost;
    private String socksProxyPort;
    private String socksProxyUsername;
    private String socksProxyPassword;
    public int ifFileExistsSkip = 0;
    public String SifFileExistsSkip = "ifFileExistsSkip";
    public int ifFileExistsCreateUniq = 1;
    public String SifFileExistsCreateUniq = "ifFileExistsCreateUniq";
    public int ifFileExistsFail = 2;
    public String SifFileExistsFail = "ifFileExistsFail";
    public int ifFileExists;
    public String SifFileExists;
    public String SUCCESS_IF_AT_LEAST_X_FILES_DOWNLOADED = "success_when_at_least";
    public String SUCCESS_IF_ERRORS_LESS = "success_if_errors_less";
    public String SUCCESS_IF_NO_ERRORS = "success_if_no_errors";
    private String nr_limit;
    private String success_condition;
    long NrErrors = 0;
    long NrfilesRetrieved = 0;
    boolean successConditionBroken = false;
    int limitFiles = 0;
    String targetFilename = null;
    static String FILE_SEPARATOR = "/";



}
