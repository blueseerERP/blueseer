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
package com.blueseer.dst;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class WareHouseMaint extends javax.swing.JPanel {

    /**
     * Creates new form LocationMaintPanel
     */
    public WareHouseMaint() {
        initComponents();
    }

    public void getWareHouseCode(String mykey) {
        initvars(null);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from wh_mstr where wh_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    tbwh.setText(mykey);
                    tbname.setText(res.getString("wh_name"));
                    tbaddr1.setText(res.getString("wh_addr1"));
                    tbaddr2.setText(res.getString("wh_addr2"));
                    tbcity.setText(res.getString("wh_city"));
                    tbzip.setText(res.getString("wh_zip"));
                    ddsite.setSelectedItem(res.getString("wh_site"));
                    ddstate.setSelectedItem(res.getString("wh_state"));
                    ddcountry.setSelectedItem(res.getString("wh_country"));
                }
               
                if (i > 0) {
                   enableAll();
                   btadd.setEnabled(false);
                }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve wh_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void clearAll() {
         tbwh.setText("");
        tbname.setText("");
        tbaddr1.setText("");
        tbaddr2.setText("");
        tbcity.setText("");
        tbzip.setText("");
        
         
        if (ddstate.getItemCount() == 0)
       for (int i = 0; i < OVData.states.length; i++) {
            ddstate.addItem(OVData.states[i]);
        } else {
           ddstate.setSelectedIndex(0);
       }
       if (ddcountry.getItemCount() == 0)
       for (int i = 0; i < OVData.countries.length; i++) {
            ddcountry.addItem(OVData.countries[i]);
        }
       ddcountry.setSelectedItem("USA");
         
         
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
         
    }
    
    public void enableAll() {
        tbwh.setEnabled(true);
        tbname.setEnabled(true);
        tbaddr1.setEnabled(true);
        tbaddr2.setEnabled(true);
        tbcity.setEnabled(true);
        ddstate.setEnabled(true);
        ddcountry.setEnabled(true);
        tbzip.setEnabled(true);
         ddsite.setEnabled(true);
         btadd.setEnabled(true);
         btdelete.setEnabled(true);
         btupdate.setEnabled(true);
         btnew.setEnabled(true);
         btbrowse.setEnabled(true);
         
    }
    
    public void disableAll() {
        tbwh.setEnabled(false);
        tbname.setEnabled(false);
         tbaddr1.setEnabled(false);
        tbaddr2.setEnabled(false);
        tbcity.setEnabled(false);
        ddstate.setEnabled(false);
        ddcountry.setEnabled(false);
        tbzip.setEnabled(false);
         ddsite.setEnabled(false);
         btadd.setEnabled(false);
         btdelete.setEnabled(false);
         btupdate.setEnabled(false);
         btnew.setEnabled(false);
         btbrowse.setEnabled(false);
    }
    
    public void initvars(String[] arg) {
        
        clearAll();
        disableAll();
        
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
        
         if (arg != null && arg.length > 0) {
            getWareHouseCode(arg[0]);
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
        btadd = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbname = new javax.swing.JTextField();
        tbwh = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        tbaddr1 = new javax.swing.JTextField();
        tbaddr2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbcity = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddstate = new javax.swing.JComboBox();
        tbzip = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("WareHouse Maintenance"));

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel2.setText("Name");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        jLabel1.setText("WH ID");

        jLabel3.setText("Site");

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

        jLabel4.setText("Addr1");

        jLabel5.setText("Addr2");

        jLabel6.setText("City");

        jLabel7.setText("State");

        jLabel8.setText("Zip");

        jLabel9.setText("Country");

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
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(tbwh, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnew))
                        .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(125, 125, 125))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbaddr2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbaddr1)
                                .addComponent(tbcity, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(66, 66, 66))
                        .addComponent(tbzip)
                        .addComponent(ddstate, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ddcountry, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btdelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd)))
                        .addGap(44, 44, 44))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tbaddr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbaddr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete)))
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
                if (tbwh.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a Warehouse code");
                   
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT wh_id FROM  wh_mstr where wh_id = " + "'" + tbwh.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into wh_mstr "
                            + "(wh_id, wh_site, wh_name, wh_addr1, wh_addr2, wh_city, wh_state, wh_zip, wh_country ) "
                            + " values ( " + "'" + tbwh.getText().toString() + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + tbname.getText().toString() + "'" + ","
                            + "'" + tbaddr1.getText().toString() + "'" + ","
                            + "'" + tbaddr2.getText().toString() + "'" + ","
                            + "'" + tbcity.getText().toString() + "'" + ","
                            + "'" + ddstate.getSelectedItem().toString() + "'" + ","
                            + "'" + tbzip.getText().toString() + "'" + ","
                            + "'" + ddcountry.getSelectedItem().toString() + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Warehouse Record");
                    } else {
                        bsmf.MainFrame.show("Warehouse Record Already Exists");
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to wh_mstr");
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
                                   
                if (proceed) {
                    st.executeUpdate("update wh_mstr set wh_name = " + "'" + tbname.getText() + "'" + ","
                            + "wh_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "wh_addr1 = " + "'" + tbaddr1.getText() + "'" + ","
                            + "wh_addr2 = " + "'" + tbaddr2.getText() + "'" + ","
                            + "wh_city = " + "'" + tbcity.getText() + "'" + ","
                            + "wh_state = " + "'" + ddstate.getSelectedItem().toString() + "'" + ","
                            + "wh_zip = " + "'" + tbzip.getText() + "'" + ","
                            + "wh_country = " + "'" + ddcountry.getSelectedItem().toString() + "'" 
                            + " where wh_id = " + "'" + tbwh.getText() + "'"                             
                            + ";");
                    bsmf.MainFrame.show("Updated Warehouse");
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating wh_mstr");
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
              
                   int i = st.executeUpdate("delete from wh_mstr where wh_id = " + "'" + tbwh.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbwh.getText());
                    initvars(null);
                    }
                } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Warehouse Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "warehousemaint,wh_id");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       enableAll();
       clearAll();
       btupdate.setEnabled(false);
       btdelete.setEnabled(false);
       
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbaddr1;
    private javax.swing.JTextField tbaddr2;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbname;
    private javax.swing.JTextField tbwh;
    private javax.swing.JTextField tbzip;
    // End of variables declaration//GEN-END:variables
}
