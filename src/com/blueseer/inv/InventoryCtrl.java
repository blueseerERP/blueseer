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

        cbmultiplan.setText("MultiScan Plan Ticket?");
        cbmultiplan.setName("cbmultiscan"); // NOI18N

        cbprintsubticket.setText("Print Sub Ticket From Scan?");
        cbprintsubticket.setName("cbprintsubticket"); // NOI18N

        cbautoitem.setText("Auto Item Number Assignment?");
        cbautoitem.setName("cbautoitem"); // NOI18N

        cbserialize.setText("Serialize Inventory?");
        cbserialize.setName("cbserialize"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbdemdtoplan)
                    .addComponent(cbmultiplan)
                    .addComponent(cbprintsubticket)
                    .addComponent(cbautoitem)
                    .addComponent(cbserialize)
                    .addComponent(btupdate))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbdemdtoplan)
                .addGap(18, 18, 18)
                .addComponent(cbmultiplan)
                .addGap(18, 18, 18)
                .addComponent(cbprintsubticket)
                .addGap(18, 18, 18)
                .addComponent(cbautoitem)
                .addGap(18, 18, 18)
                .addComponent(cbserialize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
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
    private javax.swing.JCheckBox cbprintsubticket;
    private javax.swing.JCheckBox cbserialize;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
