/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.inv;

import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class WorkCenterMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form WorkCenterMaintPanel
     */
    public WorkCenterMaintPanel() {
        initComponents();
    }

    
    public void enableAll() {
         ddsite.setEnabled(true);
        ddcc.setEnabled(true);
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
         tbwc.setEnabled(true);
        tbmachine.setEnabled(true);
        tbdesc.setEnabled(true);
        tbsetupcrewsize.setEnabled(true);
       
        tarmks.setEnabled(true);
        tbrunrate.setEnabled(true);
        tbsetuprate.setEnabled(true);
        tbbdnrate.setEnabled(true);
        tbruncrewsize.setEnabled(true);
        tbsetupcrewsize.setEnabled(true);
    }
    
    public void disableAll() {
        ddsite.setEnabled(false);
        ddcc.setEnabled(false);
         btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
         tbwc.setEnabled(false);
        tbmachine.setEnabled(false);
        tbdesc.setEnabled(false);
       tbsetupcrewsize.setEnabled(false);
        tarmks.setEnabled(false);
        tbrunrate.setEnabled(false);
        tbsetuprate.setEnabled(false);
        tbbdnrate.setEnabled(false);
        tbruncrewsize.setEnabled(false);
    }
    
    public void clearAll() {
         tbwc.setText("");
        tbmachine.setText("");
        tbdesc.setText("");
       
        tarmks.setText("");
        tbrunrate.setText("0.00");
        tbsetuprate.setText("0.00");
        tbbdnrate.setText("0.00");
        tbruncrewsize.setText("1");
        tbsetupcrewsize.setText("1");
        
         ArrayList cc = OVData.getGLCCList();
        for (int i = 0; i < cc.size(); i++) {
          ddcc.addItem(cc.get(i));
        }
        
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
        
        
        String[] args = null;
        if (! arg.isEmpty()) {
            args = arg.split(",",-1);
            getWorkCell(args[0], args[1]);
        }
    }
    
      public void getWorkCell(String wc, String mch) {
        initvars("");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from wc_mstr where wc_cell = " + "'" + wc + "'" + " AND " + 
                        " wc_mch = " + "'" + mch + "'" + ";");
                        
                while (res.next()) {
                    i++;
                    tbwc.setText(res.getString("wc_cell"));
                    tbmachine.setText(res.getString("wc_mch"));
                    tbdesc.setText(res.getString("wc_desc"));
                    ddsite.setSelectedItem(res.getString("wc_site"));
                    ddcc.setSelectedItem(res.getString("wc_cc"));
                    tarmks.setText(res.getString("wc_remarks"));
                    tbrunrate.setText(res.getString("wc_run_rate"));
                    tbsetuprate.setText(res.getString("wc_setup_rate"));
                    tbbdnrate.setText(res.getString("wc_bdn_rate"));
                    tbruncrewsize.setText(res.getString("wc_run_crew"));
                    tbsetupcrewsize.setText(res.getString("wc_setup"));
                }
               
                if (i > 0) {
                    enableAll();
                    tbwc.setEnabled(false);
                    btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to retrieve wc_mstr");
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
        tbrunrate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbbdnrate = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        ddcc = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tbwc = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tarmks = new javax.swing.JTextArea();
        tbmachine = new javax.swing.JTextField();
        tbsetuprate = new javax.swing.JTextField();
        tbruncrewsize = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();
        tbsetupcrewsize = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();
        btbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Work Cell Maintenance"));

        tbrunrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrunrateFocusLost(evt);
            }
        });

        jLabel6.setText("Labor Rate");

        tbbdnrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbbdnrateFocusLost(evt);
            }
        });

        jLabel9.setText("Burden Rate");

        jLabel8.setText("Setup Rate");

        jLabel4.setText("Work Cell");

        jLabel7.setText("Run Crew Size");

        jLabel10.setText("Remarks");

        jLabel1.setText("Dept/CC");

        jLabel3.setText("Machine");

        tarmks.setColumns(20);
        tarmks.setRows(5);
        jScrollPane1.setViewportView(tarmks);

        tbsetuprate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetuprateFocusLost(evt);
            }
        });

        tbruncrewsize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbruncrewsizeFocusLost(evt);
            }
        });

        jLabel2.setText("Site");

        jLabel5.setText("Description");

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        tbsetupcrewsize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsetupcrewsizeFocusLost(evt);
            }
        });

        jLabel11.setText("Setup Crew Size");

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
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbsetuprate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbrunrate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbmachine, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbwc, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(18, 18, 18)
                                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbbdnrate, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(24, 24, 24)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel7)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(tbruncrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(ddcc, 0, 121, Short.MAX_VALUE)
                                                .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbsetupcrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnew)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbwc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbmachine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrunrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tbruncrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsetuprate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tbsetupcrewsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbdnrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
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
                if (tbwc.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a work cell");
                }
                
                if (tbmachine.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a machine code");
                }
                
                if (proceed) {

                    res = st.executeQuery("select * from wc_mstr where wc_cell = " + "'" + tbwc.getText() + "'" + " AND " + 
                        " wc_mch = " + "'" + tbmachine.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into wc_mstr "
                            + " values ( " + "'" + tbwc.getText() + "'" + ","
                            + "'" + tbmachine.getText() + "'" + ","
                                + "'" + tbdesc.getText() + "'" + ","
                                + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                                + "'" + ddcc.getSelectedItem().toString() + "'" + ","
                                + "'" + tbrunrate.getText() + "'" + ","
                                + "'" + tbruncrewsize.getText() + "'" + ","
                                + "'" + tbsetuprate.getText() + "'" + ","
                                + "'" + tbsetupcrewsize.getText() + "'" + ","
                                + "'" + tbbdnrate.getText() + "'" + ","
                                + "'" + tarmks.getText() + "'" 
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Work Cell/Machine Record");
                    } else {
                        bsmf.MainFrame.show("Work Cell/Machine Already Exists");
                    }

                   initvars("");
                   
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to Add to wc_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
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
                if (tbwc.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a work cell");
                }
                if (tbmachine.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a machine code");
                }
                
                if (proceed) {
                    st.executeUpdate("update wc_mstr set wc_desc = " + "'" + tbdesc.getText() + "'" + ","
                            + "wc_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + "," 
                            + "wc_cc = " + "'" + ddcc.getSelectedItem().toString() + "'" + ","
                            + "wc_run_rate = " + "'" + tbrunrate.getText() + "'" + ","
                            + "wc_setup_rate = " + "'" + tbsetuprate.getText() + "'" + ","
                            + "wc_bdn_rate = " + "'" + tbbdnrate.getText() + "'" + ","
                            + "wc_run_crew = " + "'" + tbruncrewsize.getText() + "'" + ","
                            + "wc_setup = " + "'" + tbsetupcrewsize.getText() + "'" + ","
                            + "wc_remarks = " + "'" + tarmks.getText() + "'"
                            + " where wc_cell = " + "'" + tbwc.getText() + "'" + " AND "
                            + " wc_mch = " + "'" + tbmachine.getText() + "'"
                            + ";");
                    bsmf.MainFrame.show("Updated WorkCell/Machine");
                    initvars("");
                } 
         
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Problem updating wc_mstr");
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
              
                   int i = st.executeUpdate("delete from wc_mstr where wc_cell = " + "'" + tbwc.getText() + "'" + " AND " +
                           " wc_mch = " + "'" + tbmachine.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted WorkCell/Machine " + tbwc.getText() + "/" + tbmachine.getText());
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete WorkCell Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "wcmaint,wc_cell");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        tbwc.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed

    private void tbrunrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrunrateFocusLost
         String x = BlueSeerUtils.bsformat("", tbrunrate.getText(), "2");
        if (x.equals("error")) {
            tbrunrate.setText("");
            tbrunrate.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbrunrate.requestFocus();
        } else {
            tbrunrate.setText(x);
            tbrunrate.setBackground(Color.white);
        }
        if (tbrunrate.getText().isEmpty()) {
            tbrunrate.setText("0.00");
        }
    }//GEN-LAST:event_tbrunrateFocusLost

    private void tbsetuprateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetuprateFocusLost
          String x = BlueSeerUtils.bsformat("", tbsetuprate.getText(), "2");
        if (x.equals("error")) {
            tbsetuprate.setText("");
            tbsetuprate.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsetuprate.requestFocus();
        } else {
            tbsetuprate.setText(x);
            tbsetuprate.setBackground(Color.white);
        }
        if (tbsetuprate.getText().isEmpty()) {
            tbsetuprate.setText("0.00");
        }
    }//GEN-LAST:event_tbsetuprateFocusLost

    private void tbbdnrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbbdnrateFocusLost
          String x = BlueSeerUtils.bsformat("", tbbdnrate.getText(), "2");
        if (x.equals("error")) {
            tbbdnrate.setText("");
            tbbdnrate.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbbdnrate.requestFocus();
        } else {
            tbbdnrate.setText(x);
            tbbdnrate.setBackground(Color.white);
        }
        if (tbbdnrate.getText().isEmpty()) {
            tbbdnrate.setText("0.00");
        }
    }//GEN-LAST:event_tbbdnrateFocusLost

    private void tbruncrewsizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbruncrewsizeFocusLost
        String x = BlueSeerUtils.bsformat("", tbruncrewsize.getText(), "0");
        if (x.equals("error")) {
            tbruncrewsize.setText("");
            tbruncrewsize.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbruncrewsize.requestFocus();
        } else {
            tbruncrewsize.setText(x);
            tbruncrewsize.setBackground(Color.white);
        }
        if (tbruncrewsize.getText().isEmpty()) {
            tbruncrewsize.setText("1");
        }
    }//GEN-LAST:event_tbruncrewsizeFocusLost

    private void tbsetupcrewsizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsetupcrewsizeFocusLost
        String x = BlueSeerUtils.bsformat("", tbsetupcrewsize.getText(), "0");
        if (x.equals("error")) {
            tbsetupcrewsize.setText("");
            tbsetupcrewsize.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbsetupcrewsize.requestFocus();
        } else {
            tbsetupcrewsize.setText(x);
            tbsetupcrewsize.setBackground(Color.white);
        }
        if (tbsetupcrewsize.getText().isEmpty()) {
            tbsetupcrewsize.setText("1");
        }
    }//GEN-LAST:event_tbsetupcrewsizeFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddcc;
    private javax.swing.JComboBox<String> ddsite;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea tarmks;
    private javax.swing.JTextField tbbdnrate;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbmachine;
    private javax.swing.JTextField tbruncrewsize;
    private javax.swing.JTextField tbrunrate;
    private javax.swing.JTextField tbsetupcrewsize;
    private javax.swing.JTextField tbsetuprate;
    private javax.swing.JTextField tbwc;
    // End of variables declaration//GEN-END:variables
}
