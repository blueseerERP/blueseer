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
import com.blueseer.inv.invData;
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
public class ScrapMaint extends javax.swing.JPanel {

    javax.swing.JTable transtable = new javax.swing.JTable();
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
                getGlobalColumnTag("expiredate"),
                getGlobalColumnTag("program"),
                getGlobalColumnTag("warehouse")
            })    {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
    /**
     * Creates new form ScrapMaintPanel
     */
    public ScrapMaint() {
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
        tbuser.setText(bsmf.MainFrame.userid);
        tbuser.setEnabled(false);
        tbqty.setText("");
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dceffdate.setDate(now);
        dceffdate.setEnabled(false);
        
        ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        
       tbpart.setText("");
        ddop.removeAllItems();
         
        if (ddcode.getItemCount() == 0) {
        ArrayList codes = OVData.getCodeMstr("SCRAP");
        for (int i = 0; i < codes.size(); i++) {
            ddcode.addItem(codes.get(i));
        }
         ddcode.insertItemAt("", 0);
        ddcode.setSelectedIndex(0);
        } else {
             ddcode.setSelectedIndex(0);
        }
        
        if (ddcell.getItemCount() == 0) {
      ArrayList cells = OVData.getdeptidlist();
    //    ArrayList cells = OVData.getdeptidlist();
        for (int i = 0; i < cells.size(); i++) {
            ddcell.addItem(cells.get(i));
        }
         ddcell.insertItemAt("", 0);
        ddcell.setSelectedIndex(0);
        } else {
             ddcell.setSelectedIndex(0);
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
        dcassydate = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        btsubmit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ddop = new javax.swing.JComboBox();
        tbuser = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ddcode = new javax.swing.JComboBox();
        ddcell = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        pmcode = new javax.swing.JLabel();
        scrapcodelabel = new javax.swing.JLabel();
        tbpart = new javax.swing.JTextField();
        jobid = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        dceffdate = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Scrap Entry"));
        jPanel1.setName("panelmain"); // NOI18N

        dcassydate.setDateFormatString("yyyy-MM-dd");

        jLabel9.setText("ScrapCode");
        jLabel9.setName("lblcode"); // NOI18N

        btsubmit.setText("Commit");
        btsubmit.setName("btcommit"); // NOI18N
        btsubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsubmitActionPerformed(evt);
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

        jLabel10.setText("AssyDate");
        jLabel10.setName("lblassydate"); // NOI18N

        jLabel2.setText("Site");
        jLabel2.setName("lblsite"); // NOI18N

        ddcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddcodeItemStateChanged(evt);
            }
        });

        jLabel7.setText("WorkCell");
        jLabel7.setName("lblcell"); // NOI18N

        tbpart.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbpartFocusLost(evt);
            }
        });

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

        dceffdate.setDateFormatString("yyyy-MM-dd");

        jLabel13.setText("EffDate");
        jLabel13.setName("lbleffdate"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(64, 64, 64)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbuser, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btsubmit))
                            .addComponent(tbpart, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrapcodelabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcassydate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pmcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(ddcell, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(ddcode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dceffdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jobid))
                        .addGap(41, 41, 41))))
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
                    .addComponent(jLabel2)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dceffdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbpart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
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
                .addComponent(scrapcodelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcassydate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jobid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btsubmit)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btsubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsubmitActionPerformed
        boolean canproceed = true;
        String prodline = "";
        String status = "";
        String op = "";
        transtable.setModel(transmodel);
        String packdate = "";
        String assydate = "";
        String effdate = "";
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       
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
      
        
        if (dceffdate.getDate() != null) {
           if ( ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(dceffdate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1123));
            dceffdate.requestFocus();
            return;
           } else {
               effdate = BlueSeerUtils.mysqlDateFormat.format(dceffdate.getDate());
           }
        }
        
        if (dcassydate.getDate() != null) {
           if ( ! BlueSeerUtils.isValidDateStr(BlueSeerUtils.mysqlDateFormat.format(dcassydate.getDate())) ) {
            bsmf.MainFrame.show(getMessageTag(1123));
            dcassydate.requestFocus();
            return;
           } else {
               assydate = BlueSeerUtils.mysqlDateFormat.format(dcassydate.getDate());
           }
        }
        
       
        
        if (ddop.getSelectedItem() != null) {
            op = ddop.getSelectedItem().toString();
        } 
        String[] detail = invData.getItemDetail(tbpart.getText());
        String loc = detail[8];
        String wh = detail[9];
        String expire = detail[10];
        
        String code = "";
        if (ddcode.getItemCount() > 0)
           code = ddcode.getSelectedItem().toString();
        
        String cell = "";
        if (ddcell.getItemCount() > 0)
           cell = ddcell.getSelectedItem().toString();
        
        
       
         
         if ( OVData.isGLPeriodClosed(dfdate.format(dceffdate.getDate()))) {
                    canproceed = false;
                    bsmf.MainFrame.show(getMessageTag(1035));
                    return;
                }
         
        
        
        
        
        if (canproceed) {
            transmodel.addRow(new Object[]{tbpart.getText(),
                "ISS-SCRAP",
                op,
                tbqty.getText(),
                effdate,
                loc, // location
                jobid.getText(),  // serialno  ...using JOBID from tubtraveler
                code,  // reference -- tr_ref holds the scrap code
                ddsite.getSelectedItem().toString(),
                tbuser.getText(),
                prodline,
                cell,   //  tr_actcell
                "",   // remarks 
                "", // pack station
                packdate, // pack date
                assydate, // assembly date
                expire,
                "ScrapMaint", // program
                wh
            });
            
            
               if (! OVData.loadTranHistByTable(transtable)) {
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } else {
            bsmf.MainFrame.show(getMessageTag(1125));
        }
        
        initvars(null);
            
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
    private javax.swing.JButton btsubmit;
    private com.toedter.calendar.JDateChooser dcassydate;
    private com.toedter.calendar.JDateChooser dceffdate;
    private javax.swing.JComboBox ddcell;
    private javax.swing.JComboBox ddcode;
    private javax.swing.JComboBox ddop;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jobid;
    private javax.swing.JLabel pmcode;
    private javax.swing.JLabel scrapcodelabel;
    private javax.swing.JTextField tbpart;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbuser;
    // End of variables declaration//GEN-END:variables
}
