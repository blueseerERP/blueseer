/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.inv;

import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class RoutingMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form RoutingMaintPanel
     */
    public RoutingMaintPanel() {
        initComponents();
    }

    
    public void enableAll() {
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        tbrouting.setEnabled(true);
        ddop.setEnabled(true);
        tbwc.setEnabled(true);
        tbmch.setEnabled(true);
        tbopdesc.setEnabled(true);
        tbrunhoursinverted.setEnabled(true);
        ddsite.setEnabled(true);
        tbrunhours.setEnabled(true);
        tbsetuphours.setEnabled(true);
        cbmilestone.setEnabled(true); 
    }
    
    public void disableAll() {
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        tbrouting.setEnabled(false);
        ddop.setEnabled(false);
        tbwc.setEnabled(false);
        tbmch.setEnabled(false);
        tbopdesc.setEnabled(false);
        tbrunhoursinverted.setEnabled(false);
        ddsite.setEnabled(false);
        tbrunhours.setEnabled(false);
        tbsetuphours.setEnabled(false);
        cbmilestone.setEnabled(false);
    }
    
    public void clearAll() {
        tbrouting.setText("");
        ddop.removeAllItems();
        tbwc.setText("");
        tbmch.setText("");
        tbopdesc.setText("");
        tbrunhoursinverted.setText("");
       
        tbrunhours.setText("");
        tbsetuphours.setText("");
        cbmilestone.setSelected(false);
        
          ddsite.removeAllItems();
        ArrayList<String>mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
    }
    
    public void initvars(String arg) {
          clearAll();
          disableAll();
          btbrowse.setEnabled(true);
          btnew.setEnabled(true);
        
        if (! arg.isEmpty()) {
            String[] args = arg.split(",", -1);
            getRoutingAndOp(args[0],args[1]);
        }
    }
    
    public void initopvars() {
        tbwc.setText("");
        tbmch.setText("");
        tbopdesc.setText("");
        tbrunhours.setText("");
        tbsetuphours.setText("");
        tbrunhoursinverted.setText("");
      
        cbmilestone.setSelected(false);
    }
    
     public void getRouting(String routing) {
        initvars("");
        tbrouting.setText(routing);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from wf_mstr where wf_id = " + "'" + routing + "'"  + ";");
                        
                while (res.next()) {
                    i++;
                    ddop.addItem(res.getString("wf_op"));
                }
               
                if (i > 0) {
                    enableAll();
                    tbrouting.setEnabled(false);
                    btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to retrieve wf_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
      public void getRoutingAndOp(String routing, String op) {
        
        getRouting(routing);  // sets the possible operations
        ddop.setSelectedItem(op);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                double runhours = 0.00;
                double setuphours = 0.00;
                DecimalFormat df = new DecimalFormat("#0.00000");
                res = st.executeQuery("select * from wf_mstr where wf_id = " + "'" + routing + "'"  + " AND" +
                        " wf_op = " + "'" + op + "'" +  ";");
                        
                while (res.next()) {
                    i++;
                    
                    if (res.getDouble("wf_run_hours") > 0)
                        runhours = 1 / res.getDouble("wf_run_hours");
                    else
                        runhours = 0.00;
                    
                    if (res.getDouble("wf_setup_hours") > 0)
                        setuphours = 1 / res.getDouble("wf_setup_hours");
                    else
                        setuphours = 0.00;
                    
                    
                    tbwc.setText(res.getString("wf_cell"));
                    tbmch.setText(res.getString("wf_mch"));
                    tbopdesc.setText(res.getString("wf_op_desc"));
                    ddsite.setSelectedItem(res.getString("wf_site"));
                    tbrunhours.setText(res.getString("wf_run_hours"));
                    tbsetuphours.setText(res.getString("wf_setup_hours"));
                    tbrunhoursinverted.setText(String.valueOf(df.format(runhours)));
                    cbmilestone.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("wf_assert")));
                }
               
                 if (i > 0) {
                    enableAll();
                    tbrouting.setEnabled(false);
                    btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to retrieve wf_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tbopdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cbmilestone = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        tbmch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbrouting = new javax.swing.JTextField();
        tbwc = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddop = new javax.swing.JComboBox();
        tbrunhoursinverted = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbrunhours = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        tbsetuphours = new javax.swing.JTextField();
        ddsite = new javax.swing.JComboBox<>();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Routing Maintenance"));

        jLabel5.setText("Set Backflush:");

        jLabel8.setText("Machine:");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel3.setText("Operation Desc:");

        cbmilestone.setText("Auto Backflush?");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Routing ID:");

        jLabel6.setText("Operation:");

        ddop.setEditable(true);
        ddop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddopActionPerformed(evt);
            }
        });

        tbrunhoursinverted.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrunhoursinvertedFocusLost(evt);
            }
        });

        jLabel7.setText("Work Cell:");

        jLabel10.setText("Run Pieces/Hr");

        jLabel9.setText("Setup Hours Per (lotsize)");

        jLabel2.setText("Site:");

        tbrunhours.setEditable(false);

        jLabel12.setText("Run Hours");

        tbsetuphours.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetuphoursFocusLost(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btdelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btupdate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btadd))
                        .addComponent(cbmilestone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbopdesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbwc, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbmch, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbrunhoursinverted, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addComponent(tbsetuphours))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tbrunhours, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbrouting, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(tbrouting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbopdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbmilestone)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbwc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tbsetuphours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrunhoursinverted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(tbrunhours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void ddopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddopActionPerformed
        
        if (ddop != null && ddop.getItemCount() > 0) {
        initopvars();
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                double runhours = 0.00;
                double setuphours = 0.00;
                DecimalFormat df = new DecimalFormat("#0.00000");
                int i = 0;
                res = st.executeQuery("select * from wf_mstr where wf_id = " + "'" + tbrouting.getText() + "'"  + " AND " +
                        " wf_op = " + "'" + ddop.getSelectedItem().toString() + "'" + ";");
                        
                while (res.next()) {
                    i++;
                    
                    if (res.getDouble("wf_run_hours") > 0)
                        runhours = 1 / res.getDouble("wf_run_hours");
                    else
                        runhours = 0.00;
                    if (res.getDouble("wf_setup_hours") > 0)
                        setuphours = 1 / res.getDouble("wf_setup_hours");
                    else
                        setuphours = 0.00;
                    
                    tbwc.setText(res.getString("wf_cell"));
                    tbmch.setText(res.getString("wf_mch"));
                    tbopdesc.setText(res.getString("wf_op_desc"));
                    ddsite.setSelectedItem(res.getString("wf_site"));
                    tbrunhours.setText(res.getString("wf_run_hours"));
                    tbsetuphours.setText(res.getString("wf_setup_hours"));
                    tbrunhoursinverted.setText(String.valueOf(df.format(runhours)));
                    cbmilestone.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("wf_assert")));
                    
                    
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Routing / Op Record Found");

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve wf_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_ddopActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
             double setup = 0.00;
            double run = 0.00;
            double crewsize = 0.00;    
                // check the site field
                if (tbwc.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a work cell");
                    return;
                }
                if (! OVData.isValidWorkCenter(tbwc.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a valid work cell");
                    return;
                }
                
                
                if (tbmch.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a machine code");
                    return;
                }
                 if (! tbsetuphours.getText().isEmpty()) {
                    setup = Double.valueOf(tbsetuphours.getText());
                }
                if (! tbrunhours.getText().isEmpty()) {
                    run = Double.valueOf(tbrunhours.getText());
                }
                
                
                
                if (proceed) {

                    res = st.executeQuery("select * from wf_mstr where wf_id = " + "'" + tbrouting.getText() + "'" + " AND " + 
                        " wf_mch = " + "'" + ddop.getSelectedItem().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into wf_mstr "
                            + " values ( " + "'" + tbrouting.getText() + "'" + ","
                            + "'" + "notused" + "'" + ","
                            + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                            + "'" + ddop.getSelectedItem().toString() + "'" + ","
                            + "'" + BlueSeerUtils.boolToInt(cbmilestone.isSelected()) + "'" + ","
                            + "'" + tbopdesc.getText() + "'" + ","
                            + "'" + tbwc.getText() + "'" + ","
                            + "'" + tbmch.getText() + "'" + ","
                            + "'" + setup + "'" + ","
                            + "'" + run + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Routing / Op Record");
                    } else {
                        bsmf.MainFrame.show("Routing / Op Already Exists");
                    }

                   initvars("");
                   
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to Add to wf_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {
            boolean proceed = true;
            double setup = 0.00;
            double run = 0.00;
            double crewsize = 0.00;
            
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
                // check the code field
                if (tbwc.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a work cell");
                    return;
                }
                 if (! OVData.isValidWorkCenter(tbwc.getText())) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a valid work cell");
                    return;
                }
                if (tbmch.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a machine code");
                    return;
                }
                
                if (! tbsetuphours.getText().isEmpty()) {
                    setup = Double.valueOf(tbsetuphours.getText());
                }
                if (! tbrunhours.getText().isEmpty()) {
                    run = Double.valueOf(tbrunhours.getText());
                }
                
                if (proceed) {
                    st.executeUpdate("update wf_mstr set wf_desc = " + "'" + tbopdesc.getText() + "'" + ","
                            + "wf_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + "," 
                            + "wf_cell = " + "'" + tbwc.getText() + "'" + ","
                            + "wf_mch = " + "'" + tbmch.getText() + "'" + ","
                            + "wf_setup_hours = " + "'" + setup + "'" + ","
                            + "wf_run_hours = " + "'" + run + "'" + ","
                            + "wf_assert = " + "'" + BlueSeerUtils.boolToInt(cbmilestone.isSelected()) + "'"
                            + " where wf_id = " + "'" + tbrouting.getText() + "'" + " AND "
                            + " wf_op = " + "'" + ddop.getSelectedItem().toString() + "'"
                            + ";");
                    bsmf.MainFrame.show("Updated Routing/Op Record");
                    initvars("");
                } 
         
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Problem updating wf_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from wf_mstr where wf_id = " + "'" + tbrouting.getText() + "'" + " AND " +
                           " wf_op = " + "'" + ddop.getSelectedItem().toString() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted Routing/Op " + tbrouting.getText() + "/" + ddop.getSelectedItem().toString());
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete Routing/Op Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbrunhoursinvertedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrunhoursinvertedFocusLost
        DecimalFormat df = new DecimalFormat("#0.00000");
        if (Double.valueOf(tbrunhoursinverted.getText()) > 0)
        tbrunhours.setText(df.format(1 / Double.valueOf(tbrunhoursinverted.getText())));
        else
            tbrunhours.setText("0.00");
        
          String x = BlueSeerUtils.bsformat("", tbrunhoursinverted.getText(), "2");
        if (x.equals("error")) {
            tbrunhoursinverted.setText("");
            tbrunhoursinverted.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbrunhoursinverted.requestFocus();
        } else {
            tbrunhoursinverted.setText(x);
            tbrunhoursinverted.setBackground(Color.white);
        }
        if (tbrunhoursinverted.getText().isEmpty()) {
            tbrunhoursinverted.setText("0.00");
        }
        
        
    }//GEN-LAST:event_tbrunhoursinvertedFocusLost

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "routingmaint,wf_id");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        tbwc.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed

    private void tbsetuphoursFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetuphoursFocusLost
          String x = BlueSeerUtils.bsformat("", tbsetuphours.getText(), "2");
        if (x.equals("error")) {
            tbsetuphours.setText("");
            tbsetuphours.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsetuphours.requestFocus();
        } else {
            tbsetuphours.setText(x);
            tbsetuphours.setBackground(Color.white);
        }
        if (tbsetuphours.getText().isEmpty()) {
            tbsetuphours.setText("0.00");
        }
    }//GEN-LAST:event_tbsetuphoursFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbmilestone;
    private javax.swing.JComboBox ddop;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbmch;
    private javax.swing.JTextField tbopdesc;
    private javax.swing.JTextField tbrouting;
    private javax.swing.JTextField tbrunhours;
    private javax.swing.JTextField tbrunhoursinverted;
    private javax.swing.JTextField tbsetuphours;
    private javax.swing.JTextField tbwc;
    // End of variables declaration//GEN-END:variables
}
