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
package com.blueseer.shp;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import static com.blueseer.shp.shpData.addUpdateSHCtrl;
import static com.blueseer.shp.shpData.getSHCtrl;
import com.blueseer.shp.shpData.ship_ctrl;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaData;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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


public class ShipperControl extends javax.swing.JPanel implements IBlueSeerc {

    // global variable declarations
        boolean isLoad = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        boolean canUpdate = false;
        private static ArrayList<String> accounts = new ArrayList<>();
        private static ship_ctrl x = null;
   
    public ShipperControl() {
        initComponents();
        setLanguageTags(this);
    }
               
    
    
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
   
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
        if (init) {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "accounts");
       }
       for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("accounts")) {
              accounts.add(s[1]);
            }
        }
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
            bsmf.MainFrame.show(getMessageTag(1007)); 
        } else {
            bsmf.MainFrame.show(getMessageTag(1012));  
        }
    }
    
    public boolean validateInput(dbaction x) { 
        boolean b = true;
                                
               // nothing here
               
        return b;
    }
    
    public void initvars(String[] arg) {
            setComponentDefaultValues(initDataSets == null);
            executeTask(dbaction.get, new String[]{""});
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = addUpdateSHCtrl(createRecord());
     SysMeta();
        return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getSHCtrl(key);
        return x.m();
    }
    
    public ship_ctrl createRecord() {
        ship_ctrl x = new ship_ctrl(null, 
           String.valueOf(BlueSeerUtils.boolToInt(cbconfirm.isSelected())),
           String.valueOf(BlueSeerUtils.boolToInt(cbcustitem.isSelected())));
        return x;
    }
        
    public void updateForm() {
    cbconfirm.setSelected(BlueSeerUtils.ConvertStringToBool(x.shc_confirm()));
    cbcustitem.setSelected(BlueSeerUtils.ConvertStringToBool(x.shc_custitemonly()));
    
    // get sysmeta recs
    ArrayList<String[]> obc = getSysMetaData("system", "shippercontrol");
        for (String[] s : obc) {
            if (s[0].equals("auto_generate_shipper_number")) {
                cbautogenshipper.setSelected(BlueSeerUtils.ConvertStringToBool(s[1]));
            }
            if (s[0].equals("auto_confirm_shipper_scan")) {
                cbautoconfscan.setSelected(BlueSeerUtils.ConvertStringToBool(s[1]));
            }
            if (s[0].equals("auto_confirm_shipper_build")) {
                cbautoconfbuild.setSelected(BlueSeerUtils.ConvertStringToBool(s[1]));
            }
        } 
    
    }
    
    
    public void SysMeta() {
      OVData.addUpdateSysMeta("system", "shippercontrol", "auto_generate_shipper_number", BlueSeerUtils.boolToString(cbautogenshipper.isSelected())); 
      OVData.addUpdateSysMeta("system", "shippercontrol", "auto_confirm_shipper_scan", BlueSeerUtils.boolToString(cbautoconfscan.isSelected())); 
      OVData.addUpdateSysMeta("system", "shippercontrol", "auto_confirm_shipper_build", BlueSeerUtils.boolToString(cbautoconfbuild.isSelected())); 
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
        cbconfirm = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        cbcustitem = new javax.swing.JCheckBox();
        cbautoconfscan = new javax.swing.JCheckBox();
        cbautoconfbuild = new javax.swing.JCheckBox();
        cbautogenshipper = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Control"));
        jPanel1.setName("panelmain"); // NOI18N

        cbconfirm.setText("Confirm In ShipMaint");
        cbconfirm.setName("cbconfirm"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbcustitem.setText("Customer Item Only?");
        cbcustitem.setName("cbcustitem"); // NOI18N

        cbautoconfscan.setText("Auto Confirm Shipper Scan");

        cbautoconfbuild.setText("Auto Confirm Shipper Build");

        cbautogenshipper.setText("Auto Generate Shipper Number");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbautoconfbuild)
                    .addComponent(cbcustitem)
                    .addComponent(cbautoconfscan)
                    .addComponent(cbconfirm)
                    .addComponent(btupdate)
                    .addComponent(cbautogenshipper))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbautogenshipper)
                .addGap(3, 3, 3)
                .addComponent(cbconfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbcustitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbautoconfscan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbautoconfbuild)
                .addGap(18, 24, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addContainerGap())
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
    private javax.swing.JCheckBox cbautoconfbuild;
    private javax.swing.JCheckBox cbautoconfscan;
    private javax.swing.JCheckBox cbautogenshipper;
    private javax.swing.JCheckBox cbconfirm;
    private javax.swing.JCheckBox cbcustitem;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
