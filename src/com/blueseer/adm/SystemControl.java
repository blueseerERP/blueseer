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
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.deleteNonMasterTransactionData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */


public class SystemControl extends javax.swing.JPanel implements IBlueSeerc {

    // global variable declarations
                boolean isLoad = false;
                
                
    public SystemControl() {
        initComponents();
        setLanguageTags(this);
    }

    // interface functions implemented
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
         //   System.out.println("this type: " + this.type);
             switch(this.type) {
                 case "run":
                    message = getPatch();
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "get":
                    message = getRecord(key);    
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
            if (this.type.equals("run")) {
                  setAction(message);
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
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
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
    } 
    
   
    
    
    public void setComponentDefaultValues() {
       isLoad = true;
         buttonGroup1.add(rbsamba);
        buttonGroup1.add(rbwin);
        buttonGroup1.add(rblocal);
        
        tbversion.setEnabled(false);
        tblocale.setEnabled(false);
        rbsamba.setSelected(false);
        rbwin.setSelected(false);
        rblocal.setSelected(false);
        
       
       isLoad = false;
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
    
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
          setPanelComponentState(this,true);
        } 
    }
    
    public boolean validateInput(dbaction x) {
        boolean b = true;
                                
                if (tbversion.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbversion.requestFocus();
                    return b;
                }
                if (tbbgimage.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbbgimage.requestFocus();
                    return b;
                }
                if (tbrcolor.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbrcolor.requestFocus();
                    return b;
                }
                if (tbbcolor.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbbcolor.requestFocus();
                    return b;
                }
                if (tbgcolor.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show(getMessageTag(1024));
                    tbgcolor.requestFocus();
                    return b;
                }
                
               
        return b;
    }
    
    public void initvars(String[] arg) {
            setComponentDefaultValues();
            executeTask(dbaction.get, null);
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     String passwd = bsmf.MainFrame.PassWord("0", tbsmtppass.getPassword());
     
     try {
           
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                String serverfiletype = "";
                int i = 0;
                String login = "";
                
                if ( cblogin.isSelected() ) {
                login = "1";    
                } else {
                    login = "0";
                }
                
                if (rbsamba.isSelected()) {
                    serverfiletype = "S";
                }
                if (rbwin.isSelected()) {
                    serverfiletype = "W";
                }
                if (rblocal.isSelected()) {
                    serverfiletype = "L";
                }
                
                   
                    res = st.executeQuery("SELECT *  FROM  ov_mstr;");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                    st.executeUpdate("insert into ov_ctrl values (" + 
                            "'" + "0.0.0" + "'" + "," +
                            "'" + "" + "'" + "," +
                            "'" + "" + "'" + "," +
                            "'" + login + "'" +  "," +
                            "'" + "0" + "'" + "," +
                            "'" + "" + "'" + "," +
                            "'0'" + "," + "'102'" + "," + "'204'" + "," +
                            "'" + serverfiletype + "'" +  "," + "," +
                            "'" + tbimagedir.getText() + "'" +  "," + "," +
                            "'" + tbtempdir.getText() + "'" +  "," + "," +
                            "'" + tblabeldir.getText() + "'" +  "," + "," +
                            "'" + tbjasperdir.getText() + "'" +  "," + "," +
                            "'" + tbedidir.getText() + "'" +  "," + "," +
                            "'" + tbemailserver.getText() + "'" +  "," + "," +
                            "'" + tbemailfrom.getText() + "'" +  "," + 
                            "'" + tbsmtpuser.getText() + "'" +  "," + 
                            "'" + passwd + "'" +        
                            ")" + ";");                  
                          m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                    st.executeUpdate("update ov_ctrl set " 
                            + " ov_login = " + "'" + login + "'" + "," +
                              " ov_custom = " + "'" + BlueSeerUtils.boolToInt(cbcustom.isSelected()) + "'" + "," +
                            " ov_version = " + "'" + tbversion.getText() + "'" +  "," +
                            " ov_bgimage = " + "'" + tbbgimage.getText() + "'" + "," +
                            " ov_rcolor = " + "'" + tbrcolor.getText() + "'" + "," +
                            " ov_gcolor = " + "'" + tbgcolor.getText() + "'" + "," +
                            " ov_bcolor = " + "'" + tbbcolor.getText() + "'" + "," +
                            " ov_fileservertype = " + "'" + serverfiletype + "'" + "," +
                            " ov_image_directory = " + "'" + tbimagedir.getText() + "'" + "," +
                            " ov_temp_directory = " + "'" + tbtempdir.getText() + "'" + "," +
                            " ov_label_directory = " + "'" + tblabeldir.getText() + "'" + "," +
                            " ov_jasper_directory = " + "'" + tbjasperdir.getText() + "'" + "," +
                            " ov_edi_directory = " + "'" + tbedidir.getText() + "'" + "," +
                            " ov_email_server = " + "'" + tbemailserver.getText() + "'" + "," +
                            " ov_email_from = " + "'" + tbemailfrom.getText() + "'" + "," +
                            " ov_smtpauthuser = " + "'" + tbsmtpuser.getText() + "'" + "," +
                            " ov_smtpauthpass = " + "'" + passwd + "'" +        
                            ";");   
                    }
                    // now do portion of sys_meta for attachments
                    // this needs to be modified for next major release to incorporate all directory assignments into sys_meta
                    // NEWRELEASEMARK
                    int j = 0;
                    res = st.executeQuery("SELECT *  FROM  sys_meta where sysm_id = 'system' and " +
                            " sysm_type = 'sysdir' AND sysm_key = 'attachment_directory';");
                    while (res.next()) {
                        j++;
                    }
                    if (j == 0) {
                      st.executeUpdate("insert into sys_meta values ( " +
                              " 'system', 'sysdir', 'attachment_directory', " + 
                              "'" + tbattachdir.getText() + "'" + " );");
                    } else {
                       st.executeUpdate("update sys_meta set sysm_value = " +
                              "'" + tbattachdir.getText() + "'" +
                              " where sysm_id = 'system' and sysm_type = 'sysdir' and sysm_key = 'attachment_directory' ; "); 
                    }
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                res = st.executeQuery("SELECT * FROM  ov_ctrl;");
                    while (res.next()) {
                        i++;
                        cblogin.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("ov_login")));
                        tbversion.setText(res.getString("ov_version"));
                        cbcustom.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("ov_custom")));
                        tbbgimage.setText(res.getString("ov_bgimage"));
                        tbrcolor.setText(res.getString("ov_rcolor"));
                        tbgcolor.setText(res.getString("ov_gcolor"));
                        tbbcolor.setText(res.getString("ov_bcolor"));
                        tbemailserver.setText(res.getString("ov_email_server"));
                        tbemailfrom.setText(res.getString("ov_email_from"));
                        tbsmtpuser.setText(res.getString("ov_smtpauthuser"));
                        tbsmtppass.setText(bsmf.MainFrame.PassWord("1", res.getString("ov_smtpauthpass").toCharArray()));
                        tbimagedir.setText(res.getString("ov_image_directory"));
                        tbtempdir.setText(res.getString("ov_temp_directory"));
                        tblabeldir.setText(res.getString("ov_label_directory"));
                        tbjasperdir.setText(res.getString("ov_jasper_directory"));
                        tbedidir.setText(res.getString("ov_edi_directory"));
                        
                        if (res.getString("ov_fileservertype").toString().toUpperCase().equals("S")) {
                            rbsamba.setSelected(true);
                        }
                        if (res.getString("ov_fileservertype").toString().toUpperCase().equals("W")) {
                            rbwin.setSelected(true);
                        }
                        if (res.getString("ov_fileservertype").toString().toUpperCase().equals("L")) {
                            rblocal.setSelected(true);
                        }
                    }
                    res = st.executeQuery("SELECT *  FROM  sys_meta where sysm_id = 'system' and " +
                            " sysm_type = 'sysdir' AND sysm_key = 'attachment_directory';");
                    while (res.next()) {
                        tbattachdir.setText(res.getString("sysm_value"));
                    }
               
                  tblocale.setText(Locale.getDefault().toString());
                    
                if (i > 0) {
                m[0] = "0";
                m[1] = BlueSeerUtils.getRecordSuccess;
                setAction(m);
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
        }
      return m;
    }
    
    public void setColors(String r, String g, String b) {
       // exhibits inconsistent behaviour for menu backgrounds...dont use
        Color backgroundcolor = new Color(Integer.valueOf(r),Integer.valueOf(g),Integer.valueOf(b));
        Window window = SwingUtilities.getWindowAncestor(this);
        bsmf.MainFrame frame = (bsmf.MainFrame) window;
        bsmf.MainFrame.backgroundcolor = backgroundcolor;
        this.setBackground(bsmf.MainFrame.backgroundcolor);
        //frame.setBackground(backgroundcolor);
        
    }
   
    // custom
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}
    
    public String[] getPatch() throws MalformedURLException, IOException {
        String[] m = new String[]{"",""};
        Path patch = null;
        String version = OVData.getVersion();
        String url = "https://github.com/blueseerERP/blueseer/releases/download/" +
                "v" + version + "/blueseer.patch.ver." + version + ".zip";
			
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        Path patchdir = FileSystems.getDefault().getPath(s + "/patches");

        String patchfile = "";
        if (Files.exists(patchdir) && Files.isDirectory(patchdir)) {
           patchfile = s + "/patches/patch.zip";
           patch = FileSystems.getDefault().getPath(patchfile); 
           ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(patch.toFile());
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
            
            if (Files.exists(patch)) {
                m = new String[]{"0", "patch downloaded...please close and restart"};
            } else {
                m = new String[]{"1", "unable to download patch"};
            }
        }	
	
        
        // now extract zip into patches directory
        String fileZip = patchfile;
        File destDir = new File(patchdir.toString());
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        String root = zipEntry.getName().split(Pattern.quote("\\"),-1)[0];
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create zip directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create zip directory " + parent);
                }
                
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        zipEntry = zis.getNextEntry();
       }
        zis.closeEntry();
        zis.close();
        
        // now leave file patch trigger for login.bat script
        if (Files.exists(patch)) {
        Files.write(Paths.get(".update"), root.getBytes(StandardCharsets.UTF_8));
        }
        
        return m;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        cblogin = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        tbversion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cbcustom = new javax.swing.JCheckBox();
        tbbgimage = new javax.swing.JTextField();
        tbrcolor = new javax.swing.JTextField();
        tbgcolor = new javax.swing.JTextField();
        tbbcolor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rblocal = new javax.swing.JRadioButton();
        rbwin = new javax.swing.JRadioButton();
        rbsamba = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        tbimagedir = new javax.swing.JTextField();
        tbtempdir = new javax.swing.JTextField();
        tblabeldir = new javax.swing.JTextField();
        tbjasperdir = new javax.swing.JTextField();
        tbedidir = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tbattachdir = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        panelCopySite = new javax.swing.JPanel();
        tbemailserver = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbemailfrom = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tbsmtpuser = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        tbsmtppass = new javax.swing.JPasswordField();
        jLabel14 = new javax.swing.JLabel();
        tblocale = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        btpatch = new javax.swing.JButton();
        btclean = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("System Control"));
        jPanel1.setName("panelmain"); // NOI18N

        cblogin.setText("Explicit Login?");
        cblogin.setName("cbexplicit"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Version");
        jLabel1.setName("lblversion"); // NOI18N

        cbcustom.setText("Custom?");
        cbcustom.setName("cbcustom"); // NOI18N

        jLabel2.setText("BG Image Path");
        jLabel2.setName("lblimagepath"); // NOI18N

        jLabel3.setText("R Color");
        jLabel3.setName("lblrcolor"); // NOI18N

        jLabel4.setText("G Color");
        jLabel4.setName("lblgcolor"); // NOI18N

        jLabel5.setText("B Color");
        jLabel5.setName("lblbcolor"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("File Server Type"));

        rblocal.setText("Local");
        rblocal.setName("cblocal"); // NOI18N

        rbwin.setText("Win UNC");
        rbwin.setName("cbwinunc"); // NOI18N

        rbsamba.setText("Samba");
        rbsamba.setName("cbsamba"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbsamba)
                    .addComponent(rbwin)
                    .addComponent(rblocal))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbsamba)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbwin)
                .addGap(1, 1, 1)
                .addComponent(rblocal)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Directory Configurations"));
        jPanel3.setName("paneldirectory"); // NOI18N

        jLabel8.setText("Image Dir");
        jLabel8.setName("lblimagedir"); // NOI18N

        jLabel9.setText("Temp Dir");
        jLabel9.setName("lbltempdir"); // NOI18N

        jLabel10.setText("Label Dir");
        jLabel10.setName("lbllabeldir"); // NOI18N

        jLabel11.setText("Jasper Dir");
        jLabel11.setName("lbljasperdir"); // NOI18N

        jLabel12.setText("EDI Dir");
        jLabel12.setName("lbledidir"); // NOI18N

        jLabel18.setText("Attachments Dir");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel8)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbattachdir)
                    .addComponent(tbedidir, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(tbjasperdir)
                    .addComponent(tblabeldir)
                    .addComponent(tbtempdir)
                    .addComponent(tbimagedir))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbimagedir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtempdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblabeldir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbjasperdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbedidir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbattachdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        panelCopySite.setBorder(javax.swing.BorderFactory.createTitledBorder("Email Configurations"));
        panelCopySite.setName("panelemail"); // NOI18N

        jLabel6.setText("EmailServerIP");
        jLabel6.setName("lblemailserverip"); // NOI18N

        jLabel7.setText("EmailFromAddr");
        jLabel7.setName("lblemailfrom"); // NOI18N

        jLabel13.setText("SMTP Auth User");
        jLabel13.setName("lblauthuser"); // NOI18N

        jLabel14.setText("SMTP Auth Pass");
        jLabel14.setName("lblauthpass"); // NOI18N

        javax.swing.GroupLayout panelCopySiteLayout = new javax.swing.GroupLayout(panelCopySite);
        panelCopySite.setLayout(panelCopySiteLayout);
        panelCopySiteLayout.setHorizontalGroup(
            panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopySiteLayout.createSequentialGroup()
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCopySiteLayout.createSequentialGroup()
                        .addComponent(tbsmtppass, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbsmtpuser, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(tbemailfrom)
                    .addComponent(tbemailserver))
                .addContainerGap())
        );
        panelCopySiteLayout.setVerticalGroup(
            panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopySiteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbemailserver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbemailfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsmtpuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsmtppass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel17.setText("Locale");
        jLabel17.setName("lbllocale"); // NOI18N

        btpatch.setText("Patch");
        btpatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpatchActionPerformed(evt);
            }
        });

        btclean.setText("Clean");
        btclean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcleanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbcustom)
                            .addComponent(cblogin)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbbcolor, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbgcolor, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrcolor, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbbgimage, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tblocale, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btupdate)
                                .addGap(86, 86, 86)
                                .addComponent(btclean)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btpatch))
                            .addComponent(panelCopySite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cblogin, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbversion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblocale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(cbcustom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tbbgimage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbrcolor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbgcolor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbbcolor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelCopySite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btupdate)
                            .addComponent(btpatch)
                            .addComponent(btclean)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        executeTask(dbaction.update, null);
    }//GEN-LAST:event_btupdateActionPerformed

    private void btpatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpatchActionPerformed
        setPanelComponentState(this, false);            
        executeTask(dbaction.run, null); 
    }//GEN-LAST:event_btpatchActionPerformed

    private void btcleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcleanActionPerformed
       bsmf.MainFrame.show(getMessageTag(1174));
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
          deleteNonMasterTransactionData();
          bsmf.MainFrame.show(getMessageTag(1125));
        }
    }//GEN-LAST:event_btcleanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclean;
    private javax.swing.JButton btpatch;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbcustom;
    private javax.swing.JCheckBox cblogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel panelCopySite;
    private javax.swing.JRadioButton rblocal;
    private javax.swing.JRadioButton rbsamba;
    private javax.swing.JRadioButton rbwin;
    private javax.swing.JTextField tbattachdir;
    private javax.swing.JTextField tbbcolor;
    private javax.swing.JTextField tbbgimage;
    private javax.swing.JTextField tbedidir;
    private javax.swing.JTextField tbemailfrom;
    private javax.swing.JTextField tbemailserver;
    private javax.swing.JTextField tbgcolor;
    private javax.swing.JTextField tbimagedir;
    private javax.swing.JTextField tbjasperdir;
    private javax.swing.JTextField tblabeldir;
    private javax.swing.JTextField tblocale;
    private javax.swing.JTextField tbrcolor;
    private javax.swing.JPasswordField tbsmtppass;
    private javax.swing.JTextField tbsmtpuser;
    private javax.swing.JTextField tbtempdir;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
