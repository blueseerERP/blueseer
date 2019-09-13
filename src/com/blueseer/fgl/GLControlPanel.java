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
package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class GLControlPanel extends javax.swing.JPanel implements IBlueSeerc {

   
    public GLControlPanel() {
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
                                
                
                if (tbbsfrom.getText().isEmpty() || ! OVData.isValidGLAcct(tbbsfrom.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbbsfrom.requestFocus();
                    return b;
                }
                if (tbbsto.getText().isEmpty() || ! OVData.isValidGLAcct(tbbsto.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbbsto.requestFocus();
                    return b;
                }
                if (tbisfrom.getText().isEmpty() || ! OVData.isValidGLAcct(tbisfrom.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbisfrom.requestFocus();
                    return b;
                }
                if (tbisto.getText().isEmpty() || ! OVData.isValidGLAcct(tbisto.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbisto.requestFocus();
                    return b;
                }
                if (tbearnings.getText().isEmpty() || ! OVData.isValidGLAcct(tbearnings.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbearnings.requestFocus();
                    return b;
                }
                if (tbforeignreal.getText().isEmpty() || ! OVData.isValidGLAcct(tbforeignreal.getText())) {
                    b = false;
                    bsmf.MainFrame.show("must enter a valid acct");
                    tbforeignreal.requestFocus();
                    return b;
                }
                
                
               
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
                
                    
                String autopost = "";
                  if ( cbautopost.isSelected() ) {
                autopost = "1";    
                } else {
                    autopost = "0";
                }
                    int i = 0;
                    res = st.executeQuery("SELECT *  FROM  gl_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                     if (i == 0) {

                    st.executeUpdate("insert into gl_ctrl values (" + "'" + tbbsfrom.getText() + "'" + ","
                            + "'" + tbbsto.getText() + "'" + "," 
                            + "'" + tbisfrom.getText() + "'" + ","
                            + "'" + tbisto.getText() + "'" + ","
                            + "'" + tbearnings.getText() + "'" + ","
                            + "'" + tbforeignreal.getText() + "'"  + ","
                            + "'" + autopost + "'"        
                            + " )" + ";");              
                    m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
                } else {
                   st.executeUpdate("update gl_ctrl set " 
                            + " gl_bs_from = " + "'" + tbbsfrom.getText() + "'" + ","
                            + " gl_bs_to = " + "'" + tbbsto.getText() + "'" + "," 
                            + " gl_is_from = " + "'" + tbisfrom.getText() + "'" + "," 
                            + " gl_earnings = " + "'" + tbearnings.getText() + "'" + "," 
                            + " gl_foreignreal = " + "'" + tbforeignreal.getText() + "'" + ","         
                            + " gl_is_to = " + "'" + tbisto.getText() + "'" + ","
                            + " gl_autopost = " + "'" + autopost + "'" +
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
                
                res = st.executeQuery("select * from gl_ctrl;");
                while (res.next()) {
                    i++;
                    tbbsfrom.setText(res.getString("gl_bs_from"));
                    tbbsto.setText(res.getString("gl_bs_to"));
                    tbisfrom.setText(res.getString("gl_is_from"));
                    tbisto.setText(res.getString("gl_is_to"));
                    tbearnings.setText(res.getString("gl_earnings"));
                    tbforeignreal.setText(res.getString("gl_foreignreal"));
                    cbautopost.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("gl_autopost")));
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
        tbisfrom = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tbbsfrom = new javax.swing.JTextField();
        tbbsto = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbisto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbearnings = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tbforeignreal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cbautopost = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("GL Control Maintenance"));

        jLabel2.setText("Balance Sheet To Acct");

        jLabel1.setText("Balance Sheet From Acct");

        jLabel3.setText("Income Statement From Acct");

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel4.setText("Income Statement To Acct");

        jLabel5.setText("Retained Earnings Acct");

        jLabel6.setText("Foreign Currency G/L Acct");

        cbautopost.setText("Auto Post?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbautopost)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tbisfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbbsto, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tbbsfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbisto, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                .addComponent(btupdate, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbearnings)
                                .addComponent(tbforeignreal, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbsfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbsto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbisfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbisto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbearnings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbforeignreal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbautopost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(btupdate))
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
    private javax.swing.JCheckBox cbautopost;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbbsfrom;
    private javax.swing.JTextField tbbsto;
    private javax.swing.JTextField tbearnings;
    private javax.swing.JTextField tbforeignreal;
    private javax.swing.JTextField tbisfrom;
    private javax.swing.JTextField tbisto;
    // End of variables declaration//GEN-END:variables
}
