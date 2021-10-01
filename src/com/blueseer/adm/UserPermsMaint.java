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

package com.blueseer.adm;

import static bsmf.MainFrame.tags;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.util.ArrayList;
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
public class UserPermsMaint extends javax.swing.JPanel {

    /**
     * Creates new form MenuCopyPerms
     */
    public UserPermsMaint() {
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
    
    public void initvars(String[] arg)
    {
        tausers.setText("");
        tamenus.setText("");
         fromuser.removeAllItems();
        touser.removeAllItems();
        ArrayList users = OVData.getusermstrlist();
        for (int i = 0 ; i < users.size(); i++) {
            
            fromuser.addItem(users.get(i));
            if (users.get(i).toString().compareTo("admin") != 0) {
            touser.addItem(users.get(i));
            }
        }
        
        dduserapplied.removeAllItems();
        users = OVData.getusermstrlist();
        for (int i = 0 ; i < users.size(); i++) {
            dduserapplied.addItem(users.get(i));
        }
        dduserapplied.insertItemAt("ALL", 0);
        
        ddzuser.removeAllItems();
        users = OVData.getusermstrlist();
        for (int i = 0 ; i < users.size(); i++) {
            ddzuser.addItem(users.get(i));
        }
       
       
        
        ddmenucheck.removeAllItems();
        ArrayList menus = OVData.getmenulist();
        for (int i = 0 ; i < menus.size(); i++) {
            ddmenucheck.addItem(menus.get(i));
        }
        
        ddmenuuser.removeAllItems();
        menus = OVData.getmenulist();
        for (int i = 0 ; i < menus.size(); i++) {
            ddmenuuser.addItem(menus.get(i));
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

        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        ddmenucheck = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tausers = new javax.swing.JTextArea();
        btgetusers = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btusermenuassign = new javax.swing.JButton();
        ddmenuuser = new javax.swing.JComboBox();
        dduserapplied = new javax.swing.JComboBox();
        btmenuuserunassign = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        fromuser = new javax.swing.JComboBox();
        touser = new javax.swing.JComboBox();
        btCopy = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        ddzuser = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        tamenus = new javax.swing.JTextArea();
        btgetmenus = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel5.setName("panelmain"); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Users Assigned to this Menu"));
        jPanel3.setName("paneltomenu"); // NOI18N

        tausers.setColumns(20);
        tausers.setRows(5);
        jScrollPane1.setViewportView(tausers);

        btgetusers.setText("Get");
        btgetusers.setName("btview"); // NOI18N
        btgetusers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgetusersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddmenucheck, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btgetusers, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ddmenucheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btgetusers)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Menu To User Assign/Unassign"));
        jPanel4.setName("panelmenu"); // NOI18N

        btusermenuassign.setText("Assign");
        btusermenuassign.setName("btassign"); // NOI18N
        btusermenuassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btusermenuassignActionPerformed(evt);
            }
        });

        btmenuuserunassign.setText("UnAssign");
        btmenuuserunassign.setName("btunassign"); // NOI18N
        btmenuuserunassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btmenuuserunassignActionPerformed(evt);
            }
        });

        jLabel3.setText("Menu");
        jLabel3.setName("lblmenu"); // NOI18N

        jLabel4.setText("User");
        jLabel4.setName("lbluser"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btmenuuserunassign)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btusermenuassign))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dduserapplied, javax.swing.GroupLayout.Alignment.TRAILING, 0, 201, Short.MAX_VALUE)
                            .addComponent(ddmenuuser, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddmenuuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dduserapplied, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btusermenuassign)
                    .addComponent(btmenuuserunassign))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Copy All User Permissions"));
        jPanel1.setName("panelcopy"); // NOI18N

        btCopy.setText("Copy");
        btCopy.setName("btcopy"); // NOI18N
        btCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCopyActionPerformed(evt);
            }
        });

        jLabel1.setText("User From");
        jLabel1.setName("lbluserfrom"); // NOI18N

        jLabel2.setText("User To");
        jLabel2.setName("lbluserto"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btCopy, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fromuser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(touser, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fromuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(touser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btCopy)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Menus Assigned to this User"));
        jPanel6.setName("paneltouser"); // NOI18N

        tamenus.setColumns(20);
        tamenus.setRows(5);
        jScrollPane2.setViewportView(tamenus);

        btgetmenus.setText("Get");
        btgetmenus.setName("btview"); // NOI18N
        btgetmenus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgetmenusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddzuser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btgetmenus, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(ddzuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btgetmenus)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        add(jPanel5);
    }// </editor-fold>//GEN-END:initComponents

    private void btCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCopyActionPerformed
         OVData.copyUserPerms(fromuser.getSelectedItem().toString(), touser.getSelectedItem().toString());
         initvars(null);
    }//GEN-LAST:event_btCopyActionPerformed

    private void btgetusersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgetusersActionPerformed
        tausers.setText("");
        ArrayList<String> myusers = OVData.getUsersOfMenusList(ddmenucheck.getSelectedItem().toString());
        for (String user : myusers) {
            tausers.append(user);
            tausers.append("\n");
        }
         
    }//GEN-LAST:event_btgetusersActionPerformed

    private void btusermenuassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btusermenuassignActionPerformed
        if (dduserapplied.getSelectedItem().toString().equals("ALL")) {
        OVData.addMenuToAllUsers(ddmenuuser.getSelectedItem().toString());
        } else {
        String myreturn = OVData.addMenuToUser(ddmenuuser.getSelectedItem().toString(), dduserapplied.getSelectedItem().toString());
        if (myreturn.equals("0")) {
            bsmf.MainFrame.show(getMessageTag(1065));
        }
        if (myreturn.equals("1")) {
            bsmf.MainFrame.show(getMessageTag(1014));
        }
        if (myreturn.equals("2")) {
            bsmf.MainFrame.show(getMessageTag(1012));
        }
        }
    }//GEN-LAST:event_btusermenuassignActionPerformed

    private void btmenuuserunassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btmenuuserunassignActionPerformed
        if (dduserapplied.getSelectedItem().toString().equals("ALL")) {
        OVData.deleteMenuToAllUsers(ddmenuuser.getSelectedItem().toString());
        } else {
        String myreturn = OVData.deleteMenuToUser(ddmenuuser.getSelectedItem().toString(), dduserapplied.getSelectedItem().toString());
         if (myreturn.equals("0")) {
            bsmf.MainFrame.show(getMessageTag(1065));
        }
        if (myreturn.equals("1")) {
            bsmf.MainFrame.show(getMessageTag(1014));
        }
        if (myreturn.equals("2")) {
            bsmf.MainFrame.show(getMessageTag(1012));
        }
        }
    }//GEN-LAST:event_btmenuuserunassignActionPerformed

    private void btgetmenusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgetmenusActionPerformed
        tamenus.setText("");
        ArrayList<String> mymenus = OVData.getMenusOfUsersList(ddzuser.getSelectedItem().toString());
        for (String menu : mymenus) {
            tamenus.append(menu);
            tamenus.append("\n");
        }
    }//GEN-LAST:event_btgetmenusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCopy;
    private javax.swing.JButton btgetmenus;
    private javax.swing.JButton btgetusers;
    private javax.swing.JButton btmenuuserunassign;
    private javax.swing.JButton btusermenuassign;
    private javax.swing.JComboBox ddmenucheck;
    private javax.swing.JComboBox ddmenuuser;
    private javax.swing.JComboBox dduserapplied;
    private javax.swing.JComboBox ddzuser;
    private javax.swing.JComboBox fromuser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea tamenus;
    private javax.swing.JTextArea tausers;
    private javax.swing.JComboBox touser;
    // End of variables declaration//GEN-END:variables
}
