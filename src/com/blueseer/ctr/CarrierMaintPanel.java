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

package com.blueseer.ctr;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import static bsmf.MainFrame.reinitpanels;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author vaughnte
 */
public class CarrierMaintPanel extends javax.swing.JPanel {

    
     DefaultListModel mymodel = new DefaultListModel() ;
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public CarrierMaintPanel() {
        initComponents();
    }

    public void getCarrierCode(String mycode) {
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                res = st.executeQuery("select * from car_mstr where car_code = " + "'" + mycode + "'" + ";");
                while (res.next()) {
                    i++;
                    tbdesc.setText(res.getString("car_desc"));
                    tbacct.setText(res.getString("car_acct"));
                    tbscaccode.setText(res.getString("car_scac"));
                    tbphone.setText(res.getString("car_phone"));
                    tbcontact.setText(res.getString("car_contact"));
                    tbcode.setText(res.getString("car_code"));
                    if (res.getString("car_type").equals("group")) {
                        rbgroup.setSelected(true);
                        rbcarrier.setSelected(false);
                    } else {
                        rbgroup.setSelected(false);
                        rbcarrier.setSelected(true);
                    }
                }
                
                if (rbgroup.isSelected()) {
                   
                 mymodel.removeAllElements();
                ArrayList mylist = OVData.getScacsOfGroup(mycode);
                for (int j = 0; j < mylist.size(); j++) {
                 mymodel.addElement(mylist.get(j));
                }
                }
                
               
                 if (i > 0) {
                    enableAll();
                    tbcode.setEnabled(false);
                    btadd.setEnabled(false);
                }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve car_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void enableAll() {
        tbcode.setEnabled(true);
        tbdesc.setEnabled(true);
        tbphone.setEnabled(true);
        tbcontact.setEnabled(true);
        tbacct.setEnabled(true); 
        tbscaccode.setEnabled(true);
        btnew.setEnabled(true);
        btadd.setEnabled(true);
        btupdate.setEnabled(true);
        btdelete.setEnabled(true);
        btbrowse.setEnabled(true);
        ddcarrier.setEnabled(true);
        btaddgroup.setEnabled(true);
        btdeletegroup.setEnabled(true);
        carrierlist.setEnabled(true);
        rbgroup.setEnabled(true);
        rbcarrier.setEnabled(true);
        
    }
    
    public void disableAll() {
        tbcode.setEnabled(false);
        tbdesc.setEnabled(false);
        tbphone.setEnabled(false);
        tbcontact.setEnabled(false);
        tbacct.setEnabled(false); 
        tbscaccode.setEnabled(false);
        btnew.setEnabled(false);
        btadd.setEnabled(false);
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btbrowse.setEnabled(false);
        ddcarrier.setEnabled(false);
        btaddgroup.setEnabled(false);
        btdeletegroup.setEnabled(false);
        carrierlist.setEnabled(false);
        rbgroup.setEnabled(false);
        rbcarrier.setEnabled(false);
    }
    
    public void clearAll() {
      tbcode.setText("");
        tbdesc.setText("");
        tbphone.setText("");
        tbcontact.setText("");
        tbacct.setText("");  
        tbscaccode.setText("");
        mymodel.removeAllElements();
        ddcarrier.removeAllItems();
        rbcarrier.setSelected(true);
        ArrayList<String> mylist = OVData.getScacCarrierOnly();
        for (int i = 0; i < mylist.size(); i++) {
            ddcarrier.addItem(mylist.get(i));
        }
       
        
        
    }
    
      public void initvars(String[] arg) {
        buttonGroup1.add(rbcarrier);
        buttonGroup1.add(rbgroup);
        carrierlist.setModel(mymodel);
        
          clearAll();
          disableAll();
          btbrowse.setEnabled(true);
          btnew.setEnabled(true);
          
        if (arg != null && arg.length > 0) {
            getCarrierCode(arg[0]);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbdesc = new javax.swing.JTextField();
        tbphone = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbacct = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbcontact = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbcode = new javax.swing.JTextField();
        btbrowse = new javax.swing.JButton();
        tbscaccode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btaddgroup = new javax.swing.JButton();
        btdeletegroup = new javax.swing.JButton();
        rbcarrier = new javax.swing.JRadioButton();
        rbgroup = new javax.swing.JRadioButton();
        ddcarrier = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        carrierlist = new javax.swing.JList<>();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Carrier Maintenance"));

        jLabel1.setText("Code:");

        jLabel2.setText("Description:");

        btdelete.setText("delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel3.setText("AcctNbr:");

        btupdate.setText("edit");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel4.setText("ContactName:");

        jLabel5.setText("Phone:");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        jLabel6.setText("scaccode:");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btaddgroup.setText("add");
        btaddgroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddgroupActionPerformed(evt);
            }
        });

        btdeletegroup.setText("delete");
        btdeletegroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletegroupActionPerformed(evt);
            }
        });

        rbcarrier.setText("Carrier");

        rbgroup.setText("Group");

        jScrollPane2.setViewportView(carrierlist);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbcode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnew)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btadd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate)
                .addGap(99, 99, 99))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbacct)
                            .addComponent(tbcontact)
                            .addComponent(tbscaccode)
                            .addComponent(tbdesc)
                            .addComponent(tbphone))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btaddgroup)
                            .addComponent(btdeletegroup)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbcarrier)
                                .addGap(18, 18, 18)
                                .addComponent(rbgroup))
                            .addComponent(ddcarrier, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 79, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbcarrier)
                    .addComponent(rbgroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscaccode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbcontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddcarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btaddgroup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeletegroup))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
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
                String cartype = "";
                
                // check the site field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a carrier / scac code");
                }
                
                if (rbgroup.isSelected()) {
                    cartype = "group";
                } else {
                    cartype = "carrier";
                }
                
                if (proceed) {

                    res = st.executeQuery("SELECT car_code FROM  car_mstr where car_code = " + "'" + tbcode.getText() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into car_mstr "
                            + "(car_code, car_desc, car_scac, car_acct, car_phone, car_type, car_contact) " 
                            + " values ( " + "'" + tbcode.getText().toString() + "'" + ","
                            + "'" + tbdesc.getText().toString() + "'" + ","
                            + "'" + tbscaccode.getText().toString() + "'" + ","
                            + "'" + tbacct.getText().toString() + "'" + ","
                            + "'" + tbphone.getText().toString() + "'" + ","
                            + "'" + cartype + "'" + ","
                            + "'" + tbcontact.getText().toString() + "'"
                            + ")"
                            + ";");
                        
                        if (rbgroup.isSelected()) {
                           for (int j = 0; j < mymodel.getSize(); j++) {
                           st.executeUpdate("insert into car_det "
                            + "(card_code, card_carrier ) "
                            + " values ( " + "'" + tbcode.getText() + "'" + ","
                            + "'" + mymodel.getElementAt(j) + "'" 
                            + ")"
                            + ";");
                           }
                        }
                        
                        
                        bsmf.MainFrame.show("Added Carrier Master");
                    } else {
                        bsmf.MainFrame.show("Carrier Record Already Exists");
                    }

                   initvars(null);
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Add to car_mstr");
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
                   
                // check the site field
                if (tbcode.getText().isEmpty()) {
                    proceed = false;
                    bsmf.MainFrame.show("Must enter a site code");
                }
                
                String cartype = "";
                 if (rbgroup.isSelected()) {
                    cartype = "group";
                } else {
                    cartype = "carrier";
                }
                
                if (proceed) {
                    st.executeUpdate("update car_mstr set car_desc = " + "'" + tbdesc.getText() + "'" + ","
                            + "car_acct = " + "'" + tbacct.getText() + "'" + ","
                            + "car_scac = " + "'" + tbscaccode.getText() + "'" + ","
                            + "car_contact = " + "'" + tbcontact.getText() + "'" + ","
                            + "car_type = " + "'" + cartype + "'" + ","
                            + "car_phone = " + "'" + tbphone.getText() + "'"
                            + " where car_code = " + "'" + tbcode.getText() + "'"                             
                            + ";");
                    
                    
                     // erase all assigned carriers to group if this is a group type
                      if (rbgroup.isSelected()) {
                      st.executeUpdate("delete from car_det where card_code = " + "'" + tbcode.getText() + "'" + ";");
                    
                       for (int j = 0; j < mymodel.getSize(); j++) {
                        st.executeUpdate("insert into car_det "
                            + "(card_code, card_carrier ) "
                            + " values ( " + "'" + tbcode.getText() + "'" + ","
                            + "'" + mymodel.getElementAt(j) + "'" 
                            + ")"
                            + ";");
                       }
                      }
                    
                    
                    
                    bsmf.MainFrame.show("Updated Carrier");
                    initvars(null);
                } 
         
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating car_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
          try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from car_mstr where car_code = " + "'" + tbcode.getText() + "'" + ";");
                   if (rbgroup.isSelected()) {
                       st.executeUpdate("delete from car_det where card_code = " + "'" + tbcode.getText() + "'" + ";");
                   }
                   
                   
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted code " + tbcode.getText());
                    initvars(null);
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to Delete Carrier Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "carriermaint,car_code");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        enableAll();
        clearAll();
        btupdate.setEnabled(false);
        btnew.setEnabled(false);
        btbrowse.setEnabled(false);
        tbcode.requestFocus();
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddgroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddgroupActionPerformed
        if (! mymodel.contains(ddcarrier.getSelectedItem().toString()))
            mymodel.addElement(ddcarrier.getSelectedItem().toString());
        else
            bsmf.MainFrame.show("Cant load twice");
    }//GEN-LAST:event_btaddgroupActionPerformed

    private void btdeletegroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletegroupActionPerformed
       mymodel.removeElement(carrierlist.getSelectedValue());  
    }//GEN-LAST:event_btdeletegroupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddgroup;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeletegroup;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JList<String> carrierlist;
    private javax.swing.JComboBox<String> ddcarrier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rbcarrier;
    private javax.swing.JRadioButton rbgroup;
    private javax.swing.JTextField tbacct;
    private javax.swing.JTextField tbcode;
    private javax.swing.JTextField tbcontact;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbscaccode;
    // End of variables declaration//GEN-END:variables
}
