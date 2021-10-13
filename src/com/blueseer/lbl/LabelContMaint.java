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
import com.blueseer.ctr.cusData;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

/**
 *
 * @author vaughnte
 */
public class LabelContMaint extends javax.swing.JPanel {

String partnumber = "";
String revnbr = "";
String custpart = "";
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
String quantity = "";

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

    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public LabelContMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void getSiteAddress(String site) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
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
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void getOrderInfo(String order, String line) {
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                                
                res = st.executeQuery("select sod_nbr, sod_line, sod_part, sod_custpart, so_cust, sod_po, so_ship, it_item, it_desc, it_rev from sod_det " 
                        + " inner join so_mstr on so_nbr = sod_nbr " 
                        + " inner join item_mstr on it_item = sod_part "
                        + " where sod_nbr = " + "'" + order + "'"
                        + " and sod_line = " + "'" + line + "'" 
                        + ";");
                while (res.next()) {
                    i++;
                   partnumber = res.getString("it_item");
                   partdesc = res.getString("it_desc");
                   custpart = res.getString("sod_custpart");
                   billto = res.getString("so_cust");
                   shipto = res.getString("so_ship");
                   ponbr = res.getString("sod_po");
                   ordernbr = res.getString("sod_nbr");
                   linenbr = res.getString("sod_line");
                   revnbr = res.getString("it_rev");
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
                 }

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            bsmf.MainFrame.con.close();
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
        tblblqty.setText("");
        
        btprint.setEnabled(true);
        
        ddprinter.removeAllItems();
        
        ArrayList mylist = OVData.getPrinterList();
        for (int i = 0; i < mylist.size(); i++) {
            ddprinter.addItem(mylist.get(i));
        }
      
        
        getSiteAddress(OVData.getDefaultSite());
        
         if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show("No Printers Available");
            btprint.setEnabled(false);
        }
        
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

        jLabel4.setText("Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        jLabel6.setText("Order Line");
        jLabel6.setName("lblorderline"); // NOI18N

        jLabel7.setText("Number of Labels");
        jLabel7.setName("lblnumber"); // NOI18N

        jLabel8.setText("Reference");
        jLabel8.setName("lblref"); // NOI18N

        jLabel9.setText("Lot");
        jLabel9.setName("lbllot"); // NOI18N

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
                .addGap(18, 18, 18)
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
                            .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tblblqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tbordnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
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

        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dftime = new SimpleDateFormat("hh:mm");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
        
        getOrderInfo(tbordnbr.getText(), tbline.getText());
        
        serialno = OVData.getNextNbr("label");
        serialno_str = String.valueOf(serialno);
        
        quantity = tbqty.getText();
        
        Pattern p = Pattern.compile("^[1-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tbqty.requestFocus();
           return;
        }
        
        
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
        
        p = Pattern.compile("^[1-9]\\d*$");
        m = p.matcher(tblblqty.getText());
        if (!m.find() || tblblqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1026));
            tblblqty.requestFocus();
           return;
        }
        
        if (ddprinter.getSelectedItem() == null) {
            bsmf.MainFrame.show(getMessageTag(1140));
            return;
        }
        
        // ref and lot could be assigned earlier in getOrderInfo...if blank take input
        if (tbref.getText().isEmpty()) {
        ref = tbref.getText();
        }
        if (tblot.getText().isEmpty()) {
        lot = tblot.getText();
        }
        
       
        int nbroflabels = Integer.valueOf(tblblqty.getText());
        
        
      
        
        try {

            
  //          FileOutputStream fos = new FileOutputStream("10.17.4.99");
 //           PrintStream ps = new PrintStream(fos);

String cust = cusData.getCustFromOrder(tbordnbr.getText());
String label = cusData.getCustLabel(cust);
label = label + ".prn";
String labelfile = "zebra/" + label;
File f = new File(labelfile);
if(f.exists() && !f.isDirectory()) { 
    
      // ok....apparently we have a label/printer match.... lets create the label_mstr record for this label
        OVData.CreateLabelMstr(serialno_str, partnumber, custpart, serialno_str, "XX", quantity, ponbr, ordernbr, linenbr, ref, lot, "0", "0", shipname, shipaddr1, shipaddr2, shipcity, shipstate, shipstate, shipzip, shipcountry, dfdate2.format(now), dfdate2.format(now), bsmf.MainFrame.userid, ddprinter.getSelectedItem().toString(), "LabelContPanel", OVData.getDefaultSite(), "", "CONT");
       
    
    
BufferedReader fsr = new BufferedReader(new FileReader(f));
String line = "";
String concatline = "";

while ((line = fsr.readLine()) != null) {
    concatline += line;
}
fsr.close();
// fos.write(concatline.getBytes());


concatline = concatline.replace("$PART", partnumber);
concatline = concatline.replace("$CUSTPART", custpart);
concatline = concatline.replace("$SERIALNO", serialno_str);
concatline = concatline.replace("$QUANTITY", quantity);



concatline = concatline.replace("$DESCRIPTION", partdesc);
concatline = concatline.replace("$CUSTCODE", billto);
concatline = concatline.replace("$PART", "");
concatline = concatline.replace("$ADDRNAME", "");
concatline = concatline.replace("$REV", revnbr);
concatline = concatline.replace("$PONUMBER", ponbr);
concatline = concatline.replace("$SONBR", ordernbr);
concatline = concatline.replace("$SOLINE", linenbr);


concatline = concatline.replace("$SITENAME", sitename);
concatline = concatline.replace("$SITEADDR", siteaddr);
concatline = concatline.replace("$SITEPHONE", sitephone);
concatline = concatline.replace("$SITECSZ", sitecitystatezip);

concatline = concatline.replace("$TODAYDATE", dfdate.format(now));
concatline = concatline.replace("$TODAYTIME", dftime.format(now));

 OVData.printLabelStream(concatline, ddprinter.getSelectedItem().toString());

 
 initvars(null);
} else {
    bsmf.MainFrame.show(getMessageTag(1142,labelfile));
}


} catch (Exception e) {
MainFrame.bslog(e);
}
    }//GEN-LAST:event_btprintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel lblstatus;
    private javax.swing.JTextField tblblqty;
    private javax.swing.JTextField tbline;
    private javax.swing.JTextField tblot;
    private javax.swing.JTextField tbordnbr;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    // End of variables declaration//GEN-END:variables
}
