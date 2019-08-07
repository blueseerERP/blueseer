/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
package com.blueseer.inv;

import bsmf.MainFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author vaughnte
 */
public class ImportItemMstr extends javax.swing.JPanel {

    /**
     * Creates new form FileOrderLoadPanel
     */
    public ImportItemMstr() {
        initComponents();
    }

    
      public void reinitddcustcode() {
        try {
            ddtargettable.removeAllItems();
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select cm_code from cm_mstr order by cm_code ;");
                while (res.next()) {
                    ddtargettable.addItem(res.getString("cm_code"));
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code reinitddcustcode");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void initvars(String arg) {
        
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

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = fc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            String mypath = bsmf.MainFrame.temp + "/" ;
            String myfile = bsmf.MainFrame.userid + "." + "csv";
            String OF = mypath + myfile;
            File srcDir = new File(SourceDir);
            File outfile = new File(OF);
            
            //FileUtils.copyDirectoryToDirectory(srcDir, destDir);
            
            // here we will validate the file depending on target table selection
            // if condition for each potential target
            BufferedReader fsr = new BufferedReader(new FileReader(srcDir));
            String line = "";
            boolean proceed = true;
            while ((line = fsr.readLine()) != null) {
               String[] recs = line.split(",");
            
               
               // item master  ...not all fields are required...here's the list
               // item, desc, uom, prodline, type, site, loc, code 
               if (recs.length != 8) {
                   bsmf.MainFrame.show("Wrong Column count for item master...must be 8 columns");
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
                String newline =
                                 recs[0].toString() + "," + 
                                 recs[1].toString() ;
                        
                fsw.write(newline + "\n");
                }
                fsw.close();
                fsr.close();
                
                // now lets ftp the file
              //  ftpfile(mypath, myfile);
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
        btupload = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddtargettable = new javax.swing.JComboBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("File Import Loader"));

        btupload.setText("Upload File");
        btupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btuploadActionPerformed(evt);
            }
        });

        jLabel1.setText("Target Table");

        ddtargettable.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ItemMaster" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddtargettable, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btupload)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtargettable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private javax.swing.JComboBox ddtargettable;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
