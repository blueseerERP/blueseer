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

package com.blueseer.prd;

import static bsmf.MainFrame.tags;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Color;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author vaughnte
 */
public class ReworkMaint extends javax.swing.JPanel {

    /**
     * Creates new form ScrapMaintPanel
     */
    public ReworkMaint() {
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
        
           tbpart.setText("");
        transmodel.setRowCount(0);
        tbuser.setText(bsmf.MainFrame.userid);
        tbuser.setEnabled(false);
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        tbdate.setDate(now);
        String site = "";
        if ( (site = OVData.getDefaultSiteForUserid("bsmf.MainFrame.userid")) != null ) {
            tbsite.setText(site);
        } else {
            tbsite.setText(OVData.getDefaultSite());
        }
        
       
       
         
        if (ddcode.getItemCount() == 0) {
        ArrayList codes = OVData.getCodeMstr("SCRAP");
        for (int i = 0; i < codes.size(); i++) {
            ddcode.addItem(codes.get(i));
        }
        } else {
             ddcode.setSelectedIndex(0);
        }
        /*
        if (ddcell.getItemCount() == 0) {
        ArrayList cells = OVData.getCodeMstr("CELLS");
        for (int i = 0; i < cells.size(); i++) {
            ddcell.addItem(cells.get(i));
        }
        } else {
             ddcell.setSelectedIndex(0);
        }
       */
        if (ddcell.getItemCount() == 0) {
             ddcell.addItem("PP5");
             ddcell.addItem("PP6");
             ddcell.addItem("PP7");
             ddcell.addItem("7822");
             ddcell.addItem("VA");
               } else {
             ddcell.setSelectedIndex(0);
        }
        
        
        
       
    }
    
          public void reinitvars() {
        
        transmodel.setRowCount(0);
        tbuser.setText(bsmf.MainFrame.userid);
        tbuser.setEnabled(false);
        tbqty.setText("");
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        tbdate.setDate(now);
        String site = "";
        if ( (site = OVData.getDefaultSiteForUserid("bsmf.MainFrame.userid")) != null ) {
            tbsite.setText(site);
        } else {
            tbsite.setText(OVData.getDefaultSite());
        }
       remarks.setText("");
       tbpart.setText("");
        ddop.removeAllItems();
         
        if (ddcode.getItemCount() == 0) {
        ArrayList codes = OVData.getCodeMstr("SCRAP");
        for (int i = 0; i < codes.size(); i++) {
            ddcode.addItem(codes.get(i));
        }
        } else {
             ddcode.setSelectedIndex(0);
        }
        
      
             ddcell.setSelectedIndex(0);
       
       
       
    }
       
       DefaultTableCellRenderer tableRender = new DefaultTableCellRenderer();
    
   
       
       
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
            })    {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
           
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        transtable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        tbdate = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        btadd = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tbsite = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ddop = new javax.swing.JComboBox();
        tbuser = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        ddcode = new javax.swing.JComboBox();
        ddcell = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        pmcode = new javax.swing.JLabel();
        scrapcodelabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        remarks = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        tbpart = new javax.swing.JTextField();
        btsubmit = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

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

        jPanel1.setName("panelmain"); // NOI18N

        tbdate.setDateFormatString("yyyy-MM-dd");

        jLabel9.setText("Code");
        jLabel9.setName("lblcode"); // NOI18N

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

        ddcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcodeItemStateChanged(evt);
            }
        });

        jLabel7.setText("Cell");
        jLabel7.setName("lblcell"); // NOI18N

        remarks.setColumns(20);
        remarks.setLineWrap(true);
        remarks.setRows(5);
        jScrollPane2.setViewportView(remarks);

        jLabel11.setText("Remarks");
        jLabel11.setName("lblremarks"); // NOI18N

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
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddop, javax.swing.GroupLayout.Alignment.LEADING, 0, 94, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pmcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddcode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrapcodelabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(40, 40, 40))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbsite, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbuser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(256, 256, 256)
                .addComponent(btadd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdelete)
                .addGap(37, 43, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tbdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddop)
                                .addComponent(jLabel5))
                            .addComponent(pmcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrapcodelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btdelete)
                    .addComponent(btadd))
                .addGap(34, 34, 34))
        );

        btsubmit.setText("Submit");
        btsubmit.setName("btsubmit"); // NOI18N
        btsubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btsubmit)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btsubmit)
                .addContainerGap(62, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        boolean canproceed = true;
        String prodline = "";
        String op = "";
        transtable.setModel(transmodel);
       
        if (! BlueSeerUtils.isParsableToDouble(tbqty.getText()) ) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            return;
        }

         if (tbpart.getText().isEmpty()) {
             bsmf.MainFrame.show(getMessageTag(1024));
            tbpart.requestFocus();
            return;
        }
        

        if (tbdate.getDate() == null || ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(tbdate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1123));
            tbdate.requestFocus();
            return;
        }
        if (ddop.getSelectedItem() != null) {
            op = ddop.getSelectedItem().toString();
        } 
        
        String loc = OVData.getLocationByPart(tbpart.getText());
        
        if (canproceed) {
            transmodel.addRow(new Object[]{tbpart.getText().toString(),
                "ISS-REWK",
                op,
                tbqty.getText(),
                BlueSeerUtils.mysqlDateFormat.format(tbdate.getDate()),
                loc, // location
                "",  // serialno
                ddcode.getSelectedItem().toString(),  // reference -- tr_ref holds the scrap code
                tbsite.getText(),
                tbuser.getText(),
                prodline,
                ddcell.getSelectedItem().toString(),   //  tr_actcell
                remarks.getText().replace("'", ""),
                "",  // pack station
                "", // pack date
                "", // assembly date
                "ReworkMaint"
            });
        }
        tbpart.requestFocus();
    }//GEN-LAST:event_btaddActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        int[] rows = transtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) transtable.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btsubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsubmitActionPerformed
        boolean didLoad = false;

        if (! OVData.loadTranHistByTable(transtable)) {
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } else {
            bsmf.MainFrame.show(getMessageTag(1065));
             reinitvars();
        }
        
       
    }//GEN-LAST:event_btsubmitActionPerformed

    private void ddcodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddcodeItemStateChanged
        scrapcodelabel.setText(OVData.getCodeDescByCode(ddcode.getSelectedItem().toString()));
    }//GEN-LAST:event_ddcodeItemStateChanged

    private void tbpartFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbpartFocusLost
        ddop.removeAllItems();
         
         if (! tbpart.getText().isEmpty()) {
         
             if (OVData.isValidItem(tbpart.getText().toString())  ) {
                  pmcode.setText(OVData.getPMCodeByPart(tbpart.getText().toString()));
                  ArrayList myops = OVData.getOperationsByPart(tbpart.getText().toString());
                  for (int i = 0; i < myops.size(); i++) {
                  ddop.addItem(myops.get(i));
                  }
             } else {
                 bsmf.MainFrame.show(getMessageTag(1021,tbpart.getText()));
                 tbpart.requestFocus();
             }
         }
    }//GEN-LAST:event_tbpartFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btsubmit;
    private javax.swing.JComboBox ddcell;
    private javax.swing.JComboBox ddcode;
    private javax.swing.JComboBox ddop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel pmcode;
    private javax.swing.JTextArea remarks;
    private javax.swing.JLabel scrapcodelabel;
    private com.toedter.calendar.JDateChooser tbdate;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbsite;
    private javax.swing.JTextField tbuser;
    private javax.swing.JTable transtable;
    // End of variables declaration//GEN-END:variables
}
