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

package com.blueseer.ord;

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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
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
public class OrderDetRpt extends javax.swing.JPanel {
 
     MyTableModel mymodel = new OrderDetRpt.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("order"), 
                            getGlobalColumnTag("customer"), 
                            getGlobalColumnTag("po"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("orderqty"), 
                            getGlobalColumnTag("shipqty"), 
                            getGlobalColumnTag("plannbr"), 
                            getGlobalColumnTag("isscheduled"), 
                            getGlobalColumnTag("cell"), 
                            getGlobalColumnTag("schedqty"), 
                            getGlobalColumnTag("scheddate"), 
                            getGlobalColumnTag("planstatus")});
    
     
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
        
         c.setBackground(table.getBackground());
         c.setForeground(table.getForeground());
     
        return c;
    }
    }
        
        
    public OrderDetRpt() {
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
    
    public void initvars(String[] arg) {
        mymodel.setRowCount(0);
         java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
         cbopen.setSelected(true);
         cbclose.setSelected(true);
         cbbackorder.setSelected(true);
         cberror.setSelected(true);
         
         Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -365);
         dcFrom.setDate(calfrom.getTime());
         
         
         ddfromcust.removeAllItems();
         ddtocust.removeAllItems();
         ArrayList mycusts = cusData.getcustmstrlist();
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
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableorder = new javax.swing.JTable();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelqty = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbopen = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        cbclose = new javax.swing.JCheckBox();
        ddfromcust = new javax.swing.JComboBox();
        ddtocust = new javax.swing.JComboBox();
        cbbackorder = new javax.swing.JCheckBox();
        cberror = new javax.swing.JCheckBox();
        tbprint = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("lblqty"); // NOI18N

        jLabel2.setText("From Ord Date");
        jLabel2.setName("lblfromorddate"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Ord Date");
        jLabel3.setName("lbltoorddate"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Cust");
        jLabel1.setName("lblfromcust"); // NOI18N

        jLabel4.setText("To Cust");
        jLabel4.setName("lbltocust"); // NOI18N

        tableorder.setAutoCreateRowSorter(true);
        tableorder.setModel(new javax.swing.table.DefaultTableModel(
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
        tableorder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableorderMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableorder);

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        labelqty.setText("0");

        jLabel8.setText("Qty");

        cbopen.setText("Open");
        cbopen.setName("cbopen"); // NOI18N

        jLabel10.setText("Summarize");
        jLabel10.setName("lblsummarize"); // NOI18N

        cbclose.setText("Close");
        cbclose.setName("cbclose"); // NOI18N

        cbbackorder.setText("BackOrder");
        cbbackorder.setName("cbbackorder"); // NOI18N

        cberror.setText("Error");
        cberror.setName("cberror"); // NOI18N

        tbprint.setText("Print");
        tbprint.setName("btprint"); // NOI18N
        tbprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dcTo, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(dcFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddfromcust, 0, 134, Short.MAX_VALUE)
                    .addComponent(ddtocust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(193, 193, 193)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbprint))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbopen)
                        .addGap(12, 12, 12)
                        .addComponent(cbclose)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbackorder)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cberror)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(ddfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ddtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(tbprint))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbopen)
                                .addComponent(jLabel10)
                                .addComponent(cbclose)
                                .addComponent(cbbackorder)
                                .addComponent(cberror))
                            .addGap(25, 25, 25)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE))
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
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##", new DecimalFormatSymbols(Locale.US));
                int i = 0;
                String fromcust = "";
                String tocust = "";
                String fromcode = "";
                String tocode = "";
                String issched = "";
                String planstatus = "";
                String plannbr = "";
                String plancell = "";
                String planqtysched = "";
                String plandatesched = "";
                
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
               
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
                 mymodel.setNumRows(0);
                   
               
                tableorder.setModel(mymodel);
               // tableorder.getColumnModel().getColumn(0).setCellRenderer(new OrderReport1.SomeRenderer());  
              //  tableorder.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                Enumeration<TableColumn> en = tableorder.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new OrderDetRpt.SomeRenderer());
                 }
                // TableColumnModel tcm = tablescrap.getColumnModel();
               // tcm.getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
             
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                     
                 res = st.executeQuery("SELECT sod_nbr, sod_part, sod_ord_qty, sod_shipped_qty, so_cust, so_po, so_due_date, so_status, " +
                        " it_desc, plan_nbr, plan_is_sched, plan_cell, plan_qty_sched, plan_date_sched, plan_status " +
                        " FROM  so_mstr inner join sod_det on sod_nbr = so_nbr " +
                        " inner join item_mstr on it_item = sod_part " +
                        " left outer join plan_mstr on plan_line = sod_line and plan_order = sod_nbr " + 
                        " where so_ord_date >= " + "'" + dfdate.format(dcFrom.getDate())  + "'" + 
                        " AND so_ord_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND so_cust >= " + "'" + fromcust + "'" + 
                        " AND so_cust <= " + "'" + tocust + "'" + 
                        " AND so_type = 'DISCRETE' " +
                         " order by so_cust, sod_part;");    
                 
                 
                while (res.next()) {
                    if (! cbopen.isSelected() && res.getString("so_status").equals("open"))
                        continue;
                    if (! cbclose.isSelected() && res.getString("so_status").equals("closed"))
                        continue;
                    if (! cbbackorder.isSelected() && res.getString("so_status").equals("backorder"))
                        continue;
                    if (! cberror.isSelected() && res.getString("so_status").equals("error"))
                        continue;                       

                    if (res.getString("plan_nbr") != null) {
                        plannbr = res.getString("plan_nbr");
                        plancell = res.getString("plan_cell");
                        planqtysched = res.getString("plan_qty_sched");
                        plandatesched = res.getString("plan_date_sched");
                    
                    if (! res.getString("plan_is_sched").isEmpty()) {
                        if (res.getString("plan_is_sched").equals("0")) {
                            issched = "no";
                        } else {
                            issched = "yes";
                        }
                    } else {
                        issched = "no";
                    }
                    
                    if (! res.getString("plan_status").isEmpty()) {
                        if (res.getString("plan_status").equals("0")) {
                            planstatus = "open";
                        } else if (res.getString("plan_status").equals("1")) {
                            planstatus = "closed";
                        } else {
                            planstatus = "void";
                        }
                        
                    } else {
                        issched = "none";
                    }
                    
                    } // if not null plan
                    
                    qty = qty + res.getInt("sod_ord_qty");
                    i++;
                        mymodel.addRow(new Object[]{
                                res.getString("sod_nbr"),
                                res.getString("so_cust"),
                                res.getString("so_po"),
                                res.getString("so_due_date"),
                                res.getString("sod_part"),
                                res.getString("it_desc"),
                                res.getInt("sod_ord_qty"),
                                res.getInt("sod_shipped_qty"),
                                plannbr,
                                issched,
                                plancell,
                                planqtysched,
                                plandatesched,
                                planstatus
                                
                            });
                }
                labelcount.setText(String.valueOf(i));
                labelqty.setText(String.valueOf(qty));
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

    private void tableorderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableorderMouseClicked
        int row = tableorder.rowAtPoint(evt.getPoint());
        int col = tableorder.columnAtPoint(evt.getPoint());
        if ( col == 0) {
              if (! checkperms("MenuOrderMaint")) { return; }
              //  bsmf.MainFrame.itemmastmaintpanel.initvars(tablescrap.getValueAt(row, col).toString());
              reinitpanels("MenuOrderMaint", true, new String[]{tableorder.getValueAt(row, col).toString()});
               
               
           
        }
    }//GEN-LAST:event_tableorderMouseClicked

    private void tbprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbprintActionPerformed
       try {
            
                HashMap hm = new HashMap();
                File mytemplate = new File("jasper/orderbrowsedet.jasper");
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tableorder.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ordbrowsedet.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_tbprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JCheckBox cbbackorder;
    private javax.swing.JCheckBox cbclose;
    private javax.swing.JCheckBox cberror;
    private javax.swing.JCheckBox cbopen;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddfromcust;
    private javax.swing.JComboBox ddtocust;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqty;
    private javax.swing.JTable tableorder;
    private javax.swing.JButton tbprint;
    // End of variables declaration//GEN-END:variables
}
