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

package com.blueseer.far;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.util.HashMap;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class ARAgingView extends javax.swing.JPanel {
    
    String selectedCustomer = "";
 
     MyTableModel modelsummary = new ARAgingView.MyTableModel(new Object[][]{},
                        new String[]{"Detail", "Cust", "0DaysOld", "30DaysOld", "60DaysOld", "90DaysOld", "90+DaysOld"})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    
    MyTableModel2 modeldetail = new ARAgingView.MyTableModel2(new Object[][]{},
                        new String[]{"ID", "Desc", "Type", "EffDate", "DueDate", "0DaysOld", "30DaysOld", "60DaysOld", "90DaysOld", "90+DaysOld"});
   
    
    MyTableModel3 modelpayment = new ARAgingView.MyTableModel3(new Object[][]{},
                        new String[]{"ID", "InvNbr", "EffDate", "DueDate", "Type", "CheckNbr", "INAmt", "CKAmt"});
    
    /**
     * Creates new form ScrapReportPanel
     */
    
    
    
    class MyTableModel3 extends DefaultTableModel {  
      
        public MyTableModel3(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 6 || col == 7 )       
                return Double.class;  
            else return String.class;  //other columns accept String values  
             
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }   
    
     class MyTableModel2 extends DefaultTableModel {  
      
        public MyTableModel2(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 5 || col == 6 || col == 7 || col == 8 || col == 9)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
             
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }   
                        
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 2 || col == 3 || col == 4 || col == 5 || col == 6)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
           
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 5);  // 7 = status column
        
         if ("0".equals(status) || status.isEmpty()) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
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
    
        
    public ARAgingView() {
        initComponents();
    }

    
      public void getdetail(String cust) {
      
         modeldetail.setNumRows(0);
         modelpayment.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        
          tabledetail.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tabledetail.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tabledetail.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tabledetail.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tabledetail.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                double dol = 0.00;
                int qty = 0;
                
                
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " case when ar_duedate > date() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +
                         " order by ar_cust, ar_nbr ;"); 
                 } else {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " case when ar_duedate > curdate() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +
                         " order by ar_cust, ar_nbr ;");     
                 }
                 
                /*
                if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " sum(case when ar_duedate > date() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +
                         " order by ar_cust, ar_nbr ;"); 
                 } else {
                 res = st.executeQuery("SELECT ar_cust, ar_rmks, ar_type, ar_nbr, ar_effdate, ar_duedate, " +
                        " sum(case when ar_duedate > curdate() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + cust + "'" + 
                        " AND ar_status = 'o' " +
                         " order by ar_cust, ar_nbr ;");     
                 }
                */
                
                
                 
                while (res.next()) {
                    dol = dol + (res.getDouble("0") + res.getDouble("30") + res.getDouble("60") + res.getDouble("90") + res.getDouble("90p") );
                    qty = qty + 0;
                    i++;
                        modeldetail.addRow(new Object[]{
                            res.getString("ar_nbr"),
                            res.getString("ar_rmks"),
                            res.getString("ar_type"),
                            res.getString("ar_effdate"),
                            res.getString("ar_duedate"),
                                Double.valueOf(df.format(res.getDouble("0"))),
                                Double.valueOf(df.format(res.getDouble("30"))),
                                Double.valueOf(df.format(res.getDouble("60"))),
                                Double.valueOf(df.format(res.getDouble("90"))),
                                Double.valueOf(df.format(res.getDouble("90p")))
                            });
                   }
               
               
                
                tabledetail.setModel(modeldetail);
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get Detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
        public void getpayment(String cust) {
      
         modelpayment.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        
          tablepayment.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tablepayment.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                double dol = 0.00;
                int qty = 0;
                
                 res = st.executeQuery("SELECT a.ar_cust, b.ar_duedate as 'b.ar_duedate', a.ar_nbr, a.ar_ref, ard_ref, a.ar_type, a.ar_effdate, a.ar_amt, ard_amt " +
                        " FROM  ar_mstr a " +
                        " inner join ard_mstr on ard_id = a.ar_nbr " +
                        " inner join ar_mstr b on b.ar_nbr = ard_ref and b.ar_type = 'I' " +
                        " where a.ar_cust = " + "'" + cust + "'" + 
                        " AND a.ar_type = 'P' " +
                        " AND a.ar_effdate >= date() - date(date(), '-90 day') " +
                         " order by a.ar_effdate desc ;");        
                 
                String ponbr = ""; 
                while (res.next()) {
                    dol = dol + (res.getDouble("ar_amt"));
                    qty = qty + 0;
                    i++;
                   
                        modelpayment.addRow(new Object[]{
                            res.getString("ar_nbr"),
                            res.getString("ard_ref"),
                            res.getString("ar_effdate"),
                            res.getString("b.ar_duedate"),
                            res.getString("ar_type"),
                            res.getString("ar_ref"),
                            Double.valueOf(df.format(res.getDouble("ard_amt"))),
                            Double.valueOf(df.format(res.getDouble("ar_amt")))
                            });
                   }
               
               
                
                tablepayment.setModel(modelpayment);
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get payment");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
      
      
    public void initvars(String[] arg) {
        modelsummary.setRowCount(0);
         java.util.Date now = new java.util.Date();
       
         
          modelsummary.setNumRows(0);
        modeldetail.setNumRows(0);
        tablesummary.setModel(modelsummary);
        tabledetail.setModel(modeldetail);
          tablepayment.setModel(modelpayment);
          
     //    tablesummary.getColumnModel().getColumn(0).setCellRenderer(new ARAgingView.ButtonRenderer());
         tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
         
         tablesummary.getTableHeader().setReorderingAllowed(false);
         tabledetail.getTableHeader().setReorderingAllowed(false);
         tablepayment.getTableHeader().setReorderingAllowed(false);
         
         detailpanel.setVisible(false);
         btdetail.setEnabled(false);
         btexport.setEnabled(false);
         btcsv.setEnabled(false);
         btpdf.setEnabled(false);
         
         cbpaymentpanel.setEnabled(false);
         cbpaymentpanel.setSelected(false);
         paymentpanel.setVisible(false);
         
        ddfromcust.removeAllItems();
        ddtocust.removeAllItems();
        ArrayList mycusts = OVData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddfromcust.addItem(mycusts.get(i));
        }
        for (int i = 0; i < mycusts.size(); i++) {
            ddtocust.addItem(mycusts.get(i));
        } 
        ddtocust.setSelectedIndex(ddtocust.getItemCount() - 1);
         
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
        jPanel2 = new javax.swing.JPanel();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labeldollar = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btdetail = new javax.swing.JButton();
        ddfromcust = new javax.swing.JComboBox();
        ddtocust = new javax.swing.JComboBox();
        cbpaymentpanel = new javax.swing.JCheckBox();
        btexport = new javax.swing.JButton();
        btcsv = new javax.swing.JButton();
        btpdf = new javax.swing.JButton();
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablesummary = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        paymentpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablepayment = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        labelcount.setText("0");

        jLabel7.setText("Count");

        jLabel8.setText("$");

        labeldollar.setText("0");

        jLabel3.setText("To Cust");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel2.setText("From Cust");

        btdetail.setText("Hide Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        cbpaymentpanel.setText("Payments");
        cbpaymentpanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbpaymentpanelActionPerformed(evt);
            }
        });

        btexport.setText("Export Detail");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        btcsv.setText("CSV");
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        btpdf.setText("PDF");
        btpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpdfActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddfromcust, 0, 169, Short.MAX_VALUE)
                    .addComponent(ddtocust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btexport)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdetail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbpaymentpanel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btpdf)))
                .addGap(223, 223, 223))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(cbpaymentpanel)
                        .addComponent(btcsv)
                        .addComponent(btpdf))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btexport))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labeldollar, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(labelcount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablesummary.setAutoCreateRowSorter(true);
        tablesummary.setModel(new javax.swing.table.DefaultTableModel(
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
        tablesummary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablesummaryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablesummary);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setAutoCreateRowSorter(true);
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

        paymentpanel.setLayout(new java.awt.BorderLayout());

        tablepayment.setAutoCreateRowSorter(true);
        tablepayment.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tablepayment);

        paymentpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(paymentpanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1467, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(613, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(106, 106, 106)
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)))
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

        modelsummary.setNumRows(0);
    
try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
            
                
                
                int qty = 0;
                
                DecimalFormat df = new DecimalFormat("#0.00");
                int i = 0;
               
                tablesummary.setModel(modelsummary);
                 
             //   Enumeration<TableColumn> en = tablelabel.getColumnModel().getColumns();
              //   while (en.hasMoreElements()) {
             //        TableColumn tc = en.nextElement();
             //        tc.setCellRenderer(new LabelBrowsePanel.SomeRenderer());
             //    }
              //    tablesummary.getColumnModel().getColumn(0).setCellRenderer(new ARAgingView.ButtonRenderer());
                  tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
                  tablesummary.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                  tablesummary.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                  tablesummary.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                  tablesummary.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                  tablesummary.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                // TableColumnModel tcm = tablescrap.getColumnModel();
               // tcm.getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

              
                double dol = 0;
                String fromcust = "";
                String tocust = "";
                String fromcode = "";
                String tocode = "";
                
                if (ddfromcust.getSelectedItem().toString().isEmpty()) {
                    fromcust = bsmf.MainFrame.lowchar;
                } else {
                    fromcust = ddfromcust.getSelectedItem().toString();
                }
                 if (ddtocust.getSelectedItem().toString().isEmpty()) {
                    tocust = bsmf.MainFrame.hichar;
                } else {
                    tocust = ddtocust.getSelectedItem().toString();
                }
                 
                 
                   ArrayList custs = OVData.getcustmstrlistBetween(fromcust, tocust);
                 
                 for (int j = 0; j < custs.size(); j++) {
                 
                 // init for new cust
                 i = 0;
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                     res = st.executeQuery("SELECT ar_cust, " +
                        " sum(case when ar_duedate > date() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                        " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                         " group by ar_cust order by ar_cust;");
                 }  else {
                 res = st.executeQuery("SELECT ar_cust, " +
                        " sum(case when ar_duedate > curdate() then ar_open_amt else 0 end) as '0', " +
                        " sum(case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end) as '30', " +
                        " sum(case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end) as '60', " +
                        " sum(case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end) as '90', " +
                        " sum(case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end) as '90p' " +
                        " FROM  ar_mstr " +
                         " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                         " group by ar_cust order by ar_cust;");
                 }
                  while (res.next()) {
                    dol = dol + (res.getDouble("0") + res.getDouble("30") + res.getDouble("60") + res.getDouble("90") + res.getDouble("90p") );
                    qty = qty + 0;
                    i++;
                        modelsummary.addRow(new Object[]{
                                BlueSeerUtils.clickbasket,
                                res.getString("ar_cust"),
                                Double.valueOf(df.format(res.getDouble("0"))),
                                Double.valueOf(df.format(res.getDouble("30"))),
                                Double.valueOf(df.format(res.getDouble("60"))),
                                Double.valueOf(df.format(res.getDouble("90"))),
                                Double.valueOf(df.format(res.getDouble("90p")))
                            });
                }
                 
                   if (i == 0) {
                      // create record with zero fields
                       modelsummary.addRow(new Object[]{
                                BlueSeerUtils.clickbasket,
                                custs.get(j),
                                0,0,0,0,0
                            });
                  }
                  
                  
                  
             } // for each customer in range
                 
                labelcount.setText(String.valueOf(i));
                labeldollar.setText(String.valueOf(df.format(dol)));
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot execute sql query for AR Aging View");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void tablesummaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablesummaryMouseClicked

        int row = tablesummary.rowAtPoint(evt.getPoint());
        int col = tablesummary.columnAtPoint(evt.getPoint());
         // select any field in a row grabs the vendor for that row...so open the possibility of payment for that row/vendor
        cbpaymentpanel.setEnabled(true);
        selectedCustomer = tablesummary.getValueAt(row, 1).toString();
        
        if ( col == 0) {
            
            getdetail(selectedCustomer);
            btdetail.setEnabled(true);
            detailpanel.setVisible(true);
            btexport.setEnabled(true);
            btcsv.setEnabled(true);
            btpdf.setEnabled(true);
            paymentpanel.setVisible(false);
            cbpaymentpanel.setSelected(false);
        }
    }//GEN-LAST:event_tablesummaryMouseClicked

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
        detailpanel.setVisible(false);
       btdetail.setEnabled(false);
       btexport.setEnabled(false);
       btcsv.setEnabled(false);
       btpdf.setEnabled(false);
       paymentpanel.setVisible(false);
       cbpaymentpanel.setSelected(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void cbpaymentpanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbpaymentpanelActionPerformed
        if (cbpaymentpanel.isSelected()) {
           if (! selectedCustomer.isEmpty()) {
           getpayment(selectedCustomer);
            paymentpanel.setVisible(true);
           }
       } else {
           paymentpanel.setVisible(false);
       }
    }//GEN-LAST:event_cbpaymentpanelActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
        try {
                FileDialog fDialog;
                fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
                fDialog.setVisible(true);
                //fDialog.setFile("data.csv");
                String path = fDialog.getDirectory() + fDialog.getFile();
                File f = new File(path);
                BufferedWriter output;
                
                output = new BufferedWriter(new FileWriter(f));
                String myheader = "";
        
        
        try {                                         
            
            String fromcust = "";
            String tocust = "";
           
            
            if (ddfromcust.getSelectedItem().toString().isEmpty()) {
                fromcust = bsmf.MainFrame.lowchar;
            } else {
                fromcust = ddfromcust.getSelectedItem().toString();
            }
            if (ddtocust.getSelectedItem().toString().isEmpty()) {
                tocust = bsmf.MainFrame.hichar;
            } else {
                tocust = ddtocust.getSelectedItem().toString();
            }
          
              Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            
            try {
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            
               String newstring = "";
               ArrayList custs = new ArrayList();
               
               custs.add(selectedCustomer);
                 
                 for (int j = 0; j < custs.size(); j++) {
                 
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                     res = st.executeQuery("SELECT ar_cust, ar_nbr, ar_type, ar_effdate, ar_duedate, sh_po, " +
                        " case when ar_duedate > date() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= date() and ar_duedate > date() - date(date(), '+30 day') then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= date() - date(date(), '+30 day') and ar_duedate > date(date(), '+60 day') then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= date() - date(date(), '+60 day') and ar_duedate > date(date(), '+90 day') then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= date() - date(date(), '+90 day') then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr left outer join ship_mstr on sh_id = ar_nbr " +
                        " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                        " order by ar_cust, ar_nbr ;");   
                 }  else {
                 res = st.executeQuery("SELECT ar_cust, ar_nbr, ar_type, ar_effdate, ar_duedate, sh_po, " +
                        " case when ar_duedate > curdate() then ar_open_amt else 0 end as '0', " +
                        " case when ar_duedate <= curdate() and ar_duedate > curdate() - interval 30 day then ar_open_amt else 0 end as '30', " +
                        " case when ar_duedate <= curdate() - interval 30 day and ar_duedate > curdate() - interval 60 day then ar_open_amt else 0 end as '60', " +
                        " case when ar_duedate <= curdate() - interval 60 day and ar_duedate > curdate() - interval 90 day then ar_open_amt else 0 end as '90', " +
                        " case when ar_duedate <= curdate() - interval 90 day then ar_open_amt else 0 end as '90p' " +
                        " FROM  ar_mstr left outer join ship_mstr on sh_id = ar_nbr " +
                         " where ar_cust = " + "'" + custs.get(j) + "'" + 
                        " AND ar_status = 'o' " +
                        " order by ar_cust, ar_nbr ;");   
                 }
                 int i = 0;
                myheader = "Cust,Inv,PO,Invdate,DueDate,0,30,60,90,90+";
                output.write(myheader + '\n');
                  while (res.next()) {
                      // put this in place because sqlite is returning one null record when there are no ar_mstr records to pull...because of sum() function
                      // if ar_cust is null...I am assuming I tripped this sqlite issue...so break and get out before updating i counter
                      if (res.getString("ar_cust") == null) {
                          break;
                      }
                      i++;
                     
                     newstring = res.getString("ar_cust") + "," + res.getString("ar_nbr") + "," + res.getString("sh_po") + "," +
                             res.getString("ar_effdate") + "," + res.getString("ar_duedate") + "," + res.getString("0") + "," +
                            res.getString("30") + "," + res.getString("60") + "," + res.getString("90") + "," + res.getString("90p") ;
                           output.write(newstring + '\n');
                   }
                   if (i == 0) {
                      // create record with zero fields
                      newstring = custs.get(j) + ",none,none,none,none,0,0,0,0,0";
                      output.write(newstring + '\n');
                  }
                  
                 
                  
             } // for each customer in range
            
            
            
          } catch (SQLException s) {
              MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot execute sql query for AR Export");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
                
         
             output.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }//GEN-LAST:event_btexportActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (tablesummary != null)
        OVData.exportCSV(tablesummary);
    }//GEN-LAST:event_btcsvActionPerformed

    private void btpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpdfActionPerformed
           if (tabledetail != null && modeldetail.getRowCount() > 0) {
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
                File mytemplate = new File("jasper/aragingdetail.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tabledetail.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/araging.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Could not create jasperfile...see stacktrace");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btpdfActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btexport;
    private javax.swing.JButton btpdf;
    private javax.swing.JCheckBox cbpaymentpanel;
    private javax.swing.JComboBox ddfromcust;
    private javax.swing.JComboBox ddtocust;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JPanel paymentpanel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablepayment;
    private javax.swing.JTable tablesummary;
    // End of variables declaration//GEN-END:variables
}
