/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.ord;

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
public class OrderReport1 extends javax.swing.JPanel {
 
     MyTableModel mymodel = new OrderReport1.MyTableModel(new Object[][]{},
                        new String[]{"Select", "Order", "Cust", "PO", "Remarks", "OrdDate", "DueDate", "Qty", "Amount", "Status"})
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
          public Class getColumnClass(int col) {  
            if (col == 7)       
                return Integer.class;  
            else if (col == 8)
                return Double.class;
            else return String.class;  //other columns accept String values  
          
// return String.class;
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 9);  // 8 = status column
        
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
        
        
    public OrderReport1() {
        initComponents();
    }

    public void initvars(String arg) {
        mymodel.setRowCount(0);
         java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
         cbopen.setSelected(true);
         cbclose.setSelected(true);
         cbbackorder.setSelected(true);
         cberror.setSelected(true);
         
         tableorder.getTableHeader().setReorderingAllowed(false);
         
          Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -365);
         dcFrom.setDate(calfrom.getTime());
         
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
        labeldollar = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cbopen = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        cbclose = new javax.swing.JCheckBox();
        ddfromcust = new javax.swing.JComboBox();
        ddtocust = new javax.swing.JComboBox();
        cbbackorder = new javax.swing.JCheckBox();
        cberror = new javax.swing.JCheckBox();
        tbprint = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        btcsv = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jLabel2.setText("From Ord Date");

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Ord Date");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Cust");

        jLabel4.setText("To Cust");

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

        jLabel7.setText("Count");

        labelqty.setText("0");

        jLabel8.setText("Qty");

        labeldollar.setBackground(new java.awt.Color(195, 129, 129));
        labeldollar.setText("0");

        jLabel9.setText("$");

        cbopen.setText("Open");

        jLabel10.setText("Summarize");

        cbclose.setText("Close");

        cbbackorder.setText("BackOrder");

        cberror.setText("Error");

        tbprint.setText("PDF");
        tbprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbprintActionPerformed(evt);
            }
        });

        jLabel5.setText("Site:");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbprint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcsv))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btRun)
                                .addComponent(tbprint)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(btcsv))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbopen)
                                .addComponent(jLabel10)
                                .addComponent(cbclose)
                                .addComponent(cbbackorder)
                                .addComponent(cberror))
                            .addGap(31, 31, 31)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(6, 6, 6)))
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
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("###,###,###.##");
                int i = 0;
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
                     if (tc.getIdentifier().toString().equals("Select") || 
                             tc.getIdentifier().toString().equals("Print") ) {
                         continue;
                     }
                     tc.setCellRenderer(new OrderReport1.SomeRenderer());
                 }
                // TableColumnModel tcm = tablescrap.getColumnModel();
               // tcm.getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
             
                 //   tableorder.getColumnModel().getColumn(0).setCellRenderer(new OrderReport1.ButtonRenderer());
                tableorder.getColumnModel().getColumn(0).setMaxWidth(100);
                 
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                     
                 res = st.executeQuery("SELECT so_nbr, so_rmks, so_cust, so_po, so_ord_date, so_due_date, so_status, " +
                        " sum(sod_ord_qty) as totqty, sum(sod_ord_qty * sod_netprice) as totdol " +
                        " FROM  so_mstr left outer join sod_det on sod_nbr = so_nbr " +
                        " where so_ord_date >= " + "'" + dfdate.format(dcFrom.getDate())  + "'" + 
                        " AND so_ord_date <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " AND so_cust >= " + "'" + fromcust + "'" + 
                        " AND so_cust <= " + "'" + tocust + "'" + 
                        " AND so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + 
                        " AND so_type = 'DISCRETE' " +
                         " group by so_nbr order by so_nbr desc ;");    
                 
                 
                while (res.next()) {
                    if (! cbopen.isSelected() && res.getString("so_status").equals("open"))
                        continue;
                    if (! cbclose.isSelected() && res.getString("so_status").equals("close"))
                        continue;
                    if (! cbbackorder.isSelected() && res.getString("so_status").equals("backorder"))
                        continue;
                    if (! cberror.isSelected() && res.getString("so_status").equals("error"))
                        continue;                       

                    
                    dol = dol + res.getDouble("totdol");
                    qty = qty + res.getInt("totqty");
                    i++;
                        mymodel.addRow(new Object[]{
                            BlueSeerUtils.clickflag,
                                res.getString("so_nbr"),
                                res.getString("so_cust"),
                                res.getString("so_po"),
                                res.getString("so_rmks"),
                                res.getString("so_ord_date"),
                                res.getString("so_due_date"),
                                res.getInt("totqty"),
                                res.getDouble("totdol"),
                                res.getString("so_status")
                            });
                }
                labeldollar.setText(String.valueOf(df.format(dol)));
                labelcount.setText(String.valueOf(i));
                labelqty.setText(String.valueOf(qty));
            } catch (SQLException s) {
                bsmf.MainFrame.show("Cannot execute sql query for Order Report");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void tableorderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableorderMouseClicked
        int row = tableorder.rowAtPoint(evt.getPoint());
        int col = tableorder.columnAtPoint(evt.getPoint());
        if ( col == 0) {
              if (! checkperms("MenuOrderMaint")) { return; }
              //  bsmf.MainFrame.itemmastmaintpanel.initvars(tablescrap.getValueAt(row, col).toString());
              reinitpanels("MenuOrderMaint",  true, tableorder.getValueAt(row, 1).toString());
        }
    }//GEN-LAST:event_tableorderMouseClicked

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
                File mytemplate = new File("jasper/orderbrowsesumary.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tableorder.getModel()) );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ordbrowse.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Could not create jasperfile...see stacktrace");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_tbprintActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
       if (tableorder != null && mymodel.getRowCount() > 0)
        OVData.exportCSV(tableorder);
    }//GEN-LAST:event_btcsvActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JCheckBox cbbackorder;
    private javax.swing.JCheckBox cbclose;
    private javax.swing.JCheckBox cberror;
    private javax.swing.JCheckBox cbopen;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddfromcust;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtocust;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JLabel labelqty;
    private javax.swing.JTable tableorder;
    private javax.swing.JButton tbprint;
    // End of variables declaration//GEN-END:variables
}
