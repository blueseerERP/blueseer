/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.lbl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */
public class LabelAddrPanel extends javax.swing.JPanel {

String shipname = "";
String shipline1 = "";
String shipline2 = "";
String shipcity = "";
String shipstate = "";
String shipzip = "";

String sitename = "";
String siteaddr = "";
String sitephone = "";
String sitecitystatezip = "";
    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public LabelAddrPanel() {
        initComponents();
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
                   sitename = res.getString("site_desc");
                   siteaddr = res.getString("site_line1");
                   sitephone = res.getString("site_phone");
                   sitecitystatezip = res.getString("site_city") + ", " + res.getString("site_state") + " " + res.getString("site_zip");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Address Record found for site " + site );

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve site_mstr");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void getShiptoInfo(String billto, String shipto) {
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                                
                res = st.executeQuery("select * from cms_det where cms_code = " + "'" + billto + "'" +
                        " AND cms_shipto = " + "'" + shipto + "'" + ";");
                while (res.next()) {
                    i++;
                   shipname = res.getString("cms_name");
                   shipline1 = res.getString("cms_line1");
                   shipline2 = res.getString("cms_line2");
                   shipcity = res.getString("cms_city");
                   shipstate = res.getString("cms_state");
                   shipzip = res.getString("cms_zip");
                   lbladdr.setText(res.getString("cms_name") + "  " + res.getString("cms_line1") + "..." + res.getString("cms_city") +
                                    ", " + res.getString("cms_state") + " " + res.getString("cms_zip"));
                }
               
                if (i == 0)
                    bsmf.MainFrame.show("No Address Record found for billto/shipto " + billto + "/" + shipto );

            } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve cms_det");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
      public void initvars(String arg) {
        ddshipto.removeAllItems();
        ddbillto.removeAllItems();
        ArrayList mycusts = OVData.getcustmstrlist();
        for (int i = 0; i < mycusts.size(); i++) {
            ddbillto.addItem(mycusts.get(i));
        }
        
        btprint.setEnabled(true);
        lbladdr.setText("");
        
        ddprinter.removeAllItems();
        ArrayList mylist = OVData.getZebraPrinterList();
        for (int i = 0; i < mylist.size(); i++) {
            ddprinter.addItem(mylist.get(i));
        }
        
        getSiteAddress(OVData.getDefaultSite());
        
         if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show("No Zebra Printers Available");
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
        jLabel1 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        ddprinter = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        ddbillto = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        ddshipto = new javax.swing.JComboBox();
        lbladdr = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Address Label Print"));

        jLabel1.setText("Shipto Code:");

        btprint.setText("Print");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel3.setText("Billto Code:");

        jLabel2.setText("Printer:");

        jButton1.setText("Get ShipTo List");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddshipto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btprint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddbillto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ddshipto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbladdr, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

        if (ddprinter.getSelectedItem() == null) {
            bsmf.MainFrame.show("No Selected Zebra Printer");
            return;
        }
        
        try {

Socket soc = null;
DataOutputStream dos = null;
String ZPLPrinterIPAddress = OVData.getPrinterIP(ddprinter.getSelectedItem().toString());
int ZPLPrinterPort = 9100;
            
            
  //          FileOutputStream fos = new FileOutputStream("10.17.4.99");
 //           PrintStream ps = new PrintStream(fos);

BufferedReader fsr = new BufferedReader(new FileReader(new File("zebra/address.prn")));
String line = "";
String concatline = "";

while ((line = fsr.readLine()) != null) {
    concatline += line;
}
fsr.close();
// fos.write(concatline.getBytes());

java.util.Date now = new java.util.Date();
DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");

concatline = concatline.replace("$ADDRNAME", shipname);
concatline = concatline.replace("$ADDRLINE1", shipline1);
concatline = concatline.replace("$ADDRLINE2", shipline2);
concatline = concatline.replace("$ADDRCITY", shipcity);
concatline = concatline.replace("$ADDRSTATE", shipstate);
concatline = concatline.replace("$ADDRZIP", shipzip);

concatline = concatline.replace("$SITENAME", sitename);
concatline = concatline.replace("$SITEADDR", siteaddr);
concatline = concatline.replace("$SITEPHONE", sitephone);
concatline = concatline.replace("$SITECSZ", sitecitystatezip);

concatline = concatline.replace("$TODAYDATE", dfdate.format(now));


 String commands = "^XA~TA000~JSN^LT0^MMT^MCY^MNW^MTT^PON^PMN^LH0,0^JMA^PR4,4^MD0^JUS^LRN^CI0^XZ" +
"^XA^LL1218" +
"^BY2,3,106^FT406,949^B3B,N,,N,N" +
"^FD661400C01000^FS" +
"^FT585,970^A0B,65,60^FH\\^FDPC3332^FS" +
"^FT168,1198^A0B,133,122^FH\\^FD661400C01000^FS" +
"^FT578,1094^A0B,28,28^FH\\^FDPART:^FS" +
"^PQ1,0,1,Y^XZ";


 soc = new Socket(ZPLPrinterIPAddress, ZPLPrinterPort);
        dos= new DataOutputStream(soc.getOutputStream());
        dos.writeBytes(concatline);

 dos.close();
 soc.close();
 

 //ps.println(commands);
 //                   ps.print("\f");
 //                   ps.close();


/*
try {
        
        Process p = Runtime.getRuntime().exec("sleep 5");
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
*/


//printer.println(concatline);
//printer.flush();

// close and free the device
// printer.close();
//fos.close();
} catch (Exception e) {
MainFrame.bslog(e);
}
    }//GEN-LAST:event_btprintActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         ddshipto.removeAllItems();
        ArrayList mycusts = OVData.getcustshipmstrlist(ddbillto.getSelectedItem().toString());
        for (int i = 0; i < mycusts.size(); i++) {
            ddshipto.addItem(mycusts.get(i));
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ddshiptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddshiptoActionPerformed
       if (ddshipto.getSelectedItem() != null)
        getShiptoInfo(ddbillto.getSelectedItem().toString(), ddshipto.getSelectedItem().toString());
    }//GEN-LAST:event_ddshiptoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btprint;
    private javax.swing.JComboBox ddbillto;
    private javax.swing.JComboBox ddprinter;
    private javax.swing.JComboBox ddshipto;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbladdr;
    // End of variables declaration//GEN-END:variables
}
