/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
package com.blueseer.hrm;

import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class ShiftMaintenance extends javax.swing.JPanel {

    /**
     * Creates new form ShiftMaintenance
     */
    public ShiftMaintenance() {
        initComponents();
    }

      public void getShiftCode(String mykey) {
       // initvars(null);
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from shift_mstr where shf_id = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    tbcode.setText(mykey);
                    tbdesc.setText(res.getString("shf_desc"));
                    ddmonin.setSelectedItem(res.getString("shf_mon_intime").substring(0, 5));
                    ddmonout.setSelectedItem(res.getString("shf_mon_outtime").substring(0, 5));
                    ddtuein.setSelectedItem(res.getString("shf_tue_intime").substring(0, 5));
                    ddtueout.setSelectedItem(res.getString("shf_tue_outtime").substring(0, 5));
                    ddwedin.setSelectedItem(res.getString("shf_wed_intime").substring(0, 5));
                    ddwedout.setSelectedItem(res.getString("shf_wed_outtime").substring(0, 5));
                    ddthuin.setSelectedItem(res.getString("shf_thu_intime").substring(0, 5));
                    ddthuout.setSelectedItem(res.getString("shf_thu_outtime").substring(0, 5));
                    ddfriin.setSelectedItem(res.getString("shf_fri_intime").substring(0, 5));
                    ddfriout.setSelectedItem(res.getString("shf_fri_outtime").substring(0, 5));
                    ddsatin.setSelectedItem(res.getString("shf_sat_intime").substring(0, 5));
                    ddsatout.setSelectedItem(res.getString("shf_sat_outtime").substring(0, 5));
                    ddsunin.setSelectedItem(res.getString("shf_sun_intime").substring(0, 5));
                    ddsatout.setSelectedItem(res.getString("shf_sun_outtime").substring(0, 5));
                }

            if (i > 0) {
                    enableAll();
                    btadd.setEnabled(false);
                }
            
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve shift_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    
    public ArrayList setddtimelist() {
        ArrayList<String> time = new ArrayList<String>();
        String minutes = "";
        String hours = "";
        for (int i = 0; i < 24 ; i++) {
            hours = String.format("%02d", i);
            for (int j = 0; j < 12; j++) {
                minutes = String.format("%02d", (j * 5));
                time.add(hours + ":" + minutes);
            }
        }
        time.add("24:00");
        return time;
    }
    
    
    public void enableAll() {
        ddmonin.setEnabled(true);
        ddmonout.setEnabled(true);
        ddtuein.setEnabled(true);
        ddtueout.setEnabled(true);
        ddwedin.setEnabled(true);
        ddwedout.setEnabled(true);
        ddthuin.setEnabled(true);
        ddthuout.setEnabled(true);
        ddfriin.setEnabled(true);
        ddfriout.setEnabled(true);
        ddsatin.setEnabled(true);
        ddsatout.setEnabled(true);
        ddsunin.setEnabled(true);
        ddsunout.setEnabled(true);
        btnew.setEnabled(true);
        btadd.setEnabled(true);
        btdelete.setEnabled(true);
        btupdate.setEnabled(true);
        btbrowse.setEnabled(true);
        tbdesc.setEnabled(true);
        tbcode.setEnabled(true);
        
    }
    public void disableAll() {
       ddmonin.setEnabled(false);
        ddmonout.setEnabled(false);
        ddtuein.setEnabled(false);
        ddtueout.setEnabled(false);
        ddwedin.setEnabled(false);
        ddwedout.setEnabled(false);
        ddthuin.setEnabled(false);
        ddthuout.setEnabled(false);
        ddfriin.setEnabled(false);
        ddfriout.setEnabled(false);
        ddsatin.setEnabled(false);
        ddsatout.setEnabled(false);
        ddsunin.setEnabled(false);
        ddsunout.setEnabled(false);
        btnew.setEnabled(false);
        btadd.setEnabled(false);
        btdelete.setEnabled(false);
        btupdate.setEnabled(false);
        btbrowse.setEnabled(false); 
         tbdesc.setEnabled(false);
        tbcode.setEnabled(false);
    }
    public void clearAll() {
        
        tbdesc.setText("");
        tbcode.setText("");
        
         java.util.Date now = new java.util.Date();
        
        ArrayList<String> ddtime = setddtimelist();
        for (String dd : ddtime) {
           ddmonin.addItem(dd);
           ddmonout.addItem(dd);
           ddtuein.addItem(dd);
           ddtueout.addItem(dd);
           ddwedin.addItem(dd);
           ddwedout.addItem(dd);
           ddthuin.addItem(dd);
           ddthuout.addItem(dd);
           ddfriin.addItem(dd);
           ddfriout.addItem(dd);
           ddsatin.addItem(dd);
           ddsatout.addItem(dd);
           ddsunin.addItem(dd);
           ddsunout.addItem(dd);
        }
        
        ddmonin.setSelectedItem("00:00");
        ddmonout.setSelectedItem("00:00");
        ddtuein.setSelectedItem("00:00");
        ddtueout.setSelectedItem("00:00");
        ddwedin.setSelectedItem("00:00");
        ddwedout.setSelectedItem("00:00");
        ddthuin.setSelectedItem("00:00");
        ddthuout.setSelectedItem("00:00");
        ddfriin.setSelectedItem("00:00");
        ddfriout.setSelectedItem("00:00");
        ddsatin.setSelectedItem("00:00");
        ddsatout.setSelectedItem("00:00");
        ddsunin.setSelectedItem("00:00");
        ddsunout.setSelectedItem("00:00");
    }
    
    
     public void initvars(String[] arg) {
       
         clearAll();
          disableAll();
          btbrowse.setEnabled(true);
          btnew.setEnabled(true);
        
        if (arg != null && arg.length > 0) {
            getShiftCode(arg[0]);
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
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbcode = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddmonin = new javax.swing.JComboBox();
        ddtuein = new javax.swing.JComboBox();
        ddwedin = new javax.swing.JComboBox();
        ddthuin = new javax.swing.JComboBox();
        ddfriin = new javax.swing.JComboBox();
        ddsatin = new javax.swing.JComboBox();
        ddsunin = new javax.swing.JComboBox();
        ddmonout = new javax.swing.JComboBox();
        ddtueout = new javax.swing.JComboBox();
        ddwedout = new javax.swing.JComboBox();
        ddthuout = new javax.swing.JComboBox();
        ddfriout = new javax.swing.JComboBox();
        ddsatout = new javax.swing.JComboBox();
        ddsunout = new javax.swing.JComboBox();
        tbdesc = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shift Maintenance"));

        jLabel8.setText("Sunday");

        jLabel1.setText("Shift ID");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel5.setText("Thursday");

        jLabel7.setText("Saturday");

        jLabel2.setText("Monday");

        jLabel6.setText("Friday");

        jLabel4.setText("Wednesday");

        jLabel3.setText("Tuesday");

        jLabel9.setText("Description");

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

        btdelete.setText("Delete");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btdelete)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btupdate)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btadd))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(ddmonin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(43, 43, 43)
                                    .addComponent(jLabel4))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(ddtuein, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(ddwedin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(ddthuin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6)
                                .addComponent(ddfriin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(ddsatin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ddsunin, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddmonout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddtueout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddwedout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddthuout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddfriout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddsatout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ddsunout, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew))
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmonin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtuein, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddwedin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddthuin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddfriin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsatin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsunin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmonout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddtueout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddwedout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddthuout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddfriout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsatout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsunout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btupdate)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
       try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                // check the site field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a shift code");
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT shf_id FROM  shift_mstr where shf_id = " + "'" + tbcode.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into shift_mstr "
                            + "(shf_id, shf_desc, shf_mon_intime, shf_mon_outtime, shf_tue_intime, shf_tue_outtime, "
                            + " shf_wed_intime, shf_wed_outtime, shf_thu_intime, shf_thu_outtime, shf_fri_intime, shf_fri_outtime, "
                            + " shf_sat_intime, shf_sat_outtime, shf_sun_intime, shf_sun_outtime ) "
                            + " values ( " + "'" + tbcode.getText().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString() + "'" + ","
                                + "'" + ddmonin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddmonout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddtuein.getSelectedItem().toString() + "'" + ","
                                + "'" + ddtueout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddwedin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddwedout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddthuin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddthuout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddfriin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddfriout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddsatin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddsatout.getSelectedItem().toString() + "'" + ","
                                + "'" + ddsunin.getSelectedItem().toString() + "'" + ","
                                + "'" + ddsunout.getSelectedItem().toString() + "'"
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Shift Record");
                    } else {
                        bsmf.MainFrame.show("Shift Record Already Exists");
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to shift_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {
            boolean proceed = true;
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                   
                // check the code field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a code");
                }
                
                if (proceed) {
                    st.executeUpdate("update shift_mstr set shf_desc = " + "'" + tbdesc.getText() + "'" + ","
                            + "shf_mon_intime = " + "'" + ddmonin.getSelectedItem().toString() + "'" + ","
                            + "shf_mon_outtime = " + "'" + ddmonout.getSelectedItem().toString() + "'" + ","
                            + "shf_tue_intime = " + "'" + ddtuein.getSelectedItem().toString() + "'" + ","
                            + "shf_tue_outtime = " + "'" + ddtueout.getSelectedItem().toString() + "'" + ","
                            + "shf_wed_intime = " + "'" + ddwedin.getSelectedItem().toString() + "'" + ","
                            + "shf_wed_outtime = " + "'" + ddwedout.getSelectedItem().toString() + "'" + ","
                            + "shf_thu_intime = " + "'" + ddthuin.getSelectedItem().toString() + "'" + ","
                            + "shf_thu_outtime = " + "'" + ddthuout.getSelectedItem().toString() + "'" + ","
                            + "shf_fri_intime = " + "'" + ddfriin.getSelectedItem().toString() + "'" + ","
                            + "shf_fri_outtime = " + "'" + ddfriout.getSelectedItem().toString() + "'" + ","
                            + "shf_sat_intime = " + "'" + ddsatin.getSelectedItem().toString() + "'" + ","
                            + "shf_sat_outtime = " + "'" + ddsatout.getSelectedItem().toString() + "'" + ","
                            + "shf_sun_intime = " + "'" + ddsunin.getSelectedItem().toString() + "'" + ","
                            + "shf_sun_outtime = " + "'" + ddsunout.getSelectedItem().toString() + "'" 
                            + " where shf_id = " + "'" + tbcode.getText() + "'"                             
                            + ";");
                    bsmf.MainFrame.show("Updated Shift Code");
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating shift_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "shiftmaint,shf_id");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
         clearAll();
        enableAll();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddfriin;
    private javax.swing.JComboBox ddfriout;
    private javax.swing.JComboBox ddmonin;
    private javax.swing.JComboBox ddmonout;
    private javax.swing.JComboBox ddsatin;
    private javax.swing.JComboBox ddsatout;
    private javax.swing.JComboBox ddsunin;
    private javax.swing.JComboBox ddsunout;
    private javax.swing.JComboBox ddthuin;
    private javax.swing.JComboBox ddthuout;
    private javax.swing.JComboBox ddtuein;
    private javax.swing.JComboBox ddtueout;
    private javax.swing.JComboBox ddwedin;
    private javax.swing.JComboBox ddwedout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbdesc;
    // End of variables declaration//GEN-END:variables
}
