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

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.shp.*;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.hrm.hrmData;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.EDData;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
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
    double expenses = 0;
    double inventory = 0;
    boolean isnew = false;
    
     javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RecID", "EmpID", "LastName", "FirstName", "MidName", "Dept", "Shift", "Supervisor", "Type", "Profile", "JobTitle", "Rate", "tothrs", "Amount", "PayDate"})
                       {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
    
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"RecID", "EmpID", "LastName", "FirstName", "Dept", "Code", "InDate", "InTime", "InTmAdj", "OutDate", "OutTime", "OutTmAdj", "tothrs"});
    
    javax.swing.table.DefaultTableModel modelearnings = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "desc", "rate", "amt"});
     javax.swing.table.DefaultTableModel modeldeduct = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "profile", "profline", "desc", "rate", "amt"});
    
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
             initvars(null);  
           }  else {
             initvars(new String[]{tbid.getText()});  
           }
           
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
                 
    
      public String[] addPayRoll() {
        String[] message = new String[2];
        
          try {
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                
                boolean proceed = true;
                int i = 0;
                double netcash = 0;
                double netdeduction = 0;
                
                if (proceed) {
                    st.executeUpdate("insert into pay_mstr "
                        + "(py_id, py_site, py_desc, py_userid, py_startdate, py_enddate, py_paydate, py_status, py_comments, py_bank, py_nachasent ) "
                        + " values ( " + "'" + tbid.getText() + "'" + ","
                        + "'" + ddsite.getSelectedItem().toString() + "'" + ","
                        + "'" + tbcomments.getText().replace("'", "") + "'" + ","
                        + "'" + bsmf.MainFrame.userid + "'" + ","        
                        + "'" + dfdate.format(dcfrom.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(dcto.getDate()).toString() + "'" + ","
                        + "'" + dfdate.format(dcpay.getDate()).toString() + "'" + ","  
                        + "'" + "" + "'" + ","
                        + "'" + tbcomments.getText().replace("'", "") + "'" + ","
                        + "'" + ddbank.getSelectedItem().toString() + "'" + ","    
                        + "'" + "0" + "'"        
                        + ")"
                        + ";");

                  //    "select", "RecID", "EmpID", "LastName", "FirstName", "MidName", "Dept", "Shift", "Supervisor", "Type", "Profile", "JobTitle", "Rate", "tothrs", "Amount"
                   
                    int checknbr = Integer.valueOf(tbchecknbr.getText());
                    String paydate = "";
                    for (int j = 0; j < tablereport.getRowCount(); j++) {
                        netcash += bsParseDouble(tablereport.getValueAt(j, 14).toString());
                        if (tablereport.getValueAt(j, 15).toString().isEmpty()) {
                            paydate = dfdate.format(dcpay.getDate()).toString();
                        } else {
                            paydate = tablereport.getValueAt(j, 15).toString();
                        }
                        st.executeUpdate("insert into pay_det "
                            + "(pyd_id, pyd_empnbr, pyd_emplname, pyd_empfname, pyd_empmname, pyd_empdept, pyd_empshift, pyd_empsupervisor, pyd_emptype, "
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
                            + "'" + tablereport.getValueAt(j, 12).toString().replace(defaultDecimalSeparator, '.') + "'" + ","  // rate    
                            + "'" + "paid" + "'" + ","    // status
                            + "'" + String.valueOf(checknbr) + "'" + ","  // checknumber   
                            + "'" + tablereport.getValueAt(j, 13).toString().replace(defaultDecimalSeparator, '.') + "'" + ","  // tothours  
                            + "'" + tablereport.getValueAt(j, 14).toString().replace(defaultDecimalSeparator, '.') + "'" + ","  // pay amount     
                            + "'" + paydate + "'"   // paydate  
                            + ")"
                            + ";");
                        
                         
                         
                         // now do earnings detail
                         getEarnings(tablereport.getValueAt(j, 2).toString(), dfdate.format(dcfrom.getDate()).toString(), dfdate.format(dcto.getDate()).toString());
                         // "EmpID", "type", "code", "desc", "rate", "amt"
                              for (int e = 0; e < modelearnings.getRowCount() ; e++) {
                                      st.executeUpdate("insert into pay_line "
                                + "(pyl_id, pyl_empnbr, pyl_type, pyl_code, pyl_profile, pyl_profile_line, pyl_checknbr, pyl_desc, pyl_rate, pyl_amt ) "
                                + " values ( " 
                                + "'" + tbid.getText().toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 0).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 1).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 2).toString() + "'" + ","
                                + "''" + ","  // profile  
                                + "'0'" + ","  // profileline          
                                + "'" + String.valueOf(checknbr) + "'" + ","  // checknumber  
                                + "'" + modelearnings.getValueAt(e, 3).toString() + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 4).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + modelearnings.getValueAt(e, 5).toString().replace(defaultDecimalSeparator, '.') + "'" 
                                + ")"
                                + ";");
                              }
                              
                         // now do deductions detail
                         getDeductions(tablereport.getValueAt(j, 2).toString(), bsParseDouble(tablereport.getValueAt(j, 14).toString()));
                         // "EmpID", "type", "code", "desc", "rate", "amt"
                              for (int e = 0; e < modeldeduct.getRowCount() ; e++) {
                                      st.executeUpdate("insert into pay_line "
                                + "(pyl_id, pyl_empnbr, pyl_type, pyl_code, pyl_profile, pyl_profile_line, pyl_checknbr, pyl_desc, pyl_rate, pyl_amt ) "
                                + " values ( " 
                                + "'" + tbid.getText().toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 0).toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 1).toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 2).toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 3).toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 4).toString() + "'" + ","        
                                + "'" + String.valueOf(checknbr) + "'" + ","  // checknumber  
                                + "'" + modeldeduct.getValueAt(e, 5).toString() + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 6).toString().replace(defaultDecimalSeparator, '.') + "'" + ","
                                + "'" + modeldeduct.getValueAt(e, 7).toString().replace(defaultDecimalSeparator, '.') + "'" 
                                + ")"
                                + ";");
                                      netdeduction += bsParseDouble(modeldeduct.getValueAt(e, 7).toString());
                              }     
                        
                              // now update timeclock records for employee and date range
                              updateClockRecords(tablereport.getValueAt(j, 2).toString(), dfdate.format(dcfrom.getDate()).toString(), dfdate.format(dcto.getDate()).toString(), String.valueOf(checknbr));
                        
                              checknbr++;
                       
                    }
                    
                    // now lets do journal entries
                    fglData.glEntryFromPayRoll(tbid.getText(), dcpay.getDate());
                    
             message = new String[]{"0", "PayRoll has been committed"};         
                     
                     
           
             
             
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                MainFrame.bslog(s);
                message = new String[]{"1", "Cannot commit PayRoll"};
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
        
        
        return message;
    }
    
    
     public void updateClockRecords(String empnbr, String fromdate, String todate, String checknbr) {
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
                String blanket = "";
                st.executeUpdate("update time_clock set " +
                        " ispaid = '1', " +
                        " checknbr = " + "'" +  checknbr  + "'" + 
                              " where emp_nbr = " + "'" + empnbr + "'" +
                              "and indate >= " + "'" + fromdate + "'" +
                               "and indate <= " + "'" + todate + "'" + 
                               ";" );
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
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
      
     public void getEarnings(String empnbr, String fromdate, String todate) {
          modelearnings.setNumRows(0);
          jtpEarnings.setText("");
          jtpEarnings.setContentType("text/html");
        
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
                String html = "<html><body><table><tr><td align='right' style='color:blue;font-size:20px;'>Earnings:</td><td></td></tr></table>";
                String codedesc = "";
                 res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs', t.code_id as 't.code_id', " +
                           " t.emp_nbr as 't.emp_nbr',  " +
                           " e.emp_rate as 'e.emp_rate', clc_desc " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr inner join clock_code on clc_code = t.code_id " +
                              " where t.emp_nbr = "  + "'" + empnbr + "'" +
                           " and t.indate >= " + "'" + fromdate + "'" +
                           " and t.indate <= " + "'" + todate + "'" + 
                                " group by t.code_id, t.emp_nbr, e.emp_rate, clc_desc " +       
                                " order by t.code_id " +      
                               ";" );
                 html += "<table>";
                String amtstring = "";
                while (res.next()) {
                    codedesc = res.getString("t.code_id");
                    if (codedesc.equals("00") || codedesc.equals("77")) {
                        codedesc = "Compensation (" + codedesc + "):";
                    } else {
                        codedesc = res.getString("clc_desc");
                    }
                    html += "<tr><td align='right'>" + codedesc + ":" + "</td><td>" + currformatDouble(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate")) + "</td></tr>";
                amtstring = currformatDouble(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"));
                modelearnings.addRow(new Object []{empnbr,
                                            "earnings",
                                            res.getString("t.code_id"),
                                            res.getString("clc_desc"),
                                            res.getString("e.emp_rate").replace('.',defaultDecimalSeparator),
                                            amtstring.replace('.',defaultDecimalSeparator)
                                            } );
                
                }
             html += "</table></body></html>";
              jtpEarnings.setText(html);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
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
  
     public void getDeductions(String empnbr, double amount) {
         
          modeldeduct.setNumRows(0);
          jtpDeductions.setText("");
          jtpDeductions.setContentType("text/html");
          StyledDocument doc = jtpDeductions.getStyledDocument();
          SimpleAttributeSet keyWord = new SimpleAttributeSet();
          StyleConstants.setForeground(keyWord, Color.RED);
          StyleConstants.setBackground(keyWord, Color.YELLOW);
          StyleConstants.setBold(keyWord, true);
          double empexception = 0;
        
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
                String html = "<html><body><table><tr><td align='right' style='color:blue;font-size:20px;'>Deductions:</td><td></td></tr></table>";
                res = st.executeQuery("SELECT paypd_desc, paypd_id, paypd_parentcode, paypd_amt from pay_profdet inner join " +
                             " emp_mstr on emp_profile = paypd_parentcode " +
                              " where emp_nbr = " + "'" + empnbr + "'" +
                              " order by paypd_desc " +        
                               ";" );
                
                html += "<table>";
                while (res.next()) {
                    html += "<tr><td align='right'>" + res.getString("paypd_desc") + ":" + "</td><td>" + currformatDouble(amount * (res.getDouble("paypd_amt") / 100)) + "</td></tr>";
                // doc.insertString(doc.getLength(), res.getString("paypd_desc") + ":\t", null );
                // doc.insertString(doc.getLength(), currformatDouble(amount * res.getDouble("paypd_amt")) + "\n", null );
                // "EmpID", "type", "code", "desc", "rate", "amt"
                 modeldeduct.addRow(new Object []{empnbr,
                                            "deduction",
                                            "",
                                            res.getString("paypd_parentcode"),
                                            res.getString("paypd_id"),
                                            res.getString("paypd_desc"),
                                            res.getString("paypd_amt").replace('.',defaultDecimalSeparator),
                                            currformatDouble(amount * (res.getDouble("paypd_amt") / 100)).replace('.',defaultDecimalSeparator)
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
                    html += "<tr><td align='right'>" + res.getString("empx_desc") + ":" + "</td><td>" + currformatDouble(empexception) + "</td></tr>";
                // doc.insertString(doc.getLength(), res.getString("paypd_desc") + ":\t", null );
                // doc.insertString(doc.getLength(), currformatDouble(amount * res.getDouble("paypd_amt")) + "\n", null ); 
                
                    modeldeduct.addRow(new Object []{empnbr,
                                            "deduction",
                                            "",
                                            "",  // profile
                                            "",  // profile line
                                            res.getString("empx_desc"),
                                            res.getString("empx_amt").replace('.',defaultDecimalSeparator),
                                            empexception
                                            } );
                }
                
                
                html += "</table></body></html>";
              jtpDeductions.setText(html);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
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
     
    /**
     * Creates new form ScrapReportPanel
     */
    public PayRollMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public void getdetail(String empnbr, String fromdate, String todate) {
      
         modeldetail.setNumRows(0);
         double totalsales = 0;
         double totalqty = 0;
        
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
                String ispaid = isnew ? "0" : "1";
                
                 
                res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj' FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr = " + "'" + empnbr + "'" +
                              " and t.indate >= " + "'" + fromdate + "'" +
                               " and t.indate <= " + "'" + todate + "'" + 
                               " and t.ispaid =  " + "'" + ispaid + "'" +       
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
                                            res.getString("t.tothrs").replace('.',defaultDecimalSeparator)
                                            } );
                    
                    
                }
                               
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
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
    
    public boolean getPaymentBatch(String batch) {
        boolean myreturn = true;
         try{
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                int i = 0;
                   double amount = 0;
                   tbid.setText(batch);
                       res = st.executeQuery(" select * from pay_mstr p inner join pay_det d on d.pyd_id = p.py_id " +
                              " where p.py_id = " + "'" + tbid.getText() + "'" + ";");
                             
                     
                    while (res.next()) {
                        i++;
                        amount += res.getDouble("pyd_payamt"); 
                          mymodel.addRow(new Object []{BlueSeerUtils.clickflag, res.getString("pyd_id"),
                                            res.getString("pyd_empnbr"),
                                            res.getString("pyd_emplname"),
                                            res.getString("pyd_empfname"),
                                            res.getString("pyd_empmname"),
                                            res.getString("pyd_empdept"),
                                            res.getString("pyd_empshift"),
                                            res.getString("pyd_empsupervisor"),
                                            res.getString("pyd_emptype"),
                                            res.getString("pyd_payprofile"),
                                            res.getString("pyd_empjobtitle"),
                                            res.getString("pyd_emprate").replace('.',defaultDecimalSeparator),
                                            res.getString("pyd_tothours").replace('.',defaultDecimalSeparator),
                                            res.getString("pyd_payamt").replace('.',defaultDecimalSeparator),
                                            res.getString("pyd_paydate")
                                            } );
                    }
                    
                    tbtotpayroll.setText(String.valueOf(currformatDouble(amount)));
                    
                    if (i > 0) {
                        btcommit.setEnabled(false);
                        btrun.setEnabled(false);
                    } else {
                        myreturn = false;
                    }
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
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
        return myreturn;
    }
    
     public boolean processNACHAFile(String batch) throws MalformedURLException, SmbException, IOException {
        boolean myreturn = true;
         try{
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try{
                java.util.Date now = new java.util.Date();
                DateFormat dfdatetm = new SimpleDateFormat("yyMMddhhmm");
                DateFormat dfdate = new SimpleDateFormat("yyMMdd");
                DateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");
                
                int i = 0;
                   double amount = 0;
                   String hrec = "";
                   String brec = "";
                   String drec = "";
                   ArrayList<String> drecs = new ArrayList<String>();
                   String btrl = "";
                   String htrl = "";
                   String bankname = "";
                   String bankroute = "";
                   String bankacct = "";
                   String bankassignedID = "";
                   String companyname = "";
                   String paydate = "";
                   
                   
                   // get header info from payroll batch
                       res = st.executeQuery(" select * from pay_mstr p inner join bk_mstr on bk_id = py_bank inner join site_mstr on site_site = py_site " +
                              " where p.py_id = " + "'" + tbid.getText() + "'" + ";");
                        while (res.next()) {
                            bankroute = res.getString("bk_route");
                            bankacct = res.getString("bk_acct");
                            bankassignedID = res.getString("bk_assignedID");
                            bankname = res.getString("bk_desc");
                            companyname = res.getString("site_desc");
                            java.util.Date pd = dfd.parse(res.getString("py_paydate"));
                            paydate = dfdate.format(pd);
                        }
                       
                        
                        hrec = "1" +  // rec type code
                               "01" +  // priority code
                               String.format("%10s", bankroute) +   // bank ABA routing...typically 9 digit routing with space in front
                               String.format("%10s", bankassignedID) + // bank assigned ID...typically 9 digit tax id with '1' in front
                                dfdatetm.format(now) +  // YYMMDDHHMM format  ...date and time fields combined here
                                "0" +  // File ID modifier
                                "094" + // Record Size
                                "10" +  // Blocking Factor
                                "1" +  // format code
                                String.format("%-23s", bankname) +   // bank name
                                String.format("%-23s", companyname) +  // company name
                                String.format("%-8s", "");  // unused
                       
                   
                        brec = "5" +  // rec type code
                               "225" +  // service class code   200 mixed CR/DR, 220 CR only, 225 DR only
                               String.format("%-16s", companyname) +  // company name
                               String.format("%20s", "") +   // company discretionary data
                               String.format("%10s", bankassignedID) + // bank assigned ID...typically 9 digit tax id with '1' in front
                               "PPD" + // Standard Entry Code  PPD, CCD, CTX
                               String.format("%10s", "PAYROLL") +   // transaction description 
                               String.format("%6s", "") + //  company descriptive date (empty)
                               paydate +  // YYMMDD  format  ...effective post date
                               String.format("%3s", "") + //  Settlement Date...done by ACH 
                                "1" +  // originator status code
                                String.format("%-8s", bankroute.substring(0, 8)) +   // bank ABA routing...first 8 digits of routing
                                String.format("%-7s", tbid.getText());  // our batch ID
                        
                        
                        
                        
                       res = st.executeQuery(" select * from pay_mstr p inner join pay_det d on d.pyd_id = p.py_id " +
                              " inner join emp_mstr on emp_nbr = pyd_empnbr " +
                              " where p.py_id = " + "'" + tbid.getText() + "'" + ";");
                             
                    int abasum = 0; 
                    while (res.next()) {
                        i++;
                        amount += res.getDouble("pyd_payamt");
                           String tracenumber = res.getString("emp_routing").substring(0, 8) + String.format("%09d", i);
                           String aba = res.getString("emp_routing").substring(0, 8);
                           String checkdigit = res.getString("emp_routing").substring(8);
                           abasum += Integer.valueOf(res.getString("emp_routing").substring(0, 8));
                           
                           drec = "6" +  // rec type code
                               "CC" +  // transaction code
                               aba + // routing...first 8 digits
                               checkdigit + // 9th char of routing
                               String.format("%-17s", res.getString("emp_acct")) +   // receiver's bank account
                               String.format("%10s", res.getString("pyd_payamt")) + // payment amount
                               String.format("%-15s", res.getString("pyd_checknbr")) +   // originator ID number 
                               String.format("%-22s", res.getString("pyd_empfname") + " " + res.getString("pyd_emplname")) +   // originator ID number     
                               "  " + // Discretionary two digit....two blank spaces
                               "0" + // Addenda Record Indicator....0 = no addenda...1 = addenda added  
                               tracenumber ;
                           drecs.add(drec);
                    }
                    
                    
                    btrl = "8" +  // rec type code
                               "225" +  // service class code   200 mixed CR/DR, 220 CR only, 225 DR only
                               String.format("%06d", i) + // total number of detail records
                               String.valueOf(String.format("%010d", abasum)) + //  sum of 8 digit ABA numbers of all details 
                               String.format("%012d", 0) + // total dollars of debit....leave 0
                               String.format("%012d", 0) + // total dollars of credit .... leave 0 
                               String.format("%10s", bankassignedID) + // bank assigned ID...typically 9 digit tax id with '1' in front
                               String.format("%19s", "") + //  message auth code leave blank 
                               String.format("%6s", "") + //  reserved....leave blank
                               String.format("%8s", bankroute.substring(0, 8)) +   // bank ABA routing...typically 9 digit routing with space in front 
                               String.format("%-7s", tbid.getText());  // our batch ID
                               
                    htrl = "9" +  // rec type code
                               String.format("%6s", "1") +  // batch  count .... only 1
                               String.format("%-6s", "") +  // block  count .... blank
                               String.format("%08d", i) + // total number of detail records 
                               String.valueOf(String.format("%010d", abasum)) + //  sum of 8 digit ABA numbers of all details 
                               String.format("%012d", 0) + // total dollars of debit....leave 0
                               String.format("%012d", 0) + // total dollars of credit .... leave 0 
                               String.format("%-39s", "");  // reserved 
                               
                
                     // now create file
                    String filecontent = hrec + "\n";
                    filecontent +=  brec + "\n";
                    for (String d : drecs) {
                        filecontent += d + "\n";
                    }
                    filecontent += btrl + "\n";
                    filecontent += htrl + "\n";
                    
                    
                   
                    
 
    NtlmPasswordAuthentication auth = NtlmPasswordAuthentication.ANONYMOUS;
    // if samba is used filepath should be something akin to smb://10.10.1.1/somepath/somedir/ + filename
   // SmbFile folder = new SmbFile("smb://10.17.2.55/edi/", auth);
       String dir = cleanDirString(EDData.getEDIOutDir());
       String filename = "nacha" + "_" + dfdatetm.format(now) + ".txt";
    
    
    Path path = Paths.get(dir + filename);
    
    
    BufferedWriter output;
    
         if (path.startsWith("smb")) {
         output = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(new SmbFile(path.toString(), auth))));
         output.write(filecontent);
         output.close();
         } else {
         output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path.toFile())));
         output.write(filecontent);
         output.close();  
         }
   // SmbFile[] listOfFiles = folder.listFiles();
                    
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 myreturn = false;
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
            myreturn = false;
            
        }
       
        return myreturn;
    }
    
    
    public void disableAll() {
         btnew.setEnabled(false);
         btlookup.setEnabled(false);
        btcommit.setEnabled(false);
         btrun.setEnabled(false);
        btcsv.setEnabled(false);
        btdetail.setEnabled(false);
        btnacha.setEnabled(false);
        ddsite.setEnabled(false);
        dcfrom.setEnabled(false);
        dcto.setEnabled(false);
        dcpay.setEnabled(false);
        tbcomments.setEnabled(false);
        tbchecknbr.setEnabled(false);
        ddbank.setEnabled(false);
        cbsalary.setEnabled(false);
    }
    
    public void enableAll() {
        btlookup.setEnabled(true);
        btnew.setEnabled(true);
        btcommit.setEnabled(true);
        btnacha.setEnabled(true);
        btrun.setEnabled(true);
        btcsv.setEnabled(true);
        btdetail.setEnabled(true);
        ddsite.setEnabled(true);
        dcfrom.setEnabled(true);
        dcto.setEnabled(true);
        dcpay.setEnabled(true);
        tbcomments.setEnabled(true);
        tbchecknbr.setEnabled(true);
        ddbank.setEnabled(true);
        cbsalary.setEnabled(true);
        
    }
    
    public void clearAll() {
        
        isnew = false;
        
        cbsalary.setSelected(true);
        tbid.setText("");
        tbcomments.setText("");
        tbchecknbr.setText("");
        tbtotpayroll.setText("0");
        java.util.Date now = new java.util.Date();
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
        dcfrom.setDate(firstday);
        dcto.setDate(now);
        dcpay.setDate(now);
               
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
        
         ddbank.removeAllItems();
        ArrayList<String> bank = OVData.getbanklist();
        for (String code : bank) {
            ddbank.addItem(code);
        }
        ddbank.setSelectedItem(OVData.getDefaultAPBank());
        
        
        
        ArrayList<String> mylist = new ArrayList<String>();
        ddsite.removeAllItems();
        mylist = OVData.getSiteList();
        for (String code : mylist) {
            ddsite.addItem(code);
        }
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
        ddempfrom.removeAllItems();
        ArrayList<String> emps = hrmData.getempmstrlist();
        for (String emp : emps) {
            ddempfrom.addItem(emp);
        }
        
        ddempto.removeAllItems();
        for (String emp : emps) {
            ddempto.addItem(emp);
        }
        
        
         if (ddempfrom.getItemCount() > 0)
        ddempfrom.setSelectedIndex(0);
        
        if (ddempto.getItemCount() > 0)
        ddempto.setSelectedIndex(ddempto.getItemCount() - 1);
        
    }
    
    public boolean validateInput() {
        boolean myreturn = true;
        
        if (tbchecknbr.getText().isEmpty()) {
            bsmf.MainFrame.show(getMessageTag(1053));
            tbchecknbr.requestFocus();
            myreturn = false;
        }
        
        return myreturn;
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
       
         clearAll(); 
         disableAll();
         
         
          if (arg != null && arg.length > 0) {
            boolean gotIt = getPaymentBatch(arg[0].toString());
            if (gotIt) {
              tbid.setEditable(false);
              tbid.setForeground(Color.blue);
             } 
        } else {
              disableAll();
              tbid.setEnabled(true);
              tbid.setEditable(true);
              
          }
         
           btnew.setEnabled(true);
           btlookup.setEnabled(true);
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getPayRollBrowseUtil(luinput.getText(),0, "py_id");
        } else {
         luModel = DTData.getPayRollBrowseUtil(luinput.getText(),0, "py_desc");   
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
                initvars(new String[]{target.getValueAt(row,1).toString(), target.getValueAt(row,2).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
        
        
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
        btcsv = new javax.swing.JButton();
        tbid = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnew = new javax.swing.JButton();
        btcommit = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        tbcomments = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        dcpay = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        tbchecknbr = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        btnacha = new javax.swing.JButton();
        ddbank = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cbsalary = new javax.swing.JCheckBox();
        btlookup = new javax.swing.JButton();
        ddempfrom = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        ddempto = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tbtotpayroll = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1239, 564));

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
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btrun.setText("Run");
        btrun.setName("btrun"); // NOI18N
        btrun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btrunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To Date:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        btcsv.setText("CSV");
        btcsv.setName("btcsv"); // NOI18N
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        tbid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbidActionPerformed(evt);
            }
        });

        jLabel7.setText("ID:");
        jLabel7.setName("lblid"); // NOI18N

        btnew.setText("New");
        btnew.setName("btnew"); // NOI18N
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel1.setText("Site:");
        jLabel1.setName("lblsite"); // NOI18N

        jLabel2.setText("Comments:");
        jLabel2.setName("lblcomments"); // NOI18N

        dcpay.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("PayDate:");
        jLabel3.setName("lblpaydate"); // NOI18N

        jLabel4.setText("Start CheckNbr:");
        jLabel4.setName("lblstartchecknbr"); // NOI18N

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        btnacha.setText("NACHA");
        btnacha.setName("btnacha"); // NOI18N
        btnacha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnachaActionPerformed(evt);
            }
        });

        jLabel8.setText("Bank:");
        jLabel8.setName("lblbank"); // NOI18N

        cbsalary.setText("Salaried");
        cbsalary.setName("cbsalaried"); // NOI18N

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        jLabel9.setText("From EmpID:");

        jLabel10.setText("To: EmpID:");

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
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbid)
                            .addComponent(ddsite, 0, 86, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(btnew)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btrun)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(ddbank, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel10))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cbsalary)
                                        .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btcommit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnacha)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcpay, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbcomments, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tbchecknbr, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 50, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(btnew)
                        .addComponent(btdetail)
                        .addComponent(btcsv)
                        .addComponent(btcommit)
                        .addComponent(btclear)
                        .addComponent(btnacha))
                    .addComponent(btlookup))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(ddbank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(cbsalary))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddempfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddempto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcomments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(dcpay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbchecknbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btrun)
                .addContainerGap())
        );

        tbtotpayroll.setText("0");

        jLabel11.setText("Total PayRoll:");
        jLabel11.setName("lbltotal"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 91, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
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
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
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
        java.util.Date now = new java.util.Date();
        
      
        
         // if lastdayofmonth >= today and less than nowplus7   --- monthly
         // if 15th >= today and less than nowplus7  --- midmonth
         
         mymodel.setRowCount(0);
         try{
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            Statement st2 = con.createStatement();
            ResultSet res2 = null;
            try{
             
                   double amount = 0;
                   double hours = 0;
                   String ispaid = isnew ? "0" : "1";
               
                   // Collect hourly/timepunchers first
                       res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs',  " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', e.emp_mname as 'e.emp_mname', e.emp_jobtitle as 'e.emp_jobtitle', " +
                           " e.emp_supervisor as 'e.emp_supervisor', e.emp_type as 'e.emp_type', e.emp_shift as 'e.emp_shift', e.emp_profile as 'e.emp_profile', e.emp_dept as 'e.emp_dept', e.emp_rate as 'e.emp_rate' " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr " +
                              " where t.indate >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                               " and t.indate <= " + "'" + dfdate.format(dcto.getDate()) + "'" + 
                               " and t.emp_nbr >= " + "'" + ddempfrom.getSelectedItem().toString() + "'" +
                               " and t.emp_nbr <= " + "'" + ddempto.getSelectedItem().toString() + "'" +        
                                " and t.ispaid =  " + "'" + ispaid + "'" +      
                                " group by t.emp_nbr, e.emp_lname, e.emp_fname, e.emp_mname, e.emp_jobtitle, e.emp_supervisor, e.emp_type, e.emp_shift, e.emp_profile, e.emp_dept, e.emp_rate " +       
                                " order by t.emp_nbr " +      
                               ";" );
                     
                    while (res.next()) {
                        amount = res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"); 
                          mymodel.addRow(new Object []{BlueSeerUtils.clickflag, "",
                                            res.getString("t.emp_nbr"),
                                            res.getString("e.emp_lname"),
                                            res.getString("e.emp_fname"),
                                            res.getString("e.emp_mname"),
                                            res.getString("e.emp_dept"),
                                            res.getString("e.emp_shift"),
                                            res.getString("e.emp_supervisor"),
                                            res.getString("e.emp_type"),
                                            res.getString("e.emp_profile"),
                                            res.getString("e.emp_jobtitle"),
                                            res.getString("e.emp_rate").replace('.',defaultDecimalSeparator),
                                            res.getString("t.tothrs").replace('.',defaultDecimalSeparator),
                                            String.valueOf(amount).replace('.',defaultDecimalSeparator),
                                            dfdate.format(dcpay.getDate())
                                            } );
                    }
                    
                    // now collect Salaried personnel if they fall within the 7 day window
                   
                    
                    if (cbsalary.isSelected()) {
                    res = st.executeQuery("SELECT * from emp_mstr " +
                              " where emp_type = 'Salary' " +
                               " and emp_active = '1' " +
                              " and emp_nbr >= " + "'" + ddempfrom.getSelectedItem().toString() + "'" +
                              " and emp_nbr <= " + "'" + ddempto.getSelectedItem().toString() + "'" +
                               ";" );
                    
                     while (res.next()) {
                         amount = 0;
                         hours = 0;
                         java.util.Date paydate = OVData.getPayWindowForSalary(res.getString("emp_payfrequency"), dcpay.getDate());
                         if (paydate == null ) {
                             continue;
                         }
                        
                         if (res.getString("emp_payfrequency").equals("monthly")) {
                             amount = res.getDouble("emp_rate") * 40 * 4; 
                             hours = 160;
                         }
                         if (res.getString("emp_payfrequency").equals("bi-monthly")) {
                             amount = res.getDouble("emp_rate") * 40 * 2; 
                             hours = 160;
                         }
                         if (res.getString("emp_payfrequency").equals("weekly")) {
                             amount = res.getDouble("emp_rate") * 40; 
                             hours = 40;
                         }
                         
                         // now confirm that it hasn't been paid already
                           res2 = st2.executeQuery("select pyd_paydate from pay_det where pyd_empnbr =  " + "'" + res.getString("emp_nbr") + "'" +
                                   " and pyd_paydate = " + "'" + dfdate.format(paydate) + "'" + ";");
                           int z = 0;
                           while (res2.next()) {
                            z++; 
                           }
                           if (z > 0)
                               continue;
                           
                           
                          mymodel.addRow(new Object []{BlueSeerUtils.clickflag, "",
                                            res.getString("emp_nbr"),
                                            res.getString("emp_lname"),
                                            res.getString("emp_fname"),
                                            res.getString("emp_mname"),
                                            res.getString("emp_dept"),
                                            res.getString("emp_shift"),
                                            res.getString("emp_supervisor"),
                                            res.getString("emp_type"),
                                            res.getString("emp_profile"),
                                            res.getString("emp_jobtitle"),
                                            res.getString("emp_rate"),
                                            String.valueOf(hours).replace('.',defaultDecimalSeparator),
                                            String.valueOf(amount).replace('.',defaultDecimalSeparator),
                                            dfdate.format(paydate)
                                            } );
                    } // while
                    }  // if salaried selected
                    
           }
            catch (SQLException s){
                 MainFrame.bslog(s);
                 bsmf.MainFrame.show(getMessageTag(1016,Thread.currentThread().getStackTrace()[1].getMethodName()));
                 
            } finally {
                if (res != null) {
                    res.close();
                }
                if (res2 != null) {
                    res2.close();
                }
                if (st != null) {
                    st.close();
                }                
                if (st2 != null) {
                    st2.close();
                }
                con.close();
            }
        }
        catch (Exception e){
            MainFrame.bslog(e);
            
        }
        
       
        double totamt = 0; 
                 for (int j = 0; j < tablereport.getRowCount(); j++) {
                  totamt += bsParseDouble(tablereport.getValueAt(j, 14).toString()); 
                 }
                 tbtotpayroll.setText(String.valueOf(currformatDouble(totamt)));
          if (totamt > 0) {
              btcommit.setEnabled(true);
          } else {
              btcommit.setEnabled(false);
          }      
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
                getDeductions(tablereport.getValueAt(row, 2).toString(), bsParseDouble(tablereport.getValueAt(row, 14).toString()));
                getEarnings(tablereport.getValueAt(row, 2).toString(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()) );
        }
    }//GEN-LAST:event_tablereportMouseClicked

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
      if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_btcsvActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
          if (!validateInput()) {
             return;
         }
        BlueSeerUtils.startTask(new String[]{"","Committing..."});
        disableAll();
        Task task = new Task("add");
        task.execute();   
    }//GEN-LAST:event_btcommitActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
       
        initvars(null);
        
         isnew = true;
         tbid.setText(String.valueOf(OVData.getNextNbr("payroll")));
         tbid.setEditable(false);
         tbid.setForeground(Color.blue);
                
              
                enableAll();
               
                btnew.setEnabled(false);
                btcommit.setEnabled(false);
               
        
    }//GEN-LAST:event_btnewActionPerformed

    private void tbidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbidActionPerformed
          boolean gotIt = getPaymentBatch(tbid.getText());
        if (gotIt) {
          tbid.setEditable(false);
          tbid.setForeground(Color.blue);
        } else {
            tbid.setForeground(Color.red);
        }
    }//GEN-LAST:event_tbidActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
       initvars(null);
    }//GEN-LAST:event_btclearActionPerformed

    private void btnachaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnachaActionPerformed
        try {
           boolean r = processNACHAFile(tbid.getText());
           if (r) {
               bsmf.MainFrame.show(getMessageTag(1054));
           }
        } catch (SmbException ex) {
            ex.printStackTrace();
            bsmf.MainFrame.show(getMessageTag(1055, Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (IOException ex) {
            ex.printStackTrace();
            bsmf.MainFrame.show(getMessageTag(1056, Thread.currentThread().getStackTrace()[1].getMethodName()));
        }
    }//GEN-LAST:event_btnachaActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btnacha;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btrun;
    private javax.swing.JCheckBox cbsalary;
    private javax.swing.JPanel chartpanel;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcpay;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddbank;
    private javax.swing.JComboBox<String> ddempfrom;
    private javax.swing.JComboBox<String> ddempto;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JTextField tbid;
    private javax.swing.JLabel tbtotpayroll;
    // End of variables declaration//GEN-END:variables
}
