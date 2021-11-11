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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    
    // interface functions implemented
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(String type, String[] key) { 
              this.type = type;
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
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
        }
        return m;
    }
    
    public boolean validateInput(String x) { 
        boolean b = true;
                                
               // nothing here
               
        return b;
    }
    
    public void initvars(String[] arg) {
            setComponentDefaultValues();
            executeTask("get", null);
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = new String[2];
     
     try {
           
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                  String autosource = "";
                String autoinvoice = "";
                String custitemonly = "";
                String srvmtype = "";
                String srvmitemdefault = "";
                String autoallocate = "";
                String exceedQOHU = "";
                
                
                
                if ( cbexceedQOHU.isSelected() ) {
                exceedQOHU = "1";    
                } else {
                    exceedQOHU = "0";
                }
                if ( cballocate.isSelected() ) {
                autoallocate = "1";    
                } else {
                    autoallocate = "0";
                }
                
                if ( cbautosource.isSelected() ) {
                autosource = "1";    
                } else {
                    autosource = "0";
                }
                
                 if ( cbautoinvoice.isSelected() ) {
                autoinvoice = "1";    
                } else {
                    autoinvoice = "0";
                }
                
                   if ( cbcustitem.isSelected() ) {
                custitemonly = "1";    
                } else {
                    custitemonly = "0";
                }
                
                if ( cbsrvmtype.isSelected() ) {
                srvmtype = "1";    
                } else {
                    srvmtype = "0";
                }
                    
                if ( cbsrvmitemdefault.isSelected() ) {
                srvmitemdefault = "1";    
                } else {
                    srvmitemdefault = "0";
                }      
                
                    int i = 0;
                    res = st.executeQuery("SELECT *  FROM  order_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                     if (i == 0) {

                   st.executeUpdate("insert into order_ctrl (orc_autosource, orc_autoinvoice, orc_autoallocate, orc_custitem, " +
                            " orc_srvm_type, orc_srvm_item_default, orc_exceedqohu ) values (" + 
                            "'" + autosource + "'" + "," +
                            "'" + autoinvoice + "'" + "," +
                            "'" + autoallocate + "'" + "," +        
                            "'" + custitemonly + "'" + "," + 
                            "'" + srvmtype + "'" + "," +
                            "'" + srvmitemdefault + "'" +  "," +   
                            "'" + exceedQOHU + "'" +           
                                    ")" + ";");                   
                         
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                } else {
                     st.executeUpdate("update order_ctrl set " 
                            + " orc_autosource = " + "'" + autosource + "'" + "," 
                            + " orc_autoinvoice = " + "'" + autoinvoice + "'" + "," 
                            + " orc_autoallocate = " + "'" + autoallocate + "'" + ","         
                            + " orc_custitem = " + "'" + custitemonly + "'" + "," 
                            + " orc_srvm_type = " + "'" + srvmtype + "'" + ","                 
                            + " orc_srvm_item_default = " + "'" + srvmitemdefault + "'" + ","   
                            + " orc_exceedqohu = " + "'" + exceedQOHU + "'" +        
                            ";");   
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                }
                    
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};
        }
     
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                
                res = st.executeQuery("SELECT * FROM  order_ctrl ;");
                    while (res.next()) {
                        i++;
                        cbautosource.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_autosource")));
                        cbautoinvoice.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_autoinvoice")));
                        cbcustitem.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_custitem")));
                        cbsrvmtype.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_srvm_type")));
                        cbsrvmitemdefault.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_srvm_item_default")));
                        cballocate.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_autoallocate")));
                        cbexceedQOHU.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("orc_exceedqohu")));
                    }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (con != null) con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1020, Thread.currentThread().getStackTrace()[1].getMethodName())};  
        }
      return m;
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
                    .addComponent(cbcustitem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addGap(50, 50, 50))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(29, 29, 29)
                        .addComponent(btupdate)
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
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
          if (! validateInput("updateRecord")) {
           return;
       }
        executeTask("update", null);     
    }//GEN-LAST:event_btupdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cballocate;
    private javax.swing.JCheckBox cbautoinvoice;
    private javax.swing.JCheckBox cbautosource;
    private javax.swing.JCheckBox cbcustitem;
    private javax.swing.JCheckBox cbexceedQOHU;
    private javax.swing.JCheckBox cbsrvmitemdefault;
    private javax.swing.JCheckBox cbsrvmtype;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
