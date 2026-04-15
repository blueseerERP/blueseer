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
import static com.blueseer.lbl.lblData.addMixedLabelTransaction;
import static com.blueseer.lbl.lblData.getLabelZebraMstr;
import com.blueseer.lbl.lblData.label_det;
import com.blueseer.lbl.lblData.label_mstr;
import com.blueseer.lbl.lblData.label_zebra;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.getOrderLineInfo;
import static com.blueseer.ord.ordData.getOrderMstrSet;
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
public class LabelMixedContMaint extends javax.swing.JPanel {


int serialno = 0;
String serialno_str = "";
String serialno_display = "";
String quantity = "";
String labelname = "";

boolean isLoad = false;
private static ordData.so_mstr so = null;
private static ordData.sod_det sod = null;
private static ordData.salesOrder soset = null;
private static admData.site_mstr sm = null;
boolean canUpdate = false;
boolean isAutoPost = false;
ArrayList<String[]> initDataSets = null;
String defaultSite = "";
String defaultCurrency = "";
String syslabeldir = "";    

   javax.swing.table.DefaultTableModel itemmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("line"), 
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"),
                getGlobalColumnTag("orderqty"),
                "Label Quantity"
            });  
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public LabelMixedContMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void getOrderInfo(String order, String line) {
        soset = getOrderMstrSet(new String[]{order});
        so = soset.so();
        for (ordData.sod_det sd : soset.sod()) {
            if (sd.sod_line() == bsParseInt(line)) { 
                sod = sd;
                break;
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
    
    public void setComponentDefaultValues(boolean init) {
       isLoad = true;
       lblcust.setText("");
        lblship.setText("");
        lblitem.setText("");
        tbqty.setText("");
        tbordnbr.setText("");
        tbline.setText("");
        tbqty.setText("");
        tblblqty.setText("1");
        tbordnbr.setEditable(true);
        btprint.setEnabled(true);
        
        itemmodel.setRowCount(0);
        itemtable.setModel(itemmodel);
        itemtable.getTableHeader().setReorderingAllowed(false);
        
        
        
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
        /*
        if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show("No Printers Available");
            btprint.setEnabled(false);
        }
        */
       isLoad = false;
    }
    
    public void initvars(String[] arg) {
        setComponentDefaultValues(initDataSets == null);
    }
    
    public void lookUpFrameOrder() {
        
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
                tbordnbr.setEditable(false);
               // getOrderInfo(tbordnbr.getText(), target.getValueAt(row,1).toString());
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
                tbline.setText(target.getValueAt(row,1).toString());
                tbqty.setText(BlueSeerUtils.bsNumber(target.getValueAt(row,4).toString()));
                lblitem.setText(target.getValueAt(row,2).toString() + " " + target.getValueAt(row,3).toString());
                getOrderInfo(tbordnbr.getText(), target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"));
        
        
    }

    public boolean validateInput(boolean itemlevel) {
        
        if (itemlevel) {
            if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
                bsmf.MainFrame.show(getMessageTag(1028));
                tbqty.requestFocus();
                tbqty.setBackground(Color.yellow);
                return false;
            } else {
                tbqty.setBackground(Color.white);
            }
        }
        
        if (itemlevel && tbordnbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbordnbr.requestFocus();
            return false;
        }
        if (itemlevel && tbline.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbline.requestFocus();
            return false;
        }
        
        if (! itemlevel && ! BlueSeerUtils.isNumeric(tblblqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tblblqty.requestFocus();
            tblblqty.setBackground(Color.yellow);
            return false;
        } else {
            tblblqty.setBackground(Color.white);
        }
        
        if (! itemlevel && ddprinter.getSelectedItem() == null) {
            bsmf.MainFrame.show(getMessageTag(1140));
            return false;
        }
        if (! itemlevel && itemtable.getRowCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1164));
            return false;
        }
        
        return true;
    }
    
    public label_mstr createRecord() { 
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
         label_mstr x = new label_mstr(null, 
                 serialno_str, 
                 "mixeditems", 
                 sod.sod_custitem(), 
                 serialno_display, 
                 "XX", 
                 labelname,
                 "0", 
                 so.so_po(),
                 soset.cm().cm_code(),
                 so.so_nbr(), 
                 bsNumber(sod.sod_line()), 
                 tbref.getText(), 
                 "",  // lot 
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
                 "LabelMixedContPanel", 
                 so.so_site(), 
                 "", // loc
                 "CONT",
                 "mixed",
                 soset.cms().cms_shipto(),
                     "0", // scan
                     "0",  // void
                     "0"  // post
                );
        return x;
    }
   
    public ArrayList<label_det> createDetRecord() { 
        ArrayList<label_det> det = new ArrayList<label_det>();
         for (int j = 0; j < itemtable.getRowCount(); j++) {
         label_det x = new label_det(null,
                 serialno_str,
                 tbordnbr.getText(),
                 itemtable.getValueAt(j, 0).toString(),
                 itemtable.getValueAt(j, 1).toString(),
                 itemtable.getValueAt(j, 2).toString(),
                 BlueSeerUtils.bsParseDouble(itemtable.getValueAt(j, 4).toString()) // label qty
                );
         det.add(x);
         }
        return det;
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
        jLabel8 = new javax.swing.JLabel();
        btlookupOrder = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemtable = new javax.swing.JTable();
        btadditem = new javax.swing.JButton();
        btdeleteitem = new javax.swing.JButton();
        btlookupLine = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        lblcust = new javax.swing.JLabel();
        lblship = new javax.swing.JLabel();
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

        jLabel4.setText("Qty On Label");
        jLabel4.setName("lblqty"); // NOI18N

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        tbline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tblineActionPerformed(evt);
            }
        });

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

        btlookupOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupOrderActionPerformed(evt);
            }
        });

        itemtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(itemtable);

        btadditem.setText("Add Item");
        btadditem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btadditemActionPerformed(evt);
            }
        });

        btdeleteitem.setText("Delete Item");
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btlookupLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btlookupLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupLineActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookupOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btadditem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeleteitem))
                            .addComponent(lblcust, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblship, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookupLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblitem, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btprint)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tblblqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)))
                .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btlookupOrder)
                    .addComponent(btclear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(btlookupLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblitem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblcust, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblship, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btadditem)
                            .addComponent(btdeleteitem))
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(31, 31, 31)
                        .addComponent(btprint)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(34, 34, 34))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

        if (! validateInput(false)) {
            return;
        }
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
        
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
        
        String[] x = addMixedLabelTransaction(createDetRecord(),createRecord());
        if (ddprinter.getSelectedItem() != null && ddprinter.getSelectedItem().toString().equals("<record only>")) {
          bsmf.MainFrame.show("label created: " + serialno);
          return;
        }
        
        
        // if sscc18J type label
        if (lz.lblz_file().endsWith("jasper") &&
            ddprinter.getSelectedItem() != null && ! ddprinter.getSelectedItem().toString().equals("<record only>") ) {
           // printSSCC18J(tbordnbr.getText(), tbline.getText(), serialno_display, tbref.getText(), tbqty.getText());
            bsmf.MainFrame.show("Customer has jasper label assignment.  sscc18J label format only supported by PDF Container Label");
            return;
        }
        
        
        // else all other type of labels 
        if (ddprinter.getSelectedItem() != null && ! ddprinter.getSelectedItem().toString().equals("<record only>")) {
            try {
        
            Path template = checkForCustomPath(syslabeldir, lz.lblz_file());
            File f = template.toFile();
            if(f.exists() && !f.isDirectory()) { 
                
                // get zpl string from file
                BufferedReader fsr = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8));
                String line = "";
                String concatline = "";
                while ((line = fsr.readLine()) != null) {
                    concatline += line;
                }
                fsr.close();
                // replace variables with values
                concatline = concatline.replace("$PART", sod.sod_item());
                concatline = concatline.replace("$CUSTPART", sod.sod_custitem());
                concatline = concatline.replace("$SERIALNO", serialno_display);
                concatline = concatline.replace("$QUANTITY", quantity);
                concatline = concatline.replace("$DESCRIPTION", sod.sod_desc());
                concatline = concatline.replace("$CUSTCODE", so.so_cust());
                concatline = concatline.replace("$ADDRNAME", "");
                concatline = concatline.replace("$REV", tbref.getText());
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

    private void btlookupOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderActionPerformed
        lookUpFrameOrder();
    }//GEN-LAST:event_btlookupOrderActionPerformed

    private void btlookupLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupLineActionPerformed
        lookUpFrameLine();
    }//GEN-LAST:event_btlookupLineActionPerformed

    private void btadditemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btadditemActionPerformed
        
        if (! validateInput(true)) {
            return;
        }

        
        String[] x = getOrderLineInfo(tbordnbr.getText(), tbline.getText());
        if (x == null) {
            return;
        }
        for (int j = 0; j < itemtable.getRowCount(); j++) {
             if (itemtable.getValueAt(j, 0).toString().equals(tbline.getText())) {
                 bsmf.MainFrame.show("line item already added");
                 return;
             } 
         }
        itemmodel.addRow(new Object[]{ tbline.getText(), x[0], x[1], x[2], tbqty.getText() }); 
    }//GEN-LAST:event_btadditemActionPerformed

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = itemtable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show(getMessageTag(1031,String.valueOf(i)));
            ((javax.swing.table.DefaultTableModel) itemtable.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initDataSets = null;
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void tblineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tblineActionPerformed
       if (! tbordnbr.getText().isBlank() && ! tbline.getText().isBlank()) {
       String[] info = getOrderLineInfo(tbordnbr.getText(), tbline.getText());
       tbqty.setText(info[2]);
       lblitem.setText(info[0] + " " + info[1]);
       }
    }//GEN-LAST:event_tblineActionPerformed

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
    private javax.swing.JButton btadditem;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JButton btlookupLine;
    private javax.swing.JButton btlookupOrder;
    private javax.swing.JButton btprint;
    private javax.swing.JComboBox ddprinter;
    private javax.swing.JTable itemtable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblcust;
    private javax.swing.JLabel lblitem;
    private javax.swing.JLabel lblship;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTextField tblblqty;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
