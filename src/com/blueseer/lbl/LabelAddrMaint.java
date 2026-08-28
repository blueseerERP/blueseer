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

package com.blueseer.lbl;

import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.getSiteMstr;
import com.blueseer.adm.admData.site_mstr;
import com.blueseer.ctr.cusData;
import com.blueseer.ctr.cusData.cms_det;
import static com.blueseer.ctr.cusData.getCMSDet;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.checkForCustomPath;
import static com.blueseer.utl.OVData.getSystemLabelDirectory;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
public class LabelAddrMaint extends javax.swing.JPanel {

// global variable declarations
        boolean isLoad = false;
        boolean canUpdate = false;
        boolean isAutoPost = false;
        ArrayList<String[]> initDataSets = null;
        String defaultSite = "";
        String defaultCurrency = "";
        String defaultCC = "";
        String labelDir = "";
        cms_det cms = null;
        site_mstr site = null;
    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public LabelAddrMaint() {
        initComponents();
        setLanguageTags(this);
    }
   
    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
         
          String action = "";
          String[] key = null;
          
          public Task(String action, String[] key) { 
              this.action = action;
              this.key = key;
          }     
            
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
            switch(this.action) {
                case "dataInit":
                    message = getInitialization();
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
            
            
            if (this.action.equals("dataInit")) {
                    done_Initialization();
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
       executeTask("dataInit", null);
        
    }
    
    public String[] getInitialization() {
        initDataSets  = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "customers,printers");
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        isLoad = true;
        btprint.setEnabled(true);
        lbladdr.setText("");
        
        ddshipto.removeAllItems();
        ddbillto.removeAllItems();
        ddprinter.removeAllItems();
        
        for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            if (s[0].equals("labeldir")) {
              labelDir = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            if (s[0].equals("customers")) {
              ddbillto.addItem(s[1]);  
            }
            if (s[0].equals("printers")) {
              ddprinter.addItem(s[1]);  
            }
            
        }
        
        if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1139));
            btprint.setEnabled(false);
        }
        
        site = getSiteMstr(new String[]{defaultSite});
        
       isLoad = false;
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
        jLabel1 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        ddprinter = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        ddbillto = new javax.swing.JComboBox();
        ddshipto = new javax.swing.JComboBox();
        lbladdr = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Address Label Print"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel1.setText("Shipto Code:");
        jLabel1.setName("lblship"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel3.setText("Billto Code:");
        jLabel3.setName("lblcust"); // NOI18N

        jLabel2.setText("Printer:");
        jLabel2.setName("lblprinter"); // NOI18N

        ddbillto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddbilltoActionPerformed(evt);
            }
        });

        ddshipto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddshiptoActionPerformed(evt);
            }
        });

        lbladdr.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        lbladdr.setText("                                                                                                                                                 ");
        lbladdr.setBorder(javax.swing.BorderFactory.createTitledBorder("Address"));
        lbladdr.setName("paneladdress"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btprint)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ddprinter, 0, 170, Short.MAX_VALUE)
                                .addComponent(ddshipto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btprint))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

            if (ddprinter.getSelectedItem() == null) {
                 bsmf.MainFrame.show(getMessageTag(1140));
                 return;
            }
            
            if (cms == null) {
                bsmf.MainFrame.show("shipto record not found");
                return;
            }

            try {
            Path template = checkForCustomPath(getSystemLabelDirectory(), "address.prn");
             
            BufferedReader fsr = new BufferedReader(new FileReader(template.toFile(), StandardCharsets.UTF_8));
            String line = "";
            String concatline = "";
            while ((line = fsr.readLine()) != null) {
                concatline += line;
            }
            fsr.close();

            java.util.Date now = new java.util.Date();
            DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");

            concatline = concatline.replace("$ADDRNAME", cms.cms_name());
            concatline = concatline.replace("$ADDRLINE1", cms.cms_line1());
            concatline = concatline.replace("$ADDRLINE2", cms.cms_line2());
            concatline = concatline.replace("$ADDRCITY", cms.cms_city());
            concatline = concatline.replace("$ADDRSTATE", cms.cms_state());
            concatline = concatline.replace("$ADDRZIP", cms.cms_zip());

            concatline = concatline.replace("$SITENAME", site.site_desc());
            concatline = concatline.replace("$SITEADDR", site.site_line1());
            concatline = concatline.replace("$SITEPHONE", site.site_phone());
            concatline = concatline.replace("$SITECSZ", site.site_city() + ", " + site.site_state() + "  " + site.site_zip());

            concatline = concatline.replace("$TODAYDATE", dfdate.format(now));

            OVData.printLabelStream(concatline, ddprinter.getSelectedItem().toString());

            } catch (Exception e) {
            MainFrame.bslog(e);
            }
    }//GEN-LAST:event_btprintActionPerformed

    private void ddshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshiptoActionPerformed
       if (ddshipto.getSelectedItem() != null && ddbillto.getSelectedItem() != null) {
        cms = getCMSDet(ddshipto.getSelectedItem().toString(), ddbillto.getSelectedItem().toString());
        if (cms != null) {
        lbladdr.setText(cms.cms_name() + "  " + cms.cms_line1() + "  " + cms.cms_city() + ", " + cms.cms_state() + " " + cms.cms_zip());
        }
       }
    }//GEN-LAST:event_ddshiptoActionPerformed

    private void ddbilltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddbilltoActionPerformed
        ddshipto.removeAllItems();
        ArrayList mycusts = cusData.getcustshipmstrlist(ddbillto.getSelectedItem().toString());
        for (int i = 0; i < mycusts.size(); i++) {
            ddshipto.addItem(mycusts.get(i));
        }
    }//GEN-LAST:event_ddbilltoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btprint;
    private javax.swing.JComboBox ddbillto;
    private javax.swing.JComboBox ddprinter;
    private javax.swing.JComboBox ddshipto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbladdr;
    // End of variables declaration//GEN-END:variables
}
