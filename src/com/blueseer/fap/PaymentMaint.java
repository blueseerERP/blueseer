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

package com.blueseer.fap;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
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
public class PaymentMaint extends javax.swing.JPanel {
 
    
       // NOTE:  if you change this...you must also adjust APCheckRun...my advise....dont change it
       PaymentMaint.MyTableModel mymodel = new PaymentMaint.MyTableModel(new Object[][]{},
                        new String[]{"VendCode", "VendName", "Voucher", "Invoice","DueDate", "Bank", "Amt", "Select?"});
      
      


      
      double amtselected = 0;
      double tot = 0;
      
      
      
      
    /**
     * Creates new form ScrapReportPanel
     */
    
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
      
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
     
        
        
        
        
        
        
        public Class getColumnClass(int column) {
                  
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }    
        return Object.class;
      }
       
        }    
    
     
    
    public PaymentMaint() {
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
            
    public void postcommit() {
         java.util.Date now = new java.util.Date();
        // dcfrom.setDate(now);
       //  dcto.setDate(now);
         frombank.setText("");
         tobank.setText("");
         fromvend.setText("");
         tovend.setText("");
         tbchecknbr.setText("");
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
          
          
          
             
         
         
    }
    
    public void initvars(String[] arg) {
         java.util.Date now = new java.util.Date();
         dcfrom.setDate(now);
         dcto.setDate(now);
         
           Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, 90);
         dcto.setDate(calfrom.getTime());
         
         
         frombank.setText("");
         tobank.setText("");
         fromvend.setText("");
         tovend.setText("");
         if (tbchecknbr.getText().isEmpty())
         tbchecknbr.setText(String.valueOf(OVData.getNextNbr("checknumber")));
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
          
          
          
             
         
         
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
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labeldollar = new javax.swing.JLabel();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        labelselected = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbchecknbr = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fromvend = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        btexport = new javax.swing.JButton();
        tovend = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        tobank = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        frombank = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        labeldollar.setBackground(new java.awt.Color(195, 129, 129));
        labeldollar.setText("0");

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        jLabel9.setText("Total");
        jLabel9.setName("lblamt"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        labelselected.setBackground(new java.awt.Color(195, 129, 129));
        labelselected.setText("0");

        jLabel10.setText("Row(s)");
        jLabel10.setName("lblrows"); // NOI18N

        jLabel8.setText("CheckNbr:");
        jLabel8.setName("lblchecknbr"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btcommit)
                    .addComponent(tbchecknbr)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelselected, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(labeldollar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelcount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelselected, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbchecknbr)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btcommit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel6.setText("To Bank");
        jLabel6.setName("lbltobank"); // NOI18N

        jLabel4.setText("To Vend");
        jLabel4.setName("lbltovend"); // NOI18N

        jLabel3.setText("To DueDate");
        jLabel3.setName("lbltoduedate"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        btexport.setText("Export");
        btexport.setName("btexport"); // NOI18N
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        jLabel1.setText("From Vend");
        jLabel1.setName("lblfromvend"); // NOI18N

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel2.setText("From DueDate");
        jLabel2.setName("lblfromduedate"); // NOI18N

        jLabel5.setText("From Bank");
        jLabel5.setName("lblfrombank"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

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
                    .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fromvend)
                    .addComponent(tovend, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frombank, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tobank, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btexport)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(fromvend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(tovend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(frombank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tobank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btexport)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        mytable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mytableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(mytable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1)
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
        tot = 0;
        amtselected = 0;
        labelselected.setText("$0");
        labeldollar.setText("$0");
        labelcount.setText("0");
    
try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                double amt = 0;
                 int i = 0;
                String fvend = "";
                String tvend = "";
                String fbank = "";
                String tbank = "";
                
                if (fromvend.getText().isEmpty()) {
                    fvend = bsmf.MainFrame.lowchar;
                } else {
                    fvend = fromvend.getText();
                }
                 if (tovend.getText().isEmpty()) {
                    tvend = bsmf.MainFrame.hichar;
                } else {
                    tvend = tovend.getText();
                }
                  if (frombank.getText().isEmpty()) {
                    fbank = bsmf.MainFrame.lowchar;
                } else {
                    fbank = frombank.getText();
                }
                   if (tobank.getText().isEmpty()) {
                    tbank = bsmf.MainFrame.hichar;
                } else {
                    tbank = tobank.getText();
                }
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
                   
                   
                mymodel.setRowCount(0);
       
       
               TableColumnModel tcm = mytable.getColumnModel();
               tcm.getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());  
            
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

             
                 res = st.executeQuery("SELECT ap_vend, vd_name, ap_nbr, ap_ref, ap_duedate, ap_bank, ap_amt, ap_applied " +
                        " FROM  ap_mstr inner join vd_mstr on vd_addr = ap_vend " +
                        " where ap_duedate >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND ap_duedate <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                        " AND ap_vend >= " + "'" + fvend + "'" + 
                        " AND ap_vend <= " + "'" + tvend + "'" + 
                         " AND ap_bank >= " + "'" + fbank + "'" + 
                        " AND ap_bank <= " + "'" + tbank + "'" + 
                        " AND ap_type = 'V' AND ap_status = 'o' " + 
                        " order by ap_vend;");    
                 
                while (res.next()) {
                    i++;
                    tot = tot + (res.getDouble("ap_amt") - res.getDouble("ap_applied"));
                    amt = (res.getDouble("ap_amt") - res.getDouble("ap_applied"));
                    mymodel.addRow(new Object[]{res.getString("ap_vend"),
                                res.getString("vd_name"),
                                res.getString("ap_nbr"),
                                res.getString("ap_ref"),
                                res.getString("ap_duedate"),
                                res.getString("ap_bank"),
                                amt,
                                Boolean.TRUE
                            });
                }
                labeldollar.setText("$" + String.valueOf(currformatDouble(tot)));
                labelcount.setText(String.valueOf(i));
               
                
                
                
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

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
       FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;
        
         int i = 0;
                String frompart = "";
                String topart = "";
                String fromcode = "";
                String tocode = "";
                
                if (fromvend.getText().isEmpty()) {
                    frompart = bsmf.MainFrame.lowchar;
                } else {
                    frompart = fromvend.getText();
                }
                 if (tovend.getText().isEmpty()) {
                    topart = bsmf.MainFrame.hichar;
                } else {
                    topart = tovend.getText();
                }
                  if (frombank.getText().isEmpty()) {
                    fromcode = bsmf.MainFrame.lowchar;
                } else {
                    fromcode = frombank.getText();
                }
                   if (tobank.getText().isEmpty()) {
                    tocode = bsmf.MainFrame.hichar;
                } else {
                    tocode = tobank.getText();
                }
                   
           DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            output = new BufferedWriter(new FileWriter(f));
      
               String myheader = "Part,P/M,Qty,Operation,Date,Code,Dept";
                
                 output.write(myheader + '\n');
                 
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

               res = st.executeQuery("SELECT tr_part, tr_type, it_code, tr_qty, " +
                        " tr_op, tr_eff_date, tr_ref, tr_actcell " +
                        " FROM  tran_mstr inner join item_mstr on it_item = tr_part " +
                        " where tr_eff_date >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" + 
                        " AND tr_eff_date <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                        " AND tr_part >= " + "'" + frompart + "'" + 
                        " AND tr_part <= " + "'" + topart + "'" + 
                         " AND tr_ref >= " + "'" + fromcode + "'" + 
                        " AND tr_ref <= " + "'" + tocode + "'" + 
                       " AND tr_type = 'ISS-SCRAP' " + 
                        " order by tr_id desc ;");


                while (res.next()) {
                    String newstring = res.getString("tr_part") + "," + res.getString("it_code").replace(",","") + "," + 
                            res.getString("tr_qty") + "," + res.getString("tr_op") + "," + res.getString("tr_eff_date") + "," + 
                            res.getString("tr_ref") + "," + res.getString("tr_actcell") ;
                            
                    output.write(newstring + '\n');
                }
             bsmf.MainFrame.show(getMessageTag(1126));
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
        
        
        output.close();
        
        
        
        } catch (IOException ex) {
            Logger.getLogger(bsmf.MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btexportActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
            
             java.util.Date now = new java.util.Date();
             // clean out the unchecked rows
             
             for (int i = 0 ; i < mymodel.getRowCount(); i++) {
             if (! (boolean) mymodel.getValueAt(i, 7)) {
                 mymodel.removeRow(i);
                 i--;
             }
             }
             
            
             
             // now all that should be left is vouchers that needs a check cut against them....pass it over to APCHECKRUN
             if (mytable.getRowCount() > 0) {
              OVData.APCheckRun(mytable, now, Integer.valueOf(tbchecknbr.getText().toString()), "AP-Vendor");
             }
             postcommit();
             bsmf.MainFrame.show(getMessageTag(1125));
    }//GEN-LAST:event_btcommitActionPerformed

    private void mytableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mytableMouseReleased
       if (mytable.getRowCount() > 0) {
        // this is to calculate the selected rows sum
         int[] rows = mytable.getSelectedRows();
        amtselected = 0;
        for (int i : rows) {
           amtselected += Double.valueOf(mytable.getValueAt(i, 6).toString());
        }
        labelselected.setText(String.valueOf("$" + amtselected));
        
        
        // this is to calculate the checkbox true sum
        tot = 0;
        for (int i = 0 ; i < mytable.getRowCount(); i++) {
            if ((boolean) mytable.getValueAt(i, 7))
            tot += Double.valueOf(mytable.getValueAt(i, 6).toString());
        }
        labeldollar.setText("$" + String.valueOf(tot));
     }
    }//GEN-LAST:event_mytableMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btexport;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JTextField frombank;
    private javax.swing.JTextField fromvend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JLabel labelselected;
    private javax.swing.JTable mytable;
    private javax.swing.JTextField tbchecknbr;
    private javax.swing.JTextField tobank;
    private javax.swing.JTextField tovend;
    // End of variables declaration//GEN-END:variables
}
