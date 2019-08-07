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
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vaughnte
 */
public class CustXrefMaintPanel extends javax.swing.JPanel {

    
    
    
    /**
     * Creates new form CustXrefMaintPanel
     */
    public CustXrefMaintPanel() {
        initComponents();
    }

    
      public void getXref(String cust, String custpart) {
        initvars("");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  cup_mstr left outer join cm_mstr on cm_code = cup_cust where " +
                    " cup_cust = " + "'" + cust + "'" + 
                    " AND cup_citem = " + "'" + custpart + "'" + ";") ;
                        
                while (res.next()) {
                    i++;
                    ddcust.setSelectedItem(res.getString("cup_cust"));
                     ddpart.setSelectedItem(res.getString("cup_item"));
                     custitem.setText(res.getString("cup_citem"));
                     custitem2.setText(res.getString("cup_citem2"));
                    skunbr.setText(res.getString("cup_sku"));
                    upcnbr.setText(res.getString("cup_upc"));
                    misc.setText(res.getString("cup_misc"));
                     btupdate.setEnabled(true);
                    btadd.setEnabled(false);
                    btdelete.setEnabled(true);
                   
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Customer Cross Reference found for " + cust + " / " + custpart);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve cup_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    
    
    
    public void initvars(String arg) {
        ArrayList mycust = OVData.getcustmstrlist();
        ddcust.removeAllItems();
        for (int i = 0; i < mycust.size(); i++) {
            ddcust.addItem(mycust.get(i));
        }
        ArrayList mypart = OVData.getItemMasterMCodelist();
        ddpart.removeAllItems();
        for (int i = 0; i < mypart.size(); i++) {
            ddpart.addItem(mypart.get(i));
        }
         if (ddcust.getItemCount()> 0) {        
         ddcust.setSelectedIndex(0);
         }
        if (ddpart.getItemCount() > 0) { 
        ddpart.setSelectedIndex(0);
        }
         custitem.setText("");
        skunbr.setText("");
        upcnbr.setText("");
        misc.setText("");
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
         ddcust.setEnabled(true);
        custitem.setEnabled(true);
         lblexists.setText("");
         lblexists.setForeground(Color.black);
         
         String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            getXref(args[0], args[1]);
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
        jLabel2 = new javax.swing.JLabel();
        skunbr = new javax.swing.JTextField();
        misc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        upcnbr = new javax.swing.JTextField();
        ddpart = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddcust = new javax.swing.JComboBox();
        btdelete = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        custitem = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        lblexists = new javax.swing.JLabel();
        custitem2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Cross Reference Maintenance"));

        jLabel2.setText("Cust Item");

        jLabel5.setText("UPC Number");

        jLabel4.setText("Sku Number");

        jLabel3.setText("CustCode");

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel1.setText("Internal Item");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel6.setText("Misc");

        custitem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                custitemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                custitemFocusLost(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel7.setText("Cust Item Alt");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addGap(47, 47, 47))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(custitem2)
                    .addComponent(misc, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                    .addComponent(ddcust, 0, 216, Short.MAX_VALUE)
                    .addComponent(upcnbr, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(skunbr, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ddpart, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(custitem)
                    .addComponent(lblexists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(custitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblexists, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(custitem2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skunbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upcnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(misc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
           
              res = st.executeQuery("select * from cup_mstr where cup_citem = " + 
                      "'" + custitem.getText().toString() + "'" +
                      " and cup_cust = " + "'" + ddcust.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show("Record already exists");
                    proceed = false;           
               }
             
                if (proceed) {
                    st.executeUpdate("insert into cup_mstr "
                        + "(cup_cust, cup_item, cup_citem, cup_citem2, cup_sku, cup_upc, cup_misc, cup_userid"
                        + " ) "
                        + " values ( " + "'" + ddcust.getSelectedItem() + "'" + ","
                        + "'" + ddpart.getSelectedItem() + "'" + ","
                        + "'" + custitem.getText() + "'" + ","
                        + "'" + custitem2.getText() + "'" + ","
                        + "'" + skunbr.getText() + "'" + ","
                        + "'" + upcnbr.getText() + "'" + ","
                        + "'" + misc.getText() + "'"  + ","
                        + "'" + bsmf.MainFrame.userid.toString() + "'"
                        + ")"
                        + ";");

                    
        
                    bsmf.MainFrame.show("Added Cust Xref Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Cust Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void custitemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_custitemFocusLost
      
        if (! custitem.getText().isEmpty())
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
           
              res = st.executeQuery("select * from cup_mstr where cup_citem = " + 
                      "'" + custitem.getText().toString() + "'" +
                      " and cup_cust = " + "'" + ddcust.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                  //  bsmf.MainFrame.show("Record exists");
                    lblexists.setText("Record Exists");
                   lblexists.setForeground(Color.red);
                    btadd.setEnabled(false);
                    ddpart.setSelectedItem(res.getString("cup_item"));
                    skunbr.setText(res.getString("cup_sku"));
                    upcnbr.setText(res.getString("cup_upc"));
                    misc.setText(res.getString("cup_misc"));
                    custitem2.setText(res.getString("cup_citem2"));
               }
             
               if (i == 0) {
                   // bsmf.MainFrame.show("Adding New Record");
                   lblexists.setText("Adding New Record");
                   lblexists.setForeground(Color.BLUE);
                   
                    btupdate.setEnabled(false);
                    btdelete.setEnabled(false);
                    ddcust.setEnabled(false);
                    custitem.setEnabled(false);
               }
               
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Retrieve Vend Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_custitemFocusLost

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
           
              st.executeUpdate("delete from cup_mstr where cup_citem = " + 
                      "'" + custitem.getText().toString() + "'" +
                      " and cup_cust = " + "'" + ddcust.getSelectedItem().toString() + "'" +
                      ";");
              bsmf.MainFrame.show("Record deleted");
              initvars("");
             
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Delete Cust Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
           
               
                    st.executeUpdate("update cup_mstr set "
                            + " cup_item = " + "'" + ddpart.getSelectedItem() + "'" + ","
                            + " cup_citem2 = " + "'" + custitem2.getText() + "'" + ","
                            + " cup_sku = "  + "'" + skunbr.getText() + "'" + ","
                            + " cup_upc = "  + "'" + upcnbr.getText() + "'" + ","
                            + " cup_misc = "  + "'" + misc.getText() + "'"  + ","
                            + " cup_userid = "  + "'" + bsmf.MainFrame.userid.toString() + "'"
                            + " where cup_cust = " + "'" + ddcust.getSelectedItem() + "'" 
                            + " and cup_citem = " + "'" + custitem.getText() + "'"
                        + ";");
        
                    bsmf.MainFrame.show("Updated Cust Xref Record");
                    initvars("");
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Update Cust Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void custitemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_custitemFocusGained
                    btupdate.setEnabled(true);
                    btadd.setEnabled(true);
                    btdelete.setEnabled(true);
                    ddcust.setEnabled(true);
                    custitem.setEnabled(true);
                   lblexists.setText("");
                   lblexists.setForeground(Color.black);
    }//GEN-LAST:event_custitemFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JTextField custitem;
    private javax.swing.JTextField custitem2;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblexists;
    private javax.swing.JTextField misc;
    private javax.swing.JTextField skunbr;
    private javax.swing.JTextField upcnbr;
    // End of variables declaration//GEN-END:variables
}
