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
import com.blueseer.adm.admData;
import static com.blueseer.fgl.fglData.addUpdateGLCtrl;
import static com.blueseer.fgl.fglData.clearGLEntries;
import static com.blueseer.fgl.fglData.getGLCtrl;
import com.blueseer.fgl.fglData.gl_ctrl;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.BlueSeerUtils.dbaction;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.IBlueSeerc;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaData;
import java.awt.Component;
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
public class GLControl extends javax.swing.JPanel implements IBlueSeerc {

   
    public GLControl() {
        initComponents();
        setLanguageTags(this);
    }
// global variable declarations
        boolean isLoad = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        boolean canUpdate = false;
        private static ArrayList<String> accounts = new ArrayList<>();
                private static gl_ctrl x = null;
    
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
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
        if (! canUpdate) {
            bsmf.MainFrame.show(getMessageTag(1185));
            return false;
        }
                                
                
        if (tbbsfrom.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbbsfrom.requestFocus();
            return false;
        }
        if (tbbsto.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbbsto.requestFocus();
            return false;
        }
        if (tbisfrom.getText().isEmpty() ) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbisfrom.requestFocus();
            return false;
        }
        if (tbisto.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbisto.requestFocus();
            return false;
        }
        if (tbearnings.getText().isEmpty() || ! accounts.contains(tbearnings.getText())) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbearnings.requestFocus();
            return false;
        }
        if (tbforeignreal.getText().isEmpty() || ! accounts.contains(tbforeignreal.getText())) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbforeignreal.requestFocus();
            return false;
        }
                
                
               
        return true;
    }
    
    public void initvars(String[] arg) {
            setComponentDefaultValues(initDataSets == null);
            executeTask(dbaction.get, new String[]{""});
    }
    
    public String[] updateRecord(String[] x) {
     String[] m = addUpdateGLCtrl(createRecord());
     SysMeta();
        return m;
     }
      
    public String[] getRecord(String[] key) {
       x = getGLCtrl(key);
        return x.m();
    }
    
    public gl_ctrl createRecord() {
        gl_ctrl x = new gl_ctrl(null, 
        tbbsfrom.getText(),
        tbbsto.getText(),
        tbisfrom.getText(),
        tbisto.getText(),
        tbearnings.getText(),
        tbforeignreal.getText(),
        String.valueOf(BlueSeerUtils.boolToInt(cbautopost.isSelected())),
        String.valueOf(BlueSeerUtils.boolToInt(cbcurrmtl.isSelected()))
        );
        return x;
    }
        
    public void updateForm() {
    tbbsfrom.setText(x.gl_bs_from());
    tbbsto.setText(x.gl_bs_to());
    tbisfrom.setText(x.gl_is_from());
    tbisto.setText(x.gl_is_to());
    tbearnings.setText(x.gl_earnings());
    tbforeignreal.setText(x.gl_foreignreal());
    cbautopost.setSelected(BlueSeerUtils.ConvertStringToBool(x.gl_autopost()));
    cbcurrmtl.setSelected(BlueSeerUtils.ConvertStringToBool(x.gl_currmtl()));
    
    // get sysmeta recs
    ArrayList<String[]> obc = getSysMetaData("system", "glcontrol");
        for (String[] s : obc) {
            if (s[0].equals("burden_rate")) {
                tbbdnrate.setText(s[1]);
            }
        } 
    }
    
     // additional methods
    public void SysMeta() {
       OVData.addUpdateSysMeta("system", "glcontrol", "burden_rate", tbbdnrate.getText().trim()); 
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
        btcleargl = new javax.swing.JButton();
        tbbdnrate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbcurrmtl = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Balance Sheet To Acct");
        jLabel2.setName("lblbaltoacct"); // NOI18N

        jLabel1.setText("Balance Sheet From Acct");
        jLabel1.setName("lblbalfromacct"); // NOI18N

        jLabel3.setText("Income Statement From Acct");
        jLabel3.setName("lblincfromacct"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel4.setText("Income Statement To Acct");
        jLabel4.setName("lblinctoacct"); // NOI18N

        jLabel5.setText("Retained Earnings Acct");
        jLabel5.setName("lblretainearnings"); // NOI18N

        jLabel6.setText("Foreign Currency G/L Acct");
        jLabel6.setName("lblforeigncurrency"); // NOI18N

        cbautopost.setText("Auto Post?");
        cbautopost.setName("cbautopost"); // NOI18N

        btcleargl.setText("Clear GL Entries");
        btcleargl.setName("btcleargl"); // NOI18N
        btcleargl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearglActionPerformed(evt);
            }
        });

        jLabel7.setText("Burden/Overhead Rate");

        cbcurrmtl.setText("Current Matl Costing");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbbdnrate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbforeignreal)
                    .addComponent(tbearnings)
                    .addComponent(tbisto)
                    .addComponent(tbisfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbbsto, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbbsfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(btcleargl)
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btupdate)
                    .addComponent(cbcurrmtl)
                    .addComponent(cbautopost))
                .addGap(26, 26, 26))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbdnrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbautopost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbcurrmtl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btupdate)
                    .addComponent(btcleargl))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
       if (! validateInput(dbaction.update)) {
           return;
       }
        executeTask(dbaction.update, null);
    }//GEN-LAST:event_btupdateActionPerformed

    private void btclearglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearglActionPerformed
        if (bsmf.MainFrame.warn(getMessageTag(1178))) {
            int i = clearGLEntries();
            bsmf.MainFrame.show("cleared GL entry count: " + i);
        }
    }//GEN-LAST:event_btclearglActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcleargl;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbautopost;
    private javax.swing.JCheckBox cbcurrmtl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbbdnrate;
    private javax.swing.JTextField tbbsfrom;
    private javax.swing.JTextField tbbsto;
    private javax.swing.JTextField tbearnings;
    private javax.swing.JTextField tbforeignreal;
    private javax.swing.JTextField tbisfrom;
    private javax.swing.JTextField tbisto;
    // End of variables declaration//GEN-END:variables
}
