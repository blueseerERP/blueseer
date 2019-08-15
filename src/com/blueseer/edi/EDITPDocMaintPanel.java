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
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class EDITPDocMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form CarrierMaintPanel
     */
    public EDITPDocMaintPanel() {
        initComponents();
    }

    
     public boolean isFile(String myfile) {
         // lets check and see if class exists in package
       boolean isgood = false;
       
       try {
           Class.forName(myfile);
           isgood = true;
           
       } catch( ClassNotFoundException e ) {
           isgood = false;
        //my class isn't there!
       }
       
        return isgood;
    }
    
    
    public void getTPMAP(String tpid, String doctype) {
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                
                res = st.executeQuery("select * from editp_mstr where editp_id = " + "'" + tpid + "'" +  ";");
                    while (res.next()) {
                    i++;
                    }
                
                    if (i == 0) {
                        bsmf.MainFrame.show("TP ID Master record does not exist");
                           disableAll();
                        return;
                    }
                    
                i = 0;    
                res = st.executeQuery("select * from edi_mstr where edi_id = " + "'" + tpid + "'" +
                                      " AND edi_doctype = " + "'" + doctype + "'" + ";");
                while (res.next()) {
                    i++;
                    tbmap.setText(res.getString("edi_map"));
                    tbdesc.setText(res.getString("edi_desc"));
                    ddtp.setSelectedItem(res.getString("edi_id"));
                    cbisFA.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edi_fa_required")));
                    dddoc.setSelectedItem(res.getString("edi_doctype"));
                }
                
                
                if (i > 0) {
                    enableAll();
                btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void clearAll() {
         ddtp.removeAllItems();
         cbisFA.setSelected(false);
        ArrayList<String> mylist = OVData.getEDIUniqueTPID();
        for (int i = 0; i < mylist.size(); i++) {
            ddtp.addItem(mylist.get(i).toUpperCase());
        }  
        
        dddoc.removeAllItems();
        mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
        }
        
        tbdesc.setText("");
        tbmap.setText("");
    }
    public void enableAll() {
         ddtp.setEnabled(true);
        dddoc.setEnabled(true);
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btbrowse.setEnabled(true);
        tbdesc.setEnabled(true);
        tbmap.setEnabled(true);
        cbisFA.setEnabled(true);
    }
    public void disableAll() {
         ddtp.setEnabled(false);
        dddoc.setEnabled(false);
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
        tbdesc.setEnabled(false);
        tbmap.setEnabled(false);
        cbisFA.setEnabled(false);
    }
    
      public void initvars(String[] arg) {
        
       clearAll();
       disableAll();
       btbrowse.setEnabled(true);
       btnew.setEnabled(true);
        
         if (arg != null && arg.length > 1) {
            getTPMAP(arg[0], arg[1]);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tbmap = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ddtp = new javax.swing.JComboBox();
        dddoc = new javax.swing.JComboBox();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        cbisFA = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI TP/DOC Maintenance"));

        jLabel1.setText("TP ID:");

        jLabel2.setText("Description:");

        btdelete.setText("delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel3.setText("DocType:");

        jLabel4.setText("Map Name:");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        cbisFA.setText("Functional Acknowledgment?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbisFA)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbmap, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddtp, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnew)
                        .addComponent(btbrowse)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                .addComponent(cbisFA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                // check the site field
                if (tbmap.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a map name");
                    return;
                }
                 // if (! isFile("EDIMaps." + tbmap.getText())) {
                 if (! isFile(tbmap.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Map does not exist");
                    return;
                }
                
                
                if (proceed) {

                    res = st.executeQuery("SELECT edi_id FROM  edi_mstr where edi_id = " + "'" + ddtp.getSelectedItem().toString() + "'" +
                                          " AND edi_doctype = " + "'" + dddoc.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into edi_mstr "
                            + "(edi_id, edi_desc, edi_doctype, edi_fa_required, edi_map ) "
                            + " values ( " + "'" + ddtp.getSelectedItem().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString() + "'" + ","
                                + "'" + dddoc.getSelectedItem().toString() + "'" + ","
                                + "'" + BlueSeerUtils.boolToInt(cbisFA.isSelected()) + "'" + ","        
                                + "'" + tbmap.getText().toString() + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added TPID / DOC Record");
                    } else {
                        bsmf.MainFrame.show("TPID / DOC Already Exists");
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       try {
            boolean proceed = true;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
             
                if (tbmap.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a TP ID");
                }
                
                if (! isFile(tbmap.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Map does not exist");
                    return;
                }
                
                if (proceed) {
                    st.executeUpdate("update edi_mstr set edi_desc = " + "'" + tbdesc.getText() + "'" + ","
                            + "edi_fa_required = " + "'" + BlueSeerUtils.boolToInt(cbisFA.isSelected()) + "'" + ","
                            + "edi_map = " + "'" + tbmap.getText() + "'"
                            + " where edi_id = " + "'" + ddtp.getSelectedItem().toString() + "'"     
                            + " AND edi_doctype = " + "'" + dddoc.getSelectedItem().toString() + "'"
                            + ";");
                    bsmf.MainFrame.show("Updated TPID / DOC");
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from edi_mstr where edi_id = " + "'" + ddtp.getSelectedItem().toString() + "'" + 
                                            " and edi_doctype = " + "'" + dddoc.getSelectedItem().toString() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + ddtp.getSelectedItem().toString() + "/" + dddoc.getSelectedItem().toString());
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete edi_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"editpdocmaint","edi_id"});
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        clearAll();
        enableAll();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbisFA;
    private javax.swing.JComboBox dddoc;
    private javax.swing.JComboBox ddtp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbmap;
    // End of variables declaration//GEN-END:variables
}
