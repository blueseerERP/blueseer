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
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.prd.ProdSchedPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
public class ReconAccount extends javax.swing.JPanel {
 
    String exoincfilepath = OVData.getSystemTempDirectory() + "/" + "chartexpinc.jpg";
    String buysellfilepath = OVData.getSystemTempDirectory() + "/" + "chartbuysell.jpg";
    Double expenses = 0.00;
    Double inventory = 0.00;
    boolean isLoad = false;
    
    ReconAccount.MyTableModel mymodel = new ReconAccount.MyTableModel(new Object[][]{},
                        new String[]{"ID", "Acct", "CC", "Site", "Ref", "Type", "EffDate", "Desc", "Amount", "CheckBox", "Status"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 9) {
                            return Boolean.class;
                        } else { 
                            return String.class;  //other columns accept String values  
                        }
                      }
            };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Shipper/Receiver", "Item", "Desc", "Ref", "Qty", "NetPrice"});
    
   
    
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // plan is closed
            if (tablereport.getModel().getValueAt(rowIndex, 10).equals("cleared")) {   // 1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false};  
            } else {
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, true, false};  
            }
            
            return canEdit[columnIndex];
        }
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
        
        public Class getColumnClass(int column) {
            
            
               if (column == 8)       
                return Double.class; 
               else if (column == 9) 
                   return Boolean.class;
            else return String.class;  //other columns accept String values 
            
       /*     
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }  
              
        return Object.class;
*/
               }
       
        
        
   }    
    
     
    
    
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
    
     
      public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
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
        
            String trantype = tablereport.getModel().getValueAt(table.convertRowIndexToModel(row), 10).toString();
            if ( column == 10 && trantype.equals("cleared") ) {
            c.setForeground(Color.blue);
            } else {
            c.setForeground(table.getForeground());
            }
            
            return c;
    }
    }
    
      public void chartExp() {
          
          expenses = 0.00;
          
         try {
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                 
                  
                res = st.executeQuery("select posd_acct, ac_desc, sum(posd_netprice * posd_qty) as 'sum' from pos_det " +
                        " inner join pos_mstr on pos_nbr = posd_nbr  " +
                        " inner join ac_mstr on ac_id = posd_acct  " +
                        " where pos_entrydate >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND pos_entrydate <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND pos_type = 'expense' " +
                        " AND pos_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +       
                        " group by posd_acct order by posd_acct desc   ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                      acct = res.getString("ac_desc");  
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                    
                    expenses += amt;
                    
                    if (amt > 0) {
                       dataset.setValue(acct, amt);
                    }
                }
        JFreeChart chart = ChartFactory.createPieChart("Expenses For Date Range", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(exoincfilepath), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.7));
       // ChartUtilities.saveChartAsJPEG(new File(exoincfilepath), chart, 400, 200);
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(exoincfilepath);
        myicon.getImage().flush();  
      //  myicon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH);
        this.chartlabel.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
                  bsmf.MainFrame.show("cannot get pos_det");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
       public void chartBuyAndSell() {
         try {
          
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            
            try {
                Statement st = con.createStatement();
                ResultSet res = null;
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");       
                 
                  
                res = st.executeQuery("select pos_type, sum(pos_totamt) as 'sum' from pos_mstr  " +
                        " where pos_entrydate >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND pos_entrydate <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND pos_type <> 'expense' " +
                         " AND pos_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +               
                        " group by pos_type order by pos_type desc   ;");
             
                DefaultPieDataset dataset = new DefaultPieDataset();
               
                String acct = "";
                while (res.next()) {
                      acct = res.getString("pos_type");
                      if (acct.equals("income")) {
                          acct = "misc income";
                      }
                    Double amt = res.getDouble("sum");
                    if (amt < 0) {amt = amt * -1;}
                  dataset.setValue(acct, amt);
                }
        JFreeChart chart = ChartFactory.createPieChart("Buy / Sell / Misc Income For Date Range", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
      //  plot.setSectionPaint(KEY1, Color.green);
      //  plot.setSectionPaint(KEY2, Color.red);
     //   plot.setExplodePercent(KEY1, 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", new DecimalFormat("$ #,##0.00"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);

        try {
        
        ChartUtilities.saveChartAsJPEG(new File(buysellfilepath), chart, (int) (this.getWidth()/2.5), (int) (this.getHeight()/2.7));
        } catch (IOException e) {
            MainFrame.bslog(e);
        }
        ImageIcon myicon = new ImageIcon(buysellfilepath);
        myicon.getImage().flush();   
        this.pielabel.setIcon(myicon);
        this.repaint();
       
       // bsmf.MainFrame.show("your chart is complete...go to chartview");
                
              } catch (SQLException s) {
                  MainFrame.bslog(s);
                  bsmf.MainFrame.show("cannot get pos_det");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
       
     
     
   
    /**
     * Creates new form ScrapReportPanel
     */
    public ReconAccount() {
        initComponents();
    }

    public void getdetail(String shipper) {
      
         modeldetail.setNumRows(0);
         double totalsales = 0.00;
         double totalqty = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                res = st.executeQuery("select posd_nbr, posd_item, posd_desc, posd_ref, posd_qty, posd_netprice from pos_det " +
                        " where posd_nbr = " + "'" + shipper + "'" +  ";");
                while (res.next()) {
                    totalsales = totalsales + (res.getDouble("posd_qty") * res.getDouble("posd_netprice"));
                    totalqty = totalqty + res.getDouble("posd_qty");
                   modeldetail.addRow(new Object[]{ 
                      res.getString("posd_nbr"), 
                      res.getString("posd_item"),
                      res.getString("posd_desc"),
                      res.getString("posd_ref"),
                      res.getString("posd_qty"),
                      res.getString("posd_netprice")});
                }
               
             
               
                tabledetail.setModel(modeldetail);
                this.repaint();

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to get browse detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public Double sumtoggle () {
        double x = 0.00;
        DecimalFormat df = new DecimalFormat("#0.00");
         for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
              if (mymodel.getValueAt(i, 10).toString().equals("open") && (boolean) mymodel.getValueAt(i, 9)) {
                     x += Double.valueOf(mymodel.getValueAt(i, 8).toString());
                 }
         }
         
        lbselecttotal.setText(df.format(x));
       
        return x;
    }
    
    public void sumAll() {
         
    }
    
     public void updateRecon() {
     
     try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            try {
                
              double x = 0.00;
              DecimalFormat df = new DecimalFormat("#0.00");
              for (int i = 0 ; i < mymodel.getRowCount(); i++) {    
                 if ( (boolean) mymodel.getValueAt(i, 9) ) {
                     st.executeUpdate("update gl_hist set glh_recon = " + "'" + '1' + "'" 
                            + " where glh_id = " + "'" + mymodel.getValueAt(i, 0).toString() + "'"                             
                            + ";");
                 }
              }
             
              bsmf.MainFrame.show("reconciliation complete...transactions updated");
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (bsmf.MainFrame.con != null) bsmf.MainFrame.con.close();
            }
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     
     }
   
    
    
    public void initvars(String[] arg) {
        lbdiff.setText("0");
        lbstatementbal.setText("0");
        lbselecttotal.setText("0");
        expenses = 0.00;
        
        
        java.util.Date now = new java.util.Date();
       
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
        dcfrom.setDate(firstday);
        dcto.setDate(now);
               
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        
        tablereport.getModel().addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent e) {
             if (e.getColumn() == 9 && ! isLoad) {
                 //String value = ((DefaultTableModel) e.getSource()).getValueAt(e.getFirstRow(), e.getColumn()).toString();
                 // bsmf.MainFrame.show(value);
                 sumtoggle();
             }
         // your code goes here, whatever you want to do when something changes in the table
         }
         });
        
        
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        
        
          CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
                tablereport.getColumnModel().getColumn(9).setCellRenderer(checkBoxRenderer);  
        // tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         
       //  tablereport.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(9).setMaxWidth(100);
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
        ArrayList<String> myacct = OVData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacct.addItem(myacct.get(i));
        }
            ddacct.setSelectedIndex(0);
                    
                    
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
                    
                    
                    
       
        
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
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        ddsite = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        tbendbalance = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        cbtoggle = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lbstatementbal = new javax.swing.JLabel();
        lbdiff = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbselecttotal = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbacctbal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Reconciliation"));

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

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");

        jLabel6.setText("To Date:");

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("Site:");

        jLabel4.setText("Acct:");

        tbendbalance.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbendbalanceFocusLost(evt);
            }
        });

        jLabel1.setText("Statement Balance:");

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        cbtoggle.setText("Toggle All?");
        cbtoggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbtoggleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13)
                            .addComponent(jLabel2))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4)))
                    .addComponent(tbendbalance, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbtoggle)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btRun)
                    .addComponent(btcommit))
                .addGap(289, 289, 289))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbendbalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btcommit)
                    .addComponent(cbtoggle))
                .addContainerGap())
        );

        jLabel8.setText("Statement Balance:");

        lbstatementbal.setText("0");

        lbdiff.setText("0");

        jLabel10.setText("Difference:");

        lbselecttotal.setText("0");

        jLabel11.setText("Selected (open) Total:");

        lbacctbal.setText("0");

        jLabel3.setText("GL Account Balance:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(53, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbstatementbal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbdiff, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(lbacctbal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbselecttotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbacctbal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbstatementbal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbselecttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbdiff, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(23, 23, 23))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
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
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
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

        isLoad = true;
    
try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                DecimalFormat df = new DecimalFormat("#0.00");
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String fromdate = "";
                String todate = "";
               mymodel.setNumRows(0);
                 
              
                double total = 0.00;
                double stendbal = 0.00;
                if (! tbendbalance.getText().isEmpty()) {
                    stendbal = Double.valueOf(tbendbalance.getText());
                }
                 
                  Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if ( tc.getModelIndex() == 9 ) {
                         continue;
                     }
                     tc.setCellRenderer(new ReconAccount.SomeRenderer());
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
                 boolean toggle = false;
                 String status = "";
                 res = st.executeQuery("select glh_id, glh_acct, glh_cc, glh_site, glh_type, glh_ref, glh_doc, glh_effdate, glh_desc, glh_amt, glh_recon from gl_hist " +
                        " where glh_acct = " + "'" + ddacct.getSelectedItem().toString() + "'" + " AND " + 
                        " glh_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " glh_effdate >= " + "'" + fromdate + "'" + " AND " +
                        " glh_effdate <= " + "'" + todate + "'" + ";");
                while (res.next()) {
                    total = total + res.getDouble("glh_amt");
                    toggle = res.getBoolean("glh_recon");
                    if (toggle) {
                        status = "cleared";
                    } else {
                        status = "open";
                    }
                   mymodel.addRow(new Object[]{ 
                      res.getString("glh_id"),
                      res.getString("glh_acct"), 
                      res.getString("glh_cc"),
                      res.getString("glh_site"),
                      res.getString("glh_ref"), 
                      res.getString("glh_type"), 
                      res.getString("glh_effdate"),
                      res.getString("glh_desc"),
                      Double.valueOf(df.format(res.getDouble("glh_amt"))),
                      toggle, status });
                }
                
              // tbtoggletotal.setText(df.format(total));
                lbstatementbal.setText(df.format(stendbal));
                lbdiff.setText(df.format(stendbal - total));
                lbacctbal.setText(df.format(OVData.getGLAcctBalAsOfDate(ddsite.getSelectedItem().toString(), ddacct.getSelectedItem().toString(), todate)));
                       
                chartBuyAndSell();
                chartExp();  
                
                isLoad = false;
                       
                
                
             
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem executing Recon Report");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
      /* 
        if ( col == 9) {
                getdetail(tablereport.getValueAt(row, 1).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
                if ( (boolean) mymodel.getValueAt(row, col) ) {
                   //  x += Double.valueOf(mymodel.getValueAt(i, 8).toString());
                 }
        }
       
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

        if ( col == 9 && tablereport.getValueAt(row, 3).toString().equals("sell")) {
            //  OVData.printReceipt(tablereport.getValueAt(row, 2).toString());
        }
*/
    }//GEN-LAST:event_tablereportMouseClicked

    private void cbtoggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbtoggleActionPerformed
         for (int i = 0 ; i < mymodel.getRowCount(); i++) {  
            if (mymodel.getValueAt(i, 10).toString().equals("open")) {
                mymodel.setValueAt(cbtoggle.isSelected(), i, 9);
            } 
        }
         sumtoggle();
         
    }//GEN-LAST:event_cbtoggleActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        DecimalFormat df = new DecimalFormat("#0.00");
        if (lbstatementbal.getText().isEmpty() || Double.valueOf(lbstatementbal.getText()) == 0.00) {
            bsmf.MainFrame.show("Statement Balance is zero");
            return;
        }
        
        
        if (Double.valueOf(lbdiff.getText()) == 0.00) {
             updateRecon();
        } else {
            bsmf.MainFrame.show("Total selected amount does not balance");
        }
       
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbendbalanceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbendbalanceFocusLost
            String x = BlueSeerUtils.bsformat("", tbendbalance.getText(), "2");
        if (x.equals("error")) {
            tbendbalance.setText("");
            tbendbalance.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            lbstatementbal.setText("0.00");
            tbendbalance.requestFocus();
        } else {
            tbendbalance.setText(x);
            tbendbalance.setBackground(Color.white);
            lbstatementbal.setText(tbendbalance.getText());
        }
       
    }//GEN-LAST:event_tbendbalanceFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcommit;
    private javax.swing.JCheckBox cbtoggle;
    private javax.swing.JLabel chartlabel;
    private javax.swing.JPanel chartpanel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddacct;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbacctbal;
    private javax.swing.JLabel lbdiff;
    private javax.swing.JLabel lbselecttotal;
    private javax.swing.JLabel lbstatementbal;
    private javax.swing.JLabel pielabel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbendbalance;
    // End of variables declaration//GEN-END:variables
}
