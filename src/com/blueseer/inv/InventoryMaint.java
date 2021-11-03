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
import static bsmf.MainFrame.tags;
import com.blueseer.fgl.fglData;

import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author vaughnte
 */
public class InventoryMaint extends javax.swing.JPanel {

        public static javax.swing.table.DefaultTableModel lookUpModel = null;
        public static JTable lookUpTable = new JTable();
        public static MouseListener mllu = null;
        public static JDialog dialog = new JDialog();
        
        public static ButtonGroup bg = null;
        public static JRadioButton rb1 = null;
        public static JRadioButton rb2 = null;
    
    /**
     * Creates new form InventoryMiscPanel
     */
    public InventoryMaint() {
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
    
    
    public void getiteminfo(String parentpart) {
        try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                int i = 0;

               res = st.executeQuery("SELECT it_loc, it_site, it_wh  " +
                        " FROM  item_mstr  " +
                        " where it_item = " + "'" + parentpart.toString() + "'" + 
                        " ;");

                while (res.next()) {
                    i++;
                   ddsite.setSelectedItem(res.getString("it_site"));
                   ddloc.setSelectedItem(res.getString("it_loc"));
                   ddwh.setSelectedItem(res.getString("it_wh"));
              
                }
                
            } catch (SQLException s) {
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
                 MainFrame.bslog(s);
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
             
             
         }
    
    public void initvars(String[] arg) {
        
        ddtype.requestFocus();
       
        
        java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
       
        tbpart.setText("");
        tbqty.setText("");
        tbref.setText("");
        tbrmks.setText("");
        tblotserial.setText("");
        
        tbpart.setBackground(Color.white);
        tbqty.setBackground(Color.white);
        
        ArrayList<String> wh = new ArrayList();
        ddwh.removeAllItems();
        wh = OVData.getWareHouseList(); 
        for (String code : wh) {
            ddwh.addItem(code);
        }
        
       
         ArrayList<String> mylist = new ArrayList();
        ddloc.removeAllItems();
        if (ddwh.getSelectedItem() != null) {        
         mylist = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
         for (String code : mylist) {
            ddloc.addItem(code);
         }
        }
        
        
        
        ArrayList<String> sites = new ArrayList();
        ddsite.removeAllItems();
        sites = OVData.getSiteList();
        for (String code : sites) {
            ddsite.addItem(code);
        }
        
        ArrayList<String> accts = new ArrayList();
        ddacct.removeAllItems();
        accts = OVData.getGLAcctList();
        for (String code : accts) {
            ddacct.addItem(code);
        }
        
        ArrayList<String> ccs = new ArrayList();
        ddcc.removeAllItems();
        ccs = OVData.getGLCCList();
        for (String code : ccs) {
            ddcc.addItem(code);
        }
    }
    
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_item");
        } else {
         luModel = DTData.getItemDescBrowse(luinput.getText(), "it_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
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
                tbpart.setText(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getGlobalColumnTag("description")); 
        
        
        
    }

    public void lookUpFrameAcctDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_id");
        } else {
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
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
                ddacct.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblacct", this.getClass().getSimpleName()), 
                getGlobalColumnTag("description")); 
        
        
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
        tbrmks = new javax.swing.JTextField();
        tbpart = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblcc = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tblotserial = new javax.swing.JTextField();
        tbref = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcdate = new com.toedter.calendar.JDateChooser();
        ddtype = new javax.swing.JComboBox();
        ddloc = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        ddcc = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        ddacct = new javax.swing.JComboBox();
        lblacct = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btLookUpItemDesc = new javax.swing.JButton();
        btLookUpAcctDesc = new javax.swing.JButton();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Adjustment (Issue / Receipt)"));
        jPanel1.setName("panelmain"); // NOI18N

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        jLabel3.setText("Site:");
        jLabel3.setName("lblsite"); // NOI18N

        lblcc.setText("cc:");
        lblcc.setName("lblcc"); // NOI18N

        jLabel2.setText("Item:");
        jLabel2.setName("lblitem"); // NOI18N

        jLabel6.setText("EffDate");
        jLabel6.setName("lbleffdate"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Serial/Lot:");
        jLabel7.setName("lblserial"); // NOI18N

        jLabel5.setText("Qty:");
        jLabel5.setName("lblqty"); // NOI18N

        dcdate.setDateFormatString("yyyy-MM-dd");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "issue", "receipt" }));
        ddtype.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddtypeItemStateChanged(evt);
            }
        });

        jLabel1.setText("Type:");
        jLabel1.setName("lbltype"); // NOI18N

        jLabel4.setText("Location:");
        jLabel4.setName("lblloc"); // NOI18N

        btadd.setText("Commit");
        btadd.setName("btcommit"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        lblacct.setText("Acct:");
        lblacct.setName("lblacct"); // NOI18N

        jLabel9.setText("Remarks:");
        jLabel9.setName("lblremarks"); // NOI18N

        jLabel8.setText("Reference:");
        jLabel8.setName("lblref"); // NOI18N

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel10.setText("Warehouse:");
        jLabel10.setName("lblwh"); // NOI18N

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        btLookUpAcctDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAcctDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAcctDescActionPerformed(evt);
            }
        });

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel11.setText("Expire");
        jLabel11.setName("lblexpiredate"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(lblacct)
                    .addComponent(lblcc)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btadd)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ddwh, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddcc, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddacct, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbpart, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tblotserial, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbref, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(dcexpire, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btLookUpAcctDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(btLookUpItemDesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblacct))
                            .addComponent(btLookUpAcctDesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblcc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        boolean proceed = true;
        boolean isError = false;
        String type = "";
        String op = "";
        double qty = 0;
        double totalcost = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String loc = "";
        String wh = "";
        String acct = "";
        String cc = "";
        String site = "";
        
        if (ddloc.getSelectedItem() != null)
        loc = ddloc.getSelectedItem().toString();
        
        if (ddwh.getSelectedItem() != null)
        wh = ddwh.getSelectedItem().toString();
        
        if (ddacct.getSelectedItem() != null)
        acct = ddacct.getSelectedItem().toString();
        
        if (ddcc.getSelectedItem() != null)
        cc = ddcc.getSelectedItem().toString();
        
        if (ddsite.getSelectedItem() != null)
        site = ddsite.getSelectedItem().toString();
        
        
        if (tbpart.getText().isEmpty()) {
            tbpart.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1024, tbpart.getName()));
            tbpart.requestFocus();
            return;
        }
        
        if (! tbqty.getText().isEmpty()) {
            qty = Double.valueOf(tbqty.getText());
        } else {
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1036));
            tbqty.requestFocus();
            return;
        }
        
         
        
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            type = "ISS-MISC";
            qty = (-1 * qty);
        } else {
            type = "RCT-MISC";
            qty = qty;
        }
        
        
        // all inventory transactions performed in base currency
        String basecurr = OVData.getDefaultCurrency();
        
        
        // check if item exists
        if (! OVData.isValidItem(tbpart.getText())) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1026, tbpart.getText()));
            return;
        }
        
        // get cost
        double cost = invData.getItemCost(tbpart.getText(), "standard", site);
        
        // lets get the productline of the part being adjusted
        String prodline = OVData.getProdLineFromItem(tbpart.getText());
        
        if ( prodline == null || prodline.isEmpty() ) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1066, tbpart.getText()));
            return;
        }
        
        String invacct = OVData.getProdLineInvAcct(prodline);
        
        if (invacct.isEmpty()) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1067, prodline));
        }
        
        if (cost == 0.00) {
            proceed = false;
            bsmf.MainFrame.show(getMessageTag(1068));
        }
        
        
        if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
                    proceed = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return;
        }
        String expire = "";
        if (dcexpire.getDate() != null) {
            expire = dfdate.format(dcexpire.getDate());
        }
        
        if (proceed) {
        // now lets do the tran_hist write
           isError = OVData.TRHistIssDiscrete(dcdate.getDate(), tbpart.getText(), qty, op, type, 0.00, cost, 
                site, loc, wh, expire,
                "", "", "", 0, "", "", tblotserial.getText(), tbrmks.getText(), tbref.getText(), 
                acct, cc, "", "", "InventoryMaint", bsmf.MainFrame.userid);
        
        if (! isError) {
            isError = OVData.UpdateInventoryDiscrete(tbpart.getText(), site, loc, wh, tblotserial.getText(), expire, Double.valueOf(qty)); 
        } else {
            bsmf.MainFrame.show(getMessageTag(1010, "TRHistIssDiscrete"));
        }
        
       
        if (! isError) {
            if (ddtype.getSelectedItem().toString().equals("issue")) {
                fglData.glEntry(invacct, prodline, acct, cc,  
                        dfdate.format(dcdate.getDate()), (cost * Double.valueOf(tbqty.getText())), (cost * Double.valueOf(tbqty.getText())), basecurr, basecurr, tbref.getText() , site, type, tbrmks.getText());
            } else {
                fglData.glEntry(ddacct.getSelectedItem().toString(), ddcc.getSelectedItem().toString(), invacct, prodline, 
                        dfdate.format(dcdate.getDate()), (cost * Double.valueOf(tbqty.getText())), (cost * Double.valueOf(tbqty.getText())), basecurr, basecurr, tbref.getText() , site, type, tbrmks.getText());
            }
        } else {
          bsmf.MainFrame.show(getMessageTag(1010, "UpdateInventoryDiscrete"));  
        }
        if (! isError)
            bsmf.MainFrame.show(getMessageTag(1065));
            else
            bsmf.MainFrame.show(getMessageTag(1010, "glentry"));
        
        } // proceed
        
        
        
        
    }//GEN-LAST:event_btaddActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
        if (! tbpart.getText().isEmpty()) {
            if (! OVData.isValidItem(tbpart.getText())) {
                bsmf.MainFrame.show(getMessageTag(1021, tbpart.getText()));
                tbpart.setBackground(Color.yellow);
                tbpart.requestFocus();
            } else {
              tbpart.setBackground(Color.white);
              getiteminfo(tbpart.getText());   
             }
        }
    }//GEN-LAST:event_tbpartFocusLost

    private void ddtypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddtypeItemStateChanged
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            lblacct.setText("Debit Acct:");
            lblcc.setText("Debit CC:");
        } else {
            lblacct.setText("Credit Acct:");
            lblcc.setText("Credit CC:");
        }
    }//GEN-LAST:event_ddtypeItemStateChanged

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
      if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpAcctDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAcctDescActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpAcctDescActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpAcctDesc;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcexpire;
    private static javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcc;
    private javax.swing.JComboBox ddloc;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox<String> ddwh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblacct;
    private javax.swing.JLabel lblcc;
    private javax.swing.JTextField tblotserial;
    private static javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
