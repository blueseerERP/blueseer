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

package com.blueseer.vdr;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.Color;
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
public class VendXrefMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form CustXrefMaintPanel
     */
    public VendXrefMaintPanel() {
        initComponents();
    }

    
    
       public void getXref(String vend, String vendpart) {
        initvars("");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  vdp_mstr left outer join vd_mstr on vd_addr = vdp_vend where " +
                    " vdp_vend = " + "'" + vend + "'" + 
                    " AND vdp_vitem = " + "'" + vendpart + "'" + ";") ;
                        
                while (res.next()) {
                    i++;
                    ddvend.setSelectedItem(res.getString("vdp_vend"));
                     ddpart.setSelectedItem(res.getString("vdp_item"));
                     venditem.setText(res.getString("vdp_vitem"));
                    skunbr.setText(res.getString("vdp_sku"));
                    upcnbr.setText(res.getString("vdp_upc"));
                    misc.setText(res.getString("vdp_misc"));
                    
                    btupdate.setEnabled(true);
                    btadd.setEnabled(false);
                    btdelete.setEnabled(true);
                    
                   
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Vendor Cross Reference found for " + vend + " / " + vendpart);

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
        ArrayList myvend = OVData.getvendmstrlist();
        ddvend.removeAllItems();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
        ArrayList mypart = OVData.getItemMasterRawlist();
        ddpart.removeAllItems();
        for (int i = 0; i < mypart.size(); i++) {
            ddpart.addItem(mypart.get(i));
        }
         venditem.setText("");
        skunbr.setText("");
        upcnbr.setText("");
        misc.setText("");
        ddvend.setEnabled(true);
        venditem.setEnabled(true);
         lblexists.setText("");
         lblexists.setForeground(Color.black);
         
            String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            getXref(args[0], args[1]);
        }
         
    }
    
     public void reinitvars() {
        ddvend.setSelectedIndex(0);
        ddpart.setSelectedIndex(0);
        venditem.setText("");
        skunbr.setText("");
        upcnbr.setText("");
        misc.setText("");
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
         ddvend.setEnabled(true);
        venditem.setEnabled(true);
         lblexists.setText("");
         lblexists.setForeground(Color.black);
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
        ddvend = new javax.swing.JComboBox();
        btdelete = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        venditem = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        lblexists = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor Cross Reference Maintenance"));

        jLabel2.setText("Vendor Item");

        jLabel5.setText("UPC Number");

        jLabel4.setText("Sku Number");

        jLabel3.setText("VendCode");

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

        venditem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                venditemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                venditemFocusLost(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(misc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddvend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(upcnbr, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(skunbr, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ddpart, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(venditem)
                    .addComponent(lblexists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addGap(47, 47, 47))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(venditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblexists, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(15, 15, 15)
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
                .addContainerGap(34, Short.MAX_VALUE))
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
           
              res = st.executeQuery("select * from vdp_mstr where vdp_vitem = " + 
                      "'" + venditem.getText().toString() + "'" +
                      " and vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show("Record already exists");
                    proceed = false;           
               }
             
                if (proceed) {
                    st.executeUpdate("insert into vdp_mstr "
                        + "(vdp_vend, vdp_item, vdp_vitem, vdp_sku, vdp_upc, vdp_misc, vdp_userid"
                        + " ) "
                        + " values ( " + "'" + ddvend.getSelectedItem() + "'" + ","
                        + "'" + ddpart.getSelectedItem() + "'" + ","
                        + "'" + venditem.getText() + "'" + ","
                        + "'" + skunbr.getText() + "'" + ","
                        + "'" + upcnbr.getText() + "'" + ","
                        + "'" + misc.getText() + "'"  + ","
                        + "'" + bsmf.MainFrame.userid.toString() + "'"
                        + ")"
                        + ";");

                    
        
                    bsmf.MainFrame.show("Added Vend Xref Record");
                    reinitvars();
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Vend Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void venditemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_venditemFocusLost
       if (! venditem.getText().isEmpty())
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
           
              res = st.executeQuery("select * from vdp_mstr where vdp_vitem = " + 
                      "'" + venditem.getText().toString() + "'" +
                      " and vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                  //  bsmf.MainFrame.show("Record exists");
                    lblexists.setText("Record Exists");
                   lblexists.setForeground(Color.red);
                    btadd.setEnabled(false);
                    ddpart.setSelectedItem(res.getString("vdp_item"));
                    skunbr.setText(res.getString("vdp_sku"));
                    upcnbr.setText(res.getString("vdp_upc"));
                    misc.setText(res.getString("vdp_misc"));
               }
             
               if (i == 0) {
                   // bsmf.MainFrame.show("Adding New Record");
                   lblexists.setText("Adding New Record");
                   lblexists.setForeground(Color.BLUE);
                   
                    btupdate.setEnabled(false);
                    btdelete.setEnabled(false);
                    ddvend.setEnabled(false);
                    venditem.setEnabled(false);
               }
               
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Retrieve Vend Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_venditemFocusLost

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
           
              st.executeUpdate("delete from vdp_mstr where vdp_vitem = " + 
                      "'" + venditem.getText().toString() + "'" +
                      " and vdp_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" +
                      ";");
              bsmf.MainFrame.show("Record deleted");
              reinitvars();
             
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Delete Vend Xref Record");
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
           
               
                    st.executeUpdate("update vdp_mstr set "
                            + " vdp_item = " + "'" + ddpart.getSelectedItem() + "'" + ","
                            + " vdp_sku = "  + "'" + skunbr.getText() + "'" + ","
                            + " vdp_upc = "  + "'" + upcnbr.getText() + "'" + ","
                            + " vdp_misc = "  + "'" + misc.getText() + "'"  + ","
                            + " vdp_userid = "  + "'" + bsmf.MainFrame.userid.toString() + "'"
                            + " where vdp_vend = " + "'" + ddvend.getSelectedItem() + "'" 
                            + " and vdp_vitem = " + "'" + venditem.getText() + "'"
                        + ";");
        
                    bsmf.MainFrame.show("Updated Vend Xref Record");
                    reinitvars();
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Update Vend Xref Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void venditemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_venditemFocusGained
                    btupdate.setEnabled(true);
                    btadd.setEnabled(true);
                    btdelete.setEnabled(true);
                    ddvend.setEnabled(true);
                    venditem.setEnabled(true);
                   lblexists.setText("");
                   lblexists.setForeground(Color.black);
    }//GEN-LAST:event_venditemFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddpart;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblexists;
    private javax.swing.JTextField misc;
    private javax.swing.JTextField skunbr;
    private javax.swing.JTextField upcnbr;
    private javax.swing.JTextField venditem;
    // End of variables declaration//GEN-END:variables
}
