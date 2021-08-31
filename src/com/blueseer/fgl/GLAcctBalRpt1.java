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

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import java.awt.Component;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
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
public class GLAcctBalRpt1 extends javax.swing.JPanel {
 
    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("account"), getGlobalColumnTag("description"), getGlobalColumnTag("beginbalance"), getGlobalColumnTag("activity"), getGlobalColumnTag("endbalance")});
                
    
    /**
     * Creates new form ScrapReportPanel
     */
    public GLAcctBalRpt1() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
    
    
    public void initvars(String[] arg) {
        
        mymodel.setNumRows(0);
        tablereport.setModel(mymodel);
        
        java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
         
         
        ArrayList myacct = OVData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacctfrom.addItem(myacct.get(i));
            ddacctto.addItem(myacct.get(i));
        }
            ddacctfrom.setSelectedIndex(0);
            ddacctto.setSelectedIndex(ddacctto.getItemCount() - 1);
        
         
          
        ArrayList cc = OVData.getGLCCList();
        for (int i = 0; i < cc.size(); i++) {
            ddccfrom.addItem(cc.get(i));
            ddccto.addItem(cc.get(i));
        }
            ddccfrom.setSelectedIndex(0);
            ddccto.setSelectedIndex(ddccto.getItemCount() - 1);
       
          
          
          
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
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        btexport = new javax.swing.JButton();
        lblbegbal = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblactbal = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblendbal = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        ddacctfrom = new javax.swing.JComboBox();
        ddacctto = new javax.swing.JComboBox();
        ddccfrom = new javax.swing.JComboBox();
        ddccto = new javax.swing.JComboBox();
        cbzero = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setText("From Date");
        jLabel2.setName("lblfromdate"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");
        jLabel3.setName("lbltodate"); // NOI18N

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Acct");
        jLabel1.setName("lblfromacct"); // NOI18N

        jLabel4.setText("To Acct");
        jLabel4.setName("lbltoacct"); // NOI18N

        jLabel5.setText("From CC");
        jLabel5.setName("lblfromcc"); // NOI18N

        jLabel6.setText("To CC");
        jLabel6.setName("lbltocc"); // NOI18N

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablereport);

        btexport.setText("Export");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        lblbegbal.setText("0");

        jLabel7.setText("Beginning Balance");
        jLabel7.setName("lblbegbalance"); // NOI18N

        lblactbal.setText("0");

        jLabel8.setText("Activity");
        jLabel8.setName("lblactivity"); // NOI18N

        lblendbal.setBackground(new java.awt.Color(195, 129, 129));
        lblendbal.setText("0");

        EndBal.setText("Ending Balance");
        EndBal.setName("lblendbalance"); // NOI18N

        cbzero.setText("Supress Zeros");
        cbzero.setName("cbsuppress"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbzero)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dcTo, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                            .addComponent(dcFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddccfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddccto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexport)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EndBal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblendbal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblactbal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblbegbal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(ddccfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(ddccto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(btexport))
                            .addGap(56, 56, 56)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblbegbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblactbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(EndBal)
                            .addComponent(lblendbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbzero)))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed

    
try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                int i = 0;
               
               mymodel.setNumRows(0);
                   
                
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 // How to do this???
                 // what period am I starting
                 // am I within a period
                 // what period am I ending
                 // am I short of today's date
                 String[] fromdatearray = OVData.getGLCalForDate(dfdate.format(dcFrom.getDate()));
                 String[] todatearray = OVData.getGLCalForDate(dfdate.format(dcTo.getDate()));
                 int fromdateperiod = Integer.valueOf(fromdatearray[1].toString());
                 int fromdateyear = Integer.valueOf(fromdatearray[0].toString());
                 int todateperiod = Integer.valueOf(todatearray[1].toString());
                 int todateyear = Integer.valueOf(todatearray[0].toString());
                 int priorperiod = 0;
                 int prioryear = 0;
                 double begbal = 0.00;
                 double activity = 0.00;
                 double endbal = 0.00;
                 double totbegbal = 0.00;
                 double totactivity = 0.00;
                 double totendbal = 0.00;
                 double preact = 0.00;
                 double postact = 0.00;
                 Date p_datestart = null;
                 Date p_dateend = null;
                 int period = 0;
                 
                 
                 
                 for (int p = fromdateperiod ; p <= todateperiod ; p++) {
                  ArrayList<Date> actdatearray = OVData.getGLCalForPeriod(String.valueOf(fromdateyear), String.valueOf(p));  
                   p_datestart = actdatearray.get(0);
                   p_dateend = actdatearray.get(1);
                   period = p;
                 }
              /*   
                 bsmf.MainFrame.show(String.valueOf("fromdateyear=" + fromdateyear + "\n" + 
                                               "fromdateperiod=" + fromdateperiod + "\n" + 
                                               "todateyear=" + todateyear + "\n" + 
                                               "todateperiod=" + todateperiod + "\n" + 
                                               "p_startdate=" + p_datestart + "\n" + 
                                               "p_enddate=" + p_dateend));
                */
                 
                 if (p_datestart == null) {
                     bsmf.MainFrame.show("invalid start date");
                     return;
                 }
                 
                 if (p_dateend == null) {
                     bsmf.MainFrame.show("invalid start date");
                     return;
                 }
                 
                 ArrayList<String> accounts = OVData.getGLAcctListRange(ddacctfrom.getSelectedItem().toString(), ddacctto.getSelectedItem().toString());
                
                  totbegbal = 0.00;
                  totactivity = 0.00;
                  totendbal = 0.00;
                 
                 
                 prioryear = fromdateyear - 1;
                 String site = "1516"; 
                 String accttype = "";
                 String acctdesc = "";
                 
                 ACCTS:    for (String account : accounts) {
                  begbal = 0.00;
                  activity = 0.00;
                  endbal = 0.00;
                  preact = 0.00;
                  postact = 0.00;
                 
                  accttype = OVData.getGLAcctType(account);
                  acctdesc = OVData.getGLAcctDesc(account);
                  
                 // calculate all acb_mstr records for whole periods < fromdateperiod
                    // begbal += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
                  if (accttype.equals("L") || accttype.equals("A")) {
                      //must be type balance sheet
                  res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct = " + "'" + account + "'" + " AND " +
                        " acb_site = " + "'" + site + "'" + " AND " +
                        " (( acb_year = " + "'" + fromdateyear + "'" + " AND acb_per < " + "'" + fromdateperiod + "'" + " ) OR " +
                        "  ( acb_year <= " + "'" + prioryear + "'" + " )) " +
                        ";");
                
                       while (res.next()) {
                          begbal += res.getDouble(("sum"));
                       }
                  } else {
                      //must be type income statement
                      begbal = 0;
                  }
                  
                 
                 
                   
                   for (int p = fromdateperiod ; p <= todateperiod ; p++) {
                  ArrayList<Date> actdatearray = OVData.getGLCalForPeriod(String.valueOf(fromdateyear), String.valueOf(p));  
                   p_datestart = actdatearray.get(0);
                   p_dateend = actdatearray.get(1);
                   period = p; 
                   
                   // calculate period(s) activity defined by date range 
                  // activity += OVData.getGLAcctBalSummCC(account.toString(), String.valueOf(fromdateyear), String.valueOf(p));
                       res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where acb_year = " +
                        "'" + String.valueOf(fromdateyear) + "'" + 
                        " AND acb_per = " +
                        "'" + String.valueOf(period) + "'" +
                        " AND acb_acct = " +
                        "'" + account.toString() + "'" +
                        " AND acb_site = " + "'" + site + "'" +
                        ";");
                
                       while (res.next()) {
                          activity += res.getDouble(("sum"));
                       }
                 
                       /*
                // back out fromdate to periodstartdate transactions from balance if fromdate < periodstartdate
                   if (p_datestart.compareTo(dcFrom.getDate()) < 0) {
                     //  preact = OVData.getSummaryGLHistSumCC(account,  dfdate.format(p_datestart), dfdate.format(dcFrom.getDate()));
                       res = st.executeQuery("SELECT sum(glh_amt) as sum from gl_hist where " +
                        " glh_effdate >= " + "'" + dfdate.format(p_datestart) + "'" + " AND " +
                        " glh_effdate <= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + " AND " +
                        " glh_acct = " + "'" + account + "'" + " AND " +  
                        " glh_site = " + "'" + site + "'" +
                        " group by glh_acct " + ";" );
                        while (res.next()) {
                           preact += res.getDouble("sum");
                        }
                       activity -= preact;
                   }
                   
                       // back out periodenddate to ToDate transactions from balance if periodendate > Todate
                   if (p_dateend.compareTo(dcTo.getDate()) > 0) {
                    //    postact = OVData.getSummaryGLHistSumCC(account, dfdate.format(p_dateend), dfdate.format(dcTo.getDate()));
                      res = st.executeQuery("SELECT sum(glh_amt) as sum from gl_hist where " +
                        " glh_effdate >= " + "'" + dfdate.format(p_datestart) + "'" + " AND " +
                        " glh_effdate <= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + " AND " +
                        " glh_acct = " + "'" + account + "'" + " AND " +  
                        " glh_site = " + "'" + site + "'" +
                        " group by glh_acct " + ";" );
                        while (res.next()) {
                           postact += res.getDouble("sum");
                        } 
                       activity -= postact;
                   }
                               */
                               
                   } 
                 
                               
                 endbal = begbal + activity;
                 
                 if (cbzero.isSelected() && begbal == 0 && endbal == 0 && activity == 0) {
                     continue ACCTS;
                 }
                 
                 // now sum for the total labels display
                 totendbal = totendbal + endbal;
                 totbegbal = totbegbal + begbal;
                 totactivity = totactivity + activity;
                 
               //  if (begbal == 0 && endbal == 0 && activity == 0)
               //      bsmf.MainFrame.show(account);
                 mymodel.addRow(new Object[]{account,
                                acctdesc,
                                df.format(begbal),
                                df.format(activity),
                                df.format(endbal)
                            });
                
                }
                
                lblendbal.setText(df.format(totendbal));
                lblbegbal.setText(df.format(totbegbal));
                lblactbal.setText(df.format(totactivity));
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem executing Acct Bal Report");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
       FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;
        
         int i = 0;
                String frompart = "";
                String topart = "";
                String fromcode = "";
                String tocode = "";
                
            
                   
           DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            output = new BufferedWriter(new FileWriter(f));
      
               String myheader = "Part,P/M,Qty,Operation,Date,Code,Dept";
                
                 output.write(myheader + '\n');
                 
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

               res = st.executeQuery("SELECT tr_part, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                       " AND tr_type = 'ISS-SCRAP' " + 
                        " order by tr_id desc ;");


                while (res.next()) {
                    String newstring = res.getString("tr_part") + "," + res.getString("it_code").replace(",","") + "," + 
                            res.getString("tr_qty") + "," + res.getString("tr_op") + "," + res.getString("tr_eff_date") + "," + 
                            res.getString("tr_ref") + "," + res.getString("tr_actcell") ;
                            
                    output.write(newstring + '\n');
                }
             bsmf.MainFrame.show("file has been created");
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot extract tran_mstr data");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        
        output.close();
        
        
        
        } catch (IOException ex) {
            Logger.getLogger(bsmf.MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btexportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btexport;
    private javax.swing.JCheckBox cbzero;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddacctfrom;
    private javax.swing.JComboBox ddacctto;
    private javax.swing.JComboBox ddccfrom;
    private javax.swing.JComboBox ddccto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblactbal;
    private javax.swing.JLabel lblbegbal;
    private javax.swing.JLabel lblendbal;
    private javax.swing.JTable tablereport;
    // End of variables declaration//GEN-END:variables
}
