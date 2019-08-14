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
package com.blueseer.ord;

import bsmf.MainFrame;
import com.blueseer.dst.*;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
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
import javax.swing.JOptionPane;


/**
 *
 * @author vaughnte
 */
public class ServiceOrderMaint extends javax.swing.JPanel {

      boolean isLoad = false;
    
   // OVData avmdata = new OVData();
    javax.swing.table.DefaultTableModel myorddetmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
             //   "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", "QtyShip", "Status", "WH", "LOC", "Desc", "Tax"
                "line", "Part", "Type", "Desc", "Order", "Qty", "Price"
            });
      
    
    
     public void getOrder(String mykey) {
        
        initvars(null);
        
        btbrowse.setEnabled(false);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(false);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
       
        
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from sv_mstr where sv_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    ordernbr.setText(mykey);
                    ordernbr.setEnabled(false);
                    remarks.setText("sv_rmks");
                    ddcust.setSelectedItem(res.getString("sv_cust"));
                    ddship.setSelectedItem(res.getString("sv_ship"));
                    ddtype.setSelectedItem(res.getString("sv_type"));
                    ddstatus.setSelectedItem(res.getString("sv_status"));
                    ddcrew.setSelectedItem(res.getString("sv_crew"));
                    duedate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sv_due_date")));
                    createdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("sv_create_date")));
                   
                   
                }
               
                 
                res = st.executeQuery("select * from svd_det where svd_nbr = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                  myorddetmodel.addRow(new Object[]{res.getString("svd_line"), res.getString("svd_item"),
                      res.getString("svd_type"), res.getString("svd_desc"), res.getString("svd_nbr"),
                      res.getString("svd_qty"), res.getString("svd_netprice")});
                }
                orddet.setModel(myorddetmodel);
               
                 
               
                
                setTotalHours();
                 sumlinecount();
               
                if (i == 0) {
                   bsmf.MainFrame.show("No Order Record found for " + mykey);
                } else {
                         if (ddstatus.getSelectedItem().toString().compareTo("closed") == 0 || ddstatus.getSelectedItem().toString().compareTo("void") == 0) {
                             disableAll();
                             btnew.setEnabled(true);
                         } else {
                             enableAll();
                              btadd.setEnabled(false);
                              btnew.setEnabled(false);
                         }
                         
                         ddtype.setEnabled(false);
                         
                         if (ddtype.getSelectedItem().toString().compareTo("order") == 0) {
                             btquotetoorder.setEnabled(false);
                             ordernbr.setBackground(null);
                         } else {
                             ordernbr.setBackground(Color.yellow);
                         }
                         
                         
                    
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve sv_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
     public void itemChangeEvent(String myitem) {
          
         lbdesc.setText(OVData.getItemDesc(dditem.getSelectedItem().toString()));
         tbprice.setText(String.valueOf(OVData.getItemPOSPrice(dditem.getSelectedItem().toString())));
     }
     
     public void custChangeEvent(String mykey) {
           
            ddship.removeAllItems();
            
            
           if (ddcust.getSelectedItem() == null || ddcust.getSelectedItem().toString().isEmpty() ) {
               ddcust.setBackground(Color.red);
           } else {
               ddcust.setBackground(null);
           }
            
           
            
            ArrayList mycusts = OVData.getcustshipmstrlist(ddcust.getSelectedItem().toString());
            for (int i = 0; i < mycusts.size(); i++) {
                ddship.addItem(mycusts.get(i));
            }
            ddship.insertItemAt("",0);
            
            if (ddship.getItemCount() == 1) {
              ddship.setBackground(Color.red); 
           }
            
            
            
            
        try {

             Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                res = st.executeQuery("select cm_name, cm_carrier, cm_tax_code, cm_curr from cm_mstr where cm_code = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    lblcustname.setText(res.getString("cm_name"));
                   
                }
                
             
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
  
      public void jobsiteChangeEvent(String mycust, String myship) {
           
            
            
           if (ddship.getSelectedItem() == null || ddship.getSelectedItem().toString().isEmpty() ) {
               ddship.setBackground(Color.red);
           } else {
               ddship.setBackground(null);
           }
            
            
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                String namestring = "";
                res = st.executeQuery("select cms_name, cms_line1, cms_city, cms_state, cms_zip from cms_det where cms_code = " + "'" + mycust + "'" + " and cms_shipto = " + "'" + myship + "'"  + ";");
                while (res.next()) {
                    namestring = res.getString("cms_name") + " " + res.getString("cms_line1") + " " + res.getString("cms_city") + "," + res.getString("cms_state") + " " + res.getString("cms_zip");
                    lblshipname.setText(namestring);
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("SQL Code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
  
     public String[] autoInvoiceOrder() {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
         String[] message = new String[2];
        message[0] = "";
        message[1] = ""; 
         int shipperid = OVData.getNextNbr("shipper");     
                     boolean error = OVData.CreateShipperHdr(String.valueOf(shipperid), ddsite.getSelectedItem().toString(),
                             String.valueOf(shipperid), 
                              ddcust.getSelectedItem().toString(),
                              ddship.getSelectedItem().toString(),
                              ordernbr.getText(),
                              tbpo.getText().replace("'", ""),  // po
                              tbpo.getText().replace("'", ""),  // ref
                              dfdate.format(duedate.getDate()).toString(),
                              dfdate.format(createdate.getDate()).toString(),
                              remarks.getText().replace("'", ""),
                              "", // shipvia
                              "S" ); 

                     if (error) {
                         return message = new String[]{"1", "Error creating service order shipper header"};
                     }  
                    
                    //  "line", "Part", "Order", "Qty", "Price"
                   //  nbr, part, custpart, skupart, so, po, qty, listprice, discpercent, netprice, shipdate, desc, line, site, wh, loc, taxamt
                     for (int j = 0; j < orddet.getRowCount(); j++) {
                             OVData.CreateShipperDet(String.valueOf(shipperid), orddet.getValueAt(j, 1).toString(), "", "", ordernbr.getText(), tbpo.getText().replace("'", ""), orddet.getValueAt(j, 5).toString(), 
                                     orddet.getValueAt(j, 6).toString(), "0", orddet.getValueAt(j, 6).toString(), dfdate.format(now), 
                                     orddet.getValueAt(j, 2).toString(), orddet.getValueAt(j, 0).toString(), ddsite.getSelectedItem().toString(), "", "", "0");
                         }
                    

                     // now confirm shipment
                     message = OVData.confirmShipment(String.valueOf(shipperid), now);
                     if (message[0].equals("1")) { // if error
                       return message;
                     } else {
                         OVData.updateServiceOrderFromShipper(String.valueOf(shipperid));
                       message = new String[]{"0", "Service Order has been invoiced"};
                     }
                    
                 return message;
    }
  
      
     public void sumlinecount() {
         totlines.setText(String.valueOf(orddet.getRowCount()));
    }
    
    public void setTotalHours() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Double.valueOf(orddet.getValueAt(j, 5).toString()); 
         }
         tbtotqty.setText(String.valueOf(qty));
    }
    
     public void setTotalPrice() {
        double qty = 0;
         for (int j = 0; j < orddet.getRowCount(); j++) {
             qty = qty + Double.valueOf(orddet.getValueAt(j, 6).toString()); 
         }
         tbtotdollars.setText(String.valueOf(qty));
    }
    
    public Integer getmaxline() {
        int max = 0;
        int current = 0;
        for (int j = 0; j < orddet.getRowCount(); j++) {
            current = Integer.valueOf(orddet.getValueAt(j, 0).toString()); 
            if (current > max) {
                max = current;
            }
         }
        return max;
    }
   
   
 
    /**
     * Creates new form OrderMaintPanel
     */
    public ServiceOrderMaint() {
        initComponents();
    }

     public void enableAll() {
        ordernbr.setEnabled(true);
        ddcust.setEnabled(true);
        ddship.setEnabled(true);
        duedate.setEnabled(true);
        tbpo.setEnabled(true);
       
        ddtype.setEnabled(true);
        ddstatus.setEnabled(true);
        ddcrew.setEnabled(true);
      
        remarks.setEnabled(true);
        serviceitem.setEnabled(true);
        tbprice.setEnabled(true);
        tbhours.setEnabled(true);
       dditem.setEnabled(true);
       tbqty.setEnabled(true);
        ddsite.setEnabled(true);
        btinvoice.setEnabled(true);
        
        orddet.setEnabled(true);
        
        totlines.setEnabled(true);
        tbtotqty.setEnabled(true);
        tbtotdollars.setEnabled(true);
       
        
          btbrowse.setEnabled(true);
        
          btpoprint.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
         btquotetoorder.setEnabled(true);
        
    }
    
     public void disableAll() {
        ordernbr.setEnabled(false);
        ddcust.setEnabled(false);
        ddship.setEnabled(false);
        duedate.setEnabled(false);
      ddstatus.setEnabled(false);
      ddcrew.setEnabled(false);
        ddtype.setEnabled(false);
        ddsite.setEnabled(false);
        btinvoice.setEnabled(false);
       tbpo.setEnabled(false);
        remarks.setEnabled(false);
        serviceitem.setEnabled(false);
        tbprice.setEnabled(false);
        tbhours.setEnabled(false);
       createdate.setEnabled(false);
        tbpo.setEnabled(false);
       dditem.setEnabled(false);
       tbqty.setEnabled(false);
        
        orddet.setEnabled(false);
        
        totlines.setEnabled(false);
        tbtotqty.setEnabled(false);
        tbtotdollars.setEnabled(false);
       
        
          btbrowse.setEnabled(false);
         
          btpoprint.setEnabled(false);
        btnew.setEnabled(false);
        btedit.setEnabled(false);
        btadd.setEnabled(false);
        btadditem.setEnabled(false);
        btdelitem.setEnabled(false);
         btquotetoorder.setEnabled(false);
    }
    
    public void initvars(String[] arg) {
       
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        ordernbr.setEnabled(true);
        ordernbr.setText("");
        ordernbr.setEditable(false);
        ordernbr.setBackground(null);
     
        tbhours.setText("0");
        tbprice.setText("0.00");  
        
        
        lblcustname.setText("");
        lblshipname.setText("");
        duedate.setDate(now);
        
       
        myorddetmodel.setRowCount(0);
        orddet.setModel(myorddetmodel);
       
        
        btbrowse.setEnabled(true);
        btnew.setEnabled(true);
        btedit.setEnabled(true);
        btadd.setEnabled(true);
        btadditem.setEnabled(true);
        btdelitem.setEnabled(true);
        ddtype.setEnabled(false);
      
       
        remarks.setText("");
        tbtotqty.setText("");
        totlines.setText("");
        tbtotdollars.setText("");
        
        
        ddtype.setSelectedIndex(0);
        
        isLoad = true;
        
        ddcust.removeAllItems();
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
        ArrayList mycusts = OVData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddcust.addItem(mycusts.get(i));
        }
        ddship.removeAllItems();
        
        
         dditem.removeAllItems();
         ArrayList<String> items = OVData.getItemMasterAlllist();
         for (String item : items) {
         dditem.addItem(item);
         }
          dditem.insertItemAt("", 0);
         dditem.setSelectedIndex(0);
         
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        isLoad = false;
        
        
        if (arg != null && arg.length > 0) {
            getOrder(arg[0]);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btbrowse.setEnabled(true);
              ordernbr.setEnabled(true);
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

        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        ordernbr = new javax.swing.JTextField();
        duedate = new com.toedter.calendar.JDateChooser();
        btnew = new javax.swing.JButton();
        btadditem = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        orddet = new javax.swing.JTable();
        btdelitem = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        btedit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tbhours = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        serviceitem = new javax.swing.JTextArea();
        dditem = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        lbdesc = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        totlines = new javax.swing.JTextField();
        tbtotqty = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        btpoprint = new javax.swing.JButton();
        btbrowse = new javax.swing.JButton();
        tbpo = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        btquotetoorder = new javax.swing.JButton();
        ddcrew = new javax.swing.JComboBox<>();
        jLabel93 = new javax.swing.JLabel();
        tbtotdollars = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        ddcust = new javax.swing.JComboBox();
        ddship = new javax.swing.JComboBox();
        lblcustname = new javax.swing.JLabel();
        lblshipname = new javax.swing.JLabel();
        btnewcust = new javax.swing.JButton();
        btnewsite = new javax.swing.JButton();
        ddstatus = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        createdate = new com.toedter.calendar.JDateChooser();
        jLabel83 = new javax.swing.JLabel();
        btinvoice = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        jLabel9.setText("jLabel9");

        setBackground(new java.awt.Color(0, 102, 204));
        add(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Service Order Maintenance"));

        jLabel76.setText("OrderNbr");

        jLabel86.setText("Remarks");

        duedate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        jLabel85.setText("Type");

        jLabel82.setText("Customer");

        orddet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane8.setViewportView(orddet);

        btdelitem.setText("Del Item");
        btdelitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelitemActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "quote", "order" }));

        btadd.setText("Save");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel81.setText("Due Date");

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        tbhours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbhoursFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbhoursFocusLost(evt);
            }
        });

        jLabel79.setText("Service Item");

        jLabel84.setText("Hours");

        tbprice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbpriceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpriceFocusLost(evt);
            }
        });

        jLabel3.setText("Price");

        serviceitem.setColumns(20);
        serviceitem.setRows(5);
        jScrollPane2.setViewportView(serviceitem);

        dditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dditemActionPerformed(evt);
            }
        });

        jLabel8.setText("InventoryItem");

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel10.setText("Qty");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel79)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(87, 87, 87)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel84))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel79)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbhours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(dditem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap())
        );

        jLabel1.setText("Total Lines");

        jLabel2.setText("Total Hrs");

        jLabel91.setText("Job Site");

        btpoprint.setText("Print");
        btpoprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpoprintActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jLabel92.setText("PO");

        remarks.setColumns(20);
        remarks.setRows(5);
        jScrollPane3.setViewportView(remarks);

        btquotetoorder.setText("QuoteToOrder");
        btquotetoorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btquotetoorderActionPerformed(evt);
            }
        });

        jLabel93.setText("Crew");

        jLabel5.setText("Total");

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        ddship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshipActionPerformed(evt);
            }
        });

        btnewcust.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnewcust.setToolTipText("");
        btnewcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewcustActionPerformed(evt);
            }
        });

        btnewsite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btnewsite.setToolTipText("");
        btnewsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewsiteActionPerformed(evt);
            }
        });

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "open", "processing", "closed", "void" }));

        jLabel6.setText("Status");

        createdate.setDateFormatString("yyyy-MM-dd");

        jLabel83.setText("Crt Date");

        btinvoice.setText("Invoice");
        btinvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btinvoiceActionPerformed(evt);
            }
        });

        jLabel7.setText("Site");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 15, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdelitem))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(63, 63, 63))
                                    .addComponent(totlines, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5)
                                .addGap(3, 3, 3)
                                .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(btpoprint)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btedit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btadd))))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel76)
                            .addComponent(jLabel86)
                            .addComponent(jLabel85)
                            .addComponent(jLabel82)
                            .addComponent(jLabel91)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btquotetoorder)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btinvoice)
                                .addGap(13, 13, 13))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(247, 247, 247)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(ddcrew, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(92, 92, 92))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbpo, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblshipname, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(ddcust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(ddtype, 0, 133, Short.MAX_VALUE)
                                                    .addComponent(ddstatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(85, 85, 85)
                                                        .addComponent(jLabel93))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(btnewcust, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel81))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(btnewsite, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel92))))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                                                .addComponent(jLabel83)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(createdate, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                                            .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(btquotetoorder)
                        .addComponent(btinvoice)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ordernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel76))
                        .addComponent(btbrowse)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblcustname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel82)
                        .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel81))
                    .addComponent(duedate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnewcust, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblshipname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel91)
                        .addComponent(ddship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel92))
                    .addComponent(btnewsite, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85)
                    .addComponent(ddcrew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel93))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totlines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btpoprint)
                    .addComponent(tbtotdollars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
     
         initvars(null);
        
        
         ordernbr.setText(String.valueOf(OVData.getNextNbr("serviceorder")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
           
                duedate.setDate(now);
                createdate.setDate(now);
                
                // tbDateShippedSM.setEnabled(false);
                
                enableAll();
                
                ddtype.setEnabled(true);
                btnew.setEnabled(false);
                btbrowse.setEnabled(false);
                btedit.setEnabled(false);
                btpoprint.setEnabled(false);
               
                
                 
               
               
       
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
         boolean canproceed = true;
        int line = 0;
        orddet.setModel(myorddetmodel);
        line = getmaxline();
        line++;
        if (canproceed) {
         if (dditem.getSelectedIndex() > 0) {
          myorddetmodel.addRow(new Object[]{line, dditem.getSelectedItem().toString(), "I", lbdesc.getText(), ordernbr.getText(),  tbqty.getText(), tbprice.getText()});  
         } else {
          myorddetmodel.addRow(new Object[]{line, serviceitem.getText(), "S", "", ordernbr.getText(),  tbhours.getText(), tbprice.getText()});   
         }
         
         setTotalHours();
         sumlinecount();
         setTotalPrice();
         serviceitem.setText("");
         dditem.setSelectedIndex(0);
         serviceitem.requestFocus();
        }
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
           
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                String site = OVData.getDefaultSite();
                if (proceed) {
                    st.executeUpdate("insert into sv_mstr "
                        + "(sv_nbr, sv_cust, sv_ship, sv_site, sv_po, sv_due_date, sv_create_date, sv_type, sv_status, sv_rmks ) "
                        + " values ( " + "'" + ordernbr.getText() + "'" + ","
                        + "'" + ddcust.getSelectedItem().toString() + "'" + ","
                        + "'" + ddship.getSelectedItem().toString() + "'" + ","
                        + "'" + site + "'" + ","
                        + "'" + tbpo.getText() + "'" + ","
                        + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(now).toString() + "'" + ","        
                        + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                        + "'" + "open" + "'" + ","
                        + "'" + remarks.getText().replace("'", "") + "'"
                        + ")"
                        + ";");

                  
                 
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                        st.executeUpdate("insert into svd_det "
                            + "(svd_line, svd_item, svd_type, svd_desc, svd_nbr, svd_qty, svd_netprice ) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","        
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" 
                            + ")"
                            + ";");

                    }
                    bsmf.MainFrame.show("Service Order has been added");
                   initvars(new String[]{ordernbr.getText()});
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot add Service Order");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btdelitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelitemActionPerformed
        /*
        int[] rows = orddet.getSelectedRows();
        for (int i : rows) {
            if (orddet.getValueAt(i, 10).toString().equals("close") || orddet.getValueAt(i, 10).toString().equals("partial")) {
                bsmf.MainFrame.show("Cannot Delete Closed or Partial Item");
                return;
                            } else {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) orddet.getModel()).removeRow(i);
            }
        }
       
         sumqty();
         sumlinecount();
         */
    }//GEN-LAST:event_btdelitemActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
         try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
              
               
                if (proceed) {
                    st.executeUpdate("update sv_mstr "
                        + " set sv_ship = " + "'" + ddship.getSelectedItem() + "'" + ","
                        + " sv_po = " + "'" + tbpo.getText().replace("'", "") + "'" + ","
                        + " sv_rmks = " + "'" + remarks.getText().replace("'", "") + "'" + ","        
                        + " sv_status = " + "'" + ddstatus.getSelectedItem() + "'" + ","
                        + " sv_due_date = " + "'" + dfdate.format(duedate.getDate()).toString() + "'" + ","
                        + " sv_crew = " + "'" + ddcrew.getSelectedItem() + "'" 
                        + " where sv_nbr = " + "'" + ordernbr.getText().toString() + "'"
                        + ";");

                  //  "Line", "Part", "CustPart", "SO", "PO", "Qty", "ListPrice", "Discount", "NetPrice", shippedqty, status
                   
                    
                    // if available sod_det line item...then update....else insert
                    for (int j = 0; j < orddet.getRowCount(); j++) {
                         i = 0;
                        // skip closed lines
                        if (orddet.getValueAt(j, 10).toString().equals("close"))
                            continue;
                        res = st.executeQuery("Select svd_line from svd_det where svd_nbr = " + "'" + ordernbr.getText() + "'" +
                                " and svd_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'" + ";" );
                            while (res.next()) {
                            i++;
                            }
                            if (i > 0) {
                              st.executeUpdate("update svd_det set "
                            + " svd_item = " + "'" + orddet.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + " svd_qty = " + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + " svd_netprice = " + "'" + orddet.getValueAt(j, 6).toString() + "'"
                            + " where svd_nbr = " + "'" + ordernbr.getText() + "'" 
                            + " AND svd_line = " + "'" + orddet.getValueAt(j, 0).toString() + "'"
                            + ";");
                            } else {
                            st.executeUpdate("insert into svd_det "
                            + "(svd_line, svd_item, svd_nbr, svd_qty, svd_netprice ) "
                            + " values ( " 
                            + "'" + orddet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + orddet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 3).toString() + "'" + ","               
                            + "'" + orddet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + orddet.getValueAt(j, 6).toString() + "'" 
                            + ")"
                            + ";");
                            }

                    }
                    
                  
                   
                    
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to insert svd_det");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btpoprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpoprintActionPerformed
       OVData.printServiceOrder(ordernbr.getText());
    }//GEN-LAST:event_btpoprintActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "svmaint,sv_nbr");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed

        if (ddcust.getItemCount() > 0) {
            custChangeEvent(ddcust.getSelectedItem().toString());

        } // if ddcust has a list

    }//GEN-LAST:event_ddcustActionPerformed

    private void ddshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshipActionPerformed
        if (ddship.getItemCount() > 0) {
            jobsiteChangeEvent(ddcust.getSelectedItem().toString(), ddship.getSelectedItem().toString());

        } // if ddcust has a list
    }//GEN-LAST:event_ddshipActionPerformed

    private void btnewcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewcustActionPerformed
        reinitpanels("CustMaint", true, "");
    }//GEN-LAST:event_btnewcustActionPerformed

    private void btnewsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewsiteActionPerformed
       reinitpanels("CustMaint", true, ddcust.getSelectedItem().toString());
    }//GEN-LAST:event_btnewsiteActionPerformed

    private void tbpriceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusGained
         if (tbprice.getText().equals("0")) {
            tbprice.setText("");
        }
    }//GEN-LAST:event_tbpriceFocusGained

    private void tbpriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpriceFocusLost
              String x = BlueSeerUtils.bsformat("", tbprice.getText(), "2");
        if (x.equals("error")) {
            tbprice.setText("");
            tbprice.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbprice.requestFocus();
        } else {
            tbprice.setText(x);
            tbprice.setBackground(Color.white);
        }
        setTotalPrice();
    }//GEN-LAST:event_tbpriceFocusLost

    private void tbhoursFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbhoursFocusGained
          if (tbhours.getText().equals("0")) {
            tbhours.setText("");
        }
    }//GEN-LAST:event_tbhoursFocusGained

    private void tbhoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbhoursFocusLost
              String x = BlueSeerUtils.bsformat("", tbhours.getText(), "2");
        if (x.equals("error")) {
            tbhours.setText("");
            tbhours.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbhours.requestFocus();
        } else {
            tbhours.setText(x);
            tbhours.setBackground(Color.white);
        }
        setTotalHours();
    }//GEN-LAST:event_tbhoursFocusLost

    private void btquotetoorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btquotetoorderActionPerformed
        try {

           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
           
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                    st.executeUpdate("update sv_mstr set sv_type = 'order' where sv_nbr = " + "'" + ordernbr.getText() + "'" );
                    bsmf.MainFrame.show("Quote has been confirmed to Order");
                   initvars(new String[]{ordernbr.getText()});
                    // btQualProbAdd.setEnabled(false);
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot update Service Order");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btquotetoorderActionPerformed

    private void btinvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btinvoiceActionPerformed
          String[] message = autoInvoiceOrder();
         if (message[0].equals("1")) { // if error
           bsmf.MainFrame.show(message[1]);
         } else {
           getOrder(ordernbr.getText());
         }
    }//GEN-LAST:event_btinvoiceActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbqtyFocusLost

    private void dditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dditemActionPerformed
        if (dditem.getSelectedItem() != null && ! isLoad)
        itemChangeEvent(dditem.getSelectedItem().toString());
    }//GEN-LAST:event_dditemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btinvoice;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btnewcust;
    private javax.swing.JButton btnewsite;
    private javax.swing.JButton btpoprint;
    private javax.swing.JButton btquotetoorder;
    private com.toedter.calendar.JDateChooser createdate;
    private javax.swing.JComboBox<String> ddcrew;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox<String> dditem;
    private javax.swing.JComboBox ddship;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddstatus;
    private javax.swing.JComboBox ddtype;
    private com.toedter.calendar.JDateChooser duedate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbdesc;
    private javax.swing.JLabel lblcustname;
    private javax.swing.JLabel lblshipname;
    private javax.swing.JTable orddet;
    private javax.swing.JTextField ordernbr;
    private javax.swing.JTextArea remarks;
    private javax.swing.JTextArea serviceitem;
    private javax.swing.JTextField tbhours;
    private javax.swing.JTextField tbpo;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbtotdollars;
    private javax.swing.JTextField tbtotqty;
    private javax.swing.JTextField totlines;
    // End of variables declaration//GEN-END:variables
}
