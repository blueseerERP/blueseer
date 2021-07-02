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
package com.blueseer.edi;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class EDIControlPanel extends javax.swing.JPanel {

    /**
     * Creates new form EDIControlPanel
     */
    public EDIControlPanel() {
        initComponents();
    }

    public void copyPartnerDoc(String fromid, String fromdoc, String fromsndgs, String fromrcvgs, String toid, String tosndgs, String torcvgs) {
         try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT edi_id, edi_doc FROM  edi_mstr where " +
                                          " edi_id = " + "'" + toid + "'" +
                                          " AND edi_doc = " + "'" + fromdoc + "'" + 
                                          " AND edi_sndgs = " + "'" + tosndgs + "'" +
                                          " AND edi_rcvgs = " + "'" + torcvgs + "'" +        
                                          ";");
                    while (res.next()) {
                        i++;
                    }
                
                    if (i == 0) {
                    st.executeUpdate("insert into edi_mstr (edi_id, edi_doc, edi_sndisa, edi_sndq, edi_sndgs, edi_map, edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, edi_filesuffix, edi_filepath, edi_version, edi_rcvisa, edi_rcvgs, edi_rcvq, edi_supcode, edi_doctypeout, edi_filetypeout, edi_ifs, edi_ofs, edi_fa_required ) " + 
                            " select  " + "'" + toid + "'" + ", em.edi_doc, em.edi_sndisa, em.edi_sndq, " + "'" + tosndgs + "'" + ", em.edi_map, em.edi_eledelim, em.edi_segdelim, em.edi_subdelim, em.edi_fileprefix, em.edi_filesuffix, em.edi_filepath, em.edi_version, em.edi_rcvisa, " + "'" + torcvgs + "'" + ", em.edi_rcvq, em.edi_supcode, em.edi_doctypeout, em.edi_filetypeout, em.edi_ifs, em.edi_ofs, em.edi_fa_required from edi_mstr em " +
                            " where em.edi_id = " + "'" + fromid + "'" +
                            " and em.edi_doc = " + "'" + fromdoc + "'" +
                            " and em.edi_sndgs = " + "'" + fromsndgs + "'" +
                            " and em.edi_rcvgs = " + "'" + fromrcvgs + "'");
                    bsmf.MainFrame.show("Copy Complete");
                    } else {
                    bsmf.MainFrame.show("Record already exists with that Partner and DocType");    
                    }
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("problem copying edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void getdefault() {
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from edi_ctrl;");
                while (res.next()) {
                    i++;
                    tboutdir.setText(res.getString("edic_outdir"));
                    tbindir.setText(res.getString("edic_indir"));
                    tboutscript.setText(res.getString("edic_outftp"));
                    tbinarch.setText(res.getString("edic_inarch"));
                    tboutarch.setText(res.getString("edic_outarch"));
                    tbbatch.setText(res.getString("edic_batch"));
                    tbstructure.setText(res.getString("edic_structure"));
                    tberrordir.setText(res.getString("edic_errordir")); 
                    cbarchive.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edic_archyesno")));
                    cbdelete.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edic_delete")));
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No EDI Ctrl Record found");

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve edi_ctrl");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void initvars(String[] key) {
        getdefault();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tboutscript = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tboutdir = new javax.swing.JTextField();
        tbindir = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbinarch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tboutarch = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbarchive = new javax.swing.JCheckBox();
        tbbatch = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbdelete = new javax.swing.JCheckBox();
        tberrordir = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbstructure = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();

        jLabel6.setText("jLabel6");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Control Maintenance"));

        jLabel2.setText("Default In Dir");

        jLabel1.setText("Default Out Dir");

        jLabel3.setText("Default Out Script");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel4.setText("Inbound Archive");

        jLabel5.setText("Outbound Archive");

        cbarchive.setText("Archive?");

        jLabel7.setText("Batch Directory");

        cbdelete.setText("Delete (no archive)");

        jLabel8.setText("Error Directory");

        jLabel12.setText("IFS / OFS Dir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tboutscript, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbinarch, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(57, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbstructure)
                            .addComponent(tberrordir)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbdelete)
                                    .addComponent(cbarchive))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate))
                            .addComponent(tboutarch, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(tbbatch))
                        .addGap(12, 12, 12))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutscript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbinarch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutarch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tberrordir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbarchive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbdelete)
                    .addComponent(btupdate))
                .addContainerGap(243, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                res = st.executeQuery("SELECT *  FROM  edi_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                if (i == 0) {
                    
                    st.executeUpdate("insert into edi_ctrl values (" + "'" + tboutdir.getText().replace("\\","\\\\") + "'" + ","
                            + "'" + tbindir.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tboutscript.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tbinarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tboutarch.getText().replace("\\","\\\\") + "'" + ","
                            + "'" + tbbatch.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tbstructure.getText().replace("\\","\\\\") + "'" + ","          
                            + "'" + tberrordir.getText().replace("\\","\\\\") + "'" + ","         
                            + "'" + BlueSeerUtils.boolToInt(cbarchive.isSelected()) + "'" + ","      
                            + "'" + BlueSeerUtils.boolToInt(cbdelete.isSelected()) + "'"        
                            + ") ;");              
                          bsmf.MainFrame.show("Inserting Defaults");
                } else {
                    st.executeUpdate("update edi_ctrl set " 
                            + "edic_outdir = " + "'" + tboutdir.getText().replace("\\","\\\\") + "'" + ","
                            + "edic_indir = " + "'" + tbindir.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_inarch = " + "'" + tbinarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_outarch = " + "'" + tboutarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_batch = " + "'" + tbbatch.getText().replace("\\","\\\\") + "'" + ","   
                            + "edic_structure = " + "'" + tbstructure.getText().replace("\\","\\\\") + "'" + ","           
                            + "edic_errordir = " + "'" + tberrordir.getText().replace("\\","\\\\") + "'" + ","            
                            + "edic_delete = " + "'" + BlueSeerUtils.boolToInt(cbdelete.isSelected()) + "'" + ","         
                            + "edic_archyesno = " + "'" + BlueSeerUtils.boolToInt(cbarchive.isSelected()) + "'" + "," 
                            + "edic_outftp = " + "'" + tboutscript.getText() + "'" + ";");   
                    bsmf.MainFrame.show("Updated Defaults");
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating edi_ctrl");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbarchive;
    private javax.swing.JCheckBox cbdelete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbbatch;
    private javax.swing.JTextField tberrordir;
    private javax.swing.JTextField tbinarch;
    private javax.swing.JTextField tbindir;
    private javax.swing.JTextField tboutarch;
    private javax.swing.JTextField tboutdir;
    private javax.swing.JTextField tboutscript;
    private javax.swing.JTextField tbstructure;
    // End of variables declaration//GEN-END:variables
}
