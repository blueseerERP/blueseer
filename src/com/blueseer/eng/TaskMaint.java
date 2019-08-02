/*
   Copyright 2005-2017 Terry Evans Vaughn ("VCSCode").

With regard to the Blueseer Software:

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

For all third party components incorporated into the GitLab Software, those 
components are licensed under the original license provided by the owner of the 
applicable component.

 */



package com.blueseer.eng;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author vaughnte
 */


public class TaskMaint extends javax.swing.JPanel {

    
     javax.swing.table.DefaultTableModel taskmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Owner", "Desc", "Sequence", "Enabled"
            });
    
    /**
     * Creates new form ClockControl
     */
    public TaskMaint() {
        initComponents();
    }

    
     public void getTask(String task) {
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  task_mstr where task_id = " + "'" + task + "'" + ";");
                    while (res.next()) {
                        i++;
                        taskdesc.setText(res.getString("task_desc"));
                        tasknbr.setText(res.getString("task_id"));
                    }
                    res = st.executeQuery("SELECT * FROM  task_det where " +
                            " taskd_id = " + "'" + task + "'" + " order by taskd_sequence ;");
                    while (res.next()) {
                     taskmodel.addRow(new Object[]{res.getString("taskd_owner"), res.getString("taskd_desc"), res.getString("taskd_sequence"), res.getBoolean("taskd_enabled")});   
                    }
           
                    if (i > 0) {
                        enableAll();
                        btbrowse.setEnabled(false);
                        btnew.setEnabled(false);
                        btadd.setEnabled(false);
                        tasknbr.setEnabled(false);
                    }
                    
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve task master record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     }
    
     
     public void enableAll() {
         tabletasks.setEnabled(true);
         tasknbr.setEnabled(true);
         taskdesc.setEnabled(true);
         tbsequence.setEnabled(true);
         tbaction.setEnabled(true);
         cbenabled.setEnabled(true);
         ddowner.setEnabled(true);
         btbrowse.setEnabled(true);
         btedit.setEnabled(true);
         btnew.setEnabled(true);
         btadd.setEnabled(true);
     }
    
        public void disableAll() {
         tabletasks.setEnabled(false);
         tasknbr.setEnabled(false);
         taskdesc.setEnabled(false);
         tbsequence.setEnabled(false);
         tbaction.setEnabled(false);
         cbenabled.setEnabled(false);
         ddowner.setEnabled(false);
         btbrowse.setEnabled(false);
         btedit.setEnabled(false);
         btnew.setEnabled(false);
         btadd.setEnabled(false);
     }
     
    public void initvars(String arg) {
          
           taskmodel.setRowCount(0);
           tabletasks.setModel(taskmodel);
           
           ddowner.removeAllItems();
           ArrayList<String> users = OVData.getusermstrlist();
           for (String user : users) {
               ddowner.addItem(user);
           }
           tasknbr.setText("");
           taskdesc.setText("");
           tbsequence.setText("");
           tbaction.setText("");
           cbenabled.setSelected(false);
           
          
           
           if (! arg.isEmpty()) {
           enableAll();
           getTask(arg);
           } else {
               disableAll();
               tasknbr.setEnabled(true);
               btbrowse.setEnabled(true);
               btnew.setEnabled(true);
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
        btedit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ddowner = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabletasks = new javax.swing.JTable();
        tbsequence = new javax.swing.JTextField();
        cbenabled = new javax.swing.JCheckBox();
        btdeletetask = new javax.swing.JButton();
        tbaction = new javax.swing.JTextField();
        btaddtask = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnew = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tasknbr = new javax.swing.JTextField();
        taskdesc = new javax.swing.JTextField();
        btbrowse = new javax.swing.JButton();
        btadd = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Maintenance"));

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Task Action Items"));

        jLabel2.setText("Sequence");

        jLabel3.setText("Action");

        tabletasks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabletasks);

        cbenabled.setText("Enabled?");

        btdeletetask.setText("Delete");
        btdeletetask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletetaskActionPerformed(evt);
            }
        });

        btaddtask.setText("Add");
        btaddtask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddtaskActionPerformed(evt);
            }
        });

        jLabel4.setText("Owner");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbaction)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddowner, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(cbenabled)
                        .addGap(27, 27, 27)
                        .addComponent(btaddtask)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeletetask)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddowner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbenabled)
                    .addComponent(btaddtask)
                    .addComponent(btdeletetask)
                    .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addGap(45, 45, 45)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Master Task ID"));

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("Task Nbr");

        jLabel6.setText("Desc");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tasknbr, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(taskdesc))
                .addGap(38, 38, 38))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tasknbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(taskdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btedit)
                    .addComponent(btadd))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        
        
        // lets first make sure there are no duplicate sequence numbers
        boolean proceed = true;
        ArrayList checklist = new ArrayList();
        for (int j = 0; j < tabletasks.getRowCount(); j++) {
            if (checklist.contains(tabletasks.getValueAt(j, 2).toString())) {
                proceed = false;
                break;
            } else {
                checklist.add(tabletasks.getValueAt(j, 2).toString());
            }
        }
        
        if (! proceed) {
          bsmf.MainFrame.show("Duplicate sequence numbers in tasks...please adjust");
          return;
        } // if not proceed 
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    
                       st.executeUpdate("update task_mstr set " + 
                           "task_desc = " + "'" + taskdesc.getText() + "'" + 
                           " where task_id = " + "'" + tasknbr.getText() + "'" +
                             ";");     
                
               //  now lets delete all stored actions of this master task...then add back from table
                st.executeUpdate("delete from task_det where taskd_id = " + "'" + tasknbr.getText() + "'" + ";");
                       
                 for (int j = 0; j < tabletasks.getRowCount(); j++) {
                st.executeUpdate("insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_enabled, taskd_sequence ) values ( " 
                        + "'" + tasknbr.getText() + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tabletasks.getValueAt(j, 3).toString())) + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 2).toString() + "'" 
                        + " );" );
                 }
              
                 bsmf.MainFrame.show("Updated Master Task");
                 initvars("");
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating master task");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_bteditActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      
      tasknbr.setText(String.valueOf(OVData.getNextNbr("task")));
      enableAll();
      tasknbr.setEnabled(false);
      btbrowse.setEnabled(false);
      btedit.setEnabled(false);
      btnew.setEnabled(false);
     
      
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddtaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddtaskActionPerformed
         taskmodel.addRow(new Object[]{ddowner.getSelectedItem().toString(), tbaction.getText(), tbsequence.getText(), cbenabled.isSelected()});
    }//GEN-LAST:event_btaddtaskActionPerformed

    private void btdeletetaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletetaskActionPerformed
       int[] rows = tabletasks.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) tabletasks.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeletetaskActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
        boolean proceed = true;
        ArrayList checklist = new ArrayList();
        for (int j = 0; j < tabletasks.getRowCount(); j++) {
            if (checklist.contains(tabletasks.getValueAt(j, 2).toString())) {
                proceed = false;
                break;
            } else {
                checklist.add(tabletasks.getValueAt(j, 2).toString());
            }
        }
        
        if (! proceed) {
          bsmf.MainFrame.show("Duplicate sequence numbers in tasks...please adjust");
          return;
        } // if not proceed 
        
        
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
          
                int i = 0;
                    
                    st.executeUpdate("insert into task_mstr (task_id, task_desc) values (" + 
                            "'" + tasknbr.getText() + "'" + "," +
                            "'" + taskdesc.getText() + "'"  + 
                            ")" + ";");     
                
              
                 for (int j = 0; j < tabletasks.getRowCount(); j++) {
                st.executeUpdate("insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_enabled, taskd_sequence ) values ( " 
                        + "'" + tasknbr.getText() + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tabletasks.getValueAt(j, 3).toString())) + "'" + ","
                        + "'" + tabletasks.getValueAt(j, 2).toString() + "'" 
                        + " );" );
                 }
              
                bsmf.MainFrame.show("Added Master Task Record");
                 initvars("");
                 
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem adding master task");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "taskmaint,task_id");
    }//GEN-LAST:event_btbrowseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddtask;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdeletetask;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox<String> ddowner;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabletasks;
    private javax.swing.JTextField taskdesc;
    private javax.swing.JTextField tasknbr;
    private javax.swing.JTextField tbaction;
    private javax.swing.JTextField tbsequence;
    // End of variables declaration//GEN-END:variables
}
