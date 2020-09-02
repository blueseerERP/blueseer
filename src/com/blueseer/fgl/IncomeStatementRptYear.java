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
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Paint;
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
import javax.swing.ImageIcon;
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
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author vaughnte
 */
public class IncomeStatementRptYear extends javax.swing.JPanel {
 
     MyTableModel mymodel = new IncomeStatementRptYear.MyTableModel(new Object[][]{},
                        new String[]{"Element", "1",  "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    
        class CustomRenderer extends BarRenderer
{

   public CustomRenderer()
   {
   }

   public Paint getItemPaint(final int row, final int column)
   {
       Color mycolor = null;
     CategoryDataset cd = getPlot().getDataset();
    if(cd != null)
    {
      String l_rowKey = (String)cd.getRowKey(row);
      String l_colKey = (String)cd.getColumnKey(column);
      int l_value  = cd.getValue(l_rowKey, l_colKey).intValue();
      
      
   //   if (l_value > 20 )
      if (l_colKey.toString().matches("(.*)Mon(.*)"))
      {
          mycolor = Color.GREEN;
      } else {
          mycolor = Color.RED;
      }
    }
       return mycolor;
       
   }
}
     
 
     
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
     
    
        
    public IncomeStatementRptYear() {
        initComponents();
    }

    
      public void ChartIt(String element) {
       

                File f = new File(bsmf.MainFrame.temp + "icchart.jpg");
                if(f.exists()) {
                    f.delete();
                }
               
                    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                   
                   int seq = 0;
                    
                   // some summary elements will have a seq of 0....so adjust manually
                   if (element.equals("sales")) {
                       seq = 0;
                   }
                   if (element.equals("cogs")) {
                       seq = 1;
                   }
                   if (element.equals("stdmargin")) {
                       seq = 2;
                   }
                   if (element.equals("mtlvar")) {
                       seq = 3;
                   }
                   if (element.equals("lbrvar")) {
                       seq = 4;
                   }
                   if (element.equals("bdnvar")) {
                       seq = 5;
                   }
                   if (element.equals("mfggrossmargin")) {
                       seq = 6;
                   }
                   if (element.equals("prodeng")) {
                       seq = 7;
                   }
                   if (element.equals("grossmargin")) {
                       seq = 8;
                   }
                   if (element.equals("mktsales")) {
                       seq = 9;
                   }
                   if (element.equals("g&a")) {
                       seq = 10;
                   }
                   if (element.equals("profitbeforealloc")) {
                       seq = 11;
                   }
                   if (element.equals("interest")) {
                       seq = 12;
                   }
                   if (element.equals("alloc")) {
                       seq = 13;
                   }
                   if (element.equals("mgtfee")) {
                       seq = 14;
                   }
                   if (element.equals("bankfee")) {
                       seq = 15;
                   }
                   if (element.equals("other")) {
                       seq = 16;
                   }
                   if (element.equals("operationprofit")) {
                       seq = 17;
                   }
                   if (element.equals("depreciation")) {
                       seq = 18;
                   }
                   if (element.equals("ebitda")) {
                       seq = 19;
                   }
            
                    double doublevalue = 0.00;
        
                       for (int i = 0; i < mymodel.getColumnCount(); i++) {
                           if (i == 0)
                               continue;
                           
                           doublevalue = Double.valueOf(mymodel.getValueAt(seq, i).toString());
                           if (doublevalue < 0) {
                               doublevalue *= -1;
                           }
                              
                        dataset.setValue(doublevalue, element, String.valueOf(i));
                       }
                    JFreeChart chart = ChartFactory.createBarChart(element, "Month", "Dollars", dataset, PlotOrientation.VERTICAL, true, true, false);
                    CategoryItemRenderer renderer = new IncomeStatementRptYear.CustomRenderer();
                    
                    Font font = new Font("Dialog", Font.PLAIN, 30);
                    CategoryPlot p = chart.getCategoryPlot();
                    
                    CategoryAxis axis = p.getDomainAxis();
                     ValueAxis axisv = p.getRangeAxis();
                     axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                     axisv.setVerticalTickLabels(false);
                     
                     p.setRenderer(renderer);
                    try {
                        ChartUtilities.saveChartAsJPEG(new File(bsmf.MainFrame.temp + "/" + "icchart.jpg"), chart, jPanel1.getWidth(), jPanel1.getHeight()/2);
                    } catch (IOException e) {
                        System.err.println("Problem occurred creating chart.");
                    }
                    ImageIcon myicon = new ImageIcon(bsmf.MainFrame.temp + "/" + "icchart.jpg");
                    myicon.getImage().flush();
                    this.chartlabel.setIcon(myicon);
                    
                    this.repaint();

                
           
     }
    
    
    public void initvars(String[] arg) {
        
        
        mymodel.setRowCount(0);
        
        btchart.setEnabled(false);
        
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
       
        
        ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
        }
        
        ddelements.removeAllItems();
        ArrayList elements = OVData.getGLICDefsList();
        for (Object element : elements) {
            ddelements.addItem(element.toString());
        }
        ddelements.insertItemAt("stdmargin", 2);
        ddelements.insertItemAt("mfggrossmargin", 6);
        ddelements.insertItemAt("grossmargin", 8);
        ddelements.insertItemAt("profitbeforealloc", 11);
        ddelements.insertItemAt("operationprofit", 17);
        ddelements.addItem("ebitda");
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();
        ddyear = new javax.swing.JComboBox();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        ddelements = new javax.swing.JComboBox();
        btchart = new javax.swing.JButton();
        chartlabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Income Statement By Year"));

        jLabel2.setText("Year");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "element", "Jan", "Feb", "Mar", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            }
        ));
        jScrollPane1.setViewportView(mytable);

        jLabel4.setText("Site");

        btchart.setText("Chart It");
        btchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btchartActionPerformed(evt);
            }
        });

        chartlabel.setBackground(new java.awt.Color(237, 237, 210));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jLabel4)
                .addGap(12, 12, 12)
                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRun)
                .addGap(93, 93, 93)
                .addComponent(ddelements, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btchart)
                .addGap(0, 433, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
            .addComponent(chartlabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btRun))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddelements, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btchart))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed

        
        btchart.setEnabled(true);
        
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##", new DecimalFormatSymbols(Locale.US));
                int i = 0;

                //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
                    //         new String[]{"Acct", "Description", "Amt"});
                // tablescrap.setModel(mymodel);

                mymodel.setNumRows(0);

                mytable.setModel(mymodel);
                mytable.getColumnModel().getColumn(1).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(11).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                mytable.getColumnModel().getColumn(12).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                double[] sales = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] cogs = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] stdmargin = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] mtlvar = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] lbrvar = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] bdnvar = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] mfggrossmargin = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] prodeng = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] marketingandsales = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] grossmargin = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] generalandadmin = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] profitbeforealloc = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] interest = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] alloc = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] mgtfees = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] bankfees = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] other = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] opprofitbeforetaxes = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] depreciation = {0,0,0,0,0,0,0,0,0,0,0,0};
                double[] ebitda = {0,0,0,0,0,0,0,0,0,0,0,0};
                

                String startacct = "";
                String endacct = "";
                ArrayList excludeaccts;
                ArrayList includeaccts;

                
                
                // Sales
                for (int y = 1; y <= 12; y++) {
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
                        " AND acb_per = " + "'" + y + "'" +
                        ";");

                    while (res.next()) {
                        sales[y - 1] += res.getDouble("sum");
                    }

                    // now lets back out accts excluded
                    excludeaccts = OVData.getGLICAccts("sales", "out");
                    for (int k = 0; k < excludeaccts.size(); k++) {
                        sales[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), sales[y  - 1]);
                    }
                    // now add accts that are included
                    includeaccts = OVData.getGLICAccts("sales", "in");
                    for (int k = 0; k < includeaccts.size(); k++) {
                        sales[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), sales[y - 1]);
                    }

                    sales[y - 1] = (-1 * sales[y - 1]);
                } // y

                mymodel.addRow(new Object[] { "Sales", sales[0], sales[1], sales[2], sales[3], sales[4], sales[5], sales[6], sales[7], sales[8], sales[9], sales[10], sales[11]});

                
                
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    cogs[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("cogs", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    cogs[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), cogs[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("cogs", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    cogs[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), cogs[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Cost Of Goods", cogs[0], cogs[1], cogs[2], cogs[3], cogs[4], cogs[5], cogs[6], cogs[7], cogs[8], cogs[9], cogs[10], cogs[11]});


                // Standard Margin = Sales - Cogs
                for (int y = 1; y <= 12; y++) {
                stdmargin[y -1] = sales[y -1] - cogs[y -1];
                }
                mymodel.addRow(new Object[] { "Standard Margin", stdmargin[0], stdmargin[1], stdmargin[2], stdmargin[3], stdmargin[4], stdmargin[5], stdmargin[6], stdmargin[7], stdmargin[8], stdmargin[9], stdmargin[10], stdmargin[11]});


               
                
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    mtlvar[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("mtlvar", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    mtlvar[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), mtlvar[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("mtlvar", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                   mtlvar[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), mtlvar[y - 1]);
                }
                 } // y
                mymodel.addRow(new Object[] { "Matl Variance", mtlvar[0], mtlvar[1], mtlvar[2], mtlvar[3], mtlvar[4], mtlvar[5], mtlvar[6], mtlvar[7], mtlvar[8], mtlvar[9], mtlvar[10], mtlvar[11]});
                

                // Labor Variance
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    lbrvar[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("lbrvar", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                   lbrvar[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), lbrvar[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("lbrvar", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    lbrvar[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), lbrvar[y - 1]);
                }
                 } // y
                 mymodel.addRow(new Object[] { "Labor Variance", lbrvar[0], lbrvar[1], lbrvar[2], lbrvar[3], lbrvar[4], lbrvar[5], lbrvar[6], lbrvar[7], lbrvar[8], lbrvar[9], lbrvar[10], lbrvar[11]});
               
                
               
                
                
                
                // Burden Variance
                  for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    bdnvar[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("bdnvar", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    bdnvar[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), bdnvar[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("bdnvar", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    bdnvar[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), bdnvar[y - 1]);
                }
                 } // y
               
                mymodel.addRow(new Object[] { "Burden Variance", bdnvar[0], bdnvar[1], bdnvar[2], bdnvar[3], bdnvar[4], bdnvar[5], bdnvar[6], bdnvar[7], bdnvar[8], bdnvar[9], bdnvar[10], bdnvar[11]});
               
                
               
                
                // MFG Gross Margin
                for (int y = 1; y <= 12; y++) {
                mfggrossmargin[y - 1] = stdmargin[y - 1] - mtlvar[y - 1] - lbrvar[y - 1] - bdnvar[y - 1];
                } // y
                mymodel.addRow(new Object[] { "MFG Gross Margin", mfggrossmargin[0], mfggrossmargin[1], mfggrossmargin[2], mfggrossmargin[3], mfggrossmargin[4], mfggrossmargin[5], mfggrossmargin[6], mfggrossmargin[7], mfggrossmargin[8], mfggrossmargin[9], mfggrossmargin[10], mfggrossmargin[11]});

                
                //ProdEng
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    prodeng[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("prodeng", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    prodeng[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), prodeng[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("prodeng", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    prodeng[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), prodeng[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Prod Engineering", prodeng[0], prodeng[1], prodeng[2], prodeng[3], prodeng[4], prodeng[5], prodeng[6], prodeng[7], prodeng[8], prodeng[9], prodeng[10], prodeng[11]});


                // Gross Margin
                for (int y = 1; y <= 12; y++) {
                grossmargin[y - 1] = mfggrossmargin[y - 1] - prodeng[y - 1];
                } // y
                mymodel.addRow(new Object[] { "Gross Margin", grossmargin[0], grossmargin[1], grossmargin[2], grossmargin[3], grossmargin[4], grossmargin[5], grossmargin[6], grossmargin[7], grossmargin[8], grossmargin[9], grossmargin[10], grossmargin[11]});


                //Marketing and sales
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");

                while (res.next()) {
                    marketingandsales[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("mktsales", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    marketingandsales[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), marketingandsales[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("mktsales", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    marketingandsales[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), marketingandsales[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Sales and Marketing", marketingandsales[0], marketingandsales[1], marketingandsales[2], marketingandsales[3], marketingandsales[4], marketingandsales[5], marketingandsales[6], marketingandsales[7], marketingandsales[8], marketingandsales[9], marketingandsales[10], marketingandsales[11]});


                //Gen and Admin
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    generalandadmin[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("g&a", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    generalandadmin[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), generalandadmin[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("g&a", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    generalandadmin[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), generalandadmin[y - 1]);
                }
                } // y
              mymodel.addRow(new Object[] { "General Admin", generalandadmin[0], generalandadmin[1], generalandadmin[2], generalandadmin[3], generalandadmin[4], generalandadmin[5], generalandadmin[6], generalandadmin[7], generalandadmin[8], generalandadmin[9], generalandadmin[10], generalandadmin[11]});


                // Profit Before Allocation
                for (int y = 1; y <= 12; y++) {
                profitbeforealloc[y - 1] = grossmargin[y - 1] - marketingandsales[y - 1] - generalandadmin[y - 1];
                } // y
                mymodel.addRow(new Object[] { "Profit Before Alloc", profitbeforealloc[0], profitbeforealloc[1], profitbeforealloc[2], profitbeforealloc[3], profitbeforealloc[4], profitbeforealloc[5], profitbeforealloc[6], profitbeforealloc[7], profitbeforealloc[8], profitbeforealloc[9], profitbeforealloc[10], profitbeforealloc[11]});


                //Interest
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    interest[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("interest", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    interest[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), interest[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("interest", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    interest[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), interest[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Interest", interest[0], interest[1], interest[2], interest[3], interest[4], interest[5], interest[6], interest[7], interest[8], interest[9], interest[10], interest[11]});


                //Allocations
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    alloc[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("alloc", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    alloc[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), alloc[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("alloc", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    alloc[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), alloc[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Allocations", alloc[0], alloc[1], alloc[2], alloc[3], alloc[4], alloc[5], alloc[6], alloc[7], alloc[8], alloc[9], alloc[10], alloc[11]});


                //Management Fees
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    mgtfees[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("mgtfee", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    mgtfees[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), mgtfees[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("mgtfee", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    mgtfees[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), mgtfees[y - 1]);
                }
                } // y
               mymodel.addRow(new Object[] { "Mgmt Fees", mgtfees[0], mgtfees[1], mgtfees[2], mgtfees[3], mgtfees[4], mgtfees[5], mgtfees[6], mgtfees[7], mgtfees[8], mgtfees[9], mgtfees[10], mgtfees[11]});


                //Bank Fees
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    bankfees[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("bankfee", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    bankfees[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), bankfees[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("bankfee", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    bankfees[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), bankfees[y - 1]);
                }
                } // y
                 mymodel.addRow(new Object[] { "Bank Fees", bankfees[0], bankfees[1], bankfees[2], bankfees[3], bankfees[4], bankfees[5], bankfees[6], bankfees[7], bankfees[8], bankfees[9], bankfees[10], bankfees[11]});


                //Other income/expense
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    other[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("otherie", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    other[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), other[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("otherie", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    other[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), other[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Other Income/Expense", other[0], other[1], other[2], other[3], other[4], other[5], other[6], other[7], other[8], other[9], other[10], other[11]});


                // Operational Profit before taxes
                for (int y = 1; y <= 12; y++) {
                opprofitbeforetaxes[y - 1] = profitbeforealloc[y - 1] -interest[y - 1] - alloc[y - 1] - mgtfees[y - 1] - bankfees[y - 1] - other[y - 1];
                } // y
                mymodel.addRow(new Object[] { "Operation Profit Before Taxes", opprofitbeforetaxes[0], opprofitbeforetaxes[1], opprofitbeforetaxes[2], opprofitbeforetaxes[3], opprofitbeforetaxes[4], opprofitbeforetaxes[5], opprofitbeforetaxes[6], opprofitbeforetaxes[7], opprofitbeforetaxes[8], opprofitbeforetaxes[9], opprofitbeforetaxes[10], opprofitbeforetaxes[11]});


                //depreciation
                for (int y = 1; y <= 12; y++) {
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
                    " AND acb_per = " + "'" + y + "'" +
                    ";");
                while (res.next()) {
                    depreciation[y - 1] += res.getDouble("sum");
                }
                // now lets back out accts excluded
                excludeaccts = OVData.getGLICAccts("depreciation", "out");
                for (int k = 0; k < excludeaccts.size(); k++) {
                    depreciation[y - 1] = OVData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), depreciation[y - 1]);
                }
                // now add accts that are included
                includeaccts = OVData.getGLICAccts("depreciation", "in");
                for (int k = 0; k < includeaccts.size(); k++) {
                    depreciation[y - 1] = OVData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), String.valueOf(y), depreciation[y - 1]);
                }
                } // y
                mymodel.addRow(new Object[] { "Depreciation", depreciation[0], depreciation[1], depreciation[2], depreciation[3], depreciation[4], depreciation[5], depreciation[6], depreciation[7], depreciation[8], depreciation[9], depreciation[10], depreciation[11]});



                // EBITDA
               for (int y = 1; y <= 12; y++) {
                ebitda[y - 1] = opprofitbeforetaxes[y - 1] + depreciation[y - 1] + interest[y - 1] + alloc[y - 1] + mgtfees[y - 1] + bankfees[y - 1] + other[y - 1];
                } // y
                mymodel.addRow(new Object[] { "EBITDA", ebitda[0], ebitda[1], ebitda[2], ebitda[3], ebitda[4], ebitda[5], ebitda[6], ebitda[7], ebitda[8], ebitda[9], ebitda[10], ebitda[11]});


             
            ChartIt("sales");
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot execute sql query for Income Statement Report");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }//GEN-LAST:event_btRunActionPerformed

    private void btchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btchartActionPerformed
       if (ddelements.getItemCount() > 0)
        ChartIt(ddelements.getSelectedItem().toString());
    }//GEN-LAST:event_btchartActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btchart;
    private javax.swing.JLabel chartlabel;
    private javax.swing.JComboBox ddelements;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable mytable;
    // End of variables declaration//GEN-END:variables
}
