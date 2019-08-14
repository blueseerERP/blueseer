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
package com.blueseer.adm;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author vaughnte
 */
public class UserMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form UserMaintPanel
     */


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

    
     public void disableAll() {
          tbUMuserid.setEnabled(false);
        tbUMLastName.setEnabled(false);
        tbUMFirstName.setEnabled(false);
        tbpassword.setEnabled(false);
        tbUMDept.setEnabled(false);
        tarmks.setEnabled(false);
        tbemail.setEnabled(false);
        tbphone.setEnabled(false);
        tbcell.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btadd.setEnabled(false);
        btbrowseUser.setEnabled(false);
        btbrowseLastName.setEnabled(false);
        btnew.setEnabled(false);  
         ddsite.setEnabled(false);
     }
     
     public void enableAll() {
         tbUMuserid.setEnabled(true);
        tbUMLastName.setEnabled(true);
        tbUMFirstName.setEnabled(true);
        tbpassword.setEnabled(true);
        tbUMDept.setEnabled(true);
        tarmks.setEnabled(true);
        tbemail.setEnabled(true);
        tbphone.setEnabled(true);
        tbcell.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btadd.setEnabled(true);
        btbrowseUser.setEnabled(true);
        btbrowseLastName.setEnabled(false);
        btnew.setEnabled(true);
        ddsite.setEnabled(true);
     }
     
     public void clearAll() {
          tbUMuserid.setText("");
        tbUMLastName.setText("");
        tbUMFirstName.setText("");
        tbpassword.setText("");
        tbUMDept.setText("");
        tarmks.setText("");
        tbemail.setText("");
        tbphone.setText("");
        tbcell.setText("");
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
     }
     
     public void initvars(String[] arg) {
        
        clearAll();
       
               
         if (arg != null && arg.length > 0) {
            getuserinfo(arg[0]);
            enableAll();
            btadd.setEnabled(false);
            tbUMuserid.setEnabled(false);
        } else {
              disableAll();
              btnew.setEnabled(true);
              btbrowseUser.setEnabled(true);
              btbrowseLastName.setEnabled(true);
          }
        
    }
    
    
     
     
     public void getuserinfo(String user) {
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  user_mstr where user_id = " + "'" + user + "'" + ";");
                    while (res.next()) {
                        i++;
                        tbUMLastName.setText(res.getString("user_lname"));
                        tbUMFirstName.setText(res.getString("user_fname"));
                        tbUMuserid.setText(res.getString("user_id"));
                        tbemail.setText(res.getString("user_email"));
                        tbphone.setText(res.getString("user_phone"));
                        tbcell.setText(res.getString("user_cell"));
                        tarmks.setText(res.getString("user_rmks"));
                        tbpassword.setText(res.getString("user_passwd"));
                        ddsite.setSelectedItem(res.getString("user_site"));
                       
                    }

                 if (i == 0) {
                    bsmf.MainFrame.show("USERID does not exists");
                 } else {
                     btupdate.setEnabled(true);
                     btdelete.setEnabled(true);
                 }
           
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve userid");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     }
     
     
           class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
           // if (table.getValueAt(row, 0).toString().compareTo("1923") == 0)   
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
    public UserMaintPanel() {
        initComponents();
        

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
        btadd = new javax.swing.JButton();
        btupdate = new javax.swing.JButton();
        tbUMuserid = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        tbUMLastName = new javax.swing.JTextField();
        tbUMFirstName = new javax.swing.JTextField();
        tbUMDept = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tarmks = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        taUMperms1 = new javax.swing.JTextArea();
        jLabel138 = new javax.swing.JLabel();
        tbemail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tbphone = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbcell = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        tbpassword = new javax.swing.JPasswordField();
        btnew = new javax.swing.JButton();
        btbrowseUser = new javax.swing.JButton();
        btbrowseLastName = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("User Maintenance"));

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

        jLabel46.setText("User ID");

        jLabel47.setText("LastName");

        jLabel48.setText("Dept");

        jLabel49.setText("FirstName");

        tarmks.setColumns(20);
        tarmks.setRows(5);
        jScrollPane6.setViewportView(tarmks);

        taUMperms1.setColumns(20);
        taUMperms1.setRows(5);
        jScrollPane7.setViewportView(taUMperms1);

        jLabel138.setText("Password");

        jLabel1.setText("email");

        jLabel2.setText("Phone");

        jLabel3.setText("Cell");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btbrowseUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowseUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseUserActionPerformed(evt);
            }
        });

        btbrowseLastName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowseLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseLastNameActionPerformed(evt);
            }
        });

        jLabel4.setText("Default Site");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel47)
                                    .addComponent(jLabel49)))
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbUMLastName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbUMFirstName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbemail)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 39, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel46)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbUMuserid, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(47, 47, 47)
                                        .addComponent(jLabel48)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbUMDept, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbcell, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btdelete)
                                .addGap(18, 18, 18)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btadd))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel138)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ddsite, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbpassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btbrowseUser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew))
                    .addComponent(btbrowseLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbUMuserid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46)
                            .addComponent(btnew))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbUMLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btbrowseUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btbrowseLastName)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbUMFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbUMDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel138))
                    .addComponent(tbpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addGap(30, 30, 30))
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

                if (proceed) {
                    String passwd = new String(tbpassword.getPassword());
                    st.executeUpdate("insert into user_mstr "
                        + "(user_id, user_site, user_lname, user_fname,"
                        + "user_mname, user_email, user_phone, user_cell, user_rmks, user_passwd) "
                        + "values ( " + "'" + tbUMuserid.getText().toString() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + tbUMLastName.getText() + "'" + ","        
                        + "'" + tbUMFirstName.getText() + "'" + ","
                        + null + ","
                        + "'" + tbemail.getText() + "'" + ","
                            + "'" + tbphone.getText() + "'" + ","
                            + "'" + tbcell.getText() + "'" + ","
                            + "'" + tarmks.getText() + "'" + ","
                        + "'" + passwd + "'"
                        + ")"
                        + ";");

                   
                    bsmf.MainFrame.show("Added UserID Record");
                    
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Cannot Add User Mstr");
            }
            bsmf.MainFrame.con.close();
            initvars(null);
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;


                if (proceed) {



                    res = st.executeQuery("SELECT user_id FROM  user_mstr where user_id = " + "'" + tbUMuserid.getText().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }

                    if (i > 0) {
                        String passwd = new String(tbpassword.getPassword().toString().replace("'", "''"));
                        st.executeUpdate("update user_mstr set "
                                + "user_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                                + "user_lname = " + "'" + tbUMLastName.getText().toString().replace("'", "''") + "'" + ","        
                                + "user_fname = " + "'" + tbUMFirstName.getText().toString() + "'" + ","
                                + "user_email = " + "'" + tbemail.getText().toString() + "'" + ","
                                + "user_phone = " + "'" + tbphone.getText().toString() + "'" + ","
                                + "user_cell = " + "'" + tbcell.getText().toString() + "'" + ","
                                + "user_passwd = " + "'" + passwd + "'" + ","
                                + "user_rmks = " + "'" + tarmks.getText().toString().replace("'", "''")  + "'"
                                + " where user_id = " + "'" + tbUMuserid.getText().toString() + "'"
                                + ";");

                        bsmf.MainFrame.show("Updated UserID Record");
                    }

                } else {
                    bsmf.MainFrame.show("USERID does not exists");
                }
            } // if proceed
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Edit UserMstr");
            }
            bsmf.MainFrame.con.close();
            initvars(null);
        } catch (Exception e) {
            MainFrame.bslog(e);
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
              
                   int i = st.executeUpdate("delete from user_mstr where user_id = " + "'" + tbUMuserid.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("User Deleted: " + tbUMuserid.getText());
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete User ID");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseUserActionPerformed
        bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"usermaint","user_id"});
    }//GEN-LAST:event_btbrowseUserActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
                enableAll();
                clearAll();
                btnew.setEnabled(false);
                btbrowseUser.setEnabled(false);
                btupdate.setEnabled(false);
                btdelete.setEnabled(false);
    }//GEN-LAST:event_btnewActionPerformed

    private void btbrowseLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseLastNameActionPerformed
        bsmf.MainFrame.reinitpanels("BrowseUtil", true, new String[]{"usermaint","user_lname"});
    }//GEN-LAST:event_btbrowseLastNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowseLastName;
    private javax.swing.JButton btbrowseUser;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea taUMperms1;
    private javax.swing.JTextArea tarmks;
    private javax.swing.JTextField tbUMDept;
    private javax.swing.JTextField tbUMFirstName;
    private javax.swing.JTextField tbUMLastName;
    private javax.swing.JTextField tbUMuserid;
    private javax.swing.JTextField tbcell;
    private javax.swing.JTextField tbemail;
    private javax.swing.JPasswordField tbpassword;
    private javax.swing.JTextField tbphone;
    // End of variables declaration//GEN-END:variables
}
