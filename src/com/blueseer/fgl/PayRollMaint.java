/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.fgl;

import com.blueseer.shp.*;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.prd.ProdSchedPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author vaughnte
 */
public class PayRollMaint extends javax.swing.JPanel {
 
    String exoincfilepath = OVData.getSystemTempDirectory() + "/" + "chartexpinc.jpg";
    String buysellfilepath = OVData.getSystemTempDirectory() + "/" + "chartbuysell.jpg";
    Double expenses = 0.00;
    Double inventory = 0.00;
    
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Detail", "ID", "Key", "Type", "EntityNbr", "EntityName", "EffDate", "TotalQty", "TotalSales", "Print"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  || col == 9 )       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"});
    
    javax.swing.table.DefaultTableModel modelearnings = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "desc", "rate", "amt"});
     javax.swing.table.DefaultTableModel modeldeduct = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "desc", "rate", "amt"});
    
     class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
     
     class SomeRenderer extends DefaultTableCellRenderer {
         
       public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
     
        
              if (isSelected)
        {
            setBackground(Color.white);
            setForeground(Color.BLACK);
           
        }
        
            String trantype = tablereport.getModel().getValueAt(table.convertRowIndexToModel(row), 4).toString();
            if ( column == 4 && trantype.equals("sell") ) {
           // c.setBackground(Color.green);
            c.setForeground(Color.blue);
            }
            else if ( column == 4 && trantype.equals("buy") ) {
           // c.setBackground(Color.blue);
            c.setForeground(Color.red);
            }
            else {
                c.setBackground(table.getBackground());
            }
            
      
        
           
            
            return c;
    }
    }
    
     
        class Task extends SwingWorker<String[], Void> {
        /*
         * Main task. Executed in background thread.
         */
          String type = "";
          
          public Task(String type) {
              this.type = type;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "add":
                    message = addPayRoll();
                    break;
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        /*
         * Executed in event dispatch thread
         */
        public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
           if (this.type.equals("delete")) {
             initvars("");  
           }  else {
             initvars(tbid.getText());  
           }
           
            
            } catch (Exception e) {
                e.printStackTrace();
            } 
           
        }
    }  
                 
    
      public String[] addPayRoll() {
        String[] message = new String[2];
        
          try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
           Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
          
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                
                if (proceed) {
                    st.executeUpdate("insert into pay_mstr "
                        + "(py_id, py_site, py_desc, py_userid, py_startdate, py_enddate, py_paydate, py_status, py_comments ) "
                        + " values ( " + "'" + tbid.getText() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + tbcomments.getText().replace("'", "") + "'" + ","
                        + "'" + bsmf.MainFrame.userid + "'" + ","        
                        + "'" + dfdate.format(dcfrom.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(dcto.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(dcpay.getDate()).toString() + "'" + ","  
                        + "'" + "" + "'" + ","
                        + "'" + tbcomments.getText().replace("'", "") + "'" 
                        + ")"
                        + ";");

                  //    "select", "RecID", "EmpID", "LastName", "FirstName", "MidName", "Dept", "Shift", "Supervisor", "Type", "Profile", "JobTitle", "Rate", "tothrs", "Amount"
                   
                    int checknbr = Integer.valueOf(tbchecknbr.getText());
                    for (int j = 0; j < tablereport.getRowCount(); j++) {
                        
                        st.executeUpdate("insert into pay_det "
                            + "(pyd_id, pyd_empnbr, pyd_emplname, pyd_empfname, pyd_empmname, pyd_empdept, pyd_empshift, pyd_supervisor, pyd_emptype, "
                            + "pyd_payprofile, pyd_empjobtitle, pyd_emprate,  pyd_status, pyd_checknbr, pyd_tothours, pyd_payamt, pyd_paydate ) "
                            + " values ( " 
                            + "'" + tbid.getText() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 2).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 3).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 4).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 5).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 6).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 7).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 8).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 9).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 10).toString() + "'" + ","
                            + "'" + tablereport.getValueAt(j, 11).toString() + "'" + "," 
                            + "'" + tablereport.getValueAt(j, 12).toString() + "'" + ","  // rate    
                            + "'" + "paid" + "'" + ","    // status
                            + "'" + String.valueOf(checknbr) + "'" + ","  // checknumber   
                            + "'" + tablereport.getValueAt(j, 13).toString() + "'" + ","  // tothours  
                            + "'" + tablereport.getValueAt(j, 14).toString() + "'" + ","  // pay amount        
                            + "'" + dfdate.format(dcpay.getDate()).toString() + "'" 
                            + ")"
                            + ";");
                        
                         checknbr++;
                       
                         
                         // now do earnings detail
                         // "EmpID", "type", "code", "desc", "rate", "amt"
                              for (int e = 0; e < modelearnings.getRowCount() ; e++) {
                                      st.executeUpdate("insert into pay_line "
                                + "(pyl_id, pyl_empnbr, pyl_type, pyl_code, pyl_desc, pyl_rate, pyl_amt ) "
                                + " values ( " 
                                + "'" + tbid.getText().toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 0).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 1).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 2).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 3).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 4).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 5).toString() + "'" 
                                + ")"
                                + ";");
                              }
                              
                         // now do deductions detail
                         // "EmpID", "type", "code", "desc", "rate", "amt"
                              for (int e = 0; e < modeldeduct.getRowCount() ; e++) {
                                      st.executeUpdate("insert into pay_line "
                                + "(pyl_id, pyl_empnbr, pyl_type, pyl_code, pyl_desc, pyl_rate, pyl_amt ) "
                                + " values ( " 
                                + "'" + tbid.getText().toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 0).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 1).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 2).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 3).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 4).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 5).toString() + "'" 
                                + ")"
                                + ";");
                              }     
                         
                        
                    }
                    
             message = new String[]{"0", "PayRoll has been committed"};         
                     
                     
           
             
             
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                message = new String[]{"1", "Cannot commit PayRoll"};
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        return message;
    }
    
    
     
     public void getEarnings(String empnbr, String fromdate, String todate) {
         
          jtpEarnings.setText("");
          jtpEarnings.setContentType("text/html");
         DecimalFormat df = new DecimalFormat("#0.00");
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String html = "<html><body><table><tr><td align='right' style='color:blue;font-size:20px;'>Earnings:</td><td></td></tr></table>";
                String codedesc = "";
                 res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs', t.recid as 't.recid', t.code_id as 't.code_id', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', e.emp_rate as 'e.emp_rate', clc_code, clc_desc " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr inner join clock_code on clc_code = t.code_id " +
                              " where t.emp_nbr = "  + "'" + empnbr + "'" +
                           " and t.indate >= " + "'" + fromdate + "'" +
                           " and t.indate <= " + "'" + todate + "'" + 
                                " group by t.code_id " +       
                                " order by t.code_id " +      
                               ";" );
                while (res.next()) {
                    codedesc = res.getString("t.code_id");
                    if (codedesc.equals("00") || codedesc.equals("77")) {
                        codedesc = "Compensation";
                    } else {
                        codedesc = res.getString("clc_desc");
                    }
                    html += "<table><tr><td align='right'>" + codedesc + ":" + "</td><td>" + df.format(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate")) + "</td></tr>";
                
                modelearnings.addRow(new Object []{empnbr,
                                            "earnings",
                                            res.getString("t.code_id"),
                                            res.getString("clc_desc"),
                                            res.getString("e.emp_rate"),
                                            df.format(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"))
                                            } );
                
                }
             html += "</table></body></html>";
              jtpEarnings.setText(html);

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to get browse detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

         
     }
  
     public void getDeductions(String empnbr, double amount) {
         
         modeldeduct.setNumRows(0);
          jtpDeductions.setText("");
          jtpDeductions.setContentType("text/html");
          StyledDocument doc = jtpDeductions.getStyledDocument();
          SimpleAttributeSet keyWord = new SimpleAttributeSet();
          StyleConstants.setForeground(keyWord, Color.RED);
          StyleConstants.setBackground(keyWord, Color.YELLOW);
          StyleConstants.setBold(keyWord, true);
         DecimalFormat df = new DecimalFormat("#0.00");
         double empexception = 0.00;
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String html = "<html><body><table><tr><td align='right' style='color:blue;font-size:20px;'>Deductions:</td><td></td></tr></table>";
                res = st.executeQuery("SELECT paypd_desc, paypd_amt from pay_profdet inner join " +
                             " emp_mstr on emp_profile = paypd_parentcode " +
                              " where emp_nbr = " + "'" + empnbr + "'" +
                              " order by paypd_desc " +        
                               ";" );
                while (res.next()) {
                    html += "<table><tr><td align='right'>" + res.getString("paypd_desc") + ":" + "</td><td>" + df.format(amount * (res.getDouble("paypd_amt") / 100)) + "</td></tr>";
                // doc.insertString(doc.getLength(), res.getString("paypd_desc") + ":\t", null );
                // doc.insertString(doc.getLength(), df.format(amount * res.getDouble("paypd_amt")) + "\n", null );
                // "EmpID", "type", "code", "desc", "rate", "amt"
                 modeldeduct.addRow(new Object []{empnbr,
                                            "deduction",
                                            "",
                                            res.getString("paypd_desc"),
                                            res.getString("paypd_amt"),
                                            df.format(amount * (res.getDouble("paypd_amt") / 100))
                                            } );
                
                }
                
                // now get specific employee deductions
                res = st.executeQuery("SELECT empx_desc, empx_amt, empx_amttype from emp_exception " +
                              " where empx_nbr = " + "'" + empnbr + "'" +
                              " order by empx_desc " +        
                               ";" );
                while (res.next()) {
                    if (res.getString("empx_amttype").equals("percent")) {
                      empexception =  (amount * res.getDouble("empx_amt") / 100);
                    } else {
                      empexception = res.getDouble("empx_amt");  
                    }
                    html += "<tr><td align='right'>" + res.getString("empx_desc") + ":" + "</td><td>" + df.format(empexception) + "</td></tr>";
                // doc.insertString(doc.getLength(), res.getString("paypd_desc") + ":\t", null );
                // doc.insertString(doc.getLength(), df.format(amount * res.getDouble("paypd_amt")) + "\n", null ); 
                
                    modeldeduct.addRow(new Object []{empnbr,
                                            "deduction",
                                            "",
                                            res.getString("empx_desc"),
                                            res.getString("empx_amt"),
                                            empexception
                                            } );
                }
                
                
                html += "</table></body></html>";
              jtpDeductions.setText(html);

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to get browse detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     
    /**
     * Creates new form ScrapReportPanel
     */
    public PayRollMaint() {
        initComponents();
    }

    public void getdetail(String empnbr, String fromdate, String todate) {
      
         modeldetail.setNumRows(0);
         double totalsales = 0.00;
         double totalqty = 0.00;
         DecimalFormat df = new DecimalFormat("#0.00");
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;
                String blanket = "";
                res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj' FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr = " + "'" + empnbr + "'" +
                              "and t.indate >= " + "'" + fromdate + "'" +
                               "and t.indate <= " + "'" + todate + "'" + 
                               " order by e.emp_nbr, t.indate" +
                               ";" );
                while (res.next()) {
                  
                     modeldetail.addRow(new Object []{res.getString("t.recid"),
                                            res.getString("t.emp_nbr"),
                                            res.getString("e.emp_lname"),
                                            res.getString("e.emp_fname"),
                                            res.getString("e.emp_dept"),
                                            res.getString("t.code_id"),
                                             res.getString("t.indate"),
                                            res.getString("t.intime"),
                                            res.getString("t.intime_adj"),
                                            res.getString("t.outdate"),
                                            res.getString("t.outtime"),
                                            res.getString("t.outtime_adj"),
                                            res.getString("t.tothrs")
                                            } );
                    
                    
                }
               
             
               
                tabledetail.setModel(modeldetail);
                this.repaint();

            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to get browse detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void disableAll() {
        
    }
    
    public void enableAll() {
        
    }
    
    public void clearAll() {
        
    }
    
    public boolean validateInput() {
        boolean myreturn = true;
        
        return myreturn;
    }
    public void initvars(String arg) {
        tbtotpayroll.setText("0");
       
        
        java.util.Date now = new java.util.Date();
       
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
        dcfrom.setDate(firstday);
        dcto.setDate(now);
               
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        // tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
       //  tablereport.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
       //  tablereport.getColumnModel().getColumn(7).setMaxWidth(100);
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));
        
        
       
      
                    
                    
                    
       
        
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        chartpanel.setVisible(false);
          
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
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        chartpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtpEarnings = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtpDeductions = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btrun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        tbcsv = new javax.swing.JButton();
        tbid = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btbrowse = new javax.swing.JButton();
        btcommit = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        tbcomments = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dcpay = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        tbchecknbr = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tbtotpayroll = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Payroll Maintenance"));

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new javax.swing.BoxLayout(chartpanel, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane3.setViewportView(jtpEarnings);

        chartpanel.add(jScrollPane3);

        jScrollPane4.setViewportView(jtpDeductions);

        chartpanel.add(jScrollPane4);

        tablepanel.add(chartpanel);

        btdetail.setText("Hide Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btrun.setText("Run");
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");

        jLabel6.setText("To Date:");

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        tbcsv.setText("CSV");
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        jLabel7.setText("ID:");

        btnew.setText("New");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel1.setText("Site:");

        jLabel2.setText("Comments:");

        dcpay.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("PayDate:");

        jLabel4.setText("CheckNbr:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tbid, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                    .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btdetail)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbcsv))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(94, 94, 94)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tbcomments, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(tbchecknbr, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(dcpay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))))))
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(186, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btrun))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btcommit)
                        .addGap(19, 19, 19))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7)
                                .addComponent(btnew)
                                .addComponent(btdetail)
                                .addComponent(tbcsv))
                            .addComponent(btbrowse))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(tbcomments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(9, 9, 9)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(dcpay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tbchecknbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))))
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btcommit)
                            .addComponent(btrun))
                        .addContainerGap())))
        );

        tbtotpayroll.setText("0");

        jLabel11.setText("Total PayRoll:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 194, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tbtotpayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtotpayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(92, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablepanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btrunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btrunActionPerformed
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");   
        DecimalFormat df = new DecimalFormat("#0.00");
        tablereport.setModel(OVData.getPayRollHours(dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate())));
        double totamt = 0.00; 
                 for (int j = 0; j < tablereport.getRowCount(); j++) {
                  totamt += Double.valueOf(tablereport.getValueAt(j, 8).toString()); 
                 }
                 tbtotpayroll.setText(String.valueOf(df.format(totamt)));
    }//GEN-LAST:event_btrunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
       chartpanel.setVisible(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd"); 
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                getdetail(tablereport.getValueAt(row, 2).toString(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()) );
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
                 chartpanel.setVisible(true);
                getDeductions(tablereport.getValueAt(row, 2).toString(), Double.valueOf(tablereport.getValueAt(row, 8).toString()));
                getEarnings(tablereport.getValueAt(row, 2).toString(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()) );
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
      if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "ecnmaint,ecn_nbr");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
          if (!validateInput()) {
             return;
         }
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableAll();
        Task task = new Task("add");
        task.execute();   
    }//GEN-LAST:event_btcommitActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JPanel chartpanel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcpay;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextPane jtpDeductions;
    private javax.swing.JTextPane jtpEarnings;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbchecknbr;
    private javax.swing.JTextField tbcomments;
    private javax.swing.JButton tbcsv;
    private javax.swing.JTextField tbid;
    private javax.swing.JLabel tbtotpayroll;
    // End of variables declaration//GEN-END:variables
}
