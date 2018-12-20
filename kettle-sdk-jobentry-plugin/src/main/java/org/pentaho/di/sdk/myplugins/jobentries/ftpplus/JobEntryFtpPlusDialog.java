package org.pentaho.di.sdk.myplugins.jobentries.ftpplus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.annotations.PluginDialog;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.StyledTextComp;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author shepf
 */
@PluginDialog(id = "JobEntryFtpPlus",
        image = "org/pentaho/di/sdk/myplugins/jobentries/ftpplus/resources/demo.svg",
        pluginType = PluginDialog.PluginType.JOBENTRY)
public class JobEntryFtpPlusDialog extends JobEntryDialog implements JobEntryDialogInterface {

    /**
     * for i18n
     */
    private static Class<?> PKG = JobEntryFtpPlus.class;
    /**
     * the job entry configuration object
     */
    private JobEntryFtpPlus jobEntry;

    /**
     * 界面
     */
    private Label wlName;
    private Text wText;
    private FormData fdlName, fdName;
    private Label wlConfigInfo;
    private StyledTextComp wConfigInfo;
    private JEditorPane editPane;
    private FormData fdlConfigInfo, fdConfigInfo;
    private Label wlPosition;
    private FormData fdlPosition;
    private Button wOK, wGet, wCancel;
    private Listener lsOK, lsGet, lsCancel;
    private Shell shell;
    private SelectionAdapter lsDef;
    private boolean changed;

    /**
     * 配置名称
     */
    private TextVar wClassName;
    private Label wlClassName;
    private FormData fdlClassName, fdClassName;

    /**
     * Instantiates a new job entry dialog.
     *
     * @param parent      the parent shell
     * @param jobEntryInt the job entry interface
     * @param rep         the repository
     * @param jobMeta
     */
    public JobEntryFtpPlusDialog(Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta) {
        super(parent, jobEntryInt, rep, jobMeta);
        // it is safe to cast the JobEntryInterface object to the object handled by this dialog
        jobEntry = (JobEntryFtpPlus) jobEntryInt;
        // ensure there is a default name for new job entries
        if (this.jobEntry.getName() == null) {
            this.jobEntry.setName(BaseMessages.getString(PKG, "FtpPlus.Default.Name"));
        }
    }

    @Override
    public JobEntryInterface open() {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, props.getJobsDialogStyle());
        props.setLook(shell);
        JobDialog.setShellImage(shell, jobEntry);

        ModifyListener lsMod = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                jobEntry.setChanged();
            }
        };
        changed = jobEntry.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.Title"));

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        wGet = new Button(shell, SWT.PUSH);
        wGet.setText("获取默认配置");
        wGet.setToolTipText("在输入类名称后再通过此按钮获取对应默认配置信息");

        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        // at the bottom
        BaseStepDialog.positionBottomButtons(shell, new Button[]{wOK, wCancel, wGet}, margin, null);

        // Label组件
        wlName = new Label(shell, SWT.RIGHT);
        wlName.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.Jobname.Label"));
        props.setLook(wlName);
        fdlName = new FormData();
        fdlName.left = new FormAttachment(0, 0);
        fdlName.right = new FormAttachment(middle, -margin);
        fdlName.top = new FormAttachment(0, margin);
        wlName.setLayoutData(fdlName);
        wText = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wText);
        wText.addModifyListener(lsMod);
        fdName = new FormData();
        fdName.left = new FormAttachment(middle, 0);
        fdName.top = new FormAttachment(0, margin);
        fdName.right = new FormAttachment(100, 0);
        wText.setLayoutData(fdName);

        wlClassName = new Label(shell, SWT.RIGHT);
        wlClassName.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.ClassName.Label") + " ");
        props.setLook(wlClassName);
        fdlClassName = new FormData();
        fdlClassName.left = new FormAttachment(0, 0);
        fdlClassName.right = new FormAttachment(middle, -margin);
        fdlClassName.top = new FormAttachment(wText, margin);
        wlClassName.setLayoutData(fdlClassName);

        wClassName = new TextVar(jobEntry, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wClassName);
        wClassName.addModifyListener(lsMod);
        fdClassName = new FormData();
        fdClassName.left = new FormAttachment(middle, 0);
        fdClassName.top = new FormAttachment(wText, margin);
        fdClassName.right = new FormAttachment(100, margin);
        wClassName.setLayoutData(fdClassName);

        wlPosition = new Label(shell, SWT.NONE);
        wlPosition.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.LineNr.Label", "0"));
        props.setLook(wlPosition);
        fdlPosition = new FormData();
        fdlPosition.left = new FormAttachment(0, 0);
        fdlPosition.bottom = new FormAttachment(wOK, -margin);
        wlPosition.setLayoutData(fdlPosition);

        // Script line
        wlConfigInfo = new Label(shell, SWT.NONE);
        wlConfigInfo.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.Script.Label"));
        props.setLook(wlConfigInfo);
        fdlConfigInfo = new FormData();
        fdlConfigInfo.left = new FormAttachment(0, 0);
        fdlConfigInfo.top = new FormAttachment(wClassName, margin);
        wlConfigInfo.setLayoutData(fdlConfigInfo);
        wConfigInfo =
                new StyledTextComp(jobEntry, shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, "");
        //默认json配置
        wConfigInfo.setText("{}");
        props.setLook(wConfigInfo, Props.WIDGET_STYLE_FIXED);
        wConfigInfo.addModifyListener(lsMod);
        fdConfigInfo = new FormData();
        fdConfigInfo.left = new FormAttachment(0, 0);
        fdConfigInfo.top = new FormAttachment(wlConfigInfo, margin);
        fdConfigInfo.right = new FormAttachment(100, -10);
        fdConfigInfo.bottom = new FormAttachment(wlPosition, -margin);
        wConfigInfo.setLayoutData(fdConfigInfo);
        wConfigInfo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                setPosition();
            }

        });

        wConfigInfo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setPosition();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setPosition();
            }
        });
        wConfigInfo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setPosition();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setPosition();
            }
        });
        wConfigInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                setPosition();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                setPosition();
            }

            @Override
            public void mouseUp(MouseEvent e) {
                setPosition();
            }
        });
        wConfigInfo.addModifyListener(lsMod);
        // Add listeners
        lsCancel = new Listener() {
            @Override
            public void handleEvent(Event e) {
                cancel();
            }
        };
        lsOK = new Listener() {
            @Override
            public void handleEvent(Event e) {
                ok();
            }
        };
        lsGet = new Listener() {
            @Override
            public void handleEvent(Event e) {
                String prettyConf = null;
                String msg = "获取默认配置失败";
                try {
                    String confStr = jobEntry.getConfigInfo();
                    //TODO json格式化显示
                    //prettyConf = JSON.toJSONString(confStr,SerializerFeature.PrettyFormat);
                    //JSONObject jsonObject = JSON.parseObject(jobEntry.environmentSubstitute(confStr));
                    //prettyConf = jsonObject.toJSONString();
                    prettyConf = JSON.toJSONString(jobEntry.environmentSubstitute(confStr),SerializerFeature.PrettyFormat);

                } catch (Exception e1) {
                    msg = e1.getMessage();
                }
                if (StringUtils.isBlank(prettyConf)) {
                    wConfigInfo.setText("{}");
                    MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
                    mb.setMessage(msg);
                    mb.setText("错误");
                    mb.open();
                } else {
                    wConfigInfo.setText(prettyConf);
                }
            }
        };

        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);
        wGet.addListener(SWT.Selection, lsGet);

        lsDef = new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                ok();
            }
        };

        wText.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                cancel();
            }
        });

        getData();

        BaseStepDialog.setSize(shell, 250, 250, false);

        shell.open();
        props.setDialogSize(shell, "JobEvalDialogSize");
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return jobEntry;
    }

    public void setPosition() {

        String scr = wConfigInfo.getText();
        int linenr = wConfigInfo.getLineAtOffset(wConfigInfo.getCaretOffset()) + 1;
        int posnr = wConfigInfo.getCaretOffset();

        // Go back from position to last CR: how many positions?
        int colnr = 0;
        while (posnr > 0 && scr.charAt(posnr - 1) != '\n' && scr.charAt(posnr - 1) != '\r') {
            posnr--;
            colnr++;
        }
        wlPosition.setText(BaseMessages.getString(PKG, "JobEntryKettleUtil.Position.Label", "" + linenr, "" + colnr));

    }

    public void dispose() {
        WindowProperty winprop = new WindowProperty(shell);
        props.setScreen(winprop);
        shell.dispose();
    }

    /**
     * Copy information from the meta-data input to the dialog fields.
     */
    public void getData() {
        if (jobEntry.getName() != null) {
            wText.setText(jobEntry.getName());
        }
        if ( jobEntry.getClassName() != null ) {
            wClassName.setText( jobEntry.getClassName() );
        }
        if ( jobEntry.getConfigInfo() != null ) {
            wConfigInfo.setText( jobEntry.getConfigInfo() );
        }

        wText.selectAll();
        wText.setFocus();
    }

    private void cancel() {
        jobEntry.setChanged(changed);
        jobEntry = null;
        dispose();
    }

    private void ok() {
        if (Const.isEmpty(wText.getText())) {
            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
            mb.setText(BaseMessages.getString(PKG, "System.StepJobEntryNameMissing.Title"));
            mb.setMessage(BaseMessages.getString(PKG, "System.JobEntryNameMissing.Msg"));
            mb.open();
            return;
        }

        jobEntry.setConfigInfo( wConfigInfo.getText() );
        //jobEntry.setClassName( wClassName.getText() );
        dispose();
    }

}
