/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.adm;

import bsmf.MainFrame;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author vaughnte
 */
public class GenCodeMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form GenericCodeMstr
     */
    public GenCodeMaintPanel() {
        initComponents();
    }

    
    public void initvars(String arg) {
        tbcode.setText("");
        tbkey.setText("");
        tbvalue.setText("");
        if (! arg.isEmpty()) {
            String[] args = arg.split(",",-1);
            getCode(args[0], args[1]);
        }
    }
    
      public void getCode(String mycode, String mykey) {
        initvars("");
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from code_mstr where code_code = " + "'" + mycode + "'" 
                        + " AND code_key = " + "'" + mykey + "'" + ";");
                while (res.next()) {
                    i++;
                    tbcode.setText(mycode);
                    tbkey.setText(mykey);
                    tbvalue.setText(res.getString("code_value"));
                    
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Code Mstr Record found for " + mycode + " / " + mykey);

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve code_mstr");
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
        tbcode = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbkey = new javax.swing.JTextField();
        tbvalue = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btget = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Generic Code Maintenance"));

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Code");

        jLabel2.setText("Key");

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

        jLabel3.setText("Value / Desc");

        btget.setText("Get");
        btget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btget))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btadd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btdelete)
                                    .addComponent(btupdate)))
                            .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btget))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btadd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addContainerGap(14, Short.MAX_VALUE))
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
                
                // check the code field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a code");
                }
                
                 // check the key field
                if (tbkey.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a key");
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT code_value FROM  code_mstr where code_code = " + "'" + tbcode.getText() + "'" + 
                            " AND code_key = " + "'" + tbkey.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into code_mstr "
                            + "(code_code, code_key, code_value ) "
                            + " values ( " + "'" + tbcode.getText().toString() + "'" + ","
                            + "'" + tbkey.getText().toString() + "'" + ","
                            + "'" + tbvalue.getText().toString() + "'"
                            + ")"
                            + ";");
                        bsmf.MainFrame.show("Added Code / Key Record");
                    } else {
                        bsmf.MainFrame.show("Code / Key Pair Already Exists");
                    }

                   initvars("");
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to code_mstr");
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
                ResultSet res = null;
                int i = 0;   
                // check the code field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a code");
                }
                
                // check the key field
                if (tbkey.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a key");
                }
                
                res = st.executeQuery("SELECT code_code FROM  code_mstr where code_code = " + "'" + tbcode.getText() + "'" + 
                            " AND code_key = " + "'" + tbkey.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show("Code / Key pair does not exist to update...consider adding");
                }
                
                if (proceed) {
                    st.executeUpdate("update code_mstr set code_value = " + "'" + tbvalue.getText() + "'"
                            + " where code_key = " + "'" + tbkey.getText() + "'"
                            + " AND code_code = " + "'" + tbcode.getText() + "'"                             
                            + ";");
                    bsmf.MainFrame.show("Updated Code Value");
                    initvars("");
                } 
         
            } catch (SQLException s) {
                bsmf.MainFrame.show("Problem updating code_mstr");
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
              
                   int i = st.executeUpdate("delete from code_mstr where code_code = " + "'" + tbcode.getText() + "'" +
                           " AND code_key = " + "'" + tbkey.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code / key pair " + tbcode.getText() + " / " + tbkey.getText());
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete Code Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btgetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgetActionPerformed
       getCode(tbcode.getText(), tbkey.getText());
    }//GEN-LAST:event_btgetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btget;
    private javax.swing.JButton btupdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbvalue;
    // End of variables declaration//GEN-END:variables
}
