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

package com.blueseer.inv; 

import bsmf.MainFrame;
import com.blueseer.ord.*;
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
import javax.swing.ImageIcon;

/**
 *
 * @author vaughnte
 */
public class BOMBrowse extends javax.swing.JPanel {
 
     MyTableModel mymodel = new BOMBrowse.MyTableModel(new Object[][]{},
                        new String[]{"Select", "Parent", "Desc", "Operation", "Child", "QtyPer", "Ref"})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
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
     
     
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
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
        return c;
    }
    }
        
        
    public BOMBrowse() {
        initComponents();
    }

    public void initvars(String[] arg) {
        mymodel.setRowCount(0);
         java.util.Date now = new java.util.Date();
       
         
         tableorder.getTableHeader().setReorderingAllowed(false);
         
        
         
          ddfromitem.removeAllItems();
         ddtoitem.removeAllItems(); 
         ArrayList items = OVData.getItemMasterAlllist();
        for (int i = 0; i < items.size(); i++) {
            ddfromitem.addItem(items.get(i));
        }
        for (int i = 0; i < items.size(); i++) {
            ddtoitem.addItem(items.get(i));
        } 
        ddtoitem.setSelectedIndex(ddtoitem.getItemCount() - 1);
      
        
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableorder = new javax.swing.JTable();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddfromitem = new javax.swing.JComboBox();
        ddtoitem = new javax.swing.JComboBox();
        tbprint = new javax.swing.JButton();
        btcsv = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Item");

        jLabel4.setText("To Item");

        tableorder.setAutoCreateRowSorter(true);
        tableorder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableorder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableorderMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableorder);

        labelcount.setText("0");

        jLabel7.setText("Records");

        tbprint.setText("PDF");
        tbprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbprintActionPerformed(evt);
            }
        });

        btcsv.setText("CSV");
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddfromitem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbprint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 711, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel7)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(tbprint)
                        .addComponent(btcsv))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (tableorder != null && mymodel.getRowCount() > 0)
        OVData.exportCSV(tableorder);
    }//GEN-LAST:event_btcsvActionPerformed

    private void tbprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbprintActionPerformed

        if (tableorder != null && mymodel.getRowCount() > 0) {
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
                    File mytemplate = new File("jasper/bombrowse.jasper");
                    //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );

                    JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tableorder.getModel()) );
                    JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/bombrowse.pdf");

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
    }//GEN-LAST:event_tbprintActionPerformed

    private void tableorderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableorderMouseClicked
        int row = tableorder.rowAtPoint(evt.getPoint());
        int col = tableorder.columnAtPoint(evt.getPoint());
        if ( col == 0) {
            if (! checkperms("BOMMaint")) { return; }
            //  bsmf.MainFrame.itemmastmaintpanel.initvars(tablescrap.getValueAt(row, col).toString());
            reinitpanels("BOMMaint",  true, new String[] {tableorder.getValueAt(row, 1).toString()});
        }
    }//GEN-LAST:event_tableorderMouseClicked

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

              
                int i = 0;
                String fromitem = "";
                String toitem = "";
                

                if (ddfromitem.getSelectedItem() == null || ddfromitem.getSelectedItem().toString().isEmpty()) {
                    fromitem = bsmf.MainFrame.lowchar;
                } else {
                    fromitem = ddfromitem.getSelectedItem().toString();
                }
                if (ddtoitem.getSelectedItem() == null || ddtoitem.getSelectedItem().toString().isEmpty()) {
                    toitem = bsmf.MainFrame.hichar;
                } else {
                    toitem = ddtoitem.getSelectedItem().toString();
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
                    if (tc.getIdentifier().toString().equals("Select") ) {
                        continue;
                    }
                    tc.setCellRenderer(new BOMBrowse.SomeRenderer());
                }
                // TableColumnModel tcm = tablescrap.getColumnModel();
                // tcm.getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());

                //   tableorder.getColumnModel().getColumn(0).setCellRenderer(new OrderReport1.ButtonRenderer());
                tableorder.getColumnModel().getColumn(0).setMaxWidth(100);

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                res = st.executeQuery("SELECT ps_parent, it_desc, ps_op, ps_child, ps_qty_per, ps_ref " +
                    " FROM  pbm_mstr inner join item_mstr on it_item = ps_parent " +
                    " where ps_parent >= " + "'" + fromitem + "'" +
                    " AND ps_parent <= " + "'" + toitem + "'" +
                    " order by ps_parent, ps_op ;");

                while (res.next()) {
                   
                    i++;
                    mymodel.addRow(new Object[]{
                        BlueSeerUtils.clickflag,
                        res.getString("ps_parent"),
                        res.getString("it_desc"),
                        res.getString("ps_op"),
                        res.getString("ps_child"),
                        res.getString("ps_qty_per"),
                        res.getString("ps_ref")
                    });
                }
               
                labelcount.setText(String.valueOf(i));
               
            } catch (SQLException s) {
                bsmf.MainFrame.show("unable to select pbm_mstr");
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }//GEN-LAST:event_btRunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JComboBox ddfromitem;
    private javax.swing.JComboBox ddtoitem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JTable tableorder;
    private javax.swing.JButton tbprint;
    // End of variables declaration//GEN-END:variables
}
