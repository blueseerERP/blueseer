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

import com.blueseer.edi.*;
import com.blueseer.utl.*;
import static bsmf.MainFrame.tags;
import static com.blueseer.edi.EDI.edilog;
import static com.blueseer.fgl.fglData.getGLAcctListRangeWCurrTypeDesc;
import static com.blueseer.fgl.fglData.getGLCSVSales;
import static com.blueseer.fgl.fglData.getGLIIFSales;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.convertDate;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class GLExport extends javax.swing.JPanel {

     // global variable declarations
                boolean isLoad = false;
                String defaultsite = "";
                ArrayList<String[]> rlist = new ArrayList<>();
                
    /**
     * Creates new form FileOrderLoadPanel
     */
    public GLExport() {
        initComponents();
        setLanguageTags(this);
    }

    public void setPanelComponentState(Object myobj, boolean b) {
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
                if (component instanceof JScrollPane) {
                    setPanelComponentState((JScrollPane) component, b);
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
            if (scrollpane != null) {
                scrollpane.setEnabled(b);
                JViewport viewport = scrollpane.getViewport();
                Component[] componentspane = viewport.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    component.setEnabled(b);
                }
            }
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
    
    public void setComponentDefaultValues() {
        Date today = new Date();
        isLoad = true;        
        tacomments.setText("");
        dcfrom.setDate(today);
        dcto.setDate(today);
       
       isLoad = false;
       
    }
    
    public void setState() {
        setPanelComponentState(this, true);
    }
    
    public void executeTask(String x, ArrayList<String> y, String site) { 
      
        class Task extends SwingWorker<String[], Void> { 
       
          String[] key = null;
          
        
          public Task(String x, ArrayList<String> y, String site) { 
             
          }
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            if (x.equals("Invoice")) {
                 message = exportInvoices(y);
            }
            if (x.equals("Advance Ship Notice")) {
                 message = exportASNs(y);
            }
            if (x.equals("Purchase Order")) {
                 message = exportPurchaseOrders(y);
            }
            if (x.equals("Order Acknowledgement")) {
                 message = exportACKs(y, site);
            }
            
            
            return message;
        }
      
        
       public void done() {
            try {
            String[] message = get();
            if (message[0] == null) {
                message[0] = "1"; // cancel upload
            }
            BlueSeerUtils.endTask(message);
            updateForm(); 
            setState(); 
            } catch (Exception e) {
                edilog(e);
            } 
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x,y,site);  
       z.execute();
       
       
    }
   
    
    public void initvars(String[] arg) {
      setComponentDefaultValues();
      if (! ddtype.getItemAt(0).equals("")) {
          ddtype.insertItemAt("", 0);
      }  
      
      tacomments.setText("");
    }
    
    public String[] exportPurchaseOrders(ArrayList<String> list) {
        rlist = ediData.exportPurchaseOrders(list);
        return new String[]{"0", "Processing Complete"};
    }
    
    public String[] exportInvoices(ArrayList<String> list) {
        rlist = ediData.exportInvoices(list);
        return new String[]{"0", "Processing Complete"};
    }
    
    public String[] exportASNs(ArrayList<String> list) {
        rlist = ediData.exportASNs(list);
        return new String[]{"0", "Processing Complete"};
    }
    
    public String[] exportACKs(ArrayList<String> list, String site) {
        rlist = ediData.exportACKs(list);
        return new String[]{"0", "Processing Complete"};
    }
    
    public void updateForm() {
        tacomments.append("Number of export documents: " + rlist.size() + "\n");
        int errorcount = 0;
        for (String[] s : rlist) {
          if (! s[1].equals("0")) {
              errorcount++;
          }
          tacomments.append("export attempt for key: " + s[0] + "  --- " + (s[1].equals("0") ? "success" : "error") + "\n");  
        }
        tacomments.append("\n Summary Error Count: " + errorcount + "\n");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        btrun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tacomments = new javax.swing.JTextArea();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ddformat = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dddelimiter = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("GL Export"));
        jPanel1.setName("panelmain"); // NOI18N

        btrun.setText("Export");
        btrun.setName("btrun"); // NOI18N
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        jLabel1.setText("Document Type");
        jLabel1.setName("lblid"); // NOI18N

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "accounts", "sales" }));
        ddtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddtypeActionPerformed(evt);
            }
        });

        tacomments.setColumns(20);
        tacomments.setRows(5);
        jScrollPane1.setViewportView(tacomments);

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel4.setText("From Date:");

        jLabel5.setText("To Date:");

        ddformat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "IIF", "CSV" }));

        jLabel6.setText("Format Type");

        jLabel2.setText("Delimiter");

        dddelimiter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "tab", "comma", "pipe", "semicolon", "colon" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(dcfrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(218, 218, 218))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddformat, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btrun)
                            .addComponent(dddelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddformat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(dddelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcfrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(btrun)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        tacomments.setText("");
        
        String delim = " ";
            if (dddelimiter.getSelectedItem().toString().equals("tab")) {
                delim = "\t";
            }
            if (dddelimiter.getSelectedItem().toString().equals("comma")) {
                delim = ",";
            }
            if (dddelimiter.getSelectedItem().toString().equals("pipe")) {
                delim = "|";
            }
            if (dddelimiter.getSelectedItem().toString().equals("colon")) {
                delim = ":";
            }
            if (dddelimiter.getSelectedItem().toString().equals("semicolon")) {
                delim = ";";
            }
        
            StringBuilder s = new StringBuilder();
        
        // type account list
        if (ddtype.getSelectedItem().equals("sales")) {
            if (ddformat.getSelectedItem().toString().equals("IIF")) {
                 s.append("!TRNS,TRNSID,TRNSTYPE,DATE,ACCNT,CLASS,AMOUNT,DOCNUM,MEMO,NAME,\n");
                 s.append("!SPL,SPLID,TRNSTYPE,DATE,ACCNT,CLASS,AMOUNT,DOCNUM,MEMO,NAME,\n");
                 s.append("!ENDTRNS,").append("\n");
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");   
                 DateFormat dfdateiif = new SimpleDateFormat("MM/dd/yy");  
                 ArrayList<String> list = getGLIIFSales(dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()));
                 int i = 0;
                 for (String x : list) {
                     String[] recs = x.split(",",-1);
                     if (i == 0) {                     
                         // AR ACCOUNT FIRST 
                         s.append("TRNS,,GENERAL JOURNAL,").append(convertDate("MM/dd/yy",dfdate.format(dcto.getDate()))).append(",").append(recs[0]).append(",").append("Class,").append(recs[1]).append(",,BlueSeer,BlueSeer,").append("\n");                     
                     } else {
                         s.append("SPL,,GENERAL JOURNAL,").append(convertDate("MM/dd/yy",recs[1])).append(",").append(recs[0]).append(",").append("Class,").append(recs[2]).append(",").append(recs[3]).append(",").append("BlueSeer,BlueSeer,").append("\n"); 
                     } 
                     i++;
                 }
                 s.append("ENDTRNS,").append("\n");             
            }   
            if (ddformat.getSelectedItem().toString().equals("CSV")) {                
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");  
                 ArrayList<String> list = getGLCSVSales(dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()));
                 int i = 0;
                 for (String x : list) {
                     String[] recs = x.split(",",-1);
                      s.append(recs[0]).append(",").append(recs[1]).append(",").append(recs[2]).append(",").append(recs[3]).append(",").append(recs[4]).append("\n"); 
                      
                 }          
            }      
        } // ddtype = sales
        
        if (ddtype.getSelectedItem().equals("accounts")) {
            if (ddformat.getSelectedItem().toString().equals("IIF")) {
                 s.append("!ACCNT,NAME,ACCNTTYPE,ACCNTNUM,DESC,\n");  
                 ArrayList<String[]> list = getGLAcctListRangeWCurrTypeDesc("", "");
                 int i = 0;
                 for (String[] x : list) {
                    if (x[2].equals("E")) {
                    x[2] = "EXP";
                    }
                    if (x[2].equals("I")) {
                    x[2] = "INC";
                    }
                    if (x[2].equals("A")) {
                    x[2] = "ASSET";
                    }
                    if (x[2].equals("L")) {
                    x[2] = "AP";
                    }
                    if (x[2].equals("O")) {
                    x[2] = "EQUITY";
                    }
                    if (x[0].equals("10000000")) {
                    x[2] = "BANK";
                    }
                    if (x[0].startsWith("2")) {
                    x[2] = "AR";
                    }
                    
                    s.append("ACCNT").append(",").append(x[1]).append(",").append(x[2]).append(",").append(x[0]).append(",").append(x[1]).append("\n"); 
                      
                 }          
            }       
            if (ddformat.getSelectedItem().toString().equals("CSV")) {                 
                 ArrayList<String[]> list = getGLAcctListRangeWCurrTypeDesc("", "");
                 int i = 0;
                 for (String[] x : list) { 
                    s.append(x[0]).append(",").append(x[1]).append(",").append(x[2]).append(",").append(x[3]).append("\n"); 
                 }          
            }     
        } // ddtype = accounts
        
        
        
             String str = s.toString();
             if (! dddelimiter.getSelectedItem().toString().equals("comma")) {
               str = str.replace(",", delim);
             }
             
            FileDialog fDialog;
                fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
                fDialog.setFile("download" + ".iif");
                fDialog.setVisible(true);
                String path = fDialog.getDirectory() + fDialog.getFile();
                File f = new File(path);
                BufferedWriter output = null;
                    try {
                        output = new BufferedWriter(new FileWriter(f));
                        output.write(str);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                          try {  
                              output.close();
                              tacomments.append("File has been generated: " + path);
                          } catch (IOException ex) {
                              ex.printStackTrace();
                          }
                    }
       
    }//GEN-LAST:event_btrunActionPerformed

    private void ddtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddtypeActionPerformed
        tacomments.setText("");
    }//GEN-LAST:event_ddtypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btrun;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> dddelimiter;
    private javax.swing.JComboBox<String> ddformat;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea tacomments;
    // End of variables declaration//GEN-END:variables
}
