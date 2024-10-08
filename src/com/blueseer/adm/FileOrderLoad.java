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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author vaughnte
 */
public class FileOrderLoad extends javax.swing.JPanel {

    /**
     * Creates new form FileOrderLoadPanel
     */
    public FileOrderLoad() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void reinitddcustcode() {
        try {
            ddcustcode.removeAllItems();
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               

                res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
                while (res.next()) {
                    ddcustcode.addItem(res.getString("cm_code"));
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    
    public void initvars(String[] arg) {
        
        reinitddcustcode();
    }
    
    
    
    public void ftpfile(String mypath, String myfile) {
        
        try {
         FTPClient client = new FTPClient();
         client.connect("2.2.2.2");
         client.login("user", "passwd");
         client.enterLocalPassiveMode();
         client.setFileType(FTP.BINARY_FILE_TYPE);
         FileInputStream file = new FileInputStream(mypath + myfile);
         client.changeWorkingDirectory("/apps/edi/generic/inqueue");
         client.storeFile(myfile, file);
         client.logout();
         client.disconnect();
         if (file != null) {
	  file.close();
	 }
         bsmf.MainFrame.show("File has been FTP'd ");
        } catch (IOException e) {
            bsmf.MainFrame.show("Unable to FTP file ");
        }
         
    }
    
    public File getfile() {
        
        File file = null;
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(this);
        DateFormat dtf = new SimpleDateFormat(calduedate.getDateFormatString());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            String mypath = bsmf.MainFrame.temp + "/" ;
            String myfile = bsmf.MainFrame.userid + "." + tbpo.getText().toString() + ".csv";
            String OF = mypath + myfile;
            File srcDir = new File(SourceDir);
            File outfile = new File(OF);
            
            //FileUtils.copyDirectoryToDirectory(srcDir, destDir);
            
            BufferedReader fsr = new BufferedReader(new FileReader(srcDir, StandardCharsets.UTF_8));
            String line = "";
            boolean proceed = true;
            while ((line = fsr.readLine()) != null) {
               String[] recs = line.split(",");
               if (recs.length != 2) {
                   bsmf.MainFrame.show("Wrong Column count...two columns only separated by comma");
                   proceed = false;
                   break;
               }
             
            }
            fsr.close();
            
            if (proceed) {
                fsr = new BufferedReader(new FileReader(srcDir));
                BufferedWriter fsw = new BufferedWriter(new FileWriter(outfile));
                while ((line = fsr.readLine()) != null) {
                String[] recs = line.split(",");
                String newline = tbpo.getText().toString() + "," +
                                 ddcustcode.getSelectedItem().toString() + "," +
                                 ddcustcode.getSelectedItem().toString() + "," +
                                 dtf.format(calduedate.getDate()) + "," +
                                 "ODFL" + "," +
                                 "GENERIC" + "," +
                                 recs[0].toString() + "," + 
                                 recs[1].toString() ;
                        
                fsw.write(newline + "\n");
                }
                fsw.close();
                fsr.close();
                
                // now lets ftp the file
               // ftpfile(mypath, myfile);   // need to revisit this for more generic use TEV20210924
               outfile.delete();
            }
            
            
            
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
           
        } else {
           System.out.println("cancelled");
        }
        return file;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        calduedate = new com.toedter.calendar.JDateChooser();
        tbpo = new javax.swing.JTextField();
        btupload = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddcustcode = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel3.setText("PO");
        jLabel3.setName("lblpo"); // NOI18N

        calduedate.setDateFormatString("yyyyMMdd");

        btupload.setText("Upload File");
        btupload.setName("btupload"); // NOI18N
        btupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btuploadActionPerformed(evt);
            }
        });

        jLabel1.setText("BillTo Code");
        jLabel1.setName("lblcust"); // NOI18N

        jLabel4.setText("DueDate");
        jLabel4.setName("lblduedate"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btupload)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(calduedate, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(tbpo)
                            .addComponent(ddcustcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calduedate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(btupload)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btuploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btuploadActionPerformed
        getfile();
    }//GEN-LAST:event_btuploadActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupload;
    private com.toedter.calendar.JDateChooser calduedate;
    private javax.swing.JComboBox ddcustcode;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbpo;
    // End of variables declaration//GEN-END:variables
}
