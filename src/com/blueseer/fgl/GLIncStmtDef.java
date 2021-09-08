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
package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

/**
 *
 * @author vaughnte
 */
public class GLIncStmtDef extends javax.swing.JPanel {

    DefaultListModel mymodel = new DefaultListModel() ;
    DefaultListModel mymodelex = new DefaultListModel() ;
    
    /**
     * Creates new form GLIncStmtDef
     */
    public GLIncStmtDef() {
        initComponents();
        setLanguageTags(this);
    }

    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
    
    
    public void initvars(String[] vars) {
        
        ddcategory.removeAllItems();
        ArrayList mylist = OVData.getGLICDefsList();
        for (int i = 0; i < mylist.size(); i++) {
            ddcategory.addItem(mylist.get(i));
        }
    
        ddacct.removeAllItems();
        ArrayList accts = OVData.getGLAcctList();
        for (int i = 0; i < accts.size(); i++) {
            ddacct.addItem(accts.get(i));
        }
    // assignedlist.setModel(mymodel);
    assignlist.setModel(mymodel);
    excludelist.setModel(mymodelex);
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
        ddcategory = new javax.swing.JComboBox();
        btupdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox();
        acctname = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        assignlist = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        btaddassign = new javax.swing.JButton();
        btdeleteassigned = new javax.swing.JButton();
        tbfrom = new javax.swing.JTextField();
        tbto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        excludelist = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        btaddexclude = new javax.swing.JButton();
        btdeleteexclude = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        ddcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcategoryActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Category");
        jLabel1.setName("lblcategory"); // NOI18N

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        jLabel3.setText("Accounts");
        jLabel3.setName("lblaccounts"); // NOI18N

        jScrollPane2.setViewportView(assignlist);

        jLabel4.setText("Excluded:");
        jLabel4.setName("lblexcluded"); // NOI18N

        btaddassign.setText("Add");
        btaddassign.setName("btadd"); // NOI18N
        btaddassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddassignActionPerformed(evt);
            }
        });

        btdeleteassigned.setText("Delete");
        btdeleteassigned.setName("btdelete"); // NOI18N
        btdeleteassigned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteassignedActionPerformed(evt);
            }
        });

        jLabel5.setText("From");
        jLabel5.setName("lblfrom"); // NOI18N

        jLabel6.setText("To");
        jLabel6.setName("lblto"); // NOI18N

        jScrollPane3.setViewportView(excludelist);

        jLabel7.setText("Assigned");
        jLabel7.setName("lblassigned"); // NOI18N

        btaddexclude.setText("Add");
        btaddexclude.setName("btadd"); // NOI18N
        btaddexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddexcludeActionPerformed(evt);
            }
        });

        btdeleteexclude.setText("Delete");
        btdeleteexclude.setName("btdelete"); // NOI18N
        btdeleteexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteexcludeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(btdeleteassigned)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btaddassign))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(acctname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btupdate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(btdeleteexclude)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddexclude)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(acctname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddassign)
                    .addComponent(btdeleteassigned))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddexclude)
                    .addComponent(btdeleteexclude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btupdate))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void ddcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcategoryActionPerformed
       if (ddcategory.getItemCount() > 0) {
        mymodelex.removeAllElements();
        mymodel.removeAllElements();
        tbfrom.setText(OVData.getGLICDefsStart(ddcategory.getSelectedItem().toString()));
        tbto.setText(OVData.getGLICDefsEnd(ddcategory.getSelectedItem().toString()));
        
        ArrayList mylistin = OVData.getGLICAccts(ddcategory.getSelectedItem().toString(),"in");
        for (int i = 0; i < mylistin.size(); i++) {
            mymodel.addElement(mylistin.get(i));
        }
        
        ArrayList mylist = OVData.getGLICAccts(ddcategory.getSelectedItem().toString(),"out");
        for (int i = 0; i < mylist.size(); i++) {
            mymodelex.addElement(mylist.get(i));
        }
       }
    }//GEN-LAST:event_ddcategoryActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
       acctname.setText(OVData.getGLAcctDesc(ddacct.getSelectedItem().toString()));
    }//GEN-LAST:event_ddacctActionPerformed

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

                    
                    st.executeUpdate("update glic_def set glic_start = " + "'" + tbfrom.getText() + "'" + "," +
                            " glic_end = " + "'" + tbto.getText() + "'" + 
                            " where glic_name = " + "'" + ddcategory.getSelectedItem().toString() + "'" + ";");
                    
                    
                    // erase all assigned accounts and refill with current assign and exclude list
                    st.executeUpdate("delete from glic_accts where glicd_name = " + "'" + ddcategory.getSelectedItem().toString() + "'" + ";");
                    
                       for (int j = 0; j < mymodel.getSize(); j++) {
                        st.executeUpdate("insert into glic_accts "
                            + "(glicd_name, glicd_acct, glicd_type ) "
                            + " values ( " + "'" + ddcategory.getSelectedItem().toString() + "'" + ","
                            + "'" + mymodel.getElementAt(j) + "'" + ","
                            + "'in'"
                            + ")"
                            + ";");
                       }
                       
                       for (int j = 0; j < mymodelex.getSize(); j++) {
                        st.executeUpdate("insert into glic_accts "
                            + "(glicd_name, glicd_acct, glicd_type ) "
                            + " values ( " + "'" + ddcategory.getSelectedItem().toString() + "'" + ","
                            + "'" + mymodelex.getElementAt(j) + "'" + ","
                            + "'out'"
                            + ")"
                            + ";");
                       }
                       
                       
                        bsmf.MainFrame.show(getMessageTag(1008));
                   
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, this.getClass().getEnclosingMethod().getName()));
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btaddassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddassignActionPerformed
        mymodel.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddassignActionPerformed

    private void btdeleteassignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteassignedActionPerformed
         mymodel.removeElement(assignlist.getSelectedValue());
    }//GEN-LAST:event_btdeleteassignedActionPerformed

    private void btaddexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddexcludeActionPerformed
       mymodelex.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddexcludeActionPerformed

    private void btdeleteexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteexcludeActionPerformed
        mymodelex.removeElement(excludelist.getSelectedValue());
    }//GEN-LAST:event_btdeleteexcludeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acctname;
    private javax.swing.JList assignlist;
    private javax.swing.JButton btaddassign;
    private javax.swing.JButton btaddexclude;
    private javax.swing.JButton btdeleteassigned;
    private javax.swing.JButton btdeleteexclude;
    private javax.swing.JButton btupdate;
    private javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcategory;
    private javax.swing.JList excludelist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField tbfrom;
    private javax.swing.JTextField tbto;
    // End of variables declaration//GEN-END:variables
}
