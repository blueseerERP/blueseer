/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

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

import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import bsmf.MainFrame; 
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.inv.invData;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsformat;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class CustPriceMaint extends javax.swing.JPanel {

    DefaultListModel listmodel = new DefaultListModel();
    DefaultListModel pricelistmodel = new DefaultListModel();
    boolean isLoad = false;
    
    /**
     * Creates new form CustXrefMaintPanel
     */
    public CustPriceMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
        
    public void getCustPriceRecord(String cust, String part, String type, String uom, String curr, String qty) {
        initvars(null);
        try {
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                
                res = st.executeQuery("SELECT * FROM  cpr_mstr where " +
                    " cpr_cust = " + "'" + cust + "'" + 
                    " AND cpr_item = " + "'" + part + "'" + 
                    " AND cpr_uom = " + "'" + uom + "'" + 
                    " AND cpr_curr = " + "'" + curr + "'" +         
                    " AND cpr_type = " + "'" + type + "'" +
                    " AND cpr_volqty = " + "'" + qty + "'" +         
                        ";") ;
               
                        
                while (res.next()) {
                    i++;
                   if (type.equals("LIST") || type.equals("VOLUME")) {
                    ddcustcode.setSelectedItem(res.getString("cpr_cust"));
                    dditem.setSelectedItem(res.getString("cpr_item"));
                    dduom.setSelectedItem(res.getString("cpr_uom"));
                    ddcurr.setSelectedItem(res.getString("cpr_curr"));
                    if (res.getString("cpr_expire") == null || res.getString("cpr_expire").isBlank() || res.getString("cpr_expire").equals("null")) {
                        dcexpire.setDate(null);  
                      } else {
                        dcexpire.setDate(bsmf.MainFrame.dfdate.parse(res.getString("cpr_expire")));  
                      }
                     tbprice.setText(bsformat("s",res.getString("cpr_price").replace('.',defaultDecimalSeparator),"4"));
                     tbqty.setText(bsformat("s",res.getString("cpr_volqty").replace('.',defaultDecimalSeparator),"4"));
                     btUpdate.setEnabled(true);
                     btDelete.setEnabled(true);
                     btAdd.setEnabled(false);
                   } else {
                      ddcustcode_disc.setSelectedItem(res.getString("cpr_cust"));
                     tbdisckey.setText(res.getString("cpr_item"));
                     tbdisc.setText(bsformat("s",res.getString("cpr_disc").replace('.',defaultDecimalSeparator),"4")); 
                      btupdatedisc.setEnabled(true);
                     btdeletedisc.setEnabled(true);
                     btadddisc.setEnabled(false);
                   } 
                   
                }
               
               
                
                if (i == 0) 
                    bsmf.MainFrame.show(getMessageTag(1001));
               

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void initvars(String[] arg) {
        
        
        isLoad = true;
         dcexpire.setDate(null);
         lbldisccode.setVisible(false);
         lbldisccode.setForeground(Color.red);
         disclist.setModel(listmodel);
         
         lblpricecode.setVisible(false);
         lblpricecode.setForeground(Color.red);
         pricelist.setModel(pricelistmodel);
         
         tbqty.setEnabled(false);
         ddtype.setEnabled(true);
         ddtype.setSelectedIndex(0);
         btUpdate.setEnabled(false);
         btDelete.setEnabled(false);
         btAdd.setEnabled(false);
         
         tbprice.setText("");
         tbprice.setBackground(Color.WHITE);
         
        ArrayList mycusts = cusData.getcustmstrlist();
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
        
        ddcustcode.insertItemAt("", 0);
        ddcustcode.setSelectedIndex(0);
        
        dduom.removeAllItems();
        ArrayList<String> mylist = OVData.getUOMList();
        for (String code : mylist) {
            dduom.addItem(code);
        }
        
        dduom.insertItemAt("", 0);
        dduom.setSelectedIndex(0);
        
        ddcurr.removeAllItems();
        ArrayList<String> mycurr = fglData.getCurrlist();
        for (String code : mycurr) {
            ddcurr.addItem(code);
        }
        ddcurr.setSelectedItem(OVData.getDefaultCurrency());
        
        ArrayList mypart = invData.getItemMasterAlllist();
        dditem.removeAllItems();
        for (int i = 0; i < mypart.size(); i++) {
            dditem.addItem(mypart.get(i).toString());
        }
        dditem.insertItemAt("", 0);
        dditem.setSelectedIndex(0);
       
        lbcust.setText("");
        lbitem.setText("");
        lblpricecode.setText("");
        tbdisc.setText("");
        tbdisckey.setText("");
        tbqty.setText("");
        listmodel.removeAllElements();
        pricelistmodel.removeAllElements();;
    
        
         if (ddcustcode.getItemCount() > 0) {        
         setPriceList();
         }
         
         if (ddcustcode_disc.getItemCount() > 0) {
         updateDiscList();
         }
        
        isLoad = false;
          
        if (arg != null && arg.length > 5) {
            getCustPriceRecord(arg[0], arg[1], arg[2], arg[3], arg[4], arg[5]);
        }
         
    }
    
    public void updateDiscList() {
   
        if (! isLoad) {
        listmodel.removeAllElements();
        String disccode = "";
        
        
        
        try {

           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             
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
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }
    
    public void setData() {
        if (! isLoad) { 
            if (dditem.getItemCount() > 0 && ddcustcode.getItemCount() > 0 && dduom.getItemCount() > 0 && ddcurr.getItemCount() > 0) {
            double myprice = invData.getItemPriceFromCust(ddcustcode.getSelectedItem().toString(), dditem.getSelectedItem().toString(), dduom.getSelectedItem().toString(), ddcurr.getSelectedItem().toString(), ddtype.getSelectedItem().toString(), tbqty.getText());
            lbitem.setText(invData.getItemDesc(dditem.getSelectedItem().toString()));
                if (myprice == 0) {
                    tbprice.setText("0");
                    btAdd.setEnabled(true);
                    btUpdate.setEnabled(false);
                    btDelete.setEnabled(false);
                    tbprice.setBackground(Color.YELLOW);
                } else {
                    tbprice.setText(bsFormatDouble(myprice,"4"));
                    btAdd.setEnabled(false);
                    btUpdate.setEnabled(false);
                    btDelete.setEnabled(false); 
                    tbprice.setBackground(Color.GREEN);
                }
            }
        }
    }
    
     public void setPriceList() {
        if (! isLoad) {
        pricelistmodel.removeAllElements();
        String pricecode = "";
        try {

           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             
              res = st.executeQuery("select cpr_item, cpr_uom, cpr_curr, cpr_volqty, cpr_type from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_type <> " + "'DISCOUNT'" +
                      " order by cpr_item;");
               while (res.next()) {
                      if (res.getString("cpr_type").equals("LIST")) {
                      pricelistmodel.addElement(res.getString("cpr_item") + ":" + res.getString("cpr_uom") + ":" + res.getString("cpr_curr"));
                      } else {
                      pricelistmodel.addElement(res.getString("cpr_item") + ":" + res.getString("cpr_uom") + ":" + res.getString("cpr_curr") + ":" + res.getString("cpr_volqty"));    
                      }
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
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
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
        tbprice = new javax.swing.JTextField();
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
        dditem = new javax.swing.JComboBox<>();
        dduom = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        lbitem = new javax.swing.JLabel();
        lbcust = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        lblqty = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox<>();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
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
        setPreferredSize(new java.awt.Dimension(980, 424));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Pricing Maintenance"));
        jPanel3.setName("panelmain"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(950, 500));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("List Price Maintenance"));
        jPanel1.setName("panellist"); // NOI18N

        jLabel5.setText("Price");
        jLabel5.setName("lblprice"); // NOI18N

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel3.setText("Cust / GroupCode");
        jLabel3.setName("lblcust"); // NOI18N

        btAdd.setText("Add");
        btAdd.setName("btadd"); // NOI18N
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
        btDelete.setName("btdelete"); // NOI18N
        btDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeleteActionPerformed(evt);
            }
        });

        btUpdate.setText("Update");
        btUpdate.setName("btupdate"); // NOI18N
        btUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUpdateActionPerformed(evt);
            }
        });

        jLabel2.setText("Item");
        jLabel2.setName("lblitem"); // NOI18N

        pricelist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pricelistMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(pricelist);

        jLabel4.setText("Applied");
        jLabel4.setName("lblapplied"); // NOI18N

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        dduom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dduomActionPerformed(evt);
            }
        });

        jLabel6.setText("UOM");
        jLabel6.setName("lbluom"); // NOI18N

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel11.setText("Currency");
        jLabel11.setName("lblcurrency"); // NOI18N

        lblqty.setText("Quantity");
        lblqty.setName("lblqty"); // NOI18N

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LIST", "VOLUME" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel12.setText("Expire");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5)
                    .addComponent(lblqty)
                    .addComponent(jLabel12)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbprice, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addComponent(tbqty))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAdd)
                            .addComponent(btUpdate)
                            .addComponent(btDelete)
                            .addComponent(btclear)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(ddcustcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblpricecode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbitem, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblqty)))
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear))
                    .addComponent(jLabel4))
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Discount Maintenance"));
        jPanel2.setName("paneldiscount"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(414, 350));

        ddcustcode_disc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcustcode_discItemStateChanged(evt);
            }
        });

        jLabel7.setText("Cust / GroupCode");

        jLabel8.setText("Key Desc (unique)");
        jLabel8.setName("lblkeydesc"); // NOI18N

        jLabel9.setText("Disc Percent");
        jLabel9.setName("lblpercent"); // NOI18N

        disclist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disclistMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(disclist);

        jLabel10.setText("Applied");
        jLabel10.setName("lblapplied"); // NOI18N

        btadddisc.setText("Add");
        btadddisc.setName("btadd"); // NOI18N
        btadddisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadddiscActionPerformed(evt);
            }
        });

        btupdatedisc.setText("Update");
        btupdatedisc.setName("btupdate"); // NOI18N
        btupdatedisc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdatediscActionPerformed(evt);
            }
        });

        btdeletedisc.setText("Delete");
        btdeletedisc.setName("btdelete"); // NOI18N
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
                .addContainerGap(90, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                .addGap(39, 39, 39))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel3);
    }// </editor-fold>//GEN-END:initComponents

    private void btAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddActionPerformed
        try {

           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                int i = 0;
              String qty = "";
              String expiredate = null;
              if (dcexpire.getDate() != null) {
                  expiredate = BlueSeerUtils.setDateFormat(dcexpire.getDate());
              }
              if (ddtype.getSelectedItem().toString().equals("VOLUME")) {
                  qty = tbqty.getText().replace(defaultDecimalSeparator, '.');
                  res = st.executeQuery("select cpr_item from cpr_mstr where cpr_item = " + "'" + 
                      dditem.getSelectedItem().toString() + "'" +
                      " and cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'" +
                      " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'" +  
                      " and cpr_volqty = " + "'" + qty + "'" +        
                      " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'" +        
                      ";");
              }  else {
                  qty = "0";
                  res = st.executeQuery("select cpr_item from cpr_mstr where cpr_item = " + "'" + 
                      dditem.getSelectedItem().toString() + "'" +
                      " and cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'" +
                      " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'" +  
                      " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'" +        
                      ";");
              }
            
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show(getMessageTag(1014));
                proceed = false;             
               }
             
                if (proceed) {
                   
                       st.executeUpdate("insert into cpr_mstr "
                        + "(cpr_cust, cpr_item, cpr_type, cpr_desc, cpr_uom, cpr_curr, "
                        + "cpr_price, cpr_volqty, cpr_expire "
                        + " ) "
                        + " values ( " + "'" + ddcustcode.getSelectedItem() + "'" + ","
                        + "'" + dditem.getSelectedItem().toString() + "'" + ","
                        + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                        + "'" + dditem.getSelectedItem().toString() + "'" + ","
                        + "'" + dduom.getSelectedItem().toString() + "'" + ","
                        + "'" + ddcurr.getSelectedItem().toString() + "'" + ","        
                        + "'" + tbprice.getText().replace(defaultDecimalSeparator, '.') + "'" + ","   
                        + "'" + qty + "'"   + "," 
                        + "'" + expiredate + "'"                                
                        + ")"
                        + ";"); 
                    

                    
        
                    bsmf.MainFrame.show(getMessageTag(1007));
                    initvars(null);
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btAddActionPerformed

    private void ddcustcodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcustcodeItemStateChanged
       
      //  price.setText("");
      //  if (ddpart.getItemCount() > 0) {
      //  ddpart.setSelectedIndex(0);
      //  }
      
      if (! isLoad) {
        btAdd.setEnabled(true);
        btUpdate.setEnabled(false);
        btDelete.setEnabled(false);
         if (ddcustcode.getItemCount() != 0) {
              setPriceList();
              setData();
              lbcust.setText(cusData.getCustName(ddcustcode.getSelectedItem().toString()));
         /*
         String custcode = OVData.getPriceGroupCodeFromCust(ddcustcode.getSelectedItem().toString());
         if (! custcode.isEmpty()) {
             lblpricecode.setText("Cust belongs to Price Code " + custcode);
             lblpricecode.setVisible(true);
         } else {
             lblpricecode.setVisible(false);
         }
         */
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
            bsmf.MainFrame.show(getMessageTag(1081));
        } else {
           proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
            String[] z = pricelist.getSelectedValue().toString().split(":",-1);
        try {

            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                   int i = 0;
                   if (ddtype.getSelectedItem().toString().equals("LIST")) {
                       i = st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" + 
                                            " and cpr_item = " + "'" + z[0].toString() + "'" +
                                            " and cpr_uom = " + "'" + z[1].toString() + "'" +
                                            " and cpr_curr = " + "'" + z[2].toString() + "'" +
                                            " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ";");
                   } else {
                        i = st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + ddcustcode.getSelectedItem().toString() + "'" + 
                                            " and cpr_item = " + "'" + z[0].toString() + "'" +
                                            " and cpr_uom = " + "'" + z[1].toString() + "'" +
                                            " and cpr_curr = " + "'" + z[2].toString() + "'" +
                                            " and cpr_volqty = " + "'" + z[3].toString() + "'" +         
                                            " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ";");
                   }
                   
                  
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1009));
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
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

           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                int i = 0;
                
            
            
             
              res = st.executeQuery("select cpr_item from cpr_mstr where cpr_item = " + "'" + 
                      tbdisckey.getText().toString() + "'" +
                      " and cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'DISCOUNT'" +
                      ";");
               while (res.next()) {
                i++;
                if (i == 1) 
                    bsmf.MainFrame.show(getMessageTag(1014));
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
                        + "'" + tbdisc.getText().replace(defaultDecimalSeparator, '.') + "'" 
                        + ")"
                        + ";");

                    
        
                    bsmf.MainFrame.show(getMessageTag(1007));
                    initvars(null);
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btadddiscActionPerformed

    private void btdeletediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletediscActionPerformed
        boolean proceed = true; 
        if (disclist.isSelectionEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1081));
        } else {
           proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        }
        if (proceed) {
        try {

            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
              
                   int i = st.executeUpdate("delete from cpr_mstr where cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem().toString() + "'" + 
                                            " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'" +
                                            " and cpr_type = 'DISCOUNT' " + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show(getMessageTag(1009));
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeletediscActionPerformed

    private void btupdatediscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdatediscActionPerformed
        try {

           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                if (proceed) {
                    st.executeUpdate("update cpr_mstr "
                        + " set cpr_disc = " + "'" + tbdisc.getText().replace(defaultDecimalSeparator, '.') + "'"
                        + " where cpr_cust = " + "'" + ddcustcode_disc.getSelectedItem() + "'" 
                        + " and cpr_type = 'DISCOUNT' "        
                        + " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'"  
                        + ";");

                    bsmf.MainFrame.show(getMessageTag(1008));
                    initvars(null);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdatediscActionPerformed

    private void disclistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disclistMouseClicked
        if (! disclist.isSelectionEmpty())
        try {
           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                  res = st.executeQuery("select cpr_disc, cpr_item from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode_disc.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'DISCOUNT'" +
                      " and cpr_item = " + "'" + disclist.getSelectedValue().toString() + "'" +
                      ";");
               while (res.next()) {
                      tbdisc.setText(bsformat("s",res.getString("cpr_disc").replace('.', defaultDecimalSeparator),"4"));
                      tbdisckey.setText(res.getString("cpr_item"));
               }
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_disclistMouseClicked

    private void btUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUpdateActionPerformed
        try {
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                int i = 0;
                String qty = "";
                String expiredate = null;
                if (dcexpire.getDate() != null) {
                  expiredate = BlueSeerUtils.setDateFormat(dcexpire.getDate());
                }
                if (ddtype.getSelectedItem().toString().equals("VOLUME")) {
                    qty = tbqty.getText().replace(defaultDecimalSeparator, '.');
                }  else {
                    qty = "0";
                }
                
                if (proceed) {
                    
                    if (ddtype.getSelectedItem().toString().equals("LIST")) {
                       
                        st.executeUpdate("update cpr_mstr "
                        + " set cpr_price = " + "'" + tbprice.getText().replace(defaultDecimalSeparator, '.') + "'"
                        + ", cpr_expire = " + "'" + expiredate + "'"
                        + " where cpr_cust = " + "'" + ddcustcode.getSelectedItem() + "'" 
                        + " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'"        
                        + " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'"
                        + " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'"        
                        + " and cpr_item = " + "'" + dditem.getSelectedItem().toString() + "'" 
                        + ";");    
                       
                    } else {
                       st.executeUpdate("update cpr_mstr "
                        + " set cpr_price = " + "'" + tbprice.getText().replace(defaultDecimalSeparator, '.') + "'"
                        + ", cpr_expire = " + "'" + expiredate + "'"        
                        + " where cpr_cust = " + "'" + ddcustcode.getSelectedItem() + "'" 
                        + " and cpr_type = " + "'" + ddtype.getSelectedItem().toString() + "'"        
                        + " and cpr_uom = " + "'" + dduom.getSelectedItem().toString() + "'"
                        + " and cpr_curr = " + "'" + ddcurr.getSelectedItem().toString() + "'"        
                        + " and cpr_item = " + "'" + dditem.getSelectedItem().toString() + "'"  
                        + " and cpr_volqty = " + "'" + qty + "'"         
                        + ";"); 
                    }
                    
                        
                    

                    bsmf.MainFrame.show(getMessageTag(1008));
                    initvars(null);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btUpdateActionPerformed

    private void pricelistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pricelistMouseClicked
        if (! pricelist.isSelectionEmpty())
        try {
           Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                String[] str = pricelist.getSelectedValue().toString().split(":", -1);
                if (str.length > 3) {
               res = st.executeQuery("select cpr_price, cpr_item, cpr_uom, cpr_curr, cpr_volqty, cpr_type, cpr_expire from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'VOLUME'" +
                      " and cpr_item = " + "'" + str[0] + "'" +
                      " and cpr_uom = " + "'" + str[1] + "'" +
                      " and cpr_curr = " + "'" + str[2] + "'" +   
                      " and cpr_volqty = " + "'" + str[3] + "'" +        
                      ";");
                } else {
                 res = st.executeQuery("select cpr_price, cpr_item, cpr_uom, cpr_curr, cpr_volqty, cpr_type, cpr_expire from cpr_mstr where cpr_cust = " + "'" + 
                      ddcustcode.getSelectedItem().toString() + "'" +
                      " and cpr_type = " + "'LIST'" +
                      " and cpr_item = " + "'" + str[0] + "'" +
                      " and cpr_uom = " + "'" + str[1] + "'" +
                      " and cpr_curr = " + "'" + str[2] + "'" +        
                      ";");  
                }
               while (res.next()) {
                      dduom.setSelectedItem(res.getString("cpr_uom"));
                      ddcurr.setSelectedItem(res.getString("cpr_curr"));
                      dditem.setSelectedItem(res.getString("cpr_item"));
                      ddtype.setSelectedItem(res.getString("cpr_type"));
                      if (res.getString("cpr_expire") == null || res.getString("cpr_expire").isBlank() || res.getString("cpr_expire").equals("0000-00-00")) {
                        dcexpire.setDate(null);  
                      } else {
                        dcexpire.setDate(bsmf.MainFrame.dfdate.parse(res.getString("cpr_expire")));  
                      }
                      tbprice.setText(bsformat("s",res.getString("cpr_price").replace('.',defaultDecimalSeparator),"4"));
                      tbqty.setText(bsformat("s",res.getString("cpr_volqty").replace('.',defaultDecimalSeparator),"4"));
               }
               btAdd.setEnabled(false);
               btUpdate.setEnabled(true);
               btDelete.setEnabled(true);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_pricelistMouseClicked

    private void dduomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dduomActionPerformed
        setData();
    }//GEN-LAST:event_dduomActionPerformed

    private void ddcurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcurrActionPerformed
        setData();
    }//GEN-LAST:event_ddcurrActionPerformed

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
            String x = BlueSeerUtils.bsformat("", tbprice.getText(), "4");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
        if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        setData();
       
    }//GEN-LAST:event_dditemActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
        if (ddtype.getSelectedItem().toString().equals("VOLUME")) {
            tbqty.setEnabled(true);
           // btAdd.setEnabled(true);
        } else {
            tbqty.setEnabled(false);
           // btAdd.setEnabled(false);
        }
        setData();
    }//GEN-LAST:event_ddtypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btUpdate;
    private javax.swing.JButton btadddisc;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeletedisc;
    private javax.swing.JButton btupdatedisc;
    private com.toedter.calendar.JDateChooser dcexpire;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcustcode;
    private javax.swing.JComboBox ddcustcode_disc;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JComboBox<String> dduom;
    private javax.swing.JList disclist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbitem;
    private javax.swing.JLabel lbldisccode;
    private javax.swing.JLabel lblpricecode;
    private javax.swing.JLabel lblqty;
    private javax.swing.JLabel lbltype;
    private javax.swing.JList pricelist;
    private javax.swing.JTextField tbdisc;
    private javax.swing.JTextField tbdisckey;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    // End of variables declaration//GEN-END:variables
}
