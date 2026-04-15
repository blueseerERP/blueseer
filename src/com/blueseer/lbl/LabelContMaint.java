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
import static com.blueseer.lbl.lblData.addLabelMstr;
import static com.blueseer.lbl.lblData.getLabelZebraMstr;
import com.blueseer.lbl.lblData.label_mstr;
import com.blueseer.lbl.lblData.label_zebra;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import com.blueseer.ord.ordData.salesOrder;
import com.blueseer.ord.ordData.so_mstr;
import com.blueseer.ord.ordData.sod_det;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkDigitUCC18;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.setDateFormat;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.checkForCustomPath;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
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
import javax.swing.JTable;

/**
 *
 * @author vaughnte
 */
public class LabelContMaint extends javax.swing.JPanel {

String revnbr = "";
int serialno = 0;
String serialno_str = "";
String serialno_display = "";
String quantity = "";
String labelname = "";
boolean isLoad = false;
private static so_mstr so = null;
private static sod_det sod = null;
private static salesOrder soset = null;
private static site_mstr sm = null;
boolean canUpdate = false;
boolean isAutoPost = false;
ArrayList<String[]> initDataSets = null;
String defaultSite = "";
String defaultCurrency = "";
String syslabeldir = "";    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public LabelContMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public boolean validateInput() {
       
        
        
        if (tbordnbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbordnbr.requestFocus();
            tbordnbr.setBackground(Color.yellow);
            return false;
        } else {
            tbordnbr.setBackground(Color.white);
        }
        
        if (tbline.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbline.requestFocus();
            tbline.setBackground(Color.yellow);
            return false;
        } else {
            tbline.setBackground(Color.white);
        }
        
        if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            tbqty.setBackground(Color.yellow);
            return false;
        } else {
            tbqty.setBackground(Color.white);
        }
        
        if (! BlueSeerUtils.isNumeric(tblblqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tblblqty.requestFocus();
            tblblqty.setBackground(Color.yellow);
            return false;
        } else {
            tblblqty.setBackground(Color.white);
        }
        
        return true;
    }
    
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
       tbqty.setText("");
        tbordnbr.setText("");
        tbline.setText("");
        tbqty.setText("");
        tblblqty.setText("");
        
        btprint.setEnabled(true);
        
        
        
       if (init) {
          initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "printers");
        }
       
       ddprinter.removeAllItems();
         
         for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            
            if (s[0].equals("labeldir")) {
              syslabeldir = s[1];  
            }
            
            if (s[0].equals("autopost")) {
              isAutoPost = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            
            if (s[0].equals("printers")) {
              ddprinter.addItem(s[1]);  
            }
           
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
        }
        
        if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show("No Printers Available");
            btprint.setEnabled(false);
        } 
        ddprinter.insertItemAt("<record only>",0);
        
        sm = getSiteMstr(new String[]{defaultSite});
        
        
       isLoad = false;
    }
       
    public void getOrderInfo(String order, String line) {
        soset = getOrderMstrSet(new String[]{order});
        so = soset.so();
        for (sod_det sd : soset.sod()) {
            if (sd.sod_line() == bsParseInt(line)) { 
                sod = sd;
                break;
            }
        }
        
        tbline.setText(line);
        tbqty.setText(BlueSeerUtils.bsNumber(sod.sod_ord_qty()));
        lblitem.setText(sod.sod_item() + " " + sod.sod_desc());
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
        setComponentDefaultValues(initDataSets == null);
    }
    
    public void lookUpFrameOrderLine() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderBrowseUtil(luinput.getText(), 0, "so_nbr" );
        } else {
         luModel = DTData.getOrderBrowseUtil(luinput.getText(), 0, "so_cust" );
        }    
        
         
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                
                tbordnbr.setText(target.getValueAt(row,1).toString());
                //tbline.setText(target.getValueAt(row,2).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("order"), 
                getGlobalColumnTag("customer"));
        
        
    }

    public void lookUpFrameLine() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
         
        if (lurb1.isSelected()) {  
         luModel = DTData.getOrderLineBrowseUtil(luinput.getText(), "sod_item", tbordnbr.getText() );
        } else {
         luModel = DTData.getOrderLineBrowseUtil(luinput.getText(), "sod_desc", tbordnbr.getText());
        }    
        
         
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                getOrderInfo(tbordnbr.getText(), target.getValueAt(row,1).toString());
                
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"));
        
        
    }
    
    public label_mstr createRecord() { 
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
         label_mstr x = new label_mstr(null, 
                 serialno_str, 
                 sod.sod_item(), 
                 sod.sod_custitem(), 
                 serialno_display, 
                 "XX", 
                 labelname,
                 tbqty.getText(), 
                 so.so_po(),
                 soset.cm().cm_code(),
                 so.so_nbr(), 
                 bsNumber(sod.sod_line()), 
                 (tbref.getText().isBlank()) ? "" : tbref.getText(), 
                 (tblot.getText().isBlank()) ? "" : tblot.getText(), 
                 "0", 
                 "0", 
                 soset.cms().cms_name(), 
                 soset.cms().cms_line1(), 
                 soset.cms().cms_line2(), 
                 soset.cms().cms_city(), 
                 soset.cms().cms_state(), 
                 soset.cms().cms_state(), 
                 soset.cms().cms_zip(), 
                 soset.cms().cms_country(), 
                 setDateFormat(now), 
                 setDateFormat(now), 
                 bsmf.MainFrame.userid, 
                 ddprinter.getSelectedItem().toString(), 
                 "LabelContPanel", 
                 so.so_site(), 
                 "", 
                 "CONT",
                 "flat",
                 soset.cms().cms_shipto(),
                     "0", // scan
                     "0",  // void
                     "0"  // post
                );
        return x;
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
        btprint = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        ddprinter = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        tbordnbr = new javax.swing.JTextField();
        tbline = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tblblqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        tblot = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btlookupOrderLine = new javax.swing.JButton();
        btlookupLine = new javax.swing.JButton();
        lblitem = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Container Label Print (4 x 6)"));
        jPanel1.setName("panelmain"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel3.setText("Order Number");
        jLabel3.setName("lblorder"); // NOI18N

        jLabel2.setText("Printer");
        jLabel2.setName("lblprinter"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel4.setText("Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        jLabel6.setText("Order Line");
        jLabel6.setName("lblorderline"); // NOI18N

        tblblqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblblqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Number of Labels");
        jLabel7.setName("lblnumber"); // NOI18N

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

        jLabel9.setText("Lot");
        jLabel9.setName("lbllot"); // NOI18N

        btlookupOrderLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrderLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderLineActionPerformed(evt);
            }
        });

        btlookupLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupLineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btprint))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tblot, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookupLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(tblblqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookupOrderLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(47, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(20, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btlookupOrderLine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(btlookupLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblblqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprint)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

        if (! validateInput()) {
            return;
        }
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
        
        
        
        quantity = tbqty.getText();
               
        
        
        if (tbordnbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbordnbr.requestFocus();
            return;
        }
        if (tbline.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbline.requestFocus();
            return;
        }
        
        
        if (ddprinter.getSelectedItem() == null) {
            bsmf.MainFrame.show(getMessageTag(1140));
            return;
        }
        
       
       
        int nbroflabels = bsParseInt(tblblqty.getText());
       

    

    String label  = (soset.cm().cm_label().isBlank()) ? "generic" : soset.cm().cm_label();  

    label_zebra lz = getLabelZebraMstr(new String[]{label});

    labelname = label;

    serialno = OVData.getNextNbr("label");
    serialno_str = String.valueOf(serialno);

    if (lz.lblz_type().toLowerCase().equals("ucc")) {
        serialno_display = checkDigitUCC18(serialno);
    } else {
        serialno_display = serialno_str;
    }

    // ok....apparently we have a label/printer match.... lets create the label_mstr record for this label
    String[] x = addLabelMstr(createRecord()); 
    if (ddprinter.getSelectedItem() != null && ddprinter.getSelectedItem().toString().equals("<record only>")) {
          bsmf.MainFrame.show("label created: " + serialno);
          return;
    }          

    // if sscc18J type label
    if (lz.lblz_file().endsWith("jasper") &&
       ddprinter.getSelectedItem() != null && ! ddprinter.getSelectedItem().toString().equals("<record only>") ) {
       // printSSCC18J(tbordnbr.getText(), tbline.getText(), serialno_display, tbref.getText(), tbqty.getText());
        bsmf.MainFrame.show("Customer has jasper label assignment.  sscc18J label format only supported by PDF Label Print program");
        return;
    }

    if (ddprinter.getSelectedItem() != null && ! ddprinter.getSelectedItem().toString().equals("<record only>")) {
        try {  
        Path template = checkForCustomPath(syslabeldir, lz.lblz_file());

        if (template == null) {
            bsmf.MainFrame.show("unable to get path of background label file");
            return;
        }

        File f = template.toFile();
        if(f.exists() && !f.isDirectory()) { 

              


        BufferedReader fsr = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8));
        String line = "";
        String concatline = "";

        while ((line = fsr.readLine()) != null) {
            concatline += line;
        }
        fsr.close();
        // fos.write(concatline.getBytes());


        concatline = concatline.replace("$PART", sod.sod_item());
        concatline = concatline.replace("$CUSTPART", sod.sod_custitem());
        concatline = concatline.replace("$SERIALNO", serialno_display);
        concatline = concatline.replace("$QUANTITY", quantity);
        concatline = concatline.replace("$DESCRIPTION", sod.sod_desc());
        concatline = concatline.replace("$CUSTCODE", so.so_cust());
        concatline = concatline.replace("$ADDRNAME", "");
        concatline = concatline.replace("$REV", revnbr);
        concatline = concatline.replace("$PONUMBER", so.so_po());
        concatline = concatline.replace("$SONBR", so.so_nbr());
        concatline = concatline.replace("$SOLINE", bsNumber(sod.sod_line()));
        concatline = concatline.replace("$CARRIER", so.so_shipvia());
        concatline = concatline.replace("$SITENAME", sm.site_desc() );
        concatline = concatline.replace("$SITEADDR", sm.site_line1());
        concatline = concatline.replace("$SITEPHONE", sm.site_phone());
        concatline = concatline.replace("$SITECSZ", sm.site_city() + ", " + sm.site_state() + " " + sm.site_zip());
        concatline = concatline.replace("$SHIPNAME", soset.cms().cms_name());
        concatline = concatline.replace("$SHIPADDR1", soset.cms().cms_line1());
        concatline = concatline.replace("$SHIPADDR2", soset.cms().cms_line2());
        concatline = concatline.replace("$SHIPZIP", soset.cms().cms_zip());
        concatline = concatline.replace("$SHIPCSZ", soset.cms().cms_city() + ", " + soset.cms().cms_state() + " " + soset.cms().cms_zip());
        concatline = concatline.replace("$TODAYDATE", dfdate.format(now));
        concatline = concatline.replace("$TODAYTIME", dftime.format(now));


            OVData.printLabelStream(concatline, ddprinter.getSelectedItem().toString());


         initvars(null);
        } else {
            bsmf.MainFrame.show(getMessageTag(1142,template.toString()));
        }


        } catch (Exception e) {
        MainFrame.bslog(e);
        }
    }
    }//GEN-LAST:event_btprintActionPerformed

    private void btlookupOrderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderLineActionPerformed
        lookUpFrameOrderLine();
    }//GEN-LAST:event_btlookupOrderLineActionPerformed

    private void btlookupLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupLineActionPerformed
        lookUpFrameLine();
    }//GEN-LAST:event_btlookupLineActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            tbqty.setBackground(Color.yellow);
            return;
        } else {
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void tblblqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblblqtyFocusLost
        if (! BlueSeerUtils.isNumeric(tblblqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tblblqty.requestFocus();
            tblblqty.setBackground(Color.yellow);
            return;
        } else {
            tblblqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tblblqtyFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btlookupLine;
    private javax.swing.JButton btlookupOrderLine;
    private javax.swing.JButton btprint;
    private javax.swing.JComboBox ddprinter;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblitem;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTextField tblblqty;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tblot;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
