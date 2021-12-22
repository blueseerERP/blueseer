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

package com.blueseer.prd;


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.sch.schData;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.sql.Connection;
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
public class OEEEntryMaint extends javax.swing.JPanel {


 javax.swing.table.DefaultTableModel modelDT = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("desc"), getGlobalColumnTag("minutes")
            });
 javax.swing.table.DefaultTableModel modelCO = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("desc"), getGlobalColumnTag("minutes")
            });
  javax.swing.table.DefaultTableModel modelRecs = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("cell"), 
                getGlobalColumnTag("shift"), 
                getGlobalColumnTag("hour"), 
                getGlobalColumnTag("prodqty"), 
                getGlobalColumnTag("rejectqty"), 
                getGlobalColumnTag("operator")
            });
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public OEEEntryMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
     public void getRecords(String cell, String date) {
        try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
               // partnbr.removeAllItems();
               int i = 0;
               
                modelRecs.setNumRows(0);
               
                res = st.executeQuery("select * from eeo_mstr where eeo_date = " + "'" + date + "'" 
                        + " AND eeo_cell = " + "'" + cell + "'" 
                        + ";");
                while (res.next()) {
                    i++;
                     modelRecs.addRow(new Object[]{ res.getString("eeo_cell"), res.getString("eeo_shift"),
                                res.getString("eeo_hour"), res.getInt("eeo_qty_prod"), res.getInt("eeo_qty_scrap"), res.getString("eeo_operator")
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
    
    
    public void validateScan(String scan) {
          if (schData.isPlan(scan)) {
       tbqty.setText(String.valueOf(schData.getPlanSchedQty(scan)));
     
      
       tbqty.requestFocus();
       btcommit.setEnabled(true);
      } else {
              btcommit.setEnabled(false);
            
        // bsmf.MainFrame.show("Bad Ticket: " + scan);
            
            return;
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
         java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
        
        tbqty.setText("");
        tbscrapqty.setText("");
        tboperator.setText("");
        tbco.setText("");
        tbdt.setText("");
        
        ddcell.removeAllItems();
        ArrayList myparts = OVData.getCodeMstr("CELL");
        for (int i = 0; i < myparts.size(); i++) {
            ddcell.addItem(myparts.get(i));
        }
        
        ddshift.setSelectedIndex(0);
        ddhour.setSelectedIndex(0);
        
        modelDT.setRowCount(0);
        modelCO.setRowCount(0);
        modelRecs.setRowCount(0);
       
        
        tableDT.setModel(modelDT);
        tableCO.setModel(modelCO);
        tableRec.setModel(modelRecs);
        
        getRecords(ddcell.getSelectedItem().toString(), dfdate.format(dcdate.getDate()));
        
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDT = new javax.swing.JTable();
        btaddco = new javax.swing.JButton();
        btdeldt = new javax.swing.JButton();
        btdelco = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableCO = new javax.swing.JTable();
        btadddt = new javax.swing.JButton();
        tbdt = new javax.swing.JTextField();
        tbco = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbcomin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbdtmin = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        ddcell = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        ddhour = new javax.swing.JComboBox();
        ddshift = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbscrapqty = new javax.swing.JTextField();
        tboperator = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        dcdate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableRec = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("OEE Entry"));
        jPanel1.setName("panelmain"); // NOI18N

        tableDT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "DownTime Desc"
            }
        ));
        jScrollPane1.setViewportView(tableDT);

        btaddco.setText("Add ChangeOver");
        btaddco.setName("btadd"); // NOI18N
        btaddco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcoActionPerformed(evt);
            }
        });

        btdeldt.setText("Delete DownTime");
        btdeldt.setName("btdelete"); // NOI18N
        btdeldt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeldtActionPerformed(evt);
            }
        });

        btdelco.setText("Delete ChangeOver");
        btdelco.setName("btdelete"); // NOI18N
        btdelco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdelcoActionPerformed(evt);
            }
        });

        tableCO.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "ChangeOver Desc"
            }
        ));
        jScrollPane2.setViewportView(tableCO);

        btadddt.setText("Add DownTime");
        btadddt.setName("btadd"); // NOI18N
        btadddt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadddtActionPerformed(evt);
            }
        });

        jLabel1.setText("DownTime");
        jLabel1.setName("lbldowntime"); // NOI18N

        jLabel2.setText("ChangeOver");
        jLabel2.setName("lblchangeover"); // NOI18N

        jLabel3.setText("Minutes");

        jLabel5.setText("Minutes");
        jLabel5.setName("lblminutes"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbdtmin, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbcomin, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbco)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btdeldt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadddt))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btdelco)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddco))
                            .addComponent(tbdt, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbdt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdtmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadddt)
                    .addComponent(btdeldt))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(tbco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcomin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddco)
                    .addComponent(btdelco))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel11.setText("Operator:");
        jLabel11.setName("lbloperator"); // NOI18N

        ddcell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcellActionPerformed(evt);
            }
        });

        jLabel6.setText("Cell:");
        jLabel6.setName("lblcell"); // NOI18N

        ddhour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));

        ddshift.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));

        jLabel9.setText("Shift:");
        jLabel9.setName("lblshift"); // NOI18N

        jLabel10.setText("Hour:");
        jLabel10.setName("lblhour"); // NOI18N

        tbscrapqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbscrapqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbscrapqtyFocusLost(evt);
            }
        });

        jLabel12.setText("Qty Scrap:");
        jLabel12.setName("lblqtyscrap"); // NOI18N

        jLabel4.setText("Qty Prod:");
        jLabel4.setName("lblqtyprod"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        jLabel7.setText("Date");
        jLabel7.setName("lbldate"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel4)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ddcell, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddhour, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddshift, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tbscrapqty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tboperator, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddhour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscrapqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tableRec.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tableRec);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btcommit)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       
        
          try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
               
                 int qty = 0;
                 int scrapqty = 0;
                 int eeoid = 0;
        
        Pattern p = Pattern.compile("^[0-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            return;
        } else {
            qty = Integer.valueOf(tbqty.getText());
        }
        
        m = p.matcher(tbscrapqty.getText());
        if (!m.find() || tbscrapqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbscrapqty.requestFocus();
            return;
        } else {
            scrapqty = Integer.valueOf(tbscrapqty.getText());
        }       
                
                int i = 0;
                  res = st.executeQuery("Select * from eeo_mstr where eeo_date = " + "'" + dfdate.format(dcdate.getDate()) + "'" + 
                          " AND eeo_cell = " + "'" + ddcell.getSelectedItem().toString() + "'" +
                          " AND eeo_shift = " + "'" + ddshift.getSelectedItem().toString() + "'" +
                          " AND eeo_hour = " + "'" + ddhour.getSelectedItem().toString() + "'"  );
                    while (res.next()) {
                        i++;
                    }
                    
                    if (i > 0) {
                        proceed = false;
                        bsmf.MainFrame.show(getMessageTag(1014));
                        return;
                    }
                   
                    
                if (proceed) {
                    eeoid = OVData.getNextNbr("eeo");
                    
                    st.executeUpdate("insert into eeo_mstr "
                        + "( eeo_id, eeo_date, eeo_cell, eeo_shift, eeo_hour, eeo_qty_prod, eeo_qty_scrap, eeo_operator ) "
                        + " values ( " + "'" + eeoid + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()) + "'" + ","
                        + "'" + ddcell.getSelectedItem().toString() + "'" + ","
                        + "'" + ddshift.getSelectedItem().toString() + "'" + ","
                        + "'" + ddhour.getSelectedItem().toString() + "'" + ","
                        + "'" + qty + "'" + ","
                        + "'" + scrapqty + "'" + ","
                        + "'" + tboperator.getText() + "'" 
                        + ")"
                        + ";");

                  
                    
                    for (int j = 0; j < tableDT.getRowCount(); j++) {
                        st.executeUpdate("insert into eeo_det "
                            + "(eeod_id, eeod_type, eeod_value, eeod_min ) "
                            + " values ( " 
                            + "'" + eeoid + "'" + ","
                            + "'" + "DT" + "'" + ","
                            + "'" + tableDT.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tableDT.getValueAt(j, 1).toString() + "'"
                            + ")"
                            + ";");

                    }
                    
                    for (int j = 0; j < tableCO.getRowCount(); j++) {
                        st.executeUpdate("insert into eeo_det "
                            + "(eeod_id, eeod_type, eeod_value, eeod_min ) "
                            + " values ( " 
                            + "'" + eeoid + "'" + ","
                            + "'" + "CO" + "'" + ","
                            + "'" + tableCO.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tableCO.getValueAt(j, 1).toString() + "'"
                            + ")"
                            + ";");

                    }
                    
                    
                    bsmf.MainFrame.show(getMessageTag(1125));
                  
                    initvars(null);
                    getRecords(ddcell.getSelectedItem().toString(), dfdate.format(now));
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
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
        
        
        
       
        
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
       tbqty.setBackground(Color.yellow);
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        tbqty.setBackground(Color.white);
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbscrapqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscrapqtyFocusGained
        tbscrapqty.setBackground(Color.yellow);
    }//GEN-LAST:event_tbscrapqtyFocusGained

    private void tbscrapqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscrapqtyFocusLost
         tbscrapqty.setBackground(Color.white);
    }//GEN-LAST:event_tbscrapqtyFocusLost

    private void btadddtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadddtActionPerformed
       boolean canproceed = true;
       
        tableDT.setModel(modelDT);
        
       
        if (canproceed) {
            modelDT.addRow(new Object[]{tbdt.getText(), tbdtmin.getText() });
        }
       
        tbdt.setText("");
        tbdt.requestFocus();
    }//GEN-LAST:event_btadddtActionPerformed

    private void btaddcoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcoActionPerformed
       boolean canproceed = true;
       
        tableCO.setModel(modelCO);
        
       
        if (canproceed) {
            modelCO.addRow(new Object[]{tbco.getText(), tbcomin.getText() });
        }
       
        tbco.setText("");
        tbco.requestFocus();
    }//GEN-LAST:event_btaddcoActionPerformed

    private void btdeldtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeldtActionPerformed
         int[] rows = tableDT.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tableDT.getModel()).removeRow(i);
        }
        
    }//GEN-LAST:event_btdeldtActionPerformed

    private void btdelcoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdelcoActionPerformed
        int[] rows = tableCO.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tableCO.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdelcoActionPerformed

    private void ddcellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcellActionPerformed
       
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        if (ddcell.getItemCount() > 0) {
          getRecords(ddcell.getSelectedItem().toString(),dfdate.format(dcdate.getDate()));
        } // if ddcust has a list
    }//GEN-LAST:event_ddcellActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btaddco;
    private javax.swing.JButton btadddt;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdelco;
    private javax.swing.JButton btdeldt;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddcell;
    private javax.swing.JComboBox ddhour;
    private javax.swing.JComboBox ddshift;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tableCO;
    private javax.swing.JTable tableDT;
    private javax.swing.JTable tableRec;
    private javax.swing.JTextField tbco;
    private javax.swing.JTextField tbcomin;
    private javax.swing.JTextField tbdt;
    private javax.swing.JTextField tbdtmin;
    private javax.swing.JTextField tboperator;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbscrapqty;
    // End of variables declaration//GEN-END:variables
}
