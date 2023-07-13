/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.blueseer.adm;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.tags;
import static com.blueseer.adm.admData.addFTPMstr;
import static com.blueseer.adm.admData.addUpdateFTPAttr;
import static com.blueseer.adm.admData.deleteFTPAttrMstr;
import static com.blueseer.adm.admData.deleteFTPMstr;
import com.blueseer.adm.admData.ftp_attr;
import com.blueseer.adm.admData.ftp_mstr;
import static com.blueseer.adm.admData.getFTPAttr;
import static com.blueseer.adm.admData.getFTPAttrHash;
import static com.blueseer.adm.admData.getFTPMstr;
import static com.blueseer.adm.admData.updateFTPMstr;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.IBlueSeerT;
import com.blueseer.utl.OVData;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author vaughnte
 */
public class FTPMaint extends javax.swing.JPanel implements IBlueSeerT {

    
    // global variable declarations
                boolean isLoad = false;
                public static ftp_mstr x = null;
                public static ArrayList<ftp_attr> ftpa = null;
    
    // global datatablemodel declarations   
            javax.swing.table.DefaultTableModel modelattributes = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Key", "Value"
            });
    
    public FTPMaint() {
        initComponents();
        setLanguageTags(this);
    }

    // interface functions implemented
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
                    break;
                case "get":
                    message = getRecord(key);    
                    break;  
                case "run":
                    message = runClient(key); 
                    break;
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get")) {
             updateForm();  
             tbkey.requestFocus();
           } else if (this.type.equals("update") && message[0].equals("0")) {
             initvars(key);
           } else if (this.type.equals("run")) {
             tbkey.requestFocus();  
             btrun.setEnabled(true);
             btupdate.setEnabled(true);
             btadd.setEnabled(true);
             btdelete.setEnabled(true);
           } else {
             initvars(null);  
           }
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    
                    component.setEnabled(b);
                    
                }
            }
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    //   ((JLabel) component).setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
    
    public void setComponentDefaultValues() {
       isLoad = true;
       
       jTabbedPane1.removeAll();
       jTabbedPane1.add("Main", mainpanel);
       jTabbedPane1.add("Attributes", attributepanel);
       
       modelattributes.setRowCount(0);
       tableattribute.setModel(modelattributes);
       tableattribute.getTableHeader().setReorderingAllowed(false);
       
       tbkey.setText("");
       tbport.setText("");
       lblstatus.setText("");
       lblstatus.setForeground(Color.blue);
        tbdesc.setText("");
        
        tbattrkey.setText("");
        tbattrvalue.setText("");
        
         cbdelete.setSelected(false);
         cbpassive.setSelected(false);
         cbenabled.setSelected(false);
         cbsftp.setSelected(false);
         tbindir.setText("");
         tboutdir.setText("");
         tblogin.setText("");
         tbpasswd.setText("");
         tbip.setText("");
         talog.setText("");
         tacommands.setText("");
         tbtimeout.setText("10");
        
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        BlueSeerUtils.message(new String[]{"0",BlueSeerUtils.addRecordInit});
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setEditable(true);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public void setAction(String[] x) {
        if (x[0].equals("0")) { 
                   setPanelComponentState(this, true);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
                   tbkey.setForeground(Color.red); 
        }
    }
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbkey.requestFocus();
                    return b;
                }
                
                if (tbdesc.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbdesc.requestFocus();
                    return b;
                }
                
                if (tbip.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbip.requestFocus();
                    return b;
                }
                
                if (tblogin.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tblogin.requestFocus();
                    return b;
                }
                
                if (tbpasswd.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbpasswd.requestFocus();
                    return b;
                }
                
                
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
       
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            executeTask(dbaction.get,arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey.requestFocus();
        }
    }
    
    public String[] addRecord(String[] x) {
     String[] m = addFTPMstr(createRecord());
     ArrayList<String[]> al = new ArrayList<String[]>();
     for (int j = 0; j < tableattribute.getRowCount(); j++) {
        al.add(new String[]{x[0], tableattribute.getValueAt(j, 0).toString(), tableattribute.getValueAt(j, 1).toString()});
     }
     addUpdateFTPAttr(x[0],al);
         return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = updateFTPMstr(createRecord());
         ArrayList<String[]> al = new ArrayList<String[]>();
         for (int j = 0; j < tableattribute.getRowCount(); j++) {
            al.add(new String[]{x[0], tableattribute.getValueAt(j, 0).toString(), tableattribute.getValueAt(j, 1).toString()});
         }
         addUpdateFTPAttr(x[0],al);
       return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         deleteFTPAttrMstr(x[0]);
         m = deleteFTPMstr(createRecord()); 
         
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getFTPMstr(key);
       ftpa = getFTPAttr(key);
       return x.m();
    }
    
    public ftp_mstr createRecord() { 
        String passwd = bsmf.MainFrame.PassWord("0", tbpasswd.getPassword());
        ftp_mstr x = new ftp_mstr(null, 
                tbkey.getText().toString(),
                tbdesc.getText().toUpperCase(),
                tbip.getText(),
                tbport.getText(),
                tblogin.getText(),
                passwd,
                tacommands.getText().replace("'", ""),      
                tbindir.getText(),
                tboutdir.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbdelete.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbpassive.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbbinary.isSelected())),       
                tbtimeout.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbenabled.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbsftp.isSelected()))
                );
        return x;
    }
        
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getFTPBrowseUtil(luinput.getText(),0, "ftp_id");
        } else {
         luModel = DTData.getFTPBrowseUtil(luinput.getText(),0, "ftp_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getClassLabelTag("lblcode", this.getClass().getSimpleName()), 
                getClassLabelTag("lbldesc", this.getClass().getSimpleName()));
        
    }

    public void updateForm() {
        tbkey.setText(x.ftp_id());
        tbdesc.setText(x.ftp_desc());
        tbip.setText(x.ftp_ip());
        tbport.setText(x.ftp_port());
        tblogin.setText(x.ftp_login());
        tbpasswd.setText(bsmf.MainFrame.PassWord("1", x.ftp_passwd().toCharArray()));
        tacommands.setText(x.ftp_commands());      
        tbindir.setText(x.ftp_indir());
        tboutdir.setText(x.ftp_outdir());
        cbpassive.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_passive())));
        cbdelete.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_delete())));
        cbbinary.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_binary())));
        tbtimeout.setText(x.ftp_timeout());
        cbenabled.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_enabled())));
        cbsftp.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_sftp())));
        
        for (ftp_attr fa : ftpa) {
            modelattributes.addRow(new Object[]{fa.ftpa_key(), fa.ftpa_value()});   
        }
        
        setAction(x.m());
    }
    // misc
    public String[] runClient(String[] c) {
        ftp_mstr fm = admData.getFTPMstr(new String[]{c[0]});
        HashMap<String, String> ftpa = getFTPAttrHash(new String[]{c[0]});
        
        String homeIn = cleanDirString(EDData.getEDIInDir());
        String homeOut = cleanDirString(EDData.getEDIOutDir());
        if (! fm.ftp_indir().isEmpty()) {
         homeIn = cleanDirString(fm.ftp_indir());
        }
        if (! fm.ftp_outdir().isEmpty()) {
         homeOut = cleanDirString(fm.ftp_outdir());
        }
        int timeout = 0;
        if (! fm.ftp_timeout().isEmpty()) {
           timeout = Integer.valueOf(fm.ftp_timeout());
        }
        timeout *= 1000;
               
               
        // if sftp is set....run sftp logic then bail
        if (fm.ftp_sftp().equals("1")) {
        
            JSch jsch = new JSch();
            Session session = null;
            Channel channel = null;
            ChannelSftp csftp = null;  
            FileOutputStream in = null;
            
            Properties config = new Properties();
            
            boolean usePrivateKey = false;
            if (ftpa.containsKey("usePrivateKey")) {
                   if (ftpa.get("usePrivateKey").equals("yes")) {
                       usePrivateKey = true;
                   }
            }
            
            String privateKeyPath = "";
            if (ftpa.containsKey("privateKeyPath")) {
                privateKeyPath = ftpa.get("usePrivateKey");
            }
            
            String knownHostsPath = "";
            if (ftpa.containsKey("knownHostsPath")) {
                knownHostsPath = ftpa.get("knownHostsPath");
            }
            
            if (ftpa.containsKey("StrictHostKeyChecking")) {
                   config.put("StrictHostKeyChecking", ftpa.get("StrictHostKeyChecking"));
            } else {
                   config.put("StrictHostKeyChecking", "no"); 
            }
            
            if (ftpa.containsKey("PreferredAuthentications")) {
                   config.put("PreferredAuthentications", ftpa.get("PreferredAuthentications"));
            } else {
                   config.put("PreferredAuthentications", "publickey,password"); 
            }
            
            
             try {
                 
                 if (usePrivateKey && ! privateKeyPath.isEmpty()) {
                    jsch.addIdentity(privateKeyPath); 
                 }
                 
                 if (! knownHostsPath.isEmpty()) {
                    jsch.setKnownHosts(knownHostsPath);
                 } 
                 
                session = jsch.getSession(fm.ftp_login(), fm.ftp_ip(), Integer.valueOf(tbport.getText()));
                session.setPassword(bsmf.MainFrame.PassWord("1", x.ftp_passwd().toCharArray()));
                
                session.setConfig(config);
                
                talog.append("***   Attempting sftp connection to " + fm.ftp_ip() + "   ***" + "\n");
                
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                csftp = (ChannelSftp) channel;
                
                
                 for (String line : fm.ftp_commands().split("\\n"))   {
                    String[] splitLine = line.trim().split("\\s+");
                    if (splitLine.length > 1 && splitLine[0].equals("cd")) {
                        try{
                            talog.append("changing directory...\n");
                        csftp.cd(splitLine[1]); 
                        } catch(SftpException e){                            
                            talog.append(e.toString() + "\n");
                        }
                        
                    }
                    if (splitLine.length >= 1 && (splitLine[0].equals("dir") || splitLine[0].equals("ls"))) {
                        String x = ".";
                        if (splitLine.length == 2) {
                         x = splitLine[1];
                        }
                        
                        try{
                        talog.append("listing contents...\n");
                        java.util.List ftpFiles = csftp.ls(x); 
                        talog.append("file count..." + ftpFiles.size() + "\n");
                        if (ftpFiles != null) {
                            for (Object f : ftpFiles) {
                                LsEntry le = (LsEntry) f;
                                talog.append(le.getLongname() + "\n");
                            }
		        }
                        } catch(SftpException e){
                            talog.append(e.toString() + "\n");
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("put")) {
                        File localfolder = new File(homeOut);
	                File[] localFiles = localfolder.listFiles();
                        boolean isLocalDelete = false;
                        boolean isSuccess = false;
                        if (splitLine[1].equals("-d")) {
                            isLocalDelete = true;
                        }
                        for (int i = 0; i < localFiles.length; i++) {
                          if (localFiles[i].isFile()) {
                              isSuccess = false;
                              String x = ("\\Q" + splitLine[splitLine.length - 1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    talog.append("storing file: " + localFiles[i].getName() + " size: " + localFiles[i].length() + "\n");
                                    try {
                                    csftp.put(inputStream, localFiles[i].getName());
                                    talog.append("file stored: " + localFiles[i].getName() + "\n");
                                    isSuccess = true;
                                    } catch(SftpException e){
                                    talog.append("unable to store file: " + localFiles[i].getName() + "\n");
                                    talog.append(e.toString() + "\n");
                                    isSuccess = false;
                                    } finally {
                                      if (inputStream != null) {
                                          inputStream.close();
                                      }  
                                      if (isLocalDelete && isSuccess && ! localFiles[i].getName().isBlank()) {
                                        Path filepath = FileSystems.getDefault().getPath(homeOut + localFiles[i].getName());
                                        Files.deleteIfExists(filepath);
                                      }
                                    }
                                }
                          } 
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("get")) {
                        // first capture list of available files...
                        java.util.List ftpFiles = csftp.ls("."); 
                        if (ftpFiles != null) {
                            for (Object f : ftpFiles) {
                                LsEntry le = (LsEntry) f;
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (le.getFilename().matches(x)) {
                                Path inpath = FileSystems.getDefault().getPath(homeIn + "/" + le.getFilename());
	              		in = new FileOutputStream(inpath.toFile());
                                talog.append("retrieving file: " + le.getFilename() + " size:" + le.getAttrs() + "\n");
                                csftp.get(le.getFilename(), in);
                                in.close();
                                talog.append("file retrieved: " + le.getFilename() + "\n");
                                    if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_delete()))) {
                                        try {
                                        csftp.rm(le.getFilename());
                                        talog.append("deleted from server: " + le.getFilename() + "\n");
                                        } catch(SftpException e){
                                        talog.append("Could not delete the file: "+ le.getFilename() + "\n");
                                        talog.append(e.toString() + "\n");
                                        }
                                    }
                                }
                            }
		        }
                    } // if get
                } // for commands
                
             } catch (Exception e) {
                talog.append("***   Unable to connect to FTP server. " + e.toString() + "   ***" + "\n");
                lblstatus.setText("Failed");
                lblstatus.setForeground(Color.red);
            } finally {
                try {
                    if(session != null) {
                        session.disconnect();
                        talog.append("disconnect session...\n");
                    }

                    if(channel != null) {
                        channel.disconnect();
                        talog.append("disconnect channel...\n");
                    }

                    if(csftp != null) {
                        csftp.quit();
                        talog.append("quit...\n");
                    }
            
                } catch (Exception exc) {
                    talog.append("***   Unable to disconnect from sFTP server. " + exc.toString()+"   ***" + "\n");
                }  
            }
            lblstatus.setText("success");
            lblstatus.setForeground(Color.green);
            return new String[]{"0", "Connection complete"};
            
        } 
        
        // run normal ftp client since sftp is not checked
         FTPClient client = new FTPClient();
         FileOutputStream in = null;
           try {
               client.setDefaultTimeout(timeout);
               client.setDataTimeout(timeout);
               
                
             //  client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		if (tbport.getText().isEmpty()) {
                    client.connect(fm.ftp_ip());
                } else {
                client.connect(fm.ftp_ip(),Integer.valueOf(tbport.getText()));
                }
                
		showServerReply(client);
                
                int replyCode = client.getReplyCode();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    talog.append("connection failed..." + String.valueOf(replyCode) + "\n");
                return new String[]{"1", "connection failed"};
                }
                
               
		// client.login(tblogin.getText(), String.valueOf(tbpasswd.getPassword()));
                client.login(fm.ftp_login(), bsmf.MainFrame.PassWord("1", fm.ftp_passwd().toCharArray()));
		showServerReply(client);
                
                
                 if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_passive()))) {
		client.enterLocalPassiveMode();
                talog.append("CLIENT: setting passive" + "\n");
                } else {
                client.enterLocalActiveMode(); 
                talog.append("CLIENT: setting active" + "\n");
                }
                showServerReply(client);
                
                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_binary()))) {
		client.setFileType(FTP.BINARY_FILE_TYPE);
                talog.append("CLIENT: setting binary" + "\n");
                } else {
                client.setFileType(FTP.ASCII_FILE_TYPE);
                talog.append("CLIENT: setting ascii" + "\n");
                }
                showServerReply(client);
		
		    /* not sure why...but in scenario where login credentials are wrong...you have to execute a function (client.listFiles) that 
		       returns IOError to generate the error.....client.login does not return an IOError when wrong login or password without subsequent data dive  */
		
                for (String line : fm.ftp_commands().split("\\n"))   {
                    String[] splitLine = line.trim().split("\\s+");
                    if (splitLine.length > 1 && splitLine[0].equals("cd")) {
                        client.changeWorkingDirectory(splitLine[1]);
                        showServerReply(client);
                    }
                    if (splitLine.length >= 1 && (splitLine[0].equals("dir") || splitLine[0].equals("ls"))) {
                        String x = "";
                        if (splitLine.length == 2) {
                         x = splitLine[1];
                        }
                        FTPFile[] ftpFiles = client.listFiles(x);
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                talog.append(f.getName() + "\n");
                            }
		        }
                        showServerReply(client);
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("put")) {
                        File localfolder = new File(homeOut);
	                File[] localFiles = localfolder.listFiles();
                        boolean isLocalDelete = false;
                        boolean isSuccess = false;
                        if (splitLine[1].equals("-d")) {
                            isLocalDelete = true;
                        }
                        for (int i = 0; i < localFiles.length; i++) {                            
                          if (localFiles[i].isFile()) {
                              isSuccess = false;
                              String x = ("\\Q" + splitLine[splitLine.length - 1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    talog.append("storing file: " + localFiles[i].getName() + " size: " + localFiles[i].length() + "\n");
                                    boolean done = client.storeFile(localFiles[i].getName(), inputStream);
                                    inputStream.close();
                                    if (done) {
                                        talog.append("file stored: " + localFiles[i].getName() + "\n");
                                        isSuccess = true;
                                    } else {
                                        talog.append("unable to store file: " + localFiles[i].getName() + "\n");
                                        isSuccess = false;
                                    }   
                                    showServerReply(client);
                                    if (isLocalDelete && isSuccess && ! localFiles[i].getName().isBlank()) {
                                        Path filepath = FileSystems.getDefault().getPath(homeOut + localFiles[i].getName());
                                        Files.deleteIfExists(filepath);
                                    }
                                }
                          } 
                        }
                    }
                    if (splitLine.length > 1 && splitLine[0].equals("get")) {
                        // first capture list of available files...
                        FTPFile[] ftpFiles = client.listFiles();
                        if (ftpFiles != null) {
                            for (FTPFile f : ftpFiles) {
                                String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (f.getName().matches(x)) {
                                Path inpath = FileSystems.getDefault().getPath(homeIn + "/" + f.getName());
	              		in = new FileOutputStream(inpath.toFile());
                                talog.append("retrieving file: " + f.getName() + " size:" + f.getSize() + "\n");
                                client.retrieveFile(f.getName(), in);
                                in.close();
                                talog.append("file retrieved: " + f.getName() + "\n");
                                showServerReply(client);
                                if (BlueSeerUtils.ConvertStringToBool(String.valueOf(fm.ftp_delete()))) {
                                    boolean deleted = client.deleteFile(f.getName());
                                    if (deleted) {
                                        talog.append("deleted from server: " + f.getName() + "\n");
                                    } else {
                                        talog.append("Could not delete the file: "+ f.getName() + "\n");
                                    }
                                }
                                }
                            }
		        }
                    }
                } 
		    
                client.logout();
                showServerReply(client);
                client.disconnect();
                showServerReply(client);
		
                if (! lblstatus.getText().equals("Failed")) {
                    lblstatus.setText("success");
                    lblstatus.setForeground(Color.blue);
                }
		
	} catch (SocketException e) {
		System.out.println("socket error: " + e.getMessage());
                talog.append("socket error: " + e.getMessage());
                  lblstatus.setText("Failed");
                  lblstatus.setForeground(Color.red);
	} catch (IOException e) {
		System.out.println("io error: " + e.getMessage());
                talog.append("io error: " + e.getMessage());
                lblstatus.setText("Failed");
                lblstatus.setForeground(Color.red);
		
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
          if (client.isConnected()) {
              try {
                      client.disconnect();
              } catch (IOException ex) {
                  ex.printStackTrace();
              }
          }
       }
    return new String[]{"0", "Connection complete"};
    }
    
    private void showServerReply(FTPClient ftpClient) {
        
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            
            for (String aReply : replies) {
              if (aReply.startsWith("550")) {
                  lblstatus.setText("Failed");
                  lblstatus.setForeground(Color.red);
              }
              talog.append("SERVER: " + aReply + "\n");
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tbkey = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbclear = new javax.swing.JButton();
        btlookup = new javax.swing.JButton();
        bodypanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainpanel = new javax.swing.JPanel();
        tbindir = new javax.swing.JTextField();
        tbtimeout = new javax.swing.JTextField();
        tbip = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cbdelete = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        cbbinary = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        btadd = new javax.swing.JButton();
        tblogin = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbpasswd = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tacommands = new javax.swing.JTextArea();
        btdelete = new javax.swing.JButton();
        cbpassive = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        tboutdir = new javax.swing.JTextField();
        tbport = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cbenabled = new javax.swing.JCheckBox();
        cbsftp = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        btrun = new javax.swing.JButton();
        attributepanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableattribute = new javax.swing.JTable();
        btdeleteattr = new javax.swing.JButton();
        btaddattr = new javax.swing.JButton();
        tbattrkey = new javax.swing.JTextField();
        tbattrvalue = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        textpanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        talog = new javax.swing.JTextArea();

        jLabel8.setText("jLabel8");

        setBackground(new java.awt.Color(0, 102, 204));
        setPreferredSize(new java.awt.Dimension(884, 630));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("FTP Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(984, 620));

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        jLabel1.setText("ID");
        jLabel1.setName("lblcode"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        tbclear.setText("Clear");
        tbclear.setName("btclear"); // NOI18N
        tbclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbclearActionPerformed(evt);
            }
        });

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        bodypanel.setLayout(new javax.swing.BoxLayout(bodypanel, javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        mainpanel.setPreferredSize(new java.awt.Dimension(341, 466));

        jLabel4.setText("Login");
        jLabel4.setName("lbluser"); // NOI18N

        cbdelete.setText("Delete After Get?");
        cbdelete.setName("cbdelete"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbbinary.setText("Binary");

        jLabel3.setText("IP");
        jLabel3.setName("lblip"); // NOI18N

        jLabel7.setText("Home Out");
        jLabel7.setName("lbloutdir"); // NOI18N

        jLabel5.setText("Passwd");
        jLabel5.setName("lblpass"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel9.setText("Time Out (sec)");

        jLabel6.setText("Home In");
        jLabel6.setName("lblindir"); // NOI18N

        jLabel2.setText("Description:");
        jLabel2.setName("lbldesc"); // NOI18N

        tacommands.setColumns(20);
        tacommands.setRows(5);
        jScrollPane2.setViewportView(tacommands);

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        cbpassive.setText("Passive");
        cbpassive.setName("cbpassive"); // NOI18N

        jLabel10.setText("commands");
        jLabel10.setName("lblcommands"); // NOI18N

        jLabel11.setText("Port");
        jLabel11.setName("lblport"); // NOI18N

        cbenabled.setText("Enabled");
        cbenabled.setName("cbenabled"); // NOI18N

        cbsftp.setText("sftp");
        cbsftp.setName("cbsftp"); // NOI18N
        cbsftp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbsftpActionPerformed(evt);
            }
        });

        jLabel12.setText("Status:");

        btrun.setText("Run");
        btrun.setName("btrun"); // NOI18N
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainpanelLayout = new javax.swing.GroupLayout(mainpanel);
        mainpanel.setLayout(mainpanelLayout);
        mainpanelLayout.setHorizontalGroup(
            mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainpanelLayout.createSequentialGroup()
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainpanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10)
                            .addComponent(jLabel7)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainpanelLayout.createSequentialGroup()
                                .addComponent(cbenabled)
                                .addGap(29, 29, 29)
                                .addComponent(cbsftp))
                            .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbip, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tblogin, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbpasswd, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbindir, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tboutdir, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainpanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainpanelLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(mainpanelLayout.createSequentialGroup()
                                    .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cbdelete)
                                        .addComponent(tbtimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbpassive)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(cbbinary))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainpanelLayout.createSequentialGroup()
                                    .addComponent(btdelete)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btupdate)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btadd)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btrun))))))
                .addContainerGap())
        );
        mainpanelLayout.setVerticalGroup(
            mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainpanelLayout.createSequentialGroup()
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbenabled)
                    .addComponent(cbsftp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tbip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tblogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpasswd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbdelete)
                    .addComponent(cbpassive)
                    .addComponent(cbbinary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(19, 19, 19)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete)
                    .addComponent(btrun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap(96, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab1", mainpanel);

        attributepanel.setPreferredSize(new java.awt.Dimension(341, 466));

        tableattribute.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableattribute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableattributeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableattribute);

        btdeleteattr.setText("Delete");
        btdeleteattr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteattrActionPerformed(evt);
            }
        });

        btaddattr.setText("Add");
        btaddattr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddattrActionPerformed(evt);
            }
        });

        jLabel13.setText("Key");
        jLabel13.setName("lblattrkey"); // NOI18N

        jLabel14.setText("Value");
        jLabel14.setName("lblattrvalue"); // NOI18N

        javax.swing.GroupLayout attributepanelLayout = new javax.swing.GroupLayout(attributepanel);
        attributepanel.setLayout(attributepanelLayout);
        attributepanelLayout.setHorizontalGroup(
            attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributepanelLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, attributepanelLayout.createSequentialGroup()
                        .addComponent(btaddattr)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteattr))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(attributepanelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbattrkey, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                    .addComponent(tbattrvalue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        attributepanelLayout.setVerticalGroup(
            attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbattrkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbattrvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(attributepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteattr)
                    .addComponent(btaddattr))
                .addGap(0, 300, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("tab2", attributepanel);

        bodypanel.add(jTabbedPane1);

        talog.setColumns(20);
        talog.setRows(5);
        jScrollPane1.setViewportView(talog);

        javax.swing.GroupLayout textpanelLayout = new javax.swing.GroupLayout(textpanel);
        textpanel.setLayout(textpanelLayout);
        textpanelLayout.setHorizontalGroup(
            textpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        textpanelLayout.setVerticalGroup(
            textpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );

        bodypanel.add(textpanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(btnew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbclear)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bodypanel, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(tbclear))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btlookup)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bodypanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
      if (! validateInput(dbaction.add)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.add, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.update, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        if (! validateInput(dbaction.delete)) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask(dbaction.delete, new String[]{tbkey.getText()});  
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_tbclearActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask(dbaction.get, new String[]{tbkey.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        talog.setText("");
        lblstatus.setText("");
       // setPanelComponentState(this, false);
        btrun.setEnabled(false);
        btupdate.setEnabled(false);
        btadd.setEnabled(false);
        btdelete.setEnabled(false);
        executeTask(dbaction.run, new String[]{tbkey.getText()});
    }//GEN-LAST:event_btrunActionPerformed

    private void cbsftpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbsftpActionPerformed
        if (cbsftp.isSelected()) {
            tbport.setText("22");
        } else {
            tbport.setText("");
        }
    }//GEN-LAST:event_cbsftpActionPerformed

    private void btaddattrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddattrActionPerformed
        if (! tbattrkey.getText().isEmpty()) {
          modelattributes.addRow(new Object[]{ tbattrkey.getText(), tbattrvalue.getText() });
        }
        tbattrkey.setText("");
        tbattrvalue.setText("");
    }//GEN-LAST:event_btaddattrActionPerformed

    private void btdeleteattrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteattrActionPerformed
        int[] rows = tableattribute.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tableattribute.getModel()).removeRow(i);
        }
        tbattrkey.setText("");
        tbattrvalue.setText("");
    }//GEN-LAST:event_btdeleteattrActionPerformed

    private void tableattributeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableattributeMouseClicked
        int row = tableattribute.rowAtPoint(evt.getPoint());
        int col = tableattribute.columnAtPoint(evt.getPoint());
        // element, percent, type, enabled
        tbattrkey.setText(tableattribute.getValueAt(row, 0).toString());
        tbattrvalue.setText(tableattribute.getValueAt(row, 1).toString());
    }//GEN-LAST:event_tableattributeMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel attributepanel;
    private javax.swing.JPanel bodypanel;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddattr;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteattr;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbbinary;
    private javax.swing.JCheckBox cbdelete;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JCheckBox cbpassive;
    private javax.swing.JCheckBox cbsftp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JPanel mainpanel;
    private javax.swing.JTable tableattribute;
    private javax.swing.JTextArea tacommands;
    private javax.swing.JTextArea talog;
    private javax.swing.JTextField tbattrkey;
    private javax.swing.JTextField tbattrvalue;
    private javax.swing.JButton tbclear;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbindir;
    private javax.swing.JTextField tbip;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tblogin;
    private javax.swing.JTextField tboutdir;
    private javax.swing.JPasswordField tbpasswd;
    private javax.swing.JTextField tbport;
    private javax.swing.JTextField tbtimeout;
    private javax.swing.JPanel textpanel;
    // End of variables declaration//GEN-END:variables
}
