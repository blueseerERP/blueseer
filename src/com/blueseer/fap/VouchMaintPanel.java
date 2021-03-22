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
package com.blueseer.fap;

import com.blueseer.utl.OVData;
import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
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
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JTable;


/**
 *
 * @author vaughnte
 */
public class VouchMaintPanel extends javax.swing.JPanel {

                boolean isLoad = false;
                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                Double actamt = 0.00;
                Double baseamt = 0.00;
                Double rcvamt = 0.00;
                int voucherline = 0;
                String curr = "";
                String basecurr = OVData.getDefaultCurrency();
    /**
     * Creates new form ShipMaintPanel
     */
    public VouchMaintPanel() {
        initComponents();
      
        
       
       
    }
   
    
    
     public void disableAll() {
           tbprice.setEnabled(false);
          tbprice.setEnabled(false);
        tbcontrolamt.setEnabled(false);
        tbrecvamt.setEnabled(false);
        tbactualamt.setEnabled(false);
        tbinvoice.setEnabled(false);
        tbrmks.setEnabled(false);
         ddsite.setEnabled(false);
       tbitemservice.setEnabled(false);
         tbacct.setEnabled(false);
         tbcc.setEnabled(false);
         ddpo.setEnabled(false);
       
        ddvend.setEnabled(false);
        btadd.setEnabled(false);
        btedit.setEnabled(false);
        btnew.setEnabled(false);
        btlookup.setEnabled(false);
        btadditem.setEnabled(false);
        btdeleteitem.setEnabled(false);
       btadd.setEnabled(false);
        
        dcdate.setEnabled(false);
        tbqty.setEnabled(false);
        
       
         ddreceiver.setEnabled(false);
         ddtype.setEnabled(false);         
         btaddall.setEnabled(false);
         tbrecvamt.setEnabled(false);
         
         
        
    }
    
    public void enableAll() {
         
         tbprice.setEnabled(true);
        tbcontrolamt.setEnabled(true);
        tbrecvamt.setEnabled(true);
        tbactualamt.setEnabled(true);
        tbinvoice.setEnabled(true);
         tbrmks.setEnabled(true);
         tbitemservice.setEnabled(true);
         tbacct.setEnabled(true);
         tbcc.setEnabled(true);
        
        ddsite.setEnabled(true);
       
         ddpo.setEnabled(true);
       
        ddvend.setEnabled(true);
        btadd.setEnabled(true);
        btedit.setEnabled(true);
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
       
        btadditem.setEnabled(true);
        btdeleteitem.setEnabled(true);
        btadd.setEnabled(true);
        btedit.setEnabled(true);
       
        dcdate.setEnabled(true);
        tbqty.setEnabled(true);
         ddreceiver.setEnabled(true);
         ddtype.setEnabled(true);         
         btaddall.setEnabled(true);
         tbrecvamt.setEnabled(true);
       
    }
    
    public void clearAll() {
        
        
       isLoad = true; 
       java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
        
         terms = "";
         apacct = "";
         apcc = "";
         apbank = "";
         actamt = 0.00;
         rcvamt = 0.00;
         vouchernbr.setText("");
         tbinvoice.setText("");
        tbrmks.setText("");
        tbcontrolamt.setText("");
        tbrecvamt.setText("");
        tbactualamt.setText("");
        tbqty.setText("");
        lbvendor.setText("");
       
        tbprice.setDisabledTextColor(Color.black);
        tbprice.setText("");
        
        ddsite.setForeground(Color.black);
        ddpo.removeAllItems();
        ddreceiver.removeAllItems();
       
         receivermodel.setRowCount(0);
        vouchermodel.setRowCount(0);
        receiverdet.setModel(receivermodel);
        voucherdet.setModel(vouchermodel);
        ddvend.removeAllItems();
        ArrayList myvend = OVData.getvendmstrlist();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
            ddvend.insertItemAt("", 0);
            ddvend.setSelectedIndex(0);
        
          ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        
       isLoad = false;
    }
    
    
    public void initvars(String[] arg) {
        
         clearAll();
        
        disableAll();
        
        btnew.setEnabled(true);
        btlookup.setEnabled(true);
        
         if (arg != null && arg.length > 0) {
            getvoucherinfo(arg[0]);
        }
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "vod_id");
        } else {
         luModel = DTData.getVoucherBrowseUtil(luinput.getText(),0, "ap_vend");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle("No Records Found!");
        } else {
            ludialog.setTitle(luModel.getRowCount() + " Records Found!");
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog("Nbr", "VendCode"); 
        
        
    }

    
    javax.swing.table.DefaultTableModel receivermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
            });
    javax.swing.table.DefaultTableModel vouchermodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
            });
      public void reinitreceivervariables(String myreceiver) {
       
        vouchernbr.setText(myreceiver);
        if (myreceiver.compareTo("") == 0) {
            btadd.setEnabled(true);

        } else {
            btadd.setEnabled(false);
        }


        vouchernbr.setEnabled(true);
        vouchernbr.setText(myreceiver);
      
        tbcontrolamt.setText("");
        receiverdet.setModel(receivermodel);
        voucherdet.setModel(vouchermodel);
        receivermodel.setRowCount(0);
        vouchermodel.setRowCount(0);
       
       

     //   cobCustNameSM.removeAllItems();
        
    }
      
      public void setvendorvariables(String vendor) {
        
        try {
     
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;


                res = st.executeQuery("select vd_ap_acct, vd_ap_cc, vd_terms, vd_bank, vd_curr from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("vd_ap_acct");
                   apcc = res.getString("vd_ap_cc");
                   terms = res.getString("vd_terms");
                   apbank = res.getString("vd_bank");
                   curr = res.getString("vd_curr");
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("cannot retrieve vendor info");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
      
      public void getreceiverinfo(String myreceiver) {
        
        try {
            DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                rcvamt = 0.00;
                res = st.executeQuery("select * from recv_det inner join recv_mstr on rv_id = rvd_id where rvd_id = " + "'" + myreceiver + "'" + ";");
                while (res.next()) {
                    // "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
                  receivermodel.addRow(new Object[]{res.getString("rvd_part"), res.getString("rvd_po"), 
                      res.getString("rvd_poline"), (res.getInt("rvd_qty") - res.getInt("rvd_voqty")), res.getString("rvd_listprice"),
                   res.getString("rvd_disc"), res.getString("rvd_netprice"), res.getString("rvd_loc"),
                  res.getString("rvd_serial"), res.getString("rvd_lot"), res.getString("rvd_id"), res.getString("rvd_rline"),
                  res.getString("rv_ap_acct"), res.getString("rv_ap_cc")});
                  rcvamt += res.getDouble("rvd_netprice") * res.getDouble("rvd_qty");
               
                d++;
                }
                tbrecvamt.setText(df.format(rcvamt));
                receiverdet.setModel(receivermodel);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot retrieve Receiver");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }

       public void getvoucherinfo(String voucher) {
        
        try {
            DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            int i = 0;
            int d = 0;
            String uniqpo = null;
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                rcvamt = 0.00;
                res = st.executeQuery("select * from vod_mstr inner join ap_mstr on ap_nbr = vod_id inner join recv_det on rvd_id = vod_rvdid and rvd_poline = vod_rvdline " +
                        " where vod_id = " + "'" + voucher + "'" + ";");
                while (res.next()) {
                  vouchermodel.addRow(new Object[]{res.getString("rvd_po"), 
                      res.getString("vod_rvdline"), res.getString("vod_part"), res.getDouble("vod_qty"), res.getString("vod_voprice"),
                   res.getString("vod_rvdid"), res.getString("vod_rvdline"), res.getString("vod_expense_acct"),
                  res.getString("vod_expense_cc") }) ;
                d++;
                }
                if (d > 0) {
                   enableAll();
                   vouchernbr.setEnabled(false);
                    btnew.setEnabled(false);
                }
                vouchernbr.setText(voucher);
                tbrecvamt.setText(df.format(rcvamt));
                receiverdet.setModel(receivermodel);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot retrieve Voucher");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
      
      public void setType(String type) {
          if (type.equals("Receipt")) {
              ddpo.setEnabled(true);
              ddreceiver.setEnabled(true);
              tbitemservice.setEnabled(false);
              tbprice.setEnabled(false);
              btaddall.setEnabled(true);
              tbrecvamt.setEnabled(true);
          } else {
              btaddall.setEnabled(false);
              tbrecvamt.setEnabled(false);
              ddpo.setEnabled(false);
              ddreceiver.setEnabled(false);
              tbitemservice.setEnabled(true);
              tbprice.setEnabled(true);
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
        vouchernbr = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        lblreceiver = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        voucherdet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        ddreceiver = new javax.swing.JComboBox();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        receiverdet = new javax.swing.JTable();
        ddpo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tbinvoice = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        btaddall = new javax.swing.JButton();
        tbrecvamt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tbacct = new javax.swing.JTextField();
        tbcc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Voucher Maintenance"));

        jLabel24.setText("Voucher Nbr");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel36.setText("Vendor");

        lblreceiver.setText("Receivers");

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btadd.setText("Save");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        voucherdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(voucherdet);

        ddvend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddvendActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Del Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        ddreceiver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddreceiverActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Control Amt");

        jLabel35.setText("VoucherDate");

        receiverdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(receiverdet);

        ddpo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpoActionPerformed(evt);
            }
        });

        jLabel1.setText("PO");

        jLabel2.setText("Invoice");

        jLabel28.setText("Actual Amt");

        btaddall.setText("Add All");
        btaddall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddallActionPerformed(evt);
            }
        });

        jLabel3.setText("Receiver Total");

        jLabel4.setText("Rmks");

        jLabel5.setText("Item/Service");

        jLabel6.setText("Price");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Receipt", "Expense" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        jLabel37.setText("Type");

        jLabel7.setText("Qty");

        jLabel8.setText("CC");

        jLabel9.setText("ExpenseAcct");

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel35)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addGap(90, 90, 90)
                                                                        .addComponent(jLabel4))
                                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jLabel10)))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(0, 2, Short.MAX_VALUE))
                                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lbvendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel1)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel24)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(vouchernbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(13, 13, 13)
                                                        .addComponent(btnew)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel27)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(lblreceiver)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jLabel28)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(5, 5, 5))))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btaddall))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btadditem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteitem)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnew)
                        .addComponent(vouchernbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24)
                        .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28))
                    .addComponent(btlookup))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(ddreceiver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(lblreceiver)
                            .addComponent(jLabel36)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbvendor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel35)
                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddall)
                    .addComponent(tbrecvamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdeleteitem)
                    .addComponent(btadditem)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(17, 17, 17)
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
        vouchernbr.setText(String.valueOf(OVData.getNextNbr("voucher")));
               
        enableAll();
        btedit.setEnabled(false);
               
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "Line", "Qty", "listprice", "disc", "netprice", "loc", "serial", "lot", "RecvID", "RecvLine", "Acct", "CC"
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
        if (ddtype.getSelectedItem().toString().equals(("Receipt"))) {
        int[] rows = receiverdet.getSelectedRows();
            for (int i : rows) {
                actamt += Double.valueOf(receiverdet.getModel().getValueAt(i,3).toString()) * 
                          Double.valueOf(receiverdet.getModel().getValueAt(i,6).toString());

               vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                                  receiverdet.getModel().getValueAt(i, 2),
                                                  receiverdet.getModel().getValueAt(i, 0),
                                                  receiverdet.getModel().getValueAt(i, 3),
                                                  receiverdet.getModel().getValueAt(i, 6),
                                                  receiverdet.getModel().getValueAt(i, 10),
                                                  receiverdet.getModel().getValueAt(i, 11),
                                                  receiverdet.getModel().getValueAt(i, 12),
                                                  receiverdet.getModel().getValueAt(i, 13)
                                                  });
            }
        } else {
            voucherline++;
            actamt += Double.valueOf(tbqty.getText()) * 
                          Double.valueOf(tbprice.getText());
            vouchermodel.addRow(new Object[] { "", voucherline,
                                                  tbitemservice.getText(),
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  "expense",
                                                  "0",
                                                  tbacct.getText(),
                                                  tbcc.getText()
                                                  });
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
                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));   
                setvendorvariables(ddvend.getSelectedItem().toString());
                    
                                        
                    Date duedate = OVData.getDueDateFromTerms(dcdate.getDate(), terms);
                    if (duedate == null) {
                    proceed = false;
                    bsmf.MainFrame.show("Terms is undefined for this Vendor");
                    return;
                    }
                     
                    if (tbinvoice.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Invoice Nbr cannot be empty");
                    return;
                    }
                    
                     if (OVData.isCurrSameAsDefault(curr)) {
                     baseamt = actamt;
                     } else {
                     baseamt = OVData.getExchangeBaseValue(basecurr, curr, actamt);    
                     }
                    
                    if (proceed) {
                                        
                      st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_base_amt, ap_curr, ap_base_curr, ap_type, ap_ref, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_bank ) "
                        + " values ( " + "'" + ddvend.getSelectedItem() + "'" + ","
                              + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + vouchernbr.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
                        + "'" + df.format(baseamt) + "'" + ","
                        + "'" + curr + "'" + ","
                        + "'" + basecurr + "'" + ","
                        + "'" + "V" + "'" + ","
                        + "'" + tbinvoice.getText() + "'" + ","
                        + "'" + tbrmks.getText() + "'" + ","
                        + "'" + dfdate.format(now) + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + dfdate.format(duedate) + "'" + ","
                        + "'" + apacct + "'" + ","
                        + "'" + apcc + "'" + ","
                        + "'" + terms + "'" + ","
                        + "'" + "o" + "'"  + ","
                        + "'" + apbank + "'"
                        + ")"
                        + ";");
                int amt = 0;
               // voucherdet =  "PO", "Line", "Part", "Qty", "voprice", "recvID", "recvLine", "Acct", "CC"
                    for (int j = 0; j < voucherdet.getRowCount(); j++) {
                        amt = Integer.valueOf(voucherdet.getValueAt(j, 3).toString());
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_part, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + vouchernbr.getText() + "'" + ","
                                + "'" + ddvend.getSelectedItem() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + tbinvoice.getText().toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 8).toString() + "'" 
                            + ")"
                            + ";");
                      
                double voqty = 0.00;
                double rvqty = 0.00;
                double rvdvoqty = 0.00;
                String status = "0";
                
                res = st.executeQuery("select rvd_voqty, rvd_qty from recv_det " 
                         + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                        + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                        );
                while (res.next()) {
                    voqty = res.getDouble("rvd_voqty");
                    rvqty = res.getDouble("rvd_qty");
                    if ((voqty + amt) >= rvqty) {
                        status = "1";
                    }     
                }
                res.close();        
                        
                   rvdvoqty = voqty + amt;
                   
                        if (ddtype.getSelectedItem().toString().equals("Receipt")) {
                           if (bsmf.MainFrame.dbtype.equals("sqlite")) { 
                            st.executeUpdate("update recv_det  "
                            + " set rvd_voqty =  " + "'" + rvdvoqty + "'" + ","
                            + " rvd_status = " + "'" + status + "'"
                            + " where rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            );
                           } else {
                            st.executeUpdate("update recv_det as r1 inner join recv_det as r2 "
                            + " set r1.rvd_voqty = r2.rvd_voqty + " +  "'" + amt + "'" + ","
                            + " r1.rvd_status = case when r1.rvd_qty <= ( r2.rvd_voqty + " + "'" + amt + "'" +  ") then '1' else '0' end " 
                            + " where r1.rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND r1.rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            + " AND r2.rvd_id = " + "'" + voucherdet.getValueAt(j, 5).toString() + "'"
                            + " AND r2.rvd_rline = " + "'" + voucherdet.getValueAt(j, 6).toString() + "'"
                            );   
                           }
                        }
                     }
                    
                    /* create gl_tran records */
                        if (! error)
                             if (ddtype.getSelectedItem().toString().equals("Receipt")) {
                                 error = OVData.glEntryFromVoucher(vouchernbr.getText(), dcdate.getDate());
                             } else {
                                 error = OVData.glEntryFromVoucherExpense(vouchernbr.getText(), dcdate.getDate());
                             }
                        
                    if (error) {
                        bsmf.MainFrame.show("An error occurred");
                    } else {
                    bsmf.MainFrame.show("Vouchering Complete");
                    initvars(null);
                    }
                    //reinitreceivervariables("");
                   
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Voucher");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
       
      if (! isLoad) { 
           ddreceiver.removeAllItems();
           ddpo.removeAllItems();
           receivermodel.setRowCount(0);
        
        if (ddvend.getSelectedItem() != null && ! ddvend.getSelectedItem().toString().isEmpty() )
        try {
            
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select vd_name from vd_mstr where vd_addr = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbvendor.setText(res.getString("vd_name"));
                }
                res = st.executeQuery("select po_nbr from po_mstr where po_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    ddpo.addItem(res.getString("po_nbr"));
                }
                ddpo.insertItemAt("", 0);
                ddpo.setSelectedIndex(0);
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot get PO list for this Vendor");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }// not isLoad
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = voucherdet.getSelectedRows();
        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
             actamt -= Double.valueOf(voucherdet.getModel().getValueAt(i,3).toString()) * Double.valueOf(voucherdet.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) voucherdet.getModel()).removeRow(i);
           voucherline--;
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

                if (proceed) {
                    st.executeUpdate("update recv_mstr set recv_po = " + "'" + ddreceiver.getSelectedItem().toString() + "'" + ","
                        + "recv_create_date = " + "'" + tbcontrolamt.getText() + "'" + ","
                        + "recv_recv_date = " + "'" + dfdate.format(dcdate.getDate()) + "'"
                        + " where recv_id = " + "'" + vouchernbr.getText().toString() + "'"
                        + ";");
                    // delete the sod_det records and add back.
                    st.executeUpdate("delete from recv_det where rvdet_id = " + "'" + vouchernbr.getText() + "'"  );
                    for (int j = 0; j < voucherdet.getRowCount(); j++) {
                        st.executeUpdate("insert into recv_det "
                            + "(rvdet_id, rvdet_part, rvdet_so, rvdet_recv_qty,"
                            + "rvdet_recv_price, rvdet_po, rvdet_create_date,"
                            + "rvdet_rvdet_date, rvdet_char1, rvdet_char2, rvdet_char3) "
                            + " values ( " + "'" + vouchernbr.getText() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + voucherdet.getValueAt(j, 2).toString() + "'" + ","
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
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot insert into recv_det");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void ddreceiverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddreceiverActionPerformed
       if (ddreceiver.getSelectedItem() == null)
           return;
       
        receivermodel.setRowCount(0);
       if ( ddreceiver.getItemCount() != 0 && ! ddreceiver.getSelectedItem().toString().isEmpty())
       getreceiverinfo(ddreceiver.getSelectedItem().toString());
    }//GEN-LAST:event_ddreceiverActionPerformed

    private void ddpoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpoActionPerformed
       if (ddpo.getSelectedItem() == null)
           return;
        ddreceiver.removeAllItems();
        receivermodel.setRowCount(0);
   //    if (! ddvend.getSelectedItem().toString().isEmpty() && ! ddpo.getSelectedItem().toString().isEmpty() && ! ddpo.equals(null) && ! ddvend.equals(null))
        if ( ddpo.getSelectedItem() != null)
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select distinct(rvd_id) from recv_det inner join recv_mstr on " +
                        " rvd_id = rv_id where rv_vend = " + "'" + ddvend.getSelectedItem().toString() + "'" + 
                        " and rvd_po = " + "'" + ddpo.getSelectedItem().toString() + "'" + " and rvd_status = '0' " + ";");
                while (res.next()) {
                    ddreceiver.addItem(res.getString("rvd_id"));
                }
                ddreceiver.insertItemAt("", 0);
                ddreceiver.setSelectedIndex(0);
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot get Receiver list for this Vendor");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_ddpoActionPerformed

    private void btaddallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddallActionPerformed
          DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        for (int i = 0; i < receiverdet.getRowCount(); i++) {
            actamt += Double.valueOf(receiverdet.getModel().getValueAt(i,3).toString()) * Double.valueOf(receiverdet.getModel().getValueAt(i,6).toString());
            
           vouchermodel.addRow(new Object[] { receiverdet.getModel().getValueAt(i, 1),
                                              receiverdet.getModel().getValueAt(i, 2),
                                              receiverdet.getModel().getValueAt(i, 0),
                                              receiverdet.getModel().getValueAt(i, 3),
                                              receiverdet.getModel().getValueAt(i, 6),
                                              receiverdet.getModel().getValueAt(i, 10),
                                              receiverdet.getModel().getValueAt(i, 11),
                                              receiverdet.getModel().getValueAt(i, 12),
                                              receiverdet.getModel().getValueAt(i, 13)
                                              });
        }
        tbactualamt.setText(df.format(actamt));
    }//GEN-LAST:event_btaddallActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
       setType(ddtype.getSelectedItem().toString());
    }//GEN-LAST:event_ddtypeActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddall;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddpo;
    private javax.swing.JComboBox ddreceiver;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblreceiver;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTable receiverdet;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcc;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbinvoice;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrecvamt;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTable voucherdet;
    private javax.swing.JTextField vouchernbr;
    // End of variables declaration//GEN-END:variables
}
