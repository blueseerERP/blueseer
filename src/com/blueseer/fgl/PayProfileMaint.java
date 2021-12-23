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
package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 *
 * @author vaughnte
 */


public class PayProfileMaint extends javax.swing.JPanel {

    
     javax.swing.table.DefaultTableModel profilemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Element", "Type", "Acct", "CC", "Percent", "AmountType", "Enabled"
            });
    
    /**
     * Creates new form ClockControl
     */
    public PayProfileMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void getProfile(String code) {
         try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  pay_profile where payp_code = " + "'" + code + "'" + ";");
                    while (res.next()) {
                        i++;
                        tbdesc.setText(res.getString("payp_desc"));
                        tbprofilecode.setText(res.getString("payp_code"));
                    }
                    res = st.executeQuery("SELECT * FROM  pay_profdet where " +
                            " paypd_parentcode = " + "'" + code + "'" + ";");
                    while (res.next()) {
                     profilemodel.addRow(new Object[]{res.getString("paypd_desc"), res.getString("paypd_type"), res.getString("paypd_acct"), res.getString("paypd_cc"), res.getString("paypd_amt").replace('.',defaultDecimalSeparator), res.getString("paypd_amttype"), res.getBoolean("paypd_enabled")});   
                    }
           
                    if (i > 0) {
                        enableAll();
                        btlookup.setEnabled(false);
                        btnew.setEnabled(false);
                        btadd.setEnabled(false);
                        tbprofilecode.setEnabled(false);
                    }
                    
            }
            catch (SQLException s) {
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
    
    public void clearAll() {
           profilemodel.setRowCount(0);
           tableelement.setModel(profilemodel);
           tbprofilecode.setText("");
           tbdesc.setText("");
           tbelement.setText("");
           tbelementamt.setText("");
           tbacct.setText("");
           tbcc.setText("");
           cbenabled.setSelected(false);
           
     }
     
    public void enableAll() {
         tableelement.setEnabled(true);
         tbprofilecode.setEnabled(true);
         tbdesc.setEnabled(true);
         tbacct.setEnabled(true);
         tbcc.setEnabled(true);
         tbelement.setEnabled(true);
         tbelementamt.setEnabled(true);
         cbenabled.setEnabled(true);
         btlookup.setEnabled(true);
         btedit.setEnabled(true);
         btnew.setEnabled(true);
         btadd.setEnabled(true);
         btdelete.setEnabled(true);
         btaddelement.setEnabled(true);
         btdeleteelement.setEnabled(true);
         ddtype.setEnabled(true);
         ddamttype.setEnabled(true);
     }
    
    public void disableAll() {
         tableelement.setEnabled(false);
         tbprofilecode.setEnabled(false);
         tbdesc.setEnabled(false);
         tbacct.setEnabled(false);
         tbcc.setEnabled(false);
         tbelement.setEnabled(false);
         cbenabled.setEnabled(false);
         btlookup.setEnabled(false);
         btedit.setEnabled(false);
         btnew.setEnabled(false);
         btadd.setEnabled(false);
         tbelementamt.setEnabled(false);
         btaddelement.setEnabled(false);
         btdeleteelement.setEnabled(false);
         btdelete.setEnabled(false);
         ddtype.setEnabled(false);
         ddamttype.setEnabled(false);
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
          
           clearAll();
           disableAll();
           
          
           
            if (arg != null && arg.length > 0) {
             getProfile(arg[0]);
           } else {
               disableAll();
               btlookup.setEnabled(true);
               btnew.setEnabled(true);
           }
           
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getPayProfileBrowseUtil(luinput.getText(),0, "payp_code");
        } else {
         luModel = DTData.getPayProfileBrowseUtil(luinput.getText(),0, "payp_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle("No Records Found!");
        } else {
            ludialog.setTitle(luModel.getRowCount() + " Records Found!");
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog("Code", "Description"); 
        
        
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
        btedit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableelement = new javax.swing.JTable();
        cbenabled = new javax.swing.JCheckBox();
        btdeleteelement = new javax.swing.JButton();
        tbelement = new javax.swing.JTextField();
        btaddelement = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tbelementamt = new javax.swing.JTextField();
        ddtype = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        ddamttype = new javax.swing.JComboBox<>();
        tbacct = new javax.swing.JTextField();
        tbcc = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnew = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbprofilecode = new javax.swing.JTextField();
        tbdesc = new javax.swing.JTextField();
        btlookup = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Employee Profile Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        btedit.setText("Update");
        btedit.setName("btupdate"); // NOI18N
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Profile Elements Maintenance"));
        jPanel2.setName("panelelements"); // NOI18N

        jLabel3.setText("Element");
        jLabel3.setName("lblelement"); // NOI18N

        tableelement.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableelement);

        cbenabled.setText("Enabled?");
        cbenabled.setName("cbenabled"); // NOI18N

        btdeleteelement.setText("Delete");
        btdeleteelement.setName("btdelete"); // NOI18N
        btdeleteelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteelementActionPerformed(evt);
            }
        });

        btaddelement.setText("Add");
        btaddelement.setName("btadd"); // NOI18N
        btaddelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddelementActionPerformed(evt);
            }
        });

        jLabel4.setText("Percent/Rate");
        jLabel4.setName("lblpercentrate"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Deduction", "Earning" }));

        jLabel7.setText("Element Type");
        jLabel7.setName("lblelementtype"); // NOI18N

        ddamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "percent", "fixed" }));

        tbacct.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbacctFocusLost(evt);
            }
        });

        tbcc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbccFocusLost(evt);
            }
        });

        jLabel1.setText("GL Acct");
        jLabel1.setName("lblglacct"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbelement)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tbcc))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbelementamt, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddamttype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbenabled)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btaddelement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteelement)
                        .addGap(14, 14, 14)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbelement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbenabled)
                    .addComponent(jLabel4)
                    .addComponent(tbelementamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btaddelement)
                    .addComponent(btdeleteelement))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Master Profile"));
        jPanel3.setName("panelmaster"); // NOI18N

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("Code");
        jLabel5.setName("lblid"); // NOI18N

        jLabel6.setText("Desc");
        jLabel6.setName("lbldesc"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbprofilecode, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(btnew)
                        .addGap(0, 332, Short.MAX_VALUE))
                    .addComponent(tbdesc))
                .addGap(38, 38, 38))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbprofilecode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnew)
                    .addComponent(btlookup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btedit)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        
        
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                    
                       st.executeUpdate("update pay_profile set " + 
                           "payp_desc = " + "'" + tbdesc.getText() + "'" + 
                           " where payp_code = " + "'" + tbprofilecode.getText() + "'" +
                             ";");     
                
               //  now lets delete all stored actions of this master task...then add back from table
                st.executeUpdate("delete from pay_profdet where paypd_parentcode = " + "'" + tbprofilecode.getText() + "'" + ";");
                       
                 for (int j = 0; j < tableelement.getRowCount(); j++) {
                st.executeUpdate("insert into pay_profdet (paypd_parentcode, paypd_desc, paypd_type, paypd_acct, paypd_cc, paypd_amt, paypd_amttype, paypd_enabled ) values ( " 
                        + "'" + tbprofilecode.getText() + "'" + ","
                        + "'" + tableelement.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tableelement.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + tableelement.getValueAt(j, 2).toString() + "'" + ","
                        + "'" + tableelement.getValueAt(j, 3).toString() + "'" + ","    
                        + "'" + tableelement.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "'" + tableelement.getValueAt(j, 5).toString() + "'" + ","            
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tableelement.getValueAt(j, 6).toString())) + "'" 
                        + " );" );
                 }
              
                 bsmf.MainFrame.show(getMessageTag(1008));
                 initvars(null);
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
       
    }//GEN-LAST:event_bteditActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      
      tbprofilecode.setText("");
      tbprofilecode.requestFocus();
      enableAll();
      btlookup.setEnabled(false);
      btedit.setEnabled(false);
      btnew.setEnabled(false);
     
      
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddelementActionPerformed
        
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbelementamt.getText());
        if (!m.find() || tbelementamt.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1033, "tbelementamt"));
            return;
        }
        if (Double.valueOf(tbelementamt.getText()) == 0) {
            bsmf.MainFrame.show(getMessageTag(1036));
            return;
        }
        
        profilemodel.addRow(new Object[]{ tbelement.getText(), ddtype.getSelectedItem().toString(), tbacct.getText(), tbcc.getText(), tbelementamt.getText(), ddamttype.getSelectedItem().toString(), String.valueOf(BlueSeerUtils.boolToInt(cbenabled.isSelected())) });
    }//GEN-LAST:event_btaddelementActionPerformed

    private void btdeleteelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteelementActionPerformed
       int[] rows = tableelement.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) tableelement.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeleteelementActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
            
        if (tbprofilecode.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024,"tbprofilecode"));
            return;
        }
        
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
          
                int i = 0;
                
                 res = st.executeQuery("SELECT payp_code FROM  pay_profile where payp_code = " + "'" + tbprofilecode.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                
                
                if (i == 0)  {
                    st.executeUpdate("insert into pay_profile (payp_code, payp_desc) values (" + 
                            "'" + tbprofilecode.getText() + "'" + "," +
                            "'" + tbdesc.getText() + "'"  + 
                            ")" + ";");     
                
              
                     for (int j = 0; j < tableelement.getRowCount(); j++) {
                     st.executeUpdate("insert into pay_profdet (paypd_parentcode, paypd_desc, paypd_type, paypd_amt, paypd_amttype, paypd_enabled ) values ( " 
                            + "'" + tbprofilecode.getText() + "'" + ","
                            + "'" + tableelement.getValueAt(j, 0).toString() + "'" + ","
                            + "'" + tableelement.getValueAt(j, 1).toString() + "'" + ","
                            + "'" + tableelement.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + tableelement.getValueAt(j, 3).toString() + "'" + ","    
                            + "'" + tableelement.getValueAt(j, 4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                            + "'" + tableelement.getValueAt(j, 5).toString() + "'" + ","              
                            + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tableelement.getValueAt(j, 6).toString())) + "'" 
                            + " );" );
                     }
              
                bsmf.MainFrame.show(getMessageTag(1007));
                initvars(null);
                }  else {
                        bsmf.MainFrame.show(getMessageTag(1014));
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
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        
        
        
        boolean proceed = bsmf.MainFrame.warn(getMessageTag(1004));
        if (proceed) {
            try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                    int k = 0;
                    res = st.executeQuery("SELECT emp_profile FROM  emp_mstr where emp_profile = " + "'" + tbprofilecode.getText() + "'" + ";");
                    while (res.next()) {
                        k++;
                    }
                    if (k > 0) {
                        bsmf.MainFrame.show(getMessageTag(1051));
                        return;
                    }
                    
                    int i = st.executeUpdate("delete from pay_profile where payp_code = " + "'" + tbprofilecode.getText() + "'" + ";");
                    int j = st.executeUpdate("delete from pay_profdet where paypd_parentcode = " + "'" + tbprofilecode.getText() + "'" + ";");
                    if (i > 0 && j > 0) {
                        bsmf.MainFrame.show(getMessageTag(1009));
                        initvars(null);
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
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbacctFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbacctFocusLost
        if (! tbacct.getText().isEmpty()) {
            if (! OVData.isValidGLAcct(tbacct.getText())) {
                bsmf.MainFrame.show(getMessageTag(1052));
                tbacct.requestFocus();
            }
        }
    }//GEN-LAST:event_tbacctFocusLost

    private void tbccFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbccFocusLost
        if (! tbcc.getText().isEmpty()) {
            if (! OVData.isValidGLcc(tbcc.getText())) {
                bsmf.MainFrame.show(getMessageTag(1048));
                tbcc.requestFocus();
            }
        }
    }//GEN-LAST:event_tbccFocusLost

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddelement;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteelement;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnew;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox<String> ddamttype;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableelement;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbcc;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbelement;
    private javax.swing.JTextField tbelementamt;
    private javax.swing.JTextField tbprofilecode;
    // End of variables declaration//GEN-END:variables
}
