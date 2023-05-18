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
package com.blueseer.ord;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.ord.ordData.addUpdateORCtrl;
import static com.blueseer.ord.ordData.getORCtrl;
import com.blueseer.ord.ordData.order_ctrl;
import static com.blueseer.srv.SalesOrdServ.getSalesOrderJSON;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */


public class OrderControl extends javax.swing.JPanel implements IBlueSeerc {

   
    public OrderControl() {
        initComponents();
        setLanguageTags(this);
    }

    // global variable declarations
                boolean isLoad = false;
                private static order_ctrl x = null;
    
    
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
     String[] m = addUpdateORCtrl(createRecord());
        return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getORCtrl(key);
        return x.m();
    }
    
    public order_ctrl createRecord() {
        order_ctrl x = new order_ctrl(null, 
           String.valueOf(BlueSeerUtils.boolToInt(cbautosource.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbautoinvoice.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cballocate.isSelected())),    
            String.valueOf(BlueSeerUtils.boolToInt(cbcustitem.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbsrvmtype.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbsrvmitemdefault.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbexceedQOHU.isSelected())),
            String.valueOf(BlueSeerUtils.boolToInt(cbvouchershipping.isSelected()))
        );
        return x;
    }
        
    public void updateForm() {
    cbautosource.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_autosource()));
    cbautoinvoice.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_autoinvoice()));      
    cbcustitem.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_custitem()));    
    cbsrvmtype.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_srvm_type()));   
    cbsrvmitemdefault.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_srvm_item_default()));
    cballocate.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_autoallocate()));  
    cbexceedQOHU.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_exceedqohu()));  
    cbvouchershipping.setSelected(BlueSeerUtils.ConvertStringToBool(x.orc_varchar())); 
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
        cbautosource = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();
        cbautoinvoice = new javax.swing.JCheckBox();
        cbcustitem = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        cbsrvmitemdefault = new javax.swing.JCheckBox();
        cbsrvmtype = new javax.swing.JCheckBox();
        cballocate = new javax.swing.JCheckBox();
        cbexceedQOHU = new javax.swing.JCheckBox();
        btjson = new javax.swing.JButton();
        cbvouchershipping = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Order Control"));
        jPanel1.setName("panelmain"); // NOI18N

        cbautosource.setText("Auto Source Order?");
        cbautosource.setName("cbautosource"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbautoinvoice.setText("Auto Invoice Order?");
        cbautoinvoice.setName("cbautoinvoice"); // NOI18N

        cbcustitem.setText("Cust Item Xref Only?");
        cbcustitem.setName("cbxrefonly"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Service Order Defaults"));
        jPanel2.setName("panelservice"); // NOI18N

        cbsrvmitemdefault.setText("Service Default Item?");
        cbsrvmitemdefault.setName("cbservice"); // NOI18N

        cbsrvmtype.setText("Quote Type?");
        cbsrvmtype.setName("cbquotetype"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbsrvmtype)
                    .addComponent(cbsrvmitemdefault))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbsrvmtype)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbsrvmitemdefault)
                .addContainerGap())
        );

        cballocate.setText("Auto Allocate?");
        cballocate.setName("cbautoallocate"); // NOI18N

        cbexceedQOHU.setText("Exceed QOHU?");
        cbexceedQOHU.setName("cbexceed"); // NOI18N

        btjson.setText("Export (JSON)");
        btjson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btjsonActionPerformed(evt);
            }
        });

        cbvouchershipping.setText("Voucher Shipping?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cballocate)
                    .addComponent(cbexceedQOHU)
                    .addComponent(cbautosource)
                    .addComponent(cbautoinvoice)
                    .addComponent(cbcustitem)
                    .addComponent(cbvouchershipping))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btupdate)
                    .addComponent(btjson))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btupdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(btjson)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(cbautosource, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbautoinvoice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbcustitem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cballocate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbexceedQOHU)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbvouchershipping)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput(dbaction.update)) {
           return;
       }
        executeTask(dbaction.update, null);     
    }//GEN-LAST:event_btupdateActionPerformed

    private void btjsonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btjsonActionPerformed
        String ordnbr = bsmf.MainFrame.input("order number: ");
        Path path = FileSystems.getDefault().getPath("temp" + "/" + "order.json");
        BufferedWriter output;
        if (ordnbr == null || ordnbr.isBlank()) {
            return;
        }
        String filecontent = getSalesOrderJSON(ordnbr);
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile(),false)));
            output.write(filecontent);
            output.close();  
        } catch (FileNotFoundException ex) {
            bslog(ex);
        } catch (IOException ex) {
            bslog(ex);
        }
        
        
    }//GEN-LAST:event_btjsonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btjson;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cballocate;
    private javax.swing.JCheckBox cbautoinvoice;
    private javax.swing.JCheckBox cbautosource;
    private javax.swing.JCheckBox cbcustitem;
    private javax.swing.JCheckBox cbexceedQOHU;
    private javax.swing.JCheckBox cbsrvmitemdefault;
    private javax.swing.JCheckBox cbsrvmtype;
    private javax.swing.JCheckBox cbvouchershipping;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
