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

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
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
public class IncomeStatementRpt1 extends javax.swing.JPanel {
 
     MyTableModel mymodel = new IncomeStatementRpt1.MyTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("description"), 
                            getGlobalColumnTag("definition"),  
                            getGlobalColumnTag("amount")});
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
          //  if (col == 2 || col == 3 || col == 4)       
          //      return Double.class;  
          //  else return String.class;  //other columns accept String values  
              return String.class;
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }    
    
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 8);  // 8 = status column
        
         if ("error".equals(status)) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
        } else if ("close".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("backorder".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
        
        
    public IncomeStatementRpt1() {
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
        mymodel.setRowCount(0);
        
        mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
         
          ddyear.removeAllItems();
        for (int i = 1967 ; i < 2222; i++) {
            ddyear.addItem(String.valueOf(i));
        }
        ddyear.setSelectedItem(dfyear.format(now));
            
        ddper.removeAllItems();
        for (int i = 1 ; i <= 12; i++) {
            ddper.addItem(String.valueOf(i));
        }
       
         String[] fromdatearray = OVData.getGLCalForDate(dfdate.format(now));
        //int fromdateperiod = Integer.valueOf(fromdatearray.get(1).toString());
        ddper.setSelectedItem(fromdatearray[1].toString());
        ArrayList startend = OVData.getGLCalForPeriod(ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString());
        datelabel.setText(startend.get(0).toString() + " To " + startend.get(1).toString());
        
        ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labeldollar = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddyear = new javax.swing.JComboBox();
        ddper = new javax.swing.JComboBox();
        datelabel = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Year");
        jLabel2.setName("lblyear"); // NOI18N

        jLabel3.setText("Period");
        jLabel3.setName("lblperiod"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(mytable);

        labelcount.setText("0");

        jLabel7.setText("Operating Profit Before Taxes:");
        jLabel7.setName("lbloperating"); // NOI18N

        labeldollar.setBackground(new java.awt.Color(195, 129, 129));
        labeldollar.setText("0");

        jLabel9.setText("EBITDA");
        jLabel9.setName("lblebitda"); // NOI18N

        jLabel4.setText("Site");
        jLabel4.setName("lblsite"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btRun))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddper, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 666, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(btprint)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(datelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE))
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
               
                
               
               
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
                 mymodel.setNumRows(0);
                   
               
                mytable.setModel(mymodel);
                mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
             
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                 
                 double sales = 0;
                 double cogs = 0;
                 double stdmargin  = 0;
                 double mtlvar = 0;
                 double lbrvar = 0;
                 double bdnvar = 0;
                 double mfggrossmargin = 0;
                 double prodeng = 0;
                 double marketingandsales = 0;
                 double grossmargin = 0;
                 double generalandadmin = 0;
                 double profitbeforealloc = 0;
                 double interest = 0;
                 double alloc = 0;
                 double mgtfees = 0;
                 double bankfees = 0;
                 double other = 0;
                 double opprofitbeforetaxes = 0;
                 double ebitda = 0;
                 double depreciation = 0;
                 
                 String startacct = "";
                 String endacct = "";
                 ArrayList excludeaccts;
                 ArrayList includeaccts;
                 
                 
                 
                 
                 
                 // Sales
                 res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'sales';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                 res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          sales += res.getDouble("sum");
                       }
                       
                        // now lets back out accts excluded
                      excludeaccts = OVData.getGLICAccts("sales", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           sales = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), sales);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("sales", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           sales = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), sales);
                       }
                       
                       
                       sales = (-1 * sales);
                       
                       
                       
                     mymodel.addRow(new Object[] { "Sales", "", sales});
                 
                     
                     
                     // COGS
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'cogs';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                     
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          cogs += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                      excludeaccts = OVData.getGLICAccts("cogs", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           cogs = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), cogs);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("cogs", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           cogs = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), cogs);
                       }
                     mymodel.addRow(new Object[] { "Cost Of Goods", "", cogs});  
                  
                     // Standard Margin = Sales - Cogs
                     stdmargin = sales - cogs;
                     mymodel.addRow(new Object[] { "Standard Margin", "Sales less COGS", stdmargin}); 
                   
                     
                         // Mtl Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mtlvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          mtlvar += res.getDouble("sum");
                       }
                       // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("mtlvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           mtlvar = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mtlvar);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("mtlvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           mtlvar = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mtlvar);
                       }
                     mymodel.addRow(new Object[] { "Matl Variance", "", mtlvar});   
                     
                 


                    // Labor Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'lbrvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          lbrvar += res.getDouble("sum");
                       }
                      // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("lbrvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           lbrvar = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), lbrvar);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("lbrvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           lbrvar = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), lbrvar);
                       }
                  mymodel.addRow(new Object[] { "Labor Variance", "", lbrvar});  
                     
                 
                  

                      // Burden Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'bdnvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          bdnvar += res.getDouble("sum");
                       }
                        // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("bdnvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           bdnvar = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bdnvar);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("bdnvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           bdnvar = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bdnvar);
                       }
                     mymodel.addRow(new Object[] { "Burden Variance", "", bdnvar});   
                     
                     
                     
                     
                     
                     
                      // MFG Gross Margin
                     mfggrossmargin = stdmargin - mtlvar - lbrvar - bdnvar;
                     mymodel.addRow(new Object[] { "MFG Gross Margin", "Standard Margin Less Variance", mfggrossmargin}); 
                     
                     
                     //ProdEng
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'prodeng';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                
                       while (res.next()) {
                          prodeng += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("prodeng", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           prodeng = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), prodeng);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("prodeng", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           prodeng = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), prodeng);
                       }
                     mymodel.addRow(new Object[] { "Product Engineering", "", prodeng});  
                     
                     
                       // Gross Margin
                     grossmargin = mfggrossmargin - prodeng;
                     mymodel.addRow(new Object[] { "Gross Margin", "MFG Gross Margin less Prod Eng Expense", grossmargin}); 
                     
                       //Marketing and sales
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mktsales';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                
                       while (res.next()) {
                          marketingandsales += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("mktsales", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           marketingandsales = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), marketingandsales);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("mktsales", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           marketingandsales = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), marketingandsales);
                       }
                     mymodel.addRow(new Object[] { "Sales and Marketing", "", marketingandsales});  
                     
                     
                     
                       //Gen and Admin
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'g&a';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          generalandadmin += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("g&a", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           generalandadmin = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), generalandadmin);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("g&a", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           generalandadmin = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), generalandadmin);
                       }
                     mymodel.addRow(new Object[] { "General and Admin", "", generalandadmin});  
                     
                     
                        // Profit Before Allocation
                     profitbeforealloc = grossmargin - marketingandsales - generalandadmin;
                     mymodel.addRow(new Object[] { "Profit Before Allocations", "Gross Margin less SMG&A Expenses",  profitbeforealloc}); 
                     
                   

                        //Interest
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'interest';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          interest += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("interest", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           interest = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), interest);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("interest", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           interest = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), interest);
                       }
                     mymodel.addRow(new Object[] { "Interest", "", interest});  
                     
                         //Allocations
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'alloc';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          alloc += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("alloc", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           alloc = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), alloc);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("alloc", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           alloc = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), alloc);
                       }
                     mymodel.addRow(new Object[] { "Corporate Allocations", "", alloc}); 
                     
                            //Management Fees
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mgtfee';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          mgtfees += res.getDouble("sum");
                       }
                           // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("mgtfee", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           mgtfees = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mgtfees);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("mgtfee", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           mgtfees = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mgtfees);
                       }
                     mymodel.addRow(new Object[] { "Management Fees", "", mgtfees});  
                     
                     
                     
                               //Bank Fees
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'bankfee';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          bankfees += res.getDouble("sum");
                       }
                            // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("bankfee", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           bankfees = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bankfees);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("bankfee", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           bankfees = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bankfees);
                       }
                     mymodel.addRow(new Object[] { "Bank Fees", "", bankfees});  
                     
                     
                                 //Other income/expense
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'otherie';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                   while (res.next()) {
                          other += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("otherie", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           other = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), other);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("otherie", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           other = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), other);
                       }
                     mymodel.addRow(new Object[] { "Other Income/Expense", "", other});  
                     
                     
                     
                     
                       // Operational Profit before taxes
                     opprofitbeforetaxes = profitbeforealloc -interest - alloc - mgtfees - bankfees - other;
                     mymodel.addRow(new Object[] { "Operating Profit Before Taxes", "Op Profit  less Int&Alloc&Fees&Other", opprofitbeforetaxes});  
                     
                                   //depreciation
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'depreciation';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          depreciation += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = OVData.getGLICAccts("depreciation", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           depreciation = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), depreciation);
                       }
                       // now add accts that are included
                       includeaccts = OVData.getGLICAccts("depreciation", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           depreciation = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), depreciation);
                       }
                     mymodel.addRow(new Object[] { "Depreciation", "", depreciation});
                     
                     
                     
                       // EBITDA
                     ebitda = opprofitbeforetaxes + depreciation + interest + alloc + mgtfees + bankfees + other;
                     mymodel.addRow(new Object[] { "EBITDA","", ebitda});  
                     
                     
                labeldollar.setText(String.valueOf(currformatDouble(ebitda)));
                labelcount.setText(String.valueOf(currformatDouble(opprofitbeforetaxes)));
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                HashMap hm = new HashMap();
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/incomestatement.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(mytable.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/is.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btprint;
    private javax.swing.JLabel datelabel;
    private javax.swing.JComboBox ddper;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JTable mytable;
    // End of variables declaration//GEN-END:variables
}
