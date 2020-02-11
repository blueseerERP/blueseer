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

package com.blueseer.sch;

import com.blueseer.ord.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
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
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import java.util.Arrays;
import javax.swing.ImageIcon;

/**
 *
 * @author vaughnte
 */
public class RetailReorderRpt extends javax.swing.JPanel {
 
     DefaultTableModel summarymodel = new DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "Item", "Desc", "LeadTime", "QOH", "DemandAlloc", "UQOH", "POQty", "DemandUnAlloc", "FcstQty", "SafetyStock",  "Status", "NextPODate"})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0) {      
                            return ImageIcon.class;
                        } else if (col == 3 || col == 4 || col == 5 || col == 6 || col == 7 || col == 8 || col == 9 || col == 10) {
                            return Integer.class;    
                        } else return String.class;  //other columns accept String values  
                      }  
                        };
    
    
      DefaultTableModel demandmodel = new DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "OrdNbr", "Cust", "Name", "PO", "DueDate", "OrdQty", "AllocatedQty"})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0) {      
                            return ImageIcon.class;
                        } else if ( col == 7 || col == 8 ) {
                            return Integer.class;    
                        } else return String.class;  //other columns accept String values  
                      }  
                        };
    
      DefaultTableModel replenishmodel = new DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "PONbr", "Vend", "Name", "PONbr", "PODate", "DueDate", "POQty", "RcvdQty"})
             {
                       @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0) {      
                            return ImageIcon.class;
                        } else if ( col == 7 || col == 8 ) {
                            return Integer.class;    
                        } else return String.class;  //other columns accept String values  
                      }  
                        };
    
      
     
    /**
     * Creates new form ScrapReportPanel
     */
    
     
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 11);  // 8 = status column
        
        c.setBackground(table.getBackground());
        c.setForeground(table.getForeground());
        
        if (column == 11) {
             if ("urgent".equals(status)) {
                c.setBackground(Color.red);
                c.setForeground(Color.WHITE);
            } else if ("safe".equals(status)) {
                c.setBackground(Color.YELLOW);
                c.setForeground(Color.BLACK);    
            } else {
                c.setBackground(table.getBackground());
                c.setForeground(table.getForeground());
            } 
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
        
        
    public RetailReorderRpt() {
        initComponents();
    }

    
    public void getDetail(String item, String site, int leadtime) {
        
        demandmodel.setNumRows(0);
        replenishmodel.setNumRows(0);
        
        int days = 0;
        if (! tbdays.getText().isEmpty() && cboverride.isSelected()) {
            days = Integer.valueOf(tbdays.getText());
        } else {
            days = leadtime;
        }
        
        ArrayList myarray = new ArrayList();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = new java.util.Date();
        Calendar caldate = Calendar.getInstance();
        caldate.add(Calendar.DATE, days);
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                 res = st.executeQuery("SELECT so_nbr, so_cust, cm_name, so_po, so_due_date, sod_ord_qty, sod_all_qty " +
                        " FROM  sod_det inner join so_mstr on sod_nbr = so_nbr " +
                        " inner join cm_mstr on cm_code = so_cust " +
                        " where sod_part = " + "'" + item + "'" +
                    //    " AND sod_due_date >= " + "'" + dfdate.format(now) + "'" + 
                        " AND sod_due_date <= " + "'" + dfdate.format(caldate.getTime()) + "'" + 
                        " AND so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +     
                        " AND so_status <> 'close' " +           
                        " order by sod_nbr ;"); 
                     while (res.next()) {
                          demandmodel.addRow(new Object[]{
                            BlueSeerUtils.clickflag,
                                res.getString("so_nbr"),
                                res.getString("so_cust"),
                                res.getString("cm_name"),
                                res.getString("so_po"),
                                res.getString("so_due_date"),
                                res.getString("sod_ord_qty"),
                                res.getString("sod_all_qty")
                            });
                     }
                                         
                     res = st.executeQuery("SELECT po_nbr, po_vend, vd_name, po_nbr, po_ord_date, po_due_date, pod_ord_qty, pod_rcvd_qty " +
                        " FROM  pod_mstr inner join po_mstr on pod_nbr = po_nbr " +
                        " inner join vd_mstr on vd_addr = po_vend " +
                        " where pod_part = " + "'" + item + "'" +
                     //   " AND pod_due_date >= " + "'" + dfdate.format(now) + "'" + 
                        " AND pod_due_date <= " + "'" + dfdate.format(caldate.getTime()) + "'" + 
                        " AND po_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +     
                        " AND po_status <> 'close' " +           
                        " order by pod_nbr ;"); 
                     while (res.next()) {
                          replenishmodel.addRow(new Object[]{
                            BlueSeerUtils.clickflag,
                                res.getString("po_nbr"),
                                res.getString("po_vend"),
                                res.getString("vd_name"),
                                res.getString("po_nbr"),
                                res.getString("po_ord_date"),
                                res.getString("po_due_date"),
                                res.getString("pod_ord_qty"),
                                res.getString("pod_rcvd_qty")
                            });
                     }
                     
                     
                     

            } catch (SQLException s) {
                bsmf.MainFrame.show("SQL cannot get Terms Master");
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
        
    }
    
    public void initvars(String[] arg) {
       
        java.util.Date now = new java.util.Date();
        cboverride.setSelected(false);
        tbdays.setText("0");
        
        demandpanel.setVisible(false);
        replenishpanel.setVisible(false);
        btdetail.setEnabled(false);
        
        summarymodel.setRowCount(0);
        demandmodel.setNumRows(0);
        replenishmodel.setNumRows(0);
        tablesummary.setModel(summarymodel);
        tabledemand.setModel(demandmodel);
        tablereplenish.setModel(replenishmodel); 
        tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
        tabledemand.getColumnModel().getColumn(0).setMaxWidth(100);
        tablereplenish.getColumnModel().getColumn(0).setMaxWidth(100);
         
     //   tablesummary.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
     //   tablesummary.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
        tablesummary.getTableHeader().setReorderingAllowed(false);
         
         
         
         ddfromitem.removeAllItems();
         ddtoitem.removeAllItems(); 
         ArrayList myitems = OVData.getItemMasterAlllist();
        for (int i = 0; i < myitems.size(); i++) {
            ddfromitem.addItem(myitems.get(i));
        }
        for (int i = 0; i < myitems.size(); i++) {
            ddtoitem.addItem(myitems.get(i));
        } 
        ddtoitem.setSelectedIndex(ddtoitem.getItemCount() - 1);
        
       ddsite.removeAllItems();
       ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
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
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboverride = new javax.swing.JCheckBox();
        ddfromitem = new javax.swing.JComboBox();
        ddtoitem = new javax.swing.JComboBox();
        btpdf = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        btcsv = new javax.swing.JButton();
        tbdays = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablesummary = new javax.swing.JTable();
        demandpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledemand = new javax.swing.JTable();
        replenishpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablereplenish = new javax.swing.JTable();
        btdetail = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Item");

        jLabel4.setText("To Item");

        labelcount.setText("0");

        jLabel7.setText("Line Count:");

        cboverride.setText("Override?");

        btpdf.setText("PDF");
        btpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpdfActionPerformed(evt);
            }
        });

        jLabel5.setText("Site:");

        btcsv.setText("CSV");
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        tbdays.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbdaysFocusLost(evt);
            }
        });

        jLabel2.setText("Days Forward:");

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablesummary.setAutoCreateRowSorter(true);
        tablesummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

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

        demandpanel.setLayout(new java.awt.BorderLayout());

        tabledemand.setAutoCreateRowSorter(true);
        tabledemand.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tabledemand.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledemandMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabledemand);

        demandpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(demandpanel);

        replenishpanel.setLayout(new java.awt.BorderLayout());

        tablereplenish.setAutoCreateRowSorter(true);
        tablereplenish.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablereplenish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereplenishMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tablereplenish);

        replenishpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(replenishpanel);

        btdetail.setText("Hide Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
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
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddfromitem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btpdf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboverride)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbdays, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdetail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1335, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btpdf)
                        .addComponent(btcsv)
                        .addComponent(cboverride)
                        .addComponent(tbdays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(btdetail))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel7)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addGap(18, 18, 18)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
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
     java.util.Date now = new java.util.Date();
     int wk = OVData.getForecastWeek(now);
     
     try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                 double ordqty = 0;
                 double allqty = 0;
                 double purqty = 0;
                 double qoh = 0;
                 double leadtime = 0;
                 String recommendDate = "";
                 double recommendQty = 0;
                 Integer[] values = new Integer[13];
                 double diff = 0.0;
                 
                DecimalFormat df = new DecimalFormat("#0.00");
                int i = 0;
                int days = 0;
                if (! tbdays.getText().isEmpty()) {
                    days = Integer.valueOf(tbdays.getText());
                }
                
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
                summarymodel.setNumRows(0);
               
               // tableorder.getColumnModel().getColumn(0).setCellRenderer(new OrderReport1.SomeRenderer());  
              //  tableorder.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                Enumeration<TableColumn> en = tablesummary.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (tc.getIdentifier().toString().equals("Select") || 
                             tc.getIdentifier().toString().equals("Print") ) {
                         continue;
                     }
                     tc.setCellRenderer(new RetailReorderRpt.SomeRenderer());
                 }
                // TableColumnModel tcm = tablescrap.getColumnModel();
               // tcm.getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
             
               
               
                 
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                 ArrayList<String[]> mylist = new ArrayList<String[]>();
                 ArrayList<String[]> finallist = new ArrayList<String[]>();
                
                // let's start with all items of type 'A'...and collect all Demand and Replenishment orders through timeline of 'leadtime' of the item
                
               // Calendar calfrom = Calendar.getInstance();
                //calfrom.add(Calendar.DATE, -365);
                 
                 res = st.executeQuery("SELECT it_item, it_desc, it_sell_price, it_pur_price, it_leadtime, it_safestock " +
                        " FROM  item_mstr where it_code = 'A' AND " +
                        " it_item >= " + "'" + ddfromitem.getSelectedItem().toString() + "'" + " AND " +
                        " it_item <= " + "'" + ddtoitem.getSelectedItem().toString() + "'"  +  " AND " +
                        " it_status = 'ACTIVE' " +
                         " order by it_item;"); 
                 
                  while (res.next()) {
                      mylist.add(new String[] {res.getString("it_item"), 
                          res.getString("it_desc"),
                          res.getString("it_pur_price"),
                          res.getString("it_sell_price"),
                          res.getString("it_leadtime"),
                          res.getString("it_safestock")
                          });
                  }
                  res.close();
                  
                
                 for (String[] s : mylist) {
                    
                    // reset 
                    ordqty = 0;
                    allqty = 0;
                    purqty = 0;
                    qoh = 0;
                    leadtime = 0;
                    recommendDate = "";
                    recommendQty = 0;
                    diff = 0.0;
                     
                    if (cboverride.isSelected()) {
                        leadtime = days;
                    } else {
                        leadtime = Integer.valueOf(s[4]);
                    }
                    
                    int safestock = Integer.valueOf(s[5]);
                   
                    
                    Calendar caldate = Calendar.getInstance();
                    caldate.add(Calendar.DATE, (int) leadtime);   //  per lead time of item
                    
                    // get unallocated demand
                     res = st.executeQuery("SELECT  " +  
                        " sum(sod_ord_qty - sod_all_qty - sod_shipped_qty) as totqty, " +
                        " sum(case when sod_all_qty <> '0' then (sod_all_qty - sod_shipped_qty) end) as allqty " +
                        " FROM  sod_det inner join so_mstr on sod_nbr = so_nbr " +
                        " where sod_part = " + "'" + s[0] + "'" +
                       // " AND sod_due_date >= " + "'" + dfdate.format(now) + "'" + 
                        " AND sod_due_date <= " + "'" + dfdate.format(caldate.getTime()) + "'" + 
                        " AND so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +     
                        " AND so_status <> 'close' " +           
                        " group by sod_part ;"); 
                     while (res.next()) {
                         ordqty = res.getInt("totqty");
                         allqty = res.getInt("allqty");
                     }
                     
                     // get replenishment
                      res = st.executeQuery("SELECT  " +
                        " sum(pod_ord_qty - pod_rcvd_qty) as totqty " +
                        " FROM  pod_mstr inner join po_mstr on pod_nbr = po_nbr " +
                        " where pod_part = " + "'" + s[0] + "'" +
                     //   " AND pod_due_date >= " + "'" + dfdate.format(now) + "'" + 
                        " AND pod_due_date <= " + "'" + dfdate.format(caldate.getTime()) + "'" + 
                        " AND po_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +    
                        " AND po_status <> 'close' " +         
                         " group by pod_part ;"); 
                     while (res.next()) {
                         purqty = res.getInt("totqty");
                     }
                     
                     // now get QOH
                      res = st.executeQuery("SELECT  sum(in_qoh) as totqty  " +
                        " FROM  in_mstr  " +
                        " where in_part = " + "'" + s[0] + "'" + 
                        " group by in_part ;");

                while (res.next()) {
                    qoh = res.getInt("totqty");
                }
                
                double aqoh = allqty;
                double uqoh = qoh - aqoh;
                if (uqoh < 0) {
                    uqoh = 0;
                }
                
              
                
                
                // now lets get forecast quantities
                Calendar cal = Calendar.getInstance();
                cal.getTime();
                String thisyear = String.valueOf(cal.get(Calendar.YEAR));
                
                 res = st.executeQuery("select * from fct_mstr where fct_part = " + "'" + s[0] + "'" +
                                       " AND fct_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + 
                                       " AND fct_year = " + "'" + thisyear + "'" + 
                                       ";" );
                 int fctsum = 0;
                 double rdays = 0;
                 int fctleadtime = ( (int) leadtime / 7) + 1;
                 while (res.next()) {
                    values = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
                        for (int k = 0 ; k < fctleadtime + 1; k++) {
                             if ((wk + k) > 52) { continue;}
                             fctsum += res.getInt("fct_wkqty" + (wk + k));
                         // values[k] = res.getInt("fct_wkqty" + (wk + k));
                        }
                }
                if (ordqty == 0) {
                    rdays = leadtime;
                } else {
                rdays = leadtime - ((1 - (fctsum / ordqty)) * leadtime);
                }
                
                if (rdays < 1) {
                    rdays = 0;
                }
               
              //  MainFrame.show(diff + "/" + rdays + "/" + fctsum + "/" + uqoh + "/" + purqty + "/" + ordqty + "/" + leadtime);
              // if leadtime expected quantities less demand is less than leadtime forecasted quantites then order
                // diff = forecastqty -  ( (uqoh + purqty) - demand )  ...then order diff * 
                // dayToOrder = now + (diff * leadtime) / forecastqty)
                //   7 week demand is 100 pcs   14.2 per week
                //   50 pcs per week forecasted
                //   170 QOH
                //        350  -  (350 + 0) - 280) = 70...70 is good enough for another 5.6 weeks
                cal.add(Calendar.DATE, (int) rdays);
                if (fctsum == 0) {  // if no forecast
                recommendDate = "No Forecast";    
                } else {
                recommendDate = dfdate.format(cal.getTime());
                }
                
                  // now let's do the status...and override recommendDate if necessary 
                  // only 3 possibilities...start with 'good'...then flow through the logic...triggering exceptions if they occurr...otherwise 'good'.
                String status = "good";
                
                if (uqoh < ordqty) {
                    status = "safe";
                        // check if any POs are on the horizon to meet the order due dates
                        // foreach order that has unallocated demand check if sum of PO quantities come before order due date
                        res = st.executeQuery("select s.sod_nbr, s.sod_due_date, s.sod_ord_qty, s.sod_all_qty, " +
                            " ifnull((select sum(pod_ord_qty) from pod_mstr where pod_part = s.sod_part and pod_due_date <= s.sod_Due_date),0) as coverqty " +
                            " from sod_det s where " +
                            " sod_part = " + "'" + s[0] + "'" +
                            " AND (sod_ord_qty - sod_all_qty) > 0 " +
                            ";");
                    while (res.next()) {
                      if (res.getInt("coverqty") == 0) {
                          status = "urgent"; recommendDate = dfdate.format(now);
                      }
                    }
                   
                }
                
                
                
                 i++;
                     
                     summarymodel.addRow(new Object[]{
                            BlueSeerUtils.clickbasket,
                                s[0],
                                s[1],
                                Integer.valueOf(s[4]),
                                qoh,
                                aqoh,
                                uqoh,
                                purqty,
                                ordqty,
                                fctsum,
                                Integer.valueOf(s[5]),
                                status,
                                recommendDate
                            });
                     
                 }         
                res.close();
                st.close();
                
                labelcount.setText(String.valueOf(i));
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot execute sql query for Retail Report");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void tablesummaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablesummaryMouseClicked
        int row = tablesummary.rowAtPoint(evt.getPoint());
        int col = tablesummary.columnAtPoint(evt.getPoint());
      
        if ( col == 0) {
            
            getDetail(tablesummary.getValueAt(row, 1).toString(), ddsite.getSelectedItem().toString(), Integer.valueOf(tablesummary.getValueAt(row,3).toString()));
            btdetail.setEnabled(true);
           demandpanel.setVisible(true);
           replenishpanel.setVisible(true);
        }
        
        
        
    }//GEN-LAST:event_tablesummaryMouseClicked

    private void btpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpdfActionPerformed
     MainFrame.show("Not yet supported");
        /*
        if (tablesummary != null && summarymodel.getRowCount() > 0) {
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
                File mytemplate = new File("jasper/orderbrowsesumary.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tablesummary.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ordbrowse.pdf");
         
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
*/
    }//GEN-LAST:event_btpdfActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
       if (tablesummary != null && summarymodel.getRowCount() > 0)
        OVData.exportCSV(tablesummary);
    }//GEN-LAST:event_btcsvActionPerformed

    private void tbdaysFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbdaysFocusLost
          String x = BlueSeerUtils.bsformat("", tbdays.getText(), "0");
        if (x.equals("error")) {
            tbdays.setText("");
            tbdays.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbdays.requestFocus();
        } else {
            tbdays.setText(x);
            tbdays.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbdaysFocusLost

    private void tabledemandMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledemandMouseClicked
         int row = tabledemand.rowAtPoint(evt.getPoint());
        int col = tabledemand.columnAtPoint(evt.getPoint());
      
        if ( col == 0) {
             if (! checkperms("OrderMaint")) { return; }
              String[] arg = new String[] {tabledemand.getValueAt(row, 1).toString()};
              reinitpanels("OrderMaint", true, arg);
        }
       
    }//GEN-LAST:event_tabledemandMouseClicked

    private void tablereplenishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereplenishMouseClicked
        int row = tablereplenish.rowAtPoint(evt.getPoint());
        int col = tablereplenish.columnAtPoint(evt.getPoint());
      
        if ( col == 0) {
             if (! checkperms("POMaintMenu")) { return; }
              String[] arg = new String[] {tablereplenish.getValueAt(row, 1).toString()};
              reinitpanels("POMaintMenu", true, arg);
        }
       
    }//GEN-LAST:event_tablereplenishMouseClicked

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
        demandpanel.setVisible(false);
        replenishpanel.setVisible(false);
        btdetail.setEnabled(false);
       
    }//GEN-LAST:event_btdetailActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btpdf;
    private javax.swing.JCheckBox cboverride;
    private javax.swing.JComboBox ddfromitem;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtoitem;
    private javax.swing.JPanel demandpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelcount;
    private javax.swing.JPanel replenishpanel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledemand;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereplenish;
    private javax.swing.JTable tablesummary;
    private javax.swing.JTextField tbdays;
    // End of variables declaration//GEN-END:variables
}
