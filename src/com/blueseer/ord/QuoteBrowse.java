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

import com.blueseer.shp.*;
import com.blueseer.far.*;
import com.blueseer.shp.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class QuoteBrowse extends javax.swing.JPanel {
 
    boolean sending = false;
    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("number"), 
                            getGlobalColumnTag("customer"), 
                            getGlobalColumnTag("date"), 
                            getGlobalColumnTag("expire"), 
                            getGlobalColumnTag("status"), 
                            getGlobalColumnTag("qty"), 
                            getGlobalColumnTag("amount")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 1)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("number"), 
                            getGlobalColumnTag("line"),
                            getGlobalColumnTag("item"), 
                            getGlobalColumnTag("listprice"), 
                            getGlobalColumnTag("disc"), 
                            getGlobalColumnTag("netprice"),
                            getGlobalColumnTag("qty")});
    
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
        
            boolean issched = (Boolean) tablereport.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
            if (( column == 5 || column == 6) && ! issched ) {
            c.setBackground(Color.green);
            c.setForeground(Color.BLACK);
            
            }
            else {
                c.setBackground(table.getBackground());
            }
            return c;
    }
    }
    
    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public QuoteBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void executeTask(String x, int y, String w) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String key = "";
          int row = 0;
          String site = "";
          
          public Task(String key, int row, String site) { 
              this.key = key;
              this.row = row;
              this.site = site;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message = OVData.sendInvoice(key, site); 
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
            BlueSeerUtils.endTask(message);
            sending = false;
            tablereport.getModel().setValueAt(BlueSeerUtils.clickmail,row,11);
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y, w); 
       z.execute(); 
       
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
    
    public void getdetail(String nbr) {
      
         modeldetail.setNumRows(0);
         double totalsales = 0;
         double totalqty = 0;
         
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null; try {
                
                int i = 0;
                String blanket = "";
                res = st.executeQuery("select quod_nbr, quod_line, quod_item, quod_listprice, quod_disc, quod_netprice, quod_qty from quo_det " +
                        " where quod_nbr = " + "'" + nbr + "'" +  ";");
                while (res.next()) {
                    totalsales = totalsales + (res.getDouble("quod_qty") * res.getDouble("quod_netprice"));
                    totalqty = totalqty + res.getDouble("quod_qty");
                   modeldetail.addRow(new Object[]{ 
                      res.getString("quod_nbr"), 
                      res.getString("quod_line"),
                      res.getString("quod_item"),
                      currformatDouble(res.getDouble("quod_listprice")),
                      res.getString("quod_disc"), 
                      currformatDouble(res.getDouble("quod_netprice")),
                      res.getString("quod_qty")
                   });
                }
               
               tbdetsales.setText(currformatDouble(totalsales));
               tbdetqty.setText(currformatDouble(totalqty));
               
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
        
        tabledetail.setModel(modeldetail);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        tabledetail.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        tabledetail.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
           
        
        tablereport.setModel(mymodel);
        tablereport.getTableHeader().setReorderingAllowed(false);
        tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
      
        tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        tablereport.getColumnModel().getColumn(1).setMaxWidth(100);
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
        jLabel4 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        datelabel = new javax.swing.JLabel();
        tbfromnbr = new javax.swing.JTextField();
        tbtonbr = new javax.swing.JTextField();
        tbfromcust = new javax.swing.JTextField();
        tbtocust = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        tbcsv = new javax.swing.JButton();
        cbactive = new javax.swing.JCheckBox();
        btprint = new javax.swing.JButton();
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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Quote Browse"));
        jPanel1.setName("panelmain"); // NOI18N

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

        jLabel4.setText("To Billto:");
        jLabel4.setName("lbltocust"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Billto:");
        jLabel1.setName("lblfromcust"); // NOI18N

        jLabel3.setText("To Quote:");
        jLabel3.setName("lbltoshipper"); // NOI18N

        jLabel2.setText("From Quote:");
        jLabel2.setName("lblfromshipper"); // NOI18N

        jLabel5.setText("From Date:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To Date:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        cbactive.setText("Active Only");
        cbactive.setName("cbinvoiceonly"); // NOI18N

        btprint.setText("Print/PDF");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(datelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(267, 267, 267))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbtonbr, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbfromnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtocust, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbactive)
                        .addGap(52, 52, 52))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdetail)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btprint)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(tbfromnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(tbcsv)
                        .addComponent(btprint))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(tbtonbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(cbactive))
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );

        jLabel8.setText("Total Sales:");
        jLabel8.setName("lbltotalsales"); // NOI18N

        tbtotsales.setText("0");

        jLabel7.setText("Total Qty:");
        jLabel7.setName("lbltotalqty"); // NOI18N

        tbtotqty.setText("0");

        tbdetqty.setBackground(new java.awt.Color(195, 129, 129));
        tbdetqty.setText("0");

        EndBal.setText("Detail Qty:");
        EndBal.setName("lbldetailqty"); // NOI18N

        tbdetsales.setBackground(new java.awt.Color(195, 129, 129));
        tbdetsales.setText("0");

        EndBal1.setText("Detail Sales:");
        EndBal1.setName("lbldetailsales"); // NOI18N

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
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1179, Short.MAX_VALUE)
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
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
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
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                String fromdate = "";
                String todate = "";
                mymodel.setNumRows(0);
                 
              //  tablereport.getColumnModel().getColumn(10).setCellRenderer(new InvoiceBrowsePanel.SomeRenderer());
                
                 double totsales = 0;
                 double totqty = 0;
                 
            
                // String site = ddsite.getSelectedItem().toString(); 
                                  
                 String nbrfrom = tbfromnbr.getText();
                 String nbrto = tbtonbr.getText();
                 String custto = tbtocust.getText();
                 String custfrom = tbfromcust.getText();
                 String status = "";
                
                 
                 if (nbrfrom.isEmpty()) {
                     nbrfrom = "0";
                 }
                 if (nbrto.isEmpty()) {
                     nbrto = "ZZZZZZZZ";
                 }
                 if (custfrom.isEmpty()) {
                     custfrom = "0";
                 }
                 if (custto.isEmpty()) {
                     custto = "ZZZZZZZZ";
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
                
                      //must be type balance sheet
                 if (cbactive.isSelected()) {
                  res = st.executeQuery("select quo_nbr, quo_status, quo_cust, quo_date, quo_expire, sum(quod_qty) as 'qty', sum(quod_qty * quod_netprice) as 'price' from quo_mstr " +
                        " inner join quo_det on quod_nbr = quo_nbr where " +
                        " quo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " quo_nbr <= " + "'" + nbrto + "'" + " AND " +
                        " quo_date >= " + "'" + fromdate + "'" + " AND " +
                        " quo_date <= " + "'" + todate + "'" + " AND " +
                        " quo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " quo_cust <= " + "'" + custto + "'" + " AND " +
                        " quo_status <> 'closed' " +
                        " group by quo_nbr, quo_status, quo_cust, quo_date, quo_expire;");
                 } else {
                    res = st.executeQuery("select quo_nbr, quo_status, quo_cust, quo_date, quo_expire, sum(quod_qty) as 'qty', sum(quod_qty * quod_netprice) as 'price' from quo_mstr " +
                        " inner join quo_det on quod_nbr = quo_nbr where " +
                        " quo_nbr >= " + "'" + nbrfrom + "'" + " AND " +
                        " quo_nbr <= " + "'" + nbrto + "'" + " AND " +
                        " quo_date >= " + "'" + fromdate + "'" + " AND " +
                        " quo_date <= " + "'" + todate + "'" + " AND " +
                        " quo_cust >= " + "'" + custfrom + "'" + " AND " +
                        " quo_cust <= " + "'" + custto + "'" + 
                        " group by quo_nbr, quo_status, quo_cust, quo_date, quo_expire;"); 
                 }
                
                       while (res.next()) {
                         totsales = totsales + res.getDouble("price");
                         totqty = totqty + res.getDouble("qty");
                         
                         mymodel.addRow(new Object[]{BlueSeerUtils.clickflag, BlueSeerUtils.clickbasket, 
                               res.getString("quo_nbr"),
                                res.getString("quo_cust"),
                                BlueSeerUtils.xNull(res.getString("quo_date")),
                                BlueSeerUtils.xNull(res.getString("quo_expire")),
                                res.getString("quo_status"),
                                res.getString("qty"),
                                currformatDouble(res.getDouble("price"))
                            });
                                
                       }
              
                tbtotqty.setText(currformatDouble(totqty));
                tbtotsales.setText(currformatDouble(totsales));
                
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

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       tbdetqty.setText("");
       tbdetsales.setText("");
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 1) {
                getdetail(tablereport.getValueAt(row, 2).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 0) {
                String mypanel = "QuoteMaint";
               if (! checkperms(mypanel)) { return; }
               String[] args = new String[]{tablereport.getValueAt(row, 2).toString()};
               reinitpanels(mypanel, true, args);
        }
        if (col == 10) {
            OVData.printQuote(tablereport.getValueAt(row, 2).toString());  
        }
       
    }//GEN-LAST:event_tablereportMouseClicked

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
      if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper("Quote Report", tablereport, "genericJTableL7.jasper" );
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JLabel EndBal1;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbactive;
    private javax.swing.JLabel datelabel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
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
    private javax.swing.JButton tbcsv;
    private javax.swing.JLabel tbdetqty;
    private javax.swing.JLabel tbdetsales;
    private javax.swing.JTextField tbfromcust;
    private javax.swing.JTextField tbfromnbr;
    private javax.swing.JTextField tbtocust;
    private javax.swing.JTextField tbtonbr;
    private javax.swing.JLabel tbtotqty;
    private javax.swing.JLabel tbtotsales;
    // End of variables declaration//GEN-END:variables
}
