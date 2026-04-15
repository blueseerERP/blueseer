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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.lbl.lblData.addLabelMstr;
import static com.blueseer.lbl.lblData.getLabelZebraMstr;
import com.blueseer.lbl.lblData.label_mstr;
import com.blueseer.lbl.lblData.label_zebra;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.checkDigitUCC18;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
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
import static com.blueseer.utl.OVData.getSystemLabelDirectory;
import static com.blueseer.utl.OVData.printJasperLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
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
public class PalletLabelMaint extends javax.swing.JPanel {

String item = "";
String revnbr = "";
String custitem = "";
String partdesc = "";
String billto = "";
String shipto = "";
String ref = "";
String lot = "";
String ordernbr = "";
String linenbr = "";
String ponbr = "";
int serialno = 0;
String serialno_str = "";
String serialno_display = "";
String quantity = "";
String labelname = "";

String sitename = "";
String siteaddr = "";
String sitephone = "";
String sitecitystatezip = "";

String shipname = "";
String shipaddr1 = "";
String shipaddr2 = "";
String shipcity = "";
String shipstate = "";
String shipzip = "";
String shipcountry = "";
String shipcsz = "";

String carrier = "";

    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public PalletLabelMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public boolean validateInput() {
       
        if (! BlueSeerUtils.isNumeric(tbqty.getText())) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            tbqty.setBackground(Color.yellow);
            return false;
        } else {
            tbqty.setBackground(Color.white);
        }
        
        if (tbordnbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbordnbr.requestFocus();
            return false;
        }
        if (tbline.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1024));
            tbline.requestFocus();
            return false;
        }
        
        return true;
    }
    
    
    public void getSiteAddress(String site) {
        try {

            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                                
                res = st.executeQuery("select * from site_mstr where site_site = " + "'" + site + "'" +";");
                while (res.next()) {
                    i++;
                   sitename = res.getString("site_desc").replace("'", "");
                   siteaddr = res.getString("site_line1").replace("'", "");
                   sitephone = res.getString("site_phone");
                   sitecitystatezip = res.getString("site_city") + ", " + res.getString("site_state") + " " + res.getString("site_zip");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1141,site));

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void getOrderInfo(String order, String line) {
         try {

            Connection con = null;
        if (ds != null) {
          con = ds.getConnection();
        } else {
          con = DriverManager.getConnection(url + db, user, pass);  
        }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                                
                res = st.executeQuery("select sod_nbr, sod_line, sod_item, sod_desc, sod_custitem, sod_ord_qty, so_cust, sod_po, so_shipvia, so_ship from sod_det " 
                        + " inner join so_mstr on so_nbr = sod_nbr " 
                        + " where sod_nbr = " + "'" + order + "'"
                        + " and sod_line = " + "'" + line + "'" 
                        + ";");
                while (res.next()) {
                    i++;
                   item = res.getString("sod_item");
                   partdesc = res.getString("sod_desc");
                   custitem = res.getString("sod_custitem");
                   billto = res.getString("so_cust");
                   shipto = res.getString("so_ship");
                   ponbr = res.getString("sod_po");
                   ordernbr = res.getString("sod_nbr");
                   linenbr = res.getString("sod_line");
                   quantity = res.getString("sod_ord_qty");
                   revnbr = "";
                   carrier = res.getString("so_shipvia");
                   
                   if (custitem.isEmpty()) {
                      custitem = item; 
                   }
                   
                }
               
                
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1143, order + "/" + line));
                
                
                // get shipto addr info
                if (! shipto.isEmpty() && ! billto.isEmpty()) {
                    res = st.executeQuery("select * from cms_det where cms_shipto = " + "'" + shipto + "'" 
                            + " AND cms_code = " + "'" + billto + "'" + ";");
                }
                 while (res.next()) {
                 shipname = res.getString("cms_name").replace("'", "");
                 shipaddr1 = res.getString("cms_line1").replace("'", "");
                 shipaddr2 = res.getString("cms_line2").replace("'", "");
                 shipcity = res.getString("cms_city").replace("'", "");
                 shipstate = res.getString("cms_state").replace("'", "");
                 shipzip = res.getString("cms_zip").replace("'", "");
                 shipcountry = res.getString("cms_country").replace("'", "");
                 shipcsz = shipcity + ", " + shipstate + " " + shipzip;
                 }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
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
    
    public void initvars(String[] arg) {
        tbqty.setText("");
        tbordnbr.setText("");
        tbline.setText("");
        tbqty.setText("");
        lblitem.setText("");
        btprint.setEnabled(true);
        
        getSiteAddress(OVData.getDefaultSite());
        
      
        
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
                tbline.setText(target.getValueAt(row,1).toString());
                tbqty.setText(BlueSeerUtils.bsNumber(target.getValueAt(row,4).toString()));
                lblitem.setText(target.getValueAt(row,2).toString() + " " + target.getValueAt(row,3).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        
        callDialog(getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"));
        
        
    }

    
    public label_mstr createRecord() { 
        java.util.Date now = new java.util.Date();
        
        getOrderInfo(tbordnbr.getText(), tbline.getText());
        
         label_mstr x = new label_mstr(null, 
                 serialno_str, 
                 item, 
                 custitem, 
                 serialno_display, 
                 "XX", 
                 labelname,
                 tbqty.getText(), 
                 ponbr,
                 billto,
                 ordernbr, 
                 linenbr, 
                 tbref.getText(), 
                 lot, 
                 "0", 
                 "0", 
                 shipname, 
                 shipaddr1, 
                 shipaddr2, 
                 shipcity, 
                 shipstate, 
                 shipstate, 
                 shipzip, 
                 shipcountry, 
                 setDateFormat(now), 
                 setDateFormat(now), 
                 bsmf.MainFrame.userid, 
                 "pdf", 
                 "PalletLabelMaint", 
                 OVData.getDefaultSite(), 
                 "", 
                 "CONT",
                 "flat",
                 shipto,
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
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblstatus = new javax.swing.JLabel();
        tbordnbr = new javax.swing.JTextField();
        tbline = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btlookupOrderLine = new javax.swing.JButton();
        btlookupLine = new javax.swing.JButton();
        lblitem = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Pallet label 8 x 10.5 (Jasper / PDF)"));
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

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });
        tbqty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbqtyActionPerformed(evt);
            }
        });

        jLabel4.setText("Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        jLabel6.setText("Order Line");
        jLabel6.setName("lblorderline"); // NOI18N

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

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
                .addGap(63, 63, 63)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
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
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btlookupLine, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btprint)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

        
        if (! validateInput()) {
            return;
        }
       
        
        try {

            String cust = cusData.getCustFromOrder(tbordnbr.getText());
            String label = cusData.getCustLabel(cust);
            label  = (label.isBlank()) ? "sscc18J" : label; 
            
            label_zebra lz = getLabelZebraMstr(new String[]{label});

            labelname = label;

            serialno = OVData.getNextNbr("label");
            serialno_str = String.valueOf(serialno);
            if (label.startsWith("sscc18")) {
             serialno_display = checkDigitUCC18(serialno);
            } else {
             serialno_display = serialno_str;   
            }
            
             // ok....apparently we have a label/printer match.... lets create the label_mstr record for this label
            addLabelMstr(createRecord()); 

            if (lz.lblz_file().endsWith("jasper")) {
                printJasperLabel(tbordnbr.getText(), tbline.getText(), serialno_display, tbref.getText(), tbqty.getText(), lz.lblz_file());
            } else {
                bsmf.MainFrame.show(getMessageTag(1206));  
            }

        } catch (Exception e) {
        MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btprintActionPerformed

    private void btlookupOrderLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupOrderLineActionPerformed
        lookUpFrameOrderLine();
    }//GEN-LAST:event_btlookupOrderLineActionPerformed

    private void btlookupLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupLineActionPerformed
        lookUpFrameLine();
    }//GEN-LAST:event_btlookupLineActionPerformed

    private void tbqtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbqtyActionPerformed
                
    }//GEN-LAST:event_tbqtyActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btlookupLine;
    private javax.swing.JButton btlookupOrderLine;
    private javax.swing.JButton btprint;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblitem;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
