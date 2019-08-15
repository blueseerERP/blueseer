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
package com.blueseer.far;

import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import static com.blueseer.utl.OVData.getDueDateFromTerms;
import java.awt.Color;


/**
 *
 * @author vaughnte
 */
public class ARPaymentMaint extends javax.swing.JPanel {

                String terms = "";
                String aracct = "";
                String arcc = "";
                String arbank = "";
                double actamt = 0.00;
                double control = 0.00;
                double baseamt = 0.00;
                double rcvamt = 0.00;
                String curr = "";
                
                boolean isInit = false;
                
                  javax.swing.table.DefaultTableModel referencemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Reference", "Type", "DueDate", "Amount", "AmtApplied", "AmtOpen", "Tax", "Curr"});
    javax.swing.table.DefaultTableModel armodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Reference", "AmountToApply", "TaxAmount", "Curr"
            });
                
                
                
    /**
     * Creates new form ShipMaintPanel
     */
    public ARPaymentMaint() {
        initComponents();
      
        
       
       
    }
   
    
    public void clearAll() {
        
         terms = "";
         aracct = "";
         arcc = "";
         arbank = "";
         actamt = 0.00;
         control = 0.00;
         rcvamt = 0.00;
        
        lbcust.setText("");
        lbmessage.setText("");
        lbmessage.setForeground(Color.blue);
        
        batchnbr.setText(""); 
        
        tbrmks.setText("");
        tbcontrolamt.setText("0");
        tbcontrolamt.setBackground(Color.white);
        tbcheck.setText("");
        tbactualamt.setText("0");
        tbactualamt.setBackground(Color.white);
        tbrefamt.setText("0");
        referencemodel.setRowCount(0);
        armodel.setRowCount(0);
        referencedet.setModel(referencemodel);
        ardet.setModel(armodel);
        
       
        
        java.util.Date now = new java.util.Date();
        dcdate.setEnabled(true);
        dcdate.setDate(now);
        
        
        isInit = true;
        ddcust.removeAllItems();
        ArrayList mycust = OVData.getcustmstrlist();
        for (int i = 0; i < mycust.size(); i++) {
            ddcust.addItem(mycust.get(i));
        }
        ddcust.insertItemAt("", 0);
        ddcust.setSelectedIndex(0);
          
        ddsite.removeAllItems();
        ArrayList mylist = OVData.getSiteList();
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddcurr.removeAllItems();
        ArrayList<String> curr = OVData.getCurrlist();
        for (int i = 0; i < curr.size(); i++) {
            ddcurr.addItem(curr.get(i));
        }
        ddcurr.setSelectedItem(OVData.getCustCurrency(ddcust.getSelectedItem().toString()));
        
        isInit = false;
       
        
        
    }
    
    public void enableAll() {
        ddcust.setEnabled(true);
        ddsite.setEnabled(true);
        ddcurr.setEnabled(true);
        batchnbr.setEnabled(true);
        tbrmks.setEnabled(true);
        tbcontrolamt.setEnabled(true);
        tbcheck.setEnabled(true);
        tbactualamt.setEnabled(true);
        tbrefamt.setEnabled(true);
        referencedet.setEnabled(true);
        ardet.setEnabled(true);
        
        btadditem.setEnabled(true);
        btdeleteitem.setEnabled(true);
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
        btadd.setEnabled(true);
        btedit.setEnabled(true);  
        btaddall.setEnabled(true);
        
        dcdate.setEnabled(true);
    }
    
    public void disableAll() {
         ddcust.setEnabled(false);
        ddsite.setEnabled(false);
        ddcurr.setEnabled(false);
        batchnbr.setEnabled(false);
        tbrmks.setEnabled(false);
        tbcontrolamt.setEnabled(false);
        tbactualamt.setEditable(false);
        tbcheck.setEnabled(false);
        tbactualamt.setEnabled(false);
        tbrefamt.setEnabled(false);
        tbrefamt.setEditable(false);
        referencedet.setEnabled(false);
        ardet.setEnabled(false);
        
        btadditem.setEnabled(false);
        btdeleteitem.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        btadd.setEnabled(false);
        btedit.setEnabled(false);
        btaddall.setEnabled(false);
        
        dcdate.setEnabled(false); 
    }
    
    public boolean isValidInput() {
        boolean myreturn = true;
       
        return myreturn;
    }
    
    public void initvars(String[] arg) {
        
        
         clearAll();
         disableAll();
         btnew.setEnabled(true);
         btbrowse.setEnabled(true);
       
         if (arg != null && arg.length > 0) {
            getBatch(arg[0]);
        }
        
        
       
        
        // ddcust.setEnabled(false); 
        
    }
    
    public void getBatch(String batch) {
         try {
            DecimalFormat df = new DecimalFormat("#0.00");  
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            boolean gotIt = false;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                actamt = 0.00;
                res = st.executeQuery("select * from ar_mstr where ar_nbr = " + "'" + batch + "'" + ";");
                while (res.next()) {
                  // "Reference", "AmountToApply", "TaxAmount", "Curr"
                     batchnbr.setText(res.getString("ar_nbr"));
                     dcdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("ar_effdate")));
                     tbcheck.setText(res.getString("ar_ref"));
                     tbrmks.setText(res.getString("ar_rmks"));
                     ddcust.setSelectedItem(res.getString("ar_cust"));
                     ddsite.setSelectedItem(res.getString("ar_site"));
                     ddcurr.setSelectedItem(res.getString("ar_curr"));
                     gotIt = true;
                }
                
                res = st.executeQuery("select * from ard_mstr where ard_id = " + "'" + batch + "'" + ";");
                while (res.next()) {
                  // "Reference", "AmountToApply", "TaxAmount", "Curr"
                     armodel.addRow(new Object[] { res.getString("ard_ref"),
                                              res.getString("ard_amt"),
                                              res.getString("ard_amt_tax"),
                                              res.getString("ard_curr")
                                              });
                 
                  
                  actamt += res.getDouble("ard_amt");
                d++;
                }
                
                 if (gotIt) {
                tbactualamt.setText(df.format(actamt));
                tbcontrolamt.setText(df.format(actamt));
                lbmessage.setText("Batch has been committed");
                enableAll();
                btadd.setEnabled(false);
                } else {
                 lbmessage.setText("Unable to find batch");   
                }
                
                
             
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot retrieve AR Batch");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    
  
     
      
      public void setcustvariables(String cust) {
        
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select cm_ar_acct, cm_ar_cc, cm_terms, cm_bank from cm_mstr where cm_code = " + "'" + cust + "'" + ";");
                while (res.next()) {
                    i++;
                   aracct = res.getString("cm_ar_acct");
                   arcc = res.getString("cm_ar_cc");
                   terms = res.getString("cm_terms");
                   arbank = res.getString("cm_bank");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("cannot retrieve from cm_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
      
      public void getreferences(String cust) {
        referencemodel.setRowCount(0);
        try {
            DecimalFormat df = new DecimalFormat("#0.00");  
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                rcvamt = 0.00;
                res = st.executeQuery("select * from ar_mstr where ar_cust = " + "'" + cust + "'" +
                        " AND ar_curr = " + "'" + curr + "'" + 
                        " AND ar_status = 'o' " + ";");
                while (res.next()) {
                    // "Reference", "Type", "DueDate", "Amount", "AmtApplied", "AmtOpen"
                  referencemodel.addRow(new Object[]{res.getString("ar_nbr"), 
                      res.getString("ar_type"), 
                      res.getString("ar_duedate"), 
                      res.getDouble("ar_amt"), 
                      res.getDouble("ar_applied"), 
                      df.format(res.getDouble("ar_open_amt")),
                      df.format(res.getDouble("ar_amt_tax")),
                      res.getString("ar_curr")});
                  
                  rcvamt += res.getDouble("ar_open_amt");
                d++;
                }
                tbrefamt.setText(df.format(rcvamt));
                referencedet.setModel(referencemodel);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Get AR / Memo References");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

           
 public void setstatus(javax.swing.JTable mytable) throws SQLException {
            
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
             ResultSet res = null;
             Statement st = bsmf.MainFrame.con.createStatement();
             String sonbr = null;
             int qty = 0;
             boolean iscomplete = true;
             String sodstatus = "";
             
        // find the sod record for each line / So pair
        for (int j = 0; j < mytable.getRowCount(); j++) {
               //    mytable.getModel().getValueAt(j, 0);  
             i = 0;
             String thispart = mytable.getModel().getValueAt(j, 0).toString();
             String thisorder = mytable.getModel().getValueAt(j, 1).toString();
             String thispo = mytable.getModel().getValueAt(j, 2).toString();
             int thisrecvqty = Integer.valueOf(mytable.getModel().getValueAt(j, 3).toString());
             String thislinestatus = "";
             int thisrecvpedtotal = 0;
            
             try {
            
                 /* ok....let's get the current state of this line item on the sales order */
                 res = st.executeQuery("select * from sod_det where sod_nbr = " + "'" + thisorder + "'" + 
                                     " AND sod_part = " + "'" + thispart + "'" + ";");
               while (res.next()) {     
                 i++;
                   if (Integer.valueOf(res.getString("sod_recvped_qty") + thisrecvqty) < Integer.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Partial"; 
                   }
                   if (Integer.valueOf(res.getString("sod_recvped_qty") + thisrecvqty) >= Integer.valueOf(res.getString("sod_ord_qty")) ) {
                   thislinestatus = "Shipped"; 
                   }
                   thisrecvpedtotal = thisrecvqty + Integer.valueOf(res.getString("sod_recvped_qty"));
                   
                }
                 
                 
                 
                 /* ok...now lets update the status of this sod_det line item */
                 st.executeUpdate("update sod_det set sod_recvped_qty = " + "'" + thisrecvpedtotal + "'" + 
                                  "," + " sod_status = " + "'" + thislinestatus + "'" +
                                  " where sod_nbr = " + "'" + thisorder + "'" + 
                                  " and sod_part = " + "'" + thispart + "'" + 
                                  " and sod_po = " + "'" + thispo + "'" +
                     ";");
                 
                 
                 
             } catch (SQLException s) {
                 MainFrame.bslog(s);
                 bsmf.MainFrame.show("Unable to update sod_det");
             }
              // JOptionPane.showMessageDialog(mydialog, mytable.getModel().getValueAt(j,1).toString());
              
                
            //if (iscomplete) {
            //    st.executeUpdate("update so_mstr set so_status = 'Shipped' where so_nbr = " + "'" + mytable.getModel().getValueAt(j, 1).toString() + "'" );
           // }
            
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
        jPanel1 = new javax.swing.JPanel();
        batchnbr = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        ardet = new javax.swing.JTable();
        ddcust = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        referencedet = new javax.swing.JTable();
        tbcheck = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        btaddall = new javax.swing.JButton();
        tbrefamt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        ddcurr = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        lbmessage = new javax.swing.JLabel();
        lbcust = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("AR Payment Maintenance"));

        jLabel24.setText("Batch Nbr");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        tbcontrolamt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcontrolamtFocusLost(evt);
            }
        });

        jLabel36.setText("Billto");

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Commit");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        ardet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(ardet);

        ddcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcustActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btedit.setText("Uncommit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Control Amt");

        jLabel35.setText("EffDate");

        referencedet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane8.setViewportView(referencedet);

        jLabel2.setText("CheckNbr");

        btaddall.setText("Add All");
        btaddall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddallActionPerformed(evt);
            }
        });

        jLabel3.setText("Ref Total");

        jLabel4.setText("Rmks");

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel37.setText("Site");

        ddcurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcurrActionPerformed(evt);
            }
        });

        jLabel38.setText("Currency");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jLabel5.setText("ActualAmt");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteitem))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(9, 9, 9)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel24)
                                .addComponent(jLabel36)
                                .addComponent(jLabel4)
                                .addComponent(jLabel37)
                                .addComponent(jLabel38))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbrmks)
                                    .addGap(215, 215, 215))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(batchnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(26, 26, 26)
                                            .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(btnew))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(ddcurr, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(ddcust, javax.swing.GroupLayout.Alignment.LEADING, 0, 119, Short.MAX_VALUE)
                                                .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, 119, Short.MAX_VALUE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(lbcust, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(jLabel3)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(tbrefamt, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(btaddall)
                                                    .addGap(14, 14, 14))
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(tbactualamt)
                                                                .addComponent(tbcontrolamt, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                                                            .addGap(10, 10, 10)
                                                            .addComponent(jLabel2)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGap(22, 22, 22))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(lbmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 105, Short.MAX_VALUE))))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnew)
                                .addComponent(batchnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24))
                            .addComponent(btbrowse))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ddcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36)
                            .addComponent(lbcust, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcurr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27)
                            .addComponent(tbcheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35))
                        .addGap(9, 9, 9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrefamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btaddall))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteitem)
                    .addComponent(btadditem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit))
                .addGap(35, 35, 35))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
                
        enableAll();
        clearAll();
        btedit.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        batchnbr.setText(String.valueOf(OVData.getNextNbr("ar")));
        batchnbr.setEnabled(false);
               
               
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        DecimalFormat df = new DecimalFormat("#0.00"); 
              
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // "Reference", "Type", "DueDate", "Amount", "AmtApplied", "AmtOpen"
       // "Reference" "Amount"
         int[] rows = referencedet.getSelectedRows();
        for (int i : rows) {
            actamt += Double.valueOf(referencedet.getModel().getValueAt(i,5).toString());
            
           armodel.addRow(new Object[] { referencedet.getModel().getValueAt(i, 0),
                                              referencedet.getModel().getValueAt(i, 5),
                                              referencedet.getModel().getValueAt(i, 6),
                                              referencedet.getModel().getValueAt(i, 7)
                                              });
        }
         if (ardet.getRowCount() >= 1) {
             ddcurr.setEnabled(false);
           }
         if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
        tbactualamt.setText(df.format(actamt));
    }//GEN-LAST:event_btadditemActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
    
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                boolean error = false;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                DecimalFormat df = new DecimalFormat("#0.00");   
                setcustvariables(ddcust.getSelectedItem().toString());
                   
                 if (tbcheck.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Check Number cannot be blank.");
                    return;
                }
                if (arbank.isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("There is no Bank assigned for this cust");
                }
                if (arcc.isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("There is no Cost Center assigned for this cust");
                }
                if (aracct.isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("There is no AR Account assigned for this cust");
                }
                
                String basecurr = OVData.getDefaultCurrency();
                
                if (basecurr.toUpperCase().equals(ddcurr.getSelectedItem().toString().toUpperCase())) {
                  baseamt = actamt;  
                } else {
                  baseamt = OVData.getExchangeBaseValue(basecurr, ddcurr.getSelectedItem().toString(), actamt);
                }
                    if (proceed) {
                                        
                      st.executeUpdate("insert into ar_mstr "
                        + "(ar_cust, ar_nbr, ar_amt, ar_base_amt, ar_type, ar_curr, ar_base_curr, ar_ref, ar_rmks, "
                        + "ar_entdate, ar_effdate, ar_paiddate, ar_acct, ar_cc, "
                        + "ar_status, ar_bank, ar_site ) "
                        + " values ( " + "'" + ddcust.getSelectedItem() + "'" + ","
                        + "'" + batchnbr.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + df.format(baseamt) + "'" + ","
                        + "'" + "P" + "'" + ","
                        + "'" + ddcurr.getSelectedItem().toString() + "'" + ","      
                        + "'" + basecurr + "'" + ","
                        + "'" + tbcheck.getText().replace("'", "''") + "'" + ","
                        + "'" + tbrmks.getText().replace("'", "''") + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + aracct + "'" + ","
                        + "'" + arcc + "'" + ","
                        + "'" + "c" + "'"  + ","
                        + "'" + arbank + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'"
                        + ")"
                        + ";");
                double amt_d = 0;
                double taxamt_d = 0;
                double baseamt_d = 0;
                double basetaxamt_d = 0;
               // "Reference", "Type", "Date", "Amount"
                    for (int j = 0; j < ardet.getRowCount(); j++) {
                        amt_d = Double.valueOf(ardet.getValueAt(j, 1).toString());
                        taxamt_d = Double.valueOf(ardet.getValueAt(j, 2).toString());
                         if (basecurr.toUpperCase().equals(ddcurr.getSelectedItem().toString().toUpperCase())) {
                         baseamt_d = amt_d;
                         basetaxamt_d = taxamt_d;
                         } else {
                         baseamt_d = OVData.getExchangeBaseValue(basecurr, ddcurr.getSelectedItem().toString(), amt_d);
                         basetaxamt_d = OVData.getExchangeBaseValue(basecurr, ddcurr.getSelectedItem().toString(), taxamt_d);
                         }
                        st.executeUpdate("insert into ard_mstr "
                            + "(ard_id, ard_cust, ard_ref, ard_line, ard_date, ard_amt, ard_amt_tax, ard_base_amt, ard_base_amt_tax, ard_curr, ard_base_curr ) "
                            + " values ( " + "'" + batchnbr.getText() + "'" + ","
                                + "'" + ddcust.getSelectedItem() + "'" + ","
                            + "'" + ardet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + (j + 1) + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + df.format(amt_d) + "'"  + ","
                            + "'" + df.format(taxamt_d) + "'"  + ","
                            + "'" + df.format(baseamt_d) + "'"  + ","                
                            + "'" + df.format(basetaxamt_d) + "'" + "," 
                            + "'" + ddcurr.getSelectedItem().toString() + "'"  + ","
                            + "'" + basecurr + "'" 
                            + ")"
                            + ";");
                    }
                    
                  // update AR entry for original invoices with status and open amt  
                     error = OVData.ARUpdate(batchnbr.getText());
                    
                    /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromARPayment(batchnbr.getText(), dcdate.getDate());
                    if (error) {
                        bsmf.MainFrame.show("An error occurred");
                    } else {
                    bsmf.MainFrame.show("AR Payment Complete");
                    initvars(null);
                    }
               
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("cannot insert into ard_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void ddcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcustActionPerformed
        // clean slate
        referencemodel.setRowCount(0);
        armodel.setRowCount(0);
        lbcust.setText("");
        if ( ddcust.getSelectedItem() != null && ! ddcust.getSelectedItem().toString().isEmpty()  && ! isInit) {
        ddcurr.setSelectedItem(OVData.getCustCurrency(ddcust.getSelectedItem().toString()));
        lbcust.setText(OVData.getCustName(ddcust.getSelectedItem().toString()));
        getreferences(ddcust.getSelectedItem().toString());
        }
    }//GEN-LAST:event_ddcustActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = ardet.getSelectedRows();
        DecimalFormat df = new DecimalFormat("#0.00");  
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
             actamt -= Double.valueOf(ardet.getModel().getValueAt(i,1).toString());
            ((javax.swing.table.DefaultTableModel) ardet.getModel()).removeRow(i);
        }
        tbactualamt.setText(df.format(actamt));
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                bsmf.MainFrame.show("This rollback functionality has not been implemented yet");
        /*
                if (proceed) {
                    st.executeUpdate("update recv_mstr set recv_po = " + "'" + ddrefs.getSelectedItem().toString() + "'" + ","
                        + "recv_create_date = " + "'" + tbcontrolamt.getText() + "'" + ","
                        + "recv_recv_date = " + "'" + dfdate.format(dcdate.getDate()) + "'"
                        + " where recv_id = " + "'" + batchnbr.getText().toString() + "'"
                        + ";");
                    // delete the sod_det records and add back.
                    st.executeUpdate("delete from recv_det where rvdet_id = " + "'" + batchnbr.getText() + "'"  );
                    for (int j = 0; j < ardet.getRowCount(); j++) {
                        st.executeUpdate("insert into recv_det "
                            + "(rvdet_id, rvdet_part, rvdet_so, rvdet_recv_qty,"
                            + "rvdet_recv_price, rvdet_po, rvdet_create_date,"
                            + "rvdet_rvdet_date, rvdet_char1, rvdet_char2, rvdet_char3) "
                            + " values ( " + "'" + batchnbr.getText() + "'" + ","
                            + "'" + ardet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + ardet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + ardet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + ardet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + ardet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + tbcontrolamt.getText() + "'" + ","
                            + "'" + tbcontrolamt.getText() + "'" + ","
                            + null + "," + null + "," + null
                            + ")"
                            + ";");
                    }
                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Edited Shipper Record");
                    reinitreceivervariables("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
                */
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btaddallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddallActionPerformed
          DecimalFormat df = new DecimalFormat("#0.00");  
        for (int i = 0; i < referencedet.getRowCount(); i++) {
            actamt += Double.valueOf(referencedet.getModel().getValueAt(i,5).toString());
            
           armodel.addRow(new Object[] { referencedet.getModel().getValueAt(i, 0),
                                              referencedet.getModel().getValueAt(i, 5)
                                              });
        }
        
       if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
        tbactualamt.setText(df.format(actamt));
    }//GEN-LAST:event_btaddallActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void ddcurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcurrActionPerformed
        if (ddcust.getSelectedItem() != null &&  ddcurr.getSelectedItem() != null && ! isInit ) {
        curr = ddcurr.getSelectedItem().toString();
        getreferences(ddcust.getSelectedItem().toString());
        }
    }//GEN-LAST:event_ddcurrActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"arpaymentmaint","ar_nbr"});
    }//GEN-LAST:event_btbrowseActionPerformed

    private void tbcontrolamtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcontrolamtFocusGained
       if (tbcontrolamt.getText().equals("0")) {
            tbcontrolamt.setText("");
        }
    }//GEN-LAST:event_tbcontrolamtFocusGained

    private void tbcontrolamtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcontrolamtFocusLost
           String x = BlueSeerUtils.bsformat("", tbcontrolamt.getText(), "2");
        if (x.equals("error")) {
            tbcontrolamt.setText("");
            tbcontrolamt.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbcontrolamt.requestFocus();
        } else {
            tbcontrolamt.setText(x);
            tbcontrolamt.setBackground(Color.white);
        }
        
        if (! tbcontrolamt.getText().isEmpty()) {
            control = Double.valueOf(tbcontrolamt.getText());
        }
        
       if (control == actamt && control != 0.00 ) {
             tbcontrolamt.setBackground(Color.green);
             tbactualamt.setBackground(Color.green);
         } else {
            tbcontrolamt.setBackground(Color.white); 
            tbactualamt.setBackground(Color.white);
         }
       
    }//GEN-LAST:event_tbcontrolamtFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ardet;
    private javax.swing.JTextField batchnbr;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddall;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddcurr;
    private javax.swing.JComboBox ddcust;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lbcust;
    private javax.swing.JLabel lbmessage;
    private javax.swing.JTable referencedet;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcheck;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbrefamt;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
