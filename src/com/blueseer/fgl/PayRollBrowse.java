/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.rcv.*;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
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
import java.util.HashMap;
import java.util.Map;
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
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import javax.swing.ImageIcon;

/**
 *
 * @author vaughnte
 */
public class PayRollBrowse extends javax.swing.JPanel {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     //pyd_empnbr, pyd_emplname, pyd_empfname, pyd_empdept, pyd_emptype, pyd_paydate, pyd_checknbr, pyd_payamt
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Detail", "EmpNbr", "LastName", "FirstName", "Dept", "Type", "PayDate", "CheckNbr", "GrossAmt", "Deduct", "NetAmt"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"RecordID", "indate", "outdate", "intime", "outtime", "code_id", "code_orig", "tothours"});
    
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
    
   

    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public PayRollBrowse() {
        initComponents();
    }

     public void getdetail(String empnbr, String checknbr) {
      
         modeldetail.setNumRows(0);
         double total = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                //"RecordID", "indate", "outdate", "intime", "outtime", "code_id", "code_orig", "tothours"
                res = st.executeQuery("select indate, outdate, intime, outtime, code_id, tothrs, recid, code_orig " +
                        " from time_clock " +
                        " where checknbr = " + "'" + checknbr + "'" +
                        " and emp_nbr = " + "'" + empnbr + "'" + ";");
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      res.getString("recid"), 
                       res.getString("indate"),
                       res.getString("outdate"),
                       res.getString("intime"),
                       res.getString("outtime"),
                       res.getString("code_id"),
                      res.getString("code_orig"), 
                      res.getDouble("tothrs")});
                }
               
              
                tabledetail.setModel(modeldetail);
               //  tabledetail.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
                this.repaint();

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to get Recv Browse detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public void initvars(String arg) {
     
        tbtotal.setText("0");
        tbcount.setText("0");
        
        cbsalary.setSelected(true);
        cbhourly.setSelected(true);
        
        java.util.Date now = new java.util.Date();
         dcFrom.setDate(now);
         dcTo.setDate(now);
        
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
           tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
           tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
           tablereport.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
           tablereport.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
         
       
          
         
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
        
        
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        
        ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList();
        for (Object site : sites) {
            ddsite.addItem(site);
        }
        
        ddempfrom.removeAllItems();
        ArrayList emps = OVData.getempmstrlist();
        for (Object emp : emps) {
            ddempfrom.addItem(emp);
        }
        
        ddempto.removeAllItems();
        for (Object emp : emps) {
            ddempto.addItem(emp);
        }
        
        
         if (ddempfrom.getItemCount() > 0)
        ddempfrom.setSelectedIndex(0);
        
        if (ddempto.getItemCount() > 0)
        ddempto.setSelectedIndex(ddempto.getItemCount() - 1);
        
        
          
          
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
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddempto = new javax.swing.JComboBox();
        ddempfrom = new javax.swing.JComboBox();
        ddsite = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        cbsalary = new javax.swing.JCheckBox();
        cbhourly = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        tbtotal = new javax.swing.JTextField();
        tbcount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("PayRoll Browse"));

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

        btdetail.setText("Hide Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        jLabel4.setText("To Empnbr");

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Site");

        jLabel1.setText("From Empnbr");

        jLabel3.setText("To Date");

        jLabel6.setText("From Date");

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        cbsalary.setText("Salaried");

        cbhourly.setText("Hourly");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcTo, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(cbhourly)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdetail))
                    .addComponent(cbsalary))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6))
                    .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(cbsalary)
                        .addComponent(cbhourly))
                    .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("Line Count");

        jLabel7.setText("Total Amount");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbcount, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(tbtotal))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
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
             
                DecimalFormat df = new DecimalFormat("#0.00");
                
               
               mymodel.setNumRows(0);
               tbtotal.setText("0");
               tbcount.setText("0"); 
               int i = 0;
               double total = 0.00;
               double netcheck = 0.00;
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
               
               
                  
                 String empfrom = "";
                 String empto = "";
                 if (ddempfrom.getSelectedItem() != null)
                     empfrom = ddempfrom.getSelectedItem().toString();
                 
                 if (ddempto.getSelectedItem() != null)
                     empto = ddempto.getSelectedItem().toString();
                      
                 
            
        
             res = st.executeQuery("select pyd_empnbr, pyd_emplname, pyd_empfname, pyd_empdept, pyd_emptype, pyd_paydate, pyd_checknbr, pyd_payamt, " +
                         " (select sum(pyl_amt) from pay_line where pyl_id = pyd_id and pyl_checknbr = pyd_checknbr ) as 'deductions' " +
                         " from pay_det where " +
                        " pyd_empnbr >= " + "'" + empfrom + "'" + " AND " +
                        " pyd_empnbr <= " + "'" + empto + "'" + " AND " +
                     " pyd_paydate >= " + "'" + dfdate.format(dcFrom.getDate()) + "'" + " AND " +
                        " pyd_paydate <= " + "'" + dfdate.format(dcTo.getDate()) + "'" + 
                        " order by pyd_empnbr ;");
                     
                  
                
                while (res.next()) {
                 i++;
                 netcheck = res.getDouble("pyd_payamt") - res.getDouble("deductions");
                 total = total + netcheck;
                 
                  if (! cbsalary.isSelected() && res.getString("pyd_emptype").equals("Salary")) {
                      continue;
                  }
                  if (! cbhourly.isSelected() && res.getString("pyd_emptype").equals("Hourly")) {
                      continue;
                  }
                 
                    mymodel.addRow(new Object[]{BlueSeerUtils.clickbasket, res.getString("pyd_empnbr"),
                                res.getString("pyd_emplname"),
                                res.getString("pyd_empfname"),
                                res.getString("pyd_empdept"),
                                res.getString("pyd_emptype"),
                                res.getString("pyd_paydate"),
                                res.getString("pyd_checknbr"),
                                res.getDouble("pyd_payamt"),
                                res.getDouble("deductions"),
                                netcheck
                            });
               } // while   
                                                                                                                                
                tbtotal.setText(df.format(total));
                tbcount.setText(String.valueOf(i));
        
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem executing Payroll Browse Report");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                getdetail(tablereport.getValueAt(row, 1).toString(), tablereport.getValueAt(row, 7).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
              
        }
    }//GEN-LAST:event_tablereportMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btdetail;
    private javax.swing.JCheckBox cbhourly;
    private javax.swing.JCheckBox cbsalary;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox ddempfrom;
    private javax.swing.JComboBox ddempto;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbcount;
    private javax.swing.JTextField tbtotal;
    // End of variables declaration//GEN-END:variables
}
