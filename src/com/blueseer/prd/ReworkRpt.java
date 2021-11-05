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

package com.blueseer.prd;

import bsmf.MainFrame;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
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
public class ReworkRpt extends javax.swing.JPanel {
 
    /**
     * Creates new form ScrapReportPanel
     */
    public ReworkRpt() {
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
    
    public void initvars(String[] arg) {
         java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
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
        fromPart = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        toPart = new javax.swing.JTextField();
        fromCode = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        toCode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablescrap = new javax.swing.JTable();
        btexport = new javax.swing.JButton();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelqty = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labeldollar = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbsumpart = new javax.swing.JCheckBox();
        cbsumcode = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("From Date");
        jLabel2.setName("lblfromdate"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");
        jLabel3.setName("lbltodate"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Part");
        jLabel1.setName("lblfromitem"); // NOI18N

        jLabel4.setText("To Part");
        jLabel4.setName("lbltoitem"); // NOI18N

        jLabel5.setText("From Code");
        jLabel5.setName("lblfromcode"); // NOI18N

        jLabel6.setText("To Code");
        jLabel6.setName("lbltocode"); // NOI18N

        tablescrap.setAutoCreateRowSorter(true);
        tablescrap.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablescrap);

        btexport.setText("Export");
        btexport.setName("btexport"); // NOI18N
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        labelqty.setText("0");

        jLabel8.setText("Qty");
        jLabel8.setName("lblqty"); // NOI18N

        labeldollar.setBackground(new java.awt.Color(195, 129, 129));
        labeldollar.setText("0");

        jLabel9.setText("$");
        jLabel9.setName("lblamt"); // NOI18N

        cbsumpart.setText("Part");
        cbsumpart.setName("cbitem"); // NOI18N

        cbsumcode.setText("Code");
        cbsumcode.setName("cbcode"); // NOI18N

        jLabel10.setText("Summarize:");
        jLabel10.setName("lblsummarize"); // NOI18N

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dcTo, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(dcFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fromPart)
                    .addComponent(toPart, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromCode, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toCode, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexport))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbsumpart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbsumcode)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(fromPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(toPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fromCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(toCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)
                                .addComponent(cbsumpart)
                                .addComponent(cbsumcode)
                                .addComponent(jLabel10)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(btexport))
                            .addGap(56, 56, 56)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
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
                int i = 0;
                String frompart = "";
                String topart = "";
                String fromcode = "";
                String tocode = "";
                
                if (fromPart.getText().isEmpty()) {
                    frompart = bsmf.MainFrame.lowchar;
                } else {
                    frompart = fromPart.getText();
                }
                 if (toPart.getText().isEmpty()) {
                    topart = bsmf.MainFrame.hichar;
                } else {
                    topart = toPart.getText();
                }
                  if (fromCode.getText().isEmpty()) {
                    fromcode = bsmf.MainFrame.lowchar;
                } else {
                    fromcode = fromCode.getText();
                }
                   if (toCode.getText().isEmpty()) {
                    tocode = bsmf.MainFrame.hichar;
                } else {
                    tocode = toCode.getText();
                }
                   
                javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Part", "P/M", "Qty", "Cost", "Operation", "Date", "Code", "Dept", "Remarks"});
                tablescrap.setModel(mymodel);
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                  if (cbsumpart.isSelected() && cbsumcode.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell,  " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_part, tr_ref " + 
                        "   ;");
                 }
                 
                  if (cbsumcode.isSelected() && ! cbsumpart.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_ref " + 
                        "   ;");
                 } 
                  
                 if (cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_part " + 
                        "   ;");
                 } 
                 
                 
                 if (! cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                 res = st.executeQuery("SELECT tr_part, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                         " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                         " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " + 
                        " order by tr_id desc ;");    
                 }
                 
                while (res.next()) {
                    i++;
                    
                    if (cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                        mymodel.addRow(new Object[]{
                                res.getString("tr_part"),
                                res.getString("it_code"),
                                res.getInt("tr_qty"),
                                res.getDouble("tot"),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                            });
                    } else if (cbsumcode.isSelected() && ! cbsumpart.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                         mymodel.addRow(new Object[]{
                                null,
                                null,
                                res.getInt("tr_qty"),
                                res.getDouble("tot"),
                                null,
                                null,
                                res.getString("tr_ref"),
                                null,
                                null,
                                null,
                                null
                            });
                    }
                    else if (cbsumcode.isSelected() &&  cbsumpart.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                         mymodel.addRow(new Object[]{
                                res.getString("tr_part"),
                                res.getString("it_code"),
                                res.getInt("tr_qty"),
                                res.getDouble("tot"),
                                null,
                                null,
                                res.getString("tr_ref"),
                                null,
                                null,
                                null,
                                null
                            });
                    }
                    else {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + (res.getDouble("amt") * res.getInt("tr_qty"));
                         mymodel.addRow(new Object[]{res.getString("tr_part"),
                                res.getString("it_code"),
                                res.getInt("tr_qty"),
                                res.getDouble("amt"),
                                res.getInt("tr_op"),
                                res.getString("tr_eff_date"),
                                res.getString("tr_ref"),
                                res.getString("tr_actcell"),
                                res.getString("tr_timestamp"),
                                res.getString("tr_export"),
                                res.getString("tr_userid")
                            });
                    }
                }
                labeldollar.setText(String.valueOf(currformatDouble(dol)));
                labelcount.setText(String.valueOf(i));
                labelqty.setText(String.valueOf(qty));
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
           int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##", new DecimalFormatSymbols(Locale.US));
                String frompart = "";
                String topart = "";
                String fromcode = "";
                String tocode = "";
                
                if (fromPart.getText().isEmpty()) {
                    frompart = bsmf.MainFrame.lowchar;
                } else {
                    frompart = fromPart.getText();
                }
                 if (toPart.getText().isEmpty()) {
                    topart = bsmf.MainFrame.hichar;
                } else {
                    topart = toPart.getText();
                }
                  if (fromCode.getText().isEmpty()) {
                    fromcode = bsmf.MainFrame.lowchar;
                } else {
                    fromcode = fromCode.getText();
                }
                   if (toCode.getText().isEmpty()) {
                    tocode = bsmf.MainFrame.hichar;
                } else {
                    tocode = toCode.getText();
                }
                   
           DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            output = new BufferedWriter(new FileWriter(f));
               String myheader = "";
                if (cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                myheader = "Part,Code,Qty,Tot";
                output.write(myheader + '\n');
                } else if (cbsumcode.isSelected() && ! cbsumpart.isSelected()) {
                     myheader = "Qty,Tot,Ref";
                output.write(myheader + '\n');
                } else if (cbsumcode.isSelected() &&  cbsumpart.isSelected()) {
                      myheader = "Part,Code,Qty,Op,Ref";
                output.write(myheader + '\n');     
                } else {
                     myheader = "Part,Code,Qty,Amt,Op,EffDate,Ref,Dept/Cell";
                output.write(myheader + '\n');     
                }
                
              
                 
                 
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

               
                  if (cbsumpart.isSelected() && cbsumcode.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell,  " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_part, tr_ref " + 
                        "   ;");
                 }
                 
                  if (cbsumcode.isSelected() && ! cbsumpart.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_ref " + 
                        "   ;");
                 } 
                  
                 if (cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                res = st.executeQuery("SELECT tr_part, tr_type, it_code, sum(tr_qty) as tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                        " sum(case when it_code = 'M' then itr_total else itc_total end * tr_qty) as tot,  " +
                        " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                        " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " +
                        " group by tr_part " + 
                        "   ;");
                 } 
                 
                 
                 if (! cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                 res = st.executeQuery("SELECT tr_part, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell, " +
                        " case when it_code = 'M' then itr_total else itc_total end as amt,  " +
                         " tr_timestamp, tr_export, tr_userid " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " left outer join itemr_cost on itr_item = tr_part and itr_op = tr_op and itr_routing = item_mstr.it_wf" +
                         " left outer join item_cost on itc_item = tr_part and itc_set = 'standard' " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                        " AND tr_type = 'ISS-REWK' " + 
                        " order by tr_id desc ;");    
                 }

                
                while (res.next()) {
                    i++;
                    
                    if (cbsumpart.isSelected() && ! cbsumcode.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                     String newstring = res.getString("tr_part") + "," + res.getString("it_code").replace(",","") + "," + 
                            res.getString("tr_qty") + "," + res.getDouble("tot");
                    output.write(newstring + '\n');
                       
                    } else if (cbsumcode.isSelected() && ! cbsumpart.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                     String newstring = 
                            res.getInt("tr_qty") + "," + res.getDouble("tot") + "," + 
                            res.getString("tr_ref") ;
                    output.write(newstring + '\n');
                    }
                    else if (cbsumcode.isSelected() &&  cbsumpart.isSelected()) {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + res.getDouble("tot");
                    String newstring = res.getString("tr_part") + "," + res.getString("it_code").replace(",","") + "," + 
                            res.getInt("tr_qty") + "," + res.getDouble("tr_op") + "," + 
                            res.getString("tr_ref") ;
                            
                    output.write(newstring + '\n');
                    }
                    else {
                   qty = qty + res.getInt("tr_qty");
                    dol = dol + (res.getDouble("amt") * res.getInt("tr_qty"));
                    String newstring = res.getString("tr_part") + "," + res.getString("it_code").replace(",","") + "," + 
                            res.getInt("tr_qty") + "," + res.getDouble("amt") + "," + res.getInt("tr_op") + "," + res.getString("tr_eff_date") + "," + 
                            res.getString("tr_ref") + "," + res.getString("tr_actcell") ;
                           output.write(newstring + '\n');
                        
                    }
                }
                
                
                
             bsmf.MainFrame.show(getMessageTag(1126));
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    private javax.swing.JButton btRun;
    private javax.swing.JButton btexport;
    private javax.swing.JCheckBox cbsumcode;
    private javax.swing.JCheckBox cbsumpart;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JTextField fromCode;
    private javax.swing.JTextField fromPart;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JLabel labelqty;
    private javax.swing.JTable tablescrap;
    private javax.swing.JTextField toCode;
    private javax.swing.JTextField toPart;
    // End of variables declaration//GEN-END:variables
}
