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

package com.blueseer.fap;
import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class APAgingView extends javax.swing.JPanel {
 
    String selectedVendor = "";
    
     MyTableModel modelsummary = new APAgingView.MyTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("vendor"), 
                            getGlobalColumnTag("0daysold"), 
                            getGlobalColumnTag("30daysold"), 
                            getGlobalColumnTag("60daysold"), 
                            getGlobalColumnTag("90daysold"), 
                            getGlobalColumnTag("90+daysold")})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    
    MyTableModel2 modeldetail = new APAgingView.MyTableModel2(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                            getGlobalColumnTag("reference"), 
                            getGlobalColumnTag("po"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("0daysold"), 
                            getGlobalColumnTag("30daysold"), 
                            getGlobalColumnTag("60daysold"), 
                            getGlobalColumnTag("90daysold"), 
                            getGlobalColumnTag("90+daysold")});
    
     MyTableModel3 modelpayment = new APAgingView.MyTableModel3(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                            getGlobalColumnTag("reference"), 
                            getGlobalColumnTag("po"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("checknbr"), 
                            getGlobalColumnTag("voucheramt"), 
                            getGlobalColumnTag("checkamt")});
  
    
    /**
     * Creates new form ScrapReportPanel
     */
    
    
     class MyTableModel3 extends DefaultTableModel {  
      
        public MyTableModel3(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 7 || col == 8 )       
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
    
        
    public APAgingView() {
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
    
    
    public void getdetail(String vend) {
      
         modeldetail.setNumRows(0);
          modelpayment.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        
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
                 res = st.executeQuery("SELECT ap_vend, ap_ref, ap_type, ap_nbr, ap_effdate, ap_duedate, " +
                        " case when ap_duedate > date() then ap_amt else 0 end as '0', " +
                        " case when ap_duedate <= date() and ap_duedate > date() - date(date(), '+30 day') then ap_amt else 0 end as '30', " +
                        " case when ap_duedate <= date() - date(date(), '+30 day') and ap_duedate > date(date(), '+60 day') then ap_amt else 0 end as '60', " +
                        " case when ap_duedate <= date() - date(date(), '+60 day') and ap_duedate > date(date(), '+90 day') then ap_amt else 0 end as '90', " +
                        " case when ap_duedate <= date() - date(date(), '+90 day') then ap_amt else 0 end as '90p' " +
                        " FROM  ap_mstr " +
                        " where ap_vend = " + "'" + vend + "'" + 
                        " AND ap_status = 'o' " +
                        " AND ap_type = 'V' " +
                         " order by ap_vend, ap_nbr ;");   
                 } else {
                   res = st.executeQuery("SELECT ap_vend, ap_ref, ap_type, ap_nbr, ap_effdate, ap_duedate, " +
                        " case when ap_duedate > curdate() then ap_amt else 0 end as '0', " +
                        " case when ap_duedate <= curdate() and ap_duedate > curdate() - interval 30 day then ap_amt else 0 end as '30', " +
                        " case when ap_duedate <= curdate() - interval 30 day and ap_duedate > curdate() - interval 60 day then ap_amt else 0 end as '60', " +
                        " case when ap_duedate <= curdate() - interval 60 day and ap_duedate > curdate() - interval 90 day then ap_amt else 0 end as '90', " +
                        " case when ap_duedate <= curdate() - interval 90 day then ap_amt else 0 end as '90p' " +
                        " FROM  ap_mstr " +
                        " where ap_vend = " + "'" + vend + "'" + 
                        " AND ap_status = 'o' " +
                        " AND ap_type = 'V' " +
                         " order by ap_vend, ap_nbr ;");     
                 }
                 
                 
                String ponbr = ""; 
                while (res.next()) {
                    dol = dol + (res.getDouble("0") + res.getDouble("30") + res.getDouble("60") + res.getDouble("90") + res.getDouble("90p") );
                    qty = qty + 0;
                    i++;
                    ponbr = getPOorMix(res.getString("ap_nbr"));
                        modeldetail.addRow(new Object[]{
                            res.getString("ap_nbr"),
                            res.getString("ap_ref"),
                            ponbr,
                            res.getString("ap_effdate"),
                            res.getString("ap_duedate"),
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
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void getpayment(String vend) {
      
         modelpayment.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        
          tablepayment.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
          tablepayment.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
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
                
                 res = st.executeQuery("SELECT ap_vend, apd_nbr, ap_ref, apd_ref, ap_type, ap_nbr, ap_check, ap_effdate, ap_duedate, ap_amt, apd_voamt " +
                        " FROM  ap_mstr " +
                        " inner join apd_mstr on apd_batch = ap_batch " +
                        " where ap_vend = " + "'" + vend + "'" + 
                        " AND ap_type = 'C' " +
                        " AND ap_effdate >= date() - date(date(), '-90 day') " +
                         " order by ap_effdate desc ;");        
                 
                String ponbr = ""; 
                while (res.next()) {
                    dol = dol + (res.getDouble("ap_amt"));
                    qty = qty + 0;
                    i++;
                    ponbr = getPOorMix(res.getString("apd_nbr"));
                        modelpayment.addRow(new Object[]{
                            res.getString("apd_nbr"),
                            res.getString("apd_ref"),
                            ponbr,
                            res.getString("ap_effdate"),
                            res.getString("ap_duedate"),
                            res.getString("ap_type"),
                            res.getString("ap_check"),
                            Double.valueOf(df.format(res.getDouble("apd_voamt"))),
                            Double.valueOf(df.format(res.getDouble("ap_amt")))
                            });
                   }
               
               
                
                tablepayment.setModel(modelpayment);
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
     
    public String getPOorMix(String vonbr) {
          String ponbr = "";
          try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
               
                // using group by clause here...if only one record returned...then must be only one PO for all lines...else must be mixed PO voucher/receipt
                 res = st.executeQuery("SELECT rvd_po  " +
                        " FROM  ap_mstr  " +
                        " inner join vod_mstr on vod_id = ap_nbr " +
                        " inner join recv_det on rvd_id = vod_rvdid " +
                        " where ap_nbr = " + "'" + vonbr + "'" + 
                         " group by rvd_po ;");        
                 
                 
                while (res.next()) {
                    ponbr = res.getString("rvd_po");
                    i++;
                   }
                if (i > 1) {
                    ponbr = "mix";
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          
          return ponbr;
      }
      
      
      
      
      
    public void initvars(String[] arg) {
        modelsummary.setRowCount(0);
        java.util.Date now = new java.util.Date();
       
         
          modelsummary.setNumRows(0);
        modeldetail.setNumRows(0);
        modelpayment.setNumRows(0);
        tablesummary.setModel(modelsummary);
        tabledetail.setModel(modeldetail);
        tablepayment.setModel(modelpayment);
        
      //   tablesummary.getColumnModel().getColumn(0).setCellRenderer(new APAgingView.ButtonRenderer());
         tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
         
         detailpanel.setVisible(false);
         btdetail.setEnabled(false);
         cbpaymentpanel.setEnabled(false);
         cbpaymentpanel.setSelected(false);
         paymentpanel.setVisible(false);
        
        ddfromvend.removeAllItems();
        ddtovend.removeAllItems();
        ArrayList vends = OVData.getvendmstrlist();
        for (int i = 0; i < vends.size(); i++) {
            ddfromvend.addItem(vends.get(i));
        }
        for (int i = 0; i < vends.size(); i++) {
            ddtovend.addItem(vends.get(i));
        } 
        ddtovend.setSelectedIndex(ddtovend.getItemCount() - 1);
         
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
        ddfromvend = new javax.swing.JComboBox();
        ddtovend = new javax.swing.JComboBox();
        cbpaymentpanel = new javax.swing.JCheckBox();
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

        jPanel1.setName("panelmain"); // NOI18N

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        jLabel8.setText("$$");
        jLabel8.setName("lblamt"); // NOI18N

        labeldollar.setText("0");

        jLabel3.setText("To Vend");
        jLabel3.setName("lbltovend"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel2.setText("From Vend");
        jLabel2.setName("lblfromvend"); // NOI18N

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        cbpaymentpanel.setText("PaymentPanel");
        cbpaymentpanel.setName("cbpayments"); // NOI18N
        cbpaymentpanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbpaymentpanelActionPerformed(evt);
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
                    .addComponent(ddfromvend, 0, 169, Short.MAX_VALUE)
                    .addComponent(ddtovend, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addGap(18, 18, 18)
                .addComponent(cbpaymentpanel)
                .addGap(354, 354, 354))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(cbpaymentpanel))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddfromvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtovend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 381, Short.MAX_VALUE)
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
                
                DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
                int i = 0;
               
                tablesummary.setModel(modelsummary);
                 
             //   Enumeration<TableColumn> en = tablelabel.getColumnModel().getColumns();
              //   while (en.hasMoreElements()) {
             //        TableColumn tc = en.nextElement();
             //        tc.setCellRenderer(new LabelBrowsePanel.SomeRenderer());
             //    }
                //  tablesummary.getColumnModel().getColumn(0).setCellRenderer(new APAgingView.ButtonRenderer());
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
                String fromvend = "";
                String tovend = "";
                
                if (ddfromvend.getSelectedItem().toString().isEmpty()) {
                    fromvend = bsmf.MainFrame.lowchar;
                } else {
                    fromvend = ddfromvend.getSelectedItem().toString();
                }
                 if (ddtovend.getSelectedItem().toString().isEmpty()) {
                    tovend = bsmf.MainFrame.hichar;
                } else {
                    tovend = ddtovend.getSelectedItem().toString();
                }
                
                 ArrayList vends = OVData.getvendmstrlistBetween(fromvend, tovend);
                 
                
                 for (int j = 0; j < vends.size(); j++) {
                  
                      // init for new vend
                      i = 0;
                     
                 if (bsmf.MainFrame.dbtype.equals("sqlite")) {
                     res = st.executeQuery("SELECT ap_vend, " +
                        " sum(case when ap_duedate > date() then ap_amt else 0 end) as '0', " +
                        " sum(case when ap_duedate <= date() and ap_duedate > date() - date(date(), '+30 day') then ap_amt else 0 end) as '30', " +
                        " sum(case when ap_duedate <= date() - date(date(), '+30 day') and ap_duedate > date(date(), '+60 day') then ap_amt else 0 end) as '60', " +
                        " sum(case when ap_duedate <= date() - date(date(), '+60 day') and ap_duedate > date(date(), '+90 day') then ap_amt else 0 end) as '90', " +
                        " sum(case when ap_duedate <= date() - date(date(), '+90 day') then ap_amt else 0 end) as '90p' " +
                        " FROM  ap_mstr " +
                        " where ap_vend = " + "'" + vends.get(j) + "'" + 
                        " AND ap_status = 'o' " +
                         " group by ap_vend order by ap_vend;");
                 }  else {
                 res = st.executeQuery("SELECT ap_vend, " +
                        " sum(case when ap_duedate > curdate() then ap_amt else 0 end) as '0', " +
                        " sum(case when ap_duedate <= curdate() and ap_duedate > curdate() - interval 30 day then ap_amt else 0 end) as '30', " +
                        " sum(case when ap_duedate <= curdate() - interval 30 day and ap_duedate > curdate() - interval 60 day then ap_amt else 0 end) as '60', " +
                        " sum(case when ap_duedate <= curdate() - interval 60 day and ap_duedate > curdate() - interval 90 day then ap_amt else 0 end) as '90', " +
                        " sum(case when ap_duedate <= curdate() - interval 90 day then ap_amt else 0 end) as '90p' " +
                        " FROM  ap_mstr " +
                        " where ap_vend = " + "'" + vends.get(j) + "'" + 
                        " AND ap_status = 'o' " +
                         " group by ap_vend order by ap_vend;");
                 }
                  while (res.next()) {
                    dol = dol + (res.getDouble("0") + res.getDouble("30") + res.getDouble("60") + res.getDouble("90") + res.getDouble("90p") );
                    qty = qty + 0;
                    i++;
                        modelsummary.addRow(new Object[]{
                                BlueSeerUtils.clickbasket,
                                res.getString("ap_vend"),
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
                                vends.get(j),
                                0,0,0,0,0
                            });
                  }
                
            }  //for each vendor in range    
               
                labelcount.setText(String.valueOf(i));
                labeldollar.setText(String.valueOf(df.format(dol)));
            } catch (SQLException s) {
                MainFrame.bslog(s); 
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        selectedVendor = tablesummary.getValueAt(row, 1).toString();
        
        if ( col == 0) {
            getdetail(tablesummary.getValueAt(row, 1).toString());
            btdetail.setEnabled(true);
            detailpanel.setVisible(true);
            paymentpanel.setVisible(false);
            cbpaymentpanel.setSelected(false);
        }
       
    }//GEN-LAST:event_tablesummaryMouseClicked

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
        detailpanel.setVisible(false);
        paymentpanel.setVisible(false);
        cbpaymentpanel.setSelected(false);
        btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void cbpaymentpanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbpaymentpanelActionPerformed
       if (cbpaymentpanel.isSelected()) {
           if (! selectedVendor.isEmpty()) {
           getpayment(selectedVendor);
            paymentpanel.setVisible(true);
           }
       } else {
           paymentpanel.setVisible(false);
       }
    }//GEN-LAST:event_cbpaymentpanelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JCheckBox cbpaymentpanel;
    private javax.swing.JComboBox ddfromvend;
    private javax.swing.JComboBox ddtovend;
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
