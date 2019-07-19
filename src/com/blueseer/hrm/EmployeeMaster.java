/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blueseer.hrm;

import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author vaughnte
 */
public class EmployeeMaster extends javax.swing.JPanel {

      javax.swing.table.DefaultTableModel excmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Type", "Desc", "AmountType", "Amt"
            });
    
      
       javax.swing.table.DefaultTableModel mymodel =  new javax.swing.table.DefaultTableModel(new Object[][]{},
                      new String[]{"select", "RecID", "EmpNbr", "CheckNbr", "PayDate","tothrs", "GrossAmt", "Deduct", "NetAmt"})
                       {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        }; 
    
    
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"select", "ID", "Code", "InDate", "InTime", "OutDate", "OutTime", "tothrs"}){
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                        };
    javax.swing.table.DefaultTableModel modelearnings = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "desc", "rate", "amt"});
     javax.swing.table.DefaultTableModel modeldeduct = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"EmpID", "type", "code", "desc", "rate", "amt"});
      
    
    
    /**
     * Creates new form UserMaintPanel
     */
    
     public void reinitusermaintvariables() {
        
        empid.setText("");
        lastname.setText("");
        firstname.setText("");
       
        comments.setText("");
       
        middlename.setText("");
       
        btedit.setEnabled(false);
        btadd.setEnabled(true);
        setUsersTable();
        
    }
    
     public void getEarnings(String empnbr, String checknbr) {
          modelearnings.setNumRows(0);
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
                String type = "";
                
                // first determine if salary versus hourly
                res = st.executeQuery("select emp_type from emp_mstr where emp_nbr = " + "'" + empnbr + "'" + ";");
                while (res.next()) {
                    type = res.getString("emp_type");
                }
                
                
                 html += "<table>";
                
                if (type.equals("Hourly")) {
                    res = st.executeQuery("SELECT sum(t.tothrs) as 't.tothrs', t.recid as 't.recid', t.code_id as 't.code_id', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', e.emp_rate as 'e.emp_rate', clc_code, clc_desc " +
                           "  FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr inner join clock_code on clc_code = t.code_id " +
                              " where t.emp_nbr = "  + "'" + empnbr + "'" +
                           " and t.checknbr = " + "'" + checknbr + "'" +
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
                    html += "<tr><td align='right'>" + codedesc + ":" + "</td><td>" + df.format(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate")) + "</td></tr>";
                
                modelearnings.addRow(new Object []{empnbr,
                                            "earnings",
                                            res.getString("t.code_id"),
                                            res.getString("clc_desc"),
                                            res.getString("e.emp_rate"),
                                            df.format(res.getDouble("t.tothrs") * res.getDouble("e.emp_rate"))
                                            } );
                
                }
                    
                } else {
                     res = st.executeQuery("select pyd_payamt, pyd_tothours, pyd_emprate, emp_payfrequency " +
                         " from pay_det inner join emp_mstr on emp_nbr = pyd_empnbr where " +
                        " pyd_empnbr = " + "'" + empnbr + "'" + " AND pyd_checknbr = " + "'" + checknbr + "'" +
                        " order by pyd_paydate desc ;");
                      
                     while (res.next()) {
                     html += "<tr><td align='right'>" + "Salary" + ":" + "</td><td>" + df.format(res.getDouble("pyd_tothours") * res.getDouble("pyd_emprate")) + "</td></tr>";
                
                     modelearnings.addRow(new Object []{empnbr,
                                            "earnings",
                                            "Salary",
                                            res.getString("emp_payfrequency"),
                                            res.getString("pyd_emprate"),
                                            df.format(res.getDouble("pyd_payamt"))
                                            } );
                     }
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
                
                html += "<table>";
                while (res.next()) {
                    html += "<tr><td align='right'>" + res.getString("paypd_desc") + ":" + "</td><td>" + df.format(amount * (res.getDouble("paypd_amt") / 100)) + "</td></tr>";
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
     
     public void getdetail(String empnbr, String checknbr) {
      
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
                
                
                 
                res = st.executeQuery("SELECT t.tothrs as 't.tothrs', t.recid as 't.recid', " +
                           " t.emp_nbr as 't.emp_nbr', e.emp_lname as 'e.emp_lname', e.emp_fname as 'e.emp_fname', " +
                           " e.emp_dept as 'e.emp_dept', t.code_id as 't.code_id', t.indate as 't.indate', t.intime as 't.intime', " +
                           " t.intime_adj as 't.intime_adj', t.outdate as 't.outdate', t.outtime as 't.outtime', " +
                           " t.outtime_adj as 't.outtime_adj' FROM  time_clock t inner join emp_mstr e on e.emp_nbr = t.emp_nbr" +
                              " where t.emp_nbr = " + "'" + empnbr + "'" +
                              " and t.checknbr = " + "'" + checknbr + "'" +
                               " and t.ispaid =  " + "'" + "1" + "'" +       
                               " order by t.indate" +
                               ";" );
                while (res.next()) {
                  
                     modeldetail.addRow(new Object []{BlueSeerUtils.clickflag, res.getString("t.recid"),
                                            res.getString("t.code_id"),
                                             res.getString("t.indate"),
                                            res.getString("t.intime_adj"),
                                            res.getString("t.outdate"),
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
   
     public void getPayRecords(String empnbr) {
          try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                mymodel.setNumRows(0);
                int i = 0;
                double netcheck = 0.00;
                    res = st.executeQuery("SELECT pyd_id, pyd_empnbr, pyd_checknbr, pyd_paydate, pyd_tothours, pyd_payamt, " +
                             " (select sum(pyl_amt) from pay_line where pyl_id = pyd_id and pyl_checknbr = pyd_checknbr ) as 'deductions' " +
                            " FROM  pay_det where pyd_empnbr = " + "'" + empnbr + "'" + " order by pyd_paydate desc ;");
                    while (res.next()) {
                          netcheck = res.getDouble("pyd_payamt") - res.getDouble("deductions");
                          mymodel.addRow(new Object []{BlueSeerUtils.clickflag, res.getString("pyd_id"),
                                            res.getString("pyd_empnbr"),
                                            res.getString("pyd_checknbr"),
                                            res.getString("pyd_paydate"),
                                            res.getString("pyd_tothours"),
                                            res.getDouble("pyd_payamt"),
                                            res.getDouble("deductions"),
                                            netcheck
                                            } );
                    }
            }
            catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to retrieve employee pay record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     
     public boolean getEmployee(String empidvar) {
         boolean gotIt = false;
         
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  emp_mstr where emp_nbr = " + "'" + empidvar + "'" + ";");
                    while (res.next()) {
                        i++;
                        empid.setText(res.getString("emp_nbr"));
                        lastname.setText(res.getString("emp_lname"));
                        firstname.setText(res.getString("emp_fname")); 
                        middlename.setText(res.getString("emp_mname"));
                        dddept.setSelectedItem(res.getString("emp_dept"));
                        ddstatus.setSelectedItem(res.getString("emp_status"));
                        ddshift.setSelectedItem(res.getString("emp_shift"));
                        ddtype.setSelectedItem(res.getString("emp_type"));
                        ddstate.setSelectedItem(res.getString("emp_state"));
                        ddcountry.setSelectedItem(res.getString("emp_country"));
                        ddgender.setSelectedItem(res.getString("emp_gender"));
                        if (res.getString("emp_startdate") != null && ! res.getString("emp_startdate").isEmpty()) {
                        hiredate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("emp_startdate")));
                        }
                        if (res.getString("emp_termdate") != null && ! res.getString("emp_termdate").isEmpty()) {
                        termdate.setDate(bsmf.MainFrame.dfdate.parse(res.getString("emp_termdate")));
                        }
                        tbline1.setText(res.getString("emp_addrline1"));
                        tbline2.setText(res.getString("emp_addrline2"));
                        tbcity.setText(res.getString("emp_city"));
                        tbzip.setText(res.getString("emp_zip"));
                        tbvacdays.setText(res.getString("emp_vac_days"));
                        tbvactaken.setText(res.getString("emp_vac_taken"));
                        tbphone.setText(res.getString("emp_phone"));
                        tbemercontact.setText(res.getString("emp_emer_contact"));
                        tbemernumber.setText(res.getString("emp_emer_phone"));
                        tbssn.setText(res.getString("emp_ssn"));
                        tbrate.setText(res.getString("emp_rate"));
                        tbtitle.setText(res.getString("emp_jobtitle"));
                        tbefladays.setText(res.getString("emp_efla_days"));
                        tbprofile.setText(res.getString("emp_profile"));
                        tbaccount.setText(res.getString("emp_acct"));
                        tbroute.setText(res.getString("emp_routing"));
                        ddpayfrequency.setSelectedItem(res.getString("emp_payfrequency"));
                        tbsupervisor.setText(res.getString("emp_supervisor"));
                        cbautoclock.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("emp_autoclock")));
                        cbactive.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("emp_active")));
                        if (res.getString("emp_clockin").equals("1")) {
                        tbclockin.setText("yes");
                        } else {
                        tbclockin.setText("no");    
                        }
                        if (res.getString("emp_dob") != null && ! res.getString("emp_dob").isEmpty()) {
                        dcdob.setDate(bsmf.MainFrame.dfdate.parse(res.getString("emp_dob")));
                        }
                        //    
                    }

                if (i > 0) {
                    gotIt = true;
                    enableAll();
                    btadd.setEnabled(false);
                     
                    // lets get the exceptions specific to this employee
                     res = st.executeQuery("SELECT * FROM  emp_exception where empx_nbr = " + "'" + empidvar + "'" + ";");
                     while (res.next()) {
                      excmodel.addRow(new Object[]{ res.getString("empx_type"), res.getString("empx_desc"), res.getString("empx_amttype"), res.getString("empx_amt")
                      });
                    }
                }
                // now get payrecords
                getPayRecords(empidvar);
                
                
            }
            catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to retrieve employee record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
         
         return gotIt;
     }
     
    public void setUsersTable()
    {
         try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                int i = 0;

                javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"UserID", "LastName", "FirstName", "Email"});
              
               // jTable2.getColumn("Item").setCellRenderer(new ButtonRenderer());
             //   jTable2.getColumn("Item").setCellEditor(
               //         new ButtonEditor(new JCheckBox()));



                res = st.executeQuery("SELECT * FROM  user_mstr order by user_id ;");

                while (res.next()) {
                    i++;

                    mymodel.addRow(new Object[]{res.getString("user_id"),
                                res.getString("user_lname"),
                                res.getString("user_fname"),
                                res.getString("user_email")
                            });
                }

            } catch (SQLException s) {
                JOptionPane.showMessageDialog(mydialog, "Cannot SQL User_Mstr");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
     
           class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            //Integer percentage = (Integer) table.getValueAt(row, 3);
            //if (percentage > 30)
           // if (table.getValueAt(row, 0).toString().compareTo("1923") == 0)   
            if (column == 0)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
    public EmployeeMaster() {
        initComponents();
        

    }

    public void enableAll() {
        empid.setEnabled(true);
        tbprofile.setEnabled(true);
        tbzip.setEnabled(true);
        hiredate.setEnabled(true);
        termdate.setEnabled(true);
        dcdob.setEnabled(true);
        empid.setEnabled(true);
        tbline1.setEnabled(true);
        tbline2.setEnabled(true);
        tbphone.setEnabled(true);
        tbemercontact.setEnabled(true);
        tbemernumber.setEnabled(true);
        tbssn.setEnabled(true);
        tbrate.setEnabled(true);
        tbvacdays.setEnabled(true);
        tbefladays.setEnabled(true);
        tbvactaken.setEnabled(true);
        ddgender.setEnabled(true);
        ddtype.setEnabled(true);
        tbtitle.setEnabled(true);
        lastname.setEnabled(true);
        firstname.setEnabled(true);
        middlename.setEnabled(true);
        comments.setEnabled(true);
        tbclockin.setEnabled(true);
        tbsupervisor.setEnabled(true);
        tbclockin.setEnabled(true);
        cbautoclock.setSelected(true);
         btedit.setEnabled(true);
         btdelete.setEnabled(true);
         btadd.setEnabled(true);
         btempfname.setEnabled(true);
         btemplname.setEnabled(true);
         btempbrowse.setEnabled(true);
         btnew.setEnabled(true);
         dddept.setEnabled(true);
         ddshift.setEnabled(true);
         ddstate.setEnabled(true);
         ddcountry.setEnabled(true);
         ddstatus.setEnabled(true);
         tbcity.setEnabled(true);
         cbactive.setEnabled(true);
         ddpayfrequency.setEnabled(true);
         tbaccount.setEnabled(true);
         tbroute.setEnabled(true);
    }
    public void disableAll() {
        empid.setEnabled(false);
        tbzip.setEnabled(false);
        tbprofile.setEnabled(false);
        hiredate.setEnabled(false);
        termdate.setEnabled(false);
        dcdob.setEnabled(false);
        empid.setEnabled(false);
        tbline1.setEnabled(false);
        tbline2.setEnabled(false);
        tbphone.setEnabled(false);
        tbemercontact.setEnabled(false);
        tbemernumber.setEnabled(false);
        tbssn.setEnabled(false);
        tbrate.setEnabled(false);
        tbvacdays.setEnabled(false);
        tbefladays.setEnabled(false);
        tbvactaken.setEnabled(false);
        ddgender.setEnabled(false);
        ddtype.setEnabled(false);
        tbtitle.setEnabled(false);
        lastname.setEnabled(false);
        firstname.setEnabled(false);
        middlename.setEnabled(false);
        comments.setEnabled(false);
        tbclockin.setEnabled(false);
        tbsupervisor.setEnabled(false);
        tbclockin.setEnabled(false);
        cbautoclock.setSelected(false);
         btedit.setEnabled(false);
         btdelete.setEnabled(false);
         btadd.setEnabled(false);
         btempfname.setEnabled(false);
         btemplname.setEnabled(false);
         btempbrowse.setEnabled(false);
         btnew.setEnabled(false);
         dddept.setEnabled(false);
         ddshift.setEnabled(false);
         ddstate.setEnabled(false);
         ddcountry.setEnabled(false);
         ddstatus.setEnabled(false);
         tbcity.setEnabled(false);
         cbactive.setEnabled(false);
         ddpayfrequency.setEnabled(false);
         tbaccount.setEnabled(false);
         tbroute.setEnabled(false);
    }
    public void clearAll() {
        
        
        empid.setText("");
        empid.setEditable(true);
        empid.setForeground(Color.black);
        
        tbzip.setText("");
        hiredate.setDate(null);
        termdate.setDate(null);
        dcdob.setDate(null);
        empid.setEnabled(true);
        tbprofile.setText("");
        tbline1.setText("");
        tbline2.setText("");
        tbphone.setText("");
        tbemercontact.setText("");
        tbemernumber.setText("");
        tbssn.setText("");
        tbrate.setText("0");
        tbrate.setBackground(Color.white); 
        tbvacdays.setText("0");
        tbefladays.setText("0");
        tbvactaken.setText("0");
        ddgender.setSelectedIndex(0);
        ddtype.setSelectedIndex(0);
        tbtitle.setText("");
        lastname.setText("");
        firstname.setText("");
        middlename.setText("");
        comments.setText("");
        tbclockin.setText("");
        tbsupervisor.setText("");
        tbaccount.setText("");
        tbroute.setText("");
        ddpayfrequency.setSelectedIndex(0);
        
        cbautoclock.setSelected(false);
       
        
        dddept.removeAllItems();
        ArrayList<String> mylist = new ArrayList();
        mylist = OVData.getdeptidlist();
        for (String dept : mylist) {
            dddept.addItem(dept);
        }
       
        
        ddshift.removeAllItems();
        ArrayList<String> shifts = new ArrayList();
        shifts = OVData.getShiftCodes();
        for (String shift : shifts) {
            ddshift.addItem(shift);
        }
        if (ddshift.getItemCount() == 0) {
            ddshift.addItem("NotDef");
        }
        
        
         if (ddstate.getItemCount() == 0) {
       for (int i = 0; i < OVData.states.length; i++) {
            ddstate.addItem(OVData.states[i]);
        }
         } else {
             ddstate.setSelectedIndex(0);
         }
       if (ddcountry.getItemCount() == 0) {
       for (int i = 0; i < OVData.countries.length; i++) {
            ddcountry.addItem(OVData.countries[i]);
        } 
       } else {
           ddcountry.setSelectedIndex(0);
       } 
    }
    
    public void initvars(String arg) {
        
        
          jTabbedPane1.removeAll();
        jTabbedPane1.add("Main", jPanelMain);
        jTabbedPane1.add("PaySetting", jPanelPay);
         jTabbedPane1.add("PayHistory", jPanelHistory);
        
         
          mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
        detailpanel.setVisible(false);
        chartpanel.setVisible(false);
        
         tablereport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
         tablereport.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
         tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
        
         excmodel.setRowCount(0);
        exctable.setModel(excmodel);
        
       // jTabbedPane1.setEnabledAt(1, false);
        
        
          clearAll();
          disableAll();
           btempfname.setEnabled(true);
         btemplname.setEnabled(true);
         btempbrowse.setEnabled(true);
         btnew.setEnabled(true);
        
       if (! arg.isEmpty()) {
           getEmployee(arg);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        btadd = new javax.swing.JButton();
        btedit = new javax.swing.JButton();
        empid = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        comments = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        taUMperms1 = new javax.swing.JTextArea();
        jLabel15 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbline1 = new javax.swing.JTextField();
        tbphone = new javax.swing.JTextField();
        tbline2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tbemercontact = new javax.swing.JTextField();
        tbcity = new javax.swing.JTextField();
        tbemernumber = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tbzip = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddstate = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ddcountry = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        dcdob = new com.toedter.calendar.JDateChooser();
        termdate = new com.toedter.calendar.JDateChooser();
        firstname = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        tbvacdays = new javax.swing.JTextField();
        lastname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        tbtitle = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        middlename = new javax.swing.JTextField();
        tbvactaken = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        hiredate = new com.toedter.calendar.JDateChooser();
        tbsupervisor = new javax.swing.JTextField();
        tbclockin = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cbautoclock = new javax.swing.JCheckBox();
        btemplname = new javax.swing.JButton();
        btempfname = new javax.swing.JButton();
        cbactive = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        dddept = new javax.swing.JComboBox();
        jLabel54 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        tbssn = new javax.swing.JTextField();
        ddshift = new javax.swing.JComboBox();
        ddgender = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        ddstatus = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        tbefladays = new javax.swing.JTextField();
        btdelete = new javax.swing.JButton();
        btempbrowse = new javax.swing.JButton();
        btnew = new javax.swing.JButton();
        jPanelPay = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        exctable = new javax.swing.JTable();
        tbexcamt = new javax.swing.JTextField();
        tbexcdesc = new javax.swing.JTextField();
        percentlabel = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        btexcadd = new javax.swing.JButton();
        btexcdelete = new javax.swing.JButton();
        ddexcamttype = new javax.swing.JComboBox<>();
        ddexctype = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        ddpaytype = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        tbroute = new javax.swing.JTextField();
        tbaccount = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tbrate = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ddtype = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        tbprofile = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        ddpayfrequency = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jPanelHistory = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        btsummary = new javax.swing.JButton();
        chartpanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtpEarnings = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtpDeductions = new javax.swing.JTextPane();

        setBackground(new java.awt.Color(0, 102, 204));

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        add(jTabbedPane1);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder("Employee Maintenance"));

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        empid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empidActionPerformed(evt);
            }
        });

        jLabel46.setText("Employee Number");

        comments.setColumns(20);
        comments.setRows(5);
        jScrollPane6.setViewportView(comments);

        taUMperms1.setColumns(20);
        taUMperms1.setRows(5);
        jScrollPane7.setViewportView(taUMperms1);

        jLabel15.setText("Comments");

        jLabel10.setText("State");

        jLabel7.setText("City");

        jLabel12.setText("Phone");

        jLabel8.setText("ZipCode");

        jLabel17.setText("Emergency Number");

        jLabel6.setText("Addrline2");

        jLabel11.setText("Country");

        jLabel16.setText("Emergency Contact");

        jLabel5.setText("Addrline1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbline1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbline2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(tbcity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbemernumber)
                            .addComponent(tbemercontact)
                            .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbline2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbcity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbline1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbzip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbemercontact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbemernumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addContainerGap())
        );

        jLabel55.setText("Date Of Birth");

        dcdob.setDateFormatString("yyyy-MM-dd");

        termdate.setDateFormatString("yyyy-MM-dd");

        jLabel49.setText("FirstName");

        jLabel13.setText("Vacation Days");

        jLabel14.setText("Vac Days Taken");

        jLabel47.setText("LastName");

        jLabel2.setText("Middle Initial");

        jLabel56.setText("Job Title");

        jLabel50.setText("HireDate");

        jLabel51.setText("TermDate");

        hiredate.setDateFormatString("yyyy-MM-dd");

        jLabel18.setText("Supervisor");

        jLabel19.setText("Clocked In?");

        cbautoclock.setText("AutoClock");

        btemplname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btemplname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btemplnameActionPerformed(evt);
            }
        });

        btempfname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btempfname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btempfnameActionPerformed(evt);
            }
        });

        cbactive.setText("Active?");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastname, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel49)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firstname, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbvacdays, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbautoclock)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbsupervisor)
                                .addComponent(tbvactaken, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                .addComponent(tbclockin, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel55)
                            .addComponent(jLabel50)
                            .addComponent(jLabel56)
                            .addComponent(jLabel51))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbactive)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(termdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(hiredate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tbtitle)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(middlename, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(dcdob, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btemplname, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btempfname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel47))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btemplname)
                                .addGap(16, 16, 16)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(firstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel49)))
                            .addComponent(btempfname))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(middlename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbtitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                .addComponent(cbactive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50)
                    .addComponent(hiredate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(termdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dcdob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbvacdays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbvactaken, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsupervisor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbclockin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbautoclock)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jLabel9.setText("FMLA Days");

        jLabel54.setText("Gender");

        jLabel53.setText("Shift");

        ddgender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "F" }));

        jLabel1.setText("SSN");

        jLabel48.setText("Dept");

        ddstatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Active", "Inactive" }));

        jLabel52.setText("Status");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddgender, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddshift, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbssn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbefladays, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dddept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddshift, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddgender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbssn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbefladays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        btempbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btempbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btempbrowseActionPerformed(evt);
            }
        });

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(empid, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btempbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnew)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(49, 49, 49))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(empid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel46))
                    .addComponent(btempbrowse)
                    .addComponent(btnew))
                .addGap(12, 12, 12)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(63, 63, 63)
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelMainLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15))))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btadd)
                    .addComponent(btedit)
                    .addComponent(btdelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanelMain);

        jPanelPay.setBorder(javax.swing.BorderFactory.createTitledBorder("Payroll Options"));
        jPanelPay.setPreferredSize(new java.awt.Dimension(938, 622));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Exceptions"));

        exctable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(exctable);

        percentlabel.setText("Percent/Amount");

        jLabel21.setText("Desc");

        btexcadd.setText("add");
        btexcadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexcaddActionPerformed(evt);
            }
        });

        btexcdelete.setText("delete");
        btexcdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexcdeleteActionPerformed(evt);
            }
        });

        ddexcamttype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "percent", "fixed" }));

        ddexctype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "earning", "deduction" }));

        jLabel22.setText("Type");

        jLabel23.setText("AmountType");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(percentlabel)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbexcdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tbexcamt, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(btexcadd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexcdelete))
                    .addComponent(ddexcamttype, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddexctype, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddexctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbexcdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddexcamttype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btexcadd)
                    .addComponent(btexcdelete)
                    .addComponent(tbexcamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(percentlabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ddpaytype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "check", "direct deposit" }));

        jLabel24.setText("PayType");

        jLabel25.setText("Bank Routing");

        jLabel26.setText("Bank Account");

        tbrate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbrateFocusLost(evt);
            }
        });

        jLabel3.setText("Rate/hr");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hourly", "Salary", "Temp" }));

        jLabel4.setText("Type");

        jLabel20.setText("Profile");

        ddpayfrequency.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "bi-monthly", "monthly", "weekly" }));

        jLabel27.setText("Pay Frequency");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(tbprofile, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbrate)))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tbroute)
                            .addComponent(tbaccount, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(55, 55, 55))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ddpaytype, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(ddpayfrequency, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(186, 186, 186))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddpaytype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(ddpayfrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbroute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(tbrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbaccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(tbprofile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelPayLayout = new javax.swing.GroupLayout(jPanelPay);
        jPanelPay.setLayout(jPanelPayLayout);
        jPanelPayLayout.setHorizontalGroup(
            jPanelPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPayLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanelPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(95, 95, 95))
        );
        jPanelPayLayout.setVerticalGroup(
            jPanelPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPayLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(343, 343, 343))
        );

        add(jPanelPay);

        jPanelHistory.setPreferredSize(new java.awt.Dimension(938, 602));
        jPanelHistory.setLayout(new javax.swing.BoxLayout(jPanelHistory, javax.swing.BoxLayout.LINE_AXIS));

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

        jPanelHistory.add(summarypanel);

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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabledetail);

        detailpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        btsummary.setText("Back");
        btsummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btsummaryActionPerformed(evt);
            }
        });
        detailpanel.add(btsummary, java.awt.BorderLayout.PAGE_START);

        jPanelHistory.add(detailpanel);

        chartpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        chartpanel.setName(""); // NOI18N
        chartpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        chartpanel.setLayout(new javax.swing.BoxLayout(chartpanel, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane4.setViewportView(jtpEarnings);

        chartpanel.add(jScrollPane4);

        jScrollPane5.setViewportView(jtpDeductions);

        chartpanel.add(jScrollPane5);

        jPanelHistory.add(chartpanel);

        add(jPanelHistory);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        try {
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String autoclock = "";
                String isactive = "";
                
                if (cbactive.isSelected()) {
                    isactive = "1";
                } else {
                    isactive = "0";
                }
                
                if (cbautoclock.isSelected()) {
                    autoclock = "1";
                } else {
                    autoclock = "0";
                }

                 String termdatestr = Objects.toString(null);
                String hiredatestr = Objects.toString(null);
                String dobdatestr = Objects.toString(null);
                if (termdate.getDate() != null)
                    termdatestr = "'" + dfdate.format(termdate.getDate()) + "'";
                if (hiredate.getDate() != null)
                    hiredatestr = "'" + dfdate.format(hiredate.getDate()) + "'";
                if (dcdob.getDate() != null)
                    dobdatestr = "'" + dfdate.format(dcdob.getDate()) + "'";
                
                
                if (tbvacdays.getText().isEmpty())
                    tbvacdays.setText("0");
                if (tbvactaken.getText().isEmpty())
                    tbvactaken.setText("0");
                if (tbrate.getText().isEmpty())
                    tbrate.setText("0");
                if (tbefladays.getText().isEmpty())
                    tbefladays.setText("0");
                
                 if (tbprofile.getText().isEmpty()) {
                          proceed = false;
                          bsmf.MainFrame.show("Must enter valid Profile code");
                          tbprofile.requestFocus();
                          return;
                 } else {
                     if ( ! OVData.isValidProfile(tbprofile.getText())) {
                          proceed = false;
                          bsmf.MainFrame.show("Must enter valid Profile code");
                          tbprofile.requestFocus();
                          return;
                      }
                 }
                              
                
                if (proceed) {
                    st.executeUpdate("insert into emp_mstr "
                        + "(emp_nbr, emp_lname, emp_fname, "
                        + "emp_mname, emp_dept, emp_status, emp_startdate, emp_shift, emp_type, "
                            + "emp_gender, emp_jobtitle, emp_ssn, emp_autoclock, emp_active, emp_rate, emp_profile, emp_acct, emp_routing, emp_payfrequency, emp_efla_days, "
                            + "emp_vac_days, emp_vac_taken, emp_addrline1, emp_addrline2, emp_city, "
                       + "emp_state, emp_country, emp_zip, emp_phone, emp_emer_contact, emp_emer_phone, emp_dob ) "
                            + "values ( " + "'" + empid.getText().toString() + "'" + ","
                        + "'" + lastname.getText().toString() + "'" + ","
                        + "'" + firstname.getText().toString() + "'" + ","
                        + "'" + middlename.getText().toString() + "'" + ","
                        + "'" + dddept.getSelectedItem().toString() + "'" + ","
                        + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                        + hiredatestr + ","
                            + "'" + ddshift.getSelectedItem().toString() + "'" + ","
                            + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                            + "'" + ddgender.getSelectedItem().toString() + "'" + ","
                             + "'" + tbtitle.getText().toString() + "'" + ","
                             + "'" + tbssn.getText().toString() + "'" + ","
                             + "'" + autoclock + "'" + ","
                             + "'" + isactive + "'" + ","        
                             + "'" + tbrate.getText().toString() + "'" + ","
                             + "'" + tbprofile.getText().toString() + "'" + ","  
                             + "'" + tbaccount.getText().toString() + "'" + ","  
                             + "'" + tbroute.getText().toString() + "'" + ","   
                             + "'" + ddpayfrequency.getSelectedItem().toString() + "'" + ","         
                             + "'" + tbefladays.getText().toString() + "'" + ","
                             + "'" + tbvacdays.getText().toString() + "'" + ","
                             + "'" + tbvactaken.getText().toString() + "'" + ","
                             + "'" + tbline1.getText().toString() + "'" + ","
                             + "'" + tbline2.getText().toString() + "'" + ","
                             + "'" + tbcity.getText().toString() + "'" + ","
                            + "'" + ddstate.getSelectedItem().toString() + "'" + ","
                            + "'" + ddcountry.getSelectedItem().toString() + "'" + ","
                             + "'" + tbzip.getText().toString() + "'" + ","
                             + "'" + tbphone.getText().toString() + "'" + ","
                             + "'" + tbemercontact.getText().toString() + "'" + ","
                             + "'" + tbemernumber.getText().toString() + "'" + ","
                             + dobdatestr
                        + ")"
                        + ";");
                    
                      // now add employee exceptions if any
                     for (int j = 0; j < exctable.getRowCount(); j++) {
                        st.executeUpdate("insert into emp_exception "
                            + "(empx_nbr, empx_type, empx_desc, empx_amttype, empx_amt ) "
                            + " values ( " 
                            + "'" + empid.getText() + "'" + ","
                            + "'" + exctable.getValueAt(j, 0).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 3).toString().replace("'", "") + "'" 
                            + ")"
                            + ";");
                    }
                    
                    
                    initvars("");
                    bsmf.MainFrame.show("Added Employee Record");
                    
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Cannot Add Employee Mstr");
            }
            bsmf.MainFrame.con.close();
            reinitusermaintvariables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        try {
            DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String termdatestr = Objects.toString(null);
                String hiredatestr = Objects.toString(null);
                String dobdatestr = Objects.toString(null);
                if (termdate.getDate() != null)
                    termdatestr = "'" + dfdate.format(termdate.getDate()) + "'";
                if (hiredate.getDate() != null)
                    hiredatestr = "'" + dfdate.format(hiredate.getDate()) + "'";
                if (dcdob.getDate() != null)
                    dobdatestr = "'" + dfdate.format(dcdob.getDate()) + "'";
                
                 if (tbvacdays.getText().isEmpty())
                    tbvacdays.setText("0");
                if (tbvactaken.getText().isEmpty())
                    tbvactaken.setText("0");
                if (tbrate.getText().isEmpty())
                    tbrate.setText("0");
                if (tbefladays.getText().isEmpty())
                    tbefladays.setText("0");
                
                 String autoclock = "";
                 String isactive = "";
                
                if (cbautoclock.isSelected()) {
                    autoclock = "1";
                } else {
                    autoclock = "0";
                }
                
                 if (cbactive.isSelected()) {
                    isactive = "1";
                } else {
                    isactive = "0";
                }
                
                
                 if (tbprofile.getText().isEmpty()) {
                          proceed = false;
                          bsmf.MainFrame.show("Must enter valid Profile code");
                          tbprofile.requestFocus();
                          return;
                 } else {
                     if ( ! OVData.isValidProfile(tbprofile.getText())) {
                          proceed = false;
                          bsmf.MainFrame.show("Must enter valid Profile code");
                          tbprofile.requestFocus();
                          return;
                      }
                 }

                if (proceed) {
                    res = st.executeQuery("SELECT emp_nbr FROM  emp_mstr where emp_nbr = " + "'" + empid.getText().toString() + "'" + ";");
                    while (res.next()) {
                        i++;
                    }
                    if (i > 0) {
                        st.executeUpdate("update emp_mstr set "
                                + "emp_lname = " + "'" + lastname.getText().toString() + "'" + ","
                                + "emp_fname = " + "'" + firstname.getText().toString() + "'" + ","
                                + "emp_mname = " + "'" + middlename.getText().toString() + "'" + ","
                                + "emp_dept = " + "'" + dddept.getSelectedItem().toString() + "'" + ","
                                + "emp_status = " + "'" + ddstatus.getSelectedItem().toString() + "'" + ","
                                + "emp_shift = " + "'" + ddshift.getSelectedItem().toString() + "'" + ","
                                + "emp_startdate = " + hiredatestr + ","
                                + "emp_termdate = " + termdatestr + ","
                                + "emp_active = " + isactive + ","        
                                + "emp_state = " + "'" + ddstate.getSelectedItem().toString() + "'" + ","
                                + "emp_country = " + "'" + ddcountry.getSelectedItem().toString() + "'" + ","
                                + "emp_type = " + "'" + ddtype.getSelectedItem().toString() + "'" + ","
                                + "emp_gender = " + "'" + ddgender.getSelectedItem().toString() + "'" + ","
                                + "emp_addrline1 = " + "'" + tbline1.getText().toString() + "'" + ","
                                + "emp_addrline2 = " + "'" + tbline2.getText().toString() + "'" + ","
                                + "emp_city = " + "'" + tbcity.getText().toString() + "'" + ","
                                + "emp_zip = " + "'" + tbzip.getText().toString() + "'" + ","
                                + "emp_phone = " + "'" + tbphone.getText().toString() + "'" + ","
                                + "emp_emer_contact = " + "'" + tbemercontact.getText().toString() + "'" + ","
                                + "emp_emer_phone = " + "'" + tbemernumber.getText().toString() + "'" + ","
                                + "emp_efla_days = " + "'" + tbefladays.getText().toString() + "'" + ","
                                + "emp_profile = " + "'" + tbprofile.getText().toString() + "'" + ","   
                                + "emp_acct = " + "'" + tbaccount.getText().toString() + "'" + ","   
                                + "emp_routing = " + "'" + tbroute.getText().toString() + "'" + ","       
                                + "emp_payfrequency = " + "'" + ddpayfrequency.getSelectedItem().toString() + "'" + ","               
                                + "emp_vac_days = " + "'" + tbvacdays.getText().toString() + "'" + ","
                                + "emp_vac_taken = " + "'" + tbvactaken.getText().toString() + "'" + ","
                                + "emp_jobtitle = " + "'" + tbtitle.getText().toString() + "'" + ","
                                + "emp_ssn = " + "'" + tbssn.getText().toString() + "'" + ","
                                + "emp_autoclock = " + "'" + autoclock + "'" + ","
                                + "emp_rate = " + "'" + tbrate.getText().toString() + "'" + ","
                                + "emp_dob = " + dobdatestr 
                                + " where emp_nbr = " + "'" + empid.getText().toString() + "'"
                                + ";");
                        
                            // now add employee exceptions if any
                      st.executeUpdate("delete from emp_exception where empx_nbr = " + "'" + empid.getText() + "'" + ";"); 
                     for (int j = 0; j < exctable.getRowCount(); j++) {
                        st.executeUpdate("insert into emp_exception "
                            + "(empx_nbr, empx_type, empx_desc, empx_amttype, empx_amt ) "
                            + " values ( " 
                            + "'" + empid.getText() + "'" + ","
                            + "'" + exctable.getValueAt(j, 0).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 1).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 2).toString().replace("'", "") + "'" + ","
                            + "'" + exctable.getValueAt(j, 3).toString().replace("'", "") + "'" 
                            + ")"
                            + ";");
                    }
                        
                        
                        initvars("");
                        bsmf.MainFrame.show("Updated Employee Record");
                    }

                } else {
                    bsmf.MainFrame.show("employee ID does not exist");
                }
            } // if proceed
            catch (SQLException s) {
                s.printStackTrace();
                bsmf.MainFrame.show("Unable to Edit Employee Master");
            }
            bsmf.MainFrame.con.close();
            reinitusermaintvariables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_bteditActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
          boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
              
                   int i = st.executeUpdate("delete from emp_mstr where emp_nbr = " + "'" + empid.getText() + "'" + ";");
                    if (i > 0) {
                    bsmf.MainFrame.show("deleted employee record " + empid.getText());
                    initvars("");
                    }
                } catch (SQLException s) {
                bsmf.MainFrame.show("Unable to Delete Employee Record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }//GEN-LAST:event_btdeleteActionPerformed

    private void btempbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btempbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "empmaint,emp_nbr");
    }//GEN-LAST:event_btempbrowseActionPerformed

    private void btemplnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btemplnameActionPerformed
        reinitpanels("BrowseUtil", true, "empmaint,emp_lname");
    }//GEN-LAST:event_btemplnameActionPerformed

    private void btempfnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btempfnameActionPerformed
         reinitpanels("BrowseUtil", true, "empmaint,emp_fname");
    }//GEN-LAST:event_btempfnameActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
        clearAll();
        enableAll();
        btedit.setEnabled(false);
        btdelete.setEnabled(false);
        btempfname.setEnabled(false);
        btemplname.setEnabled(false);
        btempbrowse.setEnabled(false);
        
        empid.setText(String.valueOf(OVData.getNextNbr("employee")));
        empid.setEditable(false);
        empid.setForeground(Color.blue);
        
    }//GEN-LAST:event_btnewActionPerformed

    private void empidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empidActionPerformed
          boolean gotIt = getEmployee(empid.getText());
        if (gotIt) {
          empid.setEditable(false);
          empid.setForeground(Color.blue);
        } else {
           empid.setForeground(Color.red); 
        }
    }//GEN-LAST:event_empidActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
         JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
       // if (index == 1 && ddpart != null && ddpart.getItemCount() > 0) {
       //     ddpart.setSelectedIndex(0);
      //  }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void btexcaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexcaddActionPerformed
        boolean proceed = true;
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbexcamt.getText());
        if (!m.find() || tbexcamt.getText() == null) {
            bsmf.MainFrame.show("Invalid amount format");
            proceed = false;
            tbexcamt.requestFocus();
            return;
        }

        if (tbexcdesc.getText().isEmpty()) {
            bsmf.MainFrame.show("Description cannot be blank");
            proceed = false;
            tbexcdesc.requestFocus();
            return;
        }

        if (proceed)
        excmodel.addRow(new Object[]{ ddexctype.getSelectedItem().toString(), tbexcdesc.getText(), ddexcamttype.getSelectedItem().toString(), tbexcamt.getText()});
      
    }//GEN-LAST:event_btexcaddActionPerformed

    private void btexcdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexcdeleteActionPerformed
        int[] rows = exctable.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) exctable.getModel()).removeRow(i);
        }
    }//GEN-LAST:event_btexcdeleteActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        
        if ( col == 0) {
            getdetail(tablereport.getValueAt(row, 2).toString(), tablereport.getValueAt(row, 3).toString() );
            summarypanel.setVisible(false);
            detailpanel.setVisible(true);
            chartpanel.setVisible(true);
            getDeductions(tablereport.getValueAt(row, 2).toString(), Double.valueOf(tablereport.getValueAt(row,6).toString()));
            getEarnings(tablereport.getValueAt(row, 2).toString(), tablereport.getValueAt(row, 3).toString() );
        }
        
        
    }//GEN-LAST:event_tablereportMouseClicked

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        if (col == 0) {
            
        }
    }//GEN-LAST:event_tabledetailMouseClicked

    private void btsummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btsummaryActionPerformed
            summarypanel.setVisible(true);
            detailpanel.setVisible(false);
            chartpanel.setVisible(false);
    }//GEN-LAST:event_btsummaryActionPerformed

    private void tbrateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbrateFocusLost
             String x = BlueSeerUtils.bsformat("", tbrate.getText(), "4");
        if (x.equals("error")) {
            tbrate.setText("");
            tbrate.setBackground(Color.yellow);
            bsmf.MainFrame.show("Non-Numeric character in textbox");
            tbrate.requestFocus();
        } else {
            tbrate.setText(x);
            tbrate.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbrateFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btempbrowse;
    private javax.swing.JButton btempfname;
    private javax.swing.JButton btemplname;
    private javax.swing.JButton btexcadd;
    private javax.swing.JButton btexcdelete;
    private javax.swing.JButton btnew;
    private javax.swing.JButton btsummary;
    private javax.swing.JCheckBox cbactive;
    private javax.swing.JCheckBox cbautoclock;
    private javax.swing.JPanel chartpanel;
    private javax.swing.JTextArea comments;
    private com.toedter.calendar.JDateChooser dcdob;
    private javax.swing.JComboBox ddcountry;
    private javax.swing.JComboBox dddept;
    private javax.swing.JComboBox<String> ddexcamttype;
    private javax.swing.JComboBox<String> ddexctype;
    private javax.swing.JComboBox ddgender;
    private javax.swing.JComboBox<String> ddpayfrequency;
    private javax.swing.JComboBox<String> ddpaytype;
    private javax.swing.JComboBox ddshift;
    private javax.swing.JComboBox ddstate;
    private javax.swing.JComboBox ddstatus;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JTextField empid;
    private javax.swing.JTable exctable;
    private javax.swing.JTextField firstname;
    private com.toedter.calendar.JDateChooser hiredate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelHistory;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelPay;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jtpDeductions;
    private javax.swing.JTextPane jtpEarnings;
    private javax.swing.JTextField lastname;
    private javax.swing.JTextField middlename;
    private javax.swing.JLabel percentlabel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTextArea taUMperms1;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbaccount;
    private javax.swing.JTextField tbcity;
    private javax.swing.JTextField tbclockin;
    private javax.swing.JTextField tbefladays;
    private javax.swing.JTextField tbemercontact;
    private javax.swing.JTextField tbemernumber;
    private javax.swing.JTextField tbexcamt;
    private javax.swing.JTextField tbexcdesc;
    private javax.swing.JTextField tbline1;
    private javax.swing.JTextField tbline2;
    private javax.swing.JTextField tbphone;
    private javax.swing.JTextField tbprofile;
    private javax.swing.JTextField tbrate;
    private javax.swing.JTextField tbroute;
    private javax.swing.JTextField tbssn;
    private javax.swing.JTextField tbsupervisor;
    private javax.swing.JTextField tbtitle;
    private javax.swing.JTextField tbvacdays;
    private javax.swing.JTextField tbvactaken;
    private javax.swing.JTextField tbzip;
    private com.toedter.calendar.JDateChooser termdate;
    // End of variables declaration//GEN-END:variables
}
