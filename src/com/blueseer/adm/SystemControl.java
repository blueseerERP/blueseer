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
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Window;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
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
          
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
      
       Task z = new Task(x, y); 
       z.execute(); 
       
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
        
        ddfromsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddfromsite.addItem(code);
        }
        ddfromsite.setSelectedItem(OVData.getDefaultSite());
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
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
        }
        return m;
    }
    
    public boolean validateInput(String x) {
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
            executeTask("get", null);
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     String passwd = bsmf.MainFrame.PassWord("0", tbsmtppass.getPassword());
     
     try {
           
            Connection con = DriverManager.getConnection(url + db, user, pass);
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

            Connection con = DriverManager.getConnection(url + db, user, pass);
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
               
                  tblocale.setText(Locale.getDefault().toString());
                    
                // set Action if Record found (i > 0)
                m = setAction(i);
                
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
        tbemailserver = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbemailfrom = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
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
        tbsmtpuser = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tbsmtppass = new javax.swing.JPasswordField();
        panelCopySite = new javax.swing.JPanel();
        tbtosite = new javax.swing.JTextField();
        ddfromsite = new javax.swing.JComboBox<>();
        btcopy = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tblocale = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();

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

        jLabel6.setText("EmailServerIP");
        jLabel6.setName("lblemailserverip"); // NOI18N

        jLabel7.setText("EmailFromAddr");
        jLabel7.setName("lblemailfrom"); // NOI18N

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

        jLabel13.setText("SMTP Auth User");
        jLabel13.setName("lblauthuser"); // NOI18N

        jLabel14.setText("SMTP Auth Pass");
        jLabel14.setName("lblauthpass"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbedidir, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addComponent(tbjasperdir)
                    .addComponent(tblabeldir)
                    .addComponent(tbtempdir)
                    .addComponent(tbimagedir)
                    .addComponent(tbemailserver)
                    .addComponent(tbemailfrom)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbsmtpuser, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(tbsmtppass))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemailserver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemailfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbsmtpuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(tbsmtppass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addContainerGap(31, Short.MAX_VALUE))
        );

        panelCopySite.setBorder(javax.swing.BorderFactory.createTitledBorder("Copy Site"));
        panelCopySite.setName("panelcopy"); // NOI18N

        btcopy.setText("Copy");
        btcopy.setName("btcopy"); // NOI18N
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        jLabel15.setText("From Site");
        jLabel15.setName("lblfromsite"); // NOI18N

        jLabel16.setText("To Site");
        jLabel16.setName("lbltosite"); // NOI18N

        javax.swing.GroupLayout panelCopySiteLayout = new javax.swing.GroupLayout(panelCopySite);
        panelCopySite.setLayout(panelCopySiteLayout);
        panelCopySiteLayout.setHorizontalGroup(
            panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopySiteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ddfromsite, 0, 110, Short.MAX_VALUE)
                    .addComponent(btcopy)
                    .addComponent(tbtosite))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCopySiteLayout.setVerticalGroup(
            panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCopySiteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddfromsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCopySiteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtosite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(10, 10, 10)
                .addComponent(btcopy)
                .addContainerGap())
        );

        jLabel17.setText("Locale");
        jLabel17.setName("lbllocale"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCopySite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btupdate))
                .addGap(42, 42, 42))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(panelCopySite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(btupdate)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput("updateRecord")) {
           return;
       }
        executeTask("update", null);
    }//GEN-LAST:event_btupdateActionPerformed

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
       boolean r = OVData.copySite(ddfromsite.getSelectedItem().toString(), tbtosite.getText());
       if (r == true) {
           bsmf.MainFrame.show(getMessageTag(1065));
           initvars(null);
       } else {
           bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
       }
    }//GEN-LAST:event_btcopyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbcustom;
    private javax.swing.JCheckBox cblogin;
    private javax.swing.JComboBox<String> ddfromsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JTextField tbtosite;
    private javax.swing.JTextField tbversion;
    // End of variables declaration//GEN-END:variables
}
