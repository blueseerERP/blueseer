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

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;

/**
 *
 * @author vaughnte
 */
public class LedgerAcctMstrPanel extends javax.swing.JPanel {

    /**
     * Creates new form LedgerAcctMstrPanel
     */
    public LedgerAcctMstrPanel() {
        initComponents();
    }

    
     public void getAcctCode(String mykey) {
        initvars(null);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from ac_mstr where ac_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    tbdesc.setText(res.getString("ac_desc"));
                    tbacct.setText(res.getString("ac_id"));
                    ddcur.setSelectedItem(res.getString("ac_cur"));
                    cbdisplay.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("ac_display")));
                    ddtype.setSelectedItem(res.getString("ac_type"));
                    
                }
               
                if (i > 0) {
                    enableAll();
                    tbacct.setEnabled(false);
                    btadd.setEnabled(false);
                }
                    

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve ac_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
     
    public void clearAll() {
        tbdesc.setText("");
        tbacct.setText("");
        lbaccountname.setText("");
        cbdisplay.setSelected(false);
        ddcur.removeAllItems();
        ArrayList mycode = OVData.getCurrlist();
        for (int i = 0; i < mycode.size(); i++) {
            ddcur.addItem(mycode.get(i));
        }
        ddcur.setSelectedItem(OVData.getDefaultCurrency());
    } 
     
    public void enableAll() {
        tbacct.setEnabled(true);
        tbdesc.setEnabled(true);
        ddtype.setEnabled(true);
        cbdisplay.setEnabled(true);
        ddcur.setEnabled(true);
        btacctbrowse.setEnabled(true);
        btdescbrowse.setEnabled(true);
        btadd.setEnabled(true);
        btedit.setEnabled(true);
        btdelete.setEnabled(true);
    }
    
    public void disableAll() {
        tbacct.setEnabled(false);
        tbdesc.setEnabled(false);
        ddtype.setEnabled(false);
        ddcur.setEnabled(false);
        cbdisplay.setEnabled(false);
        btacctbrowse.setEnabled(false);
        btdescbrowse.setEnabled(false);
        btadd.setEnabled(false);
        btedit.setEnabled(false);
        btdelete.setEnabled(false);
    }
    public void initvars(String[] arg) {
        
        clearAll();
        disableAll();
        
        btnew.setEnabled(true);
        btacctbrowse.setEnabled(true);
        btdescbrowse.setEnabled(true);
        
        ddtype.setSelectedItem("E");
        
         if (arg != null && arg.length > 0) {
            getAcctCode(arg[0]);
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
        ddcur = new javax.swing.JComboBox();
        ddtype = new javax.swing.JComboBox();
        btedit = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbacct = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btacctbrowse = new javax.swing.JButton();
        btdescbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        lbaccountname = new javax.swing.JLabel();
        cbdisplay = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Maintenance"));

        jLabel1.setText("Account");

        jLabel2.setText("Desc");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "E", "I", "L", "O", "M" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel3.setText("Type");

        jLabel4.setText("Currency");

        btacctbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btacctbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btacctbrowseActionPerformed(evt);
            }
        });

        btdescbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btdescbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdescbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        cbdisplay.setText("Show this account in Expense DropDowns");
        cbdisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbdisplayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdescbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btacctbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ddcur, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbdisplay))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btedit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd))
                            .addComponent(lbaccountname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btacctbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btdescbrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lbaccountname, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(5, 5, 5)
                .addComponent(cbdisplay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btdelete))
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

                
                if (Integer.valueOf(tbacct.getText().toString()) >= 99000000 && Integer.valueOf(tbacct.getText().toString()) <= 99999999) {
                    proceed = false;
                     bsmf.MainFrame.show("Account numbers between 99000000 and 99999999 are system reserved");
                    return;
                } 
                
                
                if (proceed) {

                    res = st.executeQuery("SELECT ac_id FROM  ac_mstr where ac_id = " + "'" + tbacct.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into ac_mstr "
                            + "( ac_id, ac_desc, ac_type, ac_cur, ac_display ) "
                            + " values ( " + "'" + tbacct.getText().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString().replace("'", "") + "'" + ","
                            + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcur.getSelectedItem().toString() + "'" + ","
                            + "'" + BlueSeerUtils.boolToInt(cbdisplay.isSelected()) + "'"        
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Acct Number");
                        initvars(null);
                    } else {
                        bsmf.MainFrame.show("Acct Number Already Exists");
                    }

                    //reinitapmvariables();
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to insert into ac_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (proceed) {

                        st.executeUpdate("update ac_mstr set "
                            + " ac_desc = " + "'" + tbdesc.getText().toString().replace("'", "") + "'" + ","
                            + " ac_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                            + " ac_cur = " + "'" + ddcur.getSelectedItem().toString() + "'" + ","
                            + " ac_display = " + "'" + BlueSeerUtils.boolToInt(cbdisplay.isSelected()) + "'"         
                            + " where ac_id = " + "'" + tbacct.getText().toString() + "'" 
                            + ";");
                        bsmf.MainFrame.show("Edited Acct Number");
                    

                    //reinitapmvariables();
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to edit ac_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btacctbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btacctbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"acctmaint","ac_id"});
    }//GEN-LAST:event_btacctbrowseActionPerformed

    private void btdescbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdescbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"acctmaint","ac_desc"});
    }//GEN-LAST:event_btdescbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btedit.setEnabled(false);
        btnew.setEnabled(false);
        btacctbrowse.setEnabled(false);
        btdescbrowse.setEnabled(false);
        tbacct.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
            try {

                Class.forName(bsmf.MainFrame.driver).newInstance();
                bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
                try {
                    Statement st = bsmf.MainFrame.con.createStatement();

                    int i = st.executeUpdate("delete from ac_mstr where ac_id = " + "'" + tbacct.getText() + "'" + ";");
                    if (i > 0) {
                        bsmf.MainFrame.show("deleted account record " + tbacct.getText());
                        initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Unable to Delete Account Record");
                }
                bsmf.MainFrame.con.close();
            } catch (Exception e) {
                MainFrame.bslog(e);
            }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
        if (ddtype.getSelectedItem().toString().equals("E")) {
            lbaccountname.setText("Expense Account");
        } else if (ddtype.getSelectedItem().toString().equals("A")) {
            lbaccountname.setText("Asset Account");
        } else if (ddtype.getSelectedItem().toString().equals("I")) {
            lbaccountname.setText("Income Account");
        } else if (ddtype.getSelectedItem().toString().equals("L")) {
            lbaccountname.setText("Liability Account");
        } else if (ddtype.getSelectedItem().toString().equals("O")) {
            lbaccountname.setText("Owners Equity Account");
        } else if (ddtype.getSelectedItem().toString().equals("M")) {
            lbaccountname.setText("Miscellaneous Account");    
        } else {
            lbaccountname.setText("Uknown Account Type");
        }
    }//GEN-LAST:event_ddtypeActionPerformed

    private void cbdisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbdisplayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbdisplayActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btacctbrowse;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdescbrowse;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JCheckBox cbdisplay;
    private javax.swing.JComboBox ddcur;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbaccountname;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbdesc;
    // End of variables declaration//GEN-END:variables
}
