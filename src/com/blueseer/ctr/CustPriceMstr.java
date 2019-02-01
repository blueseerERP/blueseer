/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.ctr;

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
public class CustPriceMstr extends javax.swing.JPanel {

    DefaultListModel listmodel = new DefaultListModel();
    DefaultListModel pricelistmodel = new DefaultListModel();
    
    /**
     * Creates new form CustXrefMaintPanel
     */
    public CustPriceMstr() {
        initComponents();
    }

    
   
    
    public void getCustPriceRecord(String cust, String part, String type, String uom, String curr) {
        initvars("");
        try {

            DecimalFormat df = new DecimalFormat("#0.00");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("SELECT * FROM  cpr_mstr where " +
                    " cpr_cust = " + "'" + cust + "'" + 
                    " AND cpr_item = " + "'" + part + "'" + 
                    " AND cpr_uom = " + "'" + uom + "'" + 
                    " AND cpr_curr = " + "'" + curr + "'" +         
                    " AND cpr_type = " + "'" + type + "'" +
                        ";") ;
                        
                while (res.next()) {
                    i++;
                   if (type.equals("LIST")) {
                    ddcustcode.setSelectedItem(res.getString("cpr_cust"));
                    ddpart.setSelectedItem(res.getString("cpr_item"));
                    dduom.setSelectedItem(res.getString("cpr_uom"));
                    ddcurr.setSelectedItem(res.getString("cpr_curr"));
                     price.setText(df.format(res.getDouble("cpr_price")));
                     btUpdate.setEnabled(true);
                     btDelete.setEnabled(true);
                     btAdd.setEnabled(false);
                   } else {
                      ddcustcode_disc.setSelectedItem(res.getString("cpr_cust"));
                     tbdisckey.setText(res.getString("cpr_item"));
                     tbdisc.setText(df.format(res.getDouble("cpr_disc"))); 
                      btupdatedisc.setEnabled(true);
                     btdeletedisc.setEnabled(true);
                     btadddisc.setEnabled(false);
                   }
                   
                }
               
               
                
                if (i == 0) 
                    bsmf.MainFrame.show("No Price List Found");
               

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve cpr_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void initvars(String arg) {
         lbldisccode.setVisible(false);
         lbldisccode.setForeground(Color.red);
         disclist.setModel(listmodel);
         
         lblpricecode.setVisible(false);
         lblpricecode.setForeground(Color.red);
         pricelist.setModel(pricelistmodel);
         
         
         btUpdate.setEnabled(false);
         btDelete.setEnabled(false);
         btAdd.setEnabled(false);
         
         price.setText("");
         price.setBackground(Color.WHITE);
         
        ArrayList mycusts = OVData.getcustmstrlist();
        ArrayList pricegroups = OVData.getPriceGroupList();
        ddcustcode.removeAllItems();
        ddcustcode_disc.removeAllItems();
        for (int i = 0; i < pricegroups.size(); i++) {
            ddcustcode.addItem(pricegroups.get(i));
        }
        for (int i = 0; i < mycusts.size(); i++) {
            ddcustcode.addItem(mycusts.get(i));
        }
        for (int i = 0; i < pricegroups.size(); i++) {
            ddcustcode_disc.addItem(pricegroups.get(i));
        }
        for (int i = 0; i < mycusts.size(); i++) {
            ddcustcode_disc.addItem(mycusts.get(i));
        }
        
        
         dduom.removeAllItems();
        ArrayList<String> mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
          ddcurr.removeAllItems();
        ArrayList<String> mycurr = OVData.getCurrlist();
        for (String code : mycurr) {
            ddcurr.addItem(code);
        }
        
        
        ArrayList mypart = OVData.getItemMasterMCodelist();
        ddpart.removeAllItems();
        for (int i = 0; i < mypart.size(); i++) {
            ddpart.addItem(mypart.get(i).toString());
        }
        
       
        
        tbdisc.setText("");
        tbdisckey.setText("");
        listmodel.removeAllElements();
        pricelistmodel.removeAllElements();;
    
        
       
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
        
         if (ddcustcode.getItemCount() > 0) {        
         setPriceList();
         }
         
         if (ddcustcode_disc.getItemCount() > 0) {
         updateDiscList();
         }
        
        
           String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            getCustPriceRecord(args[0], args[1], args[2], args[3], args[4]);
        }
         
    }
    
    public void updateDiscList() {
   
        
        listmodel.removeAllElements();
        String disccode = "";
        
        
        
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
             
              res = st.executeQuery("select cpr_item from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode_disc.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'DISCOUNT'" +
                      ";");
               while (res.next()) {
                      listmodel.addElement(res.getString("cpr_item"));
               }
               
               // check for discount code in cm_mstr
             
               res = st.executeQuery("select cm_disc_code from cm_mstr where cm_code = " + "'" + 
                      ddcustcode_disc.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
               disccode = res.getString("cm_disc_code") == null ? "" : res.getString("cm_disc_code");
               }
               if (! disccode.isEmpty()) {
                lbldisccode.setText("Belongs to Group " + disccode);
                lbldisccode.setVisible(true); 
               } else {
                lbldisccode.setVisible(false);  
               }
               
              
            } catch (SQLException s) {
                bsmf.MainFrame.show("Sql Cannot Retrieve Disc List");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
    public void setData() {
         DecimalFormat df = new DecimalFormat("#0.00");
         
        if (ddpart.getItemCount() > 0 && ddcustcode.getItemCount() > 0 && dduom.getItemCount() > 0 && ddcurr.getItemCount() > 0) {
        double myprice = OVData.getPartPriceFromCust(ddcustcode.getSelectedItem().toString(), ddpart.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString());
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
             
              res = st.executeQuery("select cpr_item, cpr_uom, cpr_curr from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'LIST'" +
                      ";");
               while (res.next()) {
                      pricelistmodel.addElement(res.getString("cpr_item") + ":" + res.getString("cpr_uom") + ":" + res.getString("cpr_curr"));
               }
               
               // check for price code in cm_mstr
             
               res = st.executeQuery("select cm_price_code from cm_mstr where cm_code = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      ";");
               while (res.next()) {
               pricecode = res.getString("cm_price_code") == null ? "" : res.getString("cm_price_code");
               }
               if (! pricecode.isEmpty()) {
                lblpricecode.setText("Belongs to Group " + pricecode);
                lblpricecode.setVisible(true); 
               } else {
                lblpricecode.setVisible(false);  
               }
               
              
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Sql Cannot Retrieve Price List");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
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

        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        price = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        ddcustcode = new javax.swing.JComboBox();
        btDelete = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();
        lblpricecode = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pricelist = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        ddpart = new javax.swing.JComboBox<>();
        dduom = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbdisckey = new javax.swing.JTextField();
        ddcustcode_disc = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        lbltype = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbdisc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        disclist = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        btadddisc = new javax.swing.JButton();
        btupdatedisc = new javax.swing.JButton();
        btdeletedisc = new javax.swing.JButton();
        lbldisccode = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Pricing Maintenance"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("List Price Maintenance"));

        jLabel5.setText("Price");

        jLabel3.setText("Cust / GroupCode");

        btAdd.setText("Add");
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddActionPerformed(evt);
            }
        });

        ddcustcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcustcodeItemStateChanged(evt);
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

        jLabel6.setText("UOM");

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel11.setText("Currency");

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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblpricecode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddpart, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddcustcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btDelete)
                    .addComponent(btUpdate)
                    .addComponent(btAdd))
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(ddpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Discount Maintenance"));
        jPanel2.setPreferredSize(new java.awt.Dimension(414, 350));

        ddcustcode_disc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcustcode_discItemStateChanged(evt);
            }
        });

        jLabel7.setText("Cust / GroupCode");

        jLabel8.setText("Key Desc (unique)");

        jLabel9.setText("Disc Percent");

        disclist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disclistMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(disclist);

        jLabel10.setText("Applied");

        btadddisc.setText("Add");
        btadddisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadddiscActionPerformed(evt);
            }
        });

        btupdatedisc.setText("Update");
        btupdatedisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatediscActionPerformed(evt);
            }
        });

        btdeletedisc.setText("Delete");
        btdeletedisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletediscActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lbldisccode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(tbdisckey, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcustcode_disc, javax.swing.GroupLayout.Alignment.LEADING, 0, 181, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbltype, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btadddisc)
                            .addComponent(btupdatedisc)
                            .addComponent(btdeletedisc)))
                    .addComponent(tbdisc, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcustcode_disc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(lbltype, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbldisccode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisckey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdisc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btadddisc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdatedisc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeletedisc)))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel3);
    }// </editor-fold>//GEN-END:initComponents

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

            
             
              res = st.executeQuery("select cpr_item from cpr_mstr where cpr_item = " + "'" + 
                      ddpart.getSelectedItem().toString() + "'" +
                      " and cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'" +
                      " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'" +        
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show("Record already exists");
                proceed = false;             
               }
             
                if (proceed) {
                    st.executeUpdate("insert into cpr_mstr "
                        + "(cpr_cust, cpr_item, cpr_type, cpr_desc, cpr_uom, cpr_curr, "
                        + "cpr_price "
                        + " ) "
                        + " values ( " + "'" + ddcustcode.getSelectedItem() + "'" + ","
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
                s.printStackTrace();
                bsmf.MainFrame.show("Sql Cannot Add Price");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btAddActionPerformed

    private void ddcustcodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcustcodeItemStateChanged
       
      //  price.setText("");
      //  if (ddpart.getItemCount() > 0) {
      //  ddpart.setSelectedIndex(0);
      //  }
        btAdd.setEnabled(true);
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);
         if (ddcustcode.getItemCount() != 0) {
              setPriceList();
              setData();
         String custcode = OVData.getPriceGroupCodeFromCust(ddcustcode.getSelectedItem().toString());
         if (! custcode.isEmpty()) {
             lblpricecode.setText("Cust belongs to Price Code " + custcode);
             lblpricecode.setVisible(true);
         } else {
             lblpricecode.setVisible(false);
         }
         }
              
       // ArrayList mylist = OVData.getPartListFromCustCode(ddcustcode.getSelectedItem().toString());
      //      for (int i = 0; i < mylist.size(); i++) {
       //     ddpart.addItem(mylist.get(i));
       //     }
    }//GEN-LAST:event_ddcustcodeItemStateChanged

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
              
                   int i = st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" + 
                                            " and cpr_item = " + "'" + z[0].toString() + "'" +
                                            " and cpr_uom = " + "'" + z[1].toString() + "'" +
                                            " and cpr_curr = " + "'" + z[2].toString() + "'" +
                                            " and cpr_type = 'LIST' " + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + pricelist.getSelectedValue().toString());
                    initvars("");
                    }
                } catch (SQLException s) {
                    s.printStackTrace();
                    bsmf.MainFrame.show("Unable to Delete cpr_mstr Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btDeleteActionPerformed

    private void ddcustcode_discItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcustcode_discItemStateChanged
      if (ddcustcode_disc.getSelectedItem() != null) {
        updateDiscList();
        tbdisckey.setText("");
        tbdisc.setText("");
      }
    }//GEN-LAST:event_ddcustcode_discItemStateChanged

    private void btadddiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadddiscActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                         
                
             Pattern p = Pattern.compile("^\\d+\\.\\d{2}$");
             Matcher m = p.matcher(tbdisc.getText());
             if (!m.find() || tbdisc.getText() == null) {
             bsmf.MainFrame.show("Invalid Discount need x.xx format");
             proceed = false;
             }

             p = Pattern.compile(",");
             m = p.matcher(tbdisckey.getText());
             if (m.find()) {
             bsmf.MainFrame.show("Cannot have commas in key/desc field");
             proceed = false;
             }
            
             
              res = st.executeQuery("select cpr_item from cpr_mstr where cpr_item = " + "'" + 
                      tbdisckey.getText().toString() + "'" +
                      " and cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'DISCOUNT'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show("Record already exists");
                proceed = false;             
               }
             
                if (proceed) {
                    st.executeUpdate("insert into cpr_mstr "
                        + "(cpr_cust, cpr_item, cpr_type, cpr_desc, "
                        + " cpr_disc "
                        + " ) "
                        + " values ( " + "'" + ddcustcode_disc.getSelectedItem() + "'" + ","
                        + "'" + tbdisckey.getText().toString() + "'" + ","
                        + "'DISCOUNT'" + ","
                        + "'" + tbdisckey.getText().toString() + "'" + ","
                        + "'" + tbdisc.getText() + "'" 
                        + ")"
                        + ";");

                    
        
                    bsmf.MainFrame.show("Added Discount Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Sql Cannot Discount Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btadddiscActionPerformed

    private void btdeletediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletediscActionPerformed
        boolean proceed = true; 
        if (disclist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show("nothing is selected");
        } else {
           proceed = bsmf.MainFrame.warn("Are you sure?");
        }
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem().toString() + "'" + 
                                            " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'" +
                                            " and cpr_type = 'DISCOUNT' " + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + disclist.getSelectedValue().toString());
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete cpr_mstr Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeletediscActionPerformed

    private void btupdatediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatediscActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                         
                
             Pattern p = Pattern.compile("^\\d+\\.\\d{2}$");
             Matcher m = p.matcher(tbdisc.getText());
             if (!m.find() || tbdisc.getText() == null) {
             bsmf.MainFrame.show("Invalid Discount need x.xx format");
             proceed = false;
             }

             p = Pattern.compile(",");
             m = p.matcher(tbdisckey.getText());
             if (m.find()) {
             bsmf.MainFrame.show("Cannot have commas in key/desc field");
             proceed = false;
             }
             
                if (proceed) {
                    st.executeUpdate("update cpr_mstr "
                        + " set cpr_disc = " + "'" + tbdisc.getText() + "'"
                        + " where cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem() + "'" 
                        + " and cpr_type = 'DISCOUNT' "        
                        + " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'"  
                        + ";");

                    bsmf.MainFrame.show("Updated Discount Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Sql Cannot Update Discount Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btupdatediscActionPerformed

    private void disclistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disclistMouseClicked
        if (! disclist.isSelectionEmpty())
        try {
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                DecimalFormat df = new DecimalFormat("#0.00");
              res = st.executeQuery("select cpr_disc, cpr_item from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode_disc.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'DISCOUNT'" +
                      " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'" +
                      ";");
               while (res.next()) {
                      tbdisc.setText(df.format(res.getDouble("cpr_disc")));
                      tbdisckey.setText(res.getString("cpr_item"));
               }
            } catch (SQLException s) {
                bsmf.MainFrame.show("Sql Cannot Retrieve Selected Discount");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_disclistMouseClicked

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
                    st.executeUpdate("update cpr_mstr "
                        + " set cpr_price = " + "'" + price.getText() + "'"
                        + " where cpr_cust = " + "'" + ddcustcode.getSelectedItem() + "'" 
                        + " and cpr_type = 'LIST' "        
                        + " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'"
                        + " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'"        
                        + " and cpr_item = " + "'" + ddpart.getSelectedItem().toString() + "'"  
                        + ";");

                    bsmf.MainFrame.show("Updated Price List Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Sql Cannot Update Price List Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btUpdateActionPerformed

    private void pricelistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistMouseClicked
        if (! pricelist.isSelectionEmpty())
        try {
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                String[] str = pricelist.getSelectedValue().toString().split(":", -1);
                DecimalFormat df = new DecimalFormat("#0.00");
              res = st.executeQuery("select cpr_price, cpr_item, cpr_uom, cpr_curr from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'LIST'" +
                      " and cpr_item = " + "'" + str[0] + "'" +
                      " and cpr_uom = " + "'" + str[1] + "'" +
                      " and cpr_curr = " + "'" + str[2] + "'" +        
                      ";");
               while (res.next()) {
                      dduom.setSelectedItem(res.getString("cpr_uom"));
                      ddcurr.setSelectedItem(res.getString("cpr_curr"));
                      ddpart.setSelectedItem(res.getString("cpr_item"));
                      price.setText(df.format(res.getDouble("cpr_price")));
                      
               }
               btAdd.setEnabled(false);
               btUpdate.setEnabled(true);
               btDelete.setEnabled(true);
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Sql Cannot Retrieve Selected Part Price");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_pricelistMouseClicked

    private void ddpartItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddpartItemStateChanged
        setData();
    }//GEN-LAST:event_ddpartItemStateChanged

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        setData();
    }//GEN-LAST:event_dduomActionPerformed

    private void ddcurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcurrActionPerformed
        setData();
    }//GEN-LAST:event_ddcurrActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btUpdate;
    private javax.swing.JButton btadddisc;
    private javax.swing.JButton btdeletedisc;
    private javax.swing.JButton btupdatedisc;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcustcode;
    private javax.swing.JComboBox ddcustcode_disc;
    private javax.swing.JComboBox<String> ddpart;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JList disclist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbldisccode;
    private javax.swing.JLabel lblpricecode;
    private javax.swing.JLabel lbltype;
    private javax.swing.JTextField price;
    private javax.swing.JList pricelist;
    private javax.swing.JTextField tbdisc;
    private javax.swing.JTextField tbdisckey;
    // End of variables declaration//GEN-END:variables
}
