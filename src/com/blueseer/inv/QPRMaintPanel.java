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
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class QPRMaintPanel extends javax.swing.JPanel {

String sitename = "";
String siteaddr = "";
String sitephone = "";
String sitecitystatezip = "";
String sqename = "";
String sqephone = "";
String sqefax = "";
String sqeemail = "";
    
    
    /**
     * Creates new form QPRMaintPanel
     */
    public QPRMaintPanel() {
        
        initComponents();
        
        this.initvars("");
        
    }

    
      public void getQPR(String arg) {
          
          try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {

                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select * from qual_mstr where qual_id = " + "'" + arg + "'");

                int i = 0;
                while (res.next()) {
                    i++;
                    // int nextnumber = Integer.valueOf(res.getString("num"));
                    // nextnumber++;

                    tbDateCreate.setText(res.getString("qual_date_crt"));
                    tbDateUpdate.setText(res.getString("qual_date_upd"));
                    tbDateClosed.setText(res.getString("qual_date_cls"));
                    tbQtyRejected.setText(res.getString("qual_qty_rej"));
                    tbNumSuspectCont.setText(res.getString("qual_qty_susp"));
                    tbTotalQty.setText(res.getString("qual_qty_tot_def"));
                    tbChargeBack.setText(res.getString("qual_tot_charge"));
                    tbOriginator.setText(res.getString("qual_originator"));
                    tbSuppName.setText(res.getString("qual_vend_name"));
                    tbSuppNbr.setText(res.getString("qual_vend"));
                    cbQPR.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_qpr")));
                    cbInforOnly.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_infor")));
                    cbSendSupp.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_sendsupp")));
                    cbSort.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_sort")));
                    cbRework.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_rework")));
                    cbScrapped.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_scrap")));
                    cbDeviation.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_dev")));
                    cbLine.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_line")));
                    cbReceiving.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_recv")));
                    cbCustomer.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_cust")));
                    cbEngineering.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_eng")));
                    cbOther.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_src_oth")));
                    tbOtherReason.setText(res.getString("qual_src_oth_desc"));
                    cbInternal.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_int_sup")));
                    cbExternal.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("qual_ext_sup")));
                    tbDept.setText(res.getString("qual_line_dept"));
                    tbDeviationNbr.setText(res.getString("qual_dev_nbr"));
                    tbPartNumber.setText(res.getString("qual_part"));
                    tbPartDesc.setText(res.getString("qual_part_desc"));
                    taIssue.setText(res.getString("qual_desc_iss"));
                    taHistory.setText(res.getString("qual_desc_fin_hist"));
                    taComments.setText(res.getString("qual_desc_sqe_comt"));
                    tbComplaintNbr.setText(arg);
                    // jcbsupervisor_empmast.setSelectedIndex(0);
                }

                if (i == 0) {
                    bsmf.MainFrame.show("No Record Found");
                    initvars("");
                } else {
                    tbComplaintNbr.setEnabled(false);
                    btQualProbAdd.setEnabled(false);
                    tbDateUpdate.setEnabled(false);
                    tbDateCreate.setEnabled(false);
                    tbOriginator.setEnabled(false);

                    jButtonNewComplaint.setEnabled(false);
                    if (!tbDateClosed.getText().isEmpty()) {
                        btQualProbUpdate.setEnabled(false);
                        tbDateClosed.setEnabled(false);
                    }
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          
          
      }
    
      public void initvars(String arg) {
        tbQtyRejected.setText("0");
        tbNumSuspectCont.setText("0");
        tbTotalQty.setText("0");
        tbChargeBack.setText("0.00");
        btget.setEnabled(true);
        jButtonNewComplaint.setEnabled(true);
        btQualProbAdd.setEnabled(true);
        btQualProbUpdate.setEnabled(true);
        tbComplaintNbr.setText("");
        tbComplaintNbr.setEnabled(true);
        tbOriginator.setText("");
        tbDateUpdate.setText("");
        tbDateUpdate.setEnabled(true);
        tbDateCreate.setText("");
        tbDateCreate.setEnabled(true);
        tbDateClosed.setText("");
        tbSuppName.setText("");
        tbSuppNbr.setText("");
        cbQPR.setSelected(false);
        cbInforOnly.setSelected(false);
        cbSendSupp.setSelected(false);
        cbSort.setSelected(false);
        cbRework.setSelected(false);
        cbScrapped.setSelected(false);
        cbDeviation.setSelected(false);
        cbLine.setSelected(false);
        cbReceiving.setSelected(false);
        cbCustomer.setSelected(false);
        cbEngineering.setSelected(false);
        cbOther.setSelected(false);
        tbOtherReason.setText("");
        cbInternal.setSelected(false);
        cbExternal.setSelected(false);
        tbDept.setText("");
        tbDeviationNbr.setText("");
        tbPartNumber.setText("");
        tbPartDesc.setText("");
        taIssue.setText("");
        taHistory.setText("");
        taComments.setText("");
        this.revalidate();
        
        
        if (! arg.isEmpty()) {
            getQPR(arg);
        }
    }
    
    
    
    /**
     * This method is called from within the bsmf.MainFrame.constructor to initialize the form.
     * WARNING: Do NOT modify this code. The bsmf.MainFrame.content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btget = new javax.swing.JButton();
        tbComplaintNbr = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonNewComplaint = new javax.swing.JButton();
        tbDateCreate = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbOriginator = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbQPR = new javax.swing.JCheckBox();
        cbInforOnly = new javax.swing.JCheckBox();
        cbSendSupp = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        cbSort = new javax.swing.JCheckBox();
        cbRework = new javax.swing.JCheckBox();
        cbScrapped = new javax.swing.JCheckBox();
        cbDeviation = new javax.swing.JCheckBox();
        tbDeviationNbr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbLine = new javax.swing.JCheckBox();
        cbReceiving = new javax.swing.JCheckBox();
        cbCustomer = new javax.swing.JCheckBox();
        cbEngineering = new javax.swing.JCheckBox();
        cbOther = new javax.swing.JCheckBox();
        tbOtherReason = new javax.swing.JTextField();
        tbDept = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbInternal = new javax.swing.JCheckBox();
        cbExternal = new javax.swing.JCheckBox();
        tbPartNumber = new javax.swing.JTextField();
        tbPartDesc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tbQtyRejected = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tbNumSuspectCont = new javax.swing.JTextField();
        tbTotalQty = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        tbDateUpdate = new javax.swing.JTextField();
        tbDateClosed = new javax.swing.JTextField();
        tbSuppName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taIssue = new javax.swing.JTextArea();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taHistory = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taComments = new javax.swing.JTextArea();
        tbChargeBack = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btQualProbAdd = new javax.swing.JButton();
        btQualProbUpdate = new javax.swing.JButton();
        btPrintQPR = new javax.swing.JButton();
        tbSuppNbr = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Quality Problem Report (QPR)"));

        btget.setText("Get");
        btget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgetActionPerformed(evt);
            }
        });

        jLabel1.setText("Complaint#");

        jButtonNewComplaint.setText("New");
        jButtonNewComplaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewComplaintActionPerformed(evt);
            }
        });

        jLabel2.setText("DateCreated");

        jLabel3.setText("CreatedBy");

        cbQPR.setText("QPR 8d req");

        cbInforOnly.setText("Infor Only (no 8d req)");

        cbSendSupp.setText("Send Back To Supplier");

        jLabel4.setText("Disposition of Nonconformance");

        cbSort.setText("Sort");

        cbRework.setText("Rework");

        cbScrapped.setText("Scrapped");

        cbDeviation.setText("Deviation#");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Source Of Reject");

        cbLine.setText("Line");

        cbReceiving.setText("Receiving Inspection");

        cbCustomer.setText("Customer");

        cbEngineering.setText("Engineering");

        cbOther.setText("Other");

        jLabel6.setText("Dept#");

        cbInternal.setText("Internal Supplier");

        cbExternal.setText("External Supplier");

        jLabel7.setText("PartNumber");

        jLabel8.setText("Part Desc");

        jLabel9.setText("Qty Rejected");

        jLabel10.setText("Number of Suspect Containers");

        jLabel11.setText("Total Qty Found Defective");

        jLabel12.setText("DateUpdated");

        jLabel13.setText("SupplierName");

        jLabel14.setText("DateClosed");

        jLabel15.setText("SupplierContact");

        taIssue.setColumns(20);
        taIssue.setRows(5);
        jScrollPane1.setViewportView(taIssue);

        jLabel16.setText("Description of Issue:");

        jLabel17.setText("Description of History (chargeback/debit memo):");

        taHistory.setColumns(20);
        taHistory.setRows(5);
        jScrollPane2.setViewportView(taHistory);

        jLabel18.setText("SQE Comments:");

        taComments.setColumns(20);
        taComments.setRows(5);
        jScrollPane3.setViewportView(taComments);

        jLabel19.setText("Total ChargeBack for QPR");

        btQualProbAdd.setText("Add");
        btQualProbAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btQualProbAddActionPerformed(evt);
            }
        });

        btQualProbUpdate.setText("Update");
        btQualProbUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btQualProbUpdateActionPerformed(evt);
            }
        });

        btPrintQPR.setText("Print");
        btPrintQPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintQPRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbQPR)
                                            .addComponent(cbSendSupp)
                                            .addComponent(jLabel4)
                                            .addComponent(cbSort)
                                            .addComponent(cbRework)
                                            .addComponent(cbInforOnly))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel5)
                                            .addComponent(cbCustomer)
                                            .addComponent(cbEngineering)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(cbOther)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(tbOtherReason))
                                            .addComponent(cbReceiving)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(cbLine)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbDept, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel9)
                                                    .addComponent(jLabel10)
                                                    .addComponent(jLabel11)
                                                    .addComponent(jLabel19))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(tbNumSuspectCont, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(tbQtyRejected, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(tbTotalQty, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(tbChargeBack, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbPartDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbPartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel15)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbSuppName, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addComponent(cbInternal)
                                                                .addGap(172, 172, 172))
                                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(jLabel1)
                                                                        .addGap(65, 65, 65))
                                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                                        .addComponent(tbComplaintNbr, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                                                .addComponent(btget)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jButtonNewComplaint)
                                                                .addGap(49, 49, 49)))
                                                        .addGap(0, 0, Short.MAX_VALUE)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel2)
                                                            .addComponent(jLabel12))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(tbDateUpdate)
                                                            .addComponent(tbDateCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addGap(221, 221, 221)
                                                        .addComponent(jLabel13)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(tbSuppNbr, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(90, 90, 90))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(cbDeviation)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(tbDeviationNbr, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(cbScrapped)
                                                    .addComponent(cbExternal))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbDateClosed, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tbOriginator, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btPrintQPR)
                        .addGap(18, 18, 18)
                        .addComponent(btQualProbUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btQualProbAdd))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btget)
                                .addComponent(jButtonNewComplaint))
                            .addComponent(jLabel1)
                            .addComponent(tbComplaintNbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbInternal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbExternal)
                        .addGap(1, 1, 1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbOriginator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(tbDateCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbDateClosed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(tbDateUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbSuppName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel13)
                            .addComponent(tbSuppNbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(1, 1, 1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbLine)
                                        .addComponent(jLabel6)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbReceiving)
                                .addGap(3, 3, 3)
                                .addComponent(cbCustomer)
                                .addGap(6, 6, 6)
                                .addComponent(cbEngineering)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbOther)
                                    .addComponent(tbOtherReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(cbQPR)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbInforOnly)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbSendSupp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbSort)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbRework)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbScrapped)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cbDeviation)
                                    .addComponent(tbDeviationNbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbPartNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbPartDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbQtyRejected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbNumSuspectCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbTotalQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbChargeBack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btQualProbAdd)
                    .addComponent(btQualProbUpdate)
                    .addComponent(btPrintQPR))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgetActionPerformed
         getQPR(tbComplaintNbr.getText());
    }//GEN-LAST:event_btgetActionPerformed

    private void jButtonNewComplaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewComplaintActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select max(counter_id) as 'num' from counter where counter_name = 'quality';");
                while (res.next()) {

                    int nextnumber = Integer.valueOf(res.getString("num"));
                    nextnumber++;
                    OVData.updatecounter("quality", nextnumber);
                    tbComplaintNbr.setText(String.format("%d", nextnumber).toString());
                    tbComplaintNbr.setEnabled(false);
                    // jcbsupervisor_empmast.setSelectedIndex(0);
                }
                jButtonNewComplaint.setEnabled(false);
                btget.setEnabled(false);
                btQualProbUpdate.setEnabled(false);
                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                tbDateCreate.setText(clockdate);
                tbDateCreate.setEnabled(false);
                tbDateUpdate.setText(clockdate);
                tbDateUpdate.setEnabled(false);
                tbOriginator.setText(bsmf.MainFrame.userid.toString());
                tbOriginator.setEnabled(false);

                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_jButtonNewComplaintActionPerformed

    private void btQualProbAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btQualProbAddActionPerformed

        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (!BlueSeerUtils.isParsableToInt(tbQtyRejected.getText())) {
                    bsmf.MainFrame.show("Not Parseable");
                    proceed = false;
                }
                if (tbQtyRejected.getText().isEmpty()) {
                    tbQtyRejected.setText("0");
                }

                String createdate = tbDateCreate.getText();
                String closedate = tbDateClosed.getText();
                Pattern p = Pattern.compile("[2]\\d\\d\\d\\d\\d\\d\\d");
                Matcher m;
                if (createdate != null) {
                    m = p.matcher(createdate);
                    if (!m.find()) {
                        bsmf.MainFrame.show("Invalid Create date");
                        proceed = false;
                    }
                }

                p = Pattern.compile("[2]\\d\\d\\d\\d\\d\\d\\d");
                if (closedate != null && (!closedate.isEmpty())) {
                    m = p.matcher(closedate);
                    if (!m.find()) {
                        bsmf.MainFrame.show("Invalid Close date");
                        proceed = false;
                    }
                }

                if (taComments.getText().length() > 400) {
                        bsmf.MainFrame.show("SQE Comments cannot be greater than 400 chars");
                        proceed = false;
                }
                
                 if (taIssue.getText().length() > 400) {
                        bsmf.MainFrame.show("Issues cannot be greater than 400 chars");
                        proceed = false;
                }
                
                
                if (proceed) {
                    st.executeUpdate("insert into qual_mstr "
                        + "(qual_id, qual_userid, qual_date_crt,"
                        + "qual_date_upd, qual_date_cls, qual_originator,"
                        + "qual_vend, qual_vend_name, qual_vend_contact, "
                        + "qual_qpr, qual_infor, qual_sendsupp, qual_sort,"
                        + "qual_rework, qual_scrap, qual_dev, qual_dev_nbr,"
                        + "qual_src_line, qual_line_dept, qual_src_recv,"
                        + "qual_src_cust, qual_src_eng, qual_src_oth,"
                        + "qual_src_oth_desc, qual_int_sup, qual_ext_sup,"
                        + "qual_part, qual_part_desc, qual_qty_rej, qual_qty_susp,"
                        + "qual_qty_tot_def, qual_desc_iss, qual_desc_fin_hist,"
                        + "qual_desc_sqe_comt, qual_tot_charge, qual_dec1, qual_date1, qual_int1) "
                        + " values ( " + "'" + tbComplaintNbr.getText().toString() + "'" + ","
                        + "'" + tbOriginator.getText() + "'" + ","
                        + "'" + tbDateCreate.getText() + "'" + ","
                        + "'" + tbDateUpdate.getText() + "'" + ","
                        + "'" + tbDateClosed.getText() + "'" + ","
                        + "'" + tbOriginator.getText() + "'" + ","
                        + "'" + tbSuppNbr.getText().replace("'", "\\'") + "'" + ","
                        + "'" + tbSuppName.getText().replace("'", "\\'") + "'" + ","
                        + null + "," + // qual_vendor_bsmf.MainFrame.contact
                        "'" + BlueSeerUtils.boolToInt(cbQPR.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbInforOnly.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbSendSupp.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbSort.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbRework.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbScrapped.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbDeviation.isSelected()) + "'" + ","
                        + "'" + tbDeviationNbr.getText().replace("'", "\\'") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbLine.isSelected()) + "'" + ","
                        + "'" + tbDept.getText().replace("'", "\\'") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbReceiving.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbCustomer.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbEngineering.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbOther.isSelected()) + "'" + ","
                        + "'" + tbOtherReason.getText().replace("'", "\\'") + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbInternal.isSelected()) + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(cbExternal.isSelected()) + "'" + ","
                        + "'" + tbPartNumber.getText().replace("'", "\\'") + "'" + ","
                        + "'" + tbPartDesc.getText().replace("'", "\\'") + "'" + ","
                        + "'" + Integer.parseInt(tbQtyRejected.getText().toString()) + "'" + ","
                        + "'" + Integer.parseInt(tbNumSuspectCont.getText().toString()) + "'" + ","
                        + "'" + Integer.parseInt(tbTotalQty.getText().toString()) + "'" + ","
                        + "'" + taIssue.getText().replace("'", "\\'") + "'" + ","
                        + "'" + taHistory.getText().replace("'", "\\'") + "'" + ","
                        + "'" + taComments.getText().replace("'", "\\'") + "'" + ","
                        + "'" + Float.parseFloat(tbChargeBack.getText().replace("'", "\\'").toString()) + "'" + ","
                        + null + "," + null + "," + null
                        + ")"
                        + ";");
                    bsmf.MainFrame.show("Added Quality Record");
                    initvars("");
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_btQualProbAddActionPerformed

    private void btQualProbUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btQualProbUpdateActionPerformed
        btQualProbAdd.setEnabled(false);

        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;

                if (!BlueSeerUtils.isParsableToInt(tbQtyRejected.getText())) {
                    bsmf.MainFrame.show("Not Parseable");
                    proceed = false;
                }
                if (tbQtyRejected.getText().isEmpty()) {
                    tbQtyRejected.setText("0");
                }

                String createdate = tbDateCreate.getText();
                String closedate = tbDateClosed.getText();
                Pattern p = Pattern.compile("[2]\\d\\d\\d\\d\\d\\d\\d");
                Matcher m;
                if (createdate != null) {
                    m = p.matcher(createdate);
                    if (!m.find()) {
                        bsmf.MainFrame.show("Invalid Create date");
                        proceed = false;
                    }
                }

                p = Pattern.compile("[2]\\d\\d\\d\\d\\d\\d\\d");
                if (closedate != null && (!closedate.isEmpty())) {
                    m = p.matcher(closedate);
                    if (!m.find()) {
                        bsmf.MainFrame.show("Invalid Close date");
                        proceed = false;
                    }
                }
                
                
                if (taComments.getText().length() > 400) {
                        bsmf.MainFrame.show("SQE Comments cannot be greater than 400 chars");
                        proceed = false;
                }
                
                 if (taIssue.getText().length() > 400) {
                        bsmf.MainFrame.show("Issues cannot be greater than 400 chars");
                        proceed = false;
                }

                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);

                if (proceed) {
                    st.executeUpdate("update qual_mstr "
                        + "set qual_qpr = " + "'" + BlueSeerUtils.boolToInt(cbQPR.isSelected()) + "'" + ","
                        + "qual_infor = " + "'" + BlueSeerUtils.boolToInt(cbInforOnly.isSelected()) + "'" + ","
                        + "qual_date_upd = " + "'" + clockdate.toString() + "'" + ","
                        + "qual_date_cls = " + "'" + closedate.toString() + "'" + ","
                        + "qual_sendsupp = " + "'" + BlueSeerUtils.boolToInt(cbSendSupp.isSelected()) + "'" + ","
                        + "qual_sort = " + "'" + BlueSeerUtils.boolToInt(cbSort.isSelected()) + "'" + ","
                        + "qual_rework = " + "'" + BlueSeerUtils.boolToInt(cbRework.isSelected()) + "'" + ","
                        + "qual_scrap = " + "'" + BlueSeerUtils.boolToInt(cbScrapped.isSelected()) + "'" + ","
                        + "qual_dev = " + "'" + BlueSeerUtils.boolToInt(cbDeviation.isSelected()) + "'" + ","
                        + "qual_src_line = " + "'" + BlueSeerUtils.boolToInt(cbLine.isSelected()) + "'" + ","
                        + "qual_src_recv = " + "'" + BlueSeerUtils.boolToInt(cbReceiving.isSelected()) + "'" + ","
                        + "qual_src_cust = " + "'" + BlueSeerUtils.boolToInt(cbCustomer.isSelected()) + "'" + ","
                        + "qual_src_eng = " + "'" + BlueSeerUtils.boolToInt(cbEngineering.isSelected()) + "'" + ","
                        + "qual_src_oth = " + "'" + BlueSeerUtils.boolToInt(cbOther.isSelected()) + "'" + ","
                        + "qual_src_oth_desc = " + "'" + tbOtherReason.getText() + "'" + ","
                        + "qual_int_sup = " + "'" + BlueSeerUtils.boolToInt(cbInternal.isSelected()) + "'" + ","
                        + "qual_ext_sup = " + "'" + BlueSeerUtils.boolToInt(cbExternal.isSelected()) + "'" + ","
                        + "qual_vend_name = " + "'" + tbSuppName.getText().replace("'", "\\'") + "'" + ","
                        + "qual_vend = " + "'" + tbSuppNbr.getText().replace("'", "\\'") + "'" + ","
                        + "qual_line_dept = " + "'" + tbDept.getText().replace("'", "\\'") + "'" + ","
                        + "qual_dev_nbr = " + "'" + tbDeviationNbr.getText().replace("'", "\\'") + "'" + ","
                        + "qual_part = " + "'" + tbPartNumber.getText().replace("'", "\\'") + "'" + ","
                        + "qual_part_desc = " + "'" + tbPartDesc.getText().replace("'", "\\'") + "'" + ","
                        + "qual_qty_rej = " + "'" + Integer.parseInt(tbQtyRejected.getText().toString()) + "'" + ","
                        + "qual_qty_susp = " + "'" + Integer.parseInt(tbNumSuspectCont.getText().toString()) + "'" + ","
                        + "qual_qty_tot_def = " + "'" + Integer.parseInt(tbTotalQty.getText().toString()) + "'" + ","
                        + "qual_tot_charge = " + "'" + Float.parseFloat(tbChargeBack.getText().toString()) + "'" + ","
                        + "qual_desc_iss = " + "'" + taIssue.getText().replace("'", "\\'") + "'" + ","
                        + "qual_desc_fin_hist = " + "'" + taHistory.getText().replace("'", "\\'") + "'" + ","
                        + "qual_desc_sqe_comt = " + "'" + taComments.getText().replace("'", "\\'") + "'"
                        + " where qual_id = " + "'" + tbComplaintNbr.getText().replace("'", "\\'").toString() + "'"
                        + " ;");
                    bsmf.MainFrame.show("Updated Quality Record");

                    initvars("");
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btQualProbUpdateActionPerformed

    
     public void getSiteAddress(String site) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                                
                res = st.executeQuery("select * from site_mstr where site_site = " + "'" + site + "'" +";");
                while (res.next()) {
                    i++;
                   sitename = res.getString("site_desc");
                   siteaddr = res.getString("site_line1");
                   sitephone = res.getString("site_phone");
                   sitecitystatezip = res.getString("site_city") + ", " + res.getString("site_state") + " " + res.getString("site_zip");
                   sqename = res.getString("site_sqename");
                   sqephone = res.getString("site_sqephone");
                   sqefax = res.getString("site_sqefax");
                   sqeemail = res.getString("site_sqeemail");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Address Record found for site " + site );

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve site_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    
    
    private void btPrintQPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintQPRActionPerformed
       
        
        
        getSiteAddress(OVData.getDefaultSite());
        
        
        try {

            final PrinterJob pjob = PrinterJob.getPrinterJob();
            pjob.setJobName("Graphics Demo Printout");
            pjob.setCopies(1);
            pjob.setPrintable(new Printable() {
                private boolean rootPaneCheckingEnabled;
                public int print(Graphics pg, PageFormat pf, int pageNum) {
                    if (pageNum > 0) // we only print one page
                    {
                        return Printable.NO_SUCH_PAGE; // ie., end of job
                    }
                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 12));

                    
                    
                    pg.drawString(sitename, 50, 50);
                    pg.drawString(siteaddr, 50, 60);
                    pg.drawString(sitecitystatezip, 50, 70);

                    pg.drawString("Complaint #: ", 500, 50);
                    pg.drawString(tbComplaintNbr.getText(), 505, 70);
                    pg.draw3DRect(500, 55, 60, 20, rootPaneCheckingEnabled);

                    pg.setFont(new Font("TimesRoman", Font.BOLD, 18));
                    pg.drawString("Quality Problem Report", 200, 90);

                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                    pg.drawString("Date", 50, 110);
                    pg.drawString(tbDateCreate.getText(), 55, 138);
                    pg.drawString("Originator", 140, 110);
                    pg.drawString(tbOriginator.getText(), 145, 138);
                    pg.drawString("Supplier", 260, 110);
                    pg.drawString(tbSuppNbr.getText(), 265, 138);
                    pg.drawString("Supplier Contact", 400, 110);
                    pg.drawString(tbSuppName.getText(), 405, 138);
                    pg.draw3DRect(50, 120, 60, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(140, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(260, 120, 110, 20, rootPaneCheckingEnabled);
                    pg.draw3DRect(400, 120, 110, 20, rootPaneCheckingEnabled);

                    pg.setFont(new Font("TimesRoman", Font.PLAIN, 8));
                    pg.draw3DRect(50, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbQPR.isSelected()), 52, 168);
                    pg.drawString("QPR (8-D Required)", 80, 170);
                    pg.draw3DRect(50, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbInforOnly.isSelected()), 52, 188);
                    pg.drawString("Infor Only (No 8-D Required)", 80, 190);
                    pg.drawString("Disposition of Nonbsmf.MainFrame.conformance", 50, 230);
                    pg.draw3DRect(50, 240, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbSendSupp.isSelected()), 52, 248);
                    pg.drawString("Send Back to Supplier", 80, 250);
                    pg.draw3DRect(50, 260, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbSort.isSelected()), 52, 268);
                    pg.drawString("Sort", 80, 270);
                    pg.draw3DRect(50, 280, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbRework.isSelected()), 52, 288);
                    pg.drawString("Rework", 80, 290);
                    pg.draw3DRect(50, 300, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbScrapped.isSelected()), 52, 308);
                    pg.drawString("Scrapped", 80, 310);
                    pg.draw3DRect(50, 320, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbDeviation.isSelected()), 52, 328);
                    pg.drawString("Deviation # ", 80, 330);
                    pg.draw3DRect(130, 320, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbDeviationNbr.getText(), 132, 328);
                    pg.drawString("Description of Issue", 50, 350);
                    pg.draw3DRect(50, 365, 500, 50, rootPaneCheckingEnabled);
                    
                    int count = taIssue.getText().length();
                    int loopnbr = count / 100;
                    int pp = 0;
                                     
                    if (count > 100) {
                        for (int p = 0; p < loopnbr; p++) {
                        pg.drawString(taIssue.getText().substring((p * 100), ((p + 1) * 100)), 54, 380 + (p * 10));
                        pp = p;
                       // pg.drawString(taComments.getText().substring(100,count), 54, 660);
                        }
                        pp = pp + 1;
                        pg.drawString(taIssue.getText().substring((pp * 100)), 54, 380 + (pp * 10));
                    } else
                        pg.drawString(taIssue.getText(), 54, 380); 
                    
                    
                    pg.draw3DRect(200, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbLine.isSelected()), 202, 168);
                    pg.drawString("Line", 230, 170);
                    pg.drawString("Dept#", 250, 170);
                    pg.drawString(tbDept.getText(), 285, 170);
                    pg.draw3DRect(280, 160, 60, 15, rootPaneCheckingEnabled);
                    pg.draw3DRect(200, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbReceiving.isSelected()), 202, 188);
                    pg.drawString("Receiving Inspection", 230, 190);
                    pg.draw3DRect(200, 200, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbCustomer.isSelected()), 202, 208);
                    pg.drawString("Customer", 230, 210);
                    pg.draw3DRect(200, 220, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbEngineering.isSelected()), 202, 228);
                    pg.drawString("Engineering", 230, 230);
                    pg.draw3DRect(200, 240, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbOther.isSelected()), 202, 248);
                    pg.drawString("Other", 230, 250);
                    pg.draw3DRect(255, 240, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbOtherReason.getText(), 265, 250);

                    pg.draw3DRect(400, 160, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbInternal.isSelected()), 402, 168);
                    pg.drawString("Internal Supplier", 430, 170);
                    pg.draw3DRect(400, 180, 10, 10, rootPaneCheckingEnabled);
                    pg.drawString(BlueSeerUtils.convertToX(cbExternal.isSelected()), 402, 188);
                    pg.drawString("External Supplier", 430, 190);

                    pg.drawString("Part Number", 430, 210);
                    pg.draw3DRect(430, 212, 100, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbPartNumber.getText(), 435, 225);
                    pg.drawString("Part Desc", 430, 235);
                    pg.draw3DRect(430, 237, 100, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbPartDesc.getText(), 435, 251);

                    pg.drawString("Quantity Rejected", 380, 270);
                    pg.draw3DRect(460, 260, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbQtyRejected.getText(), 470, 270);
                    pg.drawString("Number of Suspect Containers", 340, 290);
                    pg.draw3DRect(460, 280, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbNumSuspectCont.getText(), 470, 290);
                    pg.drawString("Total Qty Found Defective", 350, 310);
                    pg.draw3DRect(460, 300, 80, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbTotalQty.getText(), 470, 310);

                    pg.drawString("Quality Problem Report requiring 8-D submission must be submitted per the following Timeline:", 50, 435);
                    pg.drawString("1. Initial Response including bsmf.MainFrame.containment due within 24 hours.", 80, 445);
                    pg.drawString("2. Root cause analysis due within 5 work days.", 80, 455);
                    pg.drawString("3. Corrective action plan due within 10 work days from QPR issue date with projected implementation dates.", 80, 465);
                    pg.drawString("4. Final 8-D submission including actual correction action implementation dates and validation methods identified within 30 days", 80, 475);
                    pg.drawString("   of QPR issue date.", 80, 485);

                    pg.drawString("Please contact either of the following if you require additional information:", 50, 495);
                    pg.drawString("SQE: " + sqename + "  PH:  " + sqephone + "  Fax:  " + sqefax + "  Email:  " + sqeemail , 50, 505);
                    pg.drawString("                                                                                          ", 50, 515);

                    pg.drawString("ChargeBack/Debit Memo History", 50, 535);
                    pg.draw3DRect(50, 550, 500, 50, rootPaneCheckingEnabled);
                    pg.drawString(taHistory.getText(), 54, 565);
                    pg.drawString("Total Chargeback for this QPR: ", 350, 620);
                    pg.draw3DRect(470, 610, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbChargeBack.getText(), 490, 618);

                    pg.drawString("SQE comments", 50, 620);
                    pg.draw3DRect(50, 635, 500, 50, rootPaneCheckingEnabled);
                    
                    count = taComments.getText().length();
                    loopnbr = count / 100;
                    pp = 0;
                                     
                    if (count > 100) {
                        for (int p = 0; p < loopnbr; p++) {
                        pg.drawString(taComments.getText().substring((p * 100), ((p + 1) * 100)), 54, 650 + (p * 10));
                        pp = p;
                       // pg.drawString(taComments.getText().substring(100,count), 54, 660);
                        }
                        pp = pp + 1;
                        pg.drawString(taComments.getText().substring((pp * 100)), 54, 650 + (pp * 10));
                    } else
                        pg.drawString(taComments.getText(), 54, 650); 
                    

                    pg.drawString("Date Last Updated: ", 350, 700);
                    pg.draw3DRect(350, 705, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbDateUpdate.getText(), 355, 717);
                    pg.drawString("Date Closed: ", 450, 700);
                    pg.draw3DRect(450, 705, 60, 15, rootPaneCheckingEnabled);
                    pg.drawString(tbDateClosed.getText(), 455, 717);

                    pg.drawString("14.1-1       Revised: 11/29/2012 ", 50, 725);

                    /*
                    pg.drawString("Complaint #: " + tbComplaintNbr.getText(), 50, 50);
                    pg.drawString("QPR: " + bsmf.MainFrame.convertToX(cbQPR.isSelected()) , 50, 60);
                    pg.draw3DRect(80, 50, 10, 10, rootPaneCheckingEnabled);
                    */
                    //Graphics2D g2d = (Graphics2D)pg;
                    //g2d.translate(pf.getImageableX(), pf.getImageableY());
                    // NewJFrame.this.printAll(pg);

                    return Printable.PAGE_EXISTS;
                }
            });

            if (pjob.printDialog() == false) // choose printer
            {
                return;
            }
            pjob.print();
        } catch (PrinterException pe) {
            MainFrame.bslog(pe);
        }// TODO add your handling code here:
    }//GEN-LAST:event_btPrintQPRActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btPrintQPR;
    private javax.swing.JButton btQualProbAdd;
    private javax.swing.JButton btQualProbUpdate;
    private javax.swing.JButton btget;
    private javax.swing.JCheckBox cbCustomer;
    private javax.swing.JCheckBox cbDeviation;
    private javax.swing.JCheckBox cbEngineering;
    private javax.swing.JCheckBox cbExternal;
    private javax.swing.JCheckBox cbInforOnly;
    private javax.swing.JCheckBox cbInternal;
    private javax.swing.JCheckBox cbLine;
    private javax.swing.JCheckBox cbOther;
    private javax.swing.JCheckBox cbQPR;
    private javax.swing.JCheckBox cbReceiving;
    private javax.swing.JCheckBox cbRework;
    private javax.swing.JCheckBox cbScrapped;
    private javax.swing.JCheckBox cbSendSupp;
    private javax.swing.JCheckBox cbSort;
    private javax.swing.JButton jButtonNewComplaint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea taComments;
    private javax.swing.JTextArea taHistory;
    private javax.swing.JTextArea taIssue;
    private javax.swing.JTextField tbChargeBack;
    private javax.swing.JTextField tbComplaintNbr;
    private javax.swing.JTextField tbDateClosed;
    private javax.swing.JTextField tbDateCreate;
    private javax.swing.JTextField tbDateUpdate;
    private javax.swing.JTextField tbDept;
    private javax.swing.JTextField tbDeviationNbr;
    private javax.swing.JTextField tbNumSuspectCont;
    private javax.swing.JTextField tbOriginator;
    private javax.swing.JTextField tbOtherReason;
    private javax.swing.JTextField tbPartDesc;
    private javax.swing.JTextField tbPartNumber;
    private javax.swing.JTextField tbQtyRejected;
    private javax.swing.JTextField tbSuppName;
    private javax.swing.JTextField tbSuppNbr;
    private javax.swing.JTextField tbTotalQty;
    // End of variables declaration//GEN-END:variables
}
