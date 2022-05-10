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
import static com.blueseer.adm.admData.deleteFTPMstr;
import com.blueseer.adm.admData.ftp_mstr;
import static com.blueseer.adm.admData.getFTPMstr;
import static com.blueseer.adm.admData.updateFTPMstr;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
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
import java.nio.file.Path;
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
    
    // global datatablemodel declarations       
    
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
       tbkey.setText("");
       tbport.setText("");
       lblstatus.setText("");
       lblstatus.setForeground(Color.blue);
        tbdesc.setText("");
         cbdelete.setSelected(false);
         cbpassive.setSelected(false);
         tbindir.setText("");
         tboutdir.setText("");
         tblogin.setText("");
         tbpasswd.setText("");
         tbip.setText("");
         talog.setText("");
         tacommands.setText("");
         tbtimeout.setText("");
        
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
         return m;
     }
     
    public String[] updateRecord(String[] x) {
     String[] m = updateFTPMstr(createRecord());
         return m;
     }
     
    public String[] deleteRecord(String[] x) {
     String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
         m = deleteFTPMstr(createRecord()); 
         initvars(null);
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
         return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getFTPMstr(key);
       return x.m();
    }
    
    public ftp_mstr createRecord() { 
        ftp_mstr x = new ftp_mstr(null, 
                tbkey.getText().toString(),
                tbdesc.getText().toUpperCase(),
                tbip.getText(),
                tbport.getText(),
                tblogin.getText(),
                String.valueOf(tbpasswd.getPassword()),
                tacommands.getText().replace("'", ""),      
                tbindir.getText(),
                tboutdir.getText(),
                String.valueOf(BlueSeerUtils.boolToInt(cbdelete.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbpassive.isSelected())),
                String.valueOf(BlueSeerUtils.boolToInt(cbbinary.isSelected())),       
                tbtimeout.getText()      
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
        tbpasswd.setText(x.ftp_passwd());
        tacommands.setText(x.ftp_commands());      
        tbindir.setText(x.ftp_indir());
        tboutdir.setText(x.ftp_outdir());
        cbpassive.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_passive())));
        cbdelete.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_delete())));
        cbbinary.setSelected(BlueSeerUtils.ConvertStringToBool(String.valueOf(x.ftp_binary())));
        tbtimeout.setText(x.ftp_timeout());
        setAction(x.m());
    }
    // misc
    public String[] runClient(String[] c) {
        ftp_mstr fm = admData.getFTPMstr(new String[]{c[0]});
        String[] m = new String[]{"",""};
         FTPClient client = new FTPClient();
         FileOutputStream in = null;
           try {
               
               String homeIn = EDData.getEDIInDir();
               String homeOut = EDData.getEDIOutDir();
               int timeout = 0;
               if (! fm.ftp_timeout().isEmpty()) {
                   timeout = Integer.valueOf(fm.ftp_timeout());
               }
               timeout *= 1000;
               client.setDefaultTimeout(timeout);
               client.setDataTimeout(timeout);
               
                if (! fm.ftp_indir().isEmpty()) {
                 homeIn = fm.ftp_indir();
                }
                if (! fm.ftp_outdir().isEmpty()) {
                 homeOut = fm.ftp_outdir();
                }
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
                client.login(fm.ftp_login(), fm.ftp_passwd());
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
                        for (int i = 0; i < localFiles.length; i++) {
                          if (localFiles[i].isFile()) {
                              String x = ("\\Q" + splitLine[1] + "\\E").replace("*", "\\E.*\\Q");
                                if (localFiles[i].getName().matches(x)) {
                                    InputStream inputStream = new FileInputStream(localFiles[i]);
                                    talog.append("storing file: " + localFiles[i].getName() + " size: " + localFiles[i].length() + "\n");
                                    boolean done = client.storeFile(localFiles[i].getName(), inputStream);
                                    inputStream.close();
                                    if (done) {
                                        talog.append("file stored: " + localFiles[i].getName() + "\n");
                                    } else {
                                        talog.append("unable to store file: " + localFiles[i].getName() + "\n");
                                    }   
                                    showServerReply(client);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        talog = new javax.swing.JTextArea();
        btrun = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
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
        jLabel12 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();

        jLabel8.setText("jLabel8");

        setBackground(new java.awt.Color(0, 102, 204));
        setPreferredSize(new java.awt.Dimension(884, 550));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("FTP Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(874, 560));

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

        talog.setColumns(20);
        talog.setRows(5);
        jScrollPane1.setViewportView(talog);

        btrun.setText("Run");
        btrun.setName("btrun"); // NOI18N
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cbdelete)
                                .addComponent(tbtimeout, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbpassive)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cbbinary))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbdesc, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbip, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tblogin, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbpasswd, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbindir, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tboutdir, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(btdelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btupdate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btadd)
                            .addGap(54, 54, 54)))
                    .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tbip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tblogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpasswd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbdelete)
                    .addComponent(cbpassive)
                    .addComponent(cbbinary))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtimeout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addGap(0, 0, 0))
        );

        jLabel12.setText("Status:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(tbclear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btrun))
                .addGap(40, 40, 40))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(lblstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btrun))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbbinary;
    private javax.swing.JCheckBox cbdelete;
    private javax.swing.JCheckBox cbpassive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTextArea tacommands;
    private javax.swing.JTextArea talog;
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
    // End of variables declaration//GEN-END:variables
}
