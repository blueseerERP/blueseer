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
package com.blueseer.prd;


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JTable;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.invData;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class ProdEntryByPlanMaint extends javax.swing.JPanel {

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
    public ProdEntryByPlanMaint() {
        initComponents();
        setLanguageTags(this);
    }

    
    public void printTubTicket(String scan, String subid) throws PrinterException {
        
          
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        
        if ( service != null) {
         job.setPrintService(service);
         try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
           

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "MASTER TICKET");
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("myid",  scan);
                hm.put("subid",  subid);
                File mytemplate = new File("jasper/subticket.jasper");
                JasperPrint print = JasperFillManager.fillReport(mytemplate.getPath(), hm, con );
                con.close();
  
               // code auto sends to printer
//PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//    JRPrintServiceExporter exporter;
//    exporter = new JRPrintServiceExporter();
   
 //   exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
 //         exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, service);
 //   exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, service.getAttributes());
 //   exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
 //   exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
 //   exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
 //   exporter.exportReport();  
         // end code auto sends to printer
     
                JasperExportManager.exportReportToPdfFile(print,"temp/subjob.pdf");
                JasperViewer jasperViewer = new JasperViewer(print, false);
                jasperViewer.setVisible(true);
    
    
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        }
      
   
    /* We set the selected service and pass it as a paramenter */
 
    }
    
    
    public void validateScan(String scan) {
       
        if (scan.isEmpty()) {
            partlabel.setText("");
            return;
        }
        
        if (OVData.isPlan(scan)) {
       tbqty.setText(String.valueOf(OVData.getPlanSchedQty(scan)));
       partlabel.setText(OVData.getPlanItem(scan));
       partlabel.setForeground(Color.blue);
       ddop.removeAllItems();
       ArrayList mylist = invData.getItemRoutingOPs(partlabel.getText());
       for (int i = 0; i < mylist.size(); i++) {
           ddop.addItem(mylist.get(i));
       }
       if (ddop.getItemCount() <= 0) {
           ddop.addItem("0");
           partlabel.setText(partlabel.getText() + " " + "No Operations..." + "\n" + "make sure item has routing AND standard cost");
           partlabel.setForeground(Color.yellow);
       }
       
       tbqty.requestFocusInWindow();
       btcommit.setEnabled(true);
      } else {
              btcommit.setEnabled(false);
              tbscan.setText("");
              partlabel.setText("Bad Ticket");
              partlabel.setForeground(Color.red);
        // bsmf.MainFrame.show("Bad Ticket: " + scan);
         tbscan.requestFocusInWindow();
            return;
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
        partlabel.setForeground(Color.black);
        ddop.removeAllItems();
        tbref.setText("");
      
        
        
        btcommit.setEnabled(false);
        
       tbscan.requestFocusInWindow();
        
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
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        ddop = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        partlabel = new javax.swing.JLabel();
        lblmessage = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Prod Entry By Plan Ticket"));
        jPanel1.setName("panelmain"); // NOI18N

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
        tbscan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbscanActionPerformed(evt);
            }
        });

        jLabel5.setText("Scan");
        jLabel5.setName("lblscan"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel7.setText("Operation");
        jLabel7.setName("lblop"); // NOI18N

        tbref.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbrefFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrefFocusLost(evt);
            }
        });

        jLabel8.setText("Reference");
        jLabel8.setName("lblreference"); // NOI18N

        partlabel.setForeground(new java.awt.Color(25, 102, 232));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btcommit)
                                .addGap(0, 276, Short.MAX_VALUE))
                            .addComponent(tbref)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       
        
        int qty = 0;
        
        Pattern p = Pattern.compile("^[0-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            lblmessage.setText("Invalid Qty");
            lblmessage.setForeground(Color.red);
            return;
        } else {
            qty = Integer.valueOf(tbqty.getText());
        }
        
        if (! OVData.isPlan(tbscan.getText())) {
            lblmessage.setText(getMessageTag(1070,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
        if (OVData.isPlan(tbscan.getText()) &&  OVData.getPlanStatus(tbscan.getText()) > 0 ) {
            lblmessage.setText(getMessageTag(1071,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        if (OVData.isPlan(tbscan.getText()) &&  OVData.getPlanStatus(tbscan.getText()) < 0 ) {
            lblmessage.setText(getMessageTag(1072,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
         // check inventory control flag... "Plan Multiple Scan Issues"
        // if false...only one scan per plan ticket per operation
        if (! OVData.isInvCtrlPlanMultiScan() && OVData.isPlanDetByOp(tbscan.getText(), ddop.getSelectedItem().toString())) {
            lblmessage.setText("Ticket Already Reported for this Operation " + tbscan.getText() + " / " + ddop.getSelectedItem().toString());
            lblmessage.setForeground(Color.red);
                initvars(null);
                return;
        }
        
        
        // now lets sum up qtys posted previously (if any) for this OP and this Ticket and make sure
        // qty field is not greater than qty previous + qty scheduled
        // this should work for multiscan and nonmultican conditions
        double prevscanned = OVData.getPlanDetTotQtyByOp(tbscan.getText(), ddop.getSelectedItem().toString());
        double schedqty = OVData.getPlanSchedQty(tbscan.getText());
        if ( qty > (schedqty - prevscanned) ) {
             lblmessage.setText("Qty Exceeds limit (Already Scanned Qty: " + String.valueOf(prevscanned) + " out of SchedQty: " + String.valueOf(schedqty) + ")");
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
        
       
        
        if (OVData.isPlan(tbscan.getText()) &&  OVData.getPlanStatus(tbscan.getText()) == 0 ) {
            
            //OK ...if here..we should be prepared to commit.... Let's commit the transaction with OVData.loadTranHistByTable
            JTable mytable = new JTable();
            javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "Type", "Operation", "Qty", "Date", "Location", "SerialNo", "Reference", "Site", "Userid", "ProdLine", "AssyCell", "Rmks", "PackCell", "PackDate", "AssyDate", "Program", "Warehouse"
            });
            
            // get necessary info from plan_mstr for this scan and store into mytable(mymodel)
        try{
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                 java.util.Date now = new java.util.Date();
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                 
                res = st.executeQuery("select * from plan_mstr where plan_nbr = " + "'" + tbscan.getText() + "'" + ";" );
                    while (res.next()) {
                  String[] detail = invData.getItemDetail(res.getString("plan_part"));
                  String prodline = detail[3];
                  String loc = detail[8];
                  String wh = detail[9];
                  String expire = detail[10];
                 mymodel.addRow(new Object[]{res.getString("plan_part"),
                "ISS-WIP",
                ddop.getSelectedItem().toString(),
                tbqty.getText(),
                dfdate.format(now),
                loc, // location
                tbscan.getText(),  // serialno  ...using JOBID from tubtraveler
                res.getString("plan_type"),  // reference -- tr_ref holds the scrap code
                res.getString("plan_site"),
                bsmf.MainFrame.userid,
                prodline,
                "",   //  tr_actcell
                tbref.getText().replace(",", ""),   // remarks 
                "", // pack station
                "", // pack date
                "", // assembly date
                expire,
                "ProdEntryByPlan", // program
                wh
            });
                    }
           }
            catch (SQLException s){
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
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        
            mytable.setModel(mymodel);
            
            
            // OK...we should have a JTable with the necessary info to create the tran_mstr table
            
             if (! OVData.loadTranHistByTable(mytable)) {
            lblmessage.setText("Unable to scan ticket");
            lblmessage.setForeground(Color.red);
            return;
            } else {
                 //must have successfully enter tran_mstr...now lets create pland_mstr...and update plan_mstr if closing
                 int key = OVData.CreatePlanDet(mytable);
                 lblmessage.setText("Scan Complete");
                 lblmessage.setForeground(Color.blue);
                if (OVData.isPrintTicketFromPlanScan()) {               
                     try {
                        printTubTicket(tbscan.getText(), String.valueOf(key));
                    } catch (PrinterException ex) {
                        Logger.getLogger(ProdEntryByPlanMaint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
              initvars(null);
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
        validateScan(tbscan.getText());
    }//GEN-LAST:event_tbscanFocusLost

    private void tbscanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbscanActionPerformed
        tbqty.requestFocusInWindow();
    }//GEN-LAST:event_tbscanActionPerformed

    private void tbrefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrefFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_tbrefFocusGained

    private void tbrefFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrefFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tbrefFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcommit;
    private javax.swing.JComboBox ddop;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JLabel partlabel;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
