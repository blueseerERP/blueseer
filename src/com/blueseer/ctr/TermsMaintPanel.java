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

package com.blueseer.ctr;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author vaughnte
 */
public class TermsMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form CarrierMaintPanel
     */
    public TermsMaintPanel() {
        initComponents();
    }

    public void getTermsCode(String mykey) {
        initvars(null);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from cust_term where cut_code = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    tbcode.setText(mykey);
                    tbdesc.setText(res.getString("cut_desc"));
                    duedays.setText(res.getString("cut_days"));
                    discduedays.setText(res.getString("cut_discdays"));
                    discpercent.setText(res.getString("cut_discpercent"));
                   
                }
               
                if (i > 0) {
                    enableAll();
                    tbcode.setEnabled(false);
                    btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve term");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    
   
    
    public boolean isValidInput() {
        boolean proceed = true;
        
        
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a code");
                }
                
                if (! BlueSeerUtils.isParsableToInt(duedays.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a zero or integer in Due Days");
                }
                
                if (! BlueSeerUtils.isParsableToInt(discduedays.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a zero or integer in Disc Due Days");
                }
                
                if (! BlueSeerUtils.isParsableToInt(discpercent.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a zero or integer in Disc Percent");
                }
                
        
        
        return proceed;
    }
    
    
    public void clearAll() {
        tbcode.setText("");
        tbdesc.setText("");
        duedays.setText("");
        discduedays.setText("");
        discpercent.setText("");
    }
    
    public void enableAll() {
        tbcode.setEnabled(true);
        tbdesc.setEnabled(true);
        duedays.setEnabled(true);
        discduedays.setEnabled(true);
        discpercent.setEnabled(true);
        
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
    }
    
    public void disableAll() {
       tbcode.setEnabled(false);
        tbdesc.setEnabled(false);
        duedays.setEnabled(false);
        discduedays.setEnabled(false);
        discpercent.setEnabled(false);
        
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false); 
    }
    
      public void initvars(String[] arg) {
        
          clearAll();
          disableAll();
          btnew.setEnabled(true);
          btbrowse.setEnabled(true);
        
       if (arg != null && arg.length > 0) {
            getTermsCode(arg[0]);
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
        tbcode = new javax.swing.JTextField();
        duedays = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        discduedays = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        discpercent = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Terms Maintenance"));

        jLabel1.setText("Terms Code:");

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

        jLabel3.setText("Due Days:");

        jLabel4.setText("Disc Due Days:");

        jLabel5.setText("Disc Percent%:");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(discpercent, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(discduedays, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew))
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(duedays, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(duedays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discduedays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discpercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                
                if (! OVData.checkIfRecordExists("cust_term", "cut_code", tbcode.getText()))
                    return;
                
                if (! isValidInput())
                    return;
                
                        st.executeUpdate("insert into cust_term "
                            + "(cut_code, cut_desc, cut_days, cut_discdays, cut_discpercent ) "
                            + " values ( " + "'" + tbcode.getText().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString() + "'" + ","
                            + "'" + duedays.getText().toString() + "'" + ","
                            + "'" + discduedays.getText().toString() + "'" + ","
                            + "'" + discpercent.getText().toString() + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Terms Record");
                   initvars(null);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to cust_term");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       try {
           
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
               if (! isValidInput())
                    return;
                    st.executeUpdate("update cust_term set cut_desc = " + "'" + tbdesc.getText() + "'" + ","
                            + "cut_days = " + "'" + duedays.getText() + "'" + ","
                            + "cut_discdays = " + "'" + discduedays.getText() + "'" + ","
                            + "cut_discpercent = " + "'" + discpercent.getText() + "'" 
                            + " where cut_code = " + "'" + tbcode.getText() + "'"                             
                            + ";");
                    bsmf.MainFrame.show("Updated Terms");
                    initvars(null);
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating cust_term");
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
                ResultSet res = null;
                int k = 0;
                    res = st.executeQuery("SELECT cm_code FROM  cm_mstr where cm_terms = " + "'" + tbcode.getText() + "'" + ";");
                    while (res.next()) {
                        k++;
                    }
                    res = st.executeQuery("SELECT vd_addr FROM  vd_mstr where vd_terms = " + "'" + tbcode.getText() + "'" + ";");
                    while (res.next()) {
                        k++;
                    }
                    if (k > 0) {
                        bsmf.MainFrame.show("Cannot delete terms code until removed from all customers and vendors");
                        return;
                    }
                
                
                   int i = st.executeUpdate("delete from cust_term where cut_code = " + "'" + tbcode.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbcode.getText());
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Terms Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "termmaint,cut_code");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        tbcode.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JTextField discduedays;
    private javax.swing.JTextField discpercent;
    private javax.swing.JTextField duedays;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbdesc;
    // End of variables declaration//GEN-END:variables
}
