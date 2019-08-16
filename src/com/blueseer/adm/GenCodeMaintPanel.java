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
package com.blueseer.adm;

import bsmf.MainFrame;
import static bsmf.MainFrame.reinitpanels;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.IBlueSeer;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class GenCodeMaintPanel extends javax.swing.JPanel    {

     // global variable declarations
                boolean isLoad = false;
    
   // global datatablemodel declarations    
        
     
   // NOTE:  This is a two key data table
    public GenCodeMaintPanel() {
        initComponents();
    }

    
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
                case "add":
                    message = addRecord(key);
                    break;
                case "update":
                    message = updateRecord(key);
                    break;
                case "delete":
                    message = deleteRecord(key);    
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
           if (this.type.equals("delete")) {
             initvars(null);  
           } else if (this.type.equals("get") && message[0].equals("1")) {
             tbkey.requestFocus();
           } else if (this.type.equals("get") && message[0].equals("0")) {
             tbkey.requestFocus();
           } else {
             initvars(null);  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
       
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setComponentDefaultValues() {
        isLoad = true;
           tbkey.setText("");
        tbkey2.setText("");
        tbvalue.setText("");
      
       isLoad = false;
    }
    
    public void newAction(String x) {
       setPanelComponentState(this, true);
        setComponentDefaultValues();
        btupdate.setEnabled(false);
        btdelete.setEnabled(false);
        btnew.setEnabled(false);
        tbkey.setForeground(Color.blue);
        if (! x.isEmpty()) {
          tbkey.setText(String.valueOf(OVData.getNextNbr(x)));  
          tbkey.setEditable(false);
        } 
        tbkey.requestFocus();
    }
    
    public String[] setAction(int i) {
        String[] m = new String[2];
        if (i > 0) {
            m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};  
                   setPanelComponentState(this, true);
                   btnew.setEnabled(false);
                   btadd.setEnabled(false);
                   tbkey.setEditable(false);
                   tbkey.setForeground(Color.blue);
        } else {
           m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordError};  
                   tbkey.setForeground(Color.red); 
        }
        return m;
    }
    
    public boolean validateInput(String x) {
        boolean b = true;
               
                
                if (tbkey.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a key1");
                    tbkey.requestFocus();
                    return b;
                }
                
                if (tbkey2.getText().isEmpty()) {
                    b = false;
                    bsmf.MainFrame.show("must enter a key2");
                    tbkey2.requestFocus();
                    return b;
                }
               
        return b;
    }
    
    public void initvars(String[] arg) {
       setPanelComponentState(this, false); 
       setComponentDefaultValues();
        btnew.setEnabled(true);
        btbrowse.setEnabled(true);
        
        // this is a two key data record
        if (arg != null && arg.length > 1) {
            executeTask("get", arg);
        } else {
            tbkey.setEnabled(true);
            tbkey.setEditable(true);
            tbkey2.setEnabled(true);
            tbkey2.setEditable(true);
        }
    }
     
    public String[] addRecord(String[] x) {
         String[] m = new String[2];
          try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
               
                int i = 0;
                
               boolean proceed = validateInput("addRecord");
                
                if (proceed) {

                    res = st.executeQuery("SELECT code_value FROM  code_mstr where code_code = " + "'" + x[0] + "'" + 
                            " AND code_key = " + "'" + x[1] + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i == 0) {
                        st.executeUpdate("insert into code_mstr "
                            + "(code_code, code_key, code_value ) "
                            + " values ( " + "'" + x[0] + "'" + ","
                            + "'" + x[1] + "'" + ","
                            + "'" + tbvalue.getText().toString() + "'"
                            + ")"
                            + ";");
                         m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                    } else {
                       m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists}; 
                    }

                   initvars(null);
                   
                } // if proceed
          } catch (SQLException s) {
                MainFrame.bslog(s);
                 m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
             m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordConnError};
        }
    return m;
    }
    
    public String[] updateRecord(String[] x) {
        String[] m = new String[2];
         try {
           
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;   
                boolean proceed = validateInput("updateRecord");
                
                res = st.executeQuery("SELECT code_code FROM  code_mstr where code_code = " + "'" + x[0] + "'" + 
                            " AND code_key = " + "'" + x[1] + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                
                if (i == 0) {
                    proceed = false;
                    bsmf.MainFrame.show("Code / Key pair does not exist to update...consider adding");
                }
                
                if (proceed) {
                    st.executeUpdate("update code_mstr set code_value = " + "'" + tbvalue.getText() + "'"
                            + " where code_key = " + "'" + x[1] + "'"
                            + " AND code_code = " + "'" + x[0] + "'"                             
                            + ";");
                      m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
                    initvars(null);
                  
                } 
         
           } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.updateRecordConnError};
        }
    return m;
    }
    
    public String[] deleteRecord(String[] x) {
        String[] m = new String[2];
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from code_mstr where code_key = " + "'" + x[1] + "'" 
                           + " and code_code = " + "'" + x[0] + "'" 
                           + ";");
                    if (i > 0) {
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess};
                    initvars(null);
                    }
                } catch (SQLException s) {
                 MainFrame.bslog(s); 
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
            m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordConnError};
        }
        } else {
           m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordCanceled}; 
        }
     return m;
    }
    
    
    public String[] getRecord(String[] x) {
        String[] m = new String[2];
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                if (x == null && x.length < 1) { return new String[]{}; };
                // two key system....make accomodation for first key action performed returning first record where it exists..else grab specific rec with both keys
                if (x[1].isEmpty()) {
                res = st.executeQuery("select * from code_mstr where code_code = " + "'" + x[0] + "'"  + " limit 1 ;"); 
                } else {
                 res = st.executeQuery("select * from code_mstr where code_code = " + "'" + x[0] + "'"  + 
                        " and code_key = " + "'" + x[1] + "'" +
                        ";");   
                }
                while (res.next()) {
                    i++;
                    tbkey.setText(x[0]);
                    tbkey2.setText(res.getString("code_key"));
                    tbvalue.setText(res.getString("code_value"));
                    
                }
              
           
               // set Action if Record found (i > 0)
                m = setAction(i);
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getRecordSQLError};  
            }
            bsmf.MainFrame.con.close();
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
        tbkey = new javax.swing.JTextField();
        btupdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btdelete = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        tbkey2 = new javax.swing.JTextField();
        tbvalue = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btbrowse = new javax.swing.JButton();
        btbrowsekey = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Generic Code Maintenance"));

        tbkey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkeyActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Key1");

        jLabel2.setText("Key2");

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        tbkey2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbkey2ActionPerformed(evt);
            }
        });

        jLabel3.setText("Value / Desc");

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btbrowsekey.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowsekey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowsekeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(btupdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btadd))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btbrowsekey, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btdelete)
                        .addGap(142, 142, 142)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnew)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(btnew))
                    .addComponent(btbrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbkey2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btbrowsekey))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbvalue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btdelete)
                    .addComponent(btupdate))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
          if (! validateInput("addRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("add", new String[]{tbkey.getText(), tbkey2.getText()});
    }//GEN-LAST:event_btaddActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        if (! validateInput("updateRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("update", new String[]{tbkey.getText(), tbkey2.getText()});
    }//GEN-LAST:event_btupdateActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
       if (! validateInput("deleteRecord")) {
           return;
       }
        setPanelComponentState(this, false);
        executeTask("delete", new String[]{tbkey.getText(), tbkey2.getText()});   
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"gencodemaint","code_code"});
        
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btbrowsekeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowsekeyActionPerformed
        reinitpanels("BrowseUtil", true, new String[]{"gencodemaint","code_key"});
    }//GEN-LAST:event_btbrowsekeyActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       newAction("");
    }//GEN-LAST:event_btnewActionPerformed

    private void tbkeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkeyActionPerformed
        executeTask("get", new String[]{tbkey.getText(), tbkey2.getText()});
    }//GEN-LAST:event_tbkeyActionPerformed

    private void tbkey2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbkey2ActionPerformed
       executeTask("get", new String[]{tbkey.getText(), tbkey2.getText()});
    }//GEN-LAST:event_tbkey2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btbrowsekey;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btupdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbkey;
    private javax.swing.JTextField tbkey2;
    private javax.swing.JTextField tbvalue;
    // End of variables declaration//GEN-END:variables
}
