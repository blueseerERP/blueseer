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
package com.blueseer.prd;

import static bsmf.MainFrame.tags;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class BackFlushMaint extends javax.swing.JPanel {

    // table model must be 16 fields in length
     javax.swing.table.DefaultTableModel transmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("type"), 
                getGlobalColumnTag("operation"), 
                getGlobalColumnTag("qty"), 
                getGlobalColumnTag("date"), 
                getGlobalColumnTag("location"), 
                getGlobalColumnTag("serial"), 
                getGlobalColumnTag("reference"), 
                getGlobalColumnTag("site"), 
                getGlobalColumnTag("userid"), 
                getGlobalColumnTag("prodline"), 
                getGlobalColumnTag("cell"), 
                getGlobalColumnTag("remarks"), 
                getGlobalColumnTag("cell"), 
                getGlobalColumnTag("packdate"), 
                getGlobalColumnTag("assydate"), 
                getGlobalColumnTag("program")
            });
    
    
    
    /**
     * Creates new form BackFlushMaintPanel
     */
    public BackFlushMaint() {
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
    
    public void initvars(String[] arg) {
        
        transmodel.setRowCount(0);
        ddtype.addItem("WIP-TRAN");
        tbuser.setText(bsmf.MainFrame.userid);
        tbuser.setEnabled(false);
        tbsite.setText(OVData.getDefaultSite());
        tbsite.setEnabled(false);
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM--dd");
        tbdate.setDate(now);
        ddop.removeAllItems();
        tbpart.setText("");
        tbreference.setText("");
        tbserialno.setText("");
        tbqty.setText("");
        ddcell.removeAllItems();
       
        ArrayList cells = OVData.getCodeMstr("ASSYCELL");
    //    ArrayList cells = OVData.getdeptidlist();
        for (int i = 0; i < cells.size(); i++) {
            ddcell.addItem(cells.get(i));
        }
        if (ddcell.getItemCount() > 0)
        ddcell.setSelectedIndex(0);
       
       
       
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        btsubmit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbdate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tbsite = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        ddop = new javax.swing.JComboBox();
        tbuser = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tbserialno = new javax.swing.JTextField();
        tbreference = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        tbpart = new javax.swing.JTextField();
        ddcell = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        transtable = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Production Entry"));
        jPanel2.setName("panelmain"); // NOI18N

        btsubmit.setText("Submit");
        btsubmit.setName("btcommit"); // NOI18N
        btsubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsubmitActionPerformed(evt);
            }
        });

        jLabel8.setText("SerialNo");
        jLabel8.setName("lblserial"); // NOI18N

        tbdate.setDateFormatString("yyyy-MM-dd");

        jLabel7.setText("Cell");
        jLabel7.setName("lblcell"); // NOI18N

        jLabel9.setText("reference");
        jLabel9.setName("lblref"); // NOI18N

        btadd.setText("Add");
        btadd.setName("btadd"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        jLabel1.setText("User");
        jLabel1.setName("lbluser"); // NOI18N

        jLabel6.setText("Quantity");
        jLabel6.setName("lblqty"); // NOI18N

        jLabel5.setText("Operation");
        jLabel5.setName("lblop"); // NOI18N

        jLabel3.setText("Type");
        jLabel3.setName("lbltype"); // NOI18N

        jLabel4.setText("Part");
        jLabel4.setName("lblitem"); // NOI18N

        jLabel10.setText("EffDate");
        jLabel10.setName("lbleffdate"); // NOI18N

        jLabel2.setText("Site");
        jLabel2.setName("lblsite"); // NOI18N

        btdelete.setText("Delete");
        btdelete.setName("btdelete"); // NOI18N
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbsite)
                                .addGap(48, 48, 48))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(48, 48, 48))
                            .addComponent(tbreference)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ddtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(48, 48, 48))
                            .addComponent(tbserialno)
                            .addComponent(tbpart)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ddcell, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tbqty))
                                .addGap(69, 69, 69))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(btadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btdelete)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tbdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserialno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbreference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        transtable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(transtable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 732, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btsubmit)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btsubmit)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(369, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        boolean canproceed = true;
        String prodline = "";
        String loc = OVData.getLocationByPart(tbpart.getText());
       
        transtable.setModel(transmodel);
        
        if (! BlueSeerUtils.isParsableToDouble(tbqty.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            return;
        }
        
        
         if (tbdate.getDate() == null || ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(tbdate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1123));
            tbdate.requestFocus();
            return;            
        }
        
       
        if (canproceed) {
            transmodel.addRow(new Object[]{tbpart.getText(), 
                ddtype.getSelectedItem(), 
                ddop.getSelectedItem(), 
                tbqty.getText(), 
                BlueSeerUtils.mysqlDateFormat.format(tbdate.getDate()), 
                loc, 
                tbserialno.getText(), 
                tbreference.getText(),
                tbsite.getText(),
                tbuser.getText(),
                prodline,
                ddcell.getSelectedItem().toString(), "", "", "", "", "BackFlushMaint" });
        }
        ddop.removeAllItems();
        tbpart.setText("");
        tbreference.setText("");
        tbserialno.setText("");
        tbqty.setText("");
        tbpart.requestFocus();
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
         int[] rows = transtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) transtable.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
      if (! tbpart.getText().isEmpty()) {
        ddop.removeAllItems();
        boolean goodpart = false;
       goodpart = OVData.isValidItem(tbpart.getText());
       if (! goodpart) {
           bsmf.MainFrame.show(getMessageTag(1021,tbpart.getText()));
           tbpart.setForeground(Color.red);
       } else {
           tbpart.setForeground(Color.black);
        ArrayList myops = OVData.getOperationsByPart(tbpart.getText());
        for (int i = 0; i < myops.size(); i++) {
            ddop.addItem(myops.get(i));
        }
       }
      }
    }//GEN-LAST:event_tbpartFocusLost

    private void btsubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsubmitActionPerformed
        boolean didLoad = false;
        
        if (! OVData.loadTranHistByTable(transtable)) {
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } else {
            initvars(null);
            bsmf.MainFrame.show(getMessageTag(1125));
        }
    }//GEN-LAST:event_btsubmitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btsubmit;
    private javax.swing.JComboBox ddcell;
    private javax.swing.JComboBox ddop;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser tbdate;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbreference;
    private javax.swing.JTextField tbserialno;
    private javax.swing.JTextField tbsite;
    private javax.swing.JTextField tbuser;
    private javax.swing.JTable transtable;
    // End of variables declaration//GEN-END:variables
}
