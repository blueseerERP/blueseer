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
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import com.blueseer.vdr.venData;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class ExpenseBrowse extends javax.swing.JPanel {
 
     String chartfilepath = OVData.getSystemTempDirectory() + "/" + "chartexpinc.jpg";
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                getGlobalColumnTag("vendor"), 
                getGlobalColumnTag("name"), 
                getGlobalColumnTag("reference"), 
                getGlobalColumnTag("duedate"), 
                getGlobalColumnTag("amount"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("status"), 
                getGlobalColumnTag("currency"), 
                getGlobalColumnTag("account")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 5  )       
                            return Double.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    
    
    
       class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    /**
     * Creates new form ScrapReportPanel
     */
    public ExpenseBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void chartExpense() {
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
                java.util.Date now = new java.util.Date();
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                  Calendar cal = new GregorianCalendar();
                  cal.set(Calendar.DAY_OF_YEAR, 1);
                  java.util.Date firstday = cal.getTime();
                 
                if (cbchart.isSelected()) {
                 res = st.executeQuery("select vod_expense_acct, sum(vod_voprice * vod_qty) as 'sum' from ap_mstr " +
                             //  " ap_ref, ap_effdate, ap_duedate, ap_amt, ap_base_amt,  " +
                             //  " ap_status, ap_curr, vod_item, vod_expense_acct " +
                             //  " inner join vd_mstr on vd_addr = ap_vend " +
                               " inner join vod_mstr on vod_id = ap_nbr " + 
                               " where ap_vend >= " + "'" + ddfromvend.getSelectedItem().toString() + "'" +
                               " and ap_vend <= " + "'" + ddtovend.getSelectedItem().toString() + "'" +
                               " and ap_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                               " and ap_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                               " and ap_type = 'E' " +
                               " and ap_status = 'c' " +
                               " group by vod_expense_acct " +
                               ";");   
                }  else {
                res = st.executeQuery("select ap_vend, sum(vod_voprice * vod_qty) as 'sum' from ap_mstr " +
                             //  " ap_ref, ap_effdate, ap_duedate, ap_amt, ap_base_amt,  " +
                             //  " ap_status, ap_curr, vod_item, vod_expense_acct " +
                             //  " inner join vd_mstr on vd_addr = ap_vend " +
                               " inner join vod_mstr on vod_id = ap_nbr " + 
                               " where ap_vend >= " + "'" + ddfromvend.getSelectedItem().toString() + "'" +
                               " and ap_vend <= " + "'" + ddtovend.getSelectedItem().toString() + "'" +
                               " and ap_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                               " and ap_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                               " and ap_type = 'E' " +
                               " and ap_status = 'c' " +
                               " group by ap_vend " +
                               ";");
                }
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String xvar = "";
                while (res.next()) {
                    if (cbchart.isSelected()) {
                        if (res.getString("vod_expense_acct") == null || res.getString("vod_expense_acct").isEmpty()) {
                        xvar = "Unassigned";
                        } else {
                        xvar = res.getString("vod_expense_acct");   
                        } 
                    } else {
                        if (res.getString("ap_vend") == null || res.getString("ap_vend").isEmpty()) {
                        xvar = "Unassigned";
                        } else {
                        xvar = res.getString("ap_vend");   
                        } 
                    }
                    
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                  dataset.setValue(xvar, amt);
                }
        
        String title = getTitleTag(5027);
        if (cbchart.isSelected()) {
            title = "Expense By Account";
        }        
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, false, false, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(chartfilepath), chart, (int) (this.getWidth() / 2), (int) (this.getHeight() / 1.2));
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(chartfilepath);
        myicon.getImage().flush();   
        this.pielabel.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
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
        chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
        btchart.setEnabled(true);
        java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
        mymodel.setNumRows(0);
        tablereport.setModel(mymodel);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        
       //  tablereport.getColumnModel().getColumn(0).setCellRenderer(new GLAcctBalRpt3.ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        
         ArrayList myvend = venData.getVendMstrList();
        ddfromvend.removeAllItems();
        ddtovend.removeAllItems();
        for (int i = 0; i < myvend.size(); i++) {
            ddfromvend.addItem(myvend.get(i));
            ddtovend.addItem(myvend.get(i));
        }
        ddfromvend.setSelectedIndex(0);
        ddtovend.setSelectedIndex(ddfromvend.getItemCount() - 1);
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList(bsmf.MainFrame.userid);
        for (int i = 0; i < mylist.size(); i++) {
            ddsite.addItem(mylist.get(i));
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
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
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ddfromvend = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        chartpanel = new javax.swing.JPanel();
        pielabel = new javax.swing.JLabel();
        bthidechart = new javax.swing.JButton();
        ddtovend = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        btchart = new javax.swing.JButton();
        cbchart = new javax.swing.JCheckBox();
        tbprint = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("From Vendor");
        jLabel2.setName("lblfromvend"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Date");
        jLabel1.setName("lblfromdate"); // NOI18N

        jLabel4.setText("To Date");
        jLabel4.setName("lbltodate"); // NOI18N

        jPanel2.setPreferredSize(new java.awt.Dimension(904, 402));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jPanel3.setLayout(new java.awt.BorderLayout());

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

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel3);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new java.awt.CardLayout());

        pielabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pielabel.setToolTipText("");
        chartpanel.add(pielabel, "card2");

        jPanel2.add(chartpanel);

        bthidechart.setText("Hide Chart");
        bthidechart.setName("bthidechart"); // NOI18N
        bthidechart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidechartActionPerformed(evt);
            }
        });

        jLabel3.setText("To Vendor");
        jLabel3.setName("lbltovend"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        btchart.setText("Chart");
        btchart.setName("btchart"); // NOI18N
        btchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchartActionPerformed(evt);
            }
        });

        cbchart.setText("Chart By Account");
        cbchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbchartActionPerformed(evt);
            }
        });

        tbprint.setText("PDF");
        tbprint.setName("btpdf"); // NOI18N
        tbprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbprintActionPerformed(evt);
            }
        });

        jLabel5.setText("Site");
        jLabel5.setName("lblsite"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddfromvend, 0, 115, Short.MAX_VALUE)
                    .addComponent(ddtovend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbprint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btchart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bthidechart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbchart)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(ddfromvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(btRun)
                        .addComponent(bthidechart)
                        .addComponent(btchart)
                        .addComponent(cbchart)
                        .addComponent(tbprint)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddtovend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
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
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                   
                 mymodel.setNumRows(0);
                tablereport.setModel(mymodel);
                tablereport.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
                 
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                     
                res = st.executeQuery("select ap_nbr, ap_vend, vd_name, ap_type, " +
                               " ap_ref, ap_effdate, ap_duedate, (vod_voprice * vod_qty) as amt,  " +
                               " ap_status, ap_curr, vod_item, vod_expense_acct " +
                               " from ap_mstr inner join vd_mstr on vd_addr = ap_vend " +
                               " inner join vod_mstr on vod_id = ap_nbr " + 
                               " where ap_vend >= " + "'" + ddfromvend.getSelectedItem().toString() + "'" +
                               " and ap_vend <= " + "'" + ddtovend.getSelectedItem().toString() + "'" +
                               " and ap_effdate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" +
                               " and ap_effdate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" +
                               " and ap_type = 'E' " +
                               " and ap_status = 'c' " +
                               " and ap_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                               ";");
                               
                    while (res.next()) {
                        mymodel.addRow(new Object[] {
                            bsNumber(res.getString("ap_nbr")),
                            res.getString("ap_vend"),
                            res.getString("vd_name"),
                            res.getString("ap_ref"),
                            getDateDB(res.getString("ap_effdate")),
                            bsParseDouble(currformatDouble(res.getDouble("amt"))),
                            res.getString("vod_item"),
                            res.getString("ap_status"),
                            res.getString("ap_curr"),
                            res.getString("vod_expense_acct")
                        });
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
  
       
    }//GEN-LAST:event_btRunActionPerformed

    private void bthidechartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidechartActionPerformed
       chartpanel.setVisible(false);
        bthidechart.setEnabled(false);
        btchart.setEnabled(true);
    }//GEN-LAST:event_bthidechartActionPerformed

    private void btchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchartActionPerformed
        chartExpense();
        chartpanel.setVisible(true);
        bthidechart.setEnabled(true);
        btchart.setEnabled(false);
    }//GEN-LAST:event_btchartActionPerformed

    private void cbchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbchartActionPerformed
        chartExpense();
        chartpanel.setVisible(true);
        bthidechart.setEnabled(true);
        btchart.setEnabled(false);
    }//GEN-LAST:event_cbchartActionPerformed

    private void tbprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbprintActionPerformed

        if (tablereport != null && mymodel.getRowCount() > 0) {
            OVData.printJTableToJasper("Expense Report", tablereport, "genericJTableL10.jasper" );
            /*
            try {

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                HashMap hm = new HashMap();
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
                File mytemplate = new File("jasper/orderbrowsesumary.jasper");

                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tableorder.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ordbrowse.pdf");

                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setVisible(true);
            } catch (Exception e) {
                MainFrame.bslog(e);
            }
            */
        }
    }//GEN-LAST:event_tbprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btchart;
    private javax.swing.JButton bthidechart;
    private javax.swing.JCheckBox cbchart;
    private javax.swing.JPanel chartpanel;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddfromvend;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddtovend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel pielabel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbprint;
    // End of variables declaration//GEN-END:variables
}
