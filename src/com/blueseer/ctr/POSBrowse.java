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

package com.blueseer.ctr;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
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
import java.sql.Connection;
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
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.text.DecimalFormatSymbols;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class POSBrowse extends javax.swing.JPanel {
 
    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("print"), 
                            getGlobalColumnTag("number"), 
                            getGlobalColumnTag("date"), 
                            getGlobalColumnTag("time"), 
                            getGlobalColumnTag("amount"), 
                            getGlobalColumnTag("tax"), 
                            getGlobalColumnTag("total"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("void")});
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("id"), 
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("listprice"), 
                            getGlobalColumnTag("discount"), 
                            getGlobalColumnTag("netprice")});
    
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
        
        String status = (String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 8); 
       // int stock = (int)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7);
        
        
        if (status.equals("void")) {
           // c.setBackground(Color.blue);
            c.setForeground(Color.RED);
        } else {
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

    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public POSBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void getdetail(String nbr) {
      
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
                res = st.executeQuery("select posd_nbr, posd_item, posd_qty, posd_listprice, posd_disc, posd_netprice from pos_det " +
                        " where posd_nbr = " + "'" + nbr + "'" +  ";");
                while (res.next()) {
                    totalsales = totalsales + (res.getDouble("posd_qty") * res.getDouble("posd_netprice"));
                    totalqty = totalqty + 1;
                   modeldetail.addRow(new Object[]{ 
                      res.getString("posd_nbr"), 
                      res.getString("posd_item"),
                      res.getString("posd_qty"),
                      res.getString("posd_listprice"),
                      res.getString("posd_disc"), 
                      res.getString("posd_netprice")});
                }
               
               tbdetsales.setText(df.format(totalsales));
               tbdetqty.setText(df.format(totalqty));
               
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
    
    public void voidTransaction(String nbr) {
         boolean isError = true;
         java.util.Date now = new java.util.Date();
        Connection bscon = null;
        try { 
            bscon = DriverManager.getConnection(url + db, user, pass);
            bscon.setAutoCommit(false);
            OVData.voidGLEntryFromPOS(nbr, now, bscon);
            OVData.TRHistIssSalesPOS(nbr, now, true, bscon); 
            OVData.UpdateInventoryFromPOS(nbr, true, bscon);
            OVData.voidPOSStatus(nbr, bscon);
            bscon.commit();
            bsmf.MainFrame.show(getMessageTag(1083));
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             }
        } finally {
            if (bscon != null) {
                try {
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
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
        tbdetqty.setText("0");
        tbtotqty.setText("0");
        tbtotsales.setText("0");
        tbdetsales.setText("0");
        
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
        
               
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
      
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
        
        
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
          
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
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        datelabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbtotsales = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tbtotqty = new javax.swing.JLabel();
        tbdetqty = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        tbdetsales = new javax.swing.JLabel();
        EndBal1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail)
                                .addGap(18, 20, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(datelabel, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)))
                .addGap(430, 430, 430))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(jLabel5))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel8.setText("Total Amount:");
        jLabel8.setName("lbltotalamt"); // NOI18N

        tbtotsales.setText("0");

        jLabel7.setText("Total Records:");
        jLabel7.setName("lbltotalcnt"); // NOI18N

        tbtotqty.setText("0");

        tbdetqty.setBackground(new java.awt.Color(195, 129, 129));
        tbdetqty.setText("0");

        EndBal.setText("Detail LInes:");
        EndBal.setName("lbldetailcnt"); // NOI18N

        tbdetsales.setBackground(new java.awt.Color(195, 129, 129));
        tbdetsales.setText("0");

        EndBal1.setText("Detail Amount:");
        EndBal1.setName("lbldetailamt"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EndBal1)
                    .addComponent(EndBal)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbdetqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotsales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbtotqty, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tbdetsales, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotsales, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndBal)
                    .addComponent(tbdetqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdetsales, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EndBal1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1191, Short.MAX_VALUE)
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
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
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
                 double totqty = 0.00;
                 
            
                // String site = ddsite.getSelectedItem().toString(); 
                                  
             
                 String status = "";
                
               
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
                  
                      //must be type balance sheet
               
                  res = st.executeQuery("select pos_nbr, pos_entrydate, pos_entrytime, pos_grossamt," +
                          " pos_tottax, pos_totamt, pos_status from pos_mstr where " +
                        " pos_entrydate >= " + "'" + fromdate + "'" + " AND " +
                        " pos_entrydate <= " + "'" + todate + "'" +
                        " order by pos_nbr desc;");
                 
                
                       while (res.next()) {
                         if (res.getString("pos_status").compareTo("void") != 0) {  
                         totsales = totsales + res.getDouble("pos_totamt");
                         }
                         totqty = totqty + 1;
                         
                         mymodel.addRow(new Object[]{"Detail", 
                               "Print",
                               res.getString("pos_nbr"),
                                BlueSeerUtils.xNull(res.getString("pos_entrydate")),
                                BlueSeerUtils.xNull(res.getString("pos_entrytime")),
                                df.format(res.getDouble("pos_grossamt")),
                                df.format(res.getDouble("pos_tottax")),
                                df.format(res.getDouble("pos_totamt")),
                                res.getString("pos_status"),
                                "Void"
                            });
                                
                       }
              
                   Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     tc.setCellRenderer(new SomeRenderer());
                 }     
                 
                 
                    tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
          tablereport.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(9).setMaxWidth(100);
          tablereport.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(1).setMaxWidth(100);
                       
                tbtotqty.setText(df.format(totqty));
                tbtotsales.setText(df.format(totsales));
                
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
       tbdetqty.setText("");
       tbdetsales.setText("");
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                getdetail(tablereport.getValueAt(row, 2).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 1) {
                OVData.printPOS_Jasper(tablereport.getValueAt(row, 2).toString());
        }
        if ( col == 9) {
             if (tablereport.getValueAt(row, 8).toString().compareTo("void") == 0) {
                 bsmf.MainFrame.show(getMessageTag(1084));
                 return;
             }
             
             boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
             if (proceed) {
                voidTransaction(tablereport.getValueAt(row, 2).toString());
             }
        }
    }//GEN-LAST:event_tablereportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JLabel EndBal1;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JLabel datelabel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JLabel tbdetqty;
    private javax.swing.JLabel tbdetsales;
    private javax.swing.JLabel tbtotqty;
    private javax.swing.JLabel tbtotsales;
    // End of variables declaration//GEN-END:variables
}
