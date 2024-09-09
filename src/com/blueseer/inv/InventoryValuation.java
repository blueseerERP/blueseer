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

package com.blueseer.inv;

import com.blueseer.prd.*;
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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.fgl.fglData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.threeten.bp.LocalDate;



/**
 *
 * @author vaughnte
 */
public class InventoryValuation extends javax.swing.JPanel {
 
     MyTableModel mymodel = new InventoryValuation.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("item"),                             
                            getGlobalColumnTag("description"),
                            getGlobalColumnTag("class"),
                            getGlobalColumnTag("cost"),
                            getGlobalColumnTag("qty"),
                            getGlobalColumnTag("amount")})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 3 || col == 5) 
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }
                      
                      @Override
                      public boolean isCellEditable(int row, int column) {
                            return false;
                            //Only the first column
                            // return column == 1;
                      }
                      
                        };
    
    // tr_id, tr_item, tr_type, tr_qty, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7);  // 7 = status column
        
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
        
        
    public InventoryValuation() {
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
        dcdate.setDate(now);
         
         
        ddclass.removeAllItems();
        ddclass.addItem("");
        ddclass.addItem("A");
        ddclass.addItem("M");
        ddclass.addItem("P");
        ddclass.setSelectedIndex(0);
        
        ArrayList<String> sites = new ArrayList();
        ddsite.removeAllItems();
        sites = OVData.getSiteList(bsmf.MainFrame.userid);
        for (String code : sites) {
            ddsite.addItem(code);
        }
        
        ddfromitem.removeAllItems();
        ddtoitem.removeAllItems();
        ArrayList<String> mycode = invData.getItemMasterAlllist();
        for (int i = 0; i < mycode.size(); i++) {
            ddfromitem.addItem(mycode.get(i));
            ddtoitem.addItem(mycode.get(i));
        }
        ddfromitem.insertItemAt("", 0);
        ddtoitem.insertItemAt("", 0);
        ddfromitem.setSelectedIndex(0);
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
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelamount = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ddfromitem = new javax.swing.JComboBox<>();
        ddtoitem = new javax.swing.JComboBox<>();
        tablepanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        ddsite = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        ddclass = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        tbcsv = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        dcdate = new com.toedter.calendar.JDateChooser();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1211, 539));

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Item");
        jLabel1.setName("lblfromitem"); // NOI18N

        jLabel4.setText("To Item");
        jLabel4.setName("lbltoitem"); // NOI18N

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        labelamount.setText("0");

        jLabel8.setText("Amount");
        jLabel8.setName("lblqty"); // NOI18N

        jLabel2.setText("As of Date:");

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

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
        jScrollPane1.setViewportView(tablereport);

        tablepanel.add(jScrollPane1);

        jLabel3.setText("Site");

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "M", "P" }));

        jLabel6.setText("Class");

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        btprint.setText("Print/PDF");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddtoitem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ddfromitem, 0, 128, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbcsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelcount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelamount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelamount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(ddfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btRun)
                                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(tbcsv)
                                    .addComponent(btprint))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))))))
                .addGap(21, 21, 21)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addContainerGap())
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
        
        String[] td = new String[]{"","","","",""};
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int thisyear = cal.get(Calendar.YEAR);
        int thisperiod = cal.get(Calendar.MONTH) + 1;
        
        
        Date targetdate = dcdate.getDate();
        cal.setTime(targetdate);
        int targetyear = cal.get(Calendar.YEAR);
        int targetperiod = cal.get(Calendar.MONTH) + 1;
        
        // back targetperiod up by 1
        targetperiod = targetperiod - 1;
        if (targetperiod < 1) {
            targetperiod = 12;
            targetyear = targetyear - 1;
        }
        
        
        // if dcdate = today...then reset target period and target year for today
        boolean isToday = false;
        if (setDateDB(targetdate).equals(setDateDB(now))) {
            targetperiod = thisperiod;
            targetyear = thisyear;
            isToday = true;
        } else {
            td = fglData.getGLCalForDate(targetdate); // td[2] = startdate of period
        }
        
        System.out.println("target date: " + targetdate);
        System.out.println("target period: " + targetperiod);
        System.out.println("target year: " + targetyear);
        System.out.println("start date: " + td[2]);
        
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

                double totamt = 0;
                double amt = 0;
                int i = 0;
            
                mymodel.setNumRows(0);
                tablereport.setModel(mymodel);
            
                tablereport.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency()))); 
                tablereport.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency()))); 
                
              
                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
               
                 if (isToday) {
                 res = st.executeQuery("select it_item, it_code, it_desc, itc_total, " + 
                      "case when inb_amt is null then '0' else inb_amt end as qty " +
                      " from item_mstr " +
                      " inner join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                      " left outer join inb_mstr on inb_item = it_item and " +
                      " inb_site = it_site " + 
                      " AND inb_year = " + "'" + targetyear + "'" +
                      " AND inb_per = " + "'" + targetperiod + "'" +
                       " where it_item >= " + "'" + ddfromitem.getSelectedItem().toString() + "'" +  " AND " 
                       + " it_item <= " + "'" + ddtoitem.getSelectedItem().toString() + "'" +  " AND " 
                       + " it_site = " + "'" + ddsite.getSelectedItem().toString() + "'"        
                       + ";" );
                 } else {
                    res = st.executeQuery("select it_item, it_code, it_desc, itc_total, " + 
                      "case when inb_amt is null then '0' else inb_amt end as qty, " +
                      " (select tr_qty from tran_mstr where tr_item = it_item and " +
                            " tr_eff_date >= " + "'" + td[2] + "'" + // td[2] is period start date of targetdate
                            " and " + " tr_eff_date <= " + "'" + setDateDB(targetdate) + "'"  + ") as trqty " +   
                      " from item_mstr " +
                      " inner join item_cost on itc_item = it_item and itc_set = 'standard' and itc_site = it_site " +
                      " left outer join inb_mstr on inb_item = it_item and " +
                      " inb_site = it_site " + 
                      " AND inb_year = " + "'" + targetyear + "'" +
                      " AND inb_per = " + "'" + targetperiod + "'" +
                       " where it_item >= " + "'" + ddfromitem.getSelectedItem().toString() + "'" +  " AND " 
                       + " it_item <= " + "'" + ddtoitem.getSelectedItem().toString() + "'" +  " AND " 
                       + " it_site = " + "'" + ddsite.getSelectedItem().toString() + "'"        
                       + ";" ); 
                 }
                 double actqty = 0;
                while (res.next()) {
                   
                    if (! ddclass.getSelectedItem().toString().isBlank() && ! res.getString("it_code").equals(ddclass.getSelectedItem().toString())) {
                        continue;
                    }
                    if (isToday) {
                      actqty = res.getDouble("qty");
                    } else {
                      actqty = (res.getDouble("qty") + res.getDouble("trqty"));  
                    }
                    amt = (actqty * res.getDouble("itc_total"));
                    totamt += amt;
                    i++;
                        mymodel.addRow(new Object[]{
                                res.getString("it_item"),
                                res.getString("it_desc"),
                                res.getString("it_code"),
                                bsParseDouble(currformatDouble(res.getDouble("itc_total"))),
                                bsNumber(actqty),
                                bsParseDouble(currformatDouble(amt))   
                            });
                }
               
                labelcount.setText(String.valueOf(i));
                labelamount.setText(currformatDouble(totamt));
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

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
        if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper("Inventory Valuation Report", tablereport, "genericJTableL6.jasper" );
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btprint;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox<String> ddclass;
    private javax.swing.JComboBox<String> ddfromitem;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddtoitem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelamount;
    private javax.swing.JLabel labelcount;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbcsv;
    // End of variables declaration//GEN-END:variables
}
