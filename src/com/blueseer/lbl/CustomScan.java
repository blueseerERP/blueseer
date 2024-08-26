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
import com.blueseer.sch.schData;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
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
public class CustomScan extends javax.swing.JPanel {

String partnumber = "";
String custpart = "";
int serialno = 0;
String serialno_str = "";
String quantity = "";

String sitename = "";
String siteaddr = "";
String sitephone = "";
String sitecitystatezip = "";
    
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public CustomScan() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void validateScan(String scan) {
      if (schData.isPlan(scan)) {
       tbqty.setText(String.valueOf(schData.getPlanSchedQty(scan)));
       partlabel.setText(schData.getPlanItem(scan));
       partlabel.setForeground(Color.blue);
       tbqty.requestFocusInWindow();
       btcommit.setEnabled(true);
      } else {
              btcommit.setEnabled(false);
              tbcutticket.setText("");
              partlabel.setText(getMessageTag(1070,""));
              partlabel.setForeground(Color.red);
       
         tbcutticket.requestFocusInWindow();
            return;
      }
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
                   sitename = res.getString("site_desc");
                   siteaddr = res.getString("site_line1");
                   sitephone = res.getString("site_phone");
                   sitecitystatezip = res.getString("site_city") + ", " + res.getString("site_state") + " " + res.getString("site_zip");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1143,site));

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
    
    public void getPartInfo(String part) {
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
                                
                res = st.executeQuery("select * from item_mstr where it_item = " + "'" + part + "'"
                        + ";");
                while (res.next()) {
                    i++;
                   partnumber = res.getString("it_item");
                  
                }
               
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1143,part));

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
        tbscan.setText("");
       partlabel.setText("");
        
        btprint.setEnabled(true);
        
        ddprinter.removeAllItems();
        
        ArrayList mylist = OVData.getPrinterList();
        for (int i = 0; i < mylist.size(); i++) {
            ddprinter.addItem(mylist.get(i));
        }
        
        getSiteAddress(OVData.getDefaultSite());
        
         if (ddprinter.getItemCount() == 0) {
            bsmf.MainFrame.show(getMessageTag(1139));
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
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        lblstatus = new javax.swing.JLabel();
        tbcutticket = new javax.swing.JTextField();
        partlabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Scan Label Print"));
        jPanel1.setName("panelmain"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel3.setText("Ticket:");
        jLabel3.setName("lblticket"); // NOI18N

        jLabel2.setText("Printer:");
        jLabel2.setName("lblprinter"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel4.setText("Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        tbscan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbscanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbscanFocusLost(evt);
            }
        });

        jLabel5.setText("Scan:");
        jLabel5.setName("lblscan"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        lblstatus.setBackground(java.awt.Color.white);
        lblstatus.setForeground(java.awt.Color.red);

        tbcutticket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcutticketActionPerformed(evt);
            }
        });
        tbcutticket.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbcutticketFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbcutticketFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btprint, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btcommit, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddprinter, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbscan)
                            .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbcutticket)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 108, Short.MAX_VALUE))
                            .addComponent(partlabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tbcutticket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddprinter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btprint)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed

        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat dfdate2 = new SimpleDateFormat("yyyy-MM-dd");
        
        getPartInfo(partlabel.getText());
        
        serialno = OVData.getNextNbr("label");
        serialno_str = String.valueOf(serialno);
        
        quantity = tbqty.getText();
        
        Pattern p = Pattern.compile("^[1-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1026));
           return;
        }
        
        if (ddprinter.getSelectedItem() == null) {
            bsmf.MainFrame.show(getMessageTag(1140));
            return;
        }
        
        // lets create the label_mstr record for this label
        lblData.CreateLabelMstr(serialno_str, partnumber, "", serialno_str, "XX", quantity, "", "", "0", "", "", "0", "", "", "", "", "", "", "", "", "", dfdate2.format(now), dfdate2.format(now), bsmf.MainFrame.userid, ddprinter.getSelectedItem().toString(), "LabelPartPanel", OVData.getDefaultSite(), "", "TFR-SCAN");
       
        
        try {

            
  //          FileOutputStream fos = new FileOutputStream("10.17.4.99");
 //           PrintStream ps = new PrintStream(fos);

BufferedReader fsr = new BufferedReader(new FileReader(new File("zebra/item.prn")));
String line = "";
String concatline = "";

while ((line = fsr.readLine()) != null) {
    concatline += line;
}
fsr.close();
// fos.write(concatline.getBytes());


concatline = concatline.replace("$PART", partnumber);
concatline = concatline.replace("$SERIALNO", serialno_str);
concatline = concatline.replace("$QUANTITY", quantity);


//unused
concatline = concatline.replace("$DESCRIPTION", "");
concatline = concatline.replace("$CUSTCODE", "");
concatline = concatline.replace("$PART", "");
concatline = concatline.replace("$ADDRNAME", "");
concatline = concatline.replace("$REV", "");
concatline = concatline.replace("$PONUMBER", "");
concatline = concatline.replace("$SONBR", "");
concatline = concatline.replace("$SOLINE", "");


concatline = concatline.replace("$SITENAME", sitename);
concatline = concatline.replace("$SITEADDR", siteaddr);
concatline = concatline.replace("$SITEPHONE", sitephone);
concatline = concatline.replace("$SITECSZ", sitecitystatezip);

concatline = concatline.replace("$TODAYDATE", dfdate.format(now));


 OVData.printLabelStream(concatline, ddprinter.getSelectedItem().toString());

 
 initvars(null);

} catch (Exception e) {
MainFrame.bslog(e);
}
    }//GEN-LAST:event_btprintActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       
        if (! lblData.isLabel(tbscan.getText())) {
            bsmf.MainFrame.show(getMessageTag(1070,tbscan.getText()));
            return;
        }
        if (lblData.isLabel(tbscan.getText()) &&  lblData.getLabelStatus(tbscan.getText()) == 1 ) {
            bsmf.MainFrame.show(getMessageTag(1071,tbscan.getText()));
            return;
        }
        if (lblData.isLabel(tbscan.getText()) &&  lblData.getLabelStatus(tbscan.getText()) > 1 ) {
            bsmf.MainFrame.show(getMessageTag(1144));
            return;
        }
        
     
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
       tbqty.setBackground(Color.yellow);
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        tbqty.setBackground(Color.white);
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbscanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusGained
       tbscan.setBackground(Color.yellow);
    }//GEN-LAST:event_tbscanFocusGained

    private void tbscanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusLost
        tbscan.setBackground(Color.white);
    }//GEN-LAST:event_tbscanFocusLost

    private void tbcutticketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcutticketActionPerformed
       tbqty.requestFocusInWindow();
    }//GEN-LAST:event_tbcutticketActionPerformed

    private void tbcutticketFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcutticketFocusGained
      tbcutticket.setBackground(Color.yellow);
    }//GEN-LAST:event_tbcutticketFocusGained

    private void tbcutticketFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbcutticketFocusLost
         tbcutticket.setBackground(Color.white);
        validateScan(tbcutticket.getText());
    }//GEN-LAST:event_tbcutticketFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btprint;
    private javax.swing.JComboBox ddprinter;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblstatus;
    private javax.swing.JLabel partlabel;
    private javax.swing.JTextField tbcutticket;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
