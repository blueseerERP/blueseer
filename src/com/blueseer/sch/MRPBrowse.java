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

import bsmf.MainFrame;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import com.toedter.calendar.DateUtil;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author vaughnte
 */
public class MRPBrowse extends javax.swing.JPanel {

    MyTableModel mymodel = new MyTableModel(new Object[][]{},
                    new String[]{"Select","PART", "CALC", "DATE1", "DATE2", "DATE3", "DATE4", "DATE5", "DATE6", "DATE7"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    MyTableModelDetail modelorder = new MyTableModelDetail(new Object[][]{},
                        new String[]{"Part", "Order", "Type","Status", "DueDate", "Qty"});
     
    
    javax.swing.table.DefaultTableModel modeltrans = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{"type", "date", "qty"});
    
    String startdate = "";
    String enddate = "";
    String cumstartdate = "";
    String cumenddate = "";
    /**
     * Creates new form MRPBrowse1
     */
    public MRPBrowse() {
        initComponents();
    }

    
        public void getdetail(String part) {
      
         
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
         
          ArrayList<String> parents = new ArrayList<String>();
        parents = OVData.getpsmstrparents2(part);
        Collections.sort(parents);
         
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                
                
                
               if (! parents.isEmpty()) { 
               for (String line : parents) {
                res = st.executeQuery("select * from sod_det " +
                        " where sod_part = " + "'" + line + "'" + 
                        " and sod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and sod_due_date >= "  + "'" + startdate + "'" + 
                        " and sod_due_date <= "  + "'" + enddate + "'" + 
                        " order by sod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("sod_part"), 
                       res.getString("sod_nbr"),
                       "SORD",
                       res.getString("sod_status"),
                       res.getString("sod_due_date"),
                      res.getInt("sod_ord_qty")});
                }
               }
               } else {
                   // must be top FG
                   res = st.executeQuery("select * from sod_det " +
                        " where sod_part = " + "'" + part + "'" + 
                        " and sod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and sod_due_date >= "  + "'" + startdate + "'" + 
                        " and sod_due_date <= "  + "'" + enddate + "'" + 
                        " order by sod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("sod_part"), 
                       res.getString("sod_nbr"),
                       "SORD",
                       res.getString("sod_status"),
                       res.getString("sod_due_date"),
                      res.getInt("sod_ord_qty")});
                }
                   
                   
                   
                   
               }
               
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get Order Detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
        public void getPlans(String part) {
      
       
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
         String status = "";
         
         Enumeration<TableColumn> en = tabledetail.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new MRPBrowse.SomeRenderer());
                 }
       
          ArrayList<String> parents = new ArrayList<String>();
       
         
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
              
                res = st.executeQuery("select * from plan_mstr " +
                        " where plan_part = " + "'" + part + "'" + 
                        " and plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and plan_date_due >= "  + "'" + startdate + "'" + 
                        " and plan_date_due <= "  + "'" + enddate + "'" + 
                        " order by plan_date_due " +   ";");
                while (res.next()) {
                    if (res.getInt("plan_status") == 0) {
                        status = "open";
                    } 
                    if (res.getInt("plan_status") < 0) {
                        status = "void";
                    }
                    if (res.getInt("plan_status") > 0) {
                        status = "close";
                    }
                    if (res.getInt("plan_is_sched") == 0) {
                        status = "unsched";
                    }
                   modelorder.addRow(new Object[]{ 
                      res.getString("plan_part"), 
                       res.getString("plan_nbr"),
                       res.getString("plan_type"),
                       status,
                       res.getString("plan_date_due"),
                      res.getInt("plan_qty_sched")});
                }
             
              
                
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get Plan Detail for MRP");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
        
        public void getPurch(String part) {
      
       
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
         
         
         
       
          ArrayList<String> parents = new ArrayList<String>();
       
         
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
              
                res = st.executeQuery("select * from pod_mstr " +
                        " where pod_part = " + "'" + part + "'" + 
                        " and pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and pod_due_date >= "  + "'" + startdate + "'" + 
                        " and pod_due_date <= "  + "'" + enddate + "'" + 
                        " order by pod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("pod_part"), 
                       res.getString("pod_nbr"),
                       "PORD",
                       res.getString("pod_status"),
                       res.getString("pod_due_date"),
                      (res.getInt("pod_ord_qty") - res.getInt("pod_rcvd_qty"))});
                }
             
               
                
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get Plan Detail for MRP");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
         
        public void getrecenttrans(String parentpart) {
             
                 
         tbqtyoh.setText(String.valueOf(invData.getItemQOHTotal(parentpart, OVData.getDefaultSite())));
         tbcost.setText(String.valueOf(invData.getItemCost(parentpart, "standard", OVData.getDefaultSite())));
         tbtype.setText(invData.getItemTypeByPart(parentpart));
                 
                 
       Double opcost = 0.00;
       Double prevcost = 0.00;
       DecimalFormat df = new DecimalFormat("#.####", new DecimalFormatSymbols(Locale.US)); 
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

                
           
               
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT tr_type, tr_eff_date, tr_id, tr_qty  " +
                        " FROM  tran_mstr  " +
                        " where tr_part = " + "'" + parentpart.toString() + "'" + 
                        " order by tr_eff_date desc limit 25 ;");

                while (res.next()) {
                    i++;
                    modeltrans.addRow(new Object[]{
                                res.getString("tr_type"),
                                res.getString("tr_eff_date"),
                                res.getDouble("tr_qty")
                            });
              
                }
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem selecting recent trans");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
        
        
    public void initvars(String[] arg) {
       
       
        
        buttonGroup1.add(rbclassm);
        buttonGroup1.add(rbclassp);
        buttonGroup1.add(rbclassa);
        
         tabletrans.setModel(modeltrans);
         tabledetail.setModel(modelorder);
         
        modeltrans.setRowCount(0);
        modelorder.setRowCount(0);
        mymodel.setRowCount(0);
        
        rbclassm.setSelected(true);
        rbclassp.setSelected(false);
        rbclassa.setSelected(false);
        
        tbfrompart.setText("");
        tbtopart.setText("");
        ddweek.setSelectedIndex(0);
        
         PanelDetail.setVisible(false);
         
          ddsite.removeAllItems();
       ArrayList<String>  mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
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
    
    class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 2 || col == 3 || col == 4)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
      
      List<Color> rowColours = Arrays.asList(
        Color.RED,
        Color.GREEN,
        Color.CYAN
    );

    public void setRowColour(int row, Color c) {
        rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }

    public Color getRowColour(int row) {
        return rowColours.get(row);
    }

      
        }    
    
     class MyTableModelDetail extends DefaultTableModel {  
      
        public MyTableModelDetail(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 3 )       
                return Integer.class;  
            else return String.class;  //other columns accept String values  
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
      
      List<Color> rowColours = Arrays.asList(
        Color.RED,
        Color.GREEN,
        Color.CYAN
    );

    public void setRowColour(int row, Color c) {
        rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }

    public Color getRowColour(int row) {
        return rowColours.get(row);
    }

      
        }    
    
     class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

     
    
     // IF status is close...then plan order is closed and it will not be tallied in MRP Plan row
     // IF Status is unsched ...then plan has not been scheduled and sched_amt will be 0.
     
     String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString();   
        
     if (status.compareTo("unsched") == 0) {
             c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);
        } else if (status.compareTo("close") == 0 || status.compareTo("void") == 0 ) {
             c.setBackground(Color.BLUE);
            c.setForeground(Color.WHITE); 
        } else {
             c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }
     
        
       
        return c;
    }
    }
       
      class MainRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

     
     // IF amt is 0 ...then plan order is not scheduled
     // IF status is close...then plan order is closed and it will not be tallied in MRP Plan row
     double amt1 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString());  
     double amt2 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 4).toString());  
     double amt3 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 5).toString());  
     double amt4 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 6).toString());  
     double amt5 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7).toString());  
     double amt6 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 8).toString());  
     double amt7 = Double.valueOf((String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 9).toString());  
     String calctype = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 2).toString(); 
        if (amt1 <= 0 && column == 3 && calctype.compareTo("QOH") == 0) {
             c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);
        } else if (amt2 <= 0 && column == 4 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        }  else if (amt3 <= 0 && column == 5 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        } else if (amt4 <= 0 && column == 6 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        } else if (amt5 <= 0 && column == 7 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        } else if (amt6 <= 0 && column == 8 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        } else if (amt7 <= 0 && column == 9 && calctype.compareTo("QOH") == 0 ) {
           c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);  
        } else {
             c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }
        if (column == 1 && row % 3 == 0) {
            c.setBackground(Color.BLUE);
            c.setForeground(Color.WHITE);  
        }
     
        
       
        return c;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btsearch = new javax.swing.JButton();
        ddweek = new javax.swing.JComboBox();
        tbfrompart = new javax.swing.JTextField();
        tbtopart = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btdetail = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        tbtype = new javax.swing.JTextField();
        tbqtyoh = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabletrans = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tbcost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rbclassm = new javax.swing.JRadioButton();
        rbclassp = new javax.swing.JRadioButton();
        rbclassa = new javax.swing.JRadioButton();
        PanelReport = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        PanelDetail = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("MRP Browse"));

        btsearch.setText("Search");
        btsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsearchActionPerformed(evt);
            }
        });

        ddweek.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Week1", "Week2", "Week3", "Week4", "Week5", "Week6", "Week7", "Week8", "Week9", "Week10", "Week11", "Week12", "Week13" }));

        jLabel1.setText("From:");

        jLabel2.setText("To:");

        btdetail.setText("Hide Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        jLabel3.setText("Site");

        tabletrans.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "TranType", "Date", "TranNbr", "Qty"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(tabletrans);

        jLabel4.setText("Type");

        jLabel5.setText("QOH");

        jLabel6.setText("Cost");

        rbclassm.setText("Class M");

        rbclassp.setText("Class P");

        rbclassa.setText("Class A");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbclassp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbclassm)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddweek, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbclassa)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel4)
                                                    .addComponent(jLabel5))
                                                .addGap(7, 7, 7)))
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbfrompart, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtopart, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btsearch)
                                    .addComponent(tbtype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btdetail)
                                    .addComponent(tbqtyoh, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddweek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbclassm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(rbclassp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbclassa)
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbfrompart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtopart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btsearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqtyoh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout PanelReportLayout = new javax.swing.GroupLayout(PanelReport);
        PanelReport.setLayout(PanelReportLayout);
        PanelReportLayout.setHorizontalGroup(
            PanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
        );
        PanelReportLayout.setVerticalGroup(
            PanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

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

        javax.swing.GroupLayout PanelDetailLayout = new javax.swing.GroupLayout(PanelDetail);
        PanelDetail.setLayout(PanelDetailLayout);
        PanelDetailLayout.setHorizontalGroup(
            PanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetailLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        PanelDetailLayout.setVerticalGroup(
            PanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsearchActionPerformed
       
       // if (rberror.isSelected() && rbinfo.isSelected()) {
      //     bsmf.MainFrame.show("Select either active OR inactive...not both");
       //     return;
       // }
        
       
       // reinit tables
       modeltrans.setRowCount(0);
        modelorder.setRowCount(0);
        mymodel.setRowCount(0);
       
       
       String frompart = "";
       String topart = "";
       String thispart = "";
       
             
       if (tbfrompart.getText().isEmpty()) {
           frompart = bsmf.MainFrame.lowchar;
       } else {
          frompart = tbfrompart.getText(); 
       }
       
       if (tbtopart.getText().isEmpty()) {
           topart = bsmf.MainFrame.hichar;
       } else {
          topart = tbtopart.getText(); 
       }
       
       String classcode = "";
                if (rbclassm.isSelected()) {
                    classcode = "M";
                } else  if (rbclassa.isSelected()) {
                    classcode = "A";
                } else {
                    classcode = "P";
                }
       
       
        tbtype.setText("");
        tbcost.setText("");
        tbqtyoh.setText("");
        
        mymodel.setNumRows(0);
        
        
        
        
        
        ArrayList<String> items = invData.getItemRangeByClass(ddsite.getSelectedItem().toString(), frompart, topart, classcode);
        
        
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                Statement st2 = bsmf.MainFrame.con.createStatement();
                Statement st3 = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                ResultSet res2 = null;
                ResultSet res3 = null;

                int i = 0;
                int z = 0;
                
                tablereport.setModel(mymodel);
                Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                      if (tc.getIdentifier().toString().equals("Select")) {
                         continue;
                     }
                     tc.setCellRenderer(new MRPBrowse.MainRenderer());
                 }
                // TableColumnModel tcm = tablereport.getColumnModel();
              //  tcm.getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getIntegerRenderer());
                // tablereport.getColumnModel().getColumns().setCellRenderer(new MRPBrowse1.SomeRenderer());
                // tablereport.getColumnModel().getColumn(3).setCellRenderer(new MRPBrowse1.SomeRenderer());       
                 
               // tablereport.getColumnModel().getColumn(0).setCellRenderer(new MRPBrowse1.ButtonRenderer());
                tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
                int startday = 2;
                int week = 0;
                double qoh = 0;
                double qoh1 = 0;
                double qoh2 = 0;
                double qoh3 = 0;
                double qoh4 = 0;
                double qoh5 = 0;
                double qoh6 = 0;
                double qoh7 = 0;
                double gain = 0;
                double cumqoh = 0;
                double cumplan = 0;
                double cumreplenish = 0;
                double cumdemand = 0;
                
                
                
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat hf = new SimpleDateFormat("EEE-MM-dd");
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                week = cal.get(Calendar.WEEK_OF_YEAR);
                
                if (ddweek.getSelectedItem().equals("Week1")) {
                cal.set(Calendar.DAY_OF_WEEK, startday);
                cumstartdate = dfdate.format(cal.getTime());
                }
                
                if (ddweek.getSelectedItem().equals("Week2")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 1);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                
                if (ddweek.getSelectedItem().equals("Week3")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 2);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                
                if (ddweek.getSelectedItem().equals("Week4")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 3);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week5")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 4);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week6")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 5);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week7")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 6);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week8")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 7);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week9")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 8);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week10")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 9);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week11")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 10);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week12")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 11);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                if (ddweek.getSelectedItem().equals("Week13")) {
                cal.set(Calendar.WEEK_OF_YEAR,week + 12);
                cal.set(Calendar.DAY_OF_WEEK, startday);
                }
                
                
                // go back one day to get cumenddate
                cal.add(Calendar.DATE,-1);
                cumenddate = dfdate.format(cal.getTime());
                
                // now jump back to true startdate...day 1 of week x
                cal.add(Calendar.DATE,1);
                startdate = dfdate.format(cal.getTime());
                String d1 = dfdate.format(cal.getTime());
                String d1h = hf.format(cal.getTime());
                
                // now day 2 of week x  
                cal.add(Calendar.DATE,1);
                String d2 = dfdate.format(cal.getTime());
                String d2h = hf.format(cal.getTime());
                
                // now day 3 of week x
                cal.add(Calendar.DATE,1);
                String d3 = dfdate.format(cal.getTime());
                String d3h = hf.format(cal.getTime());
                
                // now day 4 of week x
                cal.add(Calendar.DATE,1);
                String d4 = dfdate.format(cal.getTime());
                String d4h = hf.format(cal.getTime());
                
                // now day 5 of week x
                cal.add(Calendar.DATE,1);
                String d5 = dfdate.format(cal.getTime());
                String d5h = hf.format(cal.getTime());
                
                // now day 6 of week x
                cal.add(Calendar.DATE,1);
                String d6 = dfdate.format(cal.getTime());
                String d6h = hf.format(cal.getTime());
                
                // now day 7 of week x
                cal.add(Calendar.DATE,1);
                String d7 = dfdate.format(cal.getTime());
                String d7h = hf.format(cal.getTime());
                enddate = dfdate.format(cal.getTime());
                
                
                
                tablereport.getColumnModel().getColumn(3).setHeaderValue(d1h);
                tablereport.getColumnModel().getColumn(4).setHeaderValue(d2h);
                tablereport.getColumnModel().getColumn(5).setHeaderValue(d3h);
                tablereport.getColumnModel().getColumn(6).setHeaderValue(d4h);
                tablereport.getColumnModel().getColumn(7).setHeaderValue(d5h);
                tablereport.getColumnModel().getColumn(8).setHeaderValue(d6h);
                tablereport.getColumnModel().getColumn(9).setHeaderValue(d7h);
                
                tablereport.getTableHeader().repaint();
                
               
               String qa = "";
               String qb = "";
               String qc = "";
               String qd = "";
               String qe = "";
               String qf = "";
               String qg = "";
               
               for (String item : items )  {
               
                   
                qa = "0";
                qb = "0";
                qc = "0";
                qd = "0";
                qe = "0";
                qf = "0";
                qg = "0";
               qoh = invData.getItemQOHTotal(item, ddsite.getSelectedItem().toString());
               cumqoh = qoh;
               cumplan = 0;
               cumdemand = 0;
               cumreplenish = 0;
               
               // lets get cumaltive (if now week 1)
               if (! ddweek.getSelectedItem().equals("Week1")) {
                    res = st.executeQuery("select mrp_part, ifnull(sum(mrp_qty),0) as cumqty  " +
                             " from mrp_mstr where mrp_date >= " + "'" + cumstartdate + "'" + " AND" +
                             " mrp_date <= " + "'" + cumenddate + "'" + " AND" +
                             " mrp_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
                             " mrp_part = " + "'" + item + "'" + "  " +
                             " group by mrp_part ; ");
                    while (res.next()) {
                        cumdemand = res.getDouble("cumqty");
                    }
                    res.close();
               } 
               
               // lets get cumalative PLAN records (if now week 1)
               if (! ddweek.getSelectedItem().equals("Week1")) {
                    res = st.executeQuery("select plan_part, ifnull(sum(plan_qty_sched),0) as cumqty  " +
                             " from plan_mstr where plan_date_due >= " + "'" + cumstartdate + "'" + " AND" +
                             " plan_date_due <= " + "'" + cumenddate + "'" + " AND" +
                             " plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
                             " plan_status = '0' AND" +
                             " plan_part = " + "'" + item + "'" +
                             " group by plan_part ; ");
                    while (res.next()) {
                        cumplan = res.getDouble("cumqty");
                    }
                    res.close();
               }
               
                // lets get cumalative PURCHASE/REPLENISHMENT records (if now week 1)
               if (! ddweek.getSelectedItem().equals("Week1")) {
                    res = st.executeQuery("select pod_part, ifnull(sum(pod_ord_qty - pod_rcvd_qty),0) as cumqty  " +
                             " from pod_mstr " +
                             " where pod_due_date >= " + "'" + cumstartdate + "'" + " AND" +
                             " pod_due_date <= " + "'" + cumenddate + "'" + " AND" +
                             " pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                             " pod_part = " + "'" + item + "'" +
                             " group by pod_part ; ");
                          while (res.next()) {
                            cumreplenish = res.getDouble("cumqty");
                          }
                          res.close();
               }
               
               
               res = st.executeQuery("select mrp_part,  " +
               " sum(A) as A, sum(B) as B, sum(C) as C, sum(D) as D, sum(E) as E, sum(F) as F, sum(G) as G from " +
               " ( select mrp_part, (case when mrp_date = " + "'" + d1 + "'" + " then mrp_qty else '0' end) as A, " +
               " (case when mrp_date = " + "'" + d2 + "'" + " then mrp_qty else '0' end) as B, " + 
               " (case when mrp_date = " + "'" + d3 + "'" + " then mrp_qty else '0' end) as C, " +
               " (case when mrp_date = " + "'" + d4 + "'" + " then mrp_qty else '0' end) as D, " +
               " (case when mrp_date = " + "'" + d5 + "'" + " then mrp_qty else '0' end) as E, " +
               " (case when mrp_date = " + "'" + d6 + "'" + " then mrp_qty else '0' end) as F, " +
               " (case when mrp_date = " + "'" + d7 + "'" + " then mrp_qty else '0' end) as G " +
               " from mrp_mstr where mrp_date >= " + "'" + startdate + "'" + " AND" +
               " mrp_date <= " + "'" + enddate + "'" + " AND" +
               " mrp_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
               " mrp_part = " + "'" + item + "'" + " ) s " +
                       " group by mrp_part ; ");
               
             
                while (res.next()) {
                    i++;
                    qa = res.getString("A");
                    qb = res.getString("B");
                    qc = res.getString("C");
                    qd = res.getString("D");
                    qe = res.getString("E");
                    qf = res.getString("F");
                    qg = res.getString("G");
                } 
                
                 mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, item,
                        "DEMAND",
                        qa,
                        qb,
                        qc,
                        qd,
                        qe,
                        qf,
                        qg
                    });
                
                    // Now lets get the planning info if class 'M' otherwise purchase info if class 'P' or 'A'
                                   
                    if (classcode.toUpperCase().compareTo("M") == 0) {
                        
                        res2 = st2.executeQuery("select plan_part, (case when in_qoh is null then '0' else in_qoh end) as in_qoh, " +
               " sum(A) as A, sum(B) as B, sum(C) as C, sum(D) as D, sum(E) as E, sum(F) as F, sum(G) as G from " +
               " ( select plan_part, (case when plan_date_due = " + "'" + d1 + "'" + " then plan_qty_sched else '0' end) as A, " +
               " (case when plan_date_due = " + "'" + d2 + "'" + " then plan_qty_sched else '0' end) as B, " + 
               " (case when plan_date_due = " + "'" + d3 + "'" + " then plan_qty_sched else '0' end) as C, " +
               " (case when plan_date_due = " + "'" + d4 + "'" + " then plan_qty_sched else '0' end) as D, " +
               " (case when plan_date_due = " + "'" + d5 + "'" + " then plan_qty_sched else '0' end) as E, " +
               " (case when plan_date_due = " + "'" + d6 + "'" + " then plan_qty_sched else '0' end) as F, " +
               " (case when plan_date_due = " + "'" + d7 + "'" + " then plan_qty_sched else '0' end) as G " +
               " from plan_mstr where plan_date_due >= " + "'" + startdate + "'" + " AND" +
               " plan_date_due <= " + "'" + enddate + "'" + " AND" +
               " plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
               " plan_status = '0' AND" +
               " plan_part = " + "'" + item + "'" + " ) s " +
               " inner join item_mstr on it_item = plan_part " +
               " left outer join in_mstr on in_part = it_item and in_loc = it_loc " +
                       " group by plan_part ; ");
                   z = 0; 
                   while (res2.next()) {
                       z++;
                        
                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res2.getString("plan_part"),
                        "PLAN",
                        res2.getString("A"),
                        res2.getString("B"),
                        res2.getString("C"),
                        res2.getString("D"),
                        res2.getString("E"),
                        res2.getString("F"),
                        res2.getString("G")
                    });
                    
                    qoh1 = cumqoh - cumdemand + cumplan + Double.valueOf(res2.getString("A")) - Double.valueOf(qa);
                    qoh2 = qoh1 + Double.valueOf(res2.getString("B")) - Double.valueOf(qb);
                    qoh3 = qoh2 + Double.valueOf(res2.getString("C")) - Double.valueOf(qc);
                    qoh4 = qoh3 + Double.valueOf(res2.getString("D")) - Double.valueOf(qd);
                    qoh5 = qoh4 + Double.valueOf(res2.getString("E")) - Double.valueOf(qe);
                    qoh6 = qoh5 + Double.valueOf(res2.getString("F")) - Double.valueOf(qf);
                    qoh7 = qoh6 + Double.valueOf(res2.getString("G")) - Double.valueOf(qg);
                    
                   } 
                   
                   // if no plan records then create dummy zero 'PLAN' record
                   if (z == 0) {
                      mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, item,
                        "PLAN",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0"
                    });
                    
                    qoh1 = cumqoh - cumdemand + cumplan - Double.valueOf(qa);
                    qoh2 = qoh1 - Double.valueOf(qb);
                    qoh3 = qoh2 - Double.valueOf(qc);
                    qoh4 = qoh3 - Double.valueOf(qd);
                    qoh5 = qoh4 - Double.valueOf(qe);
                    qoh6 = qoh5 - Double.valueOf(qf);
                    qoh7 = qoh6 - Double.valueOf(qg); 
                   }
                 
                 } 
                    
                if (classcode.toUpperCase().compareTo("A") == 0) {
                     // if class A
                 
               res2 = st2.executeQuery("select it_item, (case when in_qoh is null then '0' else in_qoh end) as in_qoh, " +
               " (case when sum(A) is not null then sum(A) else '0' end) as A, " +
               " (case when sum(B) is not null then sum(B) else '0' end) as B, " +
               " (case when sum(C) is not null then sum(C) else '0' end) as C, " +
               " (case when sum(D) is not null then sum(D) else '0' end) as D, " +
               " (case when sum(E) is not null then sum(E) else '0' end) as E, " +
               " (case when sum(F) is not null then sum(F) else '0' end) as F, " +
               " (case when sum(G) is not null then sum(G) else '0' end) as G " +
               " from item_mstr left outer join ( " +
               " select pod_part, " +
               " (case when pod_due_date = " + "'" + d1 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as A, " +
               " (case when pod_due_date = " + "'" + d2 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as B, " + 
               " (case when pod_due_date = " + "'" + d3 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as C, " +
               " (case when pod_due_date = " + "'" + d4 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as D, " +
               " (case when pod_due_date = " + "'" + d5 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as E, " +
               " (case when pod_due_date = " + "'" + d6 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as F, " +
               " (case when pod_due_date = " + "'" + d7 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as G " +
               " from pod_mstr " +
               " where pod_due_date >= " + "'" + startdate + "'" + " AND" +
               " pod_due_date <= " + "'" + enddate + "'" + " AND" +
               " pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
               " ) s on s.pod_part = it_item" +
               " left outer join in_mstr on in_part = it_item and in_loc = it_loc " +
               " where it_item = " + "'" + item + "'" + " group by pod_part ; ");
                    
                   z = 0;         
                   while (res2.next()) {
                       z++;
                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res2.getString("it_item"),
                        "PURCH",
                        res2.getString("A"),
                        res2.getString("B"),
                        res2.getString("C"),
                        res2.getString("D"),
                        res2.getString("E"),
                        res2.getString("F"),
                        res2.getString("G")
                    });
                  //  MainFrame.show("X" + "/" + item + "/" + cumqoh + "/" + cumreplenish + "/" + qa);
                    qoh1 = cumqoh - cumdemand + cumreplenish + Double.valueOf(res2.getString("A")) - Double.valueOf(qa);
                    qoh2 = qoh1 + Double.valueOf(res2.getString("B")) - Double.valueOf(qb);
                    qoh3 = qoh2 + Double.valueOf(res2.getString("C")) - Double.valueOf(qc);
                    qoh4 = qoh3 + Double.valueOf(res2.getString("D")) - Double.valueOf(qd);
                    qoh5 = qoh4 + Double.valueOf(res2.getString("E")) - Double.valueOf(qe);
                    qoh6 = qoh5 + Double.valueOf(res2.getString("F")) - Double.valueOf(qf);
                    qoh7 = qoh6 + Double.valueOf(res2.getString("G")) - Double.valueOf(qg);
                    
                   } 
                   
                     // if no plan records then create dummy zero 'PURCH' record
                   if (z == 0) {
                      mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res.getString("mrp_part"),
                        "PURCH",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0"
                    });
                    
                    qoh1 = cumqoh - cumdemand + cumreplenish - Double.valueOf(qa);
                    qoh2 = qoh1 - Double.valueOf(qb);
                    qoh3 = qoh2 - Double.valueOf(qc);
                    qoh4 = qoh3 - Double.valueOf(qd);
                    qoh5 = qoh4 - Double.valueOf(qe);
                    qoh6 = qoh5 - Double.valueOf(qf);
                    qoh7 = qoh6 - Double.valueOf(qg); 
                   }
                   
                 }       
                   
                 if (classcode.toUpperCase().compareTo("P") == 0) {
                   // if class P
                 
               res2 = st2.executeQuery("select it_item, (case when in_qoh is null then '0' else in_qoh end) as in_qoh, " +
               " (case when sum(A) is not null then sum(A) else '0' end) as A, " +
               " (case when sum(B) is not null then sum(B) else '0' end) as B, " +
               " (case when sum(C) is not null then sum(C) else '0' end) as C, " +
               " (case when sum(D) is not null then sum(D) else '0' end) as D, " +
               " (case when sum(E) is not null then sum(E) else '0' end) as E, " +
               " (case when sum(F) is not null then sum(F) else '0' end) as F, " +
               " (case when sum(G) is not null then sum(G) else '0' end) as G " +
               " from item_mstr left outer join ( " +
               " select pod_part, " +
               " (case when pod_due_date = " + "'" + d1 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as A, " +
               " (case when pod_due_date = " + "'" + d2 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as B, " + 
               " (case when pod_due_date = " + "'" + d3 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as C, " +
               " (case when pod_due_date = " + "'" + d4 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as D, " +
               " (case when pod_due_date = " + "'" + d5 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as E, " +
               " (case when pod_due_date = " + "'" + d6 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as F, " +
               " (case when pod_due_date = " + "'" + d7 + "'" + " then (pod_ord_qty - pod_rcvd_qty) else '0' end) as G " +
               " from pod_mstr " +
               " where pod_due_date >= " + "'" + startdate + "'" + " AND" +
               " pod_due_date <= " + "'" + enddate + "'" + " AND" +
               " pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
               " ) s on s.pod_part = it_item" +
               " left outer join in_mstr on in_part = it_item and in_loc = it_loc " +
               " where it_item = " + "'" + item + "'" + " group by pod_part ; ");
                    
                   z = 0;         
                   while (res2.next()) {
                       z++;
                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res2.getString("it_item"),
                        "PURCH",
                        res2.getString("A"),
                        res2.getString("B"),
                        res2.getString("C"),
                        res2.getString("D"),
                        res2.getString("E"),
                        res2.getString("F"),
                        res2.getString("G")
                    });
                    
                    qoh1 = cumqoh - cumdemand + cumreplenish + Double.valueOf(res2.getString("A")) - Double.valueOf(qa);
                    qoh2 = qoh1 + Double.valueOf(res2.getString("B")) - Double.valueOf(qb);
                    qoh3 = qoh2 + Double.valueOf(res2.getString("C")) - Double.valueOf(qc);
                    qoh4 = qoh3 + Double.valueOf(res2.getString("D")) - Double.valueOf(qd);
                    qoh5 = qoh4 + Double.valueOf(res2.getString("E")) - Double.valueOf(qe);
                    qoh6 = qoh5 + Double.valueOf(res2.getString("F")) - Double.valueOf(qf);
                    qoh7 = qoh6 + Double.valueOf(res2.getString("G")) - Double.valueOf(qg);
                    
                   } 
                   
                     // if no plan records then create dummy zero 'PURCH' record
                   if (z == 0) {
                      mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res.getString("mrp_part"),
                        "PURCH",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0"
                    });
                    
                    qoh1 = cumqoh - cumdemand + cumreplenish - Double.valueOf(qa);
                    qoh2 = qoh1 - Double.valueOf(qb);
                    qoh3 = qoh2 - Double.valueOf(qc);
                    qoh4 = qoh3 - Double.valueOf(qd);
                    qoh5 = qoh4 - Double.valueOf(qe);
                    qoh6 = qoh5 - Double.valueOf(qf);
                    qoh7 = qoh6 - Double.valueOf(qg); 
                   }
                   
                   
                    
                 }  // end if class P
                    
                    mymodel.addRow(new Object[]{
                       "", item,
                        "QOH",
                        qoh1,
                        qoh2,
                        qoh3,
                        qoh4,
                        qoh5,
                        qoh6,
                        qoh7
                    });
               
                
               } // for items
  
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem during execution");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btsearchActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       PanelDetail.setVisible(false);
       // tabledetail.setVisible(false);
       // btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
         int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0 && ! tablereport.getValueAt(row, col).toString().isEmpty()) {
            
            modelorder.setNumRows(0);
            modeltrans.setNumRows(0);
            TableColumn tc = null;
             
            
           
            getrecenttrans(tablereport.getValueAt(row, 1).toString());
            
            if (tablereport.getValueAt(row, 2).toString().equals("PLAN")) {
            tc = tabledetail.getTableHeader().getColumnModel().getColumn(1);
            tc.setHeaderValue("PlanOrder");
            getPlans(tablereport.getValueAt(row, 1).toString());
            }
            
            if (tablereport.getValueAt(row, 2).toString().equals("DEMAND")) {
            tc = tabledetail.getTableHeader().getColumnModel().getColumn(1);
            tc.setHeaderValue("SalesOrder");
            getdetail(tablereport.getValueAt(row, 1).toString());
            }
            
            if (tablereport.getValueAt(row, 2).toString().equals("PURCH")) {
            tc = tabledetail.getTableHeader().getColumnModel().getColumn(1);
            tc.setHeaderValue("PurchOrder");
            getPurch(tablereport.getValueAt(row, 1).toString());
            }
            
            btdetail.setEnabled(true);
            PanelDetail.setVisible(true);
        }
    }//GEN-LAST:event_tablereportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDetail;
    private javax.swing.JPanel PanelReport;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btsearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddweek;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JRadioButton rbclassa;
    private javax.swing.JRadioButton rbclassm;
    private javax.swing.JRadioButton rbclassp;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTable tablereport;
    private javax.swing.JTable tabletrans;
    private javax.swing.JTextField tbcost;
    private javax.swing.JTextField tbfrompart;
    private javax.swing.JTextField tbqtyoh;
    private javax.swing.JTextField tbtopart;
    private javax.swing.JTextField tbtype;
    // End of variables declaration//GEN-END:variables
}
