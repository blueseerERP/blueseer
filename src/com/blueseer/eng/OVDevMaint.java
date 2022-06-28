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

package com.blueseer.eng;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.OVData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class OVDevMaint extends javax.swing.JPanel {

    /**
     * Creates new form ShipConfirmPanel
     */
    public OVDevMaint() {
        initComponents();
    }

    public void initvars(String[] arg) {
        java.util.Date now = new java.util.Date();
                dcdate.setDate(now);
                log.setText("");
    }
    
   
    
       public void getdev(String parent) {
        log.setText("");
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
                String mystring = "";
                  res = st.executeQuery("Select * from ov_devm where ovdm_id = " + "'" + parent.toString() + "'" + ";" );
                    while (res.next()) {
                       tbid.setText(res.getString("ovdm_id"));
                       ddstatus.setSelectedItem(res.getString("ovdm_status"));
                       ddtype.setSelectedItem(res.getString("ovdm_category"));
                       entry.setText(res.getString("ovdm_title"));
                    }
                    getnotes(parent);
                      } catch (SQLException s) {
                          MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot retrieve notes from ov_devd");
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
    
    public void getnotes(String parent) {
        log.setText("");
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
                String mystring = "";
                  res = st.executeQuery("Select * from ov_devd where ovdd_parent = " + "'" + tbid.getText().toString() + "'" + ";" );
                    while (res.next()) {
                        log.append(res.getString("ovdd_date") + "  " + res.getString("ovdd_note"));
                        log.append("\n");
                    }
                      } catch (SQLException s) {
                          MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot retrieve notes from ov_devd");
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbid = new javax.swing.JTextField();
        dcdate = new com.toedter.calendar.JDateChooser();
        btnew = new javax.swing.JButton();
        ddtype = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        ddstatus = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        entry = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        log = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();
        btaddnote = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jLabel1.setText("ID:");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        dcdate.setDateFormatString("yyyy-MM-dd");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "company", "personal", "other" }));

        jLabel2.setText("Type:");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "open", "closed", "scrapped" }));

        jLabel3.setText("Status:");

        entry.setColumns(20);
        entry.setRows(5);
        jScrollPane1.setViewportView(entry);

        log.setColumns(20);
        log.setRows(5);
        jScrollPane2.setViewportView(log);

        notes.setColumns(20);
        notes.setRows(5);
        jScrollPane3.setViewportView(notes);

        btaddnote.setText("Add Note to Entry");
        btaddnote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddnoteActionPerformed(evt);
            }
        });

        jLabel4.setText("Entry:");

        jLabel5.setText("Note:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbid)
                                    .addComponent(ddstatus, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ddtype, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btupdate))
                                    .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(btaddnote))
                        .addGap(0, 119, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnew)
                                    .addComponent(jLabel1)
                                    .addComponent(btupdate))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddnote))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
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
                boolean proceed = true;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                java.util.Date closedate = null;
                String closedatestring = null;
                
                if (ddstatus.getSelectedItem().equals("closed") || ddstatus.getSelectedItem().equals("scrapped")) {
                    closedate = now;
                   closedatestring = "'" + dfdate.format(now).toString() + "'";
               }
                
                
                  res = st.executeQuery("Select * from ov_devm where ovdm_id = " + "'" + tbid.getText().toString() + "'" + ";" );
                    while (res.next()) {
                        i++;
                    }
                   
                    if (i == 0) {
                      st.executeUpdate("insert into ov_devm "
                        + "(ovdm_id, ovdm_status, ovdm_title, ovdm_entry_date, "
                        + " ovdm_category, ovdm_user ) "
                        + " values ( " + "'" + tbid.getText().toString() + "'" + ","
                        + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                        + "'" + entry.getText() + "'" + ","
                        + "'" + dfdate.format(dcdate.getDate()).toString() + "'" + ","
                        + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                        + "'" + bsmf.MainFrame.userid.toString() + "'" 
                                + ")"
                        + ";");
                        bsmf.MainFrame.show("Added new record");
                    } else {
                        
                    st.executeUpdate("update ov_devm "
                        + " set ovdm_status = " + "'" + ddstatus.getSelectedItem() + "'" + ","
                        + " ovdm_title = " + "'" + entry.getText() + "'" + ","
                        + " ovdm_close_date = "  + closedatestring + ","
                        + "ovdm_category = " + "'" + ddtype.getSelectedItem() + "'" 
                        + " where ovdm_id = " + "'" + tbid.getText().toString() + "'"
                        + ";");
                        bsmf.MainFrame.show("Updated record");
                                
                    }
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot add or update ov_devm");
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
    }//GEN-LAST:event_btupdateActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       tbid.setText(String.valueOf(OVData.getNextNbr("ovdev")));
                java.util.Date now = new java.util.Date();
                dcdate.setDate(now);
                tbid.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddnoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddnoteActionPerformed
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
                boolean proceed = false;
                int i = 0;
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date now = new java.util.Date();
                java.util.Date closedate = null;
                String closedatestring = null;
                
                if (ddstatus.getSelectedItem().equals("closed") || ddstatus.getSelectedItem().equals("scrapped")) {
                    closedate = now;
                   closedatestring = "'" + dfdate.format(now).toString() + "'";
               }
                
                
                  res = st.executeQuery("Select * from ov_devm where ovdm_id = " + "'" + tbid.getText().toString() + "'" + ";" );
                    while (res.next()) {
                        proceed = true;
                    }
                   
                    if (proceed) {
                      st.executeUpdate("insert into ov_devd "
                        + "(ovdd_parent, ovdd_note ) "
                        + " values ( " + "'" + tbid.getText().toString() + "'" + ","
                        + "'" + notes.getText() + "'" 
                        + ")"
                        + ";");
                      getnotes(tbid.getText().toString());
                        bsmf.MainFrame.show("Added Note");
                    } 
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot note to ov_devd");
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
    }//GEN-LAST:event_btaddnoteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btaddnote;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private com.toedter.calendar.JDateChooser dcdate;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JTextArea entry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea log;
    private javax.swing.JTextArea notes;
    private javax.swing.JTextField tbid;
    // End of variables declaration//GEN-END:variables
}
