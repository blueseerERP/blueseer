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
package com.blueseer.inv;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */


public class InventoryCtrl extends javax.swing.JPanel implements IBlueSeerc {

    
    public InventoryCtrl() {
        initComponents();
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
           
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            try {
                
                    
                
                    int i = 0;
                    res = st.executeQuery("SELECT *  FROM  inv_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                     if (i == 0) {

                    st.executeUpdate("insert into inv_ctrl values (" + 
                            "'" + BlueSeerUtils.boolToInt(cbmultiplan.isSelected()) + "'" +  "," +
                            "'" + BlueSeerUtils.boolToInt(cbdemdtoplan.isSelected()) + "'" + "," +
                            "'" + BlueSeerUtils.boolToInt(cbprintsubticket.isSelected()) + "'" + "," +
                            "'" + BlueSeerUtils.boolToInt(cbautoitem.isSelected()) + "'" +
                            ")"  + ";");              
                          bsmf.MainFrame.show("Inserting Defaults");
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                } else {
                    st.executeUpdate("update inv_ctrl set " +
                            " planmultiscan = " + "'" + BlueSeerUtils.boolToInt(cbmultiplan.isSelected()) + "'" + "," +
                            " printsubticket = " + "'" + BlueSeerUtils.boolToInt(cbprintsubticket.isSelected()) + "'" + "," +
                            " demdtoplan = " + "'" + BlueSeerUtils.boolToInt(cbdemdtoplan.isSelected()) + "'"  + "," + 
                            " autoitem = " + "'" + BlueSeerUtils.boolToInt(cbautoitem.isSelected()) + "'"  +
                            ";");   
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                }
                    
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (bsmf.MainFrame.con != null) bsmf.MainFrame.con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
     
     return m;
     }
      
    public String[] getRecord(String[] x) {
       String[] m = new String[2];
       
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            Statement st = bsmf.MainFrame.con.createStatement();
            ResultSet res = null;
            try {
                
                int i = 0;
                
                res = st.executeQuery("SELECT * FROM  inv_ctrl;");
                    while (res.next()) {
                        i++;
                        cbmultiplan.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("planmultiscan")));
                        cbdemdtoplan.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("demdtoplan")));   
                        cbprintsubticket.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("printsubticket"))); 
                        cbautoitem.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("autoitem")));
                    }
               
                // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            } finally {
               if (res != null) res.close();
               if (st != null) st.close();
               if (bsmf.MainFrame.con != null) bsmf.MainFrame.con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordConnError};  
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
        btupdate = new javax.swing.JButton();
        cbdemdtoplan = new javax.swing.JCheckBox();
        cbmultiplan = new javax.swing.JCheckBox();
        cbprintsubticket = new javax.swing.JCheckBox();
        cbautoitem = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Control"));

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbdemdtoplan.setText("Demand To Plan");

        cbmultiplan.setText("MultiScan Plan Ticket?");

        cbprintsubticket.setText("Print Sub Ticket From Scan?");

        cbautoitem.setText("Auto Item Number Assignment?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbdemdtoplan)
                            .addComponent(cbmultiplan)
                            .addComponent(cbprintsubticket)
                            .addComponent(cbautoitem)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(btupdate)))
                .addGap(113, 113, 113))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(cbdemdtoplan)
                .addGap(18, 18, 18)
                .addComponent(cbmultiplan)
                .addGap(18, 18, 18)
                .addComponent(cbprintsubticket)
                .addGap(18, 18, 18)
                .addComponent(cbautoitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addGap(34, 34, 34))
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
    private javax.swing.JCheckBox cbautoitem;
    private javax.swing.JCheckBox cbdemdtoplan;
    private javax.swing.JCheckBox cbmultiplan;
    private javax.swing.JCheckBox cbprintsubticket;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
