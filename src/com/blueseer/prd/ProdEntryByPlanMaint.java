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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JTable;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.invData;
import com.blueseer.inv.invData.inv_ctrl;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.getPlanDetHistory;
import com.blueseer.sch.schData.plan_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import java.awt.Component;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
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

javax.swing.table.DefaultTableModel historymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "jobid", "operation", "qty", "date", "userid"
            });
    
    
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
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
           

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
    
    public void getScanHistory(String scan) {
       historymodel.setRowCount(0);
       ArrayList<String[]> hist = getPlanDetHistory(scan);
       for (String[] h : hist) {
            historymodel.addRow(new Object[]{
                      h[1], // jobid
                      h[3], // op
                      h[7], // qty
                      h[5], // date
                      h[8] // userid
                  });
       }
    }
    
    public void validateScan(String scan) {
       
        
        
        lblmessage.setText("");
        if (scan.isEmpty()) {
            qtylabel.setText("");
            partlabel.setText("");
            return;
        }
        
        if (schData.isPlan(scan)) {
      // tbqty.setText(String.valueOf(schData.getPlanSchedQty(scan)));
       partlabel.setText(schData.getPlanItem(scan));
       partlabel.setForeground(Color.blue);
       ddop.removeAllItems();
       ArrayList<String> mylist = OVData.getOperationsByItem(partlabel.getText());
     //  ArrayList<String> mylist = invData.getItemRoutingOPs(partlabel.getText());
       mylist.sort(Comparator.comparingInt(Integer::parseInt)); 
      // Collections.sort(mylist, Collections.reverseOrder());
    //   for (int i = 0; i < mylist.size(); i++) {
       for (int i = mylist.size() - 1; i >= 0; i--) {    
           ddop.addItem(mylist.get(i));
       }
       
       
       
       if (ddop.getItemCount() <= 0) {
           ddop.addItem("0");
           partlabel.setText(partlabel.getText() + " " + "No Operations..." + "\n" + "make sure item has routing AND standard cost");
           partlabel.setForeground(Color.yellow);
       } else {
          double prevscanned = schData.getPlanDetTotQtyByOp(tbscan.getText(), ddop.getSelectedItem().toString());
          double qtysched = schData.getPlanSchedQty(tbscan.getText());
          double remaining = qtysched - prevscanned;
          tbqty.setText(String.valueOf(remaining));
          qtylabel.setText("qty sched: " + String.valueOf(qtysched) + "     qty scanned: " + String.valueOf(prevscanned));
          qtylabel.setForeground(Color.blue);
          plan_mstr pm = schData.getPlanMstr(new String[]{tbscan.getText()});
          
          if (Integer.valueOf(pm.plan_status()) != 0 ) { // check if closed
            lblmessage.setText(getMessageTag(1071,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            btcommit.setEnabled(false);
            } else {
                btcommit.setEnabled(true);
          }
          
          if (qtysched == 0 ) { // check if scheduled
            lblmessage.setText(getMessageTag(1182,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            btcommit.setEnabled(false);
            } else {
                btcommit.setEnabled(true);
          }
          
       }
       
       // now get history of plan jobid (scan)
       getScanHistory(tbscan.getText());
       
       tbqty.requestFocusInWindow();
       
      } else {
              btcommit.setEnabled(false);
              tbscan.setText("");
              qtylabel.setText("Bad Ticket");
              qtylabel.setForeground(Color.red);
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
        qtylabel.setText("");
        qtylabel.setForeground(Color.black);
        ddop.removeAllItems();
        tbref.setText("");
      
       
       historytable.setModel(historymodel);
       historytable.getTableHeader().setReorderingAllowed(false);
       historymodel.setRowCount(0);
       
       
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
        qtylabel = new javax.swing.JLabel();
        lblmessage = new javax.swing.JLabel();
        partlabel = new javax.swing.JLabel();
        jpaneltable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        historytable = new javax.swing.JTable();

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

        qtylabel.setForeground(new java.awt.Color(25, 102, 232));

        partlabel.setForeground(new java.awt.Color(25, 102, 232));

        jpaneltable.setBorder(javax.swing.BorderFactory.createTitledBorder("JobID Scan History"));

        historytable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(historytable);

        javax.swing.GroupLayout jpaneltableLayout = new javax.swing.GroupLayout(jpaneltable);
        jpaneltable.setLayout(jpaneltableLayout);
        jpaneltableLayout.setHorizontalGroup(
            jpaneltableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpaneltableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jpaneltableLayout.setVerticalGroup(
            jpaneltableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpaneltableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpaneltable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4)
                                .addComponent(jLabel7))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(47, 47, 47)
                                    .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(qtylabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btcommit)
                                .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(161, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qtylabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpaneltable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       
        plan_mstr pm = schData.getPlanMstr(new String[]{tbscan.getText()});
        inv_ctrl ic = invData.getINVCtrl(new String[]{tbscan.getText()});
        double prevscanned = schData.getPlanDetTotQtyByOp(tbscan.getText(), ddop.getSelectedItem().toString());
        String[] detail = invData.getItemDetail(pm.plan_item());
        boolean isPlan = true;
        
        if (pm.plan_nbr().isBlank()) {
            isPlan = false;
        }
        double qty = Double.valueOf(tbqty.getText());
        
        if (! isPlan) {
            lblmessage.setText(getMessageTag(1070,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
        if (isPlan &&  Integer.valueOf(pm.plan_status()) > 0 ) {
            lblmessage.setText(getMessageTag(1071,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        if (isPlan &&  Integer.valueOf(pm.plan_status()) < 0 ) {
            lblmessage.setText(getMessageTag(1072,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
         // check inventory control flag... "Plan Multiple Scan Issues"
        // if false...only one scan per plan ticket per operation
        
        if (! BlueSeerUtils.ConvertStringToBool(ic.planmultiscan()) && (prevscanned > 0)) {
            lblmessage.setText("Ticket Already Reported for this Operation " + tbscan.getText() + " / " + ddop.getSelectedItem().toString());
            lblmessage.setForeground(Color.red);
                initvars(null);
                return;
        }
        
        
        // now lets sum up qtys posted previously (if any) for this OP and this Ticket and make sure
        // qty field is not greater than qty previous + qty scheduled
        // this should work for multiscan and nonmultican conditions
       // bsmf.MainFrame.show(pm.plan_qty_sched());
        double schedqty = pm.plan_qty_sched().isBlank() ? 0 : Double.valueOf(pm.plan_qty_sched());
        if ( qty > (schedqty - prevscanned) ) {
             lblmessage.setText("Qty Exceeds limit (Already Scanned Qty: " + String.valueOf(prevscanned) + " out of SchedQty: " + String.valueOf(schedqty) + ")");
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
        
        
        
        if (isPlan &&  Integer.valueOf(pm.plan_status()) == 0 ) {
            boolean isLastOp = OVData.isLastOperation(pm.plan_item(), ddop.getSelectedItem().toString() );
            //OK ...if here..we should be prepared to commit.... Let's commit the transaction with OVData.loadTranHistByTable
            
            JTable mytable = new JTable();
            javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Part", "Type", "Operation", "Qty", "Date", "Location", "SerialNo", "Reference", "Site", "Userid", "ProdLine", "AssyCell", "Rmks", "PackCell", "PackDate", "AssyDate", "ExpireDate", "Program", "Warehouse", "BOM"
            });
             // get necessary info from plan_mstr for this scan and store into mytable(mymodel)
             
              String prodline = detail[3];
              String loc = detail[8];
              String wh = detail[9];
              String expire = detail[10];
              java.util.Date now = new java.util.Date();
              DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
             mymodel.addRow(new Object[]{
                pm.plan_item(),
                "ISS-WIP",
                ddop.getSelectedItem().toString(),
                tbqty.getText(),
                dfdate.format(now),
                loc, // location
                tbscan.getText(),  // serialno  
                pm.plan_type(),  // reference 
                pm.plan_site(),
                bsmf.MainFrame.userid,
                prodline,
                "",   //  tr_actcell
                tbref.getText().replace(",", ""),   // remarks 
                "", // pack station
                "", // pack date
                "", // assembly date
                expire,
                "ProdEntryByPlan", // program
                wh,
                ""  // bom will grab default
            });
       
            mytable.setModel(mymodel);
            
            
            // OK...we should have a JTable with the necessary info to create the tran_mstr table
            
             if (! OVData.loadTranHistByTable(mytable)) {
            lblmessage.setText("Unable to scan ticket");
            lblmessage.setForeground(Color.red);
            return;
            } else {
                 //must have successfully enter tran_mstr...now lets create pland_mstr...and update plan_mstr if closing
                 int key = OVData.CreatePlanDet(mytable, isLastOp);
                 lblmessage.setText("Scan Complete");
                 lblmessage.setForeground(Color.blue);
                 getScanHistory(tbscan.getText());
                 
                if (BlueSeerUtils.ConvertStringToBool(ic.printsubticket())) {               
                     try {
                        printTubTicket(tbscan.getText(), String.valueOf(key));
                    } catch (PrinterException ex) {
                        MainFrame.bslog(ex);
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
         String x = BlueSeerUtils.bsformat("", tbqty.getText(), "2");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
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
    private javax.swing.JTable historytable;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpaneltable;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JLabel partlabel;
    private javax.swing.JLabel qtylabel;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
