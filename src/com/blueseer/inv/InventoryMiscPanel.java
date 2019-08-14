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
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class InventoryMiscPanel extends javax.swing.JPanel {

    /**
     * Creates new form InventoryMiscPanel
     */
    public InventoryMiscPanel() {
        initComponents();
    }

    
    public void getiteminfo(String parentpart) {
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

               res = st.executeQuery("SELECT it_loc, it_site, it_wh  " +
                        " FROM  item_mstr  " +
                        " where it_item = " + "'" + parentpart.toString() + "'" + 
                        " ;");

                while (res.next()) {
                    i++;
                   ddsite.setSelectedItem(res.getString("it_site"));
                   ddloc.setSelectedItem(res.getString("it_loc"));
                   ddwh.setSelectedItem(res.getString("it_wh"));
              
                }
                
            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to get item_mstr info");
                 MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
    
    public void initvars(String[] arg) {
        
        ddtype.requestFocus();
       
        
        java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
       
        tbpart.setText("");
        tbqty.setText("");
        tbref.setText("");
        tbrmks.setText("");
        tblotserial.setText("");
        
        tbpart.setBackground(Color.white);
        tbqty.setBackground(Color.white);
        
        ArrayList<String> wh = new ArrayList();
        ddwh.removeAllItems();
        wh = OVData.getWareHouseList(); 
        for (String code : wh) {
            ddwh.addItem(code);
        }
        
       
         ArrayList<String> mylist = new ArrayList();
        ddloc.removeAllItems();
        if (ddwh.getSelectedItem() != null) {        
         mylist = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
         for (String code : mylist) {
            ddloc.addItem(code);
         }
        }
        
        
        
        ArrayList<String> sites = new ArrayList();
        ddsite.removeAllItems();
        sites = OVData.getSiteList();
        for (String code : sites) {
            ddsite.addItem(code);
        }
        
        ArrayList<String> accts = new ArrayList();
        ddacct.removeAllItems();
        accts = OVData.getGLAcctList();
        for (String code : accts) {
            ddacct.addItem(code);
        }
        
        ArrayList<String> ccs = new ArrayList();
        ddcc.removeAllItems();
        ccs = OVData.getGLCCList();
        for (String code : ccs) {
            ddcc.addItem(code);
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
        tbrmks = new javax.swing.JTextField();
        tbpart = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblcc = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tblotserial = new javax.swing.JTextField();
        tbref = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcdate = new com.toedter.calendar.JDateChooser();
        ddtype = new javax.swing.JComboBox();
        ddloc = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        ddcc = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        ddacct = new javax.swing.JComboBox();
        lblacct = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Adjustment (Issue / Receipt)"));

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        jLabel3.setText("Site:");

        lblcc.setText("cc:");

        jLabel2.setText("Item:");

        jLabel6.setText("EffDate");

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Serial/Lot:");

        jLabel5.setText("Qty:");

        dcdate.setDateFormatString("yyyy-MM-dd");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "issue", "receipt" }));
        ddtype.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddtypeItemStateChanged(evt);
            }
        });

        jLabel1.setText("Type:");

        jLabel4.setText("Location:");

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        lblacct.setText("Acct:");

        jLabel9.setText("Remarks:");

        jLabel8.setText("Reference:");

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel10.setText("Warehouse:");

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
                    .addComponent(lblacct)
                    .addComponent(lblcc)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btadd)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(ddwh, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddcc, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddacct, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbpart, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dcdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tblotserial, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbref, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblacct))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblcc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        boolean proceed = true;
        boolean isError = false;
        String type = "";
        String op = "";
        double qty = 0;
        double totalcost = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String loc = "";
        String wh = "";
        String acct = "";
        String cc = "";
        String site = "";
        
        if (ddloc.getSelectedItem() != null)
        loc = ddloc.getSelectedItem().toString();
        
        if (ddwh.getSelectedItem() != null)
        wh = ddwh.getSelectedItem().toString();
        
        if (ddacct.getSelectedItem() != null)
        acct = ddacct.getSelectedItem().toString();
        
        if (ddcc.getSelectedItem() != null)
        cc = ddcc.getSelectedItem().toString();
        
        if (ddsite.getSelectedItem() != null)
        site = ddsite.getSelectedItem().toString();
        
        
        if (tbpart.getText().isEmpty()) {
            tbpart.setBackground(Color.yellow);
            bsmf.MainFrame.show("Item cannot be blank");
            tbpart.requestFocus();
            return;
        }
        
        if (! tbqty.getText().isEmpty()) {
            qty = Double.valueOf(tbqty.getText());
        } else {
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show("Qty can not be blank");
            tbqty.requestFocus();
            return;
        }
        
         
        
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            type = "ISS-MISC";
            qty = (-1 * qty);
        } else {
            type = "RCT-MISC";
            qty = qty;
        }
        
        
        // all inventory transactions performed in base currency
        String basecurr = OVData.getDefaultCurrency();
        
        
        // check if item exists
        if (! OVData.isValidItem(tbpart.getText())) {
            proceed = false;
            bsmf.MainFrame.show("Invalid Item " + tbpart.getText());
            return;
        }
        
        // get cost
        double cost = OVData.getItemCost(tbpart.getText(), "standard", site);
        
        // lets get the productline of the part being adjusted
        String prodline = OVData.getProdLineFromItem(tbpart.getText());
        
        if ( prodline == null || prodline.isEmpty() ) {
            proceed = false;
            bsmf.MainFrame.show("No Product Line for Part " + tbpart.getText());
            return;
        }
        
        String invacct = OVData.getProdLineInvAcct(prodline);
        
        if (invacct.isEmpty()) {
            proceed = false;
            bsmf.MainFrame.show("No Inventory Account for Product Line " + prodline);
        }
        
        if (cost == 0.00) {
            proceed = false;
            bsmf.MainFrame.show("Item Standard Cost is zero " + prodline);
        }
        
        
        if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show("Period is closed");
                    return;
                }
        
        if (proceed) {
        // now lets do the tran_hist write
           isError = OVData.TRHistIssDiscrete(dcdate.getDate(), tbpart.getText(), qty, op, type, 0.00, cost, 
                site, loc, wh,
                "", "", "", 0, "", "", tblotserial.getText(), tbrmks.getText(), tbref.getText(), 
                acct, cc, "", "", "InventoryMiscPanel", bsmf.MainFrame.userid);
        
        if (! isError) {
            isError = OVData.UpdateInventoryDiscrete(tbpart.getText(), site, loc, wh, Double.valueOf(qty)); 
        } else {
            bsmf.MainFrame.show("Error during TRHistIssDiscrete");
        }
        
       
        if (! isError) {
            if (ddtype.getSelectedItem().toString().equals("issue")) {
                OVData.glEntry(invacct, prodline, acct, cc,  
                        dfdate.format(dcdate.getDate()), (cost * Double.valueOf(tbqty.getText())), (cost * Double.valueOf(tbqty.getText())), basecurr, basecurr, tbref.getText() , site, type, tbrmks.getText());
            } else {
                OVData.glEntry(ddacct.getSelectedItem().toString(), ddcc.getSelectedItem().toString(), invacct, prodline, 
                        dfdate.format(dcdate.getDate()), (cost * Double.valueOf(tbqty.getText())), (cost * Double.valueOf(tbqty.getText())), basecurr, basecurr, tbref.getText() , site, type, tbrmks.getText());
            }
        } else {
          bsmf.MainFrame.show("Error during UpdateInventoryDiscrete");  
        }
        if (! isError)
            bsmf.MainFrame.show("Adjustment Completed");
            else
            bsmf.MainFrame.show("Error during glentry");
        
        } // proceed
        
        
        
        
    }//GEN-LAST:event_btaddActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
        if (! tbpart.getText().isEmpty()) {
            if (! OVData.isValidItem(tbpart.getText())) {
                bsmf.MainFrame.show("Invalid Item " + tbpart.getText());
                tbpart.setBackground(Color.yellow);
                tbpart.requestFocus();
            } else {
              tbpart.setBackground(Color.white);
              getiteminfo(tbpart.getText());   
             }
        }
    }//GEN-LAST:event_tbpartFocusLost

    private void ddtypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddtypeItemStateChanged
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            lblacct.setText("Debit Acct:");
            lblcc.setText("Debit CC:");
        } else {
            lblacct.setText("Credit Acct:");
            lblcc.setText("Credit CC:");
        }
    }//GEN-LAST:event_ddtypeItemStateChanged

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
      if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcc;
    private javax.swing.JComboBox ddloc;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblacct;
    private javax.swing.JLabel lblcc;
    private javax.swing.JTextField tblotserial;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
