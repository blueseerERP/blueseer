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
package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class LedgerBalanceExport extends javax.swing.JPanel {

    
      javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("site"),
                            getGlobalColumnTag("account"),
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("costcenter"), 
                            getGlobalColumnTag("period"),
                            getGlobalColumnTag("year"),
                            getGlobalColumnTag("endbalance")});
    
    /**
     * Creates new form LedgerBalanceExport
     */
    public LedgerBalanceExport() {
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
     
          lblendbal.setText("");
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        
        
        mymodel.setNumRows(0);
      
        tablereport.setModel(mymodel);
     
       
         ddsite.removeAllItems();
        ArrayList<String> mylist = OVData.getSiteList(bsmf.MainFrame.userid);
        for (String code : mylist) {
            ddsite.addItem(code);
        }
      
        
        ddfromper.removeAllItems();
        for (int i = 1 ; i <= 12; i++) {
            ddfromper.addItem(String.valueOf(i));
        }
        ddtoper.removeAllItems();
        for (int i = 1 ; i <= 12; i++) {
            ddtoper.addItem(String.valueOf(i));
        }
        
        
        ddfromyear.removeAllItems();
        for (int i = 1967 ; i < 2222; i++) {
            ddfromyear.addItem(String.valueOf(i));
        }
        ddfromyear.setSelectedItem(dfyear.format(now));
        
        ddtoyear.removeAllItems();
        for (int i = 1967 ; i < 2222; i++) {
            ddtoyear.addItem(String.valueOf(i));
        }
        ddtoyear.setSelectedItem(dfyear.format(now));
       
       
          
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        ddfromyear = new javax.swing.JComboBox();
        ddtoyear = new javax.swing.JComboBox();
        btview = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btexport = new javax.swing.JButton();
        tbdelimiter = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        ddfromper = new javax.swing.JComboBox();
        ddtoper = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        cbzero = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        lblendbal = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cbbs = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel2.setName("panelmain"); // NOI18N
        jPanel2.setLayout(new java.awt.BorderLayout());

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablereport);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btview.setText("View");
        btview.setName("btview"); // NOI18N
        btview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btviewActionPerformed(evt);
            }
        });

        jLabel3.setText("Site");
        jLabel3.setName("lblsite"); // NOI18N

        jLabel2.setText("To Year");
        jLabel2.setName("lbltoyear"); // NOI18N

        jLabel1.setText("From Year");
        jLabel1.setName("lblfromyear"); // NOI18N

        btexport.setText("Export");
        btexport.setName("btexport"); // NOI18N
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        jLabel4.setText("Delimiter");
        jLabel4.setName("lbldelimiter"); // NOI18N

        jLabel5.setText("From Period");
        jLabel5.setName("lblfromperiod"); // NOI18N

        jLabel6.setText("To Period");
        jLabel6.setName("lbltoperiod"); // NOI18N

        cbzero.setText("Supress Zeros");
        cbzero.setName("cbsupresszeros"); // NOI18N

        jLabel7.setForeground(new java.awt.Color(255, 24, 0));
        jLabel7.setText("You must 'vew' before 'export'");
        jLabel7.setName("lblnote"); // NOI18N

        jLabel8.setText("Total:");
        jLabel8.setName("lbltotal"); // NOI18N

        cbbs.setText("BS Activity");
        cbbs.setName("lblbsactivity"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ddtoyear, javax.swing.GroupLayout.Alignment.LEADING, 0, 94, Short.MAX_VALUE)
                    .addComponent(ddfromyear, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddtoper, 0, 69, Short.MAX_VALUE)
                    .addComponent(ddfromper, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbdelimiter)))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbzero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btview)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblendbal, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbbs)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblendbal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddfromyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(ddfromper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel3)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbzero)
                                .addComponent(btview)
                                .addComponent(btexport)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddtoyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(cbbs))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(tbdelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddtoper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btviewActionPerformed
        ArrayList<String> mylist = new ArrayList<String>();
          String[] ac = null;
        double total = 0;
        mylist = fglData.getGLBalByYearByPeriod(Integer.valueOf(ddfromyear.getSelectedItem().toString()), Integer.valueOf(ddtoyear.getSelectedItem().toString()), Integer.valueOf(ddfromper.getSelectedItem().toString()), Integer.valueOf(ddtoper.getSelectedItem().toString()), ddsite.getSelectedItem().toString(), cbzero.isSelected(), cbbs.isSelected());
        mymodel.setNumRows(0);
        for (String rec : mylist) {
        ac = rec.split(",", -1);
        total = total + bsParseDouble(ac[4]);
        String desc = fglData.getGLAcctDesc(ac[0]);
        mymodel.addRow(new Object[]{ddsite.getSelectedItem().toString(), ac[0],
                                desc,
                                ac[1],
                                ac[2],
                                ac[3],
                                ac[4]
                            });
        }
        tablereport.setModel(mymodel);
        lblendbal.setText(currformatDouble(total));
    }//GEN-LAST:event_btviewActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
          try {
              FileDialog fDialog;
              fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
              fDialog.setVisible(true);
              //fDialog.setFile("data.csv");
              String path = fDialog.getDirectory() + fDialog.getFile();
              File f = new File(path);
              BufferedWriter output;
              String detail = "";
              
              output = new BufferedWriter(new FileWriter(f));
              String myheader = getGlobalColumnTag("site") + tbdelimiter.getText() +
                      getGlobalColumnTag("account") + tbdelimiter.getText() +
                      getGlobalColumnTag("description") + tbdelimiter.getText() +
                      getGlobalColumnTag("costcenter") + tbdelimiter.getText() +
                      getGlobalColumnTag("period") + tbdelimiter.getText() +
                      getGlobalColumnTag("year") + tbdelimiter.getText() + 
                      getGlobalColumnTag("amount")
                      ;
              output.write(myheader + '\n');
              
               for (int j = 0; j < tablereport.getRowCount(); j++) {
                   detail = tablereport.getValueAt(j, 0).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 1).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 2).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 3).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 4).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 5).toString() + tbdelimiter.getText() +
                           tablereport.getValueAt(j, 6).toString() 
                           ;
                output.write(detail + '\n');
               }
              
              
              
              output.close();
          } catch (IOException ex) {
              MainFrame.bslog(ex);
          }
    }//GEN-LAST:event_btexportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btexport;
    private javax.swing.JButton btview;
    private javax.swing.JCheckBox cbbs;
    private javax.swing.JCheckBox cbzero;
    private javax.swing.JComboBox ddfromper;
    private javax.swing.JComboBox ddfromyear;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtoper;
    private javax.swing.JComboBox ddtoyear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblendbal;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbdelimiter;
    // End of variables declaration//GEN-END:variables
}
