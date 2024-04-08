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
import com.blueseer.hrm.hrmData.emp_mstr;
import static com.blueseer.hrm.hrmData.getEmpFormalNameByID;
import static com.blueseer.hrm.hrmData.getEmployeeMstr;
import static com.blueseer.hrm.hrmData.isValidEmployeeID;
import com.blueseer.inv.invData;
import com.blueseer.inv.invData.inv_ctrl;
import static com.blueseer.prd.prdData.addJobClock;
import static com.blueseer.prd.prdData.getJobClock;
import static com.blueseer.prd.prdData.getJobClockHistory;
import static com.blueseer.prd.prdData.getJobClockInTime;
import com.blueseer.prd.prdData.job_clock;
import static com.blueseer.prd.prdData.updateJobClock;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.getPlanDetHistory;
import static com.blueseer.sch.schData.getPlanMstr;
import static com.blueseer.sch.schData.getPlanOperation;
import com.blueseer.sch.schData.plan_mstr;
import com.blueseer.sch.schData.plan_operation;
import static com.blueseer.sch.schData.updatePlanOperationStatusQty;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import static com.blueseer.utl.BlueSeerUtils.xZero;
import static com.blueseer.utl.OVData.getSysMetaValue;
import java.awt.Component;
import java.sql.Connection;
import java.util.Calendar;
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
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class JobScanIO extends javax.swing.JPanel {

String partnumber = "";
String custpart = "";
int serialno = 0;
String serialno_str = "";
String quantity = "";

String sitename = "";
String siteaddr = "";
String sitephone = "";
String sitecitystatezip = "";
boolean planLegit = false;
boolean requireOpScan = false;
boolean isLoad = false;

String clockdate = "";
String clocktime = "";
String plannbr = "";
String planop = "";

public static plan_mstr pm = null;

javax.swing.table.DefaultTableModel historymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "user", "jobid", "operation", "qty", "in date", "in time", "out date", "out time", "In/Out"
            });
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public JobScanIO() {
        initComponents();
        setLanguageTags(this);
    }

     class AnswerWorker extends SwingWorker<Integer, Integer> {
        protected Integer doInBackground() throws Exception
        {
                // Do a time-consuming task.
                disableAll();
                Thread.sleep(2000);
                return 1;
        }

        protected void done()
        {
                try
                {
                       // JOptionPane.showMessageDialog(f, get());
                        initvars(null);
                }
                catch (Exception e)
                {
                        MainFrame.bslog(e);
                }
        }
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
     
             //   JasperExportManager.exportReportToPdfFile(print,"temp/subjob.pdf");
                JasperViewer jasperViewer = new JasperViewer(print, false);
                jasperViewer.setVisible(true);
    
    
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
        }
      
   
    /* We set the selected service and pass it as a paramenter */
 
    }
    
    public void getScanHistory() {
       historymodel.setRowCount(0);
       String io = "";
       String operatorName = "";
       ArrayList<String[]> hist = getJobClockHistory(setDateDB(new java.util.Date()));
       for (String[] h : hist) {
           io = (h[8].equals("01")) ? "In" : "Out";
           operatorName = getEmpFormalNameByID(h[2]);
            historymodel.addRow(new Object[]{
                      operatorName, // user
                      h[0], // jobid
                      h[1], // op
                      h[3], // qty
                      h[4], // in date
                      h[5], // in time
                      h[6], // out date
                      h[7], // outtime
                      io // code
                  });
       }
    }
    
    public void setOperations(String plan) {
        isLoad = true;
        ddop.removeAllItems();
        
        ArrayList<plan_operation> pos = getPlanOperation(plan);
            for (plan_operation po : pos) {
                ddop.addItem(String.valueOf(po.plo_op()));
            }
        
        isLoad = false;
    }
    
    public void disableAll() {
        dddir.setEnabled(false);
        ddop.setEnabled(false);
        tbqty.setEnabled(false);
        tbscan.setEnabled(false);
    }
    
    public void enableAll() {
        dddir.setEnabled(true);
        ddop.setEnabled(true);
        tbqty.setEnabled(true);
        tbscan.setEnabled(true);
        ddop.setEnabled(false);
       ddop.setVisible(true);
       lbloperation.setVisible(true);
       tbqty.setVisible(true);
       lblqty.setVisible(true);
       qtylabel.setVisible(true);
       btcommit.setVisible(true);
    }
    
    public void badScan(String messg) {
      planLegit = false;
      btcommit.setEnabled(false);
      tbscan.setText("");
      tbscan.setEnabled(false);
      lblmessage.setText(messg);
      lblmessage.setForeground(Color.red);
      tbscan.requestFocusInWindow();
    }
    
    public void validateOperator(String dir, String plan) {
       // System.out.println("firing...");
        if (! isValidEmployeeID(tboperator.getText())) {
            badScan("Invalid Employee ID");
            new AnswerWorker().execute();
            return;
        }
        if (plan.isBlank()) {
            badScan("Empty Job Number");
            new AnswerWorker().execute();
            return;
        }
        
        if (dir.equals("In")) {
                // create job_clock record and return init(null)
                String[] m = addJobClock(createRecordIn());
                partlabel.setText("");
                oplabel.setText("");
                userlabel.setText("");
                qtylabel.setText("");
                if (m[0].equals("1")) {
                   lblmessage.setText("already clocked in");
                   lblmessage.setForeground(Color.red);
                } else {
                   lblmessage.setText("clock in success");
                   lblmessage.setForeground(Color.blue);
                }
                new AnswerWorker().execute();
        } else {
                job_clock jc = getJobClock(new String[]{plan, ddop.getSelectedItem().toString(), tboperator.getText()});
                if (jc.m()[0].equals("1")) {
                    lblmessage.setText("No clock In record found for this ticket/operation/user combination");
                    lblmessage.setForeground(Color.red);
                    new AnswerWorker().execute();
                } else {
                    if (! pm.plan_type().equals("SRVC")) {
                    tboperator.setBackground(Color.white);
                    tbqty.requestFocusInWindow();
                    btcommit.setEnabled(true); 
                    } else {
                        job_clock jco = createRecordOut();
                        if (jco == null) {
                            badScan("missing clock in date and time in job_clock record");
                            return;
                        }
                        lblmessage.setText("Scan Out Complete");
                        lblmessage.setForeground(Color.blue);
                        updateJobClock(jco);
                        new AnswerWorker().execute();
                    }
                }
                
        }
    }
    
    public void validateJob(String scan) {
       
        boolean opticketScan = false;
        double qtysched = 0;
        double prevscanned = 0;
        double remaining = 0;
        
        lblmessage.setText("");
        if (scan.isEmpty()) {
            qtylabel.setText("");
            partlabel.setText("");
            return;
        } 
        /*
        if (! projectOpScan && scan.contains("-")) {
            badScan("Project scanning only...Generic Job Ticket must be used");
            new AnswerWorker().execute();
            return;
        }
        */
        if (requireOpScan && ! scan.contains("-")) {
            badScan("Operation Ticket must be used");
            new AnswerWorker().execute();
            return;
        }
        
         // check for operation ticket override...containing hyphen with job-op value
       if (scan.contains("-")) { // must be operation specific ticket
          opticketScan = true;
          String[] sc = scan.split("-",-1);
          if (sc != null && sc.length == 2) {
            plannbr = sc[0];
            planop = sc[1];
          } else { // if sc != null
                badScan("Bad Ticket");
                new AnswerWorker().execute();
                return;
          }
       } else { // regular job scan
           plannbr = scan;
       }
       
       
       // now validate       
       if (! schData.isPlan(plannbr)) {
                badScan("Bad Ticket");
                new AnswerWorker().execute();
                return;
        } 
        pm = getPlanMstr(new String[]{plannbr});
        if (Integer.valueOf(pm.plan_status()) != 0 ) { // check if closed
            badScan(getMessageTag(1071));
            new AnswerWorker().execute();
            return;
        }
        
        if (pm.plan_qty_sched() == 0 && ! pm.plan_type().equals("SRVC")) {
            badScan(getMessageTag(1182));
            new AnswerWorker().execute();
            return;
        }
        
        
        setOperations(plannbr);
        System.out.println("here: " + plannbr + "/" + planop + "/" + opticketScan);
        
        plan_operation po = null;
        
        if (opticketScan) {
        if (ddop.getItemCount() > 0) { 
            if (! planop.isBlank()) {
                ddop.setSelectedItem(planop);
            } else {
                ddop.setSelectedIndex(ddop.getItemCount() - 1);  
            }
        } else {
            badScan("No Operations found! Check Routing for this item.");
            new AnswerWorker().execute();
            return;
        }
        partlabel.setText(schData.getPlanItem(scan));
        qtysched = pm.plan_qty_sched(); // assume parent plan sched qty
        
       
        po = getPlanOperation(Integer.valueOf(plannbr), Integer.valueOf(planop));
        
        qtysched = po.plo_qty();  // override parent plan sched qty
           if (po.plo_operator().isBlank() && ! pm.plan_type().equals("SRVC")) {
               badScan("Operator not assigned for ticket");
               new AnswerWorker().execute();
               return;
           }
        tboperator.setText(po.plo_operator());
        userlabel.setText(getEmpFormalNameByID(po.plo_operator()));
           if (po.plo_status().equals(getGlobalProgTag("closed"))) {
               badScan("Operation ticket is closed");
               new AnswerWorker().execute();
               return;
           }
         
        
        prevscanned = schData.getPlanDetTotQtyByOp(plannbr, planop);
        remaining = qtysched - prevscanned;
        tbqty.setText(bsFormatDouble(remaining));
        qtylabel.setText("qty sched: " + String.valueOf(qtysched) + "     qty scanned: " + String.valueOf(prevscanned));
        } else {
            planop = ddop.getSelectedItem().toString();
        } 
        
        planLegit = true;
       if (dddir.getSelectedItem().toString().equals("In")) {
           tbqty.setText("0");
           tbqty.setEnabled(false);
       } else {
           tbqty.setEnabled(true);
       }
       
       if (opticketScan && po != null && ! po.plo_operator().isBlank()) {
           validateOperator(dddir.getSelectedItem().toString(), plannbr);
           tboperator.setEnabled(false);
       } else {
           tboperator.setEnabled(true);
           tboperator.requestFocusInWindow(); 
       }
       
       
    /*    
    if (! opticketScan) {    
        if (schData.isPlan(scan)) {
      // tbqty.setText(String.valueOf(schData.getPlanSchedQty(scan)));
       partlabel.setText(schData.getPlanItem(scan));
       partlabel.setForeground(Color.blue);
       ddop.removeAllItems();
       ArrayList<String> mylist = OVData.getOperationsByItem(partlabel.getText());
     //  ArrayList<String> mylist = invData.getItemRoutingOPs(partlabel.getText());
       mylist.sort(Comparator.comparingInt(Integer::parseInt)); 
      // Collections.sort(mylist, Collections.reverseOrder());
       for (int i = 0; i < mylist.size(); i++) {
    //   for (int i = mylist.size() - 1; i >= 0; i--) {    
           ddop.addItem(mylist.get(i));
       }
       if (ddop.getItemCount() > 0) {
        ddop.setSelectedIndex(mylist.size() - 1);
       }
       
       if (ddop.getItemCount() <= 0) {
           ddop.addItem("0");
           partlabel.setText(partlabel.getText() + " " + "No Operations..." + "\n" + "make sure item has routing AND standard cost");
           partlabel.setForeground(Color.yellow);
           new AnswerWorker().execute();
           return;
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
            new AnswerWorker().execute();
            return;
            } 
          
          if (qtysched == 0 ) { // check if scheduled
            lblmessage.setText(getMessageTag(1182,tbscan.getText()));
            lblmessage.setForeground(Color.red);
            btcommit.setEnabled(false);
            } 
       }
       
       // now get history of plan jobid (scan)
       planLegit = true;
       if (dddir.getSelectedItem().toString().equals("In")) {
           tbqty.setText("0");
           tbqty.setEnabled(false);
       } else {
           tbqty.setEnabled(true);
       }
       
      
       
       tboperator.requestFocusInWindow();
       
      } else {
              planLegit = false;
              btcommit.setEnabled(false);
              tbscan.setText("");
              qtylabel.setText("Bad Ticket");
              qtylabel.setForeground(Color.red);
              tbscan.requestFocusInWindow();
      }
        
    } // if not opticketscan    
    */    
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
        //System.out.println("here: inivar");
        requireOpScan = BlueSeerUtils.ConvertStringToBool(getSysMetaValue("system", "inventorycontrol", "operation_scan_required"));
       // projectOpScan = BlueSeerUtils.ConvertStringToBool(getSysMetaValue("system", "inventorycontrol", "project_operation"));
        btcommit.setEnabled(false);
        tbqty.setText("");
        tbscan.setText("");
        tboperator.setText("");
        ddop.removeAllItems();
        
        
        
        lblmessage.setText("");
        lblmessage.setForeground(Color.black);
        partlabel.setText("");
        partlabel.setForeground(Color.black);
        oplabel.setText("");
        oplabel.setForeground(Color.black);
        userlabel.setText("");
        userlabel.setForeground(Color.black);
        qtylabel.setText("");
        qtylabel.setForeground(Color.black);
        
        tboperator.setBackground(Color.white);
        tbscan.setBackground(Color.white);
        
       historytable.setModel(historymodel);
       historytable.getTableHeader().setReorderingAllowed(false);
       historymodel.setRowCount(0);
       
       getScanHistory();
       enableAll();
       if (! requireOpScan) {
           ddop.setEnabled(false);
           ddop.setVisible(false);
           lbloperation.setVisible(false);
           tbqty.setEnabled(false);
           tbqty.setVisible(false);
           lblqty.setVisible(false);
           qtylabel.setVisible(false);
           btcommit.setVisible(false);
       }
       btcommit.setEnabled(false);
       
       tbscan.requestFocusInWindow();
        
    }
    
    public job_clock createRecordIn() { 
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        clockdate = dfdate.format(now);
        clocktime = dftime.format(now);
                    
        job_clock x = new job_clock(null, 
                 bsParseInt(plannbr),
                 bsParseInt(planop),
                 0,
                 tboperator.getText(),
                 setDateDB(now), //indate
                 setDateDB(null),  //outdate
                 clocktime,
                 "",
                 0,
                 "01" // clock in
                );
        return x;
    }
   
    public job_clock createRecordOut() { 
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        clockdate = dfdate.format(now);
        clocktime = dftime.format(now);
        
        String[] intime = getJobClockInTime(bsParseInt(plannbr), bsParseInt(planop), tboperator.getText());
        
        if (intime[0].isBlank() || intime[1].isBlank()) {
            return null;
        }
        
        Calendar clockstart = Calendar.getInstance();
        Calendar clockstop = Calendar.getInstance();
        clockstart.set( Integer.valueOf(intime[0].substring(0,4)), Integer.valueOf(intime[0].substring(5,7)), Integer.valueOf(intime[0].substring(8,10)), Integer.valueOf(intime[1].substring(0,2)), Integer.valueOf(intime[1].substring(3,5)) );
        clockstop.set(Integer.valueOf(clockdate.substring(0,4)), Integer.valueOf(clockdate.substring(5,7)), Integer.valueOf(clockdate.substring(8,10)), Integer.valueOf(clocktime.substring(0,2)), Integer.valueOf(clocktime.substring(3,5)));
        double quarterhour = 0;
        long milis1 = clockstart.getTimeInMillis();
        long milis2 = clockstop.getTimeInMillis();
        long diff = milis2 - milis1;
        long diffHours = diff / (60 * 60 * 1000);
        long diffMinutes = diff / (60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffSeconds = diff / 1000;
        if (diffMinutes > 0) {
            quarterhour = (diffMinutes / (double) 60) ;

        }
        double nbrhours = quarterhour;   // this is the total hours accumulated in quarter increments

        
        
        job_clock x = new job_clock(null, 
                 bsParseInt(plannbr),
                 bsParseInt(planop),
                 bsParseDouble(xZero(tbqty.getText())),
                 tboperator.getText(),
                 setDateDB(null), //indate
                 setDateDB(now),  //outdate
                 "", // intime
                 clocktime, // out time
                 bsParseDouble(currformatDouble(nbrhours)),
                 "00" // clock out and close
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
        tbqty = new javax.swing.JTextField();
        lblqty = new javax.swing.JLabel();
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        ddop = new javax.swing.JComboBox();
        lbloperation = new javax.swing.JLabel();
        tboperator = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        qtylabel = new javax.swing.JLabel();
        lblmessage = new javax.swing.JLabel();
        partlabel = new javax.swing.JLabel();
        jpaneltable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        historytable = new javax.swing.JTable();
        dddir = new javax.swing.JComboBox<>();
        oplabel = new javax.swing.JLabel();
        userlabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Scan By Operator"));
        jPanel1.setName("panelmain"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        lblqty.setText("Quantity");
        lblqty.setName("lblqty"); // NOI18N

        tbscan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbscanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbscanFocusLost(evt);
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

        ddop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddopActionPerformed(evt);
            }
        });

        lbloperation.setText("Operation");
        lbloperation.setName("lblop"); // NOI18N

        tboperator.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tboperatorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tboperatorFocusLost(evt);
            }
        });
        tboperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tboperatorActionPerformed(evt);
            }
        });

        jLabel8.setText("UserID");
        jLabel8.setName("lbluserid"); // NOI18N

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpaneltableLayout.setVerticalGroup(
            jpaneltableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpaneltableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        dddir.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "In", "Out" }));

        oplabel.setForeground(new java.awt.Color(25, 102, 232));

        userlabel.setForeground(new java.awt.Color(25, 102, 232));

        jLabel1.setText("Direction");

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblqty, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbloperation, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(dddir, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btclear))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tboperator, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btcommit)
                                    .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(22, 22, 22)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(oplabel, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                    .addComponent(partlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                    .addComponent(userlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                    .addComponent(qtylabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jpaneltable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btclear))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(partlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tboperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbloperation))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblqty))
                        .addGap(39, 39, 39)
                        .addComponent(btcommit))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(userlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(oplabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(qtylabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpaneltable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        if (plannbr.isBlank() || planop.isBlank()) {
            badScan("job number or operation is blank");
            return;
        }
        
        job_clock jc = createRecordOut();
        if (jc == null) {
            badScan("missing clock in date and time in job_clock record");
            return;
        }
        
        
       // plan_mstr pm = schData.getPlanMstr(new String[]{plannbr});
        inv_ctrl ic = invData.getINVCtrl(new String[]{plannbr});
        double prevscanned = schData.getPlanDetTotQtyByOp(plannbr, planop);
        String[] detail = invData.getItemDetail(pm.plan_item());
        boolean isPlan = true;
        
        if (pm.m()[0].equals("1")) {
            isPlan = false;
        }
        double qty = Double.valueOf(tbqty.getText());
        
        if (! isPlan) {
            badScan(getMessageTag(1070,plannbr));
            new AnswerWorker().execute();
            return;
        }
        
        if (isPlan &&  Integer.valueOf(pm.plan_status()) > 0 ) {
            badScan(getMessageTag(1071,plannbr));
            new AnswerWorker().execute();
            return;
        }
        if (isPlan &&  Integer.valueOf(pm.plan_status()) < 0 ) {
            badScan(getMessageTag(1072,plannbr));
            new AnswerWorker().execute();
            return;
        }
        
         // check inventory control flag... "Plan Multiple Scan Issues"
        // if false...only one scan per plan ticket per operation
        
        if (! BlueSeerUtils.ConvertStringToBool(ic.planmultiscan()) && (prevscanned > 0)) {
            badScan("Ticket Already Reported for this Operation " + plannbr + " / " + planop);
            new AnswerWorker().execute();
            return;
        }
        
        
        // now lets sum up qtys posted previously (if any) for this OP and this Ticket and make sure
        // qty field is not greater than qty previous + qty scheduled
        // this should work for multiscan and nonmultican conditions
       // bsmf.MainFrame.show(pm.plan_qty_sched());
        
        if ( qty > (pm.plan_qty_sched() - prevscanned) ) {
             lblmessage.setText("Qty Exceeds limit (Already Scanned Qty: " + String.valueOf(prevscanned) + " out of SchedQty: " + String.valueOf(pm.plan_qty_sched()) + ")");
            lblmessage.setForeground(Color.red);
            initvars(null);
            return;
        }
        
        
        
        
        if (isPlan &&  Integer.valueOf(pm.plan_status()) == 0 ) {
            boolean isLastOp = OVData.isLastOperation(pm.plan_item(), planop );
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
                planop,
                tbqty.getText(),
                dfdate.format(now),
                loc, // location
                plannbr,  // serialno  
                pm.plan_type(),  // reference 
                pm.plan_site(),
                bsmf.MainFrame.userid,
                prodline,
                "",   //  tr_actcell
                tboperator.getText().replace(",", ""),   // remarks 
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
                 lblmessage.setText("Scan Out Complete");
                 lblmessage.setForeground(Color.blue);
                 
                 updateJobClock(jc);
                 String status = ((qty + prevscanned) >= pm.plan_qty_sched()) ? "closed" : "open";
                 
                // do not close or commit qty if SRVC
                 if (! pm.plan_type().equals("SRVC")) {
                 updatePlanOperationStatusQty(String.valueOf(plannbr), String.valueOf(planop), status, bsParseDouble(tbqty.getText()));
                 }
                if (BlueSeerUtils.ConvertStringToBool(ic.printsubticket())) {               
                     try {
                        printTubTicket(plannbr, String.valueOf(key));
                    } catch (PrinterException ex) {
                        MainFrame.bslog(ex);
                    }
                }
            }
              new AnswerWorker().execute();
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
        validateJob(tbscan.getText());        
    }//GEN-LAST:event_tbscanFocusLost

    private void tboperatorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tboperatorFocusGained
        if (planLegit) {
        tboperator.setBackground(Color.yellow);
        }
    }//GEN-LAST:event_tboperatorFocusGained

    private void tboperatorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tboperatorFocusLost
       if (! tboperator.getText().isBlank() && ! evt.getCause().equals("ACTIVATION")) {
        validateOperator(dddir.getSelectedItem().toString(), plannbr);
       }
    }//GEN-LAST:event_tboperatorFocusLost

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void ddopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddopActionPerformed
        if (! isLoad && ddop.getSelectedItem() != null && ! ddop.getSelectedItem().toString().isBlank()) {
            planop = ddop.getSelectedItem().toString();
        }
       // bsmf.MainFrame.show("ddopaction: " + planop);
    }//GEN-LAST:event_ddopActionPerformed

    private void tboperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tboperatorActionPerformed
        if (! tboperator.getText().isBlank()) {
           validateOperator(dddir.getSelectedItem().toString(), plannbr);
       }
       
    }//GEN-LAST:event_tboperatorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JComboBox<String> dddir;
    private javax.swing.JComboBox ddop;
    private javax.swing.JTable historytable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpaneltable;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JLabel lbloperation;
    private javax.swing.JLabel lblqty;
    private javax.swing.JLabel oplabel;
    private javax.swing.JLabel partlabel;
    private javax.swing.JLabel qtylabel;
    private javax.swing.JTextField tboperator;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbscan;
    private javax.swing.JLabel userlabel;
    // End of variables declaration//GEN-END:variables
}
