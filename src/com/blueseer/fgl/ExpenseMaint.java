/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.fgl;

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


/**
 *
 * @author vaughnte
 */
public class ExpenseMaint extends javax.swing.JPanel {

                String terms = "";
                String apacct = "";
                String apcc = "";
                String apbank = "";
                Double actamt = 0.00;
                Double rcvamt = 0.00;
                int voucherline = 0;
    /**
     * Creates new form ShipMaintPanel
     */
    public ExpenseMaint() {
        initComponents();
      
        
       
       
    }
   
    
    public void initvars(String arg) {
        
         terms = "";
         apacct = "";
         apcc = "";
         apbank = "";
         actamt = 0.00;
         rcvamt = 0.00;
         expensenbr.setText("");
         tbinvoice.setText("");
        tbrmks.setText("");
        tbcontrolamt.setText("");
       
        tbactualamt.setText("");
       
        lbacct.setText("");
        lbvendor.setText("");
      
       
        expensemodel.setRowCount(0);
       
        expensedet.setModel(expensemodel);
        ddvend.removeAllItems();
        ArrayList myvend = OVData.getvendmstrlist();
        for (int i = 0; i < myvend.size(); i++) {
            ddvend.addItem(myvend.get(i));
        }
            ddvend.setSelectedIndex(0);
        
          ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        
          ddacct.removeAllItems();
        ArrayList<String> myaccts = OVData.getGLAcctListByType("E");
        for (String code : myaccts) {
            ddacct.addItem(code);
        }
        
           ddcc.removeAllItems();
        ArrayList<String> mycc = OVData.getGLCCList();
        for (String code : mycc) {
            ddcc.addItem(code);
        }
      
       
    }
    
    
  
    javax.swing.table.DefaultTableModel expensemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
            });
     
      
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


                res = st.executeQuery("select vd_ap_acct, vd_ap_cc, vd_terms, vd_bank from vd_mstr where vd_addr = " + "'" + vendor + "'" + ";");
                while (res.next()) {
                    i++;
                   apacct = res.getString("vd_ap_acct");
                   apcc = res.getString("vd_ap_cc");
                   terms = res.getString("vd_terms");
                   apbank = res.getString("vd_bank");
                }

            } catch (SQLException s) {
                bsmf.MainFrame.show("sql code does not execute");
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

        jPanel1 = new javax.swing.JPanel();
        expensenbr = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        tbcontrolamt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        btadditem = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        expensedet = new javax.swing.JTable();
        ddvend = new javax.swing.JComboBox();
        btdeleteitem = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        tbinvoice = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbactualamt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        tbrmks = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbitemservice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbprice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox<>();
        ddcc = new javax.swing.JComboBox<>();
        lbacct = new javax.swing.JLabel();
        lbvendor = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Expense Maintenance"));

        jLabel24.setText("Expense Nbr");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel36.setText("Vendor");

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

        expensedet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(expensedet);

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

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel27.setText("Control Amt");

        jLabel35.setText("ExpenseDate");

        jLabel2.setText("InvoiceNbr");

        jLabel28.setText("Actual Amt");

        jLabel4.setText("Rmks");

        jLabel5.setText("Item/Service");

        jLabel6.setText("Price");

        jLabel7.setText("Qty");

        jLabel8.setText("CC");

        jLabel9.setText("ExpenseAcct");

        ddsite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddsiteActionPerformed(evt);
            }
        });

        jLabel10.setText("Site");

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(644, 644, 644)
                .addComponent(btadditem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdeleteitem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addGap(4, 4, 4)
                                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(34, 34, 34)
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(39, 39, 39)
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel35)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnew)
                                    .addGap(73, 73, 73)
                                    .addComponent(jLabel27)
                                    .addGap(12, 12, 12)
                                    .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel28)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btadd)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnew)
                    .addComponent(expensenbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(tbcontrolamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(tbactualamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbvendor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ddvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tbinvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbitemservice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tbprice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadditem)
                    .addComponent(btdeleteitem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addGap(18, 18, 18))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        expensenbr.setText(String.valueOf(OVData.getNextNbr("voucher")));
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
               
                dcdate.setDate(now);
                ddvend.setEnabled(true);
               
               
        
    }//GEN-LAST:event_btnewActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        boolean canproceed = true;
        
       // Pattern p = Pattern.compile("\\d\\.\\d\\d");
      //  Matcher m = p.matcher(tbprice.getText());
       // receiverdet  "Part", "PO", "line", "Qty",  listprice, disc, netprice, loc, serial, lot, recvID, recvLine
       // voucherdet   "PO", "Line", "Part", "Qty", "Price", "RecvID", "RecvLine", "Acct", "CC"
      DecimalFormat df = new DecimalFormat("#0.00");   
            voucherline++;
            actamt += Double.valueOf(tbqty.getText()) * 
                          Double.valueOf(tbprice.getText());
            expensemodel.addRow(new Object[] { "", voucherline,
                                                  tbitemservice.getText(),
                                                  tbqty.getText(),
                                                  tbprice.getText(),
                                                  "expense",
                                                  "0",
                                                  ddacct.getSelectedItem().toString(),
                                                  ddcc.getSelectedItem().toString()
                                                  });
       
        tbitemservice.setText("");
        tbqty.setText("");
        tbprice.setText("");
        tbitemservice.requestFocus();
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
                setvendorvariables(ddvend.getSelectedItem().toString());
                    
                                        
                    Date duedate = OVData.getDueDateFromTerms(dcdate.getDate(), terms);
                    if (duedate == null) {
                    proceed = false;
                    bsmf.MainFrame.show("Terms is undefined for this Vendor");
                    }
                     
                    if (proceed) {
                                        
                      st.executeUpdate("insert into ap_mstr "
                        + "(ap_vend, ap_site, ap_nbr, ap_amt, ap_type, ap_ref, ap_rmks, "
                        + "ap_entdate, ap_effdate, ap_duedate, ap_acct, ap_cc, "
                        + "ap_terms, ap_status, ap_bank ) "
                        + " values ( " + "'" + ddvend.getSelectedItem() + "'" + ","
                              + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + expensenbr.getText() + "'" + ","
                        + "'" + df.format(actamt) + "'" + ","
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
                    for (int j = 0; j < expensedet.getRowCount(); j++) {
                        amt = Integer.valueOf(expensedet.getValueAt(j, 3).toString());
                        st.executeUpdate("insert into vod_mstr "
                            + "(vod_id, vod_vend, vod_rvdid, vod_rvdline, vod_part, vod_qty, "
                            + " vod_voprice, vod_date, vod_invoice, vod_expense_acct, vod_expense_cc )  "
                            + " values ( " + "'" + expensenbr.getText() + "'" + ","
                                + "'" + ddvend.getSelectedItem() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + expensedet.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                            + "'" + tbinvoice.getText().toString() + "'" + ","
                            + "'" + ddacct.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcc.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");
                  
                     }
                    
                    /* create gl_tran records */
                        if (! error)
                        error = OVData.glEntryFromVoucherExpense(expensenbr.getText(), dcdate.getDate());
                         
                        if (! error)
                        error = OVData.APExpense(duedate, OVData.getNextNbr("expensenumber"), expensenbr.getText(), tbinvoice.getText(), ddvend.getSelectedItem().toString(), actamt);
                        
                    if (error) {
                        bsmf.MainFrame.show("An error occurred");
                    } else {
                    bsmf.MainFrame.show("Expense Complete");
                    initvars("");
                    }
                    //reinitreceivervariables("");
                   
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void ddvendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddvendActionPerformed
       
        if (ddvend.getSelectedItem() != null )
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
            } catch (SQLException s) {
                bsmf.MainFrame.show("Cannot get vendor name for this Vendor");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_ddvendActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = expensedet.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
             actamt -= Double.valueOf(expensedet.getModel().getValueAt(i,3).toString()) * Double.valueOf(expensedet.getModel().getValueAt(i,4).toString());
            ((javax.swing.table.DefaultTableModel) expensedet.getModel()).removeRow(i);
           voucherline--;
        }
        tbactualamt.setText(actamt.toString());
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void ddsiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddsiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ddsiteActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
        if (ddacct.getSelectedItem() != null )
        try {
            
        
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select ac_desc from ac_mstr where ac_id = " + "'" + ddacct.getSelectedItem().toString() + "'" + ";");
                while (res.next()) {
                    lbacct.setText(res.getString("ac_desc"));
                }
            } catch (SQLException s) {
                s.printStackTrace();
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_ddacctActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btnew;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddacct;
    private javax.swing.JComboBox<String> ddcc;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddvend;
    private javax.swing.JTable expensedet;
    private javax.swing.JTextField expensenbr;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel lbacct;
    private javax.swing.JLabel lbvendor;
    private javax.swing.JTextField tbactualamt;
    private javax.swing.JTextField tbcontrolamt;
    private javax.swing.JTextField tbinvoice;
    private javax.swing.JTextField tbitemservice;
    private javax.swing.JTextField tbprice;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
