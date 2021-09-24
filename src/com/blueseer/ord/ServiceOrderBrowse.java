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
import com.blueseer.fgl.*;
import com.blueseer.shp.*;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class ServiceOrderBrowse extends javax.swing.JPanel {
 
    String quotesfilepath = OVData.getSystemTempDirectory() + "/" + "chartquotes.jpg";
    String ordersfilepath = OVData.getSystemTempDirectory() + "/" + "chartorders.jpg";
    Double quotes = 0.00;
    Double orders = 0.00;
    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("select"), 
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("code"), 
                            getGlobalColumnTag("name"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("orderdate"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("total"), 
                            getGlobalColumnTag("print")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  || col == 1 || col == 10 )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("price")});
    
   
    
    
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
    
     
     class SomeRenderer extends DefaultTableCellRenderer {
         
       public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
     
        
              if (isSelected)
        {
            setBackground(Color.white);
            setForeground(Color.BLACK);
           
        }
        
            String trantype = tablereport.getModel().getValueAt(table.convertRowIndexToModel(row), 4).toString();
            if ( column == 4 && trantype.equals("order") ) {
           // c.setBackground(Color.green);
            c.setForeground(Color.blue);
            }
            else if ( column == 4 && trantype.equals("quote") ) {
           // c.setBackground(Color.blue);
            c.setForeground(Color.red);
            }
            else {
                c.setBackground(table.getBackground());
            }
            
      
        
           
            
            return c;
    }
    }
    
      public void chartQuotes() {
          
          quotes = 0.00;
          
         try {
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                 
                  
                res = st.executeQuery("select sv_cust, cm_name, sum(svd_netprice * svd_qty) as 'sum' from svd_det " +
                        " inner join sv_mstr on sv_nbr = svd_nbr  " +
                        " inner join cm_mstr on cm_code = sv_cust  " +
                        " where sv_create_date >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND sv_create_date <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND sv_type = 'quote' AND sv_status <> 'void' " +
                        " group by sv_cust, cm_name order by sv_cust desc   ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                      acct = res.getString("cm_name");  
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                    
                    quotes += amt;
                    
                    if (amt > 0) {
                       dataset.setValue(acct, amt);
                    }
                }
        JFreeChart chart = ChartFactory.createPieChart("Total Quotes For Date Range", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00", new DecimalFormatSymbols(Locale.US)), new DecimalFormat("0%", new DecimalFormatSymbols(Locale.US)));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(quotesfilepath), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.7));
       // ChartUtilities.saveChartAsJPEG(new File(exoincfilepath), chart, 400, 200);
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(quotesfilepath);
        myicon.getImage().flush();  
      //  myicon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH);
        this.chartlabel.setIcon(myicon);
        this.repaint();
            
              } catch (SQLException s) {
                  MainFrame.bslog(s);
                  bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
       public void chartOrders() {
         try {
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                 
                  
                res = st.executeQuery("select sv_cust, cm_name, sum(svd_netprice * svd_qty) as 'sum' from svd_det " +
                        " inner join sv_mstr on sv_nbr = svd_nbr  " +
                        " inner join cm_mstr on cm_code = sv_cust  " +
                        " where sv_create_date >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND sv_create_date <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND sv_type = 'order' AND sv_status <> 'void' " +
                        " group by sv_cust, cm_name order by sv_cust desc   ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                      acct = res.getString("cm_name");  
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                    
                    quotes += amt;
                    
                    if (amt > 0) {
                       dataset.setValue(acct, amt);
                    }
                }
        JFreeChart chart = ChartFactory.createPieChart("Total Orders For Date Range", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00", new DecimalFormatSymbols(Locale.US)), new DecimalFormat("0%", new DecimalFormatSymbols(Locale.US)));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(ordersfilepath), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.7));
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(ordersfilepath);
        myicon.getImage().flush();   
        this.pielabel.setIcon(myicon);
        this.repaint();
       
      
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
                  bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
     
     
   
    /**
     * Creates new form ScrapReportPanel
     */
    public ServiceOrderBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void getdetail(String order) {
      
         modeldetail.setNumRows(0);
         double totalsales = 0.00;
         double totalqty = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                res = st.executeQuery("select svd_nbr, svd_item, svd_qty, svd_netprice from svd_det " +
                        " where svd_nbr = " + "'" + order + "'" +  ";");
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      res.getString("svd_nbr"), 
                      res.getString("svd_item"),
                      res.getString("svd_qty"),
                      res.getString("svd_netprice")});
                }
               
             
               
                tabledetail.setModel(modeldetail);
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

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
       
        tbtotorders.setText("0");
        tbtotquotes.setText("0");
       
        cbchart.setSelected(false);
        
        java.util.Date now = new java.util.Date();
       
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
        dcfrom.setDate(firstday);
        dcto.setDate(now);
               
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        // tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
       //  tablereport.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(7).setMaxWidth(100);
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
       
      
                    
                    
                    
       
        
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        chartpanel.setVisible(false);
          
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
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        chartpanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        pielabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        tbcsv = new javax.swing.JButton();
        cbchart = new javax.swing.JCheckBox();
        ddcustfrom = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        ddcustto = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbtotorders = new javax.swing.JLabel();
        tbtotquotes = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Service Order Browse"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new java.awt.BorderLayout());

        chartlabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chartlabel.setMinimumSize(new java.awt.Dimension(50, 50));
        chartpanel.add(chartlabel, java.awt.BorderLayout.NORTH);

        pielabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pielabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        chartpanel.add(pielabel, java.awt.BorderLayout.SOUTH);

        tablepanel.add(chartpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To Date:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        cbchart.setText("Charts");
        cbchart.setName("cbcharts"); // NOI18N
        cbchart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbchartActionPerformed(evt);
            }
        });

        jLabel7.setText("From Cust:");
        jLabel7.setName("lblfromcust"); // NOI18N

        jLabel9.setText("To Cust:");
        jLabel9.setName("lbltocust"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdetail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbchart)
                        .addGap(57, 57, 57))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(tbcsv)
                        .addComponent(cbchart)
                        .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(40, 40, 40))
        );

        jLabel8.setText("Total Orders:");
        jLabel8.setName("lbltotalorders"); // NOI18N

        tbtotorders.setText("0");

        tbtotquotes.setText("0");

        jLabel11.setText("Total Quotes:");
        jLabel11.setName("lbltotalquotes"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 194, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbtotorders, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(tbtotquotes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotquotes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotorders, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablepanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE))
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

                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String fromdate = "";
                String todate = "";
               mymodel.setNumRows(0);
                 
              
                
                 double totsales = 0.00;
                 double totquotes = 0.00;
                 
                 String trantype = "";
                 String status = "";
                 
            
                  Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (    tc.getModelIndex() == 0 ||
                             tc.getModelIndex() == 1 ||
                             tc.getModelIndex() == 10 ) {
                         continue;
                     }
                     tc.setCellRenderer(new ServiceOrderBrowse.SomeRenderer());
                 }   
               
                
             
                 if (dcfrom.getDate() == null) {
                     fromdate = bsmf.MainFrame.lowdate;
                 } else {
                     fromdate = dfdate.format(dcfrom.getDate());
                 }
                 if (dcto.getDate() == null) {
                     todate = bsmf.MainFrame.hidate;
                 } else {
                    todate = dfdate.format(dcto.getDate()); 
                 }
                  
                 
                
                      
                  // now lets get the pos_mstr records    
                  res = st.executeQuery("select sv_nbr, sv_cust, sv_ship, sv_type, sv_status, sv_create_date, sv_due_date, sv_issched, sum(svd_qty * svd_netprice) as 'price' from sv_mstr " +
                        " inner join svd_det on svd_nbr = sv_nbr " +
                        " where sv_create_date >= " + "'" + fromdate + "'" + 
                        " and sv_create_date <= " + "'" + todate + "'" +
                        " group by sv_nbr, sv_cust, sv_ship, sv_type, sv_status, sv_create_date, sv_due_date, sv_issched " +
                        " order by sv_nbr desc;");
                
                
                       while (res.next()) {
                          
                        
                         trantype = res.getString("sv_type");
                         status = res.getString("sv_status");
                         
                         
                                                 
                         if (trantype.equals("order") && ! status.equals("void")) {
                             totsales = totsales + res.getDouble("price");
                         }
                         if (trantype.equals("quote") && ! status.equals("void")) {
                             totquotes = totquotes + res.getDouble("price");
                         }
                         mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, BlueSeerUtils.clickbasket, 
                               res.getString("sv_nbr"),
                                res.getString("sv_cust"),
                                res.getString("sv_ship"),
                                res.getString("sv_type"),
                                res.getString("sv_status"),
                                res.getString("sv_create_date"),
                                res.getString("sv_due_date"),
                                df.format(res.getDouble("price")),
                                BlueSeerUtils.clickprint 
                            });
                       }
              
                       
                chartQuotes();
                chartOrders();       
                       
                tbtotorders.setText(df.format(totsales));
                tbtotquotes.setText(df.format(totquotes));
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if (col == 0) {
            reinitpanels("ServiceOrderMaint", true, new String[]{tablereport.getValueAt(row, 2).toString()});
        }
        if ( col == 1) {
                getdetail(tablereport.getValueAt(row, 2).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        
        
        
        /*
        if ( col == 0 && tablereport.getValueAt(row, 4).toString().equals("sell") ) {
                String mypanel = "MenuShipMaint";
               if (! checkperms(mypanel)) { return; }
               String args = tablereport.getValueAt(row, 3).toString();
               reinitpanels(mypanel, true, args);
        }
        if ( col == 0 && tablereport.getValueAt(row, 4).toString().equals("buy") ) {
                String mypanel = "ReceiverMaintMenu";
               if (! checkperms(mypanel)) { return; }
               String args = tablereport.getValueAt(row, 3).toString();
               reinitpanels(mypanel, true, args);
        }
*/
        if ( col == 10 ) {
              OVData.printServiceOrder(tablereport.getValueAt(row, 2).toString());
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
      if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void cbchartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbchartActionPerformed
        if (cbchart.isSelected()) {
            chartpanel.setVisible(true);
        } else {
            chartpanel.setVisible(false);
        }
    }//GEN-LAST:event_cbchartActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JCheckBox cbchart;
    private javax.swing.JLabel chartlabel;
    private javax.swing.JPanel chartpanel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddcustfrom;
    private javax.swing.JComboBox<String> ddcustto;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel pielabel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbcsv;
    private javax.swing.JLabel tbtotorders;
    private javax.swing.JLabel tbtotquotes;
    // End of variables declaration//GEN-END:variables
}
