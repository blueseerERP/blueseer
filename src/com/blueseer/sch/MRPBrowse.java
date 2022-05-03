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
package com.blueseer.sch;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.invData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import com.toedter.calendar.DateUtil;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
                    new String[]{getGlobalColumnTag("select"),
                        getGlobalColumnTag("item"), 
                        getGlobalColumnTag("calc"), 
                        "DATE1", 
                        "DATE2", 
                        "DATE3", 
                        "DATE4", 
                        "DATE5", 
                        "DATE6", 
                        "DATE7"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    MyTableModelDetail modelorder = new MyTableModelDetail(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("type"),
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("qty")});
     
    
    javax.swing.table.DefaultTableModel modeltrans = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                        getGlobalColumnTag("type"), 
                        getGlobalColumnTag("date"), 
                        getGlobalColumnTag("qty")});
    
    String startdate = "";
    String enddate = "";
    String cumstartdate = "";
    String cumenddate = "";
    /**
     * Creates new form MRPBrowse1
     */
    public MRPBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    
        public void getdetail(String part) {
      
         
         double total = 0.00;
         
        Set<String> parents = new LinkedHashSet<String>();
        parents = OVData.getpsmstrparents2(part);
        List<String> sortedList = new ArrayList<>(parents);
        Collections.sort(sortedList);
         
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                String blanket = "";
                
                
                
               if (! sortedList.isEmpty()) { 
               for (String line : sortedList) {
                res = st.executeQuery("select * from sod_det " +
                        " where sod_item = " + "'" + line + "'" + 
                        " and sod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and sod_due_date >= "  + "'" + startdate + "'" + 
                        " and sod_due_date <= "  + "'" + enddate + "'" + 
                        " order by sod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("sod_item"), 
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
                        " where sod_item = " + "'" + part + "'" + 
                        " and sod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and sod_due_date >= "  + "'" + startdate + "'" + 
                        " and sod_due_date <= "  + "'" + enddate + "'" + 
                        " order by sod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("sod_item"), 
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
    
        public void getPlans(String part) {
      
       
         double total = 0.00;
         String status = "";
         
         Enumeration<TableColumn> en = tabledetail.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new MRPBrowse.SomeRenderer());
                 }
       
          ArrayList<String> parents = new ArrayList<String>();
       
         
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
              
                res = st.executeQuery("select * from plan_mstr " +
                        " where plan_item = " + "'" + part + "'" + 
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
                      res.getString("plan_item"), 
                       res.getString("plan_nbr"),
                       res.getString("plan_type"),
                       status,
                       res.getString("plan_date_due"),
                      res.getInt("plan_qty_sched")});
                }
             
              
                
                this.repaint();

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
        
        public void getPurch(String part) {
      
       
         double total = 0.00;
        
          ArrayList<String> parents = new ArrayList<String>();
       
         
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
              
                res = st.executeQuery("select * from pod_mstr " +
                        " where pod_item = " + "'" + part + "'" + 
                        " and pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +
                        " and pod_due_date >= "  + "'" + startdate + "'" + 
                        " and pod_due_date <= "  + "'" + enddate + "'" + 
                        " order by pod_due_date " +   ";");
                while (res.next()) {
                   modelorder.addRow(new Object[]{ 
                      res.getString("pod_item"), 
                       res.getString("pod_nbr"),
                       "PORD",
                       res.getString("pod_status"),
                       res.getString("pod_due_date"),
                      (res.getInt("pod_ord_qty") - res.getInt("pod_rcvd_qty"))});
                }
             
               
                
                this.repaint();

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
         
        public void getrecenttrans(String parentpart) {
             
                 
         tbqtyoh.setText(String.valueOf(invData.getItemQOHTotal(parentpart, OVData.getDefaultSite())));
         tbcost.setText(String.valueOf(invData.getItemCost(parentpart, "standard", OVData.getDefaultSite())));
         tbtype.setText(invData.getItemTypeByPart(parentpart));
                 
                 
       Double opcost = 0.00;
       Double prevcost = 0.00;
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                int i = 0;
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT tr_type, tr_eff_date, tr_id, tr_qty  " +
                        " FROM  tran_mstr  " +
                        " where tr_item = " + "'" + parentpart.toString() + "'" + 
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
        jPanel2.setName("panelmain"); // NOI18N

        btsearch.setText("Search");
        btsearch.setName("btsearch"); // NOI18N
        btsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsearchActionPerformed(evt);
            }
        });

        ddweek.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Week1", "Week2", "Week3", "Week4", "Week5", "Week6", "Week7", "Week8", "Week9", "Week10", "Week11", "Week12", "Week13" }));

        jLabel1.setText("From:");
        jLabel1.setName("lblfromitem"); // NOI18N

        jLabel2.setText("To:");
        jLabel2.setName("lbltoitem"); // NOI18N

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        jLabel3.setText("Site");
        jLabel3.setName("lblsite"); // NOI18N

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
        jLabel4.setName("lbltype"); // NOI18N

        jLabel5.setText("QOH");
        jLabel5.setName("lblqoh"); // NOI18N

        jLabel6.setText("Cost");
        jLabel6.setName("lblcost"); // NOI18N

        rbclassm.setText("Class M");
        rbclassm.setName("cbclassm"); // NOI18N

        rbclassp.setText("Class P");
        rbclassp.setName("cbclassp"); // NOI18N

        rbclassa.setText("Class A");
        rbclassa.setName("cbclassa"); // NOI18N

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
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbfrompart, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbtopart, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btsearch)
                                    .addComponent(tbtype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btdetail)
                                    .addComponent(tbqtyoh, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbcost, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addGap(0, 3, Short.MAX_VALUE)))
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            Statement st2 = con.createStatement();
            ResultSet res2 = null;
            try {
                

                int i = 0;
                int z = 0;
                
                tablereport.setModel(mymodel);
                Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (mymodel.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new MRPBrowse.MainRenderer());
                 }
              tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
              
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
                    res = st.executeQuery("select mrp_item, ifnull(sum(mrp_qty),0) as cumqty  " +
                             " from mrp_mstr where mrp_date >= " + "'" + cumstartdate + "'" + " AND" +
                             " mrp_date <= " + "'" + cumenddate + "'" + " AND" +
                             " mrp_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
                             " mrp_item = " + "'" + item + "'" + "  " +
                             " group by mrp_item ; ");
                    while (res.next()) {
                        cumdemand = res.getDouble("cumqty");
                    }
                    res.close();
               } 
               
               // lets get cumalative PLAN records (if now week 1)
               if (! ddweek.getSelectedItem().equals("Week1")) {
                    res = st.executeQuery("select plan_item, ifnull(sum(plan_qty_sched),0) as cumqty  " +
                             " from plan_mstr where plan_date_due >= " + "'" + cumstartdate + "'" + " AND" +
                             " plan_date_due <= " + "'" + cumenddate + "'" + " AND" +
                             " plan_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
                             " plan_status = '0' AND" +
                             " plan_item = " + "'" + item + "'" +
                             " group by plan_item ; ");
                    while (res.next()) {
                        cumplan = res.getDouble("cumqty");
                    }
                    res.close();
               }
               
                // lets get cumalative PURCHASE/REPLENISHMENT records (if now week 1)
               if (! ddweek.getSelectedItem().equals("Week1")) {
                    res = st.executeQuery("select pod_item, ifnull(sum(pod_ord_qty - pod_rcvd_qty),0) as cumqty  " +
                             " from pod_mstr " +
                             " where pod_due_date >= " + "'" + cumstartdate + "'" + " AND" +
                             " pod_due_date <= " + "'" + cumenddate + "'" + " AND" +
                             " pod_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                             " pod_item = " + "'" + item + "'" +
                             " group by pod_item ; ");
                          while (res.next()) {
                            cumreplenish = res.getDouble("cumqty");
                          }
                          res.close();
               }
               
               
               res = st.executeQuery("select mrp_item,  " +
               " sum(A) as A, sum(B) as B, sum(C) as C, sum(D) as D, sum(E) as E, sum(F) as F, sum(G) as G from " +
               " ( select mrp_item, (case when mrp_date = " + "'" + d1 + "'" + " then mrp_qty else '0' end) as A, " +
               " (case when mrp_date = " + "'" + d2 + "'" + " then mrp_qty else '0' end) as B, " + 
               " (case when mrp_date = " + "'" + d3 + "'" + " then mrp_qty else '0' end) as C, " +
               " (case when mrp_date = " + "'" + d4 + "'" + " then mrp_qty else '0' end) as D, " +
               " (case when mrp_date = " + "'" + d5 + "'" + " then mrp_qty else '0' end) as E, " +
               " (case when mrp_date = " + "'" + d6 + "'" + " then mrp_qty else '0' end) as F, " +
               " (case when mrp_date = " + "'" + d7 + "'" + " then mrp_qty else '0' end) as G " +
               " from mrp_mstr where mrp_date >= " + "'" + startdate + "'" + " AND" +
               " mrp_date <= " + "'" + enddate + "'" + " AND" +
               " mrp_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND" +
               " mrp_item = " + "'" + item + "'" + " ) s " +
                       " group by mrp_item ; ");
               
             
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
                        
                        res2 = st2.executeQuery("select plan_item, (case when in_qoh is null then '0' else in_qoh end) as in_qoh, " +
               " sum(A) as A, sum(B) as B, sum(C) as C, sum(D) as D, sum(E) as E, sum(F) as F, sum(G) as G from " +
               " ( select plan_item, (case when plan_date_due = " + "'" + d1 + "'" + " then plan_qty_sched else '0' end) as A, " +
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
               " plan_item = " + "'" + item + "'" + " ) s " +
               " inner join item_mstr on it_item = plan_item " +
               " left outer join in_mstr on in_item = it_item and in_loc = it_loc " +
                       " group by plan_item ; ");
                   z = 0; 
                   while (res2.next()) {
                       z++;
                        
                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag, res2.getString("plan_item"),
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
               " select pod_item, " +
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
               " ) s on s.pod_item = it_item" +
               " left outer join in_mstr on in_item = it_item and in_loc = it_loc " +
               " where it_item = " + "'" + item + "'" + " group by pod_item ; ");
                    
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
                        BlueSeerUtils.clickflag, res.getString("mrp_item"),
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
               " select pod_item, " +
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
               " ) s on s.pod_item = it_item" +
               " left outer join in_mstr on in_item = it_item and in_loc = it_loc " +
               " where it_item = " + "'" + item + "'" + " group by pod_item ; ");
                    
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
                        BlueSeerUtils.clickflag, res.getString("mrp_item"),
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
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                con.close();
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btsearchActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       PanelDetail.setVisible(false);
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
