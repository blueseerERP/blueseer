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
package com.blueseer.inv;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.inv.invData.addUpdateINVCtrl;
import static com.blueseer.inv.invData.getINVCtrl;
import com.blueseer.inv.invData.inv_ctrl;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaData;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */


public class InventoryCtrl extends javax.swing.JPanel implements IBlueSeerc {

    
    public InventoryCtrl() {
        initComponents();
        setLanguageTags(this);
    }

    // global variable declarations
                boolean isLoad = false;
                private static inv_ctrl x = null;
    
    // interface functions implemented
    public void executeTask(dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "update":
                    message = updateRecord(key);
                    break;
                case "get":
                    message = getRecord(key);    
                    break;    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
      public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
            if (this.type.equals("get")) {
             updateForm(); 
           } else {
             initvars(null);  
             setAction(message);
           }
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
      
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
   
    public void setComponentDefaultValues() {
       isLoad = true;
        
       isLoad = false;
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
        
    public void setAction(String[] x) {
        String[] m = new String[2];
        if (x[0].equals("0")) {
            bsmf.MainFrame.show(BlueSeerUtils.updateRecordSuccess); 
        } else {
            bsmf.MainFrame.show(BlueSeerUtils.updateRecordError);  
        }
    }
    
    public boolean validateInput(dbaction x) { 
        boolean b = true;
                                
               // nothing here
               
        return b;
    }
    
    public void initvars(String[] arg) {
            setComponentDefaultValues();
            executeTask(dbaction.get, null);
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = addUpdateINVCtrl(createRecord());
     // additional sys_meta 
     SysMeta();
     return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getINVCtrl(key);
        return x.m();
    }
    
    public inv_ctrl createRecord() {
        inv_ctrl x = new inv_ctrl(null, 
            String.valueOf(BlueSeerUtils.boolToInt(cbmultiplan.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbdemdtoplan.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbprintsubticket.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbautoitem.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbserialize.isSelected()))
        );
        return x;
    }
        
    public void updateForm() {
    cbmultiplan.setSelected(BlueSeerUtils.ConvertStringToBool(x.planmultiscan()));
    cbdemdtoplan.setSelected(BlueSeerUtils.ConvertStringToBool(x.demdtoplan()));      
    cbprintsubticket.setSelected(BlueSeerUtils.ConvertStringToBool(x.printsubticket()));    
    cbautoitem.setSelected(BlueSeerUtils.ConvertStringToBool(x.autoitem()));   
    cbserialize.setSelected(BlueSeerUtils.ConvertStringToBool(x.serialize())); 
    
    // get sysmeta recs
    ArrayList<String[]> obc = getSysMetaData("system", "inventorycontrol");
        for (String[] s : obc) {
            if (s[0].equals("jasper_job_ticket")) {
                tbjasper.setText(s[1]);
            }
            if (s[0].equals("operation_scan_required")) {
                cbopscan.setSelected(BlueSeerUtils.ConvertStringToBool(s[1]));
            }
            if (s[0].equals("operation_whloc_required")) {
                cbrequirewhloc.setSelected(BlueSeerUtils.ConvertStringToBool(s[1]));
            }
           
        } 
            
    }
    
  
     // additional methods
    public void SysMeta() {
      if (! tbjasper.getText().isBlank()) {
          OVData.addUpdateSysMeta("system", "inventorycontrol", "jasper_job_ticket", tbjasper.getText());  
      }
      OVData.addUpdateSysMeta("system", "inventorycontrol", "operation_scan_required", BlueSeerUtils.boolToString(cbopscan.isSelected())); 
      OVData.addUpdateSysMeta("system", "inventorycontrol", "operation_whloc_required", BlueSeerUtils.boolToString(cbrequirewhloc.isSelected())); 
    
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
        btupdate = new javax.swing.JButton();
        cbdemdtoplan = new javax.swing.JCheckBox();
        cbmultiplan = new javax.swing.JCheckBox();
        cbprintsubticket = new javax.swing.JCheckBox();
        cbautoitem = new javax.swing.JCheckBox();
        cbserialize = new javax.swing.JCheckBox();
        tbjasper = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cbopscan = new javax.swing.JCheckBox();
        cbrequirewhloc = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Control"));
        jPanel1.setName("panelmain"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbdemdtoplan.setText("Demand To Plan");
        cbdemdtoplan.setName("cbdemand"); // NOI18N

        cbmultiplan.setText("MultiScan Job Ticket?");
        cbmultiplan.setName("cbmultiscan"); // NOI18N

        cbprintsubticket.setText("Print Sub Ticket From Scan?");
        cbprintsubticket.setName("cbprintsubticket"); // NOI18N

        cbautoitem.setText("Auto Item Number Assignment?");
        cbautoitem.setName("cbautoitem"); // NOI18N

        cbserialize.setText("Serialize Inventory?");
        cbserialize.setName("cbserialize"); // NOI18N

        jLabel1.setText("Job Ticket Jasper");

        cbopscan.setText("Operation Scanning Required");

        cbrequirewhloc.setText("WH / Location Required");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbrequirewhloc)
                    .addComponent(cbopscan)
                    .addComponent(cbdemdtoplan)
                    .addComponent(cbmultiplan)
                    .addComponent(cbprintsubticket)
                    .addComponent(cbautoitem)
                    .addComponent(cbserialize)
                    .addComponent(btupdate)
                    .addComponent(tbjasper, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbdemdtoplan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbopscan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbmultiplan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbprintsubticket)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbautoitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbserialize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbrequirewhloc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbjasper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addGap(20, 20, 20))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
         if (! validateInput(dbaction.update)) {
           return;
       }
        executeTask(dbaction.update, null);
    }//GEN-LAST:event_btupdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbautoitem;
    private javax.swing.JCheckBox cbdemdtoplan;
    private javax.swing.JCheckBox cbmultiplan;
    private javax.swing.JCheckBox cbopscan;
    private javax.swing.JCheckBox cbprintsubticket;
    private javax.swing.JCheckBox cbrequirewhloc;
    private javax.swing.JCheckBox cbserialize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbjasper;
    // End of variables declaration//GEN-END:variables
}
