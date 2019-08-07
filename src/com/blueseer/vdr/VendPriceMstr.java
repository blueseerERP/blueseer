/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.vdr;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class VendPriceMstr extends javax.swing.JPanel {

    DefaultListModel listmodel = new DefaultListModel();
    DefaultListModel pricelistmodel = new DefaultListModel();
    
    /**
     * Creates new form CustXrefMaintPanel
     */
    public VendPriceMstr() {
        initComponents();
    }

    
      public void getVendPriceRecord(String vend, String part, String uom, String curr) {
        initvars("");
        try {

            DecimalFormat df = new DecimalFormat("#0.0000");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  vpr_mstr where " +
                    " vpr_vend = " + "'" + vend + "'" + 
                    " AND vpr_item = " + "'" + part + "'" +
                    " AND vpr_uom = " + "'" + uom + "'" +
                    " AND vpr_curr = " + "'" + curr + "'" +        
                        ";") ;
                        
                while (res.next()) {
                    i++;
                  
                    ddvendcode.setSelectedItem(res.getString("vpr_vend"));
                     ddpart.setSelectedItem(res.getString("vpr_item"));
                     dduom.setSelectedItem(res.getString("vpr_uom"));
                     ddcurr.setSelectedItem(res.getString("vpr_curr"));
                     price.setText(df.format(res.getDouble("vpr_price")));
                     btUpdate.setEnabled(true);
                     btDelete.setEnabled(true);
                     btAdd.setEnabled(false);
                }
               
               
                
                if (i == 0) 
                    bsmf.MainFrame.show("No Price List Found");
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve vpr_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void initvars(String arg) {
        
         
         lblpricecode.setVisible(false);
         lblpricecode.setForeground(Color.red);
         pricelist.setModel(pricelistmodel);
         
         
         btUpdate.setEnabled(false);
         btDelete.setEnabled(false);
         btAdd.setEnabled(false);
         
         price.setText("");
         price.setBackground(Color.WHITE);
         
        ArrayList myvends = OVData.getvendmstrlist();
        ArrayList pricegroups = OVData.getPriceGroupList();
        ddvendcode.removeAllItems();
      
        for (int i = 0; i < pricegroups.size(); i++) {
            ddvendcode.addItem(pricegroups.get(i));
        }
        for (int i = 0; i < myvends.size(); i++) {
            ddvendcode.addItem(myvends.get(i));
        }
       
         dduom.removeAllItems();
        ArrayList<String> mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
       
         ddcurr.removeAllItems();
        ArrayList<String> curr = OVData.getCurrlist();
        for (String code : curr) {
            ddcurr.addItem(code);
        }
        
        ArrayList mypart = OVData.getItemMasterRawlist();
        ddpart.removeAllItems();
        for (int i = 0; i < mypart.size(); i++) {
            ddpart.addItem(mypart.get(i).toString());
        }
        
        
        listmodel.removeAllElements();
        pricelistmodel.removeAllElements();;
    
       
       
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
        setPriceList();
         
             String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            getVendPriceRecord(args[0], args[1], args[2], args[3]);
        }
    }
    
  
      public void setData() {
         DecimalFormat df = new DecimalFormat("#0.0000");
         
        if (ddpart.getItemCount() > 0 && ddvendcode.getItemCount() > 0 && dduom.getItemCount() > 0 && ddcurr.getItemCount() > 0) {
        double myprice = OVData.getPartPriceFromVend(ddvendcode.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), 
                dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
         lbitem.setText(OVData.getItemDesc(ddpart.getSelectedItem().toString()));
        if (myprice == 0.00) {
            price.setText("0.00");
            btAdd.setEnabled(true);
            btUpdate.setEnabled(false);
            btDelete.setEnabled(false);
            price.setBackground(Color.YELLOW);
        } else {
        //    bsmf.MainFrame.show(ddcustcode.getSelectedItem().toString() + ":" + ddpart.getSelectedItem().toString() + ":" + dduom.getSelectedItem().toString() + ":" + df.format(myprice));
            price.setText(df.format(myprice));
            btAdd.setEnabled(false);
            btUpdate.setEnabled(false);
            btDelete.setEnabled(false); 
            price.setBackground(Color.GREEN);
        }
        }
    }
    
     public void setPriceList() {
        pricelistmodel.removeAllElements();
        String pricecode = "";
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
             
              res = st.executeQuery("select vpr_item, vpr_uom, vpr_curr from vpr_mstr where vpr_vend = " + "'" + 
                      ddvendcode.getSelectedItem().toString() + "'" +
                      " and vpr_type = " + "'LIST'" +
                      ";");
               while (res.next()) {
                      pricelistmodel.addElement(res.getString("vpr_item") + ":" + res.getString("vpr_uom") + ":" + res.getString("vpr_curr"));
               }
               
               // check for price code in cm_mstr
             
               res = st.executeQuery("select vd_price_code from vd_mstr where vd_addr = " + "'" + 
                      ddvendcode.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
               pricecode = res.getString("vd_price_code") == null ? "" : res.getString("vd_price_code");
               }
               if (! pricecode.isEmpty()) {
                lblpricecode.setText("Belongs to Group " + pricecode);
                lblpricecode.setVisible(true); 
               } else {
                lblpricecode.setVisible(false);  
               }
               
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Retrieve Price List");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
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
        jLabel5 = new javax.swing.JLabel();
        price = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        ddvendcode = new javax.swing.JComboBox();
        btDelete = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();
        lblpricecode = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pricelist = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox<>();
        dduom = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        lbitem = new javax.swing.JLabel();
        lbvend = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Vendor Price Maintenance"));

        jLabel5.setText("Price");

        price.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                priceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                priceFocusLost(evt);
            }
        });

        jLabel3.setText("Vend / GroupCode");

        btAdd.setText("Add");
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        ddvendcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddvendcodeItemStateChanged(evt);
            }
        });

        btDelete.setText("Delete");
        btDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeleteActionPerformed(evt);
            }
        });

        btUpdate.setText("Update");
        btUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUpdateActionPerformed(evt);
            }
        });

        jLabel2.setText("Item");

        pricelist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pricelistMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(pricelist);

        jLabel4.setText("Applied");

        ddpart.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddpartItemStateChanged(evt);
            }
        });

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel1.setText("uom");

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel6.setText("Currency");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblpricecode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddpart, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddvendcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btDelete)
                    .addComponent(btUpdate)
                    .addComponent(btAdd)
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(ddvendcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbvend, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btDelete))
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void pricelistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistMouseClicked
        if (! pricelist.isSelectionEmpty())
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            String[] str = pricelist.getSelectedValue().toString().split(":", -1);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                DecimalFormat df = new DecimalFormat("#0.0000");
                res = st.executeQuery("select vpr_price, vpr_item, vpr_uom, vpr_curr from vpr_mstr where vpr_vend = " + "'" +
                    ddvendcode.getSelectedItem().toString() + "'" +
                    " and vpr_type = " + "'LIST'" +
                    " and vpr_item = " + "'" + str[0] + "'" +
                    " and vpr_uom = " + "'" + str[1] + "'" +
                    " and vpr_curr = " + "'" + str[2] + "'" +        
                    ";");
                while (res.next()) {
                    dduom.setSelectedItem(res.getString("vpr_uom"));
                    ddcurr.setSelectedItem(res.getString("vpr_curr"));
                    ddpart.setSelectedItem(res.getString("vpr_item"));
                    price.setText(df.format(res.getDouble("vpr_price")));
                    
                }
                btAdd.setEnabled(false);
                btUpdate.setEnabled(true);
                btDelete.setEnabled(true);
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Retrieve Selected Part Price");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_pricelistMouseClicked

    private void btUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUpdateActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                Pattern p = Pattern.compile("^\\d+\\.\\d+$");
                Matcher m = p.matcher(price.getText());
                if (!m.find() || price.getText() == null) {
                    bsmf.MainFrame.show("Invalid Price...must be decimal format");
                    proceed = false;
                }

                if (proceed) {
                    st.executeUpdate("update vpr_mstr "
                        + " set vpr_price = " + "'" + price.getText() + "'"
                        + " where vpr_vend = " + "'" + ddvendcode.getSelectedItem() + "'"
                        + " and vpr_type = 'LIST' "
                        + " and vpr_item = " + "'" + ddpart.getSelectedItem().toString() + "'"
                        + " and vpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'"
                        + " and vpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'"        
                        + ";");

                    bsmf.MainFrame.show("Updated Price List Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Update Price List Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btUpdateActionPerformed

    private void btDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeleteActionPerformed
        boolean proceed = true;
        if (pricelist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show("nothing is selected");
        } else {
            proceed = bsmf.MainFrame.warn("Are you sure?");
        }
        if (proceed) {
            String[] z = pricelist.getSelectedValue().toString().split(":");
            try {

                Class.forName(bsmf.MainFrame.driver).newInstance();
                bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
                try {
                    Statement st = bsmf.MainFrame.con.createStatement();

                     int i = st.executeUpdate("delete from vpr_mstr where vpr_vend = " + "'" + ddvendcode.getSelectedItem().toString() + "'" + 
                                            " and vpr_item = " + "'" + z[0].toString() + "'" +
                                            " and vpr_uom = " + "'" + z[1].toString() + "'" +
                                            " and vpr_curr = " + "'" + z[2].toString() + "'" +
                                            " and vpr_type = 'LIST' " + ";");
                    if (i > 0) {
                        bsmf.MainFrame.show("deleted code " + pricelist.getSelectedValue().toString());
                        initvars("");
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Unable to Delete vpr_mstr Record");
                }
                bsmf.MainFrame.con.close();
            } catch (Exception e) {
                MainFrame.bslog(e);
            }
        }
    }//GEN-LAST:event_btDeleteActionPerformed

    private void ddvendcodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddvendcodeItemStateChanged

        price.setText("");
        if (ddpart.getItemCount() > 0) {
        ddpart.setSelectedIndex(0);
        }
        btAdd.setEnabled(true);
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);
        if (ddvendcode.getItemCount() != 0) {
            setPriceList();
            String code = OVData.getPriceGroupCodeFromVend(ddvendcode.getSelectedItem().toString());
            lbvend.setText(OVData.getVendName(ddvendcode.getSelectedItem().toString())); 
            if (! code.isEmpty()) {
                lblpricecode.setText("Vendor belongs to Price Code " + code);
                lblpricecode.setVisible(true);
            } else {
                lblpricecode.setVisible(false);
            }
        }

        // ArrayList mylist = OVData.getPartListFromCustCode(ddcustcode.getSelectedItem().toString());
        //      for (int i = 0; i < mylist.size(); i++) {
            //     ddpart.addItem(mylist.get(i));
            //     }
    }//GEN-LAST:event_ddvendcodeItemStateChanged

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);

            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                Pattern p = Pattern.compile("^\\d+\\.\\d+$");
                Matcher m = p.matcher(price.getText());
                if (!m.find() || price.getText() == null) {
                    bsmf.MainFrame.show("Invalid Price...must be decimal format");
                    proceed = false;
                }

                res = st.executeQuery("select vpr_item from vpr_mstr where vpr_item = " + "'" +
                    ddpart.getSelectedItem().toString() + "'" +
                    " and vpr_vend = " + "'" + ddvendcode.getSelectedItem().toString() + "'" +
                    " and vpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'" +
                    " and vpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'" +        
                    ";");
                while (res.next()) {
                    i++;
                    if (i == 1)
                    bsmf.MainFrame.show("Record already exists");
                    proceed = false;
                }

                if (proceed) {
                    st.executeUpdate("insert into vpr_mstr "
                        + "(vpr_vend, vpr_item, vpr_type, vpr_desc, vpr_uom, vpr_curr, "
                        + "vpr_price "
                        + " ) "
                        + " values ( " + "'" + ddvendcode.getSelectedItem() + "'" + ","
                        + "'" + ddpart.getSelectedItem().toString() + "'" + ","
                        + "'LIST'" + ","
                        + "'" + ddpart.getSelectedItem().toString() + "'" + ","
                        + "'" + dduom.getSelectedItem().toString() + "'" + ","
                        + "'" + ddcurr.getSelectedItem().toString() + "'" + ","        
                        + "'" + price.getText() + "'"
                        + ")"
                        + ";");

                    bsmf.MainFrame.show("Added Price Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql Cannot Add Price");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btAddActionPerformed

    private void ddpartItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddpartItemStateChanged
       setData();
    }//GEN-LAST:event_ddpartItemStateChanged

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        setData();
    }//GEN-LAST:event_dduomActionPerformed

    private void ddcurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcurrActionPerformed
         setData();
    }//GEN-LAST:event_ddcurrActionPerformed

    private void priceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_priceFocusLost
            String x = BlueSeerUtils.bsformat("", price.getText(), "4");
        if (x.equals("error")) {
            price.setText("");
            price.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            price.requestFocus();
        } else {
            price.setText(x);
            price.setBackground(Color.white);
        }
    }//GEN-LAST:event_priceFocusLost

    private void priceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_priceFocusGained
        if (price.getText().equals("0.0000")) {
            price.setText("");
        }
    }//GEN-LAST:event_priceFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btUpdate;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox<String> ddpart;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JComboBox ddvendcode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbitem;
    private javax.swing.JLabel lblpricecode;
    private javax.swing.JLabel lbvend;
    private javax.swing.JTextField price;
    private javax.swing.JList pricelist;
    // End of variables declaration//GEN-END:variables
}
