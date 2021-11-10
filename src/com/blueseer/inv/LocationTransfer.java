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

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class LocationTransfer extends javax.swing.JPanel {

    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                    new String[]{
                        getGlobalColumnTag("site"), 
                        getGlobalColumnTag("warehouse"), 
                        getGlobalColumnTag("location"), 
                        getGlobalColumnTag("qty")});
    
    /**
     * Creates new form LocationTransferPanel
     */
    public LocationTransfer() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void getlocqty(String parentpart) {
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               

                int i = 0;

                
                mymodel.setRowCount(0);
                tablelocqty.setModel(mymodel);
                            
                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

               res = st.executeQuery("SELECT in_site, in_loc, in_wh, in_qoh, in_date  " +
                        " FROM  in_mstr  " +
                        " where in_part = " + "'" + parentpart.toString() + "'" + 
                        " order by in_loc ;");

                while (res.next()) {
                    i++;
                    mymodel.addRow(new Object[]{
                                res.getString("in_site"),
                                res.getString("in_wh"),
                                res.getString("in_loc"),
                                res.getInt("in_qoh")
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
        
        //tbpart.requestFocus();
        mymodel.setRowCount(0);
        tablelocqty.setModel(mymodel);
        
        java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
       
        tbpart.setText("");
        tbqty.setText("");
        tbrmks.setText("");
        
        
        
         ArrayList<String> sites = new ArrayList();
        ddsitefrom.removeAllItems();
        ddsiteto.removeAllItems();
        sites = OVData.getSiteList();
        for (String code : sites) {
            ddsitefrom.addItem(code);
            ddsiteto.addItem(code);
        }
        
         ArrayList<String> wh = new ArrayList();
        ddwhfrom.removeAllItems();
        ddwhto.removeAllItems();
        wh = OVData.getWareHouseList(); 
        for (String code : wh) {
            ddwhfrom.addItem(code);
            ddwhto.addItem(code);
        }
        
        ArrayList<String> mylist = new ArrayList();
        ddlocfrom.removeAllItems();
        ddlocto.removeAllItems();
        if (ddwhfrom.getSelectedItem() != null) {
            mylist = OVData.getLocationListByWarehouse(ddwhfrom.getSelectedItem().toString());
            for (String code : mylist) {
                ddlocfrom.addItem(code);
                ddlocto.addItem(code);
            }
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

        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        ddlocfrom = new javax.swing.JComboBox();
        ddsitefrom = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddwhfrom = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        ddlocto = new javax.swing.JComboBox();
        ddsiteto = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddwhto = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        tbpart = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tbserial = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        dcdate = new com.toedter.calendar.JDateChooser();
        tbrmks = new javax.swing.JTextField();
        bttransfer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablelocqty = new javax.swing.JTable();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Warehouse/Location Transfer"));
        jPanel4.setName("panelmain"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("From:"));
        jPanel1.setName("panelfrom"); // NOI18N

        jLabel6.setText("Site:");
        jLabel6.setName("lblsite"); // NOI18N

        jLabel7.setText("Location:");
        jLabel7.setName("lblloc"); // NOI18N

        ddwhfrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhfromActionPerformed(evt);
            }
        });

        jLabel10.setText("Warehouse:");
        jLabel10.setName("lblwh"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddsitefrom, 0, 118, Short.MAX_VALUE)
                    .addComponent(ddlocfrom, 0, 118, Short.MAX_VALUE)
                    .addComponent(ddwhfrom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsitefrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddwhfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddlocfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("To:"));
        jPanel2.setName("panelto"); // NOI18N

        jLabel8.setText("Site:");

        jLabel9.setText("Location:");

        ddwhto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhtoActionPerformed(evt);
            }
        });

        jLabel11.setText("Warehouse:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddwhto, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddlocto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddsiteto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsiteto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddwhto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddlocto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        jLabel2.setText("Qty:");
        jLabel2.setName("lblqty"); // NOI18N

        jLabel3.setText("EffectiveDate:");
        jLabel3.setName("lbleffdate"); // NOI18N

        jLabel1.setText("Part:");
        jLabel1.setName("lblitem"); // NOI18N

        jLabel5.setText("Serial");
        jLabel5.setName("lblserial"); // NOI18N

        jLabel4.setText("Remarks:");
        jLabel4.setName("lblremarks"); // NOI18N

        dcdate.setDateFormatString("yyyy-MM-dd");

        bttransfer.setText("Transfer");
        bttransfer.setName("bttransfer"); // NOI18N
        bttransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bttransferActionPerformed(evt);
            }
        });

        tablelocqty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(tablelocqty);

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel12.setText("Expire");
        jLabel12.setName("lblexpiredate"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 13, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bttransfer, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bttransfer)
                .addContainerGap())
        );

        add(jPanel4);
    }// </editor-fold>//GEN-END:initComponents

    private void bttransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bttransferActionPerformed
        boolean proceed = true;
        boolean rtn;
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        Double qty = 0.00;
        String op = "";
        
        String sitefrom = "";
        String siteto = "";
        String whfrom = "";
        String whto = "";
        String locfrom = "";
        String locto = "";
        
        String expire = "";
        if (dcexpire.getDate() != null) {
            expire = dfdate.format(dcexpire.getDate());
        }
        
        if (ddlocfrom.getSelectedItem() != null)
        locfrom = ddlocfrom.getSelectedItem().toString();
        
        if (ddlocto.getSelectedItem() != null)
        locto = ddlocto.getSelectedItem().toString();
        
        if (ddwhfrom.getSelectedItem() != null)
        whfrom = ddwhfrom.getSelectedItem().toString();
        
        if (ddwhto.getSelectedItem() != null)
        whto = ddwhto.getSelectedItem().toString();
        
        if (ddsitefrom.getSelectedItem() != null)
        sitefrom = ddsitefrom.getSelectedItem().toString();
        
        if (ddsiteto.getSelectedItem() != null)
        siteto = ddsiteto.getSelectedItem().toString();
        
        
        qty = Double.valueOf(tbqty.getText());
        
        if (qty == 0) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1036));
            tbqty.requestFocus();
            return;
        }
        
        if (! OVData.isValidItem(tbpart.getText())) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1021));
            tbpart.requestFocus();
            return;
        }
        
        if (qty > invData.getItemQtyByWarehouseAndLocation(tbpart.getText(), sitefrom, whfrom, locfrom) ) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1074));
            tbqty.requestFocus();
            return;
        }
        
        if (proceed) {    
            //Date effdate, String part, int qty, String type, double price, double cost, String site, 
            //  String loc, String cust, String nbr, String order, int line, String po, String terms, String lot, String rmks, 
            //  String ref, String acct, String cc, String jobnbr, String serial, String program, String userid

            // do the transaction
            OVData.TRHistIssDiscrete(dcdate.getDate(), 
                  tbpart.getText(), 
                  qty.intValue(),
                      op,
                  "LOC-TRNF", 
                  0.00, 
                  0.00, 
                siteto, 
                locto,  
                whto,
                expire,
                "", 
                "", 
                "", 
                0, 
                "", 
                "", 
                tbserial.getText(), // lot 
                tbrmks.getText(), //rmks
                tbserial.getText(), //ref
                "", 
                "", 
                "", 
                tbserial.getText(),  // serial
                "LocactionTransferMaint", // program
                bsmf.MainFrame.userid
                );
            
        // do the 'to' side
       rtn = OVData.UpdateInventoryDiscrete(tbpart.getText(), siteto, ddlocto.getSelectedItem().toString(), whto, tbserial.getText(), expire, qty);
      
       // now do the 'from' side
       if (! rtn) {
       qty = -1 * qty;
       rtn = OVData.UpdateInventoryDiscrete(tbpart.getText(), sitefrom, ddlocfrom.getSelectedItem().toString(), whfrom, tbserial.getText(), expire, qty);
       }
       
      
       if (! rtn)
           bsmf.MainFrame.show("Transfer Complete");
       else
           bsmf.MainFrame.show("Problem with Transfer Occurred");
       
       initvars(null);
       
       } // proceed
       
        
    }//GEN-LAST:event_bttransferActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
        if (! OVData.isValidItem(tbpart.getText())) {
            bsmf.MainFrame.show("invalid item");
            tbpart.requestFocus();
            return;
        } else {
        getlocqty(tbpart.getText());
        }
    }//GEN-LAST:event_tbpartFocusLost

    private void ddwhfromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhfromActionPerformed
        if (ddwhfrom.getSelectedItem() != null) {
             ddlocfrom.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwhfrom.getSelectedItem().toString());
             for (String lc : loc) {
                ddlocfrom.addItem(lc);
             }
        }
    }//GEN-LAST:event_ddwhfromActionPerformed

    private void ddwhtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhtoActionPerformed
        if (ddwhto.getSelectedItem() != null) {
             ddlocto.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwhto.getSelectedItem().toString());
             for (String lc : loc) {
                ddlocto.addItem(lc);
             }
        }
    }//GEN-LAST:event_ddwhtoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bttransfer;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcexpire;
    private javax.swing.JComboBox ddlocfrom;
    private javax.swing.JComboBox ddlocto;
    private javax.swing.JComboBox ddsitefrom;
    private javax.swing.JComboBox ddsiteto;
    private javax.swing.JComboBox<String> ddwhfrom;
    private javax.swing.JComboBox<String> ddwhto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JTable tablelocqty;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbrmks;
    private javax.swing.JTextField tbserial;
    // End of variables declaration//GEN-END:variables
}
