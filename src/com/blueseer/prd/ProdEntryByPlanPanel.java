/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.JTable;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class ProdEntryByPlanPanel extends javax.swing.JPanel {

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
    public ProdEntryByPlanPanel() {
        initComponents();
    }

    
    public void printTubTicket(String scan, String subid) throws PrinterException {
        
          
        PrinterJob job = PrinterJob.getPrinterJob();
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        
        if ( service != null) {
         job.setPrintService(service);
         try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "MASTER TICKET");
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("myid",  scan);
                hm.put("subid",  subid);
              
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/subticket.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint print = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
  
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
    
    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("unable to print prodentrybyplan");
            }
            bsmf.MainFrame.con.close();
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
       partlabel.setText(OVData.getPlanPart(scan));
       partlabel.setForeground(Color.blue);
       ddop.removeAllItems();
       ArrayList mylist = OVData.getItemRoutingOPs(partlabel.getText());
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
    
      public void initvars(String arg) {
        
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

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel4.setText("Quantity");

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

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel7.setText("Operation");

        tbref.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbrefFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrefFocusLost(evt);
            }
        });

        jLabel8.setText("Reference");

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
            lblmessage.setText("Bad Ticket: " + tbscan.getText());
            lblmessage.setForeground(Color.red);
            bsmf.MainFrame.show("Bad Ticket: " + tbscan.getText());
            initvars("");
            return;
        }
        
        if (OVData.isPlan(tbscan.getText()) &&  OVData.getPlanStatus(tbscan.getText()) > 0 ) {
            lblmessage.setText("Ticket CLosed: " + tbscan.getText());
            lblmessage.setForeground(Color.red);
            initvars("");
            return;
        }
        if (OVData.isPlan(tbscan.getText()) &&  OVData.getPlanStatus(tbscan.getText()) < 0 ) {
            lblmessage.setText("Ticket Voided: " + tbscan.getText());
            lblmessage.setForeground(Color.red);
            initvars("");
            return;
        }
        
         // check inventory control flag... "Plan Multiple Scan Issues"
        // if false...only one scan per plan ticket per operation
        if (! OVData.isInvCtrlPlanMultiScan() && OVData.isPlanDetByOp(tbscan.getText(), ddop.getSelectedItem().toString())) {
            lblmessage.setText("Ticket Already Reported for this Operation " + tbscan.getText() + " / " + ddop.getSelectedItem().toString());
            lblmessage.setForeground(Color.red);
                initvars("");
                return;
        }
        
        
        // now lets sum up qtys posted previously (if any) for this OP and this Ticket and make sure
        // qty field is not greater than qty previous + qty scheduled
        // this should work for multiscan and nonmultican conditions
        int prevscanned = OVData.getPlanDetTotQtyByOp(tbscan.getText(), ddop.getSelectedItem().toString());
        int schedqty = OVData.getPlanSchedQty(tbscan.getText());
        if ( qty > (schedqty - prevscanned) ) {
             lblmessage.setText("Qty Exceeds limit (Already Scanned Qty: " + String.valueOf(prevscanned) + " out of SchedQty: " + String.valueOf(schedqty) + ")");
            lblmessage.setForeground(Color.red);
            initvars("");
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
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try{
                Statement st = con.createStatement();
                ResultSet res = null;
                 java.util.Date now = new java.util.Date();
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                 
                res = st.executeQuery("select * from plan_mstr where plan_nbr = " + "'" + tbscan.getText() + "'" + ";" );
                    while (res.next()) {
                  String prodline = OVData.getProdLineFromItem(res.getString("plan_part")); 
                  String loc = OVData.getLocationByPart(res.getString("plan_part")); 
                  String wh = OVData.getWarehouseByPart(res.getString("plan_part"));
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
                "ProdEntryByPlan", // program
                wh
            });
                    }
           }
            catch (SQLException s){
                MainFrame.bslog(s);
                bsmf.MainFrame.show("problem with sql commit");
                 
            }
            con.close();
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
                        Logger.getLogger(ProdEntryByPlanPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
              initvars("");
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
